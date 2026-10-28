package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import seedu.insureconnect.logic.commands.CommandResult;
import seedu.insureconnect.logic.parser.exceptions.ParseException;

/**
 * Exercises command history through real command-box key and submission events.
 */
public class CommandBoxHistoryTest extends GuiTest {
    @Test
    public void arrows_history_restoresDraftSelectionWithoutExecuting() throws Exception {
        runOnFxThread(() -> {
            List<String> commands = new ArrayList<>();
            TextArea input = createInput(command -> {
                commands.add(command);
                return new CommandResult("Done");
            });
            submit(input, "list");
            submit(input, "help");
            input.setText("unfinished command");
            input.selectRange(8, 3);
            press(input, KeyCode.UP);
            assertEquals("help", input.getText());
            assertEquals(4, input.getCaretPosition());
            press(input, KeyCode.UP);
            assertEquals("list", input.getText());
            press(input, KeyCode.DOWN);
            assertEquals("help", input.getText());
            press(input, KeyCode.DOWN);
            assertDraftSelection(input);
            press(input, KeyCode.DOWN);
            assertDraftSelection(input);
            assertEquals(List.of("list", "help"), commands);
        });
    }

    @Test
    public void submit_failure_recallsEditableCommand() throws Exception {
        runOnFxThread(() -> {
            List<String> commands = new ArrayList<>();
            TextArea input = createInput(command -> {
                commands.add(command);
                throw new ParseException("Invalid command");
            });
            submit(input, "invalid");
            input.setText("draft");
            press(input, KeyCode.UP);
            assertEquals("invalid", input.getText());
            input.appendText(" corrected");
            press(input, KeyCode.ENTER);
            press(input, KeyCode.UP);
            assertEquals("invalid corrected", input.getText());
            assertEquals(List.of("invalid", "invalid corrected"), commands);
        });
    }

    @Test
    public void arrows_emptyHistoryAndModifiedKeys_leaveTextUnchanged() throws Exception {
        runOnFxThread(() -> {
            TextArea input = createInput(command -> new CommandResult("Done"));
            input.setText("draft");
            input.selectRange(3, 1);
            press(input, KeyCode.UP);
            assertEquals("draft", input.getText());
            assertEquals(3, input.getAnchor());
            assertEquals(1, input.getCaretPosition());
            submit(input, "list");
            input.setText("draft");
            assertModifiedKeysKeepDraft(input);
            press(input, KeyCode.LEFT);
            assertEquals("draft", input.getText());
        });
    }

    /**
     * Verifies modifier combinations retain normal text-control behavior.
     */
    private void assertModifiedKeysKeepDraft(TextArea input) {
        for (KeyCode key : List.of(KeyCode.UP, KeyCode.DOWN)) {
            for (int modifier = 0; modifier < 4; modifier++) {
                input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key,
                        modifier == 0, modifier == 1, modifier == 2, modifier == 3));
                assertEquals("draft", input.getText());
            }
        }
    }

    /**
     * Checks both selection direction and text after returning to the draft.
     */
    private void assertDraftSelection(TextArea input) {
        assertEquals("unfinished command", input.getText());
        assertEquals(8, input.getAnchor());
        assertEquals(3, input.getCaretPosition());
    }

    /**
     * Loads and focuses the real command input with its native skin.
     */
    private TextArea createInput(CommandBox.CommandExecutor executor) {
        CommandBox box = new CommandBox(executor);
        show(box.getRoot());
        TextArea input = (TextArea) box.getFocusTarget();
        input.requestFocus();
        return input;
    }

    /**
     * Submits text using the command input's Enter-key handler.
     */
    private void submit(TextArea input, String text) {
        input.setText(text);
        press(input, KeyCode.ENTER);
    }

    /**
     * Sends an unmodified key through the control's event filters.
     */
    private void press(TextArea input, KeyCode key) {
        input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", key, false, false, false, false));
    }
}
