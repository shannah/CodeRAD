/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.nodes.ActionNode.Category;
import com.codename1.rad.models.Property.Name;

/**
 * An interface that keeps a number of commonly used Action categories.
 * @author shannah
 */
public interface ActionCategories {
    /**
     * Actions displayed at top left of either form, section, or field.
     */
    public static final Category TOP_LEFT_MENU = new Category(new Name("ActionCategories.TOP_LEFT_MENU"));
    
    /**
     * Actions displayed at top right of either form, section, or field.
     */
    public static final Category TOP_RIGHT_MENU = new Category(new Name("ActionCategories.TOP_RIGHT_MENU"));

    public static final Category TOP_MENU = new Category(new Name("ActionCategories.TOP_MENU"));
    
    /**
     * Actions displayed at bottom right of either form, section, or field.
     */
    public static final Category BOTTOM_RIGHT_MENU = new Category(new Name("ActionCategories.BOTTOM_RIGHT_MENU"));
    
    /**
     * Actions displayed at bottom left of either form, section, or field.
     */
    public static final Category BOTTOM_LEFT_MENU = new Category(new Name("ActionCategories.BOTTOM_LEFT_MENU"));

    public static final Category BOTTOM_MENU = new Category(new Name("ActionCategories.BOTTOM_MENU"));
    
    /**
     * Actions displayed in overflow menu of form, section, or field.
     */
    public static final Category OVERFLOW_MENU = new Category();
    
    /**
     * Action fired when clicking on an item from a list.
     */
    public static final Category LIST_SELECT_ACTION = new Category();
    public static final Category LIST_REMOVE_ACTION = new Category();
    public static final Category LIST_ADD_ACTION = new Category();
    
    /**
     * Action fired when button is pressed.
     */
    public static final Category BUTTON_ACTION = new Category();
    
    public static final Category LEFT_SWIPE_MENU = new Category();
    
    public static final Category RIGHT_SWIPE_MENU = new Category();
    
    
    
}
