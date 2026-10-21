package seedu.insureconnect.logic.parser;

import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_REMARK;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_TAG;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.logic.commands.AddCommand;
import seedu.insureconnect.logic.parser.exceptions.ParseException;
import seedu.insureconnect.model.person.Address;
import seedu.insureconnect.model.person.Email;
import seedu.insureconnect.model.person.Name;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.model.person.Phone;
import seedu.insureconnect.model.person.Remark;
import seedu.insureconnect.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap =
                ArgumentTokenizer.tokenize(args, PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL, PREFIX_ADDRESS,
                        PREFIX_TAG, PREFIX_REMARK);

        validateRequiredFieldsAndPreamble(argMultimap);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME, PREFIX_PHONE, PREFIX_EMAIL,
                PREFIX_ADDRESS, PREFIX_REMARK);
        Name name = parseField("name", () -> ParserUtil.parseName(argMultimap.getValue(PREFIX_NAME).get()));
        Phone phone = parseField("phone", () -> ParserUtil.parsePhone(argMultimap.getValue(PREFIX_PHONE).get()));
        Email email = parseField("email", () -> ParserUtil.parseEmail(argMultimap.getValue(PREFIX_EMAIL).get()));
        Address address = parseField("address", () -> ParserUtil.parseAddress(argMultimap.getValue(PREFIX_ADDRESS)
                .get()));
        Set<Tag> tagList = parseField("policy number", () ->
                ParserUtil.parseTags(argMultimap.getAllValues(PREFIX_TAG)));
        Remark remark = argMultimap.getValue(PREFIX_REMARK).isPresent()
                ? parseField("remark", () -> ParserUtil.parseRemark(argMultimap.getValue(PREFIX_REMARK).get()))
                : new Remark("");

        Person person = new Person(name, phone, email, address, tagList, remark, Instant.now());

        return new AddCommand(person);
    }

    private static void validateRequiredFieldsAndPreamble(ArgumentMultimap argumentMultimap) throws ParseException {
        List<String> missingFields = new ArrayList<>();
        addMissingFieldIfAbsent(missingFields, argumentMultimap, PREFIX_NAME, "name");
        addMissingFieldIfAbsent(missingFields, argumentMultimap, PREFIX_PHONE, "phone");
        addMissingFieldIfAbsent(missingFields, argumentMultimap, PREFIX_EMAIL, "email");
        addMissingFieldIfAbsent(missingFields, argumentMultimap, PREFIX_ADDRESS, "address");
        addMissingFieldIfAbsent(missingFields, argumentMultimap, PREFIX_TAG, "policy number");

        String preamble = argumentMultimap.getPreamble();
        if (!missingFields.isEmpty() || !preamble.isEmpty()) {
            throw new ParseException(Messages.getErrorMessageForMissingFieldsAndPreamble(
                    missingFields, preamble, AddCommand.MESSAGE_USAGE));
        }
    }

    private static void addMissingFieldIfAbsent(List<String> missingFields, ArgumentMultimap argumentMultimap,
                                                Prefix prefix, String fieldName) {
        if (argumentMultimap.getValue(prefix).isEmpty()) {
            missingFields.add(fieldName + " (" + prefix + ")");
        }
    }

    private static <T> T parseField(String fieldName, FieldParser<T> parser) throws ParseException {
        try {
            return parser.parse();
        } catch (ParseException e) {
            throw new ParseException("Invalid " + fieldName + ": " + e.getMessage(), e);
        }
    }

    @FunctionalInterface
    private interface FieldParser<T> {
        T parse() throws ParseException;
    }

}
