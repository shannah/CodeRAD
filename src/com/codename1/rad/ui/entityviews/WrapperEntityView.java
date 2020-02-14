/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui.entityviews;

import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.models.Entity;
import com.codename1.ui.Component;
import static com.codename1.ui.ComponentSelector.$;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Border;

/**
 *
 * @author shannah
 */
public class WrapperEntityView<T extends Entity> extends AbstractEntityView<T> {
    private Component component;
    private Node viewNode;
    public WrapperEntityView(Component wrapped, T entity, Node viewNode) {
        super(entity);
        this.component = wrapped;
        this.viewNode = viewNode;
        setLayout(new BorderLayout());
        $(this).setMargin(0).setPadding(0).setBorder(Border.createEmpty()).setBgTransparency(0);
        add(BorderLayout.CENTER, wrapped);
    }
    
    @Override
    public void update() {
        
    }

    @Override
    public void commit() {
        
    }

    @Override
    public Node getViewNode() {
        return viewNode;
    }
    
    
}
