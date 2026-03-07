---
name: idempiere-annotation-process
description: Orchestrates the creation of iDempiere Annotation-based Processes and Factories. 
Use to create a new process in iDempiere using the annotation-based approach.
---

# iDempiere Annotation-based Process

This skill guides you through creating iDempiere processes using the annotation-based approach.

## Workflow

1.  **Identify or create the process package**: Decide which package the process and its factory will reside in.
2.  **Create the Process Factory**: Every bundle that provides annotation-based processes needs a factory that extends `AnnotationBasedProcessFactory`.
3.  **Create the Process Class**: Create the process class extending `SvrProcess` and annotating it with `@Process`.
4.  **Define Parameters**: Use the `@Parameter` annotation to automatically inject process parameters.

## Creating the Process Factory

Use `assets/AnnotationBasedProcessFactory.java` as a template.

-   The factory must extend `org.adempiere.base.AnnotationBasedProcessFactory`.
-   It must be an OSGi component providing the `org.adempiere.base.IProcessFactory` service.
-   Override `getPackages()` to return the list of packages to scan for annotated processes.

```java
@Component(immediate = true, service = IProcessFactory.class, property = {"service.ranking:Integer=100"})
public class MyProcessFactory extends AnnotationBasedProcessFactory {
    @Override
    protected String[] getPackages() {
        return new String[] {"org.example.process"};
    }
}
```

## Creating the Process Class

Use `assets/Process.java` as a template.

-   Annotate the class with `org.adempiere.base.annotation.Process`.
-   Extend `org.compiere.process.SvrProcess`.
-   Use `org.adempiere.base.annotation.Parameter` for process parameters. The field name should match the parameter name in the database (case-insensitive, can use `p_` prefix).

```java
@Process
public class MyProcess extends SvrProcess {
    @Parameter
    private int p_M_Product_ID;

    @Override
    protected void prepare() {
    }

    @Override
    protected String doIt() throws Exception {
        // Logic here
        return "Success";
    }
}
```

## Bundled Resources

-   `assets/AnnotationBasedProcessFactory.java`: Template for the process factory.
-   `assets/Process.java`: Template for the process class.
