/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 *
 * @author shannah
 */
public class Aggregate extends Entity implements Iterable<Entity> {
    private transient final Entity root;
    private transient Set<Entity> entities=new HashSet<>();
    
    private Observer entityObserver = new Observer() {
        @Override
        public void update(Observable o, Object arg) {
            setChanged();
            notifyObservers(arg);
        }
        
    };
    
    public Aggregate(Entity root) {
        this.root = root;
        root.setAggregate(this);
        root.addObserver(entityObserver);
        entities.add(root);
    }
    public Entity getRoot() {
        return root;
    }

    @Override
    public Iterator<Entity> iterator() {
        return entities.iterator();
    }
    
    
    
    public void add(Entity entity) {
        if (entities.add(entity)) {
            entity.setAggregate(this);
            entity.addObserver(entityObserver);
        }
    }
    
    public void remove(Entity entity) {
        if (entities.remove(entity)) {
            entity.setAggregate(null);
            entity.deleteObserver(entityObserver);
        }
    }

    @Override
    public synchronized boolean hasChanged() {
        if (!super.hasChanged()) {
            for (Entity e : new ArrayList<>(entities)) {
                if (e.hasChanged()) {
                    return true;
                }
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected synchronized void clearChanged() {
        super.clearChanged();
        for (Entity e : new ArrayList<Entity>(entities)) {
            e.clearChanged();
        }
    }

    @Override
    public void notifyObservers(Object arg) {
        for (Entity e : new ArrayList<Entity>(entities)) {
            e.notifyObservers(arg);
        }
        super.notifyObservers(arg);
    }
    
    
    
    
    

    
    
    
    
    
}
