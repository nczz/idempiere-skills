package ${package};

import org.adempiere.base.annotation.EventTopicDelegate;
import org.adempiere.base.event.LoginEventData;
import org.adempiere.base.event.annotations.AfterLoadPref;
import org.adempiere.base.event.annotations.AfterLoginEventDelegate;
import org.osgi.service.event.Event;

@EventTopicDelegate
public class ${className} extends AfterLoginEventDelegate {

	public ${className}(Event event) {
		super(event);
	}
	
	@Override
	protected void onAfterLogin(LoginEventData data) {
		// logic for after login
	}

	@AfterLoadPref
	public void onAfterLoadPref() {
		// logic for after load preference
	}
}
