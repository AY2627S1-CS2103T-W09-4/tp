package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;
import static seedu.insureconnect.testutil.TypicalPersons.BENSON;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;
import seedu.insureconnect.commons.core.ColourScheme;

/**
 * Exercises theme menus and customer-selection updates through the real window controls.
 */
public class MainWindowInteractionTest extends GuiTest {
    @Test
    public void colourSchemeMenu_updatesBothWindowsAndPersistsSelection() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            MainWindow window = createWindow(logic);
            window.handleHelp();
            Stage help = Window.getWindows().stream().filter(Stage.class::isInstance)
                    .map(Stage.class::cast).filter(stage -> "Help".equals(stage.getTitle()))
                    .findFirst().orElseThrow();
            MenuBar menus = (MenuBar) window.getRoot().getScene().lookup("#menuBar");
            Menu view = menus.getMenus().stream().filter(menu -> "View".equals(menu.getText()))
                    .findFirst().orElseThrow();
            MenuItem toggle = view.getItems().stream()
                    .filter(item -> "Toggle colour scheme".equals(item.getText()))
                    .findFirst()
                    .orElseThrow();
            toggle.fire();
            assertScheme(window.getRoot(), help, logic, ColourScheme.LIGHT);
            Menu schemes = view.getItems().stream()
                    .filter(Menu.class::isInstance)
                    .map(Menu.class::cast)
                    .findFirst()
                    .orElseThrow();
            RadioMenuItem dark = (RadioMenuItem) schemes.getItems().get(0);
            RadioMenuItem light = (RadioMenuItem) schemes.getItems().get(1);
            light.fire();
            assertScheme(window.getRoot(), help, logic, ColourScheme.LIGHT);
            assertTrue(light.isSelected());
            assertFalse(dark.isSelected());
            dark.fire();
            assertScheme(window.getRoot(), help, logic, ColourScheme.DARK);
            assertTrue(dark.isSelected());
            assertFalse(light.isSelected());
        });
    }

    @Test
    public void customerSelection_updatesDetailsAndClearsRemovedCustomer() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            logic.getFilteredPersonList().addAll(ALICE, BENSON);
            MainWindow window = createWindow(logic);
            ListView<?> list = (ListView<?>) window.getRoot().getScene().lookup("#personListView");
            assertSame(list, window.getPersonListPanel().getFocusTarget());
            list.getSelectionModel().select(1);
            Label name = (Label) window.getRoot().getScene().lookup("#detailName");
            assertEquals(BENSON.getName().fullName, name.getText());
            logic.getFilteredPersonList().remove(BENSON);
            assertEquals(ALICE.getName().fullName, name.getText());
            logic.getFilteredPersonList().clear();
            assertEquals("", name.getText());
        });
    }

    @Test
    public void colourSchemeShortcut_togglesSchemeFromBothWindows() throws Exception {
        runOnFxThread(() -> {
            UiLogicStub logic = new UiLogicStub();
            MainWindow window = createWindow(logic);
            window.handleHelp();

            Stage help = Window.getWindows().stream()
                    .filter(Stage.class::isInstance)
                    .map(Stage.class::cast)
                    .filter(stage -> "Help".equals(stage.getTitle()))
                    .findFirst()
                    .orElseThrow();

            pressColourSchemeShortcut(window.getRoot());
            assertScheme(
                    window.getRoot(),
                    help,
                    logic,
                    ColourScheme.LIGHT);

            pressColourSchemeShortcut(help);
            assertScheme(
                    window.getRoot(),
                    help,
                    logic,
                    ColourScheme.DARK);
        });
    }

    /**
     * Sends F2 through the given window's keyboard event filters.
     */
    private void pressColourSchemeShortcut(Stage stage) {
        stage.getScene().getRoot().fireEvent(new KeyEvent(
                KeyEvent.KEY_PRESSED,
                "",
                "",
                KeyCode.F2,
                false,
                false,
                false,
                false));
    }

    /**
     * Asserts that the visible palettes and persisted preference agree.
     */
    private void assertScheme(Stage main, Stage help, UiLogicStub logic, ColourScheme scheme) {
        boolean isLight = scheme == ColourScheme.LIGHT;
        assertEquals(isLight, main.getScene().getStylesheets().stream()
                .anyMatch(path -> path.endsWith("LightTheme.css")));
        assertEquals(isLight, help.getScene().getStylesheets().stream()
                .anyMatch(path -> path.endsWith("LightTheme.css")));
        assertEquals(scheme, logic.getGuiSettings().getColourScheme());
    }

    /**
     * Uses the production initialization order to create a fully populated window.
     */
    private MainWindow createWindow(UiLogicStub logic) {
        MainWindow window = new MainWindow(new Stage(), logic, Path.of("test.json"));
        window.show();
        window.fillInnerParts();
        window.getRoot().getScene().getRoot().applyCss();
        window.getRoot().getScene().getRoot().layout();
        return window;
    }
}
