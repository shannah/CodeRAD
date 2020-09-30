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
package com.codename1.rad.io;

import com.codename1.io.Util;
import com.codename1.l10n.DateFormat;
import com.codename1.xml.Element;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Vector;

/**
 * A query selector similar to the {@link com.codename1.ui.ComponentSelector} for working with XML documents.
 * @author shannah
 */
public class ElementSelector implements Iterable<Element>, Set<Element>{
    private Set<ElementSelector> aggregateSelectors;
    private Set<Element> roots;
    private ElementSelector parent;
    private boolean childrenOnly;
    private String[] classes;
    private String[] classNeedles;
    private String id;
    private String tagName;
    private Set<Element> results;
    private ArrayList<AttributeFilters> allAttributeFilters;
    private AttributeFilters myAttributeFilters;
    
    /**
     * Creates an element selector with the given selector on the provided roots.
     * @param selector The selector.
     * @param roots The roots
     */
    public ElementSelector(String selector, Set<Element> roots) {
        this(selector, roots, null);
    }
    
    private ElementSelector(String selector, Set<Element> roots, ArrayList<AttributeFilters> allAttributeFilters) {
        this.roots = new LinkedHashSet<Element>();
        this.roots.addAll(roots);
        this.allAttributeFilters = allAttributeFilters;
        parse(selector);
        
    }
    
    /**
     * Creates a component selector that wraps the provided components.  The provided 
     * components are treated as the "results" of this selector.  Not the roots.  However
     * you can use {@link #find(java.lang.String) } to perform a query using this selector
     * as the roots.
     * @param cmps Components to add to this selector results.
     */
    public ElementSelector(Set<Element> cmps) {
        this.roots = new LinkedHashSet<Element>();
        this.results = new LinkedHashSet<Element>();
        this.results.addAll(cmps);

    }
    
    /**
     * Creates a element selector that wraps the provided elements.  The provided 
     * components are treated as the "results" of this selector.  Not the roots.  However
     * you can use {@link #find(java.lang.String) } to perform a query using this selector
     * as the roots.
     * @param els Components to add to this selector results.
     */
    public ElementSelector(Element... els) {
        this.roots = new LinkedHashSet<Element>();
        this.results = new LinkedHashSet<Element>();
        for (Element cmp : els) {
            this.results.add(cmp);
        }
    }
    
    /**
     * Creates a selector with the provided roots.  This will only search through the subtrees
     * of the provided roots to find results that match the provided selector string.
     * @param selector The selector string
     * @param roots The roots for this selector.
     */
    public ElementSelector(String selector, Element... roots) {
        this.roots = new LinkedHashSet<Element>();
        for (Element root : roots) {
            this.roots.add(root);
        }
        parse(selector);
    }
    
    /**
     * Creates a selector with the provided roots.  This will only search through the subtrees
     * of the provided roots to find results that match the provided selector string.
     * @param selector The selector string
     * @param roots The roots for this selector.
     */
    public ElementSelector(String selector, Collection<Element> roots) {
        this.roots = new LinkedHashSet<Element>();
        this.roots.addAll(roots);
        parse(selector);
    }

    

    /**
     * Retains only elements of the result set that are contained in the provided collection.
     * @param c
     * @return 
     */
    public boolean retainAll(Collection<?> c) {
        setDirty();
        return resultsImpl().retainAll(c);
    }
    
    

    /**
     * Removes all of the components in the provided collection from the result set.
     * @param c
     * @return 
     */
    public boolean removeAll(Collection<?> c) {
        setDirty();
        return resultsImpl().removeAll(c);
    }

    @Override
    /**
     * Clears the result set.
     */
    public void clear() {
        setDirty();
        resultsImpl().clear();
    }
    
    private class AttributeFilters {
        private List<AttributeFilter> filters = new ArrayList<>();
        private List<String> logicalConnectors = new ArrayList<>();
        
