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

import com.codename1.rad.io.XMLTag;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.Property;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.ui.NodeList;
import com.codename1.rad.ui.UI;
import com.codename1.xml.Element;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author shannah
 */
public class XMLDocumentParser extends Node {
    private Map<String, XMLElementParser> elementParserMap = new HashMap<>();
    private Map<String, String> xmlns = new HashMap<>();
    
    
    /**
     * Creates a new XMLDocumentParser.  
     * @param atts Attributes.  Generally you'll want to provide {@link XMLNamespaceAlias} and {@link XMLElementParser} nodes here.
     */
    public XMLDocumentParser(Attribute... atts) {
        super(null);
        setAttributes(atts);
    }
    
    /**
     * Encapsulates an XML namespace alias.
     * @see #xmlns(java.lang.String, java.lang.String) 
     */
    public static class XMLNamespaceAlias extends Attribute<String> {
        private String namespace;
        public XMLNamespaceAlias(String alias, String namespace) {
            super(alias);
            this.namespace = namespace;
            
        }
        
        public String getAlias() {
            return (String)getValue();
        }
        
        public String getNamespace() {
            return namespace;
        }
    }
    
    /**
     * Creates a new {@link XMLNamespaceAlias} attribute.
     * @param alias
     * @param namespace
     * @return 
     */
    public static XMLNamespaceAlias xmlns(String alias, String namespace) {
        return new XMLNamespaceAlias(alias, namespace);
    }
    

    @Override
    public void setAttributes(Attribute... atts) {
        super.setAttributes(atts);
        for (Attribute att : atts) {
            if (att.getClass() == XMLNamespaceAlias.class) {
                XMLNamespaceAlias alias = (XMLNamespaceAlias)att;
                xmlns.put(alias.getAlias(), alias.getNamespace());
            }
        }
        
        for (Attribute att : atts) {
            if (att.getClass() == XMLElementParser.class) {
                XMLElementParser xep = (XMLElementParser)att;
                XMLTag xmlTag = (XMLTag)xep.findAttribute(XMLTag.class);
                if (xmlTag == null) {
                    throw new IllegalArgumentException("XMLElementParser missing XMLTag attribute");
                }
                
                String tagName = xmlTag.getTagName();
                String namespace = xmlTag.getNamespace();
                if (namespace == null) {
                    elementParserMap.put(tagName, xep);
                } else {
                    elementParserMap.put(namespace + ":" + tagName, xep);
                }
                
            }
        }
    }
    
    /**
     * Finds a parser that can be used to parse the given element.
     * @param el
     * @return 
     */
    public XMLElementParser findParserForElement(Element el) {
        String tagName = el.getTagName();
        String namespaceAlias = null;
        String namespace = null;
        int colonPos = tagName.indexOf(":");
        if (colonPos> 0) {
            namespaceAlias = tagName.substring(0, colonPos);
            tagName = tagName.substring(colonPos+1);
            namespace = xmlns.get(namespaceAlias);
        }
        if (namespace != null) {
            tagName = namespace + ":" + tagName;
        }
        
        XMLElementParser parser = elementParserMap.get(tagName);
        
        return parser;
    }
    
    /**
     * Creates a parser for the provided EntityType.  This generally only used when 
     * a parser can't be found to parse a given element.  In this case it will generate
     * a new parser using the Properties of the entity type.  For this to work properly,
     * the Properties should have XMLTag attributes to specify the XML tags that can be
     * bound.
     * @param et
     * @return 
     */
    private XMLElementParser createElementParserForEntityType(EntityType et) {
        NodeList fieldNodes = new NodeList();
        for (Property prop : et) {
            
            
            
            XMLTag xmlTag = (XMLTag)prop.getAttribute(XMLTag.class);
            
            if (xmlTag != null) {
                FieldNode fn = new FieldNode(xmlTag);
                fn.setAttributes(UI.property(prop), UI.tags(prop.getTags().getValue()));
                
                fieldNodes.add(fn);
            }
            
        }
        
        XMLElementParser out = new XMLElementParser(fieldNodes.asAttributes());
        setAttributes(out);
        
        return out;
    }
    
    
    /**
     * Parses an XML element into the given Entity.
     * @param entity The entity that the parser will load using data from the given element.
     * @param element The element to parse.
     * @return The entity that was loaded.  This will be the same entity that was provided in the entity parameter.
     * @throws IOException 
     */
    public Entity parse(Entity entity, Element element) throws IOException {
        XMLElementParser parser = findParserForElement(element);
        if (parser == null) {
            parser = createElementParserForEntityType(entity.getEntityType());
        }
        parser.parse(entity, element);
        return entity;
        
    }
    
}
