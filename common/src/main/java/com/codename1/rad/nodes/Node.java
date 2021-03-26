/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.nodes;

import com.codename1.rad.attributes.IconUIID;
import com.codename1.rad.ui.Actions;
import com.codename1.rad.ui.NodeList;
import com.codename1.rad.ui.ViewProperty;
import com.codename1.rad.ui.ViewPropertyParameter;
import com.codename1.rad.attributes.NodeDecoratorAttribute;
import com.codename1.rad.attributes.PropertySelectorAttribute;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.attributes.UIIDPrefix;
import com.codename1.rad.attributes.ViewPropertyParameterAttribute;
import com.codename1.rad.nodes.ActionNode.Category;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.AttributeSet;
import com.codename1.rad.models.DateFormatterAttribute;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.NumberFormatterAttribute;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.models.Tags;
import com.codename1.rad.models.TextFormatterAttribute;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A special type of attribute that can contain child nodes.  This is the basis of the UI descriptor hierarchy. 
 * 
 * @author shannah
 */
public abstract class Node<T> extends Attribute<T> {
    
    /**
     * If this node is merely a proxy node for another node...
     */
    private Node<T> proxying;
    /**
     * Node attributes.
     */
    protected final AttributeSet attributes = new AttributeSet();
    protected final NodeList childNodes = new NodeList();
    
    /**
     * View parameters which can be used for setting properties in views.
     */
    protected final Map<ViewProperty,ViewPropertyParameter> viewParameters = new HashMap<>();
    
    protected final Map<ActionNode.Category,ActionsNode> actions = new HashMap<>();
    
    /**
     * Parent node.
     */
    private Node parent;
    
    /**
     * Create a new node with given value and provided attributes.
     * @param value Node value, may be null.
     * @param atts Atrributes to set in the node.
     */
    public Node(T value, Attribute... atts) {
        super(value);
        setAttributes(atts);
    }
    
    /**
     * Create a proxy for this node.  The node class must implement {@link Proxyable}
     * @param parent The parent node of the proxy node.
     * @return The proxy node.
     */
    public Node<T> proxy(Node parent) {
        if (canProxy() && Proxyable.class.isAssignableFrom(this.getClass())) {
            Proxyable orig = (Proxyable)this;
            return (Node<T>)orig.createProxy(parent);
        }
        throw new IllegalStateException("Cannot proxy node that doesn't implement Proxyable interface");
    }
    
    /**
     * Returns true if this node can be proxied.  Default return value is false.  Subclasses
     * that allow proxying should implement the Proxyable interface, and return true
     * for this method.
     * @return 
     */
    public boolean canProxy() {
        return false;
    }
    
    /**
     * Converts this node into a proxy node.
     * @param originalNode 
     */
    public void setProxying(Node<T> originalNode) {
        this.proxying = originalNode;
    }
    
    /**
     * Gets the node that this node proxies.
     * @return 
     */
    public Node<T> getProxying() {
        return proxying;
    }
    
    /**
     * Returns the original node in a proxy chain.  If this node is not proxying any
     * nodes, then it will return itself.
     * @return 
     */
    public Node getCanonicalNode() {
        if (proxying != null) {
            return proxying.getCanonicalNode();
        }
        return this;
    }
    
    /**
     * Gets an iterator for iterating up the proxy chain.  If this is not a proxy node
     * then this will return an empty iterator.
     * @return 
     */
    public Iterator<Node<T>> proxyingIterator() {
        ArrayList<Node<T>> out = new ArrayList<>();
        Node<T> proxy = proxying;
        while (proxy != null) {
            out.add(proxy);
            proxy = proxy.getProxying();
        }
        return out.iterator();
    }
    
    /**
     * Sets the parent node.  You cannot re-assign a non-null parent on a node that
     * already has a non-null parent.  A workaround is to first set parent to null, then
     * set the parent again to the new parent.  Some node types, such as Actions, need
     * to appear in multiple places in the UI tree.  In such cases, a proxy node is created
     * for the action in the various positions of the tree.
     * @param parent 
     */
    public void setParent(Node parent) {
        if (parent != null && this.parent != null && this.parent != parent) {
            throw new IllegalStateException("Cannot reassign parent of node.");
        }
        this.parent = parent;
    }

