package seedu.vms.model;

import static java.util.Objects.requireNonNull;
import static seedu.vms.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import seedu.vms.commons.core.GuiSettings;
import seedu.vms.commons.core.LogsCenter;
import seedu.vms.commons.core.Retriever;
import seedu.vms.commons.core.ValueChange;
import seedu.vms.commons.exceptions.IllegalValueException;
import seedu.vms.logic.parser.ParseResult;
import seedu.vms.logic.parser.VmsParser;
import seedu.vms.logic.parser.exceptions.ParseException;
import seedu.vms.model.appointment.Appointment;
import seedu.vms.model.appointment.AppointmentManager;
import seedu.vms.model.keyword.Keyword;
import seedu.vms.model.keyword.KeywordManager;
import seedu.vms.model.patient.Patient;
import seedu.vms.model.patient.PatientManager;
import seedu.vms.model.patient.ReadOnlyPatientManager;
import seedu.vms.model.vaccination.VaxType;
import seedu.vms.model.vaccination.VaxTypeManager;

/**
 * Represents the in-memory model of the patient manager data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final ObjectProperty<IdData<Patient>> detailedPatientProperty = new SimpleObjectProperty<>();
    private final ObjectProperty<VaxType> detailedVaxTypeProperty = new SimpleObjectProperty<>();

    private final PatientManager patientManager;
    private final AppointmentManager appointmentManager;
    private final VaxTypeManager vaxTypeManager;
    private final KeywordManager keywordManager;
    private final UserPrefs userPrefs;

    private final FilteredIdDataMap<Patient> filteredPatientMap;
    private final FilteredMapView<String, VaxType> filteredVaxTypeMap;
    private final FilteredIdDataMap<Appointment> filteredAppointmentMap;
    private final FilteredIdDataMap<Keyword> filteredKeywordMap;

    private final VmsParser vmsParser;

    private ObservableList<VaxType> displayList = null;

    /**
     * Initializes a ModelManager with the given patientManager and userPrefs.
     */
    public ModelManager(ReadOnlyPatientManager patientManager, VaxTypeManager vaxTypeManager,
            AppointmentManager appointmentManager, KeywordManager keywordManager, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(patientManager, vaxTypeManager, appointmentManager, userPrefs);

        logger.fine("Initializing with patient manager: " + patientManager + " and user prefs " + userPrefs);

        this.patientManager = new PatientManager(patientManager);
        filteredPatientMap = new FilteredIdDataMap<>(this.patientManager.getMapView());

        this.appointmentManager = new AppointmentManager(appointmentManager);
        filteredAppointmentMap = new FilteredIdDataMap<>(this.appointmentManager.getMapView());

        this.keywordManager = new KeywordManager(keywordManager);
        filteredKeywordMap = new FilteredIdDataMap<>(this.keywordManager.getMapView());

        this.vaxTypeManager = vaxTypeManager;
        filteredVaxTypeMap = new FilteredMapView<>(this.vaxTypeManager.asUnmodifiableObservableMap());

        this.userPrefs = new UserPrefs(userPrefs);

        this.vmsParser = new VmsParser();
    }

    /**
     * Convenience constructor to construct a {@code ModelManager} with an
     * empty {@code VaxTypeManager} and {@code AppointmentManager}.
     */
    public ModelManager(ReadOnlyPatientManager patientManager, ReadOnlyUserPrefs userPrefs) {
        this(patientManager, new VaxTypeManager(), new AppointmentManager(), new KeywordManager(), userPrefs);
    }

    /**
     * Convenience constructor to construct a {@code ModelManager} with an
     * empty {@code PatientManager}, {@code VaxTypeManager}, {@code AppointmentManager},
     * and {@code KeywordManager}.
     */
    public ModelManager() {
        this(new PatientManager(), new VaxTypeManager(), new AppointmentManager(),
                new KeywordManager(), new UserPrefs());
    }

    // =========== UserPrefs ==================================================================================

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

    // =========== Parsing =======================================================================================

    @Override
    public ParseResult parseCommand(String userCommand) throws ParseException {
        // TODO: Avoid creating a new parser everytime
        return vmsParser.parseCommand(userCommand);
    }

    // =========== PatientManager ================================================================================

    @Override
    public void setPatientManager(ReadOnlyPatientManager patientManager) {
        this.patientManager.resetData(patientManager);
    }

    @Override
    public ReadOnlyPatientManager getPatientManager() {
        return patientManager;
    }

    @Override
    public boolean hasPatient(int id) {
        return patientManager.contains(id);
    }

    @Override
    public void deletePatient(int id) {
        IdData<Patient> oldValue = patientManager.remove(id);
        ValueChange<IdData<Patient>> change = new ValueChange<>(oldValue, null);
        handlePatientChange(change);
    }

    @Override
    public void addPatient(Patient patient) {
        IdData<Patient> newValue = patientManager.add(patient);
        updateFilteredPatientList(PREDICATE_SHOW_ALL_PATIENTS);
        handlePatientChange(new ValueChange<>(null, newValue));
    }

    @Override
    public void setPatient(int id, Patient editedPatient) {
        requireAllNonNull(editedPatient);

        ValueChange<IdData<Patient>> change = patientManager.set(id, editedPatient);
        handlePatientChange(change);
    }


    @Override
    public ObjectProperty<IdData<Patient>> detailedPatientProperty() {
        return detailedPatientProperty;
    }


    @Override
    public void setDetailedPatient(IdData<Patient> data) {
        detailedPatientProperty.set(data);
    }

    // =========== AppointmentManager ==========================================================================

    @Override
    public void addAppointment(Appointment appointment) {
        appointmentManager.add(appointment);
        updateFilteredAppointmentList(PREDICATE_SHOW_ALL_APPOINTMENTS);
    }

    @Override
    public void deleteAppointment(int id) {
        appointmentManager.remove(id);
    }

    @Override
    public void markAppointment(int id) {
        appointmentManager.mark(id);
    }

    @Override
    public void unmarkAppointment(int id) {
        appointmentManager.unmark(id);
    }

    @Override
    public void setAppointment(int id, Appointment editedAppointment) {
        requireAllNonNull(editedAppointment);

        appointmentManager.set(id, editedAppointment);
    }


    @Override
    public void setAppointmentManager(AppointmentManager manager) {
        appointmentManager.resetData(manager);
    }


    @Override
    public List<String> validatePatientChange(ValueChange<IdData<Patient>> change) {
        // TODO: Implement this
        // implementation should be in appointment manager instead of here
        // as LogicManager is just a facade class.
        return List.of();
    }


    @Override
    public void handlePatientChange(ValueChange<IdData<Patient>> change) {
        appointmentManager.handlePatientChange(change);
        updatePatientDetail(change);
    }


    @Override
    public List<String> validateVaccinationChange(ValueChange<VaxType> change) {
        // TODO: Implement this
        // implementation should be in appointment manager instead of here
        // as LogicManager is just a facade class.
        return List.of();
    }


    @Override
    public void handleVaccinationChange(ValueChange<VaxType> change) {
        appointmentManager.handleVaccinationChange(change);
        patientManager.handleVaccinationChange(change);
        updateVaccinationDetail(change);

        IdData<Patient> oldPatient = detailedPatientProperty.get();
        if (oldPatient == null) {
            return;
        }
        IdData<Patient> newPatient = patientManager.getMapView().get(oldPatient.getId());
        ValueChange<IdData<Patient>> patientChange = new ValueChange<>(oldPatient, newPatient);
        updatePatientDetail(patientChange);
    }

    // =========== VaxTypeManager ==============================================================================

    @Override
    public ValueChange<VaxType> addVaccination(VaxType vaxType) throws IllegalValueException {
        vaxTypeManager.add(vaxType);
        ValueChange<VaxType> change = new ValueChange<>(null, vaxType);
        handleVaccinationChange(change);
        return change;
    }


    @Override
    public ValueChange<VaxType> editVaccination(String name, VaxType newValue) throws IllegalValueException {
        ValueChange<VaxType> change = vaxTypeManager.set(name, newValue);
        handleVaccinationChange(change);
        return change;
    }

    @Override
    public ValueChange<VaxType> deleteVaccination(GroupName vaxName) throws IllegalValueException {
        VaxType oldValue = vaxTypeManager.remove(vaxName.toString())
                .orElseThrow(() -> new IllegalValueException(String.format(
                        "Vaccination type does not exist: %s", vaxName.toString())));
        ValueChange<VaxType> change = new ValueChange<>(oldValue, null);
        handleVaccinationChange(change);
        return change;
    }


    @Override
    public void setVaxTypeManager(VaxTypeManager manager) {
        vaxTypeManager.resetData(manager);
    }


    @Override
    public ObjectProperty<VaxType> detailedVaxTypeProperty() {
        return detailedVaxTypeProperty;
    }


    @Override
    public void setDetailedVaxType(VaxType vaxType) {
        detailedVaxTypeProperty.set(vaxType);
    }


    @Override
    public void bindVaccinationDisplayList(ObservableList<VaxType> displayList) {
        this.displayList = displayList;
    }


    @Override
    public VaxType getVaccination(Retriever<String, VaxType> retriever) throws IllegalValueException {
        return retriever.retrieve(vaxTypeManager.asUnmodifiableObservableMap(), displayList);
    }

    // =========== KeywordManager ==============================================================================

    @Override
    public void addKeyword(Keyword keyword) {
        keywordManager.add(keyword);
        updateFilteredKeywordList(PREDICATE_SHOW_ALL_KEYWORDS);
    }

    @Override
    public void deleteKeyword(int id) {
        keywordManager.remove(id);
    }

    @Override
    public KeywordManager getKeywordManager() {
        return keywordManager;
    }

    @Override
    public void setKeywordManager(KeywordManager keywordManager) {
        this.keywordManager.resetData(keywordManager);
    }

    // =========== Filtered Patient List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Patient} backed by the internal list of
     * {@code versionedPatientManager}
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

    @Override
    public void setPatientFilters(Collection<Predicate<Patient>> filters) {
        filteredPatientMap.filter(filters);
    }

    // =========== Filtered VaxType Map Accessors ==============================================================

    @Override
    public ObservableMap<String, VaxType> getFilteredVaxTypeMap() {
        return filteredVaxTypeMap.asUnmodifiableObservableMap();
    }

    @Override
    public void setVaccinationFilters(Collection<Predicate<VaxType>> filters) {
        filteredVaxTypeMap.setFilters(filters);
    }

    @Override
    public VaxTypeManager getVaxTypeManager() {
        return vaxTypeManager;
    }

    // =========== Filtered Appointment Map Accessors ==========================================================

    @Override
    public ObservableMap<Integer, IdData<Appointment>> getFilteredAppointmentMap() {
        return filteredAppointmentMap.asUnmodifiableObservableMap();
    }

    @Override
    public void updateFilteredAppointmentList(Predicate<Appointment> predicate) {
        requireNonNull(predicate);
        filteredAppointmentMap.filter(predicate);
    }

    @Override
    public AppointmentManager getAppointmentManager() {
        return appointmentManager;
    }

    // =========== Filtered Keyword Map Accessors ==========================================================
    @Override
    public ObservableMap<Integer, IdData<Keyword>> getFilteredKeywordList() {
        return filteredKeywordMap.asUnmodifiableObservableMap();
    }

    @Override
    public void updateFilteredKeywordList(Predicate<Keyword> predicate) {
        requireNonNull(predicate);
        filteredKeywordMap.filter(predicate);
    }

    // =========== Misc methods ================================================================================


    private void updateVaccinationDetail(ValueChange<VaxType> change) {
        boolean isUpdated = change.getOldValue()
                .map(oldValue -> oldValue.equals(detailedVaxTypeProperty.get()))
                .orElse(false);
        if (isUpdated || change.getNewValue().isPresent()) {
            detailedVaxTypeProperty.set(change.getNewValue().orElse(null));
        }
    }


    private void updatePatientDetail(ValueChange<IdData<Patient>> change) {
        boolean isUpdated = change.getOldValue()
                .map(oldValue -> oldValue.equals(detailedPatientProperty.get()))
                .orElse(false);
        if (isUpdated || change.getNewValue().isPresent()) {
            detailedPatientProperty.set(change.getNewValue().orElse(null));
        }
    }


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
        return patientManager.equals(other.patientManager)
                && userPrefs.equals(other.userPrefs)
                && filteredPatientMap.asUnmodifiableObservableMap()
                        .equals(other.filteredPatientMap.asUnmodifiableObservableMap());
    }

    @Override
    public String toString() {
        return filteredPatientMap.asUnmodifiableObservableMap().toString();
    }

}