        boolean match(Element el) {
            boolean[] results = new boolean[filters.size()];
            int i =0;
            for (AttributeFilter f : filters) {
                results[i++] = f.match(el);
            }
            boolean[] stack = new boolean[results.length];
            int stackPtr = 0;
            int pos = 0;
            stack[stackPtr++] = results[pos++];
            for (String conn : logicalConnectors) {
                if ("and".equals(conn)) {
                    stack[stackPtr++] = results[pos];
                } else if ("or".equals(conn)){
                    boolean lastTermResult = true;
                    while (stackPtr-- > 0) {
                        if (!stack[stackPtr]) {
                            lastTermResult = false;
                        }
                    }
                    stackPtr++;
                    if (lastTermResult) {
                        return true;
                    }
                    stack[stackPtr++] = results[pos];
                    
                } else {
                    throw new RuntimeException("Illegal logical connector "+conn);
                }
                pos++;
            }
            boolean lastTermResult = true;
            while (stackPtr-- > 0) {
                if (!stack[stackPtr]) {
                    lastTermResult = false;
                }
            }
            stackPtr++;
            return lastTermResult;
                
            
            
        }
        
        int parse(String selector, int startPos) throws IOException {
            int len = selector.length();
            int pos = startPos;
            while (pos < len) {
                AttributeFilter filter = new AttributeFilter();
                int endPos = filter.parse(selector, pos);
                if (pos == endPos) {
                    break;
                }
                filters.add(filter);
                while (endPos < len && selector.charAt(endPos) == ' ') {
                    endPos++;
                }
                if (endPos < len-2 && selector.substring(endPos,endPos+2).equalsIgnoreCase("OR")) {
                    logicalConnectors.add("or");
                    endPos+=2;
                    
                } else if (endPos < len-2 && selector.substring(endPos, endPos+3).equalsIgnoreCase("AND")) {
                    logicalConnectors.add("and");
                    endPos+=3;
                    
                } else if (endPos < len && selector.charAt(endPos) == ']') {
                    pos = endPos+1;
                    break;
                }
                
                pos = endPos;
            }
            return pos;
        }
    }
    
    private class AttributeFilter {
        private String attributeName;
        private String attributeValue;
        private String comparator;
        
        boolean match(Element el) {
            if ("=".equals(comparator)) {
                return Objects.equals(el.getAttribute(attributeName), attributeValue);
            }
            if ("!=".equals(comparator)) {
                return !Objects.equals(el.getAttribute(attributeName), attributeValue);
            }
            if (">".equals(comparator)) {
                double val1 = 0;
                double val2 = 0;
                try {
                    String val = el.getAttribute(attributeName);
                    if (val != null) {
                        val1 = Double.parseDouble(val);
                    }
                } catch (Throwable t){}
                try {
                    String val = attributeValue;
                    if (val != null) {
                        val2 = Double.parseDouble(val);
                    }
                } catch (Throwable t){}
                
                return val1 > val2;
                
            }
            if ("<".equals(comparator)) {
                double val1 = 0;
                double val2 = 0;
                try {
                    String val = el.getAttribute(attributeName);
                    if (val != null) {
                        val1 = Double.parseDouble(val);
                    }
                } catch (Throwable t){}
                try {
                    String val = attributeValue;
                    if (val != null) {
                        val2 = Double.parseDouble(val);
                    }
                } catch (Throwable t){}
                
                return val1 < val2;
                
            }
            
            if (">=".equals(comparator)) {
                double val1 = 0;
                double val2 = 0;
                try {
                    String val = el.getAttribute(attributeName);
                    if (val != null) {
                        val1 = Double.parseDouble(val);
                    }
                } catch (Throwable t){}
                try {
                    String val = attributeValue;
                    if (val != null) {
                        val2 = Double.parseDouble(val);
                    }
                } catch (Throwable t){}
                
                return val1 >= val2;
                
            }
            
            if ("<=".equals(comparator)) {
                double val1 = 0;
                double val2 = 0;
                try {
                    String val = el.getAttribute(attributeName);
                    if (val != null) {
                        val1 = Double.parseDouble(val);
                    }
                } catch (Throwable t){}
                try {
                    String val = attributeValue;
                    if (val != null) {
                        val2 = Double.parseDouble(val);
                    }
                } catch (Throwable t){}
                
                return val1 <= val2;
                
            }
            throw new RuntimeException("Unrecognized comparator "+comparator);
        }
        
