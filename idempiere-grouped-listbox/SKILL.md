---
name: idempiere-grouped-listbox
description: Orchestrates the creation of iDempiere Annotation-based Forms with Grouped Listboxes. 
Use when you need to create a new form in iDempiere that displays grouped data in a ZK Listbox.
---

# iDempiere Grouped Listbox Form

This skill guides you through creating a new iDempiere form using the annotation-based approach and setting up a ZK Listbox with grouping.

## Workflow

1.  **Identify the Form Requirements**: Determine the data structure (records, group headers, group footers) and the columns to display.
2.  **Create the Form Factory**: If the project doesn't have an `AnnotationBasedFormFactory`, create one using `assets/AnnotationBasedFormFactory.java.template`.
3.  **Create the ADForm**: Create the form class extending `ADForm` using `assets/ADForm.java.template`. Use the `@Form` annotation.
4.  **Create the Groups Model**: Create a model class extending `SimpleGroupsModel` using `assets/SimpleGroupsModel.java.template`.
5.  **Create the Item Renderer**: Create a renderer class extending `AbstractGroupListitemRenderer` using `assets/AbstractGroupListitemRenderer.java.template`.
6.  **Implement Data Fetching**: In the `ADForm`, implement the `createGroupModel` method to fetch data and populate the `SimpleGroupsModel`.
7.  **Customize UI**: Update the `initForm` method in the `ADForm` to add appropriate `Listheader`s and configure the `Listbox`.

## Templates

- `assets/AnnotationBasedFormFactory.java.template`: Template for the OSGi component that registers forms in a package.
- `assets/ADForm.java.template`: Main form class with `@Form` annotation.
- `assets/SimpleGroupsModel.java.template`: Data model for the grouped listbox.
- `assets/AbstractGroupListitemRenderer.java.template`: Renderer for group headers, records, and group footers.

## Placeholder Definitions

- `${package}`: The Java package name.
- `${className}`: The name of the class being created.
- `${rendererClassName}`: The name of the renderer class.
- `${modelClassName}`: The name of the model class.
- `${dataType}`: The class of the individual records (e.g., `MBPartner`).
- `${headType}`: The class of the group headers (e.g., `MBPGroup`).
- `${footType}`: The class of the group footers (e.g., `Map<String, BigDecimal>`).
- `${columnCount}`: Total number of columns in the listbox.
