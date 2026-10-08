package seedu.insureconnect.storage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import seedu.insureconnect.commons.exceptions.IllegalValueException;
import seedu.insureconnect.model.InsureConnect;
import seedu.insureconnect.model.ReadOnlyInsureConnect;
import seedu.insureconnect.model.person.Person;

/**
 * An Immutable InsureConnect that is serializable to JSON format.
 */
@JsonRootName(value = "insureconnect")
class JsonSerializableInsureConnect {

    public static final String MESSAGE_DUPLICATE_PERSON = "Persons list contains duplicate person(s).";
    public static final String MESSAGE_DUPLICATE_POLICY_NUMBER = "Persons list contains duplicate policy numbers.";

    private final List<JsonAdaptedPerson> persons = new ArrayList<>();

    /**
     * Constructs a {@code JsonSerializableInsureConnect} with the given persons.
     */
    @JsonCreator
    public JsonSerializableInsureConnect(@JsonProperty("persons") List<JsonAdaptedPerson> persons) {
        this.persons.addAll(persons);
    }

    /**
     * Converts a given {@code ReadOnlyInsureConnect} into this class for Jackson use.
     *
     * @param source future changes to this will not affect the created {@code JsonSerializableInsureConnect}.
     */
    public JsonSerializableInsureConnect(ReadOnlyInsureConnect source) {
        persons.addAll(source.getPersonList().stream().map(JsonAdaptedPerson::new).collect(Collectors.toList()));
    }

    /**
     * Converts this InsureConnect into the model's {@code InsureConnect} object.
     *
     * @throws IllegalValueException if there were any data constraints violated.
     */
    public InsureConnect toModelType() throws IllegalValueException {
        InsureConnect insureConnect = new InsureConnect();
        for (JsonAdaptedPerson jsonAdaptedPerson : persons) {
            Person person = jsonAdaptedPerson.toModelType();
            if (insureConnect.hasPerson(person)) {
                throw new IllegalValueException(MESSAGE_DUPLICATE_PERSON);
            }
            for (Person existing : insureConnect.getPersonList()) {
                if (person.getTags().stream().anyMatch(existing.getTags()::contains)) {
                    throw new IllegalValueException(MESSAGE_DUPLICATE_POLICY_NUMBER);
                }
            }
            insureConnect.addPerson(person);
        }
        return insureConnect;
    }

}
