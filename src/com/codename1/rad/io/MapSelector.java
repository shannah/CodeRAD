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

import com.codename1.io.JSONParser;
import com.codename1.io.Util;
import com.codename1.l10n.DateFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author shannah
 */
public class MapSelector implements Iterable<Map> {
    private Set<MapSelector> aggregateSelectors;
    private Set<Map> roots;
    private MapSelector parent;
    private boolean childrenOnly;
    private String[] classes;
    private String[] classNeedles;
    private String id;
    private String tagName;
    private Set<Map> results;
    private ArrayList<AttributeFilters> allAttributeFilters;
    private AttributeFilters myAttributeFilters;
    
    private static class Node {
        private List list;
        private Map map;
        private Node parent;
        

        Node(Node parent, Map m) {
            this.map = m;
            this.parent = parent;
        }
        
        Node(Node parent, List l) {
            this.list = l;
            this.parent = parent;
        }
        
        @Override
        public boolean equals(Object o) {
            if (o instanceof Node) {
                Node n = (Node)o;
                if (map != null) {
                    return Objects.equals(map, n.map);
                }
                if (list != null) {
                    return Objects.equals(list, n.list);
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 97 * hash + Objects.hashCode(this.list);
            hash = 97 * hash + Objects.hashCode(this.map);
            return hash;
        }
        
        
        
        
    }
    
    
    public MapSelector(String selector, Set<Map> roots) {
        this(selector, roots, null);
    }
    
    private MapSelector(String selector, Set<Map> roots, ArrayList<AttributeFilters> allAttributeFilters) {
        this.roots = new LinkedHashSet<Map>();
        this.roots.addAll(roots);
        parse(selector);
        
    }
    
    /**
     * Creates a element selector that wraps the provided elements.  The provided 
     * components are treated as the "results" of this selector.  Not the roots.  However
     * you can use {@link #find(java.lang.String) } to perform a query using this selector
     * as the roots.
     * @param els Components to add to this selector results.
     */
    public MapSelector(Map... els) {
        this.roots = new LinkedHashSet<Map>();
        this.results = new LinkedHashSet<Map>();
        for (Map cmp : els) {
            this.results.add(cmp);
        }
    }
    
    /**
     * Creates a selector with the provided roots.  This will only search through the subtrees
     * of the provided roots to find results that match the provided selector string.
     * @param selector The selector string
     * @param roots The roots for this selector.
     */
    public MapSelector(String selector, Map... roots) {
        this.roots = new LinkedHashSet<Map>();
        for (Map root : roots) {
            this.roots.add(root);
        }
        parse(selector);
    }
    
    private class AttributeFilters {
        private List<AttributeFilter> filters = new ArrayList<>();
        private List<String> logicalConnectors = new ArrayList<>();
        
        boolean match(Map el) {
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
                    if (lastTermResult) {
                        return true;
                    }
                    
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
        
        boolean match(Map el) {
            if ("=".equals(comparator)) {
                return Objects.equals(String.valueOf(el.get(attributeName)), attributeValue);
            }
            if ("!=".equals(comparator)) {
                return !Objects.equals(String.valueOf(el.get(attributeName)), attributeValue);
            }
            if (">".equals(comparator)) {
                double val1 = 0;
                double val2 = 0;
                try {
                    String val = String.valueOf(el.get(attributeName));
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
                    String val = String.valueOf(el.get(attributeName));
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
                    String val = String.valueOf(el.get(attributeName));
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
                    String val = String.valueOf(el.get(attributeName));
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
            
            aggregateSelectors = new LinkedHashSet<MapSelector>();
            for (String part : parts) {
                part = part.trim();
                if (part.length() == 0) {
                    continue;
                }
                aggregateSelectors.add(new MapSelector(part, roots, allAttributeFilters));
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
                parent = new MapSelector(parentSelector.toString(), roots, allAttributeFilters);
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
            MapSelector out = this;
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
    public Iterator<Map> iterator() {
        return resultsImpl().iterator();
    }
    
    private Set<Map> resultsImpl() {
        if (results == null) {
            results = new LinkedHashSet<Map>();
            
            if (aggregateSelectors != null) {
                for (MapSelector sel : aggregateSelectors) {
                    results.addAll(sel.resultsImpl());
                   
                }
                return results;
            }
            
            if (parent != null) {
                roots.clear();
                roots.addAll(parent.resultsImpl());
            }
            
            for (Map root : roots) {
                if (childrenOnly) {
                    
                    for (Map.Entry e : (Set<Map.Entry>)root.keySet()) {
                        
                        Object v = e.getValue();
                        if (v instanceof Map) {
                            if (match((Map)v)) {
                                results.add((Map)v);
                                
                            }
                        } else if (v instanceof List) {
                            for (Object o : (List)v) {
                                if (o instanceof Map) {
                                    results.add((Map)o);
                                    
                                }
                            }
                        }
                    }
                } else {
                    for (Map.Entry e : (Set<Map.Entry>)root.keySet()) {
                        
                        Object v = e.getValue();
                        if (v instanceof Map) {
                            resultsImpl(results, (Map)v);
                        } else if (v instanceof List) {
                            for (Object o : (List)v) {
                                if (o instanceof Map) {
                                    resultsImpl(results, (Map)o);
                                }
                            }
                        }
                    }
                }
            }
        }
        return results;
    }
    
    
    private Set<Map> resultsImpl(Set<Map> out, Map root) {
        if (match(root)) {
            out.add(root);
        }
        for (Map.Entry e : (Set<Map.Entry>)root.keySet()) {
            Object v = e.getValue();
            if (v instanceof Map) {
                resultsImpl(results, (Map)v);
            } else if (v instanceof List) {
                for (Object o : (List)v) {
                    if (o instanceof Map) {
                        resultsImpl(results, (Map)o);
                    }
                }
            }
        }
        
        return out;
    }
    
    private boolean match(Map c) {

        if (myAttributeFilters != null) {
            if (!myAttributeFilters.match(c)) {
                return false;
            }
        }
        
        if (id != null && !id.equalsIgnoreCase(String.valueOf(c.get("id")))) {
            return false;
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
    public boolean add(Map e) {
        setDirty();
        return resultsImpl().add(e);
    }
    
    private void setDirty() {
        
    }

   
    
   
    
    public MapSelector put(Object key, Object value) {
        for (Map c : this) {
            c.put(key, value);
        }
        return this;
    }
    
    public Object get(String key) {
        for (Map c : this) {
           return c.get(key);
        }
        return null;
    }
    
    
    public int getInt(String key, int defaultVal) {
        Object o = get(key);
        if (o == null) {
            return defaultVal;
        }
        if (o instanceof Integer) {
            return (Integer)o;
        }
        if (o instanceof Number) {
            return ((Number)o).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(key));
        } catch (Throwable t) {
            return defaultVal;
        }
    }
    
    public double getDouble(String key, double defaultVal) {
        Object o = get(key);
        if (o == null) {
            return defaultVal;
        }
        if (o instanceof Double) {
            return (Double)o;
        }
        if (o instanceof Number) {
            return ((Number)o).doubleValue();
        }
        try {
            return Double.parseDouble(String.valueOf(key));
        } catch (Throwable t) {
            return defaultVal;
        }
    }
    
    public String getString(String key, String defaultVal) {
        Object o = get(key);
        if (o == null) {
            return defaultVal;
        }
        return String.valueOf(key);
    }
    
    
    public boolean getBoolean(String key, boolean defaultVal) {
        Object o = get(key);
        if (o == null) {
            return defaultVal;
        }
        if (o instanceof Boolean) {
            return (Boolean)o;
        }
        String strval = String.valueOf(o);
        return "true".equalsIgnoreCase(strval) || "1".equals(strval);
        
    }
    
    public List getList(String key, boolean nonNull) {
        Object o = get(key);
        if (o == null) {
            if (nonNull) {
                return new ArrayList();
            }
            return null;
        }
        if (o instanceof List) {
            return (List)o;
        } else {
            ArrayList out = new ArrayList();
            out.add(o);
            return out;
        }
    }
    
    public Map getMap(String key, boolean nonNull) {
        Object o = get(key);
        if (o == null) {
            if (nonNull) {
                return new HashMap();
            }
            return null;
        }
        if (o instanceof Map) {
            return (Map)o;
        } else {
            Map out = new HashMap();
            out.put("ROOT", out);
            return out;
        }
    }
    
    public Date getDate(String key, DateFormat... formats) {
        Object o = get(key);
        if (o == null) {
            return null;
        }
        if (o instanceof Date) {
            return (Date)o;
        }
        if (o instanceof Number) {
            return new Date(((Number)o).longValue());
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
    
}
