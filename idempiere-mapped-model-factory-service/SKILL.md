---
name: idempiere-mapped-model-factory-service
description: Orchestrates the use of iDempiere IMappedModelFactory service to scan annotated model classes.
---

# iDempiere Mapped Model Factory Service Skill

This skill assists in the use of  IMappedModelFactory service to scan and register annotated model classes.

## Workflow

1.  **Identify the Package**: Determine the base package where the activator will reside.
2.  **Identify the Model Package**: Determine the package where the annotated model classes will reside.
2.  **Create/Update Activator**: 
    - Each bundle needs an Activator that scans for annotated model classes.
    - The activator should extend `Incremental2PackActivator` and use `@Reference` to inject `IMappedModelFactory`.
    - Use `assets/Activator.java.template` if a new activator is needed.
4.  **Register OSGi Component**:
    - Ensure the Activator is registered as an OSGi component (typically via `OSGI-INF/*.xml`).

## Templates

- `assets/Activator.java.template`: Template for the OSGi component activator that performs the package scan.

## Example Usage

When asked to scan annotated model classes using the IMappedModelFactory service:
1. Check if an activator exists in the package. If not, use `assets/Activator.java.template`.
2. Ensure the activator's `activate` method calls `mappedModelFactory.scan(context, "model.package.name")`.
