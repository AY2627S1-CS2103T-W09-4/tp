package seedu.insureconnect.commons.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;

/**
 * Exercises logger initialization failure in a child JVM with an isolated working directory.
 */
public final class LoggingFailureProcessProbe {
    private LoggingFailureProcessProbe() {
    }

    /**
     * Blocks the log file with a directory so the failure does not depend on filesystem permissions.
     */
    public static void main(String[] args) throws Exception {
        Files.createDirectory(Path.of("insureconnect.log.0"));
        Logger logger = LogsCenter.getLogger("startup-probe");
        logger.warning("CONSOLE_FALLBACK_VERIFIED");
    }
}
