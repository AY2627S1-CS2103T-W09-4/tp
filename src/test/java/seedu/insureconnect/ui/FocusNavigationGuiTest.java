package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class FocusNavigationGuiTest extends GuiTest {

    @Test
    public void f6_realControls_cyclesInBothDirectionsAndPreservesDraft() throws Exception {
        runOnFxThread(() -> {
            Scene scene = createScene(new UiLogicStub());
            TextField input = (TextField) scene.lookup("#commandTextField");
            Node output = scene.lookup("#resultDisplay");
            Node contacts = scene.lookup("#personScrollPane");
            input.setText("add n/Unfinished customer");
            input.requestFocus();
            assertCycle(scene, List.of(output, contacts, input), false);
            assertCycle(scene, List.of(contacts, output, input), true);
            assertEquals("add n/Unfinished customer", input.getText());
        });
    }

    @Test
    public void f6_populatedCards_preservesCustomerAndReadOnlyOutput() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            logic.getFilteredPersonList().add(ALICE);
            Scene scene = createScene(logic);
            TextArea output = (TextArea) scene.lookup("#resultDisplay");
            GridPane cards = (GridPane) scene.lookup("#personCards");
            Node contacts = scene.lookup("#personScrollPane");
            contacts.requestFocus();
            assertCycle(scene, List.of(scene.lookup("#commandTextField"), output, contacts), false);
            assertEquals(1, cards.getChildren().size());
            assertEquals(ALICE.getName().fullName, ((Label) cards.getChildren().get(0).lookup("#name")).getText());
            assertFalse(output.isEditable());
        });
    }

    @Test
    public void otherKeys_textControlsAndList_remainAvailable() throws Exception {
        runOnFxThread(() -> {
            Scene scene = createScene(new UiLogicStub());
            TextField input = (TextField) scene.lookup("#commandTextField");
            input.setText("draft");
            input.requestFocus();
            input.positionCaret(input.getLength());
            input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.LEFT,
                    false, false, false, false));
            assertSame(input, scene.getFocusOwner());
            assertEquals(4, input.getCaretPosition());
            input.fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F6,
                    false, true, false, false));
            assertSame(input, scene.getFocusOwner());
        });
    }

    /**
     * Exercises the Stage event filter, rather than calling the navigation handler directly.
     */
    private void assertCycle(Scene scene, List<Node> expectedTargets, boolean isReverse) {
        for (Node expected : expectedTargets) {
            scene.getFocusOwner().fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F6,
                    isReverse, false, false, false));
            assertSame(expected, scene.getFocusOwner());
        }
    }

    /**
     * Loads real FXML controls and initializes skins for native key handling.
     */
    private Scene createScene(UiLogicStub logic) {
        MainWindow window = new MainWindow(new Stage(), logic, Path.of("test.json"));
        window.show();
        window.fillInnerParts();
        Scene scene = window.getRoot().getScene();
        scene.getRoot().applyCss();
        scene.getRoot().layout();
        return scene;
    }
}
