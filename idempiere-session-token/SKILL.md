---
name: idempiere-session-token
description: Generates JWT tokens from an active AD_Session_ID for iDempiere REST API access. Use when a ZK Form (or any server-side component) needs to pass a REST token to an embedded SPA/iframe without requiring a service account.
---

# iDempiere Session-based JWT Token

## Problem

A ZK Form embeds an SPA via iframe. The SPA needs a JWT token to call the iDempiere REST API. But the ZK session doesn't have the user's password, so you can't call `POST /api/v1/auth/tokens` directly.

**Bad solution**: Configure a service account (username/password in AD_SysConfig). Requires manual setup per tenant, and the token doesn't represent the actual logged-in user.

**Good solution**: Exchange the current `AD_Session_ID` for a JWT token via a custom servlet. No credentials needed — the session is already authenticated.

## How It Works

```
ZK Session (authenticated user)
  → Env.getCtx() → #AD_Session_ID
  → POST http://localhost:8080/<context>/token {"sessionId": <id>}
  → Servlet validates session in DB (IsActive='Y', Processed='N')
  → Signs JWT with same HMAC-SHA512 secret as REST API
  → Returns token usable with standard REST API endpoints
```

## JWT Signing

The token must be signed with the **same secret** the REST API uses, read from:

```java
MSysConfig.getValue("REST_TOKEN_SECRET")
```

Uses standard Java crypto (`javax.crypto.Mac` + `HmacSHA512`) — no dependency on `com.auth0.jwt` (which is bundled inside the REST API plugin and not exported).

### Required JWT Claims

| Claim | Source | Required |
|-------|--------|----------|
| `sub` | AD_User.Name | ✅ |
| `AD_Client_ID` | AD_Session | ✅ |
| `AD_User_ID` | AD_Session.CreatedBy | ✅ |
| `AD_Role_ID` | AD_Session | ✅ |
| `AD_Org_ID` | AD_Session | ✅ |
| `M_Warehouse_ID` | AD_OrgInfo | if > 0 |
| `AD_Language` | hardcode or from user pref | ✅ |
| `AD_Session_ID` | input | ✅ |
| `iss` | `"idempiere.org"` | ✅ |
| `exp` | now + 3600s | ✅ |

### JWT Header

```json
{"alg":"HS512","typ":"JWT","kid":"idempiere"}
```

## Deployment

This servlet is deployed as a WAB. See `idempiere-wab-servlet` skill for the full WAB deployment pattern.

## Templates

- `assets/TokenServlet.java.template` — Complete servlet with session validation and JWT signing.
- `assets/FormController.java.template` — ZK FormController that obtains token and sets up postMessage refresh bridge.

## ZK Form Integration

The FormController:
1. Reads `#AD_Session_ID` from `Env.getCtx()`
2. Calls `POST http://localhost:8080/<context>/token` with the session ID
3. Passes the token to the iframe SPA via URL fragment (`#token=...`)
4. Sets up a postMessage bridge for token refresh

```java
int sessionId = Env.getContextAsInt(Env.getCtx(), "#AD_Session_ID");
String json = "{\"sessionId\":" + sessionId + "}";
String response = httpPost("http://localhost:8080/appointment/token", json);
String token = extractJsonValue(response, "token");
form.loadSpa(token); // sets iframe src with #token=...
```

## Security Notes

- The servlet only accepts `AD_Session_ID` — it validates the session is active and not processed (logged out).
- The token is signed with the same secret as the REST API, so it's indistinguishable from a "real" token.
- The servlet should only be accessible from localhost (the ZK FormController calls it server-side). Consider adding IP filtering if the WAB context is exposed externally.
