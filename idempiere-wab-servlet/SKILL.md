---
name: idempiere-wab-servlet
description: Creates a Web Application Bundle (WAB) with custom servlets in iDempiere. Use when you need to add HTTP endpoints (REST APIs, webhooks, token services) that run inside the iDempiere Jetty server.
---

# iDempiere WAB Servlet

## Overview

iDempiere uses Jetty OSGi Boot to deploy web applications. Each web-capable bundle is a **WAB (Web Application Bundle)** — an OSGi bundle with a `Web-ContextPath` MANIFEST header and a standard `WEB-INF/web.xml`.

This skill creates custom HTTP servlets deployed as a WAB, accessible at `http://<host>:8080/<context-path>/<url-pattern>`.

## Critical MANIFEST Headers

These headers are **mandatory** for Jetty to recognize and deploy the WAB:

| Header | Value | Why |
|--------|-------|-----|
| `Web-ContextPath` | e.g. `myapi` | Defines the URL prefix (`/myapi/*`) |
| `Jetty-Environment` | `ee8` | **Without this, Jetty ignores the bundle entirely** |
| `Eclipse-BundleShape` | `dir` | Deploy as exploded directory (avoids JAR `\r` corruption) |
| `Bundle-ActivationPolicy` | `lazy` | Standard for WABs — Jetty manages activation |

## Workflow

1. **Create the Servlet** — Extend `javax.servlet.http.HttpServlet`.
2. **Create `WEB-INF/web.xml`** — Map the servlet to a URL pattern.
3. **Configure MANIFEST.MF** — Include all critical headers above.
4. **Deploy as exploded bundle** — Directory format `plugins/symbolicName_version/`.
5. **Register in `bundles.info`** — Path must end with `/`.

## Templates

- `assets/MANIFEST.MF.template` — WAB MANIFEST with all required headers.
- `assets/web.xml.template` — Standard servlet mapping.
- `assets/MyServlet.java.template` — HttpServlet boilerplate.

## Deployment

### Exploded Bundle (Recommended)

Deploy as a directory under `plugins/`:

```
plugins/com.example.myplugin_1.0.0.qualifier/
├── META-INF/MANIFEST.MF
├── WEB-INF/web.xml
├── com/example/myplugin/MyServlet.class
└── ...
```

### bundles.info Entry

```
com.example.myplugin,1.0.0.qualifier,plugins/com.example.myplugin_1.0.0.qualifier/,4,false
```

- Path **must** end with `/` for directories.
- `autoStart=false` is fine — Jetty activates WABs automatically.

### After Deployment

Clear OSGi cache and restart:
```bash
rm -rf configuration/org.eclipse.osgi
systemctl restart idempiere
```

## Common Pitfalls

### ❌ Missing `Jetty-Environment: ee8`
**Symptom**: Bundle starts, but all requests return 404 (`SERVLET: default`).
**Cause**: Jetty OSGi Boot only deploys bundles with this header. Without it, the bundle is a regular OSGi bundle, not a web app.

### ❌ `HttpService.registerServlet()` doesn't work
**Symptom**: `registerServlet()` returns success, but requests return 404.
**Cause**: iDempiere's Jetty doesn't route through the Equinox HTTP Servlet Bridge. Always use the WAB pattern (`Web-ContextPath` + `web.xml`) instead.

### ❌ JAR `\r` corruption in MANIFEST
**Symptom**: Bundle fails to resolve. MANIFEST headers are garbled (missing letters).
**Cause**: Java's `jar` command writes MANIFEST with `\r\n` line endings. When the source MANIFEST also has `\r`, the result is `\r\r\n` which corrupts header continuation lines.
**Fix**: Deploy as exploded directory, or ensure MANIFEST source has Unix line endings (`\n` only).

### ❌ Bundle installed but not started
**Symptom**: No log output, no errors, bundle is invisible.
**Cause**: `simpleconfigurator` with `exclusiveInstallation=false` (default) only installs bundles on first framework start. Subsequent restarts reuse cached state.
**Fix**: Clear OSGi cache: `rm -rf configuration/org.eclipse.osgi`

### ❌ Menu item not visible after login
**Symptom**: AD_Menu, AD_TreeNodeMM, AD_Form_Access all correct in DB, but menu doesn't appear in WebUI.
**Cause**: iDempiere with `zh_TW` (or any non-`en_US` system language) requires **translation records** (`AD_Menu_Trl`, `AD_Form_Trl`). Without them, the menu item is invisible — no error, just silently hidden.
**Fix**: Insert translation records for all active system languages:
```sql
INSERT INTO AD_Menu_Trl (AD_Menu_ID, AD_Language, AD_Client_ID, AD_Org_ID, IsActive,
  Created, CreatedBy, Updated, UpdatedBy, Name, Description, IsTranslated, AD_Menu_Trl_UU)
SELECT <menu_id>, l.AD_Language, 0, 0, 'Y', NOW(), 100, NOW(), 100,
  '<name>', '<description>', 'Y', '<uuid_prefix>' || l.AD_Language
FROM AD_Language l
WHERE l.IsActive = 'Y' AND l.IsSystemLanguage = 'Y'
AND NOT EXISTS (SELECT 1 FROM AD_Menu_Trl t WHERE t.AD_Menu_ID = <menu_id> AND t.AD_Language = l.AD_Language);
```
Same pattern for `AD_Form_Trl`. Also need `AD_Form_Access` for role permission.

### ❌ Menu item at tree root not visible
**Symptom**: Tree node with `Parent_ID = 0` doesn't show up.
**Cause**: Root-level nodes in iDempiere's menu tree must be `IsSummary = 'Y'` (folders). Leaf items (`IsSummary = 'N'`) at root are not displayed.
**Fix**: Place the menu node under an existing summary folder (e.g. `Parent_ID = 263` for Partner Relations).

## Reference: REST API Plugin

The official REST API plugin (`com.trekglobal.idempiere.rest.api`) is the canonical example of a WAB in iDempiere. Key MANIFEST headers:

```
Web-ContextPath: api
Jetty-Environment: ee8
Eclipse-BundleShape: dir
Bundle-ActivationPolicy: lazy
```

## React/Vite SPA in WAB

A WAB can serve a React SPA built with Vite:

```
spa/                          ← React source (not deployed)
├── vite.config.ts            ← base: './', outDir: ../web/appointments
└── src/

com.example.plugin/
├── web/appointments/         ← Vite build output
│   ├── index.html
│   └── assets/
└── WEB-INF/web.xml           ← NoCacheFilter for /web/*
```

**Vite config**: `base: './'` for relative asset paths, `outDir` points to WAB's web directory.

**Development**: `npm run build` in `spa/`, then copy output. SPA updates don't need iDempiere restart (NoCacheFilter ensures fresh files).

## NoCacheFilter

Add to `WEB-INF/web.xml` to prevent browser caching of static files:

```xml
<filter>
    <filter-name>NoCacheFilter</filter-name>
    <filter-class>com.example.NoCacheFilter</filter-class>
</filter>
<filter-mapping>
    <filter-name>NoCacheFilter</filter-name>
    <url-pattern>/web/*</url-pattern>
</filter-mapping>
```

The filter sets `Cache-Control: no-cache, no-store, must-revalidate` on all responses.
