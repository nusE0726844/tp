package seedu.vms.logic.commands.patient;

import static java.util.Objects.requireNonNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import seedu.vms.commons.core.Messages;
import seedu.vms.commons.util.CollectionUtil;
import seedu.vms.logic.CommandMessage;
import seedu.vms.logic.commands.Command;
import seedu.vms.model.GroupName;
import seedu.vms.model.Model;
import seedu.vms.model.patient.BloodType;
import seedu.vms.model.patient.Dob;
import seedu.vms.model.patient.Phone;
import seedu.vms.model.patient.predicates.BloodTypePredicate;
import seedu.vms.model.patient.predicates.DobPredicate;
import seedu.vms.model.patient.predicates.NameContainsKeywordsPredicate;
import seedu.vms.model.patient.predicates.PhoneNumberPredicate;

/**
 * Finds and lists all patients in patient manager whose name contains any of the argument keywords.
 * Keyword matching is case insensitive.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";
    public static final String COMMAND_GROUP = "patient";

    public static final String MESSAGE_USAGE = COMMAND_GROUP + " " + COMMAND_WORD
            + ": Finds all patients whose names contain any of "
            + "the specified keywords (case-insensitive) and displays them as a list with index numbers.\n"
            + "Parameters: KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_GROUP + " " + COMMAND_WORD + " alice bob charlie";

    private final NameContainsKeywordsPredicate namePredicate;
    private final PhoneNumberPredicate phonePredicate;
    private final DobPredicate dobPredicate;
    private final BloodTypePredicate bloodTypePredicate;

    /**
     * Existing FindCommand that was previously used to search using name only
     *
     * @param namePredicate
     */
    public FindCommand(NameContainsKeywordsPredicate namePredicate) {
        this.namePredicate = namePredicate;
        this.phonePredicate = null;
        this.dobPredicate = null;
        this.bloodTypePredicate = null;
    }

    /**
     * New FindCommand that contains more patient information that is given by the user.
     * Accepts different descriptors when applicable
     *
     * @param findPatientDescriptor
     */
    public FindCommand(FindPatientDescriptor findPatientDescriptor) {
        if (findPatientDescriptor.getNameSearch().isPresent()) {
            String[] nameKeywords = findPatientDescriptor.getNameSearch().get().split("\\s+");
            this.namePredicate = new NameContainsKeywordsPredicate(Arrays.asList(nameKeywords));
        } else {
            this.namePredicate = null;
        }

        if (findPatientDescriptor.getPhone().isPresent()) {
            this.phonePredicate = new PhoneNumberPredicate(findPatientDescriptor.getPhone().get());
        } else {
            this.phonePredicate = null;
        }

        if (findPatientDescriptor.getDob().isPresent()) {
            this.dobPredicate = new DobPredicate(findPatientDescriptor.getDob().get());
        } else {
            this.dobPredicate = null;
        }

        if (findPatientDescriptor.getBloodType().isPresent()) {
            this.bloodTypePredicate = new BloodTypePredicate(findPatientDescriptor.getBloodType().get());
        } else {
            this.bloodTypePredicate = null;
        }
    }

    @Override
    public CommandMessage execute(Model model) {
        requireNonNull(model);
        if (namePredicate != null) {
            model.updateFilteredPatientList(namePredicate);
        }
        if (phonePredicate != null) {
            model.updateFilteredPatientList(phonePredicate);
        }
        if (dobPredicate != null) {
            model.updateFilteredPatientList(dobPredicate);
        }
        if (bloodTypePredicate != null) {
            model.updateFilteredPatientList(bloodTypePredicate);
        }
        return new CommandMessage(
                String.format(Messages.MESSAGE_PATIENTS_LISTED_OVERVIEW, model.getFilteredPatientList().size()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
                || (other instanceof FindCommand // instanceof handles nulls
                        && namePredicate.equals(((FindCommand) other).namePredicate) // state check
                        && phonePredicate.equals(((FindCommand) other).phonePredicate) // state check
                        && dobPredicate.equals(((FindCommand) other).dobPredicate) // state check
                        && bloodTypePredicate.equals(((FindCommand) other).bloodTypePredicate)); // state check
    }

    /**
     * Stores the details to edit the patient with. Each non-empty field value will replace the
     * corresponding field value of the patient.
     */
    public static class FindPatientDescriptor {
        private String nameSearch;
        private Phone phone;
        private Dob dob;
        private BloodType bloodType;
        private Set<GroupName> allergies;
        private Set<GroupName> vaccines;

        public FindPatientDescriptor() {}

        /**
         * Copy constructor.
         * A defensive copy of {@code allergies} is used internally.
         * A defensive copy of {@code vaccines} is used internally.
         */
        public FindPatientDescriptor(FindPatientDescriptor toCopy) {
            setNameSearch(toCopy.nameSearch);
            setPhone(toCopy.phone);
            setDob(toCopy.dob);
            setBloodType(toCopy.bloodType);
            setAllergies(toCopy.allergies);
            setVaccines(toCopy.vaccines);
        }

        /**
         * Returns true if at least one field is edited.
         */
        public boolean isAnyFieldEdited() {
            return CollectionUtil.isAnyNonNull(nameSearch, phone, dob, bloodType, allergies, vaccines);
        }

        public void setNameSearch(String nameSearch) {
            this.nameSearch = nameSearch;
        }

        public Optional<String> getNameSearch() {
            return Optional.ofNullable(nameSearch);
        }

        public void setPhone(Phone phone) {
            this.phone = phone;
        }

        public Optional<Phone> getPhone() {
            return Optional.ofNullable(phone);
        }

        public void setDob(Dob dob) {
            this.dob = dob;
        }

        public Optional<Dob> getDob() {
            return Optional.ofNullable(dob);
        }

        public void setBloodType(BloodType bloodType) {
            this.bloodType = bloodType;
        }

        public Optional<BloodType> getBloodType() {
            return Optional.ofNullable(bloodType);
        }

        /**
         * Sets {@code allergies} to this object's {@code allergies}.
         * A defensive copy of {@code allergies} is used internally.
         */
        public void setAllergies(Set<GroupName> allergies) {
            this.allergies = (allergies != null) ? new HashSet<>(allergies) : null;
        }

        /**
         * Returns an unmodifiable allergy set, which throws
         * {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code allergies} is null.
         */
        public Optional<Set<GroupName>> getAllergies() {
            return (allergies != null) ? Optional.of(Collections.unmodifiableSet(allergies)) : Optional.empty();
        }

        /**
         * Sets {@code vaccines} to this object's {@code vaccines}.
         * A defensive copy of {@code vaccines} is used internally.
         */
        public void setVaccines(Set<GroupName> vaccines) {
            this.vaccines = (vaccines != null) ? new HashSet<>(vaccines) : null;
        }

        /**
         * Returns an unmodifiable vaccine set, which throws
         * {@code UnsupportedOperationException}
         * if modification is attempted.
         * Returns {@code Optional#empty()} if {@code vaccines} is null.
         */
        public Optional<Set<GroupName>> getVaccines() {
            return (vaccines != null) ? Optional.of(Collections.unmodifiableSet(vaccines)) : Optional.empty();
        }

        @Override
        public boolean equals(Object other) {
            // short circuit if same object
            if (other == this) {
                return true;
            }

            // instanceof handles nulls
            if (!(other instanceof FindPatientDescriptor)) {
                return false;
            }

            // state check
            FindPatientDescriptor e = (FindPatientDescriptor) other;

            return getNameSearch().equals(e.getNameSearch())
                    && getPhone().equals(e.getPhone())
                    && getDob().equals(e.getDob())
                    && getBloodType().equals(e.getBloodType())
                    && getAllergies().equals(e.getAllergies())
                    && getVaccines().equals(e.getVaccines());
        }
    }
}
