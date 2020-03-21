/*
 * Copyright 2020 shannah.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.codename1.rad.ui.entityviews;

import ca.weblite.shared.components.CollapsibleHeaderContainer.ScrollableContainer;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.models.Tags;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.PropertyNode;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.EntityViewFactory;
import com.codename1.rad.ui.UI;
import com.codename1.rad.ui.ViewPropertyParameter;
import static com.codename1.rad.ui.entityviews.EntityListView.SCROLLABLE_Y;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Tabs;
import com.codename1.ui.layouts.BorderLayout;

/**
 *
 * @author shannah
 */
public class TabsEntityView extends AbstractEntityView implements ScrollableContainer {
    private ViewNode node;
    private Tabs tabs;
    
    public TabsEntityView(Entity entity, ViewNode node) {
        super(entity);
        this.node = node;
        initUI();
    }
    
    protected Tabs createTabs() {
        Tabs out = new Tabs();
        out.setTabPlacement(TOP);
        UIID uiid = (UIID)node.findAttribute(UIID.class);
        if (uiid != null) {
            out.setUIID(uiid.getValue());
        }
        
        return out;
    }
    
    private void initUI() {
        tabs = createTabs();
        for (Node n : getViewNode().getChildNodes()) {
            UIID uiid = (UIID)n.findAttribute(UIID.class);
            if (uiid != null) {
                tabs.setTabUIID(uiid.getValue());
            }
            Label l = (Label)n.findAttribute(Label.class);
            if (l == null) {
                continue;
            }
            EntityView content = createTab(n);
            if (content == null) {
                continue;
            }
            tabs.addTab(l.getValue(getEntity()), (Component)content);
        }
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, tabs);
    }
    
    
    private EntityView createTab(Node node) {
        ViewNode vn = (ViewNode)node.as(ViewNode.class);
        if (vn != null) {
            return createViewTab(vn);
            
        }
        ListNode ln = (ListNode)node.as(ListNode.class);
        if (ln != null) {
            return createListTab(ln);
        }
        return null;
    }
    
    private EntityView createListTab(ListNode ln) {
        PropertySelector selector = ln.createPropertySelector(getEntity());
        if (selector == null) {
            return null;
        }
        EntityList tabEntity = selector.getEntityList(null);
        if (tabEntity == null) {
            return null;
        }
        ln.setAttributesIfNotExists(
                UI.param(SCROLLABLE_Y, true)
        );
        return new EntityListView(tabEntity, ln);
    }
    
    private EntityView createViewTab(ViewNode vn) {
       
        
        
        EntityViewFactory factory = vn.getViewFactory(null);
        if (factory == null) {
            return null;
        }
        
        Entity tabEntity = null;
        PropertySelector sel = vn.createPropertySelector(getEntity());
        if (sel == null) {
            return null;
        }
        tabEntity = sel.getEntity(null);
        if (tabEntity == null) {
            return null;
        }
        
        return factory.createView(tabEntity, vn);

        
    }
    
    @Override
    public void update() {
        
    }

    @Override
    public void commit() {
        
    }

    @Override
    public Node getViewNode() {
        return node;
    }

    /**
     * For use with CollapsibleHeaderContainer.  When tabs are used as the body
     * it needs to let the collapsible header container which container us
     * the current scrollable.
     * @return 
     */
    @Override
    public Container getVerticalScroller() {
        Component cmp = tabs.getSelectedComponent();
        if (cmp == null) {
            return null;
        }
        if (cmp instanceof ScrollableContainer) {
            cmp = ((ScrollableContainer)cmp).getVerticalScroller();
        }
        if (cmp instanceof Container) {
            return (Container)cmp;
        }
        return cmp.getParent();
    }
    
    
    
}
