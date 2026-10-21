package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import seedu.insureconnect.commons.core.GuiSettings;
import seedu.insureconnect.logic.commands.CommandResult;
import seedu.insureconnect.logic.commands.exceptions.CommandException;
import seedu.insureconnect.logic.parser.exceptions.ParseException;

public class MainWindowTest extends GuiTest {

    @Test
    public void initialize_savedGeometryAndFeedback_restoresWindowAndContacts() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            logic.setGuiSettings(new GuiSettings(900, 700, 30, 40));
            logic.getFilteredPersonList().add(ALICE);
            MainWindow window = new MainWindow(new Stage(), logic, Path.of("data", "test.json"), "Load warning");
            assertEquals(900, window.getRoot().getWidth());
            assertEquals(700, window.getRoot().getHeight());
            assertEquals(30, window.getRoot().getX());
            assertEquals(40, window.getRoot().getY());
            window.show();
            window.fillInnerParts();
            window.getRoot().getScene().getRoot().applyCss();
            window.getRoot().getScene().getRoot().layout();
            assertEquals("Load warning", outputOf(window).getText());
            GridPane cards = (GridPane) window.getPersonListPanel().getRoot().lookup("#personCards");
            assertEquals(1, cards.getChildren().size());
            Node card = cards.getChildren().get(0);
            assertEquals(ALICE.getName().fullName, ((Label) card.lookup("#name")).getText());
            assertEquals("01", ((Label) card.lookup("#id")).getText());
        });
    }

    @Test
    public void submit_success_displaysFeedbackAndClearsInput() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            MainWindow window = createWindow(logic);
            assertEquals("", outputOf(window).getText());
            submit(window, "list");
            assertEquals(List.of("list"), logic.getCommands());
            assertEquals("Executed: list", outputOf(window).getText());
            assertEquals("", inputOf(window).getText());
        });
    }

    @Test
    public void submit_parseFailure_displaysErrorAndRetainsInput() throws Exception {
        runOnFxThread(() -> assertCommandFailure(command -> {
            throw new ParseException("Invalid input");
        }));
    }

    @Test
    public void submit_commandFailure_displaysErrorAndRetainsInput() throws Exception {
        runOnFxThread(() -> assertCommandFailure(command -> {
            throw new CommandException("Invalid input");
        }));
    }

    @Test
    public void submit_helpThenExit_showsHelpAndClosesBothWindows() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            MainWindow window = createWindow(logic);
            logic.setExecutor(command -> new CommandResult("Help", true, false));
            submit(window, "help");
            Stage help = helpStage();
            assertTrue(help.isShowing());
            window.handleHelp();
            assertSame(help, helpStage());
            logic.setExecutor(command -> new CommandResult("Bye", false, true));
            submit(window, "exit");
            assertFalse(window.getRoot().isShowing());
            assertFalse(help.isShowing());
            assertEquals(window.getRoot().getWidth(), logic.getGuiSettings().getWindowWidth());
            assertEquals(window.getRoot().getHeight(), logic.getGuiSettings().getWindowHeight());
        });
    }

    @Test
    public void closeRequest_savesWindowPositionAndHidesStage() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            MainWindow window = createWindow(logic);
            Event.fireEvent(window.getRoot(), new WindowEvent(window.getRoot(), WindowEvent.WINDOW_CLOSE_REQUEST));
            assertFalse(window.getRoot().isShowing());
            assertEquals((int) window.getRoot().getX(), logic.getGuiSettings().getWindowCoordinates().x);
            assertEquals((int) window.getRoot().getY(), logic.getGuiSettings().getWindowCoordinates().y);
        });
    }

    @Test
    public void helpMenuAndFunctionKey_openSameHelpWindow() throws Exception {
        runOnFxThread(() -> {
            MainWindow window = createWindow(new UiLogicStub());
            MenuBar menus = (MenuBar) window.getRoot().getScene().lookup("#menuBar");
            menus.getMenus().stream().flatMap(menu -> menu.getItems().stream())
                    .filter(item -> "helpMenuItem".equals(item.getId())).findFirst().orElseThrow().fire();
            Stage help = helpStage();
            help.hide();
            inputOf(window).fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1,
                    false, false, false, false));
            assertSame(help, helpStage());
            help.hide();
            outputOf(window).fireEvent(new KeyEvent(KeyEvent.KEY_PRESSED, "", "", KeyCode.F1,
                    false, false, false, false));
            assertSame(help, helpStage());
        });
    }

    /**
     * Verifies that both parser and command errors reach the user through the same UI path.
     */
    private void assertCommandFailure(CommandBox.CommandExecutor executor) {
        UiLogicStub logic = new UiLogicStub();
        logic.setExecutor(executor);
        MainWindow window = createWindow(logic);
        submit(window, "invalid draft");
        assertEquals("Invalid input", outputOf(window).getText());
        assertEquals("invalid draft", inputOf(window).getText());
        assertTrue(inputOf(window).getStyleClass().contains(CommandBox.ERROR_STYLE_CLASS));
    }

    /**
     * Creates a populated window using the same initialization order as UiManager.
     */
    private MainWindow createWindow(UiLogicStub logic) {
        MainWindow window = new MainWindow(new Stage(), logic, Path.of("test.json"));
        window.show();
        window.fillInnerParts();
        return window;
    }

    /**
     * Submits text through the FXML command handler.
     */
    private void submit(MainWindow window, String command) {
        TextField input = inputOf(window);
        input.setText(command);
        input.fireEvent(new ActionEvent());
    }

    /**
     * Retrieves the command input from the live scene.
     */
    private TextField inputOf(MainWindow window) {
        return (TextField) window.getRoot().getScene().lookup("#commandTextField");
    }

    /**
     * Retrieves the feedback control from the live scene.
     */
    private TextArea outputOf(MainWindow window) {
        return (TextArea) window.getRoot().getScene().lookup("#resultDisplay");
    }

    /**
     * Finds the visible help stage without exposing private controller fields.
     */
    private Stage helpStage() {
        return Window.getWindows().stream().filter(window -> window instanceof Stage)
                .map(window -> (Stage) window).filter(stage -> "Help".equals(stage.getTitle()))
                .findFirst().orElseThrow();
    }
}
