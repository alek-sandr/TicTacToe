package com.kodingen.cetrin.view;

import com.kodingen.cetrin.controller.GameController;
import com.kodingen.cetrin.model.GameModel;
import com.kodingen.cetrin.model.IModelSubscriber;

/**
 * Base class for all views. Children classes have access to GameModel(gm) and GameController(controller) fields
 */
public abstract class View implements IModelSubscriber {
    protected GameModel gm;
    protected GameController controller;

    /**
     * Set GameModel for this View
     * @param gm GameModel
     */
    public void setModel(GameModel gm) {
        this.gm = gm;
    }

    /**
     * Set GameController for this View
     * @param gc GameController
     */
    public void setController(GameController gc) {
        controller = gc;
    }

    /**
     * Start new game
     */
    public abstract void start();

    /**
     * Allow player make move
     */
    public abstract void askPlayerForMove();

    /**
     * Show message
     * @param message Message text
     */
    public abstract void showMessage(String message);

    /**
     * Show message and read input
     * @param message Message to show
     * @return player input
     */
    public abstract String askForInput(String message);

    /**
     * Ask for repeat game
     */
    public abstract void askForRepeat();

    /**
     * Reaction on model changing
     */
    @Override
    public abstract void modelChanged();
}
