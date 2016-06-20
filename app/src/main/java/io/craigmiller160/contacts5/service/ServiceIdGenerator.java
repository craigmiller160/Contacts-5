package io.craigmiller160.contacts5.service;


import org.apache.commons.collections4.multimap.HashSetValuedHashMap;

/**
 * Created by craig on 6/20/16.
 */
public class ServiceIdGenerator {

    private final HashSetValuedHashMap<Class<?>,Integer> uniqueIds = new HashSetValuedHashMap<>();

    public ServiceIdGenerator(){}

    public int generateUniqueId(Class<?> serviceType){
        int id = (int) Math.round(Math.random() * 1000);

        if(uniqueIds.containsMapping(serviceType, id)){
            return generateUniqueId(serviceType);
        }
        uniqueIds.put(serviceType, id);
        return id;
    }

}
