package seedu.insureconnect;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.storage.JsonInsureConnectStorage;
import seedu.insureconnect.storage.JsonUserPrefsStorage;
import seedu.insureconnect.testutil.ProcessTestUtil;

/**
 * Verifies the real entry point, default file locations and normal process shutdown.
 */
public class MainProcessTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void main_existingCustomerFile_opensWindowAndSavesPreferencesOnExit() throws Exception {
        Path dataFile = temporaryFolder.resolve("data/addressbook.json");
        InsureConnect data = new InsureConnect();
        data.addPerson(ALICE);
        JsonInsureConnectStorage storage = new JsonInsureConnectStorage(dataFile);
        storage.saveInsureConnect(data);
        ProcessTestUtil.Result result = ProcessTestUtil.run(MainProcessProbe.class, temporaryFolder, "main");
        assertEquals(0, result.exitCode(), result.output());
        assertTrue(result.output().contains("STARTUP_CUSTOMERS=1"), result.output());
        assertEquals(data, storage.readInsureConnect().orElseThrow());
        assertTrue(new JsonUserPrefsStorage(temporaryFolder.resolve("preferences.json"))
                .readUserPrefs().isPresent());
    }
}