    /**
     * Returns an attribute of the given type for this node.  If this is a proxy node, 
     * it will first check its own attributes.  If none is found, it will check the
     * node that it is proxying for the attribute.
     * @param <V> The type of attribute to return.
     * @param type The attribute type.
     * @return The attribute, or null, if this node doesn't have an attribute of this type.
     */
    public <V extends Attribute> V findAttribute(Class<V> type) {
        for (Attribute att : attributes) {
            if (att.getClass() == type) {
                return (V)att;
            }
        }
        if (proxying != null) {
            return proxying.findAttribute(type);
        }
        return null;
    }
    
    /**
     * Find an attribute in this node, or a parent node.  This first checks the current 
     * node for the given attribute. If none is found, it will check the parent node.
     * It will walk up the UI tree to the root until it finds an attribute of this type.
     * If none is found, it will check the proxy node, and walk up the tree from there.
     * @param <V> The attribute type to retrieve.
     * @param type The attribute type.
     * @return An attribute of the given type, or null if none found.
     */
    public <V extends Attribute> V findInheritedAttribute(Class<V> type) {
        
        V out = findAttribute(type);
        if (out != null) {
            return out;
        }
        if (parent != null) {
            out = (V)parent.findInheritedAttribute(type);
            if (out != null) {
                return out;
            }
        }
        if (proxying != null) {
            return (V)proxying.findInheritedAttribute(type);
        }
        return null;
        
    }
    
    
    /**
     * Gets a view parameter for this node.  This will walk up the tree until it finds 
     * a parameter for the given view property.  View properties are defined generally inside
     * the View that consumes them.  You can then set values or bindings on these properties
     * in the UI tree using the {@link UI#param()} method.
     * @param <V> The view parameter type
     * @param prop The property to retrieve.
     * @return The property parameter, or null if none found.
     */
    public <V> ViewPropertyParameter<V> getViewParameter(ViewProperty<V> prop) {
        return getViewParameter(true, prop, null);
    }
    
    /**
     * Gets the value of a view parameter.
     * @param <V>
     * @param prop
     * @return 
     */
    public <V> V getViewParameterValue(ViewProperty<V> prop) {
        ViewPropertyParameter<V> param = getViewParameter(prop);
        if (param == null) {
            return null;
        }
        return param.getValue();
    }
    
    /**
     * Gets the value of a view parameter
     * @param <V> The ViewProperty type.
     * @param prop The view property
     * @param defaultValue The default value returned in the case where no such view property is found on this node.
     * @return The view parameter value.
     */
    public <V> V getViewParameterValue(ViewProperty<V> prop, V defaultValue) {
        ViewPropertyParameter<V> param = getViewParameter(prop);
        if (param == null) {
            return defaultValue;
        }
        return param.getValue();
    }
    
    /**
     * Checks if this node has the given view parameter.
     * @param prop
     * @return 
     */
    public boolean hasViewParameter(ViewProperty<?> prop) {
        return getViewParameter(prop) != null;
    }
    
    /**
     * Gets a view parameter for this node.  This will walk up the tree until it finds 
     * a parameter for the given view property.  View properties are defined generally inside
     * the View that consumes them.  You can then set values or bindings on these properties
     * in the UI tree using the {@link UI#param()} method.
     * @param <V> The view parameter type
     * @param checkParent Whether to walk up the tree.
     * @param prop The property to retrieve.
     * @param defaultVal THe default value, if no property found.
     * @return 
     */
    private <V> ViewPropertyParameter<V> getViewParameter(boolean checkParent, ViewProperty<V> prop, ViewPropertyParameter<V> defaultVal) {
        ViewPropertyParameter<V> param =  viewParameters.get(prop);
        if (param == null) {
            if (proxying != null) {
                param = proxying.getViewParameter(false, prop, null);
            }
            if (param == null && checkParent && parent != null) {
                param = parent.getViewParameter(true, prop, null);
            }
            if (param == null && proxying != null && checkParent && proxying.parent != null) {
                param = proxying.parent.getViewParameter(true, prop, null);
            }
            
        }
        
        return param == null ? defaultVal : param;
    }
    
