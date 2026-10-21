package seedu.insureconnect.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.commons.core.LogsCenter;
import seedu.insureconnect.logic.commands.Command;
import seedu.insureconnect.logic.commands.CommandResult;
import seedu.insureconnect.logic.commands.ConfirmDeleteCommand;
import seedu.insureconnect.logic.commands.DeleteCommand;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.InsureConnectParser;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ReadOnlyInsureConnect;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT = "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final InsureConnectParser insureConnectParser;

    private DeleteCommand pendingDeleteCommand;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        insureConnectParser = new InsureConnectParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;

        Command command = parseCommandWithPendingDeletionHandling(commandText);
        command = resolvePendingConfirmation(command);

        ReadOnlyInsureConnect previousData = new InsureConnect(model.getInsureConnect());
        Predicate<Person> previousFilter = model.getFilteredPersonListPredicate();

        try {
            commandResult = command.execute(model);
        } catch (CommandException ce) {
            model.setInsureConnect(previousData);
            model.updateFilteredPersonList(previousFilter);
            throw ce;
        }

        if (command instanceof DeleteCommand deleteCommand && !deleteCommand.isConfirmed()) {
            pendingDeleteCommand = deleteCommand;
            logger.info("Deletion awaiting confirmation for displayed index "
                    + deleteCommand.getTargetIndexOneBased());
        }

        if (!previousData.equals(model.getInsureConnect())) {
            try {
                storage.saveInsureConnect(model.getInsureConnect());
                logger.info("Successfully saved data after " + command.getClass().getSimpleName());
            } catch (AccessDeniedException e) {
                model.setInsureConnect(previousData);
                model.updateFilteredPersonList(previousFilter);
                throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
            } catch (IOException ioe) {
                model.setInsureConnect(previousData);
                model.updateFilteredPersonList(previousFilter);
                throw new CommandException(String.format(FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
            }
        }

        return commandResult;
    }

    /**
     * Parses a command and cancels a pending deletion for unrelated invalid commands.
     */
    private Command parseCommandWithPendingDeletionHandling(String commandText) throws ParseException {
        try {
            return insureConnectParser.parseCommand(commandText);
        } catch (ParseException pe) {
            if (pendingDeleteCommand != null && isMalformedConfirmation(commandText)) {
                throw new ParseException(String.format(DeleteCommand.MESSAGE_CONFIRM_REMINDER,
                        pendingDeleteCommand.getTargetIndexOneBased()));
            }

            if (pendingDeleteCommand != null) {
                logger.info("Pending deletion cancelled after an unrelated invalid command.");
                pendingDeleteCommand = null;
            }
            throw pe;
        }
    }

    /**
     * Converts a valid confirmation into the stored deletion, or cancels it for another command.
     */
    private Command resolvePendingConfirmation(Command command) throws CommandException {
        if (command instanceof ConfirmDeleteCommand) {
            if (pendingDeleteCommand == null) {
                throw new CommandException(ConfirmDeleteCommand.MESSAGE_NO_PENDING_DELETION);
            }

            logger.info("Deletion confirmation received for displayed index "
                    + pendingDeleteCommand.getTargetIndexOneBased());
            Command confirmedDelete = pendingDeleteCommand.confirmed();
            pendingDeleteCommand = null;
            return confirmedDelete;
        }

        if (pendingDeleteCommand != null) {
            logger.info("Pending deletion cancelled by " + command.getClass().getSimpleName());
            pendingDeleteCommand = null;
        }
        return command;
    }

    /**
     * Returns whether the input appears to be an attempt to confirm a pending deletion.
     */
    private boolean isMalformedConfirmation(String commandText) {
        return commandText.trim().toLowerCase(Locale.ROOT)
                .startsWith(ConfirmDeleteCommand.COMMAND_WORD);
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
