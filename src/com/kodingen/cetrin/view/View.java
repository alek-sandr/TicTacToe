package com.kodingen.cetrin.view;

import com.kodingen.cetrin.controller.GameController;
import com.kodingen.cetrin.model.GameModel;
import com.kodingen.cetrin.model.IModelSubscriber;

public abstract class View implements IModelSubscriber {
    protected GameModel gm;
    protected GameController controller;

    public void setModel(GameModel gm) {
        this.gm = gm;
    }

    public void setController(GameController gc) {
        controller = gc;
    }

    public abstract void start();

    public abstract void showPlayerInputForm();

    public abstract void showMessage(String message);

    public abstract String askForInput(String message);

    public abstract void askForRepeat();

    @Override
    public abstract void modelChanged();
}
