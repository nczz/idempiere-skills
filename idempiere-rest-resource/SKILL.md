---
name: idempiere-rest-resource
description: Orchestrates the creation of iDempiere REST resource extensions. Use when you need to add custom REST endpoints to iDempiere using the ResourceExtension interface and JAX-RS annotations.
---

# iDempiere REST Resource Extension

This skill helps you create custom REST endpoints in iDempiere. It provides templates for the `ResourceExtension` service component and JAX-RS resource classes.

## Workflow

1.  **Identify the entity**: Determine which iDempiere model or data you want to expose via REST.
2.  **Create the Resource class**: Use `TaxResource.java.template` as a starting point for your JAX-RS resource.
3.  **Register the Resource**: Ensure the resource class is added to the `getResourceClasses()` method in your `Resources` class.
4.  **Configure OSGi**: Verify the `Resources` class is annotated with `@Component(service = ResourceExtension.class)`.

## Templates

### ResourceExtension Service Component
Use `assets/Resources.java.template` to create the OSGi service component that registers your REST resources.

### REST Resource Endpoint
Use `assets/TaxResource.java.template` to create a new JAX-RS resource endpoint.

## Example

To create a new endpoint for `MBankStatement`:
1. Copy `assets/TaxResource.java.template` to `BankStatementResource.java`.
2. Replace `${package}`, `${modelClass}` (MBankStatement), `${resourcePath}` (bankstatement), `${className}` (BankStatementResource), and `${entityName}` (BankStatement).
3. Update `Resources.java` to include `BankStatementResource.class` in `getResourceClasses()`.
