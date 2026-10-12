package seedu.insureconnect.logic.parser;

import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.commands.ListCommand;
import seedu.insureconnect.logic.commands.ListCommand.SortOrder;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_validArgs_returnsListCommand() {
        assertParseSuccess(parser, "  ", new ListCommand());
        assertParseSuccess(parser, " n/ ", new ListCommand(SortOrder.NAME));
        assertParseSuccess(parser, " c/ ", new ListCommand(SortOrder.CREATED_AT));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " 3", expectedMessage);
        assertParseFailure(parser, " n/ c/", expectedMessage);
        assertParseFailure(parser, " n/Alice", expectedMessage);
        assertParseFailure(parser, " c/oldest", expectedMessage);
    }
}
