---
name: idempiere-event-annotation
description: Orchestrates the creation of iDempiere Annotation-based Event Delegates. 
Use when you need to handle iDempiere events (Model events, Document events, Login events) using the annotation-based approach.
---

# iDempiere Annotation-based Event Handling

This skill provides templates and guidance for implementing event delegates in iDempiere using annotations.

## Workflows

### 1. Create AnnotationBasedEventManager
Every bundle that wants to use annotation-based events must have exactly one class extending `AnnotationBasedEventManager` and annotated with OSGi `@Component(immediate = true)`.

- **Template**: [references/manager_template.java](references/manager_template.java)
- **Key Task**: Override `getPackages()` to return the list of packages where your delegate classes are located.

### 2. Create Event Delegates
Delegates are classes that perform the actual work when an event occurs. They must be annotated with `@EventTopicDelegate`.

#### After Login Events
- **Template**: [references/after_login_delegate_template.java](references/after_login_delegate_template.java)
- **Base Class**: `AfterLoginEventDelegate`
- **Events**: `onAfterLogin`, `@AfterLoadPref`

#### Model/PO Events (Persistence)
- **Template**: [references/model_event_delegate_template.java](references/model_event_delegate_template.java)
- **Base Class**: `ModelEventDelegate<T>`
- **Annotation**: `@ModelEventTopic(modelClass = T.class)`
- **Events**: `@BeforeNew`, `@AfterNew`, `@BeforeChange`, `@AfterChange`, `@BeforeDelete`, `@AfterDelete`, `@PostCreate`, `@PostUpdate`, `@PostDelete`

#### Document Action Events
- **Template**: [references/doc_event_delegate_template.java](references/doc_event_delegate_template.java)
- **Base Class**: `ModelEventDelegate<T>`
- **Annotation**: `@ModelEventTopic(modelClass = T.class)`
- **Events**: `@BeforePrepare`, `@AfterPrepare`, `@BeforeComplete`, `@AfterComplete`, `@BeforeClose`, `@AfterClose`, `@BeforeVoid`, `@AfterVoid`, `@BeforeReverseAccrual`, `@AfterReverseAccrual`, `@DocAction` (for other document actions)

#### Facts Validation Events
- **Template**: [references/facts_delegate_template.java](references/facts_delegate_template.java)
- **Base Class**: `FactsValidateDelegate<T>`
- **Annotation**: `@ModelEventTopic(modelClass = T.class)`
- **Events**: `onFactsValidate`

## Best Practices
- Keep delegates focused on a single responsibility.
- Use the correct base class to gain access to helper methods and context (e.g., `getModel()` in `ModelEventDelegate`).
- Ensure the package containing delegates is registered in your `AnnotationBasedEventManager`.
- Delegates are instantiated per event, so they are thread-safe by design for state held within the instance.

## Constraints & Validation (Gemini CLI)
- **Path Awareness**: Always use the `ls` or `search` tools to confirm that the `modelClass` defined in `@ModelEventTopic` exists in the source tree before generating code.
- **Contextual Search**: Check the root directory and any paths displayed in `/directory show`.
- **Validation Rule**: If `modelClass = MOrder.class` is requested, verify `**/MOrder.java` exists.

## Constraints & Validation (Antigravity/VSCode Context)

### 1. Workspace Awareness
- **Context Source**: The user is running inside the Antigravity IDE. 
- **Rule**: Before generating code, use the `ls -R` or `find` command to locate the `modelClass` file (e.g., `MOrder.java`) within the current workspace.
- **Verification**: 
    - If the file is open in the editor (visible via IDE context), accept it immediately.
    - If not open, search the `./src` or `./base` directories.
    - **FAILURE CONDITION**: If `modelClass` cannot be found in the file system, STOP and ask the user to provide the full package name or add the correct directory via `/directory add`.

### 2. Classpath Check
- **Constraint**: Do not assume standard iDempiere packages (like `org.compiere.model`).
- **Action**: If the `modelClass` is an interface (starts with `I_`), verify that the corresponding implementation class also exists or is available in the target platform.