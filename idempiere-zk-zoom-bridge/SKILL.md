---
name: zk-zoom-bridge
description: 從 iframe SPA 觸發 iDempiere ZK 視窗的 Zoom 功能
tags: [zk, iframe, zoom, postMessage]
---

# ZK Zoom Bridge — iframe SPA → iDempiere Window

## 概述
從嵌入在 ZK Form iframe 中的 SPA，透過 postMessage 觸發 iDempiere 的 Zoom 功能，在同一個 ZK desktop 開啟記錄視窗。

## ZoomCommand（iDempiere 內建）

iDempiere 有一個 Desktop 級的 `ZoomCommand` AuService（`org.adempiere.webui.component.ZoomCommand`），攔截所有 `onZoom` 事件。

### 正確的資料格式
```javascript
zAu.send(new zk.Event(widget, 'onZoom', {
  data: ['C_BPartner_ID', '117']  // [columnName, recordId as string]
}));
```

### ZoomCommand 處理流程
1. 從 `request.getData()` 取得 `Map`，讀 `data` key 得到 `JSONArray`
2. `data[0]` = columnName（如 `C_BPartner_ID`）
3. `data[1]` = recordId（字串，會被 parseInt）
4. 用 `MQuery.getZoomTableName(columnName)` 取得 tableName
5. 建立 `MQuery` 並呼叫 `AEnv.zoom(query)`

### 常見錯誤
- ❌ `zk.Event(w, 'onZoom', JSON.stringify(data))` — 字串不是 Map，ZoomCommand 報錯
- ❌ `zk.Event(w, 'onZoom', 'C_BPartner|117')` — 同上
- ❌ 自訂 `onZoom` 事件名 — 會被 ZoomCommand 攔截，格式不符就報錯
- ✅ `zk.Event(w, 'onZoom', {data: [columnName, value]})` — 正確格式

## 完整橋接模式

### Client-side（FormController 注入）
```javascript
window.addEventListener('message', function(e) {
  if (e.data && e.data.type === 'zoom') {
    var w = zk.Widget.$('#iframeUuid');
    zAu.send(new zk.Event(w, 'onZoom', {
      data: [e.data.tableName + '_ID', String(e.data.recordId)]
    }));
  }
});
```

### SPA 端
```typescript
export function zoomRecord(tableName: string, recordId: number): void {
  window.parent.postMessage({ type: 'zoom', tableName, recordId: String(recordId) }, '*');
}

// 使用
zoomRecord('C_BPartner', 117);  // → onZoom {data: ['C_BPartner_ID', '117']}
zoomRecord('S_Resource', 100);  // → onZoom {data: ['S_Resource_ID', '100']}
```

## iDempiere 永久連結格式

來自 `AEnv.getZoomUrlTableID()`：
```
{applicationUrl}?Action=Zoom&TableName={tableName}&Record_ID={id}
```

例：`https://host/webui/?Action=Zoom&TableName=C_BPartner&Record_ID=117`

## ZK Widget 注意事項

- `setVisible(false)` — ZK 不渲染 widget 到 DOM，`zk.Widget.$()` 找不到
- `setStyle("display:none")` — widget 在 DOM 但不可見，`zk.Widget.$()` 可找到
- `zk.Widget.$('#uuid')` — 用 component 的 `getUuid()` 查找，不是 `getId()`
