package seedu.insureconnect.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static seedu.insureconnect.testutil.TypicalPersons.getTypicalInsureConnect;

import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.ReadOnlyInsureConnect;
import seedu.insureconnect.model.UserPrefs;

public class StorageManagerTest {

    @TempDir
    public Path testFolder;

    private StorageManager storageManager;

    @BeforeEach
    public void setUp() {
        JsonInsureConnectStorage insureConnectStorage = new JsonInsureConnectStorage(getTempFilePath("ab"));
        JsonUserPrefsStorage userPrefsStorage = new JsonUserPrefsStorage(getTempFilePath("prefs"));
        storageManager = new StorageManager(insureConnectStorage, userPrefsStorage);
    }

    private Path getTempFilePath(String fileName) {
        return testFolder.resolve(fileName);
    }

    @Test
    public void prefsReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonUserPrefsStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonUserPrefsStorageTest} class.
         */
        UserPrefs original = new UserPrefs();
        original.setGuiSettings(new GuiSettings(300, 600, 4, 6));
        storageManager.saveUserPrefs(original);
        UserPrefs retrieved = storageManager.readUserPrefs().get();
        assertEquals(original, retrieved);
    }

    @Test
    public void insureConnectReadSave() throws Exception {
        /*
         * Note: This is an integration test that verifies the StorageManager is properly wired to the
         * {@link JsonInsureConnectStorage} class.
         * More extensive testing of UserPref saving/reading is done in {@link JsonInsureConnectStorageTest} class.
         */
        InsureConnect original = getTypicalInsureConnect();
        storageManager.saveInsureConnect(original);
        ReadOnlyInsureConnect retrieved = storageManager.readInsureConnect().get();
        assertEquals(original, new InsureConnect(retrieved));
    }

    @Test
    public void getInsureConnectFilePath() {
        assertNotNull(storageManager.getInsureConnectFilePath());
    }

}
