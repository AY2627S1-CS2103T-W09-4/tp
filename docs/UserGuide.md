---
layout: page
title: User Guide
---

InsureConnect is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, InsureConnect can help you manage contacts faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/AY2627S1-CS2103T-W09-4/tp/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your InsureConnect.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar insureconnect.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01` : Adds a contact named `John Doe` to InsureConnect.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* **Name constraints**: `NAME` must start with an alphanumeric character and can only contain alphanumeric characters, spaces, and the characters: `-`, `'`, `.`, and `/`. Names cannot consist solely of punctuation (e.g. `---`, `...`, `///` are invalid).

* **Escape character (`!`) for name prefix collisions**:
  Command prefixes are identified by a space followed by a letter and a slash (e.g. ` a/` for address, ` t/` for tag). Names that contain patterns such as ` a/` (common in Malaysian and Singaporean names with `a/l` or `a/p`, e.g. `Mohd a/l Kassim`) will conflict with these command prefixes if typed directly.<br>
  To avoid prefix collisions, prefix the collision point with an exclamation mark `!` within the name field:
  * `n/Mohd !a/l Kassim` or `n/Mohd a!/l Kassim` will be saved as `Mohd a/l Kassim`.
  * Indian patronymics such as `s/o` or `d/o` (e.g. `n/Ravi s/o Muthu`) do not conflict with existing prefixes and do not require escaping (though `!s/o` is also supported).
  * If you need a literal exclamation mark in a name, type `!!`.
  * **Note:** `!` is treated as an escape character **only for the name field (`n/`)**. Other fields (such as `e/` email or `a/` address) do not use `!` as an escape character, ensuring that characters like `!` in email addresses (e.g. `user!name@example.com`) are preserved as-is.

* Extraneous parameters for commands that take no parameters, such as `help`, `list`, `exit`, and `clear`, are ignored.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Adding a person: `add`

Adds a person to InsureConnect.

A person is a duplicate only when both their name and phone number match an existing person.
Name matching ignores case, leading or trailing spaces, and repeated spaces between words.
For example, `John Doe` and `JOHN  DOE` with the same phone number are duplicates.
People with the same name but different phone numbers, or different names with the same phone number, are allowed.
Names are displayed as entered after surrounding spaces are trimmed by the command parser.

Format: `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​`

<div markdown="span" class="alert alert-primary">:bulb: **Tip:**
A person can have any number of tags, including zero.
</div>

Examples:
* `add n/John Doe p/98765432 e/johnd@example.com a/John street, block 123, #01-01`
* `add n/Betsy Crowe t/friend e/betsycrowe@example.com a/Newgate Prison p/1234567 t/criminal`
* `add n/Ravi s/o Muthu p/91234567 e/ravi@example.com a/Blk 123 Jurong West`
* `add n/Mohd !a/l Kassim p/81234567 e/mohd@example.com a/Blk 456 Clementi Ave 3`

### Listing all persons: `list`

Shows a list of all persons in InsureConnect.

Format: `list`

### Editing a person: `edit`

Edits an existing person in InsureConnect.

Format: `edit INDEX [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`

* Edits the person at the specified `INDEX`. The index refers to the index number shown in the displayed person list. The index **must be a positive integer** 1, 2, 3, …​
* At least one of the optional fields must be provided.
* Existing values will be updated to the input values.
* An edit is rejected if the resulting name and phone number match another person, using the same duplicate rule as `add`.
* When editing tags, all of the person's existing tags are removed; adding tags is not cumulative.
* To remove all of a person's tags, enter `t/` without a tag after it.

Examples:
*  `edit 1 p/91234567 e/johndoe@example.com` Edits the phone number and email address of the 1st person to be `91234567` and `johndoe@example.com` respectively.
*  `edit 2 n/Betsy Crower t/` Edits the name of the 2nd person to be `Betsy Crower` and clears all existing tags.
*  `edit 1 n/Mohd !a/l Kassim` Edits the name of the 1st person to be `Mohd a/l Kassim`.

### Locating persons by name: `find`

Finds persons with a word in their name starting with any of the given keywords.

Format: `find KEYWORD [MORE_KEYWORDS]`

* The search is case-insensitive; for example, `hans` matches `Hans`.
* Keyword order does not matter; for example, `Hans Bo` matches `Bo Hans`.
* The search considers only names.
* Keywords match the start of any name word; for example, `Han` matches `Hans` and `Bo Hans`.
* Full words also match, but middle-of-word and suffix matches do not; for example, `ans` does not match `Hans`.
* Persons matching at least one keyword are returned (an `OR` search); for example, `Hans Bo` returns `Hans Gruber` and `Bo Yang`.

Examples:
* `find John` returns `john` and `John Doe`
* `find jo` returns `John Doe` and `Mary Jones`
* `find alex david` returns `Alex Yeoh`, `David Li`<br>
  ![result for 'find alex david'](images/findAlexDavidResult.png)

### Deleting a person: `delete`

Deletes the specified person from InsureConnect.

Format: `delete INDEX`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​

Examples:
* `list` followed by `delete 2` deletes the 2nd person in InsureConnect.
* `find Betsy` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Clearing all entries: `clear`

Clears all entries from InsureConnect.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

InsureConnect automatically saves data after every command. You do not need to save manually.

### Editing the data file

InsureConnect data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. The legacy filename is retained so existing installations continue to load their records. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, InsureConnect starts with an empty contact list at the next run. The invalid file remains on disk until you run a command (InsureConnect saves after every command). Still, we recommend backing up the file before editing it.<br>
Furthermore, certain edits can cause InsureConnect to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous InsureConnect home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add n/NAME p/PHONE_NUMBER e/EMAIL a/ADDRESS [t/TAG]…​` <br> e.g., `add n/James Ho p/22224444 e/jamesho@example.com a/123, Clementi Rd, 1234665 t/friend t/colleague` <br> e.g. escaping `a/`: `add n/Mohd !a/l Kassim p/81234567 e/mohd@example.com a/Blk 456 Clementi Ave 3`
**Clear** | `clear`
**Delete** | `delete INDEX`<br> e.g., `delete 3`
**Edit** | `edit INDEX [n/NAME] [p/PHONE_NUMBER] [e/EMAIL] [a/ADDRESS] [t/TAG]…​`<br> e.g., `edit 2 n/James Lee e/jameslee@example.com`<br> e.g. escaping `a/`: `edit 1 n/Mohd !a/l Kassim`
**Find** | `find KEYWORD [MORE_KEYWORDS]`<br> e.g., `find James Jake`
**List** | `list`
**Help** | `help`
