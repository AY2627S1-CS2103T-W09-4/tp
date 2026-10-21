package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import javafx.stage.Window;

public class UiManagerTest extends GuiTest {

    @Test
    public void start_withWarning_showsConfiguredWindowAndIcon() throws Exception {
        runOnFxThread(() -> {
            UiManager manager = new UiManager(new UiLogicStub(), Path.of("test.json"), "Storage warning");
            Stage stage = new Stage();
            manager.start(stage);
            assertTrue(stage.isShowing());
            assertFalse(stage.getIcons().isEmpty());
            assertEquals("Storage warning", ((TextArea) stage.getScene().lookup("#resultDisplay")).getText());
        });
    }

    @Test
    public void showAlert_displaysOwnedDialogAndReturnsAfterDismissal() throws Exception {
        runOnFxThread(() -> {
            UiManager manager = new UiManager(new UiLogicStub(), Path.of("test.json"));
            Stage owner = new Stage();
            manager.start(owner);
            FutureTask<Void> inspection = new FutureTask<>(() -> {
                inspectAndDismissAlert(owner);
                return null;
            });
            Platform.runLater(inspection);
            manager.showAlertDialogAndWait(Alert.AlertType.INFORMATION, "Notice", "Header", "Details");
            inspection.get(10, TimeUnit.SECONDS);
            assertTrue(owner.isShowing());
        });
    }

    /**
     * Inspects a modal alert during its nested event loop and always dismisses it.
     */
    private void inspectAndDismissAlert(Stage owner) {
        Stage dialog = Window.getWindows().stream().filter(window -> window instanceof Stage)
                .map(window -> (Stage) window).filter(stage -> "Notice".equals(stage.getTitle()))
                .findFirst().orElseThrow();
        try {
            assertEquals(owner, dialog.getOwner());
            DialogPane pane = (DialogPane) dialog.getScene().lookup("#" + UiManager.ALERT_DIALOG_PANE_FIELD_ID);
            assertEquals("Header", pane.getHeaderText());
            assertEquals("Details", pane.getContentText());
            assertTrue(pane.getStylesheets().contains("view/DarkTheme.css"));
            Button dismiss = (Button) pane.lookupButton(ButtonType.OK);
            dismiss.fire();
        } finally {
            dialog.hide();
        }
    }
}