    /**
     * Gets a view parameter for this node.  This will walk up the tree until it finds 
     * a parameter for the given view property.  View properties are defined generally inside
     * the View that consumes them.  You can then set values or bindings on these properties
     * in the UI tree using the {@link UI#param()} method.
     * @param <V> The view parameter type.
     * @param prop The property to retrieve.
     * @param defaultVal Default value in case no property found.
     * @return 
     */
    public <V> ViewPropertyParameter<V> getViewParameter(ViewProperty<V> prop, ViewPropertyParameter<V> defaultVal) {
        return getViewParameter(true, prop, defaultVal);
    }
    
    /**
     * Gets the parent node of this node.
     * @return 
     */
    public Node getParent() {
        return parent;
    }
    
    /**
     * Gets the first ancestor whose class matches the given type.
     * @param <V> 
     * @param type The class
     * @return The first matching ancestor or null
     */
    public <V extends Node> V getAncestor(Class<V> type) {
        if (parent != null && parent.getClass() == type) {
            return (V)parent;
        }
        if (parent != null) {
            return (V)parent.getAncestor(type);
        }
        return null;
    }
    
    public T getValue() {
        if (proxying != null) {
            T out = proxying.getValue();
            if (out != null) {
                return out;
            }
        }
        return super.getValue();
    }
    
   public void setAttributesIfNotExists(Attribute... atts) {
       for (Attribute att : atts) {
           if (findAttribute(att.getClass()) == null) {
               setAttributes(att);
           }
       }
   }
    
    /**
     * Sets attributes on this node.
     * @param atts The attributes to set.
     */
    public void setAttributes(Attribute... atts) {
        for (Attribute att : atts) {
            if (att == null) {
                continue;
            }
            if (att instanceof Node) {
                Node n = (Node)att;
                if (n.parent != null && n.parent != this) {
                    if (n.canProxy()) {
                        n = n.proxy(this);
                    } else {
                        throw new IllegalStateException("Node "+n+" already has parent "+n.parent+".  Cannot be added to "+this);
                    }
                } else {
                    n.parent = this;
                }
                NodeDecoratorAttribute nodeDecorator = (NodeDecoratorAttribute)n.findAttribute(NodeDecoratorAttribute.class);
                if (nodeDecorator != null) {
                    //System.out.println("Decorating node "+n+" with "+nodeDecorator.getValue());
                    nodeDecorator.getValue().decorate(n);
                }
                this.childNodes.add(n);
                
            }
            if (att.getClass() == ViewPropertyParameterAttribute.class) {
                ViewPropertyParameterAttribute valueAtt = (ViewPropertyParameterAttribute)att;
                ViewPropertyParameter val = (ViewPropertyParameter)valueAtt.getValue();
                viewParameters.put(val.getProperty(), val);
            }
            if (att.getClass() == ActionsNode.class) {
                ActionsNode actionsNode = (ActionsNode)att;
                ActionNode.Category category = actionsNode.getCategory();
                if (category != null) {
                    actions.put(category, actionsNode);
                }
            }
        }
        this.attributes.setAttributes(atts);
    }
    
    /**
     * Gets the child nodes of this node.  If this is a proxying node, this will include both the 
     * actual child nodes, and the nodes of the node that this is proxying for.
     * @return 
     */
    public NodeList getChildNodes() {
        NodeList out = new NodeList();
        out.add(this.childNodes);
        if (proxying != null) {
            out.add(proxying.getChildNodes());
        }
        return out;
    }
    
    /**
     * Gets the child nodes of this node that are FieldNode instances.
     * @param tags
     * @return 
     */
    public NodeList getChildFieldNodes(Tags tags) {
        NodeList out = new NodeList();
        NodeList fieldNodes = getChildNodes(FieldNode.class);
        for (Node n : fieldNodes) {
            FieldNode fn = (FieldNode)n;
            if (fn.getTags().intersects(tags)) {
                out.add(fn);
            }
        }
        return out;
    }
    
