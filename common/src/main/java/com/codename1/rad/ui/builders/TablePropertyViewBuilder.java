package com.codename1.rad.ui.builders;

import ca.weblite.shared.components.table.Table;
import com.codename1.rad.annotations.Inject;
import com.codename1.rad.annotations.RAD;
import com.codename1.rad.propertyviews.TablePropertyView;
import com.codename1.rad.propertyviews.TextFieldPropertyView;
import com.codename1.rad.ui.ViewContext;
import com.codename1.ui.TextField;

import java.util.Map;

@RAD(tag={"tablePropertyView", "radTable"})
public class TablePropertyViewBuilder extends PropertyViewBuilder<Table> {
    private Table table;
    public TablePropertyViewBuilder(ViewContext context, String tagName, Map<String, String> attributes) {
        super(context, tagName, attributes);
    }

    public TablePropertyViewBuilder table(@Inject Table table) {
        this.table = table;
        return this;
    }

    @Override
    public TablePropertyView build() {
        if (fieldNode == null) {
            throw new IllegalStateException("TablePropertyViewBuilder requires a tag attribute to be set");
        }
        return new TablePropertyView(table, getContext().getEntity(), fieldNode);
    }

    @Override
    public TablePropertyView getComponent() {
        return (TablePropertyView)super.getComponent();
    }
}