        int parse(String selector, int startPos) throws IOException {
            int len = selector.length();
            int pos = startPos;
            StringBuilder name = new StringBuilder();
            StringBuilder val = new StringBuilder();
            StringBuilder comparator = new StringBuilder();
            int state = 0;
            char quote = 0;
            while (pos < len) {
                char c = selector.charAt(pos);
                if (state == 0) {
                    // Parsing the name
                    if (c == '[') {
                        pos++;
                        continue;
                    }
                    if (c == '=' || c == '!' || c == '<' || c == '>') {
                        comparator.append(c);
                        pos++;
                        state = 1;
                        continue;
                    }
                    if (c == ' ') {
                        if (name.length() > 0) {
                            state = 1;
                            pos++;
                            continue;
                        } else {
                            pos++;
                            continue;
                        }
                    }
                    name.append(c);
                    pos++;
                    continue;
                        
                }
                if (state == 1) {
                    // Parsing the comparator
                    if (c == '=' || c == '!' || c == '<' || c == '>') {
                        comparator.append(c);
                        pos++;
                        continue;
                    }
                    if (c == ' ') {
                        if (comparator.length() > 0) {
                            state = 2;
                            pos++;
                            continue;
                        } else {
                            pos++;
                            continue;
                        }
                    }
                    
                    state = 2;
                }
                if (state == 2) {
                    // Parsing the value.
                    if (c == '"' || c == '\'') {
                        if (quote == 0) {
                            quote = c;
                            pos++;
                            continue;
                        } else if (quote != c) {
                            val.append(c);
                            pos++;
                            continue;
                            
                        } else {
                            // This is a closing quote
                            // We're done
                            // We don't support escaping quotes
                            quote = 0;
                            pos++;
                            break;
                            
                        }
                    }
                    if (c == ' ' || c == ']') {
                        if (quote == 0) {
                            // Space... and no quote.  We're done.
                            
                            if (c != ']') pos++; // We don't want to advance position to the closing bracket
                                                 // because it will screw up compound expression parsing.
                            break;
                        } else {
                            val.append(c);
                            pos++;
                            continue;
                        }
                    }
                    
                    val.append(c);
                    pos++;
                }
            }
            if (quote != 0) {
                throw new IOException("Unclosed quote parsing selector at "+selector.substring(startPos));
            }
            this.attributeName = name.toString();
            this.comparator = comparator.toString();
            this.attributeValue = val.toString();
            return pos;
        }
    }
    
    
    
    private void initAllAttributeFilters() {
        if (allAttributeFilters == null) {
            allAttributeFilters = new ArrayList<AttributeFilters>();
        }
    }
    
