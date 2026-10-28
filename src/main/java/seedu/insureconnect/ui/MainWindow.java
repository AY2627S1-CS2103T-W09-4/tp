package seedu.insureconnect.ui;

import java.awt.Point;
import java.nio.file.Path;
import java.util.logging.Logger;

import javafx.beans.binding.Bindings;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import seedu.insureconnect.commons.core.ColourScheme;
import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.commons.core.LogsCenter;
import seedu.insureconnect.logic.Logic;
import seedu.insureconnect.logic.commands.CommandResult;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.person.Person;

/**
 * The main application window.
 */
public class MainWindow extends UiPart<Stage> {

    private static final String FXML = "MainWindow.fxml";
    private static final double RESIZE_MARGIN = 12;

    private final Logger logger = LogsCenter.getLogger(getClass());

    private Stage primaryStage;
    private Logic logic;
    private String initialFeedback;

    private PersonListPanel personListPanel;
    private CustomerDetailsPanel customerDetailsPanel;
    private ResultDisplay resultDisplay;
    private HelpWindow helpWindow;

    @FXML
    private StackPane commandBoxPlaceholder;

    @FXML
    private StackPane resultDisplayPlaceholder;

    @FXML
    private StackPane personListPanelPlaceholder;

    @FXML
    private StackPane customerDetailsPlaceholder;

    @FXML
    private Label customerCountLabel;

    @FXML
    private MenuItem helpMenuItem;

    @FXML
    private RadioMenuItem darkSchemeMenuItem;

    @FXML
    private RadioMenuItem lightSchemeMenuItem;

    /**
     * Creates a {@code MainWindow}.
     *
     * <p>The data file path parameter is temporarily retained so existing callers
     * continue to compile. It is no longer displayed in the interface.</p>
     */
    public MainWindow(Stage primaryStage, Logic logic, Path ignoredDataFilePath) {
        this(primaryStage, logic, ignoredDataFilePath, null);
    }

    /**
     * Creates a {@code MainWindow} with optional startup feedback.
     *
     * <p>The data file path parameter is temporarily retained so existing callers
     * continue to compile. It is no longer displayed in the interface.</p>
     */
    public MainWindow(Stage primaryStage, Logic logic, Path ignoredDataFilePath,
                      String initialFeedback) {
        super(FXML, primaryStage);

        this.primaryStage = primaryStage;
        this.logic = logic;
        this.initialFeedback = initialFeedback;

        helpWindow = new HelpWindow();
        configureWindow();
    }

    /**
     * Restores window preferences and connects menu actions.
     */
    private void configureWindow() {
        GuiSettings settings = logic.getGuiSettings();
        GuiSettings safeSettings = fitToScreen(settings, screenFor(settings.getWindowCoordinates()).getVisualBounds());
        logic.setGuiSettings(safeSettings);
        setWindowDefaultSize(safeSettings);
        setAccelerators();
        configureColourSchemeMenu();
        applyColourScheme(logic.getGuiSettings().getColourScheme());
    }

    /**
     * Connects the mutually exclusive colour-scheme menu choices.
     */
    private void configureColourSchemeMenu() {
        ToggleGroup schemes = new ToggleGroup();

        darkSchemeMenuItem.setToggleGroup(schemes);
        lightSchemeMenuItem.setToggleGroup(schemes);

        darkSchemeMenuItem.setOnAction(
                event -> applyColourScheme(ColourScheme.DARK));
        lightSchemeMenuItem.setOnAction(
                event -> applyColourScheme(ColourScheme.LIGHT));
    }

    /**
     * Applies and saves the selected colour scheme.
     */
    private void applyColourScheme(ColourScheme colourScheme) {
        updateWindowColours(colourScheme);
        updateColourSchemeSelection(colourScheme);
        saveColourSchemePreference(colourScheme);
    }

    /**
     * Applies the colour scheme to the main and help windows.
     */
    private void updateWindowColours(ColourScheme colourScheme) {
        ColourSchemeStyles.apply(
                primaryStage.getScene().getStylesheets(), colourScheme);

        ColourSchemeStyles.apply(
                helpWindow.getRoot().getScene().getStylesheets(), colourScheme);
    }

