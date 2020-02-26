/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * An attribute whose sole purpose is to contain tags.  When tags are added to a {@link Property} or {@link FieldNode}, they are always wrapped inside a {@link Tags} attribute.
 * @author shannah
 * @see Tag
 * @see EntityType#tags(com.codename1.rad.models.Tag...) 
 * @see com.codename1.rad.ui.UI#tags(com.codename1.rad.models.Tag...) 
 */
public class Tags extends Attribute<Tag[]> implements Iterable<Tag> {
    private LinkedHashSet<Tag> tags = new LinkedHashSet<>();
    public Tags(Tag... value) {
        super(value);
        addTags(value);
    }

    @Override
    public Tag[] getValue() {
        return tags.toArray(new Tag[tags.size()]);
    }
    
    public void addTags(Tag... tags) {
        this.tags.addAll(Arrays.asList(tags));
    }

    
    
    @Override
    public Iterator<Tag> iterator() {
        return tags.iterator();
    }
    
    public boolean isEmpty() {
        return tags.isEmpty();
    }
    
    public boolean intersects(Tags tags) {
        if (tags == null) {
            return false;
        }
        for (Tag t : tags) {
            if (this.tags.contains(t)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contains(Tag tag) {
        return tags.contains(tag);
    }
    
    public Tag[] toArray() {
        return tags.toArray(new Tag[tags.size()]);
    }
    
}
