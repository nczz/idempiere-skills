---
name: idempiere-window-validator
description: Orchestrates the creation of iDempiere WindowValidator OSGi service components. Use when you need to add custom validation logic to an iDempiere window using the WindowValidator interface, such as before processing a button or before a document action.
---

# Idempiere Window Validator

## Overview

This skill guides the creation of `WindowValidator` implementations in iDempiere. `WindowValidator` is an OSGi service that allows intercepting window-level events like button clicks (`beforeProcess`) or document actions (`beforeDocAction`).

## Implementation Pattern

### 1. Define the Java Component

Create a Java class that implements `org.adempiere.webui.adwindow.validator.WindowValidator`. Use OSGi Declarative Services annotations to register it.

```java
@Component(
    service = WindowValidator.class, 
    immediate = true, 
    property = {
        "AD_Window_UU=UUID-OF-THE-WINDOW", 
        "events=beforeProcess" // or beforeDocAction, etc.
    }
)
public class MyWindowValidator implements WindowValidator {
    @Override
    public void onWindowEvent(WindowValidatorEvent event, Callback<Boolean> callback) {
        // Validation logic
        callback.onCallback(Boolean.TRUE); // return TRUE to proceed, FALSE to abort
    }
}
```

### 2. Available Events

- `beforeProcess`: Triggered before a process button is executed.
- `beforeDocAction`: Triggered before a document action is performed.

### 3. Key Data Structures

- `WindowValidatorEvent`: Contains information about the event.
  - `event.getName()`: The name of the event.
  - `event.getData()`: Data associated with the event (e.g., `IProcessButton` for `beforeProcess`).
  - `event.getWindow()`: Access to the `ADWindow`.

## Examples

### Before Process Validation

Used to ask for confirmation or validate conditions before running a process.

```java
if (WindowValidatorEventType.BEFORE_PROCESS.getName().equals(event.getName())) {
    IProcessButton processButton = (IProcessButton) event.getData();
    // check process or tab data
    Dialog.ask("Confirmation", 0, null, "Proceed?", result -> callback.onCallback(result));
    return;
}
```

### Before Document Action Validation

Used to validate business rules before completing or closing a document.

```java
if (event.getName().equals(WindowValidatorEventType.BEFORE_DOC_ACTION.getName()) ) {
    ADWindow adwindow = event.getWindow();
    GridTab gridTab = adwindow.getADWindowContent().getActiveGridTab();
    // perform validation logic
    if (validationFailed) {
        Dialog.warn(0, null, "Validation Failed");
        callback.onCallback(Boolean.FALSE);
        return;
    }
}
```

## Resources

### assets/
- `WindowValidatorTemplate.java`: A boilerplate implementation of a Window Validator.
