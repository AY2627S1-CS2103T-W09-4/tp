package seedu.insureconnect;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.stage.Stage;
import seedu.insureconnect.commons.core.LogsCenter;
import seedu.insureconnect.commons.exceptions.DataLoadingException;
import seedu.insureconnect.commons.util.StringUtil;
import seedu.insureconnect.logic.Logic;
import seedu.insureconnect.logic.LogicManager;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.ReadOnlyInsureConnect;
import seedu.insureconnect.model.ReadOnlyUserPrefs;
import seedu.insureconnect.model.UserPrefs;
import seedu.insureconnect.model.util.SampleDataUtil;
import seedu.insureconnect.storage.JsonInsureConnectStorage;
import seedu.insureconnect.storage.JsonUserPrefsStorage;
import seedu.insureconnect.storage.Storage;
import seedu.insureconnect.storage.StorageManager;
import seedu.insureconnect.ui.Ui;
import seedu.insureconnect.ui.UiManager;

/**
 * Runs the application.
 */
public class MainApp extends Application {

    public static final String VERSION = "V0.5.1";

    private static final Logger logger = LogsCenter.getLogger(MainApp.class);
    private static final Path USER_PREFS_FILE_PATH = Paths.get("preferences.json");
    private static final Path INSURE_CONNECT_FILE_PATH = Paths.get("data", "addressbook.json");

    protected Ui ui;
    protected Logic logic;
    protected Storage storage;
    protected Model model;

    @Override
    public void init() throws Exception {
        logger.info("=============================[ Initializing InsureConnect ]===========================");
        super.init();

        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(USER_PREFS_FILE_PATH);
        UserPrefs userPrefs = initPrefs(userPrefsStorage);
        JsonInsureConnectStorage insureConnectStorage = new JsonInsureConnectStorage(INSURE_CONNECT_FILE_PATH);
        storage = new StorageManager(insureConnectStorage, userPrefsStorage);

        model = initModelManager(storage, userPrefs);

        logic = new LogicManager(model, storage);

        ui = new UiManager(logic, storage.getInsureConnectFilePath());
    }

    /**
     * Returns a {@code ModelManager} with the data from {@code storage}'s address book and {@code userPrefs}. <br>
     * The data from the sample address book will be used instead if {@code storage}'s address book is not found,
     * or an empty address book will be used instead if errors occur when reading {@code storage}'s address book.
     */
    private Model initModelManager(Storage storage, ReadOnlyUserPrefs userPrefs) {
        logger.info("Using data file : " + storage.getInsureConnectFilePath());

        Optional<ReadOnlyInsureConnect> insureConnectOptional;
        ReadOnlyInsureConnect initialData;
        try {
            insureConnectOptional = storage.readInsureConnect();
            if (insureConnectOptional.isEmpty()) {
                logger.info("Creating a new data file " + storage.getInsureConnectFilePath()
                        + " populated with a sample InsureConnect.");
            }
            initialData = insureConnectOptional.orElseGet(SampleDataUtil::getSampleInsureConnect);
        } catch (DataLoadingException e) {
            logger.warning("Data file at " + storage.getInsureConnectFilePath() + " could not be loaded."
                    + " Will be starting with an empty InsureConnect.");
            initialData = new InsureConnect();
        }

        return new ModelManager(initialData, userPrefs);
    }

    /**
     * Returns a {@code UserPrefs} using the file at {@code storage}'s user prefs file path,
     * or a new {@code UserPrefs} with default configuration if errors occur when
     * reading from the file.
     */
    protected UserPrefs initPrefs(JsonUserPrefsStorage storage) {
        Path prefsFilePath = storage.getUserPrefsFilePath();
        logger.info("Using preference file : " + prefsFilePath);

        UserPrefs initializedPrefs;
        try {
            Optional<UserPrefs> prefsOptional = storage.readUserPrefs();
            if (prefsOptional.isEmpty()) {
                logger.info("Creating new preference file " + prefsFilePath);
            }
            initializedPrefs = prefsOptional.orElse(new UserPrefs());
        } catch (DataLoadingException e) {
            logger.warning("Preference file at " + prefsFilePath + " could not be loaded."
                    + " Using default preferences.");
            initializedPrefs = new UserPrefs();
        }

        //Update prefs file in case it was missing to begin with or there are new/unused fields
        try {
            storage.saveUserPrefs(initializedPrefs);
        } catch (IOException e) {
            logger.warning("Failed to save preference file : " + StringUtil.getDetails(e));
        }

        return initializedPrefs;
    }

    @Override
    public void start(Stage primaryStage) {
        logger.info("Starting InsureConnect " + MainApp.VERSION);
        ui.start(primaryStage);
    }

    @Override
    public void stop() {
        logger.info("============================ [ Stopping InsureConnect ] =============================");
        try {
            storage.saveUserPrefs(model.getUserPrefs());
        } catch (IOException e) {
            logger.severe("Failed to save preferences " + StringUtil.getDetails(e));
        }
    }
}
