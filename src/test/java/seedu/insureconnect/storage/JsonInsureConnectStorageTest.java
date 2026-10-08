package seedu.insureconnect.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.Assert.assertThrows;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;
import static seedu.insureconnect.testutil.TypicalPersons.HOON;
import static seedu.insureconnect.testutil.TypicalPersons.IDA;
import static seedu.insureconnect.testutil.TypicalPersons.getTypicalInsureConnect;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.insureconnect.commons.exceptions.DataLoadingException;
import seedu.insureconnect.commons.util.FileUtil;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.ReadOnlyInsureConnect;

public class JsonInsureConnectStorageTest {
    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonInsureConnectStorageTest");

    @TempDir
    public Path testFolder;

    @Test
    public void readInsureConnect_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> readInsureConnect(null));
    }

    private java.util.Optional<ReadOnlyInsureConnect> readInsureConnect(String filePath) throws Exception {
        return new JsonInsureConnectStorage(Paths.get(filePath))
                .readInsureConnect(addToTestDataPathIfNotNull(filePath));
    }

    private Path addToTestDataPathIfNotNull(String prefsFileInTestDataFolder) {
        return prefsFileInTestDataFolder != null
                ? TEST_DATA_FOLDER.resolve(prefsFileInTestDataFolder)
                : null;
    }

    @Test
    public void read_missingFile_emptyResult() throws Exception {
        assertFalse(readInsureConnect("NonExistentFile.json").isPresent());
    }

    @Test
    public void read_notJsonFormat_exceptionThrown() {
        assertThrows(DataLoadingException.class, () -> readInsureConnect("notJsonFormatInsureConnect.json"));
    }

    @Test
    public void readInsureConnect_invalidPersonInsureConnect_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readInsureConnect("invalidPersonInsureConnect.json"));
    }

    @Test
    public void readInsureConnect_invalidAndValidPersonInsureConnect_throwDataLoadingException() {
        assertThrows(DataLoadingException.class, () -> readInsureConnect("invalidAndValidPersonInsureConnect.json"));
    }

    @Test
    public void readAndSaveInsureConnect_allInOrder_success() throws Exception {
        Path filePath = testFolder.resolve("TempInsureConnect.json");
        InsureConnect original = getTypicalInsureConnect();
        JsonInsureConnectStorage jsonInsureConnectStorage = new JsonInsureConnectStorage(filePath);

        // Save in new file and read back
        jsonInsureConnectStorage.saveInsureConnect(original, filePath);
        ReadOnlyInsureConnect readBack = jsonInsureConnectStorage.readInsureConnect(filePath).get();
        assertEquals(original, new InsureConnect(readBack));

        // Modify data, overwrite existing file, and read back
        original.addPerson(HOON);
        original.removePerson(ALICE);
        jsonInsureConnectStorage.saveInsureConnect(original, filePath);
        readBack = jsonInsureConnectStorage.readInsureConnect(filePath).get();
        assertEquals(original, new InsureConnect(readBack));

        // Save and read without specifying file path
        original.addPerson(IDA);
        jsonInsureConnectStorage.saveInsureConnect(original); // file path not specified
        readBack = jsonInsureConnectStorage.readInsureConnect().get(); // file path not specified
        assertEquals(original, new InsureConnect(readBack));

    }

    @Test
    public void saveInsureConnect_nullInsureConnect_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveInsureConnect(null, "SomeFile.json"));
    }

    /**
     * Saves {@code insureConnect} at the specified {@code filePath}.
     */
    private void saveInsureConnect(ReadOnlyInsureConnect insureConnect, String filePath) {
        try {
            new JsonInsureConnectStorage(Paths.get(filePath))
                    .saveInsureConnect(insureConnect, addToTestDataPathIfNotNull(filePath));
        } catch (IOException ioe) {
            throw new AssertionError("There should not be an error writing to the file.", ioe);
        }
    }

    @Test
    public void saveInsureConnect_nullFilePath_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> saveInsureConnect(new InsureConnect(), null));
    }

    @Test
    public void saveInsureConnect_unreadableFile_throwsIoException() throws Exception {
        Path filePath = testFolder.resolve("unreadable.json");
        FileUtil.writeToFile(filePath, "invalid json");
        JsonInsureConnectStorage jsonInsureConnectStorage = new JsonInsureConnectStorage(filePath);

        // First attempt to read should fail and record it as unreadable
        assertThrows(DataLoadingException.class, () -> jsonInsureConnectStorage.readInsureConnect(filePath));
        assertTrue(jsonInsureConnectStorage.isUnreadableFile(filePath));

        // Attempt to save to this file should throw IOException
        assertThrows(IOException.class, () -> jsonInsureConnectStorage.saveInsureConnect(
                new InsureConnect(), filePath));
    }

    @Test
    public void readInsureConnect_validFileAfterCorrupted_clearsUnreadableStatus() throws Exception {
        Path filePath = testFolder.resolve("unreadableThenValid.json");
        FileUtil.writeToFile(filePath, "invalid json");
        JsonInsureConnectStorage jsonInsureConnectStorage = new JsonInsureConnectStorage(filePath);

        assertThrows(DataLoadingException.class, () -> jsonInsureConnectStorage.readInsureConnect(filePath));
        assertTrue(jsonInsureConnectStorage.isUnreadableFile(filePath));

        // Now write valid json
        FileUtil.writeToFile(filePath, "{\n  \"persons\" : [ ]\n}");

        // Read again should succeed and remove unreadable status
        jsonInsureConnectStorage.readInsureConnect(filePath);
        assertFalse(jsonInsureConnectStorage.isUnreadableFile(filePath));
    }
}
