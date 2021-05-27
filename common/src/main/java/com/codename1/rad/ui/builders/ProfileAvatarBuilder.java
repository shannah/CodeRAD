package com.codename1.rad.ui.builders;


import com.codename1.rad.annotations.RAD;
import com.codename1.rad.models.Tag;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.ui.UI;
import com.codename1.rad.ui.ViewContext;
import com.codename1.rad.ui.entityviews.ProfileAvatarView;

import java.util.Map;

@RAD(tag={"profileAvatarView", "profileAvatar"})
public class ProfileAvatarBuilder extends AbstractEntityViewBuilder<ProfileAvatarView> {

    private float size = 5f;
    private Tag nameTag, iconTag;

    protected ProfileAvatarBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public ProfileAvatarBuilder size(float size) {
        this.size = size;
        return this;
    }

    public ProfileAvatarBuilder nameTag(Tag nameTag) {
        this.nameTag = nameTag;
        return this;
    }

    public ProfileAvatarBuilder iconTag(Tag iconTag) {
        this.iconTag = iconTag;
        return this;
    }

    @Override
    public ProfileAvatarView build() {
        ViewNode n = new ViewNode();
        n.setParent(node);
        if (nameTag != null) {
            n.setAttributes(UI.param(ProfileAvatarView.NAME_PROPERTY_TAGS, nameTag));
        }
        if (iconTag != null) {
            n.setAttributes(UI.param(ProfileAvatarView.ICON_PROPERTY_TAGS, iconTag));
        }
        return new ProfileAvatarView(entity, n, size);
    }
}
