package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import javafx.scene.control.TextArea;

public class ResultDisplayTest extends GuiTest {

    @Test
    public void setFeedback_repeatedUpdates_replacesReadOnlyOutput() throws Exception {
        runOnFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            TextArea output = (TextArea) display.getRoot().lookup("#resultDisplay");
            display.setFeedbackToUser("First result\nSecond line");
            assertEquals("First result\nSecond line", output.getText());
            assertFalse(output.isEditable());
            output.selectRange(0, 5);
            assertEquals("First", output.getSelectedText());
            display.setFeedbackToUser("Replacement");
            assertEquals("Replacement", output.getText());
            display.setFeedbackToUser("");
            assertEquals("", output.getText());
        });
    }

    @Test
    public void setFeedback_null_rejectsWithoutLosingPreviousOutput() throws Exception {
        runOnFxThread(() -> {
            ResultDisplay display = new ResultDisplay();
            display.setFeedbackToUser("Keep this");
            assertThrows(NullPointerException.class, () -> display.setFeedbackToUser(null));
            assertEquals("Keep this", ((TextArea) display.getRoot().lookup("#resultDisplay")).getText());
        });
    }
}
