package seedu.vms.logic.commands.vaccination;

import java.nio.file.Path;
import java.util.function.Predicate;

import javafx.collections.ObservableMap;
import seedu.vms.commons.core.GuiSettings;
import seedu.vms.commons.exceptions.IllegalValueException;
import seedu.vms.model.IdData;
import seedu.vms.model.Model;
import seedu.vms.model.ReadOnlyUserPrefs;
import seedu.vms.model.appointment.Appointment;
import seedu.vms.model.patient.Patient;
import seedu.vms.model.patient.ReadOnlyAddressBook;
import seedu.vms.model.vaccination.VaxType;
import seedu.vms.model.vaccination.VaxTypeAction;
import seedu.vms.model.vaccination.VaxTypeManager;


/**
 * Stub {@code Model} to test vaccination commands.
 */
public class VaxTypeModelStub implements Model {

    public final VaxTypeManager manager = new VaxTypeManager();

    @Override
    public void setUserPrefs(ReadOnlyUserPrefs userPrefs) {
        throw new UnsupportedOperationException("Unimplemented method 'setUserPrefs'");
    }

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        throw new UnsupportedOperationException("Unimplemented method 'getUserPrefs'");
    }

    @Override
    public GuiSettings getGuiSettings() {
        throw new UnsupportedOperationException("Unimplemented method 'getGuiSettings'");
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        throw new UnsupportedOperationException("Unimplemented method 'setGuiSettings'");
    }

    @Override
    public Path getAddressBookFilePath() {
        throw new UnsupportedOperationException("Unimplemented method 'getAddressBookFilePath'");
    }

    @Override
    public void setAddressBookFilePath(Path addressBookFilePath) {
        throw new UnsupportedOperationException("Unimplemented method 'setAddressBookFilePath'");
    }

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        throw new UnsupportedOperationException("Unimplemented method 'setAddressBook'");
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        throw new UnsupportedOperationException("Unimplemented method 'getAddressBook'");
    }

    @Override
    public boolean hasPatient(int id) {
        throw new UnsupportedOperationException("Unimplemented method 'hasPatient'");
    }

    @Override
    public void deletePatient(int id) {
        throw new UnsupportedOperationException("Unimplemented method 'deletePatient'");
    }

    @Override
    public void addPatient(Patient patient) {
        throw new UnsupportedOperationException("Unimplemented method 'addPatient'");
    }

    @Override
    public void setPatient(int id, Patient editedPatient) {
        throw new UnsupportedOperationException("Unimplemented method 'setPatient'");
    }

    @Override
    public ObservableMap<Integer, IdData<Patient>> getFilteredPatientList() {
        throw new UnsupportedOperationException("Unimplemented method 'getFilteredPatientList'");
    }

    @Override
    public ObservableMap<String, VaxType> getFilteredVaxTypeMap() {
        throw new UnsupportedOperationException("Unimplemented method 'getFilteredVaxTypeMap'");
    }

    @Override
    public void updateFilteredPatientList(Predicate<Patient> predicate) {
        throw new UnsupportedOperationException("Unimplemented method 'updateFilteredPatientList'");
    }

    @Override
    public VaxTypeManager getVaxTypeManager() {
        throw new UnsupportedOperationException("Unimplemented method 'getVaxTypeManager'");
    }

    @Override
    public VaxType performVaxTypeAction(VaxTypeAction action) throws IllegalValueException {
        return action.apply(manager);
    }

    @Override
    public void addAppointment(Appointment appointment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addAppointment'");
    }

}