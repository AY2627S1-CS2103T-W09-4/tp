package seedu.insureconnect.commons.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class GuiSettingsTest {
    @Test
    public void withColourScheme_preservesWindowSettings() {
        GuiSettings original = new GuiSettings(800, 700, 40, 50);
        GuiSettings updated = original.withColourScheme(ColourScheme.LIGHT);
        assertEquals(original.getWindowWidth(), updated.getWindowWidth());
        assertEquals(original.getWindowHeight(), updated.getWindowHeight());
        assertEquals(original.getWindowCoordinates(), updated.getWindowCoordinates());
        assertEquals(ColourScheme.LIGHT, updated.getColourScheme());
        assertEquals(ColourScheme.DARK, original.getColourScheme());
        assertNotEquals(original, updated);
        assertEquals(original, updated.withColourScheme(ColourScheme.DARK));
        assertEquals(original.hashCode(), updated.withColourScheme(ColourScheme.DARK).hashCode());
    }

    @Test
    public void withColourScheme_defaultSettings_preservesAutomaticPosition() {
        GuiSettings updated = new GuiSettings().withColourScheme(ColourScheme.LIGHT);
        assertNull(updated.getWindowCoordinates());
    }

    @Test
    public void withColourScheme_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GuiSettings().withColourScheme(null));
    }

    @Test
    public void toStringMethod() {
        GuiSettings guiSettings = new GuiSettings();
        String expected = GuiSettings.class.getCanonicalName() + "{windowWidth=" + guiSettings.getWindowWidth()
                + ", windowHeight=" + guiSettings.getWindowHeight() + ", windowCoordinates="
                + guiSettings.getWindowCoordinates() + ", colourScheme=" + guiSettings.getColourScheme() + "}";
        assertEquals(expected, guiSettings.toString());
    }
}
