package org.idempiere.wlistbox.editor.example;

import java.math.BigDecimal;

import org.adempiere.webui.apps.form.WAllocation;
import org.adempiere.webui.component.Label;
import org.adempiere.webui.component.WListItemRenderer;
import org.adempiere.webui.component.WListbox;
import org.adempiere.webui.component.WTableColumn;
import org.adempiere.webui.editor.WEditor;
import org.adempiere.webui.editor.WNumberEditor;
import org.compiere.minigrid.IMiniTable;
import org.zkoss.zk.ui.Component;

/**
 * Example of how to use a custom WEditor for a WListbox column.
 */
public class CustomAllocationForm extends WAllocation {

	@Override
	public void setInvoiceColumnClass(IMiniTable invoiceTable, boolean isMultiCurrency) {
		super.setInvoiceColumnClass(invoiceTable, isMultiCurrency);
		
		// 1. Cast IMiniTable to WListbox
		WListbox invoiceListbox = (WListbox) invoiceTable;
		
		// 2. Get the WListItemRenderer
		WListItemRenderer renderer = (WListItemRenderer) invoiceListbox.getItemRenderer();
		
		// 3. Get the WTableColumn by index
		WTableColumn amtColumn = renderer.getColumn(4);
		
		// 4. Set the EditorProvider
		if (amtColumn.getEditorProvider() == null) {
			amtColumn.setEditorProvider(parameters -> newAmtColumnEditor(parameters));
		}
	}

	private WEditor newAmtColumnEditor(WTableColumn.EditorProviderParameters parameters) {
		// 5. Return a custom WEditor implementation
		return new WNumberEditor() {
			@Override
			public Component getDisplayComponent() {
				Label label = new Label();
				BigDecimal value = (BigDecimal) getValue();
				if (value != null) {
					// Custom logic for display component
					if (value.compareTo(new BigDecimal("100.00")) > 0) {
						label.setStyle("color:red;text-align:right;width:100%;display:inline-block;");
					} else {
						label.setStyle("color:green;text-align:right;width:100%;display:inline-block;");
					}
				}
				return label;
			}
		};
	}
}
