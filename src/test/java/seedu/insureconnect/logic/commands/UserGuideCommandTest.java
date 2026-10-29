package seedu.insureconnect.logic.commands;

import static seedu.insureconnect.logic.commands.CommandTestUtil.assertCommandSuccess;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;

public class UserGuideCommandTest {
    private final Model model = new ModelManager();
    private final Model expectedModel = new ModelManager();

    @Test
    public void execute_userGuide_success() {
        // valid userguide command -> opens the User Guide without changing customer data
        CommandResult expectedResult = new CommandResult(
                UserGuideCommand.MESSAGE_SUCCESS, false, false, false, true);

        assertCommandSuccess(new UserGuideCommand(), model, expectedResult, expectedModel);
    }
}
