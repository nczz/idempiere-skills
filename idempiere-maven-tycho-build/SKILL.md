---
name: idempiere-maven-tycho-build
description: Sets up Maven/Tycho build system for iDempiere plugins. Use when creating a new plugin project that needs standard build, p2 repository output, and CI/CD support.
---

# iDempiere Maven/Tycho Build

## Overview

iDempiere plugins use Eclipse Tycho (Maven plugin for OSGi) to build. The output is a p2 update site that can be deployed using iDempiere's standard `update-rest-extensions.sh` script.

## Project Structure

```
my-plugin/
├── pom.xml                              # Root: lists modules
├── com.example.plugin.parent/
│   └── pom.xml                          # Parent: Tycho config + target platform
├── com.example.plugin/
│   ├── pom.xml                          # Plugin: eclipse-plugin packaging
│   ├── META-INF/MANIFEST.MF
│   ├── build.properties
│   └── src/
└── com.example.plugin.p2/
    ├── pom.xml                          # p2: eclipse-repository packaging
    └── category.xml
```

## Key Concepts

### Target Platform

Tycho resolves OSGi dependencies from a **p2 repository**. For iDempiere plugins, this is the iDempiere core p2 repository built from source:

```
<idempiere.core.repository.url>file:///${basedir}/../../iDempiere/org.idempiere.p2/target/repository</idempiere.core.repository.url>
```

Override this property to point to your iDempiere build location:
```bash
mvn verify -Didempiere.core.repository.url=file:///path/to/iDempiere/org.idempiere.p2/target/repository
```

### Packaging Types

| Module | Packaging | Purpose |
|--------|-----------|--------|
| Parent | `pom` | Tycho plugin management, target platform config |
| Plugin | `eclipse-plugin` | Compiles Java against OSGi classpath from MANIFEST.MF |
| p2 | `eclipse-repository` | Produces deployable p2 update site |

### Build & Deploy

```bash
# Build (requires iDempiere p2 repository)
mvn verify

# Output: p2 repository at
# com.example.plugin.p2/target/repository/

# Deploy to iDempiere
cd /opt/idempiere
./update-rest-extensions.sh /path/to/com.example.plugin.p2/target/repository/
systemctl restart idempiere
```

## Templates

- `assets/root-pom.xml.template` — Root pom listing modules
- `assets/parent-pom.xml.template` — Parent pom with Tycho 4.0.8 configuration
- `assets/plugin-pom.xml.template` — Plugin pom (eclipse-plugin)
- `assets/p2-pom.xml.template` — p2 repository pom
- `assets/category.xml.template` — p2 category definition

## Prerequisites

1. **iDempiere source built**: `cd iDempiere && mvn verify` to produce the p2 repository
2. **Maven 3.9+** and **JDK 17+**
3. Tycho version must match iDempiere's (currently 4.0.8)

## Common Pitfalls

### ❌ `Cannot resolve dependencies`
**Cause**: `idempiere.core.repository.url` doesn't point to a valid p2 repository.
**Fix**: Build iDempiere first (`mvn verify` in iDempiere root), then point to `org.idempiere.p2/target/repository`.

### ❌ MANIFEST.MF `\r` corruption in JAR
**Cause**: Tycho writes MANIFEST with `\r\n` (standard). This is fine — the issue only occurs with manual `jar` command.
**Fix**: Use `mvn verify` instead of manual `jar` command. Tycho handles MANIFEST correctly.

### ❌ Version mismatch in Require-Bundle
**Cause**: `bundle-version="12.0.0"` in MANIFEST but actual bundle is `1.0.0`.
**Fix**: Check actual bundle versions in the target platform. Use `bundle-version="0.0.0"` for loose matching.

## Reference: REST API Plugin

The official REST API plugin (`bxservice/idempiere-rest`) uses the same structure and is the canonical reference for iDempiere Tycho builds.
