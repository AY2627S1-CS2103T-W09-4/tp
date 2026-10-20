package seedu.insureconnect.ui;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.insureconnect.commons.core.ColourScheme;

/**
 * Applies color palettes without replacing component-specific stylesheets.
 */
public final class ColourSchemeStyles {

    private static final String LIGHT_STYLESHEET = ColourSchemeStyles.class
            .getResource("/view/LightTheme.css").toExternalForm();

    private ColourSchemeStyles() {
    }

    /**
     * Adds or removes the light palette while preserving the dark base styles.
     */
    public static void apply(List<String> stylesheets, ColourScheme colourScheme) {
        requireNonNull(stylesheets);
        requireNonNull(colourScheme);
        stylesheets.remove(LIGHT_STYLESHEET);
        if (colourScheme == ColourScheme.LIGHT) {
            stylesheets.add(LIGHT_STYLESHEET);
        }
    }
}
