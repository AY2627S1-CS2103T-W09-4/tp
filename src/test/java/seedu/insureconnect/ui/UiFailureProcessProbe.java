package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.model.person.Person;

/**
 * Exercises the fatal UI path in a child process where System.exit is safe.
 */
public final class UiFailureProcessProbe {
    private static final String FAILURE_MESSAGE = "Simulated UI initialization failure";

    private UiFailureProcessProbe() {
    }

    /**
     * Starts the real UI manager with a deterministic initialization failure.
     */
    public static void main(String[] args) {
        Platform.startup(() -> {
            try {
                Stage owner = new Stage();
                UiManager manager = new UiManager(failingLogic(args[0]), Path.of("test.json"));
                Platform.runLater(() -> inspectAndDismissAlert(owner));
                manager.start(owner);
                System.exit(3); // A fatal initialization failure must not return normally.
            } catch (Throwable failure) {
                failure.printStackTrace();
                System.exit(2);
            }
        });
    }

    /**
     * Fails either while constructing the window or while populating its controls.
     */
    private static UiLogicStub failingLogic(String phase) {
        return new UiLogicStub() {
            private boolean hasFailed;

            @Override
            public GuiSettings getGuiSettings() {
                if ("settings".equals(phase)) {
                    throw new IllegalStateException(FAILURE_MESSAGE);
                }
                if ("constructor".equals(phase) && !hasFailed) {
                    hasFailed = true;
                    throw new IllegalStateException(FAILURE_MESSAGE);
                }
                return super.getGuiSettings();
            }

            @Override
            public ObservableList<Person> getFilteredPersonList() {
                throw new IllegalStateException(FAILURE_MESSAGE);
            }
        };
    }

    /**
     * Verifies the actual modal error dialog before allowing the production shutdown to proceed.
     */
    private static void inspectAndDismissAlert(Stage owner) {
        try {
            Stage dialog = Window.getWindows().stream().filter(Stage.class::isInstance).map(Stage.class::cast)
                    .filter(stage -> "Fatal error during initializing".equals(stage.getTitle()))
                    .findFirst().orElseThrow();
            assertSame(owner, dialog.getOwner());
            DialogPane pane = (DialogPane) dialog.getScene().lookup("#" + UiManager.ALERT_DIALOG_PANE_FIELD_ID);
            assertTrue(pane.getStylesheets().contains("view/DarkTheme.css"));
            assertEquals(FAILURE_MESSAGE, pane.getHeaderText());
            assertEquals("java.lang.IllegalStateException: " + FAILURE_MESSAGE, pane.getContentText());
            System.out.println("FATAL_DIALOG_VERIFIED");
            Button dismiss = (Button) pane.lookupButton(ButtonType.OK);
            dismiss.fire();
        } catch (Throwable failure) {
            failure.printStackTrace();
            System.exit(2);
        }
    }
}
