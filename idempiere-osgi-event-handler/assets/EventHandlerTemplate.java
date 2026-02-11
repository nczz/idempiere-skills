package ${packageName};

import org.adempiere.base.event.AbstractEventHandler;
import org.adempiere.base.event.IEventManager;
import org.adempiere.base.event.IEventTopics;
import org.compiere.model.PO;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.event.Event;

@Component(immediate = true)
public class ${className} extends AbstractEventHandler {

	@Override
	protected void initialize() {
		registerTableEvent(${eventTopic}, ${modelClassName}.Table_Name);
	}

	@Override
	protected void doHandleEvent(Event event) {
		if (event.getTopic().equals(${eventTopic})) {
			 PO po = getPO(event);
			 // Implement business logic for ${modelClassName} here
		}
	}

	@Override
	@Reference(service = IEventManager.class, unbind = "unbindEventManager")
	public void bindEventManager(IEventManager eventManager) {
		super.bindEventManager(eventManager);
	}

	@Override
	public void unbindEventManager(IEventManager eventManager) {
		super.unbindEventManager(eventManager);
	}
}
