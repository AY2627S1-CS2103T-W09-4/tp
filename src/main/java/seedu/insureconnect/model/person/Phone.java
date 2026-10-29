package seedu.insureconnect.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.insureconnect.commons.util.AppUtil.checkArgument;

/**
 * Represents a Person's phone number in InsureConnect.
 * Guarantees: immutable; is valid as declared in {@link #isValidPhone(String)}
 */
public class Phone {


    public static final String MESSAGE_CONSTRAINTS =
        "Phone numbers should only contain digits, may optionally start with '+', "
            + "and should be at least 3 digits long";
    public static final String VALIDATION_REGEX = "\\+?\\d{3,}";
    public final String value;

    /**
     * Constructs a {@code Phone}.
     *
     * @param phone A valid phone number.
     */
    public Phone(String phone) {
        requireNonNull(phone);
        checkArgument(isValidPhone(phone), MESSAGE_CONSTRAINTS);
        value = phone;
    }

    /**
     * Returns true if a given string is a valid phone number.
     */
    public static boolean isValidPhone(String test) {
        return test.matches(VALIDATION_REGEX);
    }

    /**
     * Returns true if both phone numbers are equivalent for duplicate detection.
     * A leading plus sign is ignored for all phone numbers. For Singapore numbers,
     * the country code {@code 65} is also ignored when followed by an 8-digit
     * local number.
     */
    public boolean hasSameDigits(Phone otherPhone) {
        requireNonNull(otherPhone);
        return normalizeForComparison(value)
                .equals(normalizeForComparison(otherPhone.value));
    }

    /**
     * Normalizes Singapore phone numbers for duplicate detection.
     */
    private static String normalizeForComparison(String phone) {
        String digits = phone.startsWith("+") ? phone.substring(1) : phone;

        // Remove Singapore country code when followed by an 8-digit local number.
        if (digits.startsWith("65") && digits.length() == 10) {
            return digits.substring(2);
        }

        return digits;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Phone otherPhone)) {
            return false;
        }

        return value.equals(otherPhone.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

}
