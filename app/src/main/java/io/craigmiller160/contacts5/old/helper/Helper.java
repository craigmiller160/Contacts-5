package io.craigmiller160.contacts5.old.helper;

import java.util.Collection;

import io.craigmiller160.contacts5.old.MVPException;
import io.craigmiller160.contacts5.old.controller.AbstractController;
import io.craigmiller160.contacts5.old.model.AbstractModel;
import io.craigmiller160.contacts5.old.view.MVPView;

/**
 * Created by Craig on 2/13/2016.
 */
public interface Helper {

    //TODO document how this only works with one of each model, and all propNames must be unique

    void addModel(AbstractModel model);

    void removeModel(AbstractModel model);

    Collection<AbstractModel> getModels();

    void addController(String controllerName, AbstractController controller);

    void removeController(String controllerName);

    Collection<AbstractController> getControllers();

    Collection<String> getControllerNames();

    AbstractController getController(String controllerName);

    Object getModelProperty(String propName, Object...params) throws MVPException;

    void setModelProperty(String propName, Object...values) throws MVPException;

    void addView(MVPView view);

    void removeView(MVPView view);

    Collection<MVPView> getViews();

}
