package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.testutil.PersonBuilder;

public class PersonCardTest extends GuiTest {

    @Test
    public void constructor_personDetails_rendersFieldsAndSortedPolicies() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder(ALICE).withTags("Z999", "A123").withRemark("Call after 6").build();
            PersonCard card = new PersonCard(person, 3);
            assertEquals("3. ", label(card, "id").getText());
            assertEquals(person.getName().fullName, label(card, "name").getText());
            assertEquals(person.getPhone().value, label(card, "phone").getText());
            assertEquals(person.getEmail().value, label(card, "email").getText());
            assertEquals(person.getAddress().value, label(card, "address").getText());
            FlowPane tags = (FlowPane) card.getRoot().lookup("#tags");
            assertEquals(List.of("A123", "Z999"), tags.getChildren().stream()
                    .map(node -> ((Label) node).getText()).toList());
            assertEquals("Remark: Call after 6", label(card, "remark").getText());
            assertTrue(label(card, "remark").isVisible());
            assertTrue(label(card, "remark").isManaged());
        });
    }

    @Test
    public void constructor_emptyRemark_hidesLabelAndReleasesLayoutSpace() throws Exception {
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(new PersonBuilder(ALICE).withRemark("").build(), 1);
            Label remark = label(card, "remark");
            assertEquals("", remark.getText());
            assertFalse(remark.isVisible());
            assertFalse(remark.isManaged());
        });
    }

    /**
     * Looks up a rendered label by its FXML identifier.
     */
    private Label label(PersonCard card, String id) {
        return (Label) card.getRoot().lookup("#" + id);
    }
}
