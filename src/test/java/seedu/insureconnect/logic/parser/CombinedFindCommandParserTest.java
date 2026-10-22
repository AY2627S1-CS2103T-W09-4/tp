package seedu.insureconnect.logic.parser;

import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.insureconnect.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.logic.commands.FindCommand;
import seedu.insureconnect.model.person.AllCriteriaPredicate;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.PhoneStartsWithPredicate;
import seedu.insureconnect.model.person.PolicyNumberMatchesPredicate;
import seedu.insureconnect.model.tag.Tag;

/**
 * Tests explicit find criteria, ordering, validation and compatibility with legacy searches.
 */
public class CombinedFindCommandParserTest {
    private final FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_combinedCriteria_acceptsAnyOrderAndWhitespace() {
        FindCommand expected = new FindCommand(new AllCriteriaPredicate(List.of(
                new NameContainsKeywordsPredicate(List.of("Alice", "Bob")),
                new PhoneStartsWithPredicate("912"), new PolicyNumberMatchesPredicate("LIFE"))));
        for (String query : List.of("n/Alice Bob p/912 t/LIFE", "t/LIFE p/912 n/Alice Bob",
                "p/912 n/Alice Bob t/LIFE", " \n n/ Alice\tBob\tp/912\nt/LIFE  ")) {
            assertParseSuccess(parser, query, expected);
        }
    }

    @Test
    public void parse_singleCriteria_reusesExistingMatchingRules() {
        assertParseSuccess(parser, "n/Alice Bob",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Alice", "Bob"))));
        assertParseSuccess(parser, "t/LIFE", new FindCommand(new PolicyNumberMatchesPredicate("LIFE")));
        assertParseSuccess(parser, "n/Mohd !a/l",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("Mohd", "a/l"))));
        assertParseSuccess(parser, "n/!p/123",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("p/123"))));
    }

    @Test
    public void parse_legacyLiteralKeywords_preservesPunctuation() {
        assertParseSuccess(parser, "!Alice a/l",
                new FindCommand(new NameContainsKeywordsPredicate(List.of("!Alice", "a/l"))));
    }

    @Test
    public void parse_missingValues_reportsInvalidValueOrUsage() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        for (String query : List.of("n/", "n/ \t", "n/ p/912", "p/912 n/")) {
            assertParseFailure(parser, query, usage);
        }
        assertParseFailure(parser, "n/Alice p/", PhoneStartsWithPredicate.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "n/Alice t/", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "n/Alice t/LIFE-1", Tag.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "t/" + "A".repeat(31), Tag.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateCriteria_rejectsRatherThanIgnoringValues() {
        for (Prefix prefix : List.of(CliSyntax.PREFIX_NAME, CliSyntax.PREFIX_PHONE, CliSyntax.PREFIX_TAG)) {
            String marker = prefix.getPrefix();
            String message = Messages.getErrorMessageForDuplicatePrefixes(prefix);
            assertParseFailure(parser, marker + "123 " + marker + "456", message);
            assertParseFailure(parser, marker + " " + marker + "123", message);
            assertParseFailure(parser, marker + "123 " + marker, message);
        }
    }

    @Test
    public void parse_mixedPreambleOrUnsupportedCriteria_rejectsAmbiguity() {
        for (String query : List.of("Alice p/912", "Alice n/Bob", "n/Alice e/a@example.com",
                "n/Alice x/value", "n/Alice a/address", "t/LIFE z/value")) {
            assertParseFailure(parser, query, String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
    }
}
