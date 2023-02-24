package seedu.vms.model;

import javafx.collections.ObservableMap;
import seedu.vms.model.person.Person;

/**
 * Unmodifiable view of an address book
 */
public interface ReadOnlyAddressBook {

    /**
     * Returns an unmodifiable view of the persons list.
     * This list will not contain any duplicate persons.
     */
    public ObservableMap<Integer, IdData<Person>> getPersonMap();

}