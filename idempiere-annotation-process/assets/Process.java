package ${package};

import org.adempiere.base.annotation.Parameter;
import org.adempiere.base.annotation.Process;
import org.compiere.process.SvrProcess;

@Process
public class ${className} extends SvrProcess {

	//@Parameter
	//private int p_Parameter_ID;
	
	public ${className}() {
	}

	@Override
	protected void prepare() {
		// Process Parameters
		// for (ProcessInfoParameter para : getParameter()) {
		// 	String name = para.getParameterName();
		// 	if (para.getParameter() == null)
		// 		;
		// 	else
		// 		log.log(Level.SEVERE, "Unknown Parameter: " + name);
		// }
	}

	@Override
	protected String doIt() throws Exception {
		// Implement process logic
		return "";
	}

}
