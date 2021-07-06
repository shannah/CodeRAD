package com.codename1.rad.ui.builders;

import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityListProvider;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.ui.AbstractComponentBuilder;
import com.codename1.rad.ui.EntityListCellRenderer;
import com.codename1.rad.ui.UI;
import com.codename1.rad.ui.ViewContext;
import com.codename1.rad.ui.entityviews.EntityListView;
import com.codename1.rad.ui.entityviews.ProfileAvatarsTitleComponent;
import com.codename1.ui.CN;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.plaf.Style;

import java.util.Map;

import static com.codename1.rad.util.NonNull.with;

@RAD(tag={"profileAvatarsTitle", "profileAvatarsTitleComponent"})
public class ProfileAvatarsTitleComponentBuilder extends AbstractEntityViewBuilder<ProfileAvatarsTitleComponent> {

    private int avatarSize;

    public ProfileAvatarsTitleComponentBuilder(@Inject ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);

    }


    public void setAvatarSize(int size) {
        this.avatarSize = size;
    }




    @Override
    public ProfileAvatarsTitleComponent build() {
        ViewNode node = new ViewNode();
        node.setParent(getContext().getNode());
        EntityList l = (entity instanceof EntityList) ? (EntityList)entity : new EntityList();
        if (entity != l) {
            l.add(entity);
        }
        if (avatarSize <= 0) {
            avatarSize = CN.convertToPixels(1f, Style.UNIT_TYPE_REM);
        }
        return new ProfileAvatarsTitleComponent(l, node, avatarSize / (float)CN.convertToPixels(1f));
    }

    @Override
    public Object parseConstraint(String constraint) {
        return null;
    }


}
