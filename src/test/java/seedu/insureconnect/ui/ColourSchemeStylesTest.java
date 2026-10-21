package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.commons.core.ColourScheme;

public class ColourSchemeStylesTest {

    @Test
    public void apply_lightWithDuplicateStylesheets_keepsOneLightStylesheetLast() {
        String lightStylesheet = ColourSchemeStyles.class.getResource("/view/LightTheme.css").toExternalForm();
        List<String> stylesheets = new ArrayList<>(List.of(lightStylesheet, "view/DarkTheme.css",
                lightStylesheet, "view/Extensions.css", lightStylesheet));

        ColourSchemeStyles.apply(stylesheets, ColourScheme.LIGHT);

        assertEquals(List.of("view/DarkTheme.css", "view/Extensions.css", lightStylesheet), stylesheets);
    }

    @Test
    public void apply_darkWithDuplicateStylesheets_removesAllLightStylesheets() {
        String lightStylesheet = ColourSchemeStyles.class.getResource("/view/LightTheme.css").toExternalForm();
        List<String> stylesheets = new ArrayList<>(List.of(lightStylesheet, "view/DarkTheme.css",
                lightStylesheet, "view/Extensions.css", lightStylesheet));

        ColourSchemeStyles.apply(stylesheets, ColourScheme.DARK);

        assertEquals(List.of("view/DarkTheme.css", "view/Extensions.css"), stylesheets);
    }

    @Test
    public void apply_switchRepeatedly_preservesComponentStylesWithoutDuplicates() {
        List<String> original = List.of("view/DarkTheme.css", "view/Extensions.css");
        List<String> stylesheets = new ArrayList<>(original);
        String lightStylesheet = ColourSchemeStyles.class.getResource("/view/LightTheme.css").toExternalForm();

        ColourSchemeStyles.apply(stylesheets, ColourScheme.LIGHT);
        ColourSchemeStyles.apply(stylesheets, ColourScheme.LIGHT);
        assertEquals(List.of(original.get(0), original.get(1), lightStylesheet), stylesheets);

        ColourSchemeStyles.apply(stylesheets, ColourScheme.DARK);
        ColourSchemeStyles.apply(stylesheets, ColourScheme.DARK);
        assertEquals(original, stylesheets);
    }
}
