# iDempiere Annotation-based ZK Form

This skill guides the creation of custom forms in iDempiere using the ZK web UI framework, leveraging annotations and the `IMappedFormFactory` for automatic registration.

## Workflow

1.  **Activator Setup**: Configure the bundle activator to scan for annotated forms.
2.  **Form Controller**: Create a controller class implementing `IFormController` and annotated with `@Form`.
3.  **Custom Form**: Create a UI class extending `CustomForm` that loads and wires ZK components from `.zul` files.
4.  **ZUL Layout**: Define the UI structure in `.zul` files.

## Templates

### 1. Activator Setup (`MyActivator.java`)

The activator must obtain a reference to `IMappedFormFactory` and call `scan` to register forms within the bundle.

```java
package ${package};

import org.adempiere.plugin.utils.Incremental2PackActivator;
import org.adempiere.webui.factory.IMappedFormFactory;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component(immediate = true)
public class MyActivator extends Incremental2PackActivator {

	@Reference(service = IMappedFormFactory.class, cardinality = ReferenceCardinality.MANDATORY)
	private IMappedFormFactory mappedFormFactory;

	@Activate
	public void activate(BundleContext context) {
		// Scan the package for @Form annotated classes
		mappedFormFactory.scan(context, "${package}");
	}
}
```

### 2. Form Controller (`YourFormController.java`)

The controller handles business logic and event listeners. It must be annotated with `@Form`.

```java
package ${package};

import org.adempiere.webui.panel.ADForm;
import org.adempiere.webui.panel.IFormController;
import org.idempiere.ui.zk.annotation.Form;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Listen;

@Form
public class ${yourFormControllerClassName} implements IFormController {

	private YourForm ui;

	public YourFormController() {
		ui = new YourForm();
		
		// Wire event listeners defined in this controller to the UI components
		Selectors.wireEventListeners(ui, this);
		// If using nested components from ZUL, wire them as well
		// Selectors.wireEventListeners(ui.someNestedComponent, this);
		
		init();
	}

	private void init() {
		// Initialize business logic and UI state
	}

	@Override
	public ADForm getForm() {
		return ui;
	}

	@Listen("onClick = #myButton")
	public void onMyButtonClick() {
		// Handle button click
	}
}
```

### 3. Custom Form (`YourForm.java`)

The UI class loads `.zul` files and wires components to fields using `@Wire`.

```java
package ${package};

import java.util.HashMap;
import java.util.Map;
import org.adempiere.webui.panel.CustomForm;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.Selectors;
import org.zkoss.zk.ui.select.annotation.Wire;
import org.adempiere.webui.component.Button;

public class ${yourFormClassName} extends CustomForm {

	@Wire("#myButton")
	protected Button myButton;

	public YourForm() {
		Map<String, Object> arguments = new HashMap<>();
		// Prepare arguments for ZUL (e.g., labels, initial values)
		
		Component view = null;
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		try {
			// Ensure correct class loader for loading ZUL resources from the bundle
			Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
			view = Executions.createComponents("~./${zulFilePath}", this, arguments);
		} finally {
			Thread.currentThread().setContextClassLoader(cl);
		}
		
		// Wire @Wire annotated fields to components found in the view
		Selectors.wireComponents(view, this, false);
	}
}
```
## Example
- `assets/MyActivator.java`: Example for using IMappedFormFactory service.
- `assets/AllocationForm.java`: Example for CustomForm, use of `@Wire` annotation and instantiation of ZK form component from class path ZUL file resources.
- `assets/AllocationFormController.java`: Example for form controller (IFormController) and the use of `@Listen` annotation.
- `assets/*.zul`: Example ZUL files for the ZK form component.

## Best Practices

- **Resource Paths**: Use `~./` prefix in `Executions.createComponents` to refer to resources within the bundle's classpath (usually under `src/web/`).
- **Wiring**: Use `Selectors.wireComponents` for `@Wire` fields and `Selectors.wireEventListeners` for `@Listen` methods.
- **Class Loader**: Always wrap `Executions.createComponents` with the context class loader switch to ensure ZK can find your custom components and resources.
- **Separation of Concerns**: Keep UI wiring and structure in the `CustomForm` class and business logic/event handling in the `IFormController` class.
