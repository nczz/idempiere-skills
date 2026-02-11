package ${package};

import org.adempiere.base.annotation.EventTopicDelegate;
import org.adempiere.base.annotation.ModelEventTopic;
import org.adempiere.base.event.FactsEventData;
import org.adempiere.base.event.annotations.doc.FactsValidateDelegate;
import ${modelClassImport};
import org.osgi.service.event.Event;

@EventTopicDelegate
@ModelEventTopic(modelClass = ${modelClassName}.class)
public class ${className} extends FactsValidateDelegate<${modelClassName}> {

	public ${className}(${modelClassName} po, Event event) {
		super(po, event);
	}

	@Override
	protected void onFactsValidate(FactsEventData data) {
	}

}
