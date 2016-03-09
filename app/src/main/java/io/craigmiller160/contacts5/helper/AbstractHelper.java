package io.craigmiller160.contacts5.helper;

import android.os.Handler;
import android.os.Looper;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.craigmiller160.contacts5.controller.AbstractController;
import io.craigmiller160.contacts5.model.AbstractModel;
import io.craigmiller160.contacts5.view.MVPView;

/**
 * Created by Craig on 2/13/2016.
 */
public abstract class AbstractHelper implements Helper, PropertyChangeListener{

    //TODO document how Helpers are not thread-safe

    private Set<AbstractModel> registeredModels;
    private Set<MVPView> registeredViews;
    private Map<String,AbstractController> registeredControllers;

    protected AbstractHelper(){
        registeredModels = new HashSet<>();
        registeredControllers = new HashMap<>();
        registeredViews = new HashSet<>();
    }

    @Override
    public void addModel(AbstractModel model) {
        model.addPropertyChangeListener(this);
        registeredModels.add(model);
    }

    @Override
    public void removeModel(AbstractModel model) {
        model.removePropertyChangeListener(this);
        registeredModels.remove(model);
    }

    //TODO document how lock methods ensure thread safety

    public void lockModels(){
        registeredModels = Collections.unmodifiableSet(registeredModels);
    }

    public void lockControllers(){
        registeredControllers = Collections.unmodifiableMap(registeredControllers);
    }

    @Override
    public Collection<AbstractModel> getModels() {
        return registeredModels;
    }

    @Override
    public void addController(String controllerName, AbstractController controller) {
        controller.setHelper(this);
        registeredControllers.put(controllerName, controller);
    }

    @Override
    public void removeController(String controllerName) {
        registeredControllers.remove(controllerName);
    }

    @Override
    public Collection<AbstractController> getControllers() {
        return registeredControllers.values();
    }

    @Override
    public Collection<String> getControllerNames() {
        return registeredControllers.keySet();
    }

    @Override
    public AbstractController getController(String controllerName) {
        return registeredControllers.get(controllerName);
    }

    //TODO consider if synchronization is enough for getViews()

    @Override
    public synchronized Collection<MVPView> getViews() {
        return new ArrayList<>(registeredViews);
    }

    @Override
    public synchronized void removeView(MVPView view) {
        registeredViews.remove(view);
    }

    @Override
    public synchronized void addView(MVPView view) {
        registeredViews.add(view);
        System.out.println("Adding View: " + view.getClass().getName() + " Size: " + registeredViews.size()); //TODO delete this
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if(Looper.myLooper() != Looper.getMainLooper()){
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    updateView(event);
                }
            });
        }
        else {
            updateView(event);
        }
    }

    private void updateView(PropertyChangeEvent event){
        Collection<MVPView> views = getViews();
        for(MVPView view : views){
            view.updateView(event);
        }
    }
}
