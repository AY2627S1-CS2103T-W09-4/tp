package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class FocusNavigationGuiTest extends GuiTest {

    @Test
    public void startup_commandBoxReceivesInitialFocus() throws Exception {
        AtomicReference<Scene> sceneReference = new AtomicReference<>();

        runOnFxThread(() -> sceneReference.set(createScene(new UiLogicStub())));

        // Use a second task so MainWindow's deferred focus request runs before the assertion.
        runOnFxThread(() -> {
            Scene scene = sceneReference.get();
            assertSame(scene.lookup("#commandTextField"), scene.getFocusOwner());
        });
    }

    @Test
    public void customerListFocus_updatesCustomerWorkspaceCardFocusState() throws Exception {
        runOnFxThread(() -> {
            Scene scene = createScene(new UiLogicStub());
            Node input = scene.lookup("#commandTextField");
            Node customers = scene.lookup("#personListView");
            Node customerWorkspaceCard = scene.lookup("#customerWorkspaceCard");
            PseudoClass customerListFocused = PseudoClass.getPseudoClass("customer-list-focused");

            input.requestFocus();
            assertFalse(customerWorkspaceCard.getPseudoClassStates().contains(customerListFocused));

            customers.requestFocus();
            assertTrue(customerWorkspaceCard.getPseudoClassStates().contains(customerListFocused));

            input.requestFocus();
            assertFalse(customerWorkspaceCard.getPseudoClassStates().contains(customerListFocused));
        });
    }

    @Test
    public void f6_realControls_cyclesInBothDirectionsAndPreservesDraft() throws Exception {
        runOnFxThread(() -> {
            Scene scene = createScene(new UiLogicStub());
            TextArea input = (TextArea) scene.lookup("#commandTextField");
            Node output = scene.lookup("#resultDisplay");
            Node customers = scene.lookup("#personListView");
            input.setText("add n/Unfinished customer");
            input.requestFocus();
            assertCycle(scene, List.of(output, customers, input), false);
            assertCycle(scene, List.of(customers, output, input), true);
            assertEquals("add n/Unfinished customer", input.getText());
        });
    }

    @Test
    public void f6_populatedList_preservesCustomerAndReadOnlyOutput() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            logic.getFilteredPersonList().add(ALICE);
            Scene scene = createScene(logic);
            TextArea output = (TextArea) scene.lookup("#resultDisplay");
            ListView<?> customers = (ListView<?>) scene.lookup("#personListView");
            customers.requestFocus();
            assertCycle(scene, List.of(scene.lookup("#commandTextField"), output, customers), false);
            assertEquals(List.of(ALICE), customers.getItems());
            assertSame(ALICE, customers.getSelectionModel().getSelectedItem());
            assertFalse(output.isEditable());
        });
    }

    @Test
    public void otherKeys_textControlsAndList_remainAvailable() throws Exception {
        runOnFxThread(() -> {
            Scene scene = createScene(new UiLogicStub());
            TextArea input = (TextArea) scene.lookup("#commandTextField");
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
