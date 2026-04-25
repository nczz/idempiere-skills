---
name: idempiere-fragment-development
description: OSGi Fragment 開發模式與 best practice。Use when you need to extend iDempiere's ZK web UI without modifying the core — adding renderers, widgets, ZK listeners, or theme customizations via OSGi fragments.
---

# iDempiere Fragment Development

## Overview

iDempiere 使用 OSGi Fragment 來擴充 ZK Web UI（`org.adempiere.ui.zk`）。Fragment 的 classpath 會合併到 host bundle，共享 classloader，因此 fragment 中的 Java class 可以存取 host bundle 的所有 class。

Core 中有 8 個 fragment，分為三種模式。開發 plugin 時應選擇對應的模式。

## 三大 Fragment 模式

### 模式 1：Theme Fragment（純 UI 資源）

**範例**：`org.idempiere.zk.iceblue_c.theme`, `org.idempiere.zk.breeze.theme`

**特徵**：
- 無 Java source
- 提供 CSS、images、ZUL 模板
- 必須 `Eclipse-BundleShape: dir`（Jetty 需要 exploded directory 才能 serve 靜態資源）
- `lang-addon.xml` 註冊假 JS module 做 cache-busting

**目錄結構**：
```
my.theme/
├── META-INF/MANIFEST.MF
├── build.properties
└── src/
    ├── metainfo/zk/
    │   └── lang-addon.xml
    └── web/theme/<theme_name>/
        ├── css/
        ├── images/
        ├── zul/desktop/
        │   ├── desktop.zul
        │   └── header.zul
        └── preference.zul
```

**MANIFEST.MF 必要 header**：
```
Fragment-Host: org.adempiere.ui.zk;bundle-version="11.0.0"
Eclipse-BundleShape: dir
```

### 模式 2：Functional Fragment（Java 功能擴充）

**範例**：`org.idempiere.zk.pivot`, `org.idempiere.zk.datatable`, `org.idempiere.keikai`

**特徵**：
- 有 Java source
- 用 OSGI-INF + `@Component` 註冊 OSGi 服務
- 可提供 web 資源（JS/CSS）
- `lang-addon.xml` 註冊 ZK JS module
- `config.xml` 可註冊 ZK listener

**目錄結構**：
```
my.fragment/
├── META-INF/MANIFEST.MF
├── OSGI-INF/
│   └── my.service.Component.xml
├── build.properties
└── src/
    ├── metainfo/zk/
    │   ├── lang-addon.xml          ← ZK JS/CSS 模組（可選）
    │   └── config.xml              ← ZK listener 註冊（可選）
    ├── org/my/package/
    │   └── MyService.java
    └── web/js/my-widget/           ← 靜態資源（可選）
        └── my-widget.js
```

**MANIFEST.MF 必要 header**：
```
Fragment-Host: org.adempiere.ui.zk;bundle-version="11.0.0"
Service-Component: OSGI-INF/my.service.Component.xml
Bundle-ActivationPolicy: lazy
```

### 模式 3：Library Fragment（JAR 注入）

**範例**：`org.adempiere.report.jasper.library`, `javax.websocket-api.fragment`

**特徵**：
- 無 Java source
- 用 `Bundle-ClassPath` 注入外部 JAR 到 host bundle 的 classpath
- 可注入 Spring config、OSGi capability 等

## 關鍵規則

### Fragment 不能有 Bundle-Activator

OSGi 規範禁止 fragment 有自己的 activator。用以下替代方案：

| 需求 | 替代方案 |
|------|---------|
| 初始化邏輯 | `metainfo/zk/config.xml` 註冊 `WebAppInit` listener |
| 服務註冊 | `OSGI-INF` + `@Component` (Declarative Services) |
| 事件監聽 | `WebAppInit.init()` 中動態註冊 ZK listener |

### ZK 擴充檔案（放在 `src/metainfo/zk/`）

| 檔案 | 用途 | 範例 |
|------|------|------|
| `lang-addon.xml` | 註冊 ZK component、JS module、CSS | pivot, datatable, billboard |
| `config.xml` | 註冊 ZK listener（WebAppInit, UiLifeCycle 等） | ZK 官方機制，core 未使用但 ZK 支援 |

### config.xml 範本（註冊 ZK Listener）

> **原始碼驗證**：ZK `ConfigParser.parseConfigXml()` 使用 `XMLResourcesLocator.getDependentXMLResources("metainfo/zk/config.xml", ...)`，與 `lang-addon.xml` 使用**完全相同的掃描機制**。既然 iDempiere 的 6 個 fragment 的 `lang-addon.xml` 都能被掃描到，`config.xml` 也一定能。

