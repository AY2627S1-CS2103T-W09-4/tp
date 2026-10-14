package seedu.insureconnect.logic.parser;

import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.commands.FindCommand;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.PhoneStartsWithPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // no leading and trailing whitespaces
        FindCommand expectedFindCommand =
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob")));
        assertParseSuccess(parser, "Alice Bob", expectedFindCommand);

        // multiple whitespaces between keywords
        assertParseSuccess(parser, " \n Alice \n \t Bob  \t", expectedFindCommand);
    }

    /**
     * Phone mode accepts partial or complete numbers and surrounding whitespace.
     */
    @Test
    public void parse_phonePrefix_returnsPhoneSearch() {
        assertParseSuccess(parser, "p/9", new FindCommand(new PhoneStartsWithPredicate("9")));
        assertParseSuccess(parser, "p/98765432", new FindCommand(new PhoneStartsWithPredicate("98765432")));
        assertParseSuccess(parser, " \t p/ 0012 \n", new FindCommand(new PhoneStartsWithPredicate("0012")));
    }

    /**
     * Empty values, formatting symbols, and multiple search criteria are rejected.
     */
    @Test
    public void parse_invalidPhoneQuery_throwsParseException() {
        for (String query : List.of("p/", "p/  ", "p/abc", "p/+6598765432", "p/987-654", "p/98 76",
                "p/9876 Alice", "p/9876 p/1234", "p/9876 n/Alice", "p/１２３")) {
            assertParseFailure(parser, query, PhoneStartsWithPredicate.MESSAGE_CONSTRAINTS);
        }
    }

    /**
     * A phone marker cannot silently become a name keyword when preceded by a name.
     */
    @Test
    public void parse_mixedNameAndPhone_throwsParseException() {
        assertParseFailure(parser, "Alice p/9876",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    /**
     * Digits without the explicit phone marker retain the existing name-search interpretation.
     */
    @Test
    public void parse_numericNameKeyword_returnsNameSearch() {
        assertParseSuccess(parser, "9876", new FindCommand(new NameContainsKeywordsPredicate(List.of("9876"))));
    }

}
