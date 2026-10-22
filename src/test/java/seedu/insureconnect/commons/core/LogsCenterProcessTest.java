package seedu.insureconnect.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.insureconnect.testutil.ProcessTestUtil;

/**
 * Verifies that file logging failures preserve console diagnostics and do not prevent startup.
 */
public class LogsCenterProcessTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void initialize_unwritableLogFile_retainsConsoleLoggingAndExceptionDetails() throws Exception {
        ProcessTestUtil.Result result = ProcessTestUtil.run(LoggingFailureProcessProbe.class,
                temporaryFolder, "logging-failure");
        assertEquals(0, result.exitCode(), result.output());
        assertTrue(result.output().contains("Error adding file handler for logger."), result.output());
        assertTrue(result.output().contains("java.io."), result.output());
        assertTrue(result.output().contains("CONSOLE_FALLBACK_VERIFIED"), result.output());
    }
}
