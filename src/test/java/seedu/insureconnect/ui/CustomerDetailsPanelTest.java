package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.testutil.PersonBuilder;

public class CustomerDetailsPanelTest extends GuiTest {

    @Test
    public void constructor_noCustomer_showsEmptyState() throws Exception {
        runOnFxThread(() -> {
            CustomerDetailsPanel panel = new CustomerDetailsPanel();

            assertTrue(emptyStateOf(panel).isVisible());
            assertTrue(emptyStateOf(panel).isManaged());
            assertFalse(detailsOf(panel).isVisible());
            assertFalse(detailsOf(panel).isManaged());
        });
    }

    @Test
    public void displayCustomer_customerWithRemark_rendersAllDetailsAndSortedPolicies() throws Exception {
        runOnFxThread(() -> {
            Person customer = new PersonBuilder(ALICE)
                    .withTags("Z999", "A123")
                    .withRemark("Call after 6")
                    .build();
            CustomerDetailsPanel panel = new CustomerDetailsPanel();
            show(panel.getRoot());

            panel.displayCustomer(customer);

            assertFalse(emptyStateOf(panel).isVisible());
            assertFalse(emptyStateOf(panel).isManaged());
            assertTrue(detailsOf(panel).isVisible());
            assertTrue(detailsOf(panel).isManaged());
            assertEquals(customer.getName().fullName, textControlOf(panel, "detailName").getText());
            assertEquals(customer.getPhone().value, textControlOf(panel, "detailPhone").getText());
            assertEquals(customer.getEmail().value, textControlOf(panel, "detailEmail").getText());
            assertEquals(customer.getAddress().value, textControlOf(panel, "detailAddress").getText());
            assertEquals("Call after 6", textControlOf(panel, "detailRemark").getText());
            assertTrue(remarkSectionOf(panel).isVisible());
            assertTrue(remarkSectionOf(panel).isManaged());
            assertEquals(List.of("A123", "Z999"), policiesOf(panel).getChildren().stream()
                    .map(node -> ((Label) node).getText()).toList());
        });
    }

    @Test
    public void displayCustomer_customerWithoutRemark_hidesRemarkSection() throws Exception {
        runOnFxThread(() -> {
            CustomerDetailsPanel panel = new CustomerDetailsPanel();
            show(panel.getRoot());

            panel.displayCustomer(ALICE);

            assertFalse(remarkSectionOf(panel).isVisible());
            assertFalse(remarkSectionOf(panel).isManaged());
        });
    }

    @Test
    public void displayCustomer_detailsAreReadOnlyAndSelectable() throws Exception {
        runOnFxThread(() -> {
            Person customer = new PersonBuilder(ALICE)
                    .withRemark("Call after 6")
                    .build();
            CustomerDetailsPanel panel = new CustomerDetailsPanel();
            show(panel.getRoot());
            panel.displayCustomer(customer);

            List.of(
                    "detailName",
                    "detailPhone",
                    "detailEmail",
                    "detailAddress",
                    "detailRemark"
            ).forEach(id -> {
                TextInputControl detail = textControlOf(panel, id);

                assertFalse(detail.isEditable());
                assertTrue(detail.isFocusTraversable());

                detail.selectAll();
                assertEquals(detail.getText(), detail.getSelectedText());
            });
        });
    }

    @Test
    public void clear_displayedCustomer_restoresEmptyStateAndClearsDetails() throws Exception {
        runOnFxThread(() -> {
            CustomerDetailsPanel panel = new CustomerDetailsPanel();
            show(panel.getRoot());
            panel.displayCustomer(new PersonBuilder(ALICE).withRemark("Follow up").build());

            panel.clear();

            assertTrue(emptyStateOf(panel).isVisible());
            assertTrue(emptyStateOf(panel).isManaged());
            assertFalse(detailsOf(panel).isVisible());
            assertFalse(detailsOf(panel).isManaged());
            assertEquals("", textControlOf(panel, "detailName").getText());
            assertEquals("", textControlOf(panel, "detailPhone").getText());
            assertEquals("", textControlOf(panel, "detailEmail").getText());
            assertEquals("", textControlOf(panel, "detailAddress").getText());
            assertEquals("", textControlOf(panel, "detailRemark").getText());
            assertTrue(policiesOf(panel).getChildren().isEmpty());
        });
    }

    @Test
    public void displayCustomer_null_throwsNullPointerException() throws Exception {
        runOnFxThread(() -> {
            CustomerDetailsPanel panel = new CustomerDetailsPanel();

            assertThrows(NullPointerException.class, () -> panel.displayCustomer(null));
        });
    }

    /**
     * Returns the panel's empty-state container.
     */
    private VBox emptyStateOf(CustomerDetailsPanel panel) {
        return (VBox) panel.getRoot().lookup("#emptyState");
    }

    /**
     * Returns the scroll pane containing a selected customer's details.
     */
    private ScrollPane detailsOf(CustomerDetailsPanel panel) {
        return (ScrollPane) panel.getRoot().lookup("#detailsScrollPane");
    }

    /**
     * Returns the container for the optional remark.
     */
    private VBox remarkSectionOf(CustomerDetailsPanel panel) {
        return (VBox) panel.getRoot().lookup("#detailRemarkSection");
    }

    /**
     * Returns the policy container.
     */
    private FlowPane policiesOf(CustomerDetailsPanel panel) {
        return (FlowPane) panel.getRoot().lookup("#detailPolicies");
    }

    /**
     * Returns a selectable detail control by its FXML identifier.
     */
    private TextInputControl textControlOf(CustomerDetailsPanel panel, String id) {
        return (TextInputControl) panel.getRoot().lookup("#" + id);
    }
}
