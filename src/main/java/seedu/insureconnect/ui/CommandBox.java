package seedu.insureconnect.ui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import seedu.insureconnect.logic.commands.CommandResult;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.exceptions.ParseException;

/**
 * The UI component that is responsible for receiving user command inputs.
 */
public class CommandBox extends UiPart<Region> {

    public static final String ERROR_STYLE_CLASS = "error";
    private static final String FXML = "CommandBox.fxml";

    private static final KeyCombination HISTORY_PREVIOUS = new KeyCodeCombination(KeyCode.UP);
    private static final KeyCombination HISTORY_NEXT = new KeyCodeCombination(KeyCode.DOWN);

    private final CommandExecutor commandExecutor;
    private final CommandHistory history = new CommandHistory();
    private int draftAnchor;
    private int draftCaret;

    @FXML
    private TextField commandTextField;

    /**
     * Creates a {@code CommandBox} with the given {@code CommandExecutor}.
     */
    public CommandBox(CommandExecutor commandExecutor) {
        super(FXML);
        this.commandExecutor = commandExecutor;
        commandTextField.addEventFilter(KeyEvent.KEY_PRESSED, this::handleHistoryKey);
        // calls #setStyleToDefault() whenever there is a change to the text of the command box.
        commandTextField.textProperty().addListener((unused1, unused2, unused3) -> setStyleToDefault());
    }

    /**
     * Handles the Enter button pressed event.
     */
    @FXML
    private void handleCommandEntered() {
        String commandText = commandTextField.getText();
        if (commandText.equals("")) {
            return;
        }

        history.record(commandText);
        try {
            commandExecutor.execute(commandText);
            commandTextField.setText("");
        } catch (CommandException | ParseException e) {
            setStyleToIndicateCommandFailure();
        }
    }

    /**
     * Handles unmodified history shortcuts only within the command input.
     */
    private void handleHistoryKey(KeyEvent event) {
        if (HISTORY_PREVIOUS.match(event)) {
            recallPreviousCommand();
        } else if (HISTORY_NEXT.match(event)) {
            recallNextCommand();
        } else {
            return;
        }
        event.consume();
    }

    /**
     * Saves the draft selection before recalling an older submission.
     */
    private void recallPreviousCommand() {
        if (history.isAtDraft()) {
            draftAnchor = commandTextField.getAnchor();
            draftCaret = commandTextField.getCaretPosition();
        }
        showHistoryEntry(history.previous(commandTextField.getText()));
    }

    /**
     * Advances toward the draft, leaving its editing state alone when already there.
     */
    private void recallNextCommand() {
        if (!history.isAtDraft()) {
            showHistoryEntry(history.next(commandTextField.getText()));
        }
    }

    /**
     * Places recalled commands at the end and restores the original draft selection.
     */
    private void showHistoryEntry(String text) {
        commandTextField.setText(text);
        if (history.isAtDraft()) {
            commandTextField.selectRange(draftAnchor, draftCaret);
        } else {
            commandTextField.positionCaret(text.length());
        }
    }

    /**
     * Sets the command box style to use the default style.
     */
    private void setStyleToDefault() {
        commandTextField.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets the command box style to indicate a failed command.
     */
    private void setStyleToIndicateCommandFailure() {
        ObservableList<String> styleClass = commandTextField.getStyleClass();

        if (styleClass.contains(ERROR_STYLE_CLASS)) {
            return;
        }

        styleClass.add(ERROR_STYLE_CLASS);
    }

    /**
     * Returns the control that receives keyboard focus for this area.
     */
    Node getFocusTarget() {
        return commandTextField;
    }

    /**
     * Represents a function that can execute commands.
     */
    @FunctionalInterface
    public interface CommandExecutor {
        /**
         * Executes the command and returns the result.
         *
         * @see seedu.insureconnect.logic.Logic#execute(String)
         */
        CommandResult execute(String commandText) throws CommandException, ParseException;
    }
}