    /**
     * Gets the child nodes of this node of the given type.
     * @param type
     * @return 
     */
    public NodeList getChildNodes(Class type) {
        NodeList out = new NodeList();
        for (Node n : childNodes) {
            if (n.getClass() == type) {
                out.add(n);
            }
        }
        if (proxying != null) {
            out.add(proxying.getChildNodes(type));
        }
        return out;
    }
    
    /**
     * Gets a child node of this node of the given type.
     * @param <V>
     * @param type
     * @return 
     */
    public <V> V getChildNode(Class<V> type) {
        NodeList n = getChildNodes(type);
        if (n.isEmpty()) {
            return null;
        }
        return (V)n.iterator().next();
    }
    
    
    /**
     * Gets actions on this node in the given category.  This will not include 
     * actions defined in parent nodes.  See {@link #getInheritedActions(com.codename1.rad.nodes.ActionNode.Category) }
     * if you want to also include actions defined in parents.
     * @param category
     * @return 
     */
    public Actions getActions(Category category) {
        return getActions(false, new Actions(), category);
    }
    
    /**
     * Gets an action defined on this node that matches the given category.  This will
     * not check parent nodes for matching actions.  See {@link #getInheritedAction(com.codename1.rad.nodes.ActionNode.Category) }
     * if you want to also check parent nodes for matching actions.
     * @param category
     * @return 
     */
    public ActionNode getAction(Category category) {
        Actions actions = getActions(category);
        if (actions.isEmpty()) {
            return null;
        }
        return actions.iterator().next();
    }
    
    /**
     * Gets actions on this node, and parent node(s) in the given category.
     * @param category 
     * @return 
     */
    public Actions getInheritedActions(Category category) {
        return getActions(true, new Actions(), category);
    }
    
    /**
     * Gets action matching the given category in this node.  If none is found
     * in the current node, it will check the parent node for matches.
     * @param category
     * @return 
     */
    public ActionNode getInheritedAction(Category category) {
        Actions actions = getInheritedActions(category);
        if (actions.isEmpty()) {
            return null;
        }
        return actions.iterator().next();
    }
    
    private Actions getActions(boolean recurse, Actions out, Category category) {
        
        ActionsNode actionsNode = actions.get(category);
        if (actionsNode != null) {
            out.add(actionsNode);
        }
        if (proxying != null) {
            out.add(proxying.getActions(false, out, category));
        }
        if (recurse && parent != null) {
            out.add(parent.getActions(true, out, category));
        }
        if (recurse && proxying != null && proxying.parent != null) {
            out.add(proxying.parent.getActions(recurse, out, category));
        }
        return out;
    }
    
    /**
     * Flattens attributes in provided arrays into a single Attribute[] array.
     * @param arrs
     * @return 
     */
    protected final static Attribute[] merge(Attribute[]... arrs) {
        
        int outerLen = arrs.length;
        int totalLen = 0;
        for (int i=0; i<outerLen; i++) {
            Attribute[] atts = arrs[i];
            int innerLen = atts.length;
            totalLen += innerLen;
        }
        Attribute[] out = new Attribute[totalLen];
        int i = 0;
        for (Attribute[] atts : arrs) {
            for (Attribute att : atts) {
                out[i++] = att;
            }
        }
        return out;
    }
    
    protected final static Attribute[] mergeRecursive(Attribute[]... arrs) {
        LinkedHashMap<Class,Attribute> map = new LinkedHashMap<>();
        for (Attribute[] arr : arrs) {
            for (Attribute att : arr) {
                Attribute existing = map.get(att.getClass());
                if (existing == null) {
                    map.put(att.getClass(), att);
                    continue;
                }
                
                if (existing instanceof Node) {
                    Node existingNode = (Node)existing;
                    Node node = (Node)att;
                    existingNode.mergeAttributes(node, true);
                    
                } else {
                    
                    map.put(att.getClass(), att);
                }
            }
        }
        return map.values().toArray(new Attribute[map.size()]);
        
    }
    
