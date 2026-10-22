package seedu.insureconnect.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.logic.commands.CommandFormats.ADD_COMMAND_FORMAT;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_EMAIL;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_NAME;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_PHONE;
import static seedu.insureconnect.logic.parser.CliSyntax.PREFIX_TAG;
import static seedu.insureconnect.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import seedu.insureconnect.commons.util.ToStringBuilder;
import seedu.insureconnect.logic.Messages;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.model.tag.Tag;

/**
 * Adds a person to InsureConnect.
 */
public class AddCommand extends Command {

    public static final String COMMAND_WORD = "add";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Adds a customer to InsureConnect. "
            + "Format: " + ADD_COMMAND_FORMAT + "\n"
            + "Example: " + COMMAND_WORD + " "
            + PREFIX_NAME + "John Doe "
            + PREFIX_PHONE + "98765432 "
            + PREFIX_EMAIL + "johnd@example.com "
            + PREFIX_ADDRESS + "311, Clementi Ave 2, #02-25 "
            + PREFIX_TAG + "LIFE20481 "
            + PREFIX_TAG + "HEALTH20482";

    public static final String MESSAGE_SUCCESS = "New customer added: %1$s";
    public static final String MESSAGE_DUPLICATE_PERSON = "This customer is a duplicate "
            + "because a customer with the same "
            + "name and phone number already exists. Existing record at index %d: %s (phone: %s).";
    public static final String MESSAGE_DUPLICATE_POLICY_NUMBER =
            "Policy number %s already belongs to %s (phone: %s).";

    private final Person toAdd;

    /**
     * Creates an AddCommand to add the specified {@code Person}
     */
    public AddCommand(Person person) {
        requireNonNull(person);
        toAdd = person;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);

        Person duplicate = model.getInsureConnect().getPersonList().stream()
                .filter(toAdd::isSamePerson)
                .findFirst()
                .orElse(null);
        if (duplicate != null) {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            int displayedIndex = model.getFilteredPersonList().indexOf(duplicate) + 1;
            throw new CommandException(String.format(MESSAGE_DUPLICATE_PERSON, displayedIndex,
                    duplicate.getName().fullName, duplicate.getPhone().value));
        }

        for (Person existing : model.getInsureConnect().getPersonList()) {
            for (Tag policyNumber : toAdd.getTags()) {
                if (existing.getTags().contains(policyNumber)) {
                    throw new CommandException(String.format(MESSAGE_DUPLICATE_POLICY_NUMBER,
                            policyNumber.tagName, existing.getName().fullName, existing.getPhone().value));
                }
            }
        }

        model.addPerson(toAdd);
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(toAdd)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof AddCommand otherAddCommand)) {
            return false;
        }

        return toAdd.equals(otherAddCommand.toAdd);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("toAdd", toAdd)
                .toString();
    }
}
