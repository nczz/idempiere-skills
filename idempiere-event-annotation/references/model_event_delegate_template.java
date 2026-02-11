package ${package};

import org.adempiere.base.annotation.EventTopicDelegate;
import org.adempiere.base.annotation.ModelEventTopic;
import org.adempiere.base.event.annotations.ModelEventDelegate;
import org.adempiere.base.event.annotations.po.AfterChange;
import org.adempiere.base.event.annotations.po.AfterDelete;
import org.adempiere.base.event.annotations.po.AfterNew;
import org.adempiere.base.event.annotations.po.BeforeChange;
import org.adempiere.base.event.annotations.po.BeforeDelete;
import org.adempiere.base.event.annotations.po.BeforeNew;
import org.adempiere.base.event.annotations.po.PostCreate;
import org.adempiere.base.event.annotations.po.PostDelete;
import org.adempiere.base.event.annotations.po.PostUpdate;
import ${modelClassImport};
import org.osgi.service.event.Event;

@EventTopicDelegate
@ModelEventTopic(modelClass = ${modelClassName}.class)
public class ${className} extends ModelEventDelegate<${modelClassName}> {

	public ${className}(${modelClassName} po, Event event) {
		super(po, event);
	}

	@BeforeChange
	public void onBeforeChange() {
	}
	
	@AfterChange
	public void onAfterChange() {
	}
	
	@BeforeNew
	public void onBeforeNew() {
	}
	
	@AfterNew
	public void onAfterNew() {
	}
	
	@BeforeDelete
	public void onBeforeDelete() {
	}
	
	@AfterDelete
	public void onAfterDelete() {
	}
	
	@PostCreate
	public void onPostCreate() {
	}
	
	@PostUpdate
	public void onPostUpdate() {
	}
	
	@PostDelete
	public void onPostDelete() {
	}
}
