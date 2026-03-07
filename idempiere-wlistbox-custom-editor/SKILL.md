---
name: idempiere-wlistbox-custom-editor
description: Instructions for using a custom WEditor for a WListbox (WTableColumn) in iDempiere ZK web UI. 
Use when you need to customize the rendering or editing behavior of a specific column in a WListbox.
---

# iDempiere WListbox Custom Editor

This skill provides guidance on how to inject a custom `WEditor` into a `WListbox` column (`WTableColumn`). This is typically done in iDempiere ZK forms that use `WListbox` to display data.

## Workflow

1.  **Identify the WListbox**: Locate the `WListbox` instance. In many forms (like `WAllocation`), it is passed as an `IMiniTable`.
2.  **Access the Renderer**: Get the `WListItemRenderer` from the `WListbox` using `getItemRenderer()`.
3.  **Target the Column**: Use `renderer.getColumn(index)` to get the `WTableColumn` you want to customize.
4.  **Set EditorProvider**: Call `setEditorProvider(parameters -> ...)` on the `WTableColumn`.
5.  **Implement Custom Editor**: Return an instance of `WEditor` (or a subclass like `WNumberEditor`, `WStringEditor`, etc.) and override `getDisplayComponent()` or `createEditor()` as needed.

## Example

See [assets/CustomAllocationForm.java](assets/CustomAllocationForm.java) for a concrete example of customizing a column in the Allocation form to change text color based on the value.

## Key Classes
- `org.adempiere.webui.component.WListbox`
- `org.adempiere.webui.component.WListItemRenderer`
- `org.adempiere.webui.component.WTableColumn`
- `org.adempiere.webui.editor.WEditor`
