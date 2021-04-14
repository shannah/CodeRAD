/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;

import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.entityviews.EntityListView;
import com.codename1.rad.models.Entity;

/**
 * Interface used by {@link EntityListView} for rendering its rows.
 * @author shannah
 */
public interface EntityListCellRenderer {
    public EntityView getListCellRendererComponent(
            EntityListView list, 
            Entity value,
            int index, 
            boolean isSelected, 
            boolean isFocused
    );
  
}
