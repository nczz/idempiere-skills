---
name: idempiere-mapped-process
description: Orchestrates the creation of iDempiere Annotation-based Processes and Factories. 
Use when you need to create a new process in iDempiere using the annotation-based approach with IMappedProcessFactory.
---

# iDempiere Mapped Process Skill

This skill assists in creating annotation-based processes in iDempiere that are automatically registered using the `IMappedProcessFactory` service.

## Workflow

1.  **Identify the Package**: Determine the base package where the process and activator will reside.
2.  **Create/Update Activator**: 
    - Each bundle needs an Activator that scans for annotated processes.
    - The activator must extend `Incremental2PackActivator` and use `@Reference` to inject `IMappedProcessFactory`.
    - Use `assets/Activator.java.template` if a new activator is needed.
3.  **Create Process Class**:
    - Create a class extending `SvrProcess`.
    - Use `@Process` annotation on the class.
    - Use `@Parameter` annotation for process parameters.
    - Use `assets/Process.java.template` as a starting point.
4.  **Register OSGi Component**:
    - Ensure the Activator is registered as an OSGi component (typically via `OSGI-INF/*.xml`).

## Templates

- `assets/Activator.java.template`: Template for the OSGi component activator that performs the package scan.
- `assets/Process.java.template`: Template for the SvrProcess implementation with annotations.

## Example Usage

When asked to create a new process:
1. Read `assets/Process.java.template`.
2. Replace `${package_name}`, `${process_class_name}`, and `${parameter_name}` with appropriate values.
3. Check if an activator exists in the package. If not, use `assets/Activator.java.template`.
4. Ensure the activator's `activate` method calls `mappedProcessFactory.scan(context, "your.package.name")`.
