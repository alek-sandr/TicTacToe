package com.kodingen.cetrin;

public abstract class View implements IModelSubscriber {
    //GameModel model;
    @Override
    public abstract void modelChanged(BaseModel model);

}
