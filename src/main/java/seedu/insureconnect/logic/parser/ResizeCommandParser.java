package seedu.insureconnect.logic.parser;

import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.insureconnect.logic.commands.ResizeCommand;
import seedu.insureconnect.logic.commands.WindowResize;
import seedu.insureconnect.logic.commands.WindowResize.Preset;
import seedu.insureconnect.logic.parser.exceptions.ParseException;

/** Parses an exclusive preset or custom window-size argument. */
public class ResizeCommandParser implements Parser<ResizeCommand> {
    public static final String MESSAGE_SIZE_TOO_SMALL = "Window size must be at least 880 by 600 pixels.";
    private static final Pattern PRESET = Pattern.compile("\\s+p/(min|med|max)\\s*");
    private static final Pattern CUSTOM = Pattern.compile("\\s+s/(\\d+)\\s+(\\d+)\\s*");

    @Override
    public ResizeCommand parse(String args) throws ParseException {
        Matcher preset = PRESET.matcher(args);
        if (preset.matches()) {
            return new ResizeCommand(WindowResize.preset(Preset.valueOf(preset.group(1).toUpperCase(Locale.ROOT))));
        }

        Matcher custom = CUSTOM.matcher(args);
        if (custom.matches()) {
            try {
                int width = Integer.parseInt(custom.group(1));
                int height = Integer.parseInt(custom.group(2));
                if (width < 880 || height < 600) {
                    throw new ParseException(MESSAGE_SIZE_TOO_SMALL);
                }
                return new ResizeCommand(WindowResize.custom(width, height));
            } catch (NumberFormatException ignored) {
                // Oversized integer input is invalid command syntax.
            }
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ResizeCommand.MESSAGE_USAGE));
    }
}
