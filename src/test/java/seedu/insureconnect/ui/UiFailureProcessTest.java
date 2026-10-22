package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.insureconnect.testutil.ProcessTestUtil;

/**
 * Checks fatal UI dialogs and exit status without terminating the test worker.
 */
public class UiFailureProcessTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void start_windowConstructionFails_showsErrorAndExits() throws Exception {
        assertFatalShutdown("constructor");
    }

    @Test
    public void start_contentInitializationFails_showsErrorAndExits() throws Exception {
        assertFatalShutdown("content");
    }

    /**
     * Confirms both the user-visible error and the operating-system exit status.
     */
    private void assertFatalShutdown(String phase) throws Exception {
        ProcessTestUtil.Result result = ProcessTestUtil.run(UiFailureProcessProbe.class,
                temporaryFolder, "ui-" + phase, phase);
        assertEquals(1, result.exitCode(), result.output());
        assertTrue(result.output().contains("FATAL_DIALOG_VERIFIED"), result.output());
    }
}
