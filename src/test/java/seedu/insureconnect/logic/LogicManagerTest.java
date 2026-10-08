package seedu.insureconnect.logic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.insureconnect.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.insureconnect.logic.commands.CommandTestUtil.ADDRESS_DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.EMAIL_DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.NAME_DESC_AMY;
import static seedu.insureconnect.logic.commands.CommandTestUtil.PHONE_DESC_AMY;
import static seedu.insureconnect.testutil.Assert.assertThrows;
import static seedu.insureconnect.testutil.TypicalPersons.AMY;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.insureconnect.commons.util.FileUtil;
import seedu.insureconnect.logic.commands.AddCommand;
import seedu.insureconnect.logic.commands.CommandResult;
import seedu.insureconnect.logic.commands.ListCommand;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.ReadOnlyInsureConnect;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.storage.JsonInsureConnectStorage;
import seedu.insureconnect.storage.JsonUserPrefsStorage;
import seedu.insureconnect.storage.StorageManager;
import seedu.insureconnect.testutil.PersonBuilder;

public class LogicManagerTest {
    private static final IOException DUMMY_IO_EXCEPTION = new IOException("dummy IO exception");
    private static final IOException DUMMY_AD_EXCEPTION = new AccessDeniedException("dummy access denied exception");

    @TempDir
    public Path temporaryFolder;

    private Model model = new ModelManager();
    private Logic logic;

    @BeforeEach
    public void setUp() {
        JsonInsureConnectStorage insureConnectStorage =
                new JsonInsureConnectStorage(temporaryFolder.resolve("insureConnect.json"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(temporaryFolder.resolve("userPrefs.json"));
        StorageManager storage = new StorageManager(insureConnectStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);
    }

    @Test
    public void execute_invalidCommandFormat_throwsParseException() {
        String invalidCommand = "uicfhmowqewca";
        assertParseException(invalidCommand, MESSAGE_UNKNOWN_COMMAND);
    }

    @Test
    public void execute_commandExecutionError_throwsCommandException() {
        String deleteCommand = "delete 9";
        assertCommandException(deleteCommand, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validCommand_success() throws Exception {
        String listCommand = ListCommand.COMMAND_WORD;
        assertCommandSuccess(listCommand, ListCommand.MESSAGE_SUCCESS, model);
    }

    @Test
    public void execute_storageThrowsIoException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_IO_EXCEPTION, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT, DUMMY_IO_EXCEPTION.getMessage()));
    }

    @Test
    public void execute_storageThrowsAdException_throwsCommandException() {
        assertCommandFailureForExceptionFromStorage(DUMMY_AD_EXCEPTION, String.format(
                LogicManager.FILE_OPS_PERMISSION_ERROR_FORMAT, DUMMY_AD_EXCEPTION.getMessage()));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> logic.getFilteredPersonList().remove(0));
    }

    /**
     * Executes the command and confirms that
     * - no exceptions are thrown <br>
     * - the feedback message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandSuccess(String inputCommand, String expectedMessage,
            Model expectedModel) throws CommandException, ParseException {
        CommandResult result = logic.execute(inputCommand);
        assertEquals(expectedMessage, result.getFeedbackToUser());
        assertEquals(expectedModel, model);
    }

    /**
     * Executes the command, confirms that a ParseException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertParseException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, ParseException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that a CommandException is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandException(String inputCommand, String expectedMessage) {
        assertCommandFailure(inputCommand, CommandException.class, expectedMessage);
    }

    /**
     * Executes the command, confirms that the exception is thrown and that the result message is correct.
     * @see #assertCommandFailure(String, Class, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage) {
        Model expectedModel = new ModelManager(model.getInsureConnect(), new UserPrefs());
        assertCommandFailure(inputCommand, expectedException, expectedMessage, expectedModel);
    }

    /**
     * Executes the command and confirms that
     * - the {@code expectedException} is thrown <br>
     * - the resulting error message is equal to {@code expectedMessage} <br>
     * - the internal model manager state is the same as that in {@code expectedModel} <br>
     * @see #assertCommandSuccess(String, String, Model)
     */
    private void assertCommandFailure(String inputCommand, Class<? extends Throwable> expectedException,
            String expectedMessage, Model expectedModel) {
        assertThrows(expectedException, expectedMessage, () -> logic.execute(inputCommand));
        assertEquals(expectedModel, model);
    }

    /**
     * Tests the Logic component's handling of an {@code IOException} thrown by the Storage component.
     *
     * @param e the exception to be thrown by the Storage component
     * @param expectedMessage the message expected inside exception thrown by the Logic component
     */
    private void assertCommandFailureForExceptionFromStorage(IOException e, String expectedMessage) {
        Path prefPath = temporaryFolder.resolve("ExceptionUserPrefs.json");

        // Inject LogicManager with a JsonInsureConnectStorage that throws the IOException e when saving
        JsonInsureConnectStorage insureConnectStorage = new JsonInsureConnectStorage(prefPath) {
            @Override
            public void saveInsureConnect(ReadOnlyInsureConnect insureConnect) throws IOException {
                throw e;
            }
        };

        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("ExceptionUserPrefs.json"));
        StorageManager storage = new StorageManager(insureConnectStorage, userPrefsStorage);

        logic = new LogicManager(model, storage);

        // Triggers the saveInsureConnect method by executing an add command
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(addCommand, CommandException.class, expectedMessage, expectedModel);
        assertFalse(model.hasPerson(expectedPerson));
    }

    @Test
    public void execute_unreadableStorageFile_rollsBackAndNonMutatingSucceeds() throws Exception {
        Path filePath = temporaryFolder.resolve("unreadableAddressBook.json");
        FileUtil.writeToFile(filePath, "invalid json");
        JsonInsureConnectStorage insureConnectStorage = new JsonInsureConnectStorage(filePath);
        try {
            insureConnectStorage.readInsureConnect();
        } catch (Exception ignored) {
            // expected DataLoadingException marking file as unreadable
        }
        JsonUserPrefsStorage userPrefsStorage =
                new JsonUserPrefsStorage(temporaryFolder.resolve("unreadableUserPrefs.json"));
        StorageManager storage = new StorageManager(insureConnectStorage, userPrefsStorage);
        logic = new LogicManager(model, storage);

        // Non-mutating command succeeds
        assertCommandSuccess(ListCommand.COMMAND_WORD, ListCommand.MESSAGE_SUCCESS, model);

        // Mutating command fails and rolls back in-memory changes
        String addCommand = AddCommand.COMMAND_WORD + NAME_DESC_AMY + PHONE_DESC_AMY
                + EMAIL_DESC_AMY + ADDRESS_DESC_AMY;
        Person expectedPerson = new PersonBuilder(AMY).withTags().build();
        ModelManager expectedModel = new ModelManager();
        assertCommandFailure(addCommand, CommandException.class, String.format(
                LogicManager.FILE_OPS_ERROR_FORMAT,
                "The existing data file at " + filePath + " is unreadable and cannot be overwritten."),
                expectedModel);
        assertFalse(model.hasPerson(expectedPerson));
    }
}
