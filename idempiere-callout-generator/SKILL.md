---
name: idempiere-callout-generator
description: Generates iDempiere column callouts and callout factories based on annotation templates. Use when the user wants to create a new callout for a database column or a new callout factory in an iDempiere OSGi bundle.
---

# iDempiere Callout Generator

## Overview
This skill automates the creation of iDempiere column callouts and their associated factories using annotations. It uses predefined templates to ensure consistency and adherence to iDempiere development standards.

## Workflow
1. **Identify the Target**: Determine the table name and column name for the callout.
2. **Generate Callout**: Use `assets/CalloutTemplate.java` as a base to create a new `IColumnCallout` implementation.
   - Update the `@Callout` annotation with the correct `tableName` and `columnName`.
   - Implement the `start` method logic.
3. **Generate Factory**: If a factory doesn't exist or needs update, use `assets/FactoryTemplate.java` as a base.
   - Ensure the `getPackages` method includes the package of the new callout.
   - The factory is an OSGi `@Component` serving `IColumnCalloutFactory`.

## Templates
- **Callout Template**: `assets/CalloutTemplate.java`
- **Factory Template**: `assets/FactoryTemplate.java`

## Example Usage
If the user asks to "Create a callout for C_Order.DateOrdered to set DatePromised to today", you should:
1. Create `CalloutOrder.java` using the template.
2. Set `@Callout(tableName = I_C_Order.Table_Name, columnName = I_C_Order.COLUMNNAME_DateOrdered)`.
3. Set `mTab.setValue(I_C_Order.COLUMNNAME_DatePromised, today)` in the `start` method.
