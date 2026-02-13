# iDempiere Gemini Skills

This repository contains a collection of skills for the [Gemini CLI](https://github.com/google/gemini-cli) to assist in iDempiere development.

## Available Skills

| Skill | Description |
|---|---|
| `idempiere-annotation-process` | Orchestrates the creation of iDempiere Annotation-based Processes and Factories. Use when you need to create a new process in iDempiere using the annotation-based approach. |
| `idempiere-callout-generator` | Generates iDempiere column callouts and callout factories based on annotation templates. Use when you need to create a new callout for a database column or a new callout factory in an iDempiere OSGi bundle. |
| `idempiere-event-annotation` | Orchestrates the creation of iDempiere Annotation-based Event Managers and Delegates. Use when you need to handle iDempiere events (Model events, Document events, Login events) using the annotation-based approach. |
| `idempiere-grouped-listbox` | Orchestrates the creation of iDempiere Annotation-based Forms with Grouped Listboxes. Use when you need to create a new form in iDempiere that displays grouped data in a ZK Listbox. |
| `idempiere-mapped-process` | Orchestrates the creation of iDempiere Annotation-based Processes and Factories. Use when you need to create a new process in iDempiere using the annotation-based approach with `IMappedProcessFactory`. |
| `idempiere-osgi-event-handler` | Orchestrates the creation of iDempiere OSGi component-based event handlers. Use when you need to create a new event handler for iDempiere tables using the standard `AbstractEventHandler` and OSGi component pattern. |
| `idempiere-rest-resource` | Orchestrates the creation of iDempiere REST resource extensions. Use when you need to add custom REST endpoints to iDempiere using the `ResourceExtension` interface and JAX-RS annotations. |
| `idempiere-window-validator` | Orchestrates the creation of iDempiere WindowValidator OSGi service components. Use when you need to add custom validation logic to an iDempiere window using the `WindowValidator` interface. |
| `idempiere-wlistbox-custom-editor` | Instructions for using a custom `WEditor` for a `WListbox` (`WTableColumn`) in iDempiere ZK web UI. Use when you need to customize the rendering or editing behavior of a specific column in a `WListbox`. |
| `idempiere-zul-form` | Orchestrates the creation of custom forms in iDempiere using the ZK web UI framework, leveraging annotations and the `IMappedFormFactory` for automatic registration. |
| `idempiere-mapped-model-factory-service` | Orchestrates the use of iDempiere IMappedModelFactory service to scan annotated mnodel classes. |

## Gemini CLI Installation Instruction

To use these skills, you need to have the Gemini CLI installed.

### Prerequisites

- Node.js (v18 or later)
- npm

### Installation

1. Install the Gemini CLI globally:
   ```bash
   npm install -g @google/gemini-cli
   ```

2. Install individual skills using the `--path` flag:
   ```bash
   # To install all skills, run these commands:
   gemini skills install https://github.com/hengsin/idempiere-skills.git --path idempiere-annotation-process
   gemini skills install https://github.com/hengsin/idempiere-skills.git --path idempiere-callout-generator
   gemini skills install https://github.com/hengsin/idempiere-skills.git --path idempiere-event-annotation
   gemini skills install https://github.com/hengsin/idempiere-skills.git --path idempiere-grouped-listbox
   gemini skills install https://github.com/hengsin/idempiere-skills.git --path idempiere-mapped-process
   gemini skills install https://github.com/hengsin/idempiere-skills.git --path idempiere-osgi-event-handler
   gemini skills install https://github.com/hengsin/idempiere-skills.git --path idempiere-rest-resource
   gemini skills install https://github.com/hengsin/idempiere-skills.git --path idempiere-window-validator
   gemini skills install https://github.com/hengsin/idempiere-skills.git --path idempiere-wlistbox-custom-editor
   gemini skills install https://github.com/hengsin/idempiere-skills.git --path idempiere-zul-form
   gemini skills install https://github.com/hengsin/idempiere-skills.git --path idempiere-mapped-model-factory-service
   ```

## Usage

Once installed, you can activate a skill within a Gemini CLI session:

```bash
gemini activate_skill <skill-name>
```

Replace `<skill-name>` with one of the skills listed in the table above.
