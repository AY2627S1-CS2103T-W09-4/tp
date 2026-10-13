package seedu.insureconnect.logic.parser;

import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_PHONE;

import java.util.List;

import seedu.insureconnect.logic.commands.FindCommand;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.PhoneStartsWithPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        String query = requireSearchQuery(args);
        if (isPhoneSearch(query)) {
            return parsePhoneSearch(query);
        }
        return parseNameSearch(query);
    }

    /**
     * Removes surrounding whitespace and rejects a missing search query.
     */
    private String requireSearchQuery(String args) throws ParseException {
        String trimmedArgs = args.trim();
        if (trimmedArgs.isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        return trimmedArgs;
    }

    /**
     * Identifies the explicit phone-search mode.
     */
    private boolean isPhoneSearch(String query) {
        return query.startsWith(PREFIX_PHONE.getPrefix());
    }

    /**
     * Parses one phone prefix without accepting additional search criteria.
     */
    private FindCommand parsePhoneSearch(String query) throws ParseException {
        String phonePrefix = parsePhonePrefix(query);
        return new FindCommand(new PhoneStartsWithPredicate(phonePrefix));
    }

    /**
     * Extracts and validates the digits after the phone-search marker.
     */
    private String parsePhonePrefix(String query) throws ParseException {
        String phonePrefix = query.substring(PREFIX_PHONE.getPrefix().length()).trim();
        if (!PhoneStartsWithPredicate.isValidPrefix(phonePrefix)) {
            throw new ParseException(PhoneStartsWithPredicate.MESSAGE_CONSTRAINTS);
        }
        return phonePrefix;
    }

    /**
     * Parses name keywords while rejecting a phone prefix mixed into a name search.
     */
    private FindCommand parseNameSearch(String query) throws ParseException {
        List<String> keywords = parseNameKeywords(query);
        return new FindCommand(new NameContainsKeywordsPredicate(keywords));
    }

    /**
     * Extracts whitespace-separated name keywords and rejects mixed search modes.
     */
    private List<String> parseNameKeywords(String query) throws ParseException {
        List<String> keywords = List.of(query.split("\\s+"));
        if (keywords.stream().anyMatch(this::isPhoneSearch)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }
        return keywords;
    }
}
