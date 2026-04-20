---
name: idempiere-plugin-auto-migration
description: Automatically runs SQL migration on first plugin install using the Incremental2PackActivator lifecycle. Use when your plugin needs to create AD records (forms, menus, columns, references) without manual SQL execution.
---

# iDempiere Plugin Auto-Migration

## Overview

iDempiere's `Incremental2PackActivator` provides lifecycle hooks that run after the framework is fully started. By overriding `afterPackIn()`, a plugin can automatically execute SQL migration on first install, with version tracking to prevent re-execution.

## How It Works

```
Bundle starts
  → Incremental2PackActivator.start(context)
  → Waits for iDempiere framework to fully start (frameworkStarted)
  → Runs 2Pack ZIPs from META-INF/ (if any)
  → Calls afterPackIn()
    → Check AD_Package_Imp for installed version
    → If not installed: read SQL from bundle, execute, record version
    → Register @Form controllers via IMappedFormFactory
```

## Key Points

- **`afterPackIn()`** runs AFTER iDempiere is fully started — DB connection is available, AD cache is ready
- **`AD_Package_Imp`** table tracks installed versions — same mechanism as 2Pack
- **`getContext().getBundle().getEntry()`** reads files from the bundle (works for both JAR and exploded bundles)
- **SQL must be idempotent** — use `WHERE NOT EXISTS` with UUID checks

## Template

See `assets/AutoMigrationActivator.java.template`

## SQL Migration Best Practices

### Idempotent INSERT Pattern
```sql
INSERT INTO AD_Form (AD_Form_ID, ..., AD_Form_UU)
SELECT 1000100, ..., 'my-plugin-form-001'
WHERE NOT EXISTS (SELECT 1 FROM AD_Form WHERE AD_Form_UU = 'my-plugin-form-001');
```

### Required AD Records for a Form Plugin

| Order | Table | Purpose | Trap |
|-------|-------|---------|------|
| 1 | AD_Element | Column metadata | Must exist before AD_Column |
| 2 | AD_Reference | List type definition | — |
| 3 | AD_Ref_List | List values + colors | — |
| 4 | AD_Column | Table column definition | Needs AD_Element_ID |
| 5 | **Java syncColumns()** | Actual DB column | **Use `MColumn.getSQLAdd()` — never `ALTER TABLE` in SQL** |
| 6 | AD_Form | Form registration | Classname = IFormController class |
| 7 | AD_Menu | Menu item | Action='X' for Form |
| 8 | AD_TreeNodeMM | Menu tree position | **Parent must be a summary folder, not root** |
| 9 | AD_Form_Access | Role permission | Per-role, per-client |
| 10 | AD_Menu_Trl | Menu translation | **Required for non-en_US — silent failure if missing** |
| 11 | AD_Form_Trl | Form translation | **Required for non-en_US — silent failure if missing** |

### Translation Records (Critical)

```sql
INSERT INTO AD_Menu_Trl (AD_Menu_ID, AD_Language, ..., Name, IsTranslated, AD_Menu_Trl_UU)
SELECT <id>, l.AD_Language, ..., '<name>', 'Y', '<uuid>' || l.AD_Language
FROM AD_Language l
WHERE l.IsActive = 'Y' AND l.IsSystemLanguage = 'Y'
AND NOT EXISTS (SELECT 1 FROM AD_Menu_Trl t WHERE t.AD_Menu_ID = <id> AND t.AD_Language = l.AD_Language);
```

Without these, menu items are **silently invisible** in non-English locales.

## Version Upgrade Pattern

For future versions, add new SQL files and check version:

```java
if (!isMigrationApplied("12.0.0")) runMigration("12.0.0", "migration/001_initial.sql");
if (!isMigrationApplied("12.0.1")) runMigration("12.0.1", "migration/002_add_feature.sql");
```

Each version is tracked independently in `AD_Package_Imp`.

## DDL: Creating Actual DB Columns

**Never use `ALTER TABLE` in migration SQL.** iDempiere's `DB.executeUpdateEx()` doesn't handle DDL, and pooled connections may rollback DDL on return.

**Correct approach:** Insert `AD_Element` + `AD_Column` via DML, then use Java to sync:

```java
private void syncColumns() {
    String[][] cols = {{"S_Resource", "X_Color"}, {"S_ResourceAssignment", "X_AppointmentStatus"}};
    for (String[] c : cols) {
        MTable table = MTable.get(Env.getCtx(), c[0]);
        MColumn col = MColumn.get(Env.getCtx(), c[0], c[1]);
        if (col == null || table == null) continue;
        // Skip if DB column already exists
        if (DB.getSQLValue(null,
            "SELECT COUNT(*) FROM information_schema.columns WHERE table_name=? AND column_name=?",
            c[0].toLowerCase(), c[1].toLowerCase()) > 0) continue;
        // Create DB column using iDempiere's method
        String sql = col.getSQLAdd(table);
        if (sql != null && !sql.isEmpty()) {
            DB.executeUpdate(sql, false, null);  // false = don't throw
        }
    }
}
```

Call `syncColumns()` in `afterPackIn()` after `runMigration()`.
