package com.codename1.rad.attributes;

import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.EntityListProvider;
import com.codename1.rad.ui.entityviews.EntityListView;

/**
 * Attribute to set the {@link EntityListProvider} on an {@link EntityListView}.
 */
public class EntityListProviderAttribute extends Attribute<EntityListProvider> {
    public EntityListProviderAttribute(EntityListProvider value) {
        super(value);
    }
}
