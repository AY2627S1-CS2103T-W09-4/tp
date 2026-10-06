package seedu.insureconnect.storage;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.insureconnect.commons.core.LogsCenter;
import seedu.insureconnect.commons.exceptions.DataLoadingException;
import seedu.insureconnect.commons.exceptions.IllegalValueException;
import seedu.insureconnect.commons.util.FileUtil;
import seedu.insureconnect.commons.util.JsonUtil;
import seedu.insureconnect.model.ReadOnlyInsureConnect;

/**
 * A class to access InsureConnect data stored as a JSON file on the hard disk.
 */
public class JsonInsureConnectStorage {

    private static final Logger logger = LogsCenter.getLogger(JsonInsureConnectStorage.class);

    private Path filePath;

    public JsonInsureConnectStorage(Path filePath) {
        this.filePath = filePath;
    }

    public Path getInsureConnectFilePath() {
        return filePath;
    }

    /**
     * Returns InsureConnect data as a {@link ReadOnlyInsureConnect}.
     * Returns {@code Optional.empty()} if storage file is not found.
     *
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyInsureConnect> readInsureConnect() throws DataLoadingException {
        return readInsureConnect(filePath);
    }

    /**
     * Similar to {@link #readInsureConnect()}.
     *
     * @param filePath location of the data. Cannot be null.
     * @throws DataLoadingException if loading the data from storage failed.
     */
    public Optional<ReadOnlyInsureConnect> readInsureConnect(Path filePath) throws DataLoadingException {
        requireNonNull(filePath);

        Optional<JsonSerializableInsureConnect> jsonInsureConnect = JsonUtil.readJsonFile(
                filePath, JsonSerializableInsureConnect.class);
        if (!jsonInsureConnect.isPresent()) {
            return Optional.empty();
        }

        try {
            return Optional.of(jsonInsureConnect.get().toModelType());
        } catch (IllegalValueException ive) {
            logger.info("Illegal values found in " + filePath + ": " + ive.getMessage());
            throw new DataLoadingException(ive);
        }
    }

    /**
     * Saves the given {@link ReadOnlyInsureConnect} to the storage.
     * @param insureConnect cannot be null.
     * @throws IOException if there was any problem writing to the file.
     */
    public void saveInsureConnect(ReadOnlyInsureConnect insureConnect) throws IOException {
        saveInsureConnect(insureConnect, filePath);
    }

    /**
     * Similar to {@link #saveInsureConnect(ReadOnlyInsureConnect)}.
     *
     * @param filePath location of the data. Cannot be null.
     */
    public void saveInsureConnect(ReadOnlyInsureConnect insureConnect, Path filePath) throws IOException {
        requireNonNull(insureConnect);
        requireNonNull(filePath);

        FileUtil.createIfMissing(filePath);
        JsonUtil.saveJsonFile(new JsonSerializableInsureConnect(insureConnect), filePath);
    }

}
