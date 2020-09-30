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

import com.codename1.rad.models.Attribute;

/**
 * An attribute that can be attached to a Property or FieldNode to assist
 * the {@link XMLDocumentParser} in matching the Property to the correct {@link XMLElementParser}
 * when parsing XML documents to Entities.
 * @author shannah
 */
public class XMLTag extends Attribute<String> {
    
    /**
     * The namespace of the XML tag.  This should not be the alias, but the long-form of the namespace.
     * May be null.
     */
    private String namespace;
    
    /**
     * The tag name.  This should not include the namespace portion.
     */
    private String tagName;

    /**
     * Creates a new tag with the given tag name, and no namespace.
     * @param tagName 
     */
    public XMLTag(String tagName) {
        super(tagName);
        this.tagName = tagName;
    }

    /**
     * Creates a new tag with the given tag name and namespace.
     * @param tagName
     * @param namespace 
     */
    public XMLTag(String tagName, String namespace) {
        super(namespace + ":" + tagName);
        this.tagName = tagName;
        this.namespace = namespace;
    }

    /**
     * Gets fully qualified namespace.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Gets the tag name.  Does not include namespace.
     * @return 
     */
    public String getTagName() {
        return tagName;
    }
    
}