    /**
     * Marks the active colour scheme in the menu.
     */
    private void updateColourSchemeSelection(ColourScheme colourScheme) {
        darkSchemeMenuItem.setSelected(colourScheme == ColourScheme.DARK);
        lightSchemeMenuItem.setSelected(colourScheme == ColourScheme.LIGHT);
    }

    /**
     * Saves the selected colour scheme.
     */
    private void saveColourSchemePreference(ColourScheme colourScheme) {
        logic.setGuiSettings(
                logic.getGuiSettings().withColourScheme(colourScheme));
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Configures the application keyboard accelerators.
     */
    private void setAccelerators() {
        setAccelerator(helpMenuItem, KeyCombination.valueOf("F1"));
    }

    /**
     * Sets the accelerator for a menu item.
     */
    private void setAccelerator(MenuItem menuItem,
                                KeyCombination keyCombination) {
        menuItem.setAccelerator(keyCombination);

        /*
         * TextInputControl can consume function-key events. Capture them here
         * so F1 continues to open the Help window when a text control is focused.
         */
        getRoot().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            boolean isTextControl =
                    event.getTarget() instanceof TextInputControl;

            if (isTextControl && keyCombination.match(event)) {
                menuItem.getOnAction().handle(new ActionEvent());
                event.consume();
            }
        });
    }

    /**
     * Fills all placeholders in the main window.
     */
    void fillInnerParts() {
        fillCustomerWorkspace();
        fillResultDisplay();
        configureFocusNavigation(fillCommandBox());
    }

    /**
     * Creates the customer list and details panels and connects their selection.
     */
    private void fillCustomerWorkspace() {
        ObservableList<Person> displayedCustomers =
                logic.getFilteredPersonList();

        personListPanel = new PersonListPanel(displayedCustomers);
        customerDetailsPanel = new CustomerDetailsPanel();

        personListPanelPlaceholder.getChildren()
                .setAll(personListPanel.getRoot());

        customerDetailsPlaceholder.getChildren()
                .setAll(customerDetailsPanel.getRoot());

        bindCustomerCount(displayedCustomers);

        personListPanel.selectedPersonProperty().addListener((observable, oldCustomer,
            selectedCustomer) ->
                updateDisplayedCustomer(selectedCustomer));

        updateDisplayedCustomer(personListPanel.getSelectedPerson());
    }

    /**
     * Keeps the displayed customer count synchronized with the filtered list.
     */
    private void bindCustomerCount(
            ObservableList<Person> displayedCustomers) {
        customerCountLabel.textProperty().bind(
                Bindings.createStringBinding(() -> formatCustomerCount(displayedCustomers.size()),
                    displayedCustomers));
    }

    /**
     * Formats the customer count with the correct singular or plural wording.
     */
    private String formatCustomerCount(int customerCount) {
        if (customerCount == 1) {
            return "1 customer";
        }

        return customerCount + " customers";
    }

    /**
     * Displays the selected customer or the empty details state.
     */
    private void updateDisplayedCustomer(Person selectedCustomer) {
        if (selectedCustomer == null) {
            customerDetailsPanel.clear();
            return;
        }

        customerDetailsPanel.displayCustomer(selectedCustomer);
    }

    /**
     * Creates the result display and shows any startup feedback.
     */
    private void fillResultDisplay() {
        resultDisplay = new ResultDisplay();

        resultDisplayPlaceholder.getChildren()
                .setAll(resultDisplay.getRoot());

        if (initialFeedback != null) {
            resultDisplay.setFeedbackToUser(initialFeedback);
        }
    }

    /**
     * Creates the command input.
     */
    private CommandBox fillCommandBox() {
        CommandBox commandBox =
                new CommandBox(this::executeCommand);

        commandBoxPlaceholder.getChildren()
                .setAll(commandBox.getRoot());

        return commandBox;
    }

    /**
     * Configures F6 keyboard navigation between the main interface areas.
     */
    private void configureFocusNavigation(CommandBox commandBox) {
        FocusNavigation navigation = new FocusNavigation(
                commandBox.getFocusTarget(),
                resultDisplay.getFocusTarget(),
                personListPanel.getFocusTarget());

        getRoot().addEventFilter(
                KeyEvent.KEY_PRESSED, navigation::handleKeyPressed);
    }

    /**
     * Sets the default window size and position.
     */
    private void setWindowDefaultSize(GuiSettings guiSettings) {
        primaryStage.setMinWidth(Math.min(primaryStage.getMinWidth(), guiSettings.getWindowWidth()));
        primaryStage.setMinHeight(Math.min(primaryStage.getMinHeight(), guiSettings.getWindowHeight()));
        primaryStage.setHeight(guiSettings.getWindowHeight());
        primaryStage.setWidth(guiSettings.getWindowWidth());

        if (guiSettings.getWindowCoordinates() != null) {
            primaryStage.setX(
                    guiSettings.getWindowCoordinates().getX());
            primaryStage.setY(
                    guiSettings.getWindowCoordinates().getY());
        }
    }

    /** Returns the monitor containing the saved window origin, or the primary monitor if it is unavailable. */
    private static Screen screenFor(Point coordinates) {
        if (coordinates != null) {
            var screens = Screen.getScreensForRectangle(coordinates.getX(), coordinates.getY(), 1, 1);
            if (!screens.isEmpty()) {
                return screens.get(0);
            }
        }
        return Screen.getPrimary();
    }

    /** Keeps the window and its resize edges within the monitor's usable area. */
    static GuiSettings fitToScreen(GuiSettings settings, Rectangle2D bounds) {
        double width = Math.min(settings.getWindowWidth(), Math.max(1, bounds.getWidth() - 2 * RESIZE_MARGIN));
        double height = Math.min(settings.getWindowHeight(), Math.max(1, bounds.getHeight() - 2 * RESIZE_MARGIN));
        Point coordinates = settings.getWindowCoordinates();
        if (coordinates != null) {
            int x = (int) Math.max(bounds.getMinX() + RESIZE_MARGIN,
                    Math.min(coordinates.getX(), bounds.getMaxX() - width - RESIZE_MARGIN));
            int y = (int) Math.max(bounds.getMinY() + RESIZE_MARGIN,
                    Math.min(coordinates.getY(), bounds.getMaxY() - height - RESIZE_MARGIN));
            coordinates = new Point(x, y);
        }
        return settings.withWindowGeometry(width, height, coordinates);
    }

    /**
     * Opens the Help window or focuses it if it is already open.
     */
    @FXML
    public void handleHelp() {
        if (!helpWindow.isShowing()) {
            helpWindow.show();
        } else {
            helpWindow.focus();
        }
    }

    /**
     * Shows the main window.
     */
    void show() {
        primaryStage.show();
    }

    /**
     * Closes the application.
     */
    @FXML
    private void handleExit() {
        GuiSettings guiSettings = logic.getGuiSettings().withWindowGeometry(
                primaryStage.getWidth(),
                primaryStage.getHeight(),
                new Point((int) primaryStage.getX(), (int) primaryStage.getY()));

        logic.setGuiSettings(fitToScreen(guiSettings,
                screenFor(guiSettings.getWindowCoordinates()).getVisualBounds()));

        helpWindow.hide();
        primaryStage.hide();
    }

    public PersonListPanel getPersonListPanel() {
        return personListPanel;
    }

    /**
     * Executes a command and displays its result.
     */
    private CommandResult executeCommand(String commandText)
            throws CommandException, ParseException {
        try {
            CommandResult commandResult = logic.execute(commandText);

            logger.info(
                    "Result: " + commandResult.getFeedbackToUser());

            resultDisplay.setFeedbackToUser(
                    commandResult.getFeedbackToUser());

            if (commandResult.isShowHelp()) {
                handleHelp();
            }

            if (commandResult.isExit()) {
                handleExit();
            }

            return commandResult;
        } catch (CommandException | ParseException e) {
            logger.info(
                    "An error occurred while executing command: "
                            + commandText);

            resultDisplay.setFeedbackToUser(e.getMessage());
            throw e;
        }
    }
}
