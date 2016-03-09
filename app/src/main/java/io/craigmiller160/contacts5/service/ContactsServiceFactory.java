package io.craigmiller160.contacts5.service;

/**
 * Created by Craig on 2/22/2016.
 */
public class ContactsServiceFactory {

    private static ContactsServiceFactory instance;
    private ContactsService service;

    private ContactsServiceFactory(){}

    public static ContactsServiceFactory getInstance(){
        if(instance == null){
            synchronized (ContactsServiceFactory.class){
                if(instance == null){
                    instance = new ContactsServiceFactory();
                }
            }
        }
        return instance;
    }

    public ContactsService getContactsService(){
        if(service == null){
            synchronized (this){
                if(service == null){
                    service = new DefaultContactsService();
                }
            }
        }
        return service;
    }

}
