package seedu.insureconnect.commons.core;

import static java.util.Objects.requireNonNull;

import java.awt.Point;
import java.io.Serializable;
import java.util.Objects;

import seedu.insureconnect.commons.util.ToStringBuilder;

/**
 * A Serializable class that contains the GUI settings.
 * Guarantees: immutable.
 */
public class GuiSettings implements Serializable {

    private static final double DEFAULT_HEIGHT = 600;
    private static final double DEFAULT_WIDTH = 740;

    private final double windowWidth;
    private final double windowHeight;
    private final Point windowCoordinates;
    private final ColourScheme colourScheme;

    /**
     * Constructs a {@code GuiSettings} with the default height, width and position.
     */
    public GuiSettings() {
        windowWidth = DEFAULT_WIDTH;
        windowHeight = DEFAULT_HEIGHT;
        windowCoordinates = null; // null represent no coordinates
        colourScheme = ColourScheme.DARK;
    }

    /**
     * Constructs a {@code GuiSettings} with the specified height, width and position.
     */
    public GuiSettings(double windowWidth, double windowHeight, int xPosition, int yPosition) {
        this(windowWidth, windowHeight, new Point(xPosition, yPosition), ColourScheme.DARK);
    }

    /**
     * Constructs settings while preserving an optional window position.
     */
    private GuiSettings(double windowWidth, double windowHeight, Point windowCoordinates, ColourScheme colourScheme) {
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        this.windowCoordinates = windowCoordinates == null ? null : new Point(windowCoordinates);
        this.colourScheme = requireNonNull(colourScheme);
    }

    public double getWindowWidth() {
        return windowWidth;
    }

    public double getWindowHeight() {
        return windowHeight;
    }

    public Point getWindowCoordinates() {
        return windowCoordinates != null ? new Point(windowCoordinates) : null;
    }

    /**
     * Returns the selected scheme, defaulting to dark for older preference files.
     */
    public ColourScheme getColourScheme() {
        return colourScheme == null ? ColourScheme.DARK : colourScheme;
    }

    /**
     * Returns settings with the given scheme, preserving window size and position.
     */
    public GuiSettings withColourScheme(ColourScheme colourScheme) {
        return new GuiSettings(windowWidth, windowHeight, windowCoordinates, colourScheme);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof GuiSettings otherGuiSettings)) {
            return false;
        }

        return windowWidth == otherGuiSettings.windowWidth
                && windowHeight == otherGuiSettings.windowHeight
                && Objects.equals(windowCoordinates, otherGuiSettings.windowCoordinates)
                && getColourScheme() == otherGuiSettings.getColourScheme();
    }

    @Override
    public int hashCode() {
        return Objects.hash(windowWidth, windowHeight, windowCoordinates, getColourScheme());
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("windowWidth", windowWidth)
                .add("windowHeight", windowHeight)
                .add("windowCoordinates", windowCoordinates)
                .add("colourScheme", getColourScheme())
                .toString();
    }
}
