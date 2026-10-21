package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import javafx.scene.control.Label;

public class StatusBarFooterTest extends GuiTest {

    @TempDir
    public Path directory;

    @Test
    public void constructor_relativeAndAbsolutePaths_displaysConfiguredLocation() throws Exception {
        runOnFxThread(() -> {
            assertLocation(Path.of("data", "customers.json"));
            assertLocation(directory.resolve("customers.json"));
        });
    }

    /**
     * Verifies that the footer uses the supplied storage path rather than a fixed default.
     */
    private void assertLocation(Path path) {
        StatusBarFooter footer = new StatusBarFooter(path);
        Label location = (Label) footer.getRoot().lookup("#saveLocationStatus");
        assertEquals(Path.of(".").resolve(path).toString(), location.getText());
    }
}
