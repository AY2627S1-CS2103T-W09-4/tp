package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.VALID_REMARK_AMY;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RemarkCommand;
import seedu.address.model.person.Remark;

public class RemarkCommandParserTest {

    private final RemarkCommandParser parser = new RemarkCommandParser();

    @Test
    public void parse_nullArgs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_validArgs_returnsRemarkCommand() {
        assertParseSuccess(parser, "1 r/" + VALID_REMARK_AMY,
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY)));
        assertParseSuccess(parser, "  1  r/  " + VALID_REMARK_AMY + "  ",
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark(VALID_REMARK_AMY)));
    }

    @Test
    public void parse_emptyRemark_returnsRemarkCommand() {
        assertParseSuccess(parser, "1 r/", new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")));
        assertParseSuccess(parser, "1", new RemarkCommand(INDEX_FIRST_PERSON, new Remark("")));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, RemarkCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expectedMessage);
        assertParseFailure(parser, "1 " + VALID_REMARK_AMY, expectedMessage);
        assertParseFailure(parser, " r/" + VALID_REMARK_AMY, expectedMessage);
        assertParseFailure(parser, "0 r/" + VALID_REMARK_AMY, expectedMessage);
        assertParseFailure(parser, "-1 r/" + VALID_REMARK_AMY, expectedMessage);
        assertParseFailure(parser, "a r/" + VALID_REMARK_AMY, expectedMessage);
        assertParseFailure(parser, "1.5 r/" + VALID_REMARK_AMY, expectedMessage);
        assertParseFailure(parser, "2147483648 r/" + VALID_REMARK_AMY, expectedMessage);
    }

    @Test
    public void parse_duplicateRemarks_usesLastValue() {
        assertParseSuccess(parser, "1 r/First r/Second",
                new RemarkCommand(INDEX_FIRST_PERSON, new Remark("Second")));
    }
}
