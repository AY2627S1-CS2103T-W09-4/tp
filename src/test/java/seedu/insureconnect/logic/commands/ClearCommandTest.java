package seedu.insureconnect.logic.commands;

import static seedu.insureconnect.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.insureconnect.testutil.TypicalPersons.getTypicalInsureConnect;

import org.junit.jupiter.api.Test;

import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.Model;
import seedu.insureconnect.model.ModelManager;
import seedu.insureconnect.model.UserPrefs;

public class ClearCommandTest {

    @Test
    public void execute_emptyInsureConnect_success() {
        Model model = new ModelManager();
        Model expectedModel = new ModelManager();

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_nonEmptyInsureConnect_success() {
        Model model = new ModelManager(getTypicalInsureConnect(), new UserPrefs());
        Model expectedModel = new ModelManager(getTypicalInsureConnect(), new UserPrefs());
        expectedModel.setInsureConnect(new InsureConnect());

        assertCommandSuccess(new ClearCommand(), model, ClearCommand.MESSAGE_SUCCESS, expectedModel);
    }

}
