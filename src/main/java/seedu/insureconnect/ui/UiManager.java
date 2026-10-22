package seedu.insureconnect.ui;

import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import seedu.insureconnect.MainApp;
import seedu.insureconnect.commons.core.ColourScheme;
import seedu.insureconnect.commons.core.LogsCenter;
import seedu.insureconnect.commons.util.StringUtil;
import seedu.insureconnect.logic.Logic;

/**
 * The manager of the UI components.
 */
public class UiManager implements Ui {

    public static final String ALERT_DIALOG_PANE_FIELD_ID =
            "alertDialogPane";

    private static final Logger logger =
            LogsCenter.getLogger(UiManager.class);

    private static final String ICON_APPLICATION =
            "/images/insureconnect_32.png";

    private Logic logic;
    private Path dataFilePath;
    private String initialFeedback;
    private MainWindow mainWindow;

    /**
     * Creates a {@code UiManager}.
     *
     * <p>The data file path is temporarily retained for compatibility with
     * existing callers. It is no longer displayed in the interface.</p>
     */
    public UiManager(Logic logic, Path dataFilePath) {
        this(logic, dataFilePath, null);
    }

    /**
     * Creates a {@code UiManager} with optional startup feedback.
     *
     * <p>The data file path is temporarily retained for compatibility with
     * existing callers. It is no longer displayed in the interface.</p>
     */
    public UiManager(Logic logic, Path dataFilePath,
                     String initialFeedback) {
        this.logic = logic;
        this.dataFilePath = dataFilePath;
        this.initialFeedback = initialFeedback;
    }

    /**
     * Starts the user interface.
     */
    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting UI...");

        primaryStage.getIcons().add(
                getImage(ICON_APPLICATION));

        try {
            mainWindow = new MainWindow(
                    primaryStage,
                    logic,
                    dataFilePath,
                    initialFeedback);

            mainWindow.show();
            mainWindow.fillInnerParts();
        } catch (Throwable e) {
            logger.severe(StringUtil.getDetails(e));
            showFatalErrorDialogAndShutdown(
                    primaryStage, "Fatal error during initializing", e);
        }
    }

    /**
     * Loads an image from the application's resources.
     */
    private Image getImage(String imagePath) {
        return new Image(
                MainApp.class.getResourceAsStream(imagePath));
    }

    /**
     * Shows an alert dialog using the main application window as its owner.
     */
    void showAlertDialogAndWait(AlertType type,
                                String title,
                                String headerText,
                                String contentText) {
        showAlertDialogAndWait(
                mainWindow.getPrimaryStage(),
                type,
                title,
                headerText,
                contentText,
                logic.getGuiSettings().getColourScheme());
    }

    /**
     * Shows an alert dialog and waits for the user to close it.
     */
    private void showAlertDialogAndWait(Stage owner,
                                        AlertType type,
                                        String title,
                                        String headerText,
                                        String contentText,
                                        ColourScheme colourScheme) {
        Alert alert = new Alert(type);

        alert.getDialogPane()
                .getStylesheets()
                .add("view/DarkTheme.css");

        ColourSchemeStyles.apply(
                alert.getDialogPane().getStylesheets(),
                colourScheme);

        alert.initOwner(owner);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.getDialogPane().setId(
                ALERT_DIALOG_PANE_FIELD_ID);

        alert.showAndWait();
    }

    /**
     * Shows a fatal-error dialog using a safe default theme and exits the application.
     */
    private void showFatalErrorDialogAndShutdown(
            Stage owner, String title, Throwable exception) {
        logger.severe(
                title
                        + " "
                        + exception.getMessage()
                        + StringUtil.getDetails(exception));

        showAlertDialogAndWait(
                owner,
                AlertType.ERROR,
                title,
                exception.getMessage(),
                exception.toString(),
                ColourScheme.DARK);

        Platform.exit();
        System.exit(1);
    }
}