    private void parse(String selector) {
        selector = selector.trim();
        int pos;
        ArrayList<AttributeFilters> allFilters = null;
        if (selector.indexOf("[") != -1) {
            initAllAttributeFilters();
            allFilters = allAttributeFilters;
        }
        
        while ((pos =selector.indexOf("[")) != -1) {
            
            // We need to extract all of the parameter queries
            AttributeFilters attFilters = new AttributeFilters();
            try {
                int endPos = attFilters.parse(selector, pos+1);
                int filterNum = allFilters.size();
                allFilters.add(attFilters);
                selector = selector.substring(0, pos) + "{"+filterNum+"}" + selector.substring(endPos);
            } catch (IOException ex) {
                throw new RuntimeException("Failed to parse selector "+selector+".  "+ex.getMessage(), ex);
            }
        }
        
        if (selector.indexOf(",") != -1) {
            // this is an aggregate selector
            String[] parts = Util.split(selector, ",");
            
            aggregateSelectors = new LinkedHashSet<ElementSelector>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() == 0) {
                    continue;
                }
                aggregateSelectors.add(new ElementSelector(part, roots, allAttributeFilters));
            }
            return;
            
        }
        
        String[] parts = Util.split(selector, " ");
        int len = parts.length;
        if (len > 1) {
            StringBuilder parentSelector = new StringBuilder();
            for (int i=0; i<len; i++) {
                if (">".equals(parts[i])) {
                    if (i < len-1) {
                        parts[i] = ">" + parts[i+1].trim();
                        for (int j=i+1; j<len-1; j++) {
                            parts[j] = parts[j+1];
                        }
                        len--;
                        parts[len] = null;
                    } else {
                        throw new IllegalArgumentException("Failed to parse selector.  Selector cannot end with '>'");
                    }
                }
                if (i>0 && i < len-1) {
                    parentSelector.append(" ");
                }
                if (i < len-1) {
                    parentSelector.append(parts[i]);
                }
                if (i == len-1) {
                    selector = parts[i];
                }
            }
            if (parentSelector.length() > 0) {
                parent = new ElementSelector(parentSelector.toString(), roots, allAttributeFilters);
                roots.clear();
            }
        }
        
       
        
        if (selector.indexOf(">") == 0) {
            childrenOnly = true;
            selector = selector.substring(1).trim();
        }

        if (selector.indexOf(",") != -1) {
            throw new IllegalArgumentException("Invalid character in selector "+selector);
        } else {
            ElementSelector out = this;
            selector = selector.trim();


            if ((pos = selector.indexOf("{")) != -1) {
                int endBracePos = selector.indexOf("}", pos+1);
                if (endBracePos == -1) {
                    throw new RuntimeException("Syntax error in selector "+selector+". No closing brace found for opening brace at "+selector.substring(pos));
                }
                int filterInt = Integer.parseInt(selector.substring(pos+1, endBracePos));
                myAttributeFilters = allAttributeFilters.get(filterInt);
                selector = selector.substring(0, pos) + selector.substring(endBracePos+1);
            }
            if ((pos = selector.indexOf(".")) != -1) {
                out.classes = Util.split(selector.substring(pos+1), ".");
                len = out.classes.length;
                out.classNeedles = new String[len];
                String[] needles = out.classNeedles;
                String[] tags = out.classes;
                for (int i=0; i<len; i++) {
                    needles[i] = " " + tags[i] + " "; // Will make it easier to match against components' tags.
                }
                selector = selector.substring(0, pos);
            }
            if ((pos = selector.indexOf("#")) >= 0) {
                out.id = selector.substring(pos+1);
                selector = selector.substring(0, pos);
            }
            if (selector.length() > 0 && !"*".equals(selector)) {
                out.tagName = selector;
            }
            
            //return out;

        }

    }

    @Override
    public Iterator<Element> iterator() {
        return resultsImpl().iterator();
    }
    
    private Set<Element> resultsImpl() {
        if (results == null) {
            results = new LinkedHashSet<Element>();
            
            if (aggregateSelectors != null) {
                for (ElementSelector sel : aggregateSelectors) {
                    results.addAll(sel.resultsImpl());
                }
                return results;
            }
            
            if (parent != null) {
                roots.clear();
                roots.addAll(parent.resultsImpl());
            }
            
            for (Element root : roots) {
                if (childrenOnly) {
                    if (!root.isTextElement() && !root.isEmpty()) {
                        for (Element child : root) {
                            if (match(child)) {
                                results.add(child);
                            }
                        }
                    }
                } else {
                    if (!root.isTextElement() && !root.isEmpty()) {
                        for (Element child : root) {
                            resultsImpl(results, child);
                        }
                    }
                }
            }
        }
        return results;
    }
    
    
    private Set<Element> resultsImpl(Set<Element> out, Element root) {
        if (match(root)) {
            out.add(root);
        }
        
        if (!root.isTextElement() && !root.isEmpty()) {
            for (Element child : root) {
                resultsImpl(out, child);
            }
        }
        return out;
    }
    
    private boolean match(Element c) {

        if (myAttributeFilters != null) {
            if (!myAttributeFilters.match(c)) {
                return false;
            }
        }
        
        if (id != null && !id.equalsIgnoreCase(c.getAttribute("id"))) {
            return false;
        }
        if (tagName != null && !tagName.equalsIgnoreCase(c.getTagName())) {
            return false;
        }
        if (classes != null) {
            String ctags = (String)c.getAttribute("class");

            if (ctags != null) {
                for (String ctag : classNeedles) {
                    if (ctags.indexOf(ctag) == -1) {
                        return false;
                    }
                }
            } else {
                return false;
            }
        }

        return true;


    }
    
    /**
     * Returns number of results found.
     * @return 
     */
    public int size() {
        return resultsImpl().size();
    }

    /**
     * 
     * @return True if there were no results.
     */
    public boolean isEmpty() {
        return resultsImpl().isEmpty();
    }

    /**
     * Checks if an object is contained in result set.
     * @param o
     * @return 
     */
    public boolean contains(Object o) {
        return resultsImpl().contains(o);
    }

    /**
     * Returns results as an array.
     * @return 
     */
    public Object[] toArray() {
        return resultsImpl().toArray();
    }

    /**
     * Returns results as an array.
     * @param <T>
     * @param a
     * @return 
     */
    public <T> T[] toArray(T[] a) {
        return resultsImpl().toArray(a);
    }

    /**
     * Explicitly adds a component to the result set.
     * @param e
     * @return True on success
     */
    public boolean add(Element e) {
        setDirty();
        return resultsImpl().add(e);
    }
    
    private void setDirty() {
        
    }

    /**
     * Appends a child component to the first container in this set.  Same as calling
     * {@link Container#add(com.codename1.ui.Component) } padding {@literal child} on first container
     * in this set.
     * @param child Component to add to container.
     * @return Self for chaining.
     */
    public ElementSelector append(Element child) {
        for (Element c : this) {
            if (!(c.isTextElement())) {
                c.addChild(child);
                return this;
            }
        }
        return this;
    }
    
    /**
     * Appends a child component to the first container in this set.  Same as calling
     * {@link Container#add(java.lang.Object, com.codename1.ui.Component) } padding {@literal child} on first container
     * in this set.
     * @param constraint
     * @param child
     * @return 
     */
    public ElementSelector append(Object constraint, Element child) {
        for (Element c : this) {
            if (!(c.isTextElement())) {
                c.addChild(child);
                return this;
            }
        }
        return this;
    }
    
    public ElementSelector setAttribute(String key, String value) {
        for (Element c : this) {
            if (!(c.isTextElement())) {
                c.setAttribute(key, value);
            }
        }
        return this;
    }
    
    /**
     * Gets an attribute value from the first item in this found set.
     * @param key
     * @return 
     */
    public String getAttribute(String key) {
        for (Element c : this) {
            if (!(c.isTextElement())) {
                return c.getAttribute(key);
            }
        }
        return null;
    }
    
    public ElementSelector setString(String key, String value) {
        int pos;
        if ((pos = key.indexOf("/")) != -1) {
            return new ElementSelector(key.substring(0, pos), this).setString(key.substring(pos+1), value);
        }
        if (key.startsWith("@")) {
            setAttribute(key.substring(1), value);
        } else {
            for (Element el : new ElementSelector(key, this)) {
                if (el.isTextElement()) {
                    el.setText(value);
                } else {
                    while (!el.isEmpty()) {
                        el.removeChildAt(0);
                    }
                    el.addChild(new Element(value, true));
                }
            }
        }
        return this;
    }
    
    public ElementSelector setInt(String key, int value) {
        return setString(key, String.valueOf(value));
    }
    
    public ElementSelector setDouble(String key, double value) {
        return setString(key, String.valueOf(value));
    }
    
    public ElementSelector setBoolean(String key, boolean value) {
        return setString(key, value ? "true" : "false");
    }
    
    public ElementSelector setDate(String key, Date date, DateFormat fmt) {
        return setString(key, fmt.format(date));
    }
    
    /**
     * Gets a string value on this selector.  This uses a notation that allows you to target
     * the current result, or a subselection.
     * @param key The key.  E.g. "@attname" for attribute.  "tagname" for subtag.  "subselector/@attname", "subselector/tagname"
     * @param defaultValue The default value if no value is found.
     * @return 
     */
    public String getString(String key, String defaultValue) {
        int pos;
        if ((pos = key.indexOf("/")) != -1) {
            return new ElementSelector(key.substring(0, pos), this).getString(key.substring(pos+1), defaultValue);
        }
        if (key.startsWith("@")) {
            String out = getAttribute(key.substring(1));
            
            if (out == null) {
                return defaultValue;
            }
            return out;
        }
        for (Element el : new ElementSelector(key, this)) {
            if (el.isTextElement()) {
                    return el.getText();
            } else if (el.getNumChildren() == 0) {
                    return "";
            } else if (el.getChildAt(0).isTextElement()){
                    return el.getChildAt(0).getText();
            } else {
                    return el.toString();
            }
        }
        
        return defaultValue;
        
    }
    
    /**
     * Gets an int value on this selector.  This uses a notation that allows you to target
     * the current result, or a subselection.
     * @param key The key.  E.g. "@attname" for attribute.  "tagname" for subtag.  "subselector/@attname", "subselector/tagname"
     * @param defaultValue The default value if no value is found.
     * @return 
     */
    public int getInt(String key, int defaultValue) {
        
        String str = getString(key, null);
        if (str == null) {
            return defaultValue;
        }
        try {
            return (int)Double.parseDouble(str);
        } catch (Throwable t) {
            return defaultValue;
        }
        
    }
    
    /**
     * Gets a double value on this selector.  This uses a notation that allows you to target
     * the current result, or a subselection.
     * @param key The key.  E.g. "@attname" for attribute.  "tagname" for subtag.  "subselector/@attname", "subselector/tagname"
     * @param defaultValue The default value if no value is found.
     * @return 
     */
    public double getDouble(String key, double defaultValue) {
        
        String str = getString(key, null);
        if (str == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(str);
        } catch (Throwable t) {
            return defaultValue;
        }
        
    }
    
    /**
     * Gets a boolean value on this selector.  This uses a notation that allows you to target
     * the current result, or a subselection.
     * @param key The key.  E.g. "@attname" for attribute.  "tagname" for subtag.  "subselector/@attname", "subselector/tagname"
     * @param defaultValue The default value if no value is found.
     * @return 
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        
        String str = getString(key, null);
        if (str == null) {
            return defaultValue;
        }
        return "true".equalsIgnoreCase(str) || "1".equalsIgnoreCase(str);
        
    }
    
    /**
     * Gets a date value on this selector.  This uses a notation that allows you to target
     * the current result, or a subselection.
     * @param key The key.  E.g. "@attname" for attribute.  "tagname" for subtag.  "subselector/@attname", "subselector/tagname"
     * @param formats DateFormats to attempt to parse date with.
     * @return 
     */
    public Date getDate(String key, DateFormat... formats) {
        String o = getString(key, null);
        if (o == null) {
            return null;
        }


        String strval = String.valueOf(o);
        if (strval.isEmpty()) {
            return null;
        }
        for (DateFormat fmt : formats) {
            try {
                return fmt.parse(strval);
            } catch (Throwable t){} 
        }
        throw new RuntimeException("Failed to parse key "+key+" value "+o+" using any of the provided date formatters.");
    }
    
   
    
    
    /**
     * Explicitly removes a component from the result set.
     * @param o
     * @return Self for chaining.
     */
    public boolean remove(Object o) {
        setDirty();
        return resultsImpl().remove(o);
    }

    
    
    
    /**
     * Checks if the result set contains all of the components found in the provided
     * collection.
     * @param c
     * @return 
     */
    public boolean containsAll(Collection<?> c) {
        return resultsImpl().containsAll(c);
    }

    /**
     * Adds all components in the given collection to the result set.
     * @param c
     * @return 
     */
    public boolean addAll(Collection<? extends Element> c) {
        setDirty();
        return resultsImpl().addAll(c);
    }
    
    public ElementSelector getChildAt(int index) {
        LinkedHashSet<Element> out = new LinkedHashSet<>();
        for (Element el : this) {
            if (el.getNumChildren() > index) {
                out.add(el.getChildAt(index));
            }
            
        }
        return new ElementSelector(out);
        
    }
    
    public ElementSelector getParent() {
        LinkedHashSet<Element> out = new LinkedHashSet<>();
        for (Element el : this) {
            Element parent = el.getParent();
            if (parent != null) {
                out.add(parent);
            }
            
            
        }
        return new ElementSelector(out);
        
    }
    
    public int getChildIndex(Element child) {
        for (Element el : this) {
            int index = el.getChildIndex(child);
            if (index >= 0) {
                return index;
            }
            
        }
        return -1;
    }
    
    public ElementSelector getChildrenByTagName(String name) {
        LinkedHashSet<Element> out = new LinkedHashSet<>();
        for (Element el : this) {
            Vector v = el.getChildrenByTagName(name);
            if (v != null) {
                for (Element child : (List<Element>)v) {
                    out.add(child);
                }
            }
            
        }
        return new ElementSelector(out);
    }
    
    
    
    public ElementSelector getDescendantsByTagName(String name, int depth) {
        LinkedHashSet<Element> out = new LinkedHashSet<>();
        for (Element el : this) {
            Vector v = el.getDescendantsByTagName(name, depth);
            if (v != null) {
                for (Element child : (List<Element>)v) {
                    out.add(child);
                }
            }
            
        }
        return new ElementSelector(out);
    }
    
    public ElementSelector getDescendantsByTagName(String name) {
        LinkedHashSet<Element> out = new LinkedHashSet<>();
        for (Element el : this) {
            Vector v = el.getDescendantsByTagName(name);
            if (v != null) {
                for (Element child : (List<Element>)v) {
                    out.add(child);
                }
            }
            
            
            
        }
        return new ElementSelector(out);
    }
    
    public ElementSelector getDescendantsByTagNameAndAttribute(String name, String tagName, int depth) {
        LinkedHashSet<Element> out = new LinkedHashSet<>();
        for (Element el : this) {
            Vector v = el.getDescendantsByTagNameAndAttribute(name, tagName, depth);
            if (v != null) {
                for (Element child : (List<Element>)v) {
                    out.add(child);
                }
            }
            
            
        }
        return new ElementSelector(out);
    }
    
    public ElementSelector getElementById(String id) {
        LinkedHashSet<Element> out = new LinkedHashSet<>();
        for (Element el : this) {
            Element match = el.getElementById(id);
            if (match != null) {
                out.add(match);
            }
            
            
        }
        return new ElementSelector(out);
    }
    
    public ElementSelector getFirstChildByTagName(String name) {
        LinkedHashSet<Element> out = new LinkedHashSet<>();
        for (Element el : this) {
            Element match = el.getFirstChildByTagName(name);
            if (match != null) {
                out.add(match);
            }
            
            
        }
        return new ElementSelector(out);
    }
    
    public int getNumChildren() {
        for (Element el : this) {
            return el.getNumChildren();
            
        }
        
        return 0;
    }
    
    
    public String getText() {
        for (Element el : this) {
            return el.getText();
            
        }
        
        return null;
    }
    
    public boolean hasTextChild() {
        for (Element e : this) {
            if (e.hasTextChild()) {
                return true;
            }
            
        }
        return false;
    }
    
    public ElementSelector removeAttribute(String attribute) {
        for (Element el : this) {
            el.removeAttribute(attribute);
            
        }
        return this;
    }
    
    
    public ElementSelector removeChildAt(int indx) {
        for (Element el : this) {
            if (el.getNumChildren() > indx) {
                el.removeChildAt(indx);
            }
        }
        return this;
    }
    
    public ElementSelector find(String selector) {
        return new ElementSelector(selector, this);
    }
    
    
    
}
