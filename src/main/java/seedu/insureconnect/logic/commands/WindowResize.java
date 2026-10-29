package seedu.insureconnect.logic.commands;

/** A requested main-window size, expressed as a preset or explicit dimensions. */
public record WindowResize(Preset preset, int width, int height) {
    /** Supported resize modes. */
    public enum Preset { MIN, MED, MAX, CUSTOM }

    public static WindowResize preset(Preset preset) {
        return new WindowResize(preset, 0, 0);
    }

    public static WindowResize custom(int width, int height) {
        return new WindowResize(Preset.CUSTOM, width, height);
    }
}
