package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_BOB;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.logic.commands.RemarkCommand.MESSAGE_ADD_REMARK_SUCCESS;
import static seedu.address.logic.commands.RemarkCommand.MESSAGE_DELETE_REMARK_SUCCESS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.Remark;
import seedu.address.testutil.PersonBuilder;

public class RemarkCommandTest {

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new RemarkCommand(null, new Remark(VALID_REMARK_AMY)));
        assertThrows(NullPointerException.class, () -> new RemarkCommand(INDEX_FIRST_PERSON, null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        RemarkCommand command = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY));
        assertThrows(NullPointerException.class, () -> command.execute(null));
    }

    @Test
    public void execute_addRemark_success() {
        assertRemarkSuccess(Index.fromOneBased(3), VALID_REMARK_AMY, MESSAGE_ADD_REMARK_SUCCESS);
    }

    @Test
    public void execute_overwriteRemark_success() {
        assertRemarkSuccess(INDEX_FIRST_PERSON, VALID_REMARK_AMY, MESSAGE_ADD_REMARK_SUCCESS);
    }

    @Test
    public void execute_removeRemark_success() {
        assertRemarkSuccess(INDEX_FIRST_PERSON, "", MESSAGE_DELETE_REMARK_SUCCESS);
    }

    @Test
    public void execute_filteredList_updatesDisplayedPersonAndShowsAllPersons() {
        showPersonAtIndex(model, INDEX_SECOND_PERSON);
        assertRemarkSuccess(INDEX_FIRST_PERSON, VALID_REMARK_BOB, MESSAGE_ADD_REMARK_SUCCESS);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_failure() {
        Index invalidIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new RemarkCommand(invalidIndex, new Remark(VALID_REMARK_AMY)), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_invalidIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandFailure(new RemarkCommand(INDEX_SECOND_PERSON, new Remark(VALID_REMARK_AMY)), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_emptyAddressBook_failure() {
        assertCommandFailure(new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY)), new ModelManager(),
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Checks that only the selected person's remark changes and the full list is shown afterwards.
     */
    private void assertRemarkSuccess(Index index, String remark, String message) {
        Person personToEdit = model.getFilteredPersonList().get(index.getZeroBased());
        Person editedPerson = new PersonBuilder(personToEdit).withRemark(remark).build();
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(personToEdit, editedPerson);

        assertCommandSuccess(new RemarkCommand(index, new Remark(remark)), model,
                String.format(message, Messages.format(editedPerson)), expectedModel);
    }

    @Test
    public void equals() {
        final RemarkCommand standardCommand = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY));

        // same values -> returns true
        RemarkCommand commandWithSameValues = new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY));
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_SECOND_PERSON, new Remark(VALID_REMARK_AMY))));

        // different remark -> returns false
        assertFalse(standardCommand.equals(new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_BOB))));
    }
}
