package seedu.insureconnect.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.commons.core.LogsCenter;
import seedu.insureconnect.logic.commands.ClearCommand;
import seedu.insureconnect.logic.commands.Command;
import seedu.insureconnect.logic.commands.CommandResult;
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
    private boolean awaitingClearConfirmation;

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

        if (awaitingClearConfirmation) {
            awaitingClearConfirmation = false;
            if (!"y".equals(commandText.trim().toLowerCase(Locale.ROOT))) {
                return new CommandResult(ClearCommand.MESSAGE_CANCELLED);
            }
            return executeCommand(new ClearCommand());
        }

        Command command = insureConnectParser.parseCommand(commandText);
        if (command instanceof ClearCommand) {
            awaitingClearConfirmation = true;
            return new CommandResult(ClearCommand.MESSAGE_CONFIRMATION, false, false, true);
        }

        return executeCommand(command);
    }

    /** Executes a parsed command and saves any resulting data changes. */
    private CommandResult executeCommand(Command command) throws CommandException {
        CommandResult commandResult;
        ReadOnlyInsureConnect previousData = new InsureConnect(model.getInsureConnect());
        Predicate<Person> previousFilter = model.getFilteredPersonListPredicate();

        try {
            commandResult = command.execute(model);
        } catch (CommandException ce) {
            model.setInsureConnect(previousData);
            model.updateFilteredPersonList(previousFilter);
            throw ce;
        }

        if (!previousData.equals(model.getInsureConnect())) {
            try {
                storage.saveInsureConnect(model.getInsureConnect());
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