```xml
<config>
    <config-name>my-extension</config-name>
    <depends>zk</depends>
    <listener>
        <listener-class>org.my.package.MyWebAppInit</listener-class>
    </listener>
</config>
```

對應的 Java class：
```java
public class MyWebAppInit implements WebAppInit {
    public void init(WebApp wapp) throws Exception {
        // 可透過 wapp.getConfiguration() 動態註冊更多 listener
        wapp.getConfiguration().addListener(MyUiLifeCycleListener.class);
    }
}
```

### lang-addon.xml 範本

**簡單版（JS module）**：
```xml
<language-addon>
    <addon-name>my-addon</addon-name>
    <language-name>xul/html</language-name>
    <javascript-module name="my.module" version="1.0.0"/>
</language-addon>
```

**完整版（ZK component）**：
```xml
<language-addon>
    <addon-name>my-widget</addon-name>
    <depends>zul</depends>
    <language-name>xul/html</language-name>
    <component>
        <component-name>mywidget</component-name>
        <component-class>org.my.package.MyWidget</component-class>
        <mold>
            <mold-name>default</mold-name>
            <widget-class>my.widget.MyWidget</widget-class>
            <mold-uri>mold/mywidget.js</mold-uri>
        </mold>
    </component>
    <stylesheet href="~./js/my-widget/my-widget.css" type="text/css"/>
    <javascript-module name="my.widget" version="1.0.0"/>
</language-addon>
```

### OSGI-INF 服務註冊範本

```xml
<scr:component xmlns:scr="http://www.osgi.org/xmlns/scr/v1.1.0"
    immediate="true" name="org.my.package.MyService">
   <service>
      <provide interface="org.idempiere.some.IServiceInterface"/>
   </service>
   <implementation class="org.my.package.MyService"/>
</scr:component>
```

### build.properties 範本

```properties
source.. = src/
output.. = target/classes/
bin.includes = META-INF/,\
               .,\
               OSGI-INF/
```

## Web 資源路徑對應

Fragment 中 `src/web/` 下的資源會自動 merge 到 host bundle 的 classpath：

| 檔案路徑 | ZK URL | 用途 |
|---------|--------|------|
| `src/web/js/my-widget/my.js` | `~./js/my-widget/my.js` | JS/CSS 靜態資源 |
| `src/web/theme/<name>/zul/...` | `~./theme/<name>/zul/...` | Theme ZUL 模板 |

在 Java 中讀取自己的 web 資源：
```java
URL url = getClass().getResource("/web/js/my-widget/my.js");
```

## ⚠️ Fragment 的 Import-Package 會破壞 host 的 package wiring（致命坑）

**Fragment 的 MANIFEST.MF 不要寫 Import-Package。**

Host bundle（`org.adempiere.ui.zk`）用 `Require-Bundle` 引入依賴。Fragment 的 `Import-Package` 會被合併到 host 的 imports 中，改變 host 的 package wiring，導致其他 bundle 的 class resolution 失敗。

**實測結果**：加了 `Import-Package: org.compiere.model, org.zkoss.zul, ...` 後，iDempiere 的 Dashboard、Language 視窗等功能全部壞掉，報 `ClassNotFoundException: org.zkoss.zul.impl.CustomGridDataLoader cannot be found by zcommon`。移除所有 Import-Package 後恢復正常。

**正確做法**：Fragment 共享 host 的 classloader。Host 能存取的 class，fragment 都能存取。不需要任何 Import-Package 或 Require-Bundle。

```
# ❌ 錯誤 — 會破壞 host 的 wiring
Import-Package: org.compiere.model,
 org.compiere.util,
 org.zkoss.zk.ui,
 org.zkoss.zul

# ✅ 正確 — fragment 只需要 Fragment-Host
Fragment-Host: org.adempiere.ui.zk;bundle-version="11.0.0"
```

## ⚠️ ZUL 覆蓋的可靠性問題

**當多個 fragment 提供同一 classpath 路徑的資源時，`Class.getResource()` 回傳哪一個取決於 Equinox classloader 的 fragment 附加順序，不保證可控。**

- ❌ 不要依賴「我的 fragment 的 header.zul 會覆蓋 theme 的 header.zul」
- ✅ 如果需要修改 ZUL 行為，改用 runtime patching（見下方模式）

## Runtime Patching 模式（不改 ZUL 的元件替換）

