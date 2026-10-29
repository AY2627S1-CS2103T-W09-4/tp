package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;
import seedu.insureconnect.logic.commands.CommandResult;

/**
 * Exercises command word suggestions through real command-box key events.
 */
public class CommandBoxSuggestionTest extends GuiTest {
    @Test
    public void findSuggestion_partialCommandWord_returnsRestOfFirstMatch() {
        // EP: prefix of exactly one command word
        assertEquals("dd", CommandBox.findSuggestion("a"));
        assertEquals("it", CommandBox.findSuggestion("ex"));
        assertEquals("esize", CommandBox.findSuggestion("r"));
        // EP: prefix of several command words, first in alphabetical order wins
        assertEquals("lear", CommandBox.findSuggestion("c"));
        assertEquals("dit", CommandBox.findSuggestion("e"));
    }

    @Test
    public void findSuggestion_noPartialCommandWord_returnsEmpty() {
        // EP: empty input
        assertEquals("", CommandBox.findSuggestion("")); // boundary value
        // EP: complete command word, with or without arguments
        assertEquals("", CommandBox.findSuggestion("edit")); // boundary value
        assertEquals("", CommandBox.findSuggestion("edit 1"));
        // EP: input that does not start with a lowercase command word prefix
        assertEquals("", CommandBox.findSuggestion(" ed"));
        assertEquals("", CommandBox.findSuggestion("Ed"));
        assertEquals("", CommandBox.findSuggestion("x"));
    }

    @Test
    public void tab_suggestionShowing_fillsCommandWordWithoutExecuting() throws Exception {
        runOnFxThread(() -> {
            List<String> commands = new ArrayList<>();
            CommandBox box = createBox(command -> {
                commands.add(command);
                return new CommandResult("Done");
            });
            TextArea input = getInput(box);
            type(input, "fi");
            assertEquals("nd", getSuggestion(box));
            press(input, KeyCode.TAB);
            // A real key press also sends the typed tab character, which must not add a space.
            input.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "\t", "", KeyCode.UNDEFINED,
                    false, false, false, false));
            assertEquals("find", input.getText());
            assertEquals(4, input.getCaretPosition());
            assertEquals("", getSuggestion(box));
            assertTrue(commands.isEmpty());
        });
    }

    @Test
    public void tab_resizeSuggestion_fillsCommandWordWithoutExecuting() throws Exception {
        runOnFxThread(() -> {
            List<String> commands = new ArrayList<>();
            CommandBox box = createBox(command -> {
                commands.add(command);
                return new CommandResult("Done");
            });
            TextArea input = getInput(box);
            type(input, "r");
            assertEquals("esize", getSuggestion(box));
            press(input, KeyCode.TAB);
            assertEquals("resize", input.getText());
            assertEquals(6, input.getCaretPosition());
            assertEquals("", getSuggestion(box));
            assertTrue(commands.isEmpty());
        });
    }

    @Test
    public void tab_caretNotAtEnd_hidesSuggestionAndDoesNotFillWord() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = createBox(command -> new CommandResult("Done"));
            TextArea input = getInput(box);
            type(input, "fi");
            input.positionCaret(1);
            assertEquals("", getSuggestion(box));
            press(input, KeyCode.TAB);
            assertFalse(input.getText().contains("find"));
            input.selectAll();
            assertEquals("", getSuggestion(box));
        });
    }

    @Test
    public void tab_noSuggestion_insertsSpace() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = createBox(command -> new CommandResult("Done"));
            TextArea input = getInput(box);
            for (String text : List.of(
                    // EP: text that starts no command word
                    "x",
                    // EP: command word that is already complete
                    "find")) {
                type(input, text);
                assertEquals("", getSuggestion(box));
                press(input, KeyCode.TAB);
                assertEquals(text + " ", input.getText());
            }
        });
    }

    @Test
    public void tab_modified_keepsText() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = createBox(command -> new CommandResult("Done"));
            TextArea input = getInput(box);
            // Presses Shift, Ctrl, Alt, then Meta with Tab, one modifier at a time.
            for (int modifier = 0; modifier < 4; modifier++) {
                type(input, "fi");
                input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.TAB,
                        modifier == 0, modifier == 1, modifier == 2, modifier == 3));
                assertEquals("fi", input.getText());
            }
        });
    }

    @Test
    public void right_suggestionShowing_keepsTypedText() throws Exception {
        runOnFxThread(() -> {
            CommandBox box = createBox(command -> new CommandResult("Done"));
            TextArea input = getInput(box);
            type(input, "fi");
            press(input, KeyCode.RIGHT);
            assertEquals("fi", input.getText());
            assertEquals("nd", getSuggestion(box));
        });
    }

    /**
     * Loads and shows the real command box with its native skin.
     */
    private CommandBox createBox(CommandBox.CommandExecutor executor) {
        CommandBox box = new CommandBox(executor);
        show(box.getRoot());
        getInput(box).requestFocus();
        return box;
    }

    /**
     * Returns the command input of the given box.
     */
    private TextArea getInput(CommandBox box) {
        return (TextArea) box.getFocusTarget();
    }

    /**
     * Returns the faded suggestion currently shown after the typed text.
     */
    private String getSuggestion(CommandBox box) {
        return ((Text) box.getRoot().lookup("#commandSuggestionText")).getText();
    }

    /**
     * Replaces the input text and leaves the caret at the end, as typing would.
     */
    private void type(TextArea input, String text) {
        input.setText(text);
        input.end();
    }

    /**
     * Sends an unmodified key through the control's event filters.
     */
    private void press(TextArea input, KeyCode key) {
        input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key, false, false, false, false));
    }
}
