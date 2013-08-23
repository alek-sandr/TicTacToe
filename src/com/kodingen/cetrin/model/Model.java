package com.kodingen.cetrin.model;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

public class Model {
    private final Collection<IModelSubscriber> subscribers = new CopyOnWriteArrayList<IModelSubscriber>();

    protected void notifySubscribers() {
        for (final IModelSubscriber subscriber : subscribers) {
            notifySubscriber(subscriber);
        }
    }

    private void notifySubscriber(IModelSubscriber subscriber) {
        assert subscriber != null;
        subscriber.modelChanged();
    }

    public void subscribe(IModelSubscriber subscriber) {
        if (subscriber == null) {
            throw new NullPointerException();
        }
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
            notifySubscribers();
        }
    }

    public void unsubscribe(IModelSubscriber subscriber) {
        if (subscriber == null) {
            throw new NullPointerException();
        }
        if (subscribers.contains(subscriber)) {
            subscribers.remove(subscriber);
        }
    }
}
