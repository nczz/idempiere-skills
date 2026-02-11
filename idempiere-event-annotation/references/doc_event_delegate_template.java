package ${package};

import org.adempiere.base.annotation.EventTopicDelegate;
import org.adempiere.base.annotation.ModelEventTopic;
import org.adempiere.base.event.EventManager;
import org.adempiere.base.event.annotations.ModelEventDelegate;
import org.adempiere.base.event.annotations.doc.AfterClose;
import org.adempiere.base.event.annotations.doc.AfterComplete;
import org.adempiere.base.event.annotations.doc.AfterPrepare;
import org.adempiere.base.event.annotations.doc.AfterReactivate;
import org.adempiere.base.event.annotations.doc.AfterVoid;
import org.adempiere.base.event.annotations.doc.BeforeClose;
import org.adempiere.base.event.annotations.doc.BeforeComplete;
import org.adempiere.base.event.annotations.doc.BeforePrepare;
import org.adempiere.base.event.annotations.doc.BeforeReactivate;
import org.adempiere.base.event.annotations.doc.BeforeVoid;
import org.adempiere.base.event.annotations.doc.DocAction;
import org.adempiere.base.event.annotations.doc.AfterReverseAccrual;
import org.adempiere.base.event.annotations.doc.AfterReverseCorrect;
import org.adempiere.base.event.annotations.doc.BeforeReverseAccrual;
import org.adempiere.base.event.annotations.doc.BeforeReverseCorrect;
import ${modelClassImport};
import org.compiere.process.DocActionEventData;
import org.osgi.service.event.Event;

@EventTopicDelegate
@ModelEventTopic(modelClass = ${modelClassName}.class)
public class ${className} extends ModelEventDelegate<${modelClassName}> {

	public ${className}(${modelClassName} po, Event event) {
		super(po, event);
	}

	@DocAction
	public void onDocAction() {
		DocActionEventData data = (DocActionEventData) event.getProperty(EventManager.EVENT_DATA);
	}
	
	@BeforePrepare
	public void onBeforePrepare() {
	}
	
	@AfterPrepare
	public void onAfterPrepare() {
	}
	
	@BeforeComplete
	public void onBeforeComplete() {
	}
	
	@AfterComplete
	public void onAfterComplete() {
	}
	
	@BeforeClose
	public void onBeforeClose() {
	}
	
	@AfterClose
	public void onAfterClose() {
	}
	
	@BeforeReactivate
	public void onBeforeReactivate() {
	}
	
	@AfterReactivate
	public void onAfterReactivate() {
	}
	
	@BeforeVoid
	public void onBeforeVoid() {
	}
	
	@AfterVoid
	public void onAfterVoid() {
	}

	@BeforeReverseAccrual
	public void onBeforeReverseAccrual() {
	}
	
	@AfterReverseAccrual
	public void onAfterReverseAccrual() {
	}
	
	@BeforeReverseCorrect
	public void onBeforeReverseCorrect() {
	}
	
	@AfterReverseCorrect
	public void onAfterReverseCorrect() {
	}
}