    private void mergeAttributes(Node node, boolean recursive) {
        for (Attribute att : node.attributes) {
            if (attributes.getAttribute(att.getClass()) == null) {
                setAttributes(att);
            } else {
                if (recursive && att instanceof Node) {
                    Node existing = (Node)attributes.getAttribute(att.getClass());
                    Node newNode = (Node)att;
                    existing.mergeAttributes(newNode, recursive);
                } else {
                    setAttributes(att);
                }
            }
            
        }
    }
    
    /**
     * Gets the value of the {@link UIID} attribute on this node.  This will not check the parent node.
     * @param defaultVal Default returned if the node does not contain a UIID attribute.
     * @return 
     */
    public String getUIID(String defaultVal) {
        UIID out = getUIID();
        if (out == null) {
            return defaultVal;
        }
        return out.getValue();
    }
    
    public String getUIID(Entity context, String defaultVal) {
        UIID out = getUIID();
        if (out == null) {
            return defaultVal;
        }
        return out.getValue(context);
    }
    
    /**
     * Gets {@link UIID} attribute of this node.  This will not check the parent node.
     * @return 
     */
    public UIID getUIID() {
        return (UIID)findAttribute(UIID.class);
    }
    
    /**
     * Gets {@link UIIDPrefix} value of this node.  This will not check the parent node.
     * @param defaultVal
     * @return 
     */
    public String getUIIDPrefix(String defaultVal) {
        UIIDPrefix out = getUIIDPrefix();
        if (out == null) {
            return defaultVal;
        }
        return out.getValue();
    }
    
    /**
     * Gets {@link UIIDPrefix} attribute of this node.  This will crawl up the parent hierarchy
     * until it finds a UIIDPrefix setting.
     * @return 
     */
    public UIIDPrefix getUIIDPrefix() {
        return (UIIDPrefix)findInheritedAttribute(UIIDPrefix.class);
    }
    
    /**
     * Gets {@link IconUIID} attribute of this node.  This will NOT check the parent node.
     * @return 
     */
    public IconUIID getIconUIID() {
        return (IconUIID)findAttribute(IconUIID.class);
    }
    
    /**
     * Gets {@link DataFormatterAttribute} of this node.  This will crawl up the parent hierarchy
     * until it finds a date formatter.
     * @return 
     */
    public DateFormatterAttribute getDateFormatter() {
        return findInheritedAttribute(DateFormatterAttribute.class);
        
    }
    
    /**
     * Gets {@link TextFormatterAttribute} of this node.  THis will crawl up the parent hierarchy
     * until it finds a text formatter.
     * @return 
     */
    public TextFormatterAttribute getTextFormatter() {
        return findInheritedAttribute(TextFormatterAttribute.class);
    }
    
    /**
     * Gets {@link NumberFormatterAttribute} of this node.
     * @return 
     */
    public NumberFormatterAttribute getNumberFormatter() {
        return findInheritedAttribute(NumberFormatterAttribute.class);
    }
    
    /**
     * Convenience method that casts a node to the given type.
     * @param <V>
     * @param type
     * @return 
     */
    public <V extends Node> V as(Class<V> type) {
        if (type.isAssignableFrom(this.getClass())) {
            return (V)this;
        }
        return null;
    }
    
    /**
     * Creates a property selector on the given entity using (in order of decreasing precedence):
     * 
     * 1. A {@link PropertyNode} attribute on the node.
     * 2. A {@link Tags} attribute on the node.
     * 3. A {@link PropertySelectorAttribute} on the node.
     * 
     * @param entity The entity on which the property selector should be created.
     * @return The property selector.
     */
    public PropertySelector createPropertySelector(Entity entity) {
        PropertyNode prop = findAttribute(PropertyNode.class);
        if (prop != null) {
            return new PropertySelector(entity, prop.getValue());
        }
        Tags tags = findAttribute(Tags.class);
        if (tags != null) {
            return new PropertySelector(entity, tags.toArray());
        }
        PropertySelectorAttribute att = (PropertySelectorAttribute)findAttribute(PropertySelectorAttribute.class);
        if (att != null) {
            return att.getValue(entity);
        }
        return null;
    }
    
}
