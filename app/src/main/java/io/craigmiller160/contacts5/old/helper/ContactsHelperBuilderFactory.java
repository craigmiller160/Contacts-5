package io.craigmiller160.contacts5.old.helper;

import io.craigmiller160.contacts5.old.controller.AddContactController;
import io.craigmiller160.contacts5.old.controller.ContactsActivityController;
import io.craigmiller160.contacts5.old.controller.ContactsInGroupActivityController;
import io.craigmiller160.contacts5.old.model.ContactsModel;

import static io.craigmiller160.contacts5.old.helper.ContactsHelper.*;

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

            helper.addController(CONTACTS_ACTIVITY_CONTROLLER,
                    new ContactsActivityController());
            helper.addController(ADD_CONTACT_CONTROLLER,
                    new AddContactController());
            helper.addController(CONTACTS_IN_GROUP_ACTIVITY_CONTROLLER,
                    new ContactsInGroupActivityController());

            ((AbstractHelper) helper).lockModels();
            ((AbstractHelper) helper).lockControllers();

            return helper;
        }
    }
}
