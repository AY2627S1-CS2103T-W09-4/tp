package seedu.insureconnect.ui;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.logic.Logic;
import seedu.insureconnect.logic.commands.CommandResult;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.person.Person;

/**
 * Supplies deterministic command results and records GUI interactions without touching disk.
 */
class UiLogicStub implements Logic {

    private final ObservableList<Person> persons = FXCollections.observableArrayList();
    private final List<String> commands = new ArrayList<>();
    private GuiSettings guiSettings = new GuiSettings();
    private CommandBox.CommandExecutor executor = command -> new CommandResult("Executed: " + command);

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        commands.add(commandText);
        return executor.execute(commandText);
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return persons;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return guiSettings;
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        this.guiSettings = guiSettings;
    }

    List<String> getCommands() {
        return List.copyOf(commands);
    }

    /**
     * Configures the result or checked failure of the next command.
     */
    void setExecutor(CommandBox.CommandExecutor executor) {
        this.executor = executor;
    }
}
