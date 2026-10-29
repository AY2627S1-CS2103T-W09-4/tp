package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import javafx.scene.layout.StackPane;

public class SelectableTextTest extends GuiTest {

    @Test
    public void constructor_configuresLabelLikeBehaviour() throws Exception {
        runOnFxThread(() -> {
            SelectableText selectableText = new SelectableText();

            assertFalse(selectableText.isEditable());
            assertTrue(selectableText.isFocusTraversable());
            assertTrue(selectableText.isWrapText());
            assertEquals(1, selectableText.getPrefRowCount());
            assertEquals(SelectableText.USE_PREF_SIZE, selectableText.getMinHeight());
            assertEquals(SelectableText.USE_PREF_SIZE, selectableText.getMaxHeight());

            selectableText.setText("Updated text");
            assertTrue(selectableText.isNeedsLayout());
        });
    }

    @Test
    public void computePrefHeight_emptyTextOrNonPositiveWidth_returnsSingleLineHeight() throws Exception {
        runOnFxThread(() -> {
            SelectableText selectableText = new SelectableText();
            show(new StackPane(selectableText));

            double emptyTextHeight = selectableText.computePrefHeight(200);
            selectableText.setText("Some text");
            double zeroWidthHeight = selectableText.computePrefHeight(0);

            assertTrue(emptyTextHeight > 0);
            assertTrue(zeroWidthHeight > 0);
        });
    }

    @Test
    public void computePrefHeight_longText_growsWhenAvailableWidthNarrows() throws Exception {
        runOnFxThread(() -> {
            SelectableText selectableText = new SelectableText();
            selectableText.setText("A long customer detail that wraps across several lines when space is limited");
            show(new StackPane(selectableText));

            double wideHeight = selectableText.computePrefHeight(600);
            double narrowHeight = selectableText.computePrefHeight(120);

            assertTrue(narrowHeight > wideHeight);
        });
    }
}
