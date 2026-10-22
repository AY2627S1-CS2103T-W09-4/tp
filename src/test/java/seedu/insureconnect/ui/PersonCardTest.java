package seedu.insureconnect.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.insureconnect.testutil.TypicalPersons.ALICE;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import seedu.insureconnect.model.person.Person;
import seedu.insureconnect.testutil.PersonBuilder;

public class PersonCardTest extends GuiTest {

    @Test
    public void constructor_personDetails_rendersCompactFieldsAndSortedPolicies() throws Exception {
        runOnFxThread(() -> {
            Person person = new PersonBuilder(ALICE).withTags("Z999", "A123").withRemark("Call after 6").build();
            PersonCard card = new PersonCard(person, 3);

            assertEquals("03", label(card, "id").getText());
            assertEquals(person.getName().fullName, label(card, "name").getText());
            assertEquals(person.getPhone().value, label(card, "phone").getText());
            assertEquals(person.getEmail().value, label(card, "email").getText());

            FlowPane tags = (FlowPane) card.getRoot().lookup("#tags");
            assertEquals(List.of("A123", "Z999"), tags.getChildren().stream()
                    .map(node -> ((Label) node).getText()).toList());
        });
    }

    @Test
    public void setDisplayedIndex_validIndex_updatesDisplayedIndex() throws Exception {
        runOnFxThread(() -> {
            PersonCard card = new PersonCard(ALICE, 1);

            card.setDisplayedIndex(12);

            assertEquals("12", label(card, "id").getText());
        });
    }

    /**
     * Looks up a rendered label by its FXML identifier.
     */
    private Label label(PersonCard card, String id) {
        return (Label) card.getRoot().lookup("#" + id);
    }
}
