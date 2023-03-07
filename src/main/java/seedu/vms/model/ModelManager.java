package seedu.vms.model;

import static java.util.Objects.requireNonNull;
import static seedu.vms.commons.util.CollectionUtil.requireAllNonNull;

import java.nio.file.Path;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.collections.ObservableMap;
import seedu.vms.commons.core.GuiSettings;
import seedu.vms.commons.core.LogsCenter;
import seedu.vms.model.appointment.Appointment;
import seedu.vms.model.appointment.AppointmentManager;
import seedu.vms.model.patient.AddressBook;
import seedu.vms.model.patient.Patient;
import seedu.vms.model.patient.ReadOnlyAddressBook;
import seedu.vms.model.vaccination.VaxType;
import seedu.vms.model.vaccination.VaxTypeManager;

/**
 * Represents the in-memory model of the address book data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
        private final AppointmentManager appointmentManager;
    private final VaxTypeManager vaxTypeManager;
    private final UserPrefs userPrefs;

    private final FilteredIdDataMap<Patient> filteredPatientMap;

    /**
     * Initializes a ModelManager with the given addressBook and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, VaxTypeManager vaxTypeManager, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPatientMap = new FilteredIdDataMap<>(this.addressBook.getMapView());

        appointmentManager = new AppointmentManager();
        this.vaxTypeManager = vaxTypeManager;
    }

    /**
     * Convenience constructor to construct a {@code ModelManager} with an
     * empty {@code VaxTypeManager}.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        this(addressBook, new VaxTypeManager(), userPrefs);
    }

    public ModelManager() {
        this(new AddressBook(), new VaxTypeManager(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        requireNonNull(userPrefs);
        this.userPrefs.resetData(userPrefs);
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    @Override
    public Path getAddressBookFilePath() {
        return userPrefs.getAddressBookFilePath();
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        requireNonNull(addressBookFilePath);
        userPrefs.setAddressBookFilePath(addressBookFilePath);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPatient(int id) {
        return addressBook.contains(id);
    }

    @Override
    public void deletePatient(int id) {
        addressBook.remove(id);
    }

    @Override
    public void addPatient(Patient patient) {
        addressBook.add(patient);
        updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS);
    }

    @Override
    public void setPatient(int id, Patient editedPatient) {
        requireAllNonNull(editedPatient);

        addressBook.set(id, editedPatient);
    }

    //=========== AppointmentManager ==========================================================================

    @Override
    public void addAppointment(Appointment appointment) {
        appointmentManager.add(appointment);
    }

    //=========== Filtered Patient List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Patient} backed by the internal list of
     * {@code versionedAddressBook}
     */
    @Override
    public ObservableMap<Integer, IdData<Patient>> getFilteredPatientList() {
        return filteredPatientMap.asUnmodifiableObservableMap();
    }

    @Override
    public void updateFilteredPatientList(Predicate<Patient> predicate) {
        requireNonNull(predicate);
        filteredPatientMap.filter(predicate);
    }

    //=========== Filtered VaxType Map Accessors ==============================================================

    @Override
    public ObservableMap<String, VaxType> getFilteredVaxTypeMap() {
        return vaxTypeManager.asUnmodifiableObservableMap();
    }

    //=========== Misc methods ================================================================================

    @Override
    public boolean equals(Object obj) {
        // short circuit if same object
        if (obj == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(obj instanceof ModelManager)) {
            return false;
        }

        // state check
        ModelManager other = (ModelManager) obj;
        return addressBook.equals(other.addressBook)
                && userPrefs.equals(other.userPrefs)
                && filteredPatientMap.asUnmodifiableObservableMap()
                        .equals(other.filteredPatientMap.asUnmodifiableObservableMap());
    }

    @Override
    public String toString() {
        return filteredPatientMap.asUnmodifiableObservableMap().toString();
    }

}
