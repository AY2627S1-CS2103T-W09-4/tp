package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;

/**
 * Verifies that area navigation preserves text editing state with real JavaFX controls.
 */
public class FocusNavigationSelectionTest extends GuiTest {

    @Test
    public void f6_caretInMiddle_preservesInsertionPoint() throws Exception {
        assertEditingPreserved(false, 2, 2, "drXaft command");
        assertEditingPreserved(true, 2, 2, "drXaft command");
    }

    @Test
    public void f6_selectedText_preservesReplacementRange() throws Exception {
        assertEditingPreserved(false, 2, 5, "drX command");
        assertEditingPreserved(true, 2, 5, "drX command");
    }

    @Test
    public void f6_reverseSelection_preservesAnchorAndCaret() throws Exception {
        assertEditingPreserved(false, 5, 2, "drX command");
        assertEditingPreserved(true, 5, 2, "drX command");
    }

    /**
     * Checks the restored editing state and the effect of the next typed character.
     */
    private void assertEditingPreserved(boolean isReverse, int anchor, int caret, String expectedText)
            throws Exception {
        runOnFxThread(() -> {
            TextField input = new TextField("draft command");
            Scene scene = createScene(input);
            input.requestFocus();
            input.selectRange(anchor, caret);
            cycleAreas(scene, isReverse);
            assertSame(input, scene.getFocusOwner());
            assertEquals(anchor, input.getAnchor());
            assertEquals(caret, input.getCaretPosition());
            input.fireEvent(new KeyEvent(KeyEvent.KEY_TYPED, "X", "", KeyCode.UNDEFINED,
                    false, false, false, false));
            assertEquals(expectedText, input.getText());
        });
    }

    /**
     * Installs the same stage filter as the main window on controls with initialized skins.
     */
    private Scene createScene(TextField input) {
        TextArea output = new TextArea();
        ListView<String> contacts = new ListView<>();
        FocusNavigation navigation = new FocusNavigation(input, output, contacts);
        Scene scene = show(new VBox(input, output, contacts)).getScene();
        scene.getWindow().addEventFilter(KeyEvent.KEY_PRESSED, navigation::handleKeyPressed);
        return scene;
    }

    /**
     * Visits all three areas and returns to the original control using real key events.
     */
    private void cycleAreas(Scene scene, boolean isReverse) {
        for (int i = 0; i < 3; i++) {
            scene.getFocusOwner().fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F6,
                    isReverse, false, false, false));
        }
    }
}
