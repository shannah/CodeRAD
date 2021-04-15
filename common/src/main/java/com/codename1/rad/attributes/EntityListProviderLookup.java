package com.codename1.rad.attributes;

import com.codename1.rad.models.Attribute;

/**
 * Attribute to set the lookup for a {@link com.codename1.rad.models.EntityListProvider} on a {@link com.codename1.rad.ui.entityviews.EntityListView}.
 */
public class EntityListProviderLookup extends Attribute<Class> {
    public EntityListProviderLookup(Class value) {
        super(value);
    }
}
