---
layout: page
title: Testing guide
---

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<div markdown="span" class="alert alert-secondary">:link: **Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</div>

--------------------------------------------------------------------------------------------------------------------

## JavaFX UI tests

UI tests load the real FXML and run assertions on the JavaFX Application Thread.
Run them with `./gradlew test --tests 'seedu.insureconnect.ui.*'` (Windows:
`gradlew.bat test --tests "seedu.insureconnect.ui.*"`). They briefly open windows,
so run them in a desktop session. On Linux without a display, install Xvfb and
use `xvfb-run -a ./gradlew test`. Linux CI uses the same virtual-display approach;
GUI tests fail rather than silently skipping when no display is available.

New GUI test classes should extend `GuiTest` and use `runOnFxThread` for control
creation, events, and assertions. The helper starts JavaFX once, propagates
assertion failures with bounded waits, serializes GUI tests with a shared resource
lock, and closes their windows after each test. Use `UiLogicStub` and the existing
person builders to supply deterministic data without accessing saved contacts.
The clipboard test restores the previous clipboard contents after checking Copy URL.

`FocusNavigationGuiTest` verifies Stage event routing and real focus owners;
`FocusNavigationTest` covers the navigation algorithm independently of a display.
The GUI tests preserve draft text but do not assert caret/selection restoration:
returning to command input currently selects the entire draft, a known behavior
that should be addressed separately with a regression test.

Generate coverage with `./gradlew clean test coverage`. Open
`build/reports/jacoco/coverage/html/index.html` and select the UI package.
The fatal startup-error path in `UiManager` is not exercised by these in-process
tests because it terminates the JVM. Theme-switching tests belong with the separate
colour-scheme feature, which is not included in this branch.

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.insureconnect.commons.util.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.insureconnect.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.insureconnect.logic.LogicManagerTest`
