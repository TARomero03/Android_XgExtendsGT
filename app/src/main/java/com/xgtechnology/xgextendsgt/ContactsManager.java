package com.xgtechnology.xgextendsgt;

/**
 * Created by timr on 11/1/2017.
 */
import com.gotenna.sdk.user.User;
import com.gotenna.sdk.user.UserDataStore;

import java.util.ArrayList;
import java.util.List;
import com.xgtechnology.xgextendsgt.Contact;
public class ContactsManager
{
    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private final ArrayList<Contact> contactArrayList;

    //==============================================================================================
    // Singleton Methods
    //==============================================================================================

    private ContactsManager()
    {
        contactArrayList = new ArrayList<>();

        // Add demo contacts
        contactArrayList.add(new Contact("Alice", 678912313L));
        contactArrayList.add(new Contact("Bob", 4321123123L));
        contactArrayList.add(new Contact("Carol", 8132131231L));
        contactArrayList.add(new Contact("Doug", 5926534234L));
    }

    private static class SingletonHelper
    {
        private static final ContactsManager INSTANCE = new ContactsManager();
    }

    public static ContactsManager getInstance()
    {
        return SingletonHelper.INSTANCE;
    }

    //==============================================================================================
    // Class Instance Methods
    //==============================================================================================

    public List<Contact> getAllDemoContacts()
    {
        return contactArrayList;
    }

    public List<Contact> getDemoContactsExcludingSelf()
    {
        List<Contact> contactsExcludingSelfList = new ArrayList<>();

        User currentUser = UserDataStore.getInstance().getCurrentUser();
        long currentUserGid = currentUser == null ? 0 : currentUser.getGID();

        for (Contact contact : contactArrayList)
        {
            if (contact.getGid() != currentUserGid)
            {
                contactsExcludingSelfList.add(contact);
            }
        }

        return contactsExcludingSelfList;
    }

    public Contact findContactWithGid(long gid)
    {
        for (Contact contact : contactArrayList)
        {
            if (contact.getGid() == gid)
            {
                return contact;
            }
        }

        return null;
    }
}