當你需要替換或修改 ZK 元件的行為，但無法可靠地覆蓋 ZUL 檔案時，使用以下模式：

### 啟動鏈
```
Fragment 提供 metainfo/zk/config.xml
  → ZK 啟動時掃描 → 呼叫 WebAppInit.init(WebApp)
    → wapp.getConfiguration().addListener(MyPatcher.class)
      → MyPatcher 實作 UiLifeCycle
        → afterComponentAttached() 偵測目標元件
          → Events.echoEvent() 延遲到 DOM 穩定後
            → 在 echoEvent handler 中執行替換
```

### ⚠️ 關鍵時序陷阱

`afterComponentAttached` 在 `insertBefore`/`appendChild` 的**那一刻**就觸發，不是在呼叫方的 method 執行完之後。

```java
// 假設 HeaderPanel.createSearchPanel() 的程式碼：
parent.insertBefore(globalSearch, stub);  // ← afterComponentAttached 在這裡觸發
stub.detach();                             // ← 還沒跑
globalSearch.setId("menuLookup");          // ← 還沒跑
```

**如果在 afterComponentAttached 中直接修改 DOM，會破壞呼叫方的後續邏輯。**

### 正確做法：echoEvent 延遲

```java
public void afterComponentAttached(Component comp, Page page) {
    if (!(comp instanceof TargetComponent)) return;
    
    // 不立即修改，排程到當前 execution 結束後
    Events.echoEvent("onMyPatch", comp, null);
    comp.addEventListener("onMyPatch", evt -> {
        // 此時呼叫方的 method 已完全執行完畢
        // comp.getId() 已設定，DOM 穩定
        if (comp.getAttribute("my.patched") != null) return;
        comp.setAttribute("my.patched", true);
        doPatch(comp);
    });
}
```

`Events.echoEvent` 的機制是 server → client → server，確保在下一次 request 中才處理。iDempiere 中有 89 處使用此模式。

## Core Fragment 服務介面參考

| 介面 | Package | 用途 |
|------|---------|------|
| `IReportRenderer` | `org.idempiere.print.renderer` | 報表渲染（pivot, datatable） |
| `IReportViewerRenderer` | `org.idempiere.ui.zk.report` | 報表檢視器渲染 |
| `IChartRendererService` | `org.adempiere.webui.apps.graph` | 圖表渲染（billboard） |
| `IMediaViewProvider` | `org.idempiere.ui.zk.media` | 媒體檢視（keikai） |

## 開發工作流（從研究到實作的方法論）

在開始寫任何程式碼之前，按以下順序執行。跳過任何一步都可能導致方案不可行或上線後才發現 showstopper。

### Step 1：讀原始碼確認擴充點

❌ 不要只看 JavaDoc 或 wiki
✅ 讀目標 class 的實際原始碼，標記每個 field 和 method 的可見性

```
你想覆蓋的 method 是 protected 還是 private？
  → protected：可以 subclass override
  → private：不能 override，需要複製或 runtime patch

你需要存取的 field 是 protected 還是 private？
  → protected：subclass 可直接存取
  → private：需要 reflection 或完全不同的方案
```

**實戰教訓**：HeaderPanel.createSearchPanel() 是 protected（可 override），但它用到的 globalSearch 和 menuTreePanel 都是 private（不可存取）。只看 method 簽名會以為能 subclass，實際上不行。

### Step 2：驗證 ZUL/資源覆蓋的可靠性

❌ 不要假設「我的 fragment 的 ZUL 會覆蓋 theme 的 ZUL」
✅ 確認 Equinox classloader 的 fragment 資源解析順序

多個 fragment 提供同路徑資源時，`Class.getResource()` 回傳哪一個取決於 fragment 附加順序，**不保證可控**。如果你的方案依賴 ZUL 覆蓋，需要有 fallback。

### Step 3：從 ZK 原始碼驗證機制（不要只看文件）

❌ 不要只看 ZK 文件說「支援 config.xml」就相信
✅ 去 GitHub 讀 ZK 的原始碼，確認掃描機制

```
關鍵問題：ZK 用什麼機制掃描 classpath？
  → XMLResourcesLocator.getDependentXMLResources()
  → 和 lang-addon.xml 用同一個掃描器嗎？
  → 是 → lang-addon.xml 能在 OSGi fragment 中被掃描到，config.xml 也能
  → 不是 → 需要額外驗證
```

**實戰教訓**：iDempiere 從未使用過 config.xml，但讀 ZK ConfigParser.java 原始碼後確認它和 lang-addon.xml 用同一個 XMLResourcesLocator，風險從「未知」降為「低」。

