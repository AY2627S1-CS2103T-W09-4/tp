package seedu.insureconnect.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.insureconnect.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.commons.exceptions.IllegalValueException;
import seedu.insureconnect.commons.util.JsonUtil;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.testutil.TypicalPersons;

public class JsonSerializableInsureConnectTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableInsureConnectTest");
    private static final Path TYPICAL_PERSONS_FILE = TEST_DATA_FOLDER.resolve("typicalPersonsInsureConnect.json");
    private static final Path INVALID_PERSON_FILE = TEST_DATA_FOLDER.resolve("invalidPersonInsureConnect.json");
    private static final Path DUPLICATE_PERSON_FILE = TEST_DATA_FOLDER.resolve("duplicatePersonInsureConnect.json");

    @Test
    public void toModelType_typicalPersonsFile_success() throws Exception {
        JsonSerializableInsureConnect dataFromFile = JsonUtil.readJsonFile(TYPICAL_PERSONS_FILE,
                JsonSerializableInsureConnect.class).get();
        InsureConnect insureConnectFromFile = dataFromFile.toModelType();
        InsureConnect typicalPersonsInsureConnect = TypicalPersons.getTypicalInsureConnect();
        assertEquals(insureConnectFromFile, typicalPersonsInsureConnect);
    }

    @Test
    public void toModelType_invalidPersonFile_throwsIllegalValueException() throws Exception {
        JsonSerializableInsureConnect dataFromFile = JsonUtil.readJsonFile(INVALID_PERSON_FILE,
                JsonSerializableInsureConnect.class).get();
        assertThrows(IllegalValueException.class, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicatePersons_throwsIllegalValueException() throws Exception {
        JsonSerializableInsureConnect dataFromFile = JsonUtil.readJsonFile(DUPLICATE_PERSON_FILE,
                JsonSerializableInsureConnect.class).get();
        assertThrows(IllegalValueException.class, JsonSerializableInsureConnect.MESSAGE_DUPLICATE_PERSON,
                dataFromFile::toModelType);
    }

}
