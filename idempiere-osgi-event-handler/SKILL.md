---
name: idempiere-osgi-event-handler
description: Orchestrates the creation of iDempiere OSGi component-based event handlers. Use when you need to create a new event handler for iDempiere tables using the standard AbstractEventHandler and OSGi component pattern.
---

# iDempiere OSGi Event Handler Skill

This skill helps you create iDempiere event handlers following the OSGi component pattern, using `AbstractEventHandler` as the base class.

## Workflow

1.  **Identify requirements**: Determine the table name and the event topic (e.g., `IEventTopics.PO_AFTER_CHANGE`).
2.  **Generate Java class**: Use `assets/EventHandlerTemplate.java` to create the event handler class.
    *   Replace `${packageName}` with the target package.
    *   Replace `${className}` with the desired class name.
    *   Replace `${eventTopic}` with the appropriate topic from `IEventTopics`.
    *   Replace `${modelClassName}` with the iDempiere model class (e.g., `MBPartner`).
3.  **Generate OSGi component XML**: Use `assets/ComponentTemplate.xml` to create the XML configuration in `OSGI-INF/`.
    *   The file name should be `${packageName}.${className}.xml`.
4.  **Register the component**: Ensure the XML is referenced in `build.properties` and `META-INF/MANIFEST.MF` (Service-Component header).

## Templates

*   **Java Template**: `assets/EventHandlerTemplate.java`
*   **XML Template**: `assets/ComponentTemplate.xml`

## Example Usage

"Create an event handler for MOrder after change in the org.example.event package."
