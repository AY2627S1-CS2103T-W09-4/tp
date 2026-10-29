package seedu.insureconnect.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.commands.ResizeCommand;
import seedu.insureconnect.logic.commands.WindowResize;
import seedu.insureconnect.logic.commands.WindowResize.Preset;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.ModelManager;

public class ResizeCommandParserTest {
    private final InsureConnectParser parser = new InsureConnectParser();

    @Test
    public void presetAndCustom_validInputs() throws Exception {
        assertEquals(WindowResize.preset(Preset.MIN), resizeOf("resize p/min"));
        assertEquals(WindowResize.preset(Preset.MED), resizeOf("resize p/med"));
        assertEquals(WindowResize.preset(Preset.MAX), resizeOf("resize p/max"));
        assertEquals(WindowResize.custom(880, 600), resizeOf("resize s/880 600"));
    }

    @Test
    public void invalidInputs_rejected() {
        for (String input : new String[] {"resize", "resize p/large", "resize p/min s/880 600",
            "resize s/880", "resize s/880 600 700",
            "resize s/-880 600", "resize s/999999999999999999 600"}) {
            assertThrows(ParseException.class, () -> parser.parseCommand(input), input);
        }
    }

    @Test
    public void customBelowMinimum_reportsSizeConstraint() {
        for (String input : new String[] {"resize s/879 600", "resize s/880 599"}) {
            ParseException error = assertThrows(ParseException.class, () -> parser.parseCommand(input));
            assertEquals(ResizeCommandParser.MESSAGE_SIZE_TOO_SMALL, error.getMessage());
        }
    }

    private WindowResize resizeOf(String input) throws Exception {
        return ((ResizeCommand) parser.parseCommand(input)).execute(new ModelManager()).getWindowResize();
    }
}
