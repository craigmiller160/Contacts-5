package io.craigmiller160.contacts5.helper;

import io.craigmiller160.contacts5.controller.AddContactController;
import io.craigmiller160.contacts5.controller.ContactsActivityController;
import io.craigmiller160.contacts5.model.ContactsModel;

/**
 * Created by Craig on 2/14/2016.
 */
public class ContactsHelperBuilderFactory implements HelperBuilderFactory {

    private static HelperBuilderFactory instance;

    public static HelperBuilderFactory getInstance(){
        if(instance == null){
            synchronized (HelperBuilderFactory.class){
                if(instance == null){
                    instance = new ContactsHelperBuilderFactory();
                }
            }
        }
        return instance;
    }

    @Override
    public HelperBuilder newHelperBuilder() {
        return new ContactsHelperBuilder();
    }

    private class ContactsHelperBuilder implements HelperBuilder{

        @Override
        public Helper buildNewHelper() {
            Helper helper = new ContactsHelper();
            helper.addModel(new ContactsModel());

            helper.addController(ContactsHelper.CONTACTS_ACTIVITY_CONTROLLER,
                    new ContactsActivityController());
            helper.addController(ContactsHelper.ADD_CONTACT_CONTROLLER,
                    new AddContactController());

            ((AbstractHelper) helper).lockModels();
            ((AbstractHelper) helper).lockControllers();

            return helper;
        }
    }
}
