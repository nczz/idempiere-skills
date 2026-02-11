package ${package};

import org.adempiere.base.AnnotationBasedProcessFactory;
import org.adempiere.base.IProcessFactory;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = IProcessFactory.class, property = {"service.ranking:Integer=100"})
public class ${className} extends AnnotationBasedProcessFactory {

	public ${className}() {
	}

	@Override
	protected String[] getPackages() {
		return new String[] {"${processPackage}"};
	}

}
