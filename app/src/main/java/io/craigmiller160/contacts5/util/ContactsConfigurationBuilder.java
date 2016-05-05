package io.craigmiller160.contacts5.util;

import io.craigmiller160.locus.util.LocusConfiguration;

/**
 * A utility class to encapsulate building
 * the Locus Framework configuration for this
 * application.
 *
 * Created by craig on 5/4/16.
 */
public class ContactsConfigurationBuilder {

    public static LocusConfiguration buildConfiguration(){
        LocusConfiguration config = new LocusConfiguration();

        config.addClassName("io.craigmiller160.contacts5.model.DisplayPrefsModel");

        return config;
    }

}
