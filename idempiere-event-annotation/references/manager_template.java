package ${package};

import org.adempiere.base.AnnotationBasedEventManager;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true)
public class ${className} extends AnnotationBasedEventManager {

	public ${className}() {
	}

	@Override
	public String[] getPackages() {
		return new String[] {"${delegatePackage}"};
	}

}