### Step 4：識別時序陷阱

❌ 不要假設「事件在 method 執行完之後才觸發」
✅ 逐行追蹤目標 method 的執行順序，標記每個副作用的觸發時機

```
afterComponentAttached 在什麼時候觸發？
  → insertBefore() 的那一刻，不是 method 結束後
  → 此時 setId() 還沒跑 → getId() 回傳 null
  → 如果在這裡修改 DOM → 破壞呼叫方的後續邏輯
```

**實戰教訓**：如果沒有發現這個時序問題，Spike 階段就會撞牆。用 echoEvent 延遲是標準解法（iDempiere 中 89 處使用）。

### Step 5：複製參考原始碼 + 建立修改計劃

❌ 不要直接開始改 code
✅ 先把要複製/修改的原始碼存到專案的 reference/ 目錄

```
reference/
├── MenuSearchController.java  (813 行，要複製修改的)
├── GlobalSearch.java           (278 行，要 reflection 存取的)
├── MenuItem.java               (124 行，要使用的 VO)
└── HeaderPanel.java            (233 行，要 reflection 存取的)
```

然後建立 MODIFICATION-PLAN.md：
- 哪些行保留不動
- 哪些行需要修改（標記原始內容和修改內容）
- 哪些是新增的（附程式碼草稿）
- 驗證 checklist

### Step 6：Spike 驗證核心假設

在寫正式程式碼之前，用最小的 fragment 驗證最高風險的假設：
1. config.xml 能被 ZK 掃描到嗎？（部署 → 看 log）
2. UiLifeCycle listener 能被觸發嗎？（log component class name）
3. echoEvent 在 Desktop 建立期間可靠嗎？（log 觸發順序）
4. GlobalSearch 替換後功能正常嗎？（手動測試）

**每個假設獨立驗證，失敗一個就停下來重新設計，不要繼續往下做。**

## Checklist

建立 fragment 前逐項確認：

- [ ] `Fragment-Host: org.adempiere.ui.zk` 已設定
- [ ] 無 `Bundle-Activator`（fragment 禁止）
- [ ] 有 web 資源 → 加 `Eclipse-BundleShape: dir`
- [ ] 有 OSGi 服務 → 加 `Service-Component` + `OSGI-INF/` XML
- [ ] 有 ZK listener → 加 `metainfo/zk/config.xml`
- [ ] 有 ZK JS/CSS → 加 `metainfo/zk/lang-addon.xml`
- [ ] `build.properties` 的 `bin.includes` 包含所有必要目錄
- [ ] 不依賴 ZUL 覆蓋來改變行為（改用 runtime patching）

## Build 踩坑速查

| 症狀 | 原因 | 解法 |
|------|------|------|
| `Unresolved: Import-Package: org.eclipse.core.runtime` | Require-Bundle 拉入傳遞依賴 | 改用 Import-Package；加 extraRequirements |
| `Invalid URI file:${project_loc:...}` | Eclipse IDE 變數在 CLI 中不解析 | 不用 iDempiere parent pom，建 standalone parent |
| `Could not resolve target platform` | target platform artifact 未安裝 | 先 `mvn install -N` 或不用 iDempiere parent |
| Eclipse p2 URL 404 | 舊版移到 archive.eclipse.org | 改用 `https://archive.eclipse.org/eclipse/updates/4.29` |
| `must implement afterShadowAttached` | ZK 10 的 UiLifeCycle 多了 2 個 method | 加空實作 `afterShadowAttached` + `afterShadowDetached` |
| Docker build 每次重新下載依賴 | `--rm` 刪除 Maven cache | 掛載 `-v "$HOME/.m2":/root/.m2` |
| 獨立 build 的 fragment 用 Require-Bundle 失敗 | 傳遞依賴無法 resolve | 改用 Import-Package（推薦獨立 build） |
| **Fragment 加 Import-Package 後 iDempiere 全面異常** | **Fragment 的 Import-Package 合併到 host，改變 package wiring** | **Fragment 不要寫 Import-Package（致命坑）** |
| **清除 OSGi cache 後 iDempiere 異常** | **重建的 state 和原始 state wiring 不同** | **永遠不要 `rm -rf configuration/org.eclipse.osgi`** |
| **v14 API 在 v12 報 NoSuchMethodError** | **MenuItem(String)、Icon.getIconSclass() 是 v14 才有** | **用 reflection 做 runtime 偵測** |
