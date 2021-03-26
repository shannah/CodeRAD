/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

/**
 * Enum specifying an action style.
 * 
 * @author shannah
 * @see ActionStyleAttribute
 * @see ActionNode
 */
public enum ActionStyle {
    
    /**
     * Action should be rendered as an icon only.
     */
    IconOnly,
    
    /**
     * Action should be rendered as text only.
     */
    TextOnly,
    
    /**
     * Action should be rendered with the icon on top.
     */
    IconTop,
    
    /**
     * Action should be rendered with the icon on the bottom.
     */
    IconBottom,
    
    /**
     * Action should be rendered with the icon on the left.
     */
    IconLeft,
    
    /**
     * Action should be rendered with the icon on the right.
     */
    IconRight
}
