package seedu.insureconnect.logic.parser;

import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_TAG;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import seedu.insureconnect.logic.commands.FindCommand;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.person.AllCriteriaPredicate;
import seedu.insureconnect.model.person.NameContainsKeywordsPredicate;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.model.person.PhoneStartsWithPredicate;
import seedu.insureconnect.model.person.PolicyNumberMatchesPredicate;

/**
 * Parses legacy name searches and explicit, conjunctive name, phone and policy criteria.
 */
public class FindCommandParser implements Parser<FindCommand> {
    private static final Prefix[] UNSUPPORTED_PREFIXES = {PREFIX_EMAIL, PREFIX_ADDRESS, PREFIX_REMARK};

    @Override
    public FindCommand parse(String args) throws ParseException {
        String query = requireSearchQuery(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + query, PREFIX_NAME, PREFIX_PHONE, PREFIX_TAG);
        if (!hasSearchCriteria(arguments)) {
            return new FindCommand(parseNameCriterion(query));
        }
        validateCriteria(query, arguments);
        return new FindCommand(combineCriteria(parseCriteria(arguments)));
    }

    /**
     * Normalizes separators for the shared tokenizer and rejects empty queries.
     */
    private String requireSearchQuery(String args) throws ParseException {
        String query = args.trim().replaceAll("\\s+", " ");
        if (query.isEmpty()) {
            throw invalidFormat();
        }
        return query;
    }

    /**
     * Distinguishes explicit criteria from the existing unprefixed name-search syntax.
     */
    private boolean hasSearchCriteria(ArgumentMultimap arguments) {
        return Stream.of(PREFIX_NAME, PREFIX_PHONE, PREFIX_TAG)
                .anyMatch(prefix -> arguments.getValue(prefix).isPresent());
    }

    /**
     * Rejects ambiguous preambles, unsupported fields and repeated criteria.
     */
    private void validateCriteria(String query, ArgumentMultimap arguments) throws ParseException {
        if (!arguments.getPreamble().isEmpty()) {
            throw invalidFormat();
        }
        rejectUnsupportedFields(query);
        arguments.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_TAG);
    }

    /**
     * Rejects customer fields that find cannot search, such as {@code e/}, so they do not silently
     * become part of a name value. Other words containing a slash, such as {@code s/o}, are name text,
     * as they are in {@code add}.
     */
    private void rejectUnsupportedFields(String query) throws ParseException {
        ArgumentMultimap unsupportedFields = ArgumentTokenizer.tokenize(" " + query, UNSUPPORTED_PREFIXES);
        if (Stream.of(UNSUPPORTED_PREFIXES).anyMatch(prefix -> unsupportedFields.getValue(prefix).isPresent())) {
            throw invalidFormat();
        }
    }

    /**
     * Parses each supported field in a fixed order, independent of argument order.
     */
    private List<Predicate<Person>> parseCriteria(ArgumentMultimap arguments) throws ParseException {
        List<Predicate<Person>> criteria = new ArrayList<>();
        if (arguments.getValue(PREFIX_NAME).isPresent()) {
            criteria.add(parseNameCriterion(ParserUtil.unescape(arguments.getValue(PREFIX_NAME).orElseThrow())));
        }
        if (arguments.getValue(PREFIX_PHONE).isPresent()) {
            criteria.add(parsePhoneCriterion(arguments.getValue(PREFIX_PHONE).orElseThrow()));
        }
        if (arguments.getValue(PREFIX_TAG).isPresent()) {
            criteria.add(parsePolicyCriterion(arguments.getValue(PREFIX_TAG).orElseThrow()));
        }
        return criteria;
    }

    /**
     * Reuses a single criterion directly and intersects multiple criteria.
     */
    private Predicate<Person> combineCriteria(List<Predicate<Person>> criteria) {
        return criteria.size() == 1 ? criteria.getFirst() : new AllCriteriaPredicate(criteria);
    }

    /**
     * Preserves OR matching between whitespace-separated name keywords.
     */
    private NameContainsKeywordsPredicate parseNameCriterion(String value) throws ParseException {
        if (value.isEmpty()) {
            throw invalidFormat();
        }
        return new NameContainsKeywordsPredicate(List.of(value.split("\\s+")));
    }

    /**
     * Reuses the existing partial-phone validation and matching rule.
     */
    private PhoneStartsWithPredicate parsePhoneCriterion(String value) throws ParseException {
        if (!PhoneStartsWithPredicate.isValidPrefix(value)) {
            throw new ParseException(PhoneStartsWithPredicate.MESSAGE_CONSTRAINTS);
        }
        return new PhoneStartsWithPredicate(value);
    }

    /**
     * Validates policy numbers using the existing policy-number parser.
     */
    private PolicyNumberMatchesPredicate parsePolicyCriterion(String value) throws ParseException {
        return new PolicyNumberMatchesPredicate(ParserUtil.parseTag(value).tagName);
    }

    /**
     * Builds the shared usage error for malformed search syntax.
     */
    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }
}
