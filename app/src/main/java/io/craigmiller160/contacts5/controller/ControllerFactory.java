package io.craigmiller160.contacts5.controller;

import android.content.Context;

import static io.craigmiller160.contacts5.util.ContactsConstants.ADD_CONTACT_CONTROLLER;
import static io.craigmiller160.contacts5.util.ContactsConstants.DISPLAY_SETTINGS_CONTROLLER;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_CONTACT_CONTROLLER;
import static io.craigmiller160.contacts5.util.ContactsConstants.SELECT_GROUP_CONTROLLER;

/**
 * Created by craig on 5/25/16.
 */
public class ControllerFactory extends AbstractControllerFactory{

    private static ControllerFactory instance;
    private static final Object instanceLock = new Object();

    public ControllerFactory(Context context){
        super(context);
        init();
    }

    private void init(){
        registerControllerType(ADD_CONTACT_CONTROLLER, AddContactController.class);
        registerControllerType(DISPLAY_SETTINGS_CONTROLLER, DisplaySettingsController.class);
        registerControllerType(SELECT_CONTACT_CONTROLLER, SelectContactController.class);
        registerControllerType(SELECT_GROUP_CONTROLLER, SelectGroupController.class);
    }

}
