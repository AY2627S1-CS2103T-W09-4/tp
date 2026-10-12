package seedu.insureconnect.logic.parser;

import static seedu.insureconnect.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.insureconnect.logic.commands.ListCommand;
import seedu.insureconnect.logic.commands.ListCommand.SortOrder;
import seedu.insureconnect.logic.parser.exceptions.ParseException;

/** Parses the optional sort order of the list command. */
public class ListCommandParser implements Parser<ListCommand> {

    @Override
    public ListCommand parse(String args) throws ParseException {
        return switch (args.trim()) {
            case "" -> new ListCommand();
            case "n/" -> new ListCommand(SortOrder.NAME);
            case "c/" -> new ListCommand(SortOrder.CREATED_AT);
            default -> throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE));
        };
    }
}
