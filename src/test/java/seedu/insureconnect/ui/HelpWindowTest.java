package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.stage.Stage;

public class HelpWindowTest extends GuiTest {

    @Test
    public void showHideAndFocus_reusesHelpWindowWithGuideLink() throws Exception {
        runOnFxThread(() -> {
            Stage stage = new Stage();
            HelpWindow help = new HelpWindow(stage);
            assertFalse(help.isShowing());
            Label message = (Label) stage.getScene().lookup("#helpMessage");
            assertEquals(HelpWindow.HELP_MESSAGE, message.getText());
            help.show();
            assertTrue(help.isShowing());
            help.focus();
            assertTrue(help.isShowing());
            help.hide();
            assertFalse(help.isShowing());
            help.show();
            assertTrue(help.isShowing());
        });
    }

    @Test
    public void copyButton_copiesUserGuideUrl() throws Exception {
        runOnFxThread(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            Map<DataFormat, Object> original = copyClipboardContents(clipboard);
            try {
                HelpWindow help = new HelpWindow();
                Button copy = (Button) help.getRoot().getScene().lookup("#copyButton");
                copy.fire();
                assertEquals(HelpWindow.USERGUIDE_URL, clipboard.getString());
            } finally {
                clipboard.setContent(original);
            }
        });
    }

    /**
     * Captures every available format so the test can restore the user's clipboard.
     */
    private Map<DataFormat, Object> copyClipboardContents(Clipboard clipboard) {
        Map<DataFormat, Object> contents = new HashMap<>();
        for (DataFormat format : clipboard.getContentTypes()) {
            contents.put(format, clipboard.getContent(format));
        }
        return contents;
    }
}
