package seedu.insureconnect.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.commons.core.LogsCenter;
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

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        insureConnectParser = new InsureConnectParser();
    }

    /**
     * Restores customer data and the displayed view after an unsuccessful command.
     */
    private void restoreModelState(ReadOnlyInsureConnect previousData,
            Predicate<Person> previousFilter, Comparator<? super Person> previousComparator) {
        model.setInsureConnect(previousData);
        model.updateFilteredPersonList(previousFilter);
        model.updatePersonListComparator(previousComparator);
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        CommandResult commandResult;
        Command command = insureConnectParser.parseCommand(commandText);
        ReadOnlyInsureConnect previousData = new InsureConnect(model.getInsureConnect());
        Predicate<Person> previousFilter = model.getFilteredPersonListPredicate();
        Comparator<? super Person> previousComparator = model.getPersonListComparator();

        try {
            commandResult = command.execute(model);
        } catch (CommandException ce) {
            restoreModelState(previousData, previousFilter, previousComparator);
            throw ce;
        }

        if (!previousData.equals(model.getInsureConnect())) {
            try {
                storage.saveInsureConnect(model.getInsureConnect());
            } catch (AccessDeniedException e) {
                restoreModelState(previousData, previousFilter, previousComparator);
                throw new CommandException(String.format(FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
            } catch (IOException ioe) {
                restoreModelState(previousData, previousFilter, previousComparator);
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
