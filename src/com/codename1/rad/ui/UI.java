/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;


import ca.weblite.shared.components.table.DefaultTableCellEditor;
import ca.weblite.shared.components.table.DefaultTableCellRenderer;
import com.codename1.rad.attributes.ActionStyleAttribute;
import com.codename1.rad.events.EventFactory;
import com.codename1.rad.nodes.FormNode;
import com.codename1.rad.nodes.FieldNode;
import com.codename1.rad.nodes.PropertyNode;
import com.codename1.rad.nodes.SectionNode;
import com.codename1.rad.attributes.Columns;
import com.codename1.rad.attributes.IconRendererAttribute;
import com.codename1.rad.attributes.ImageIcon;
import com.codename1.rad.attributes.ListCellRendererAttribute;
import com.codename1.rad.attributes.MaterialIcon;
import com.codename1.rad.attributes.NodeDecoratorAttribute;
import com.codename1.rad.attributes.TableCellEditorAttribute;
import com.codename1.rad.attributes.TableCellRendererAttribute;
import com.codename1.rad.attributes.ViewPropertyParameterAttribute;

import com.codename1.rad.ui.image.EntityImageRenderer;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ActionsNode;
import com.codename1.rad.nodes.EntityViewFactoryNode;
import com.codename1.rad.nodes.TableColumns;
import com.codename1.rad.nodes.EventFactoryNode;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.PropertyViewFactoryNode;
import com.codename1.rad.nodes.RowTemplateNode;
import com.codename1.rad.nodes.ViewNode;
import ca.weblite.shared.components.table.TableCellEditor;
import ca.weblite.shared.components.table.TableCellRenderer;
import com.codename1.io.File;
import com.codename1.rad.attributes.Badge;
import com.codename1.rad.attributes.BadgeUIID;
import com.codename1.rad.attributes.Condition;
import com.codename1.rad.attributes.IconUIID;
import com.codename1.rad.attributes.SelectedCondition;
import com.codename1.rad.attributes.TextIcon;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.events.DefaultEventFactory;
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.DateFormatterAttribute;
import com.codename1.rad.models.EntityTest;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.NumberFormatterAttribute;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Editable;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.StringProvider;
import com.codename1.rad.models.Tag;
import com.codename1.rad.nodes.ActionNode.Category;
import com.codename1.rad.nodes.ActionNode.EnabledCondition;
import com.codename1.rad.nodes.ActionViewFactoryNode;
import com.codename1.rad.text.CurrencyFormatter;
import com.codename1.rad.text.DateFormatter;
import com.codename1.rad.text.DecimalNumberFormatter;
import com.codename1.rad.text.IntegerFormatter;
import com.codename1.rad.text.LocalDateLongStyleFormatter;
import com.codename1.rad.text.LocalDateShortStyleFormatter;
import com.codename1.rad.text.LocalDateTimeFormatter;
import com.codename1.rad.text.LocalDateTimeMediumStyleFormatter;
import com.codename1.rad.text.LocalDateTimeShortStyleFormatter;
import com.codename1.rad.text.NumberFormatter;
import com.codename1.rad.text.TimeAgoDateFormatter;
import com.codename1.ui.Image;
import com.codename1.util.EasyThread;


/**
 * The base class for UI descriptors. This class sits at the foundation of CodeRAD's dynamic form generation
 * capability.
 * 
 * See {@link FormNode} for some usage examples.
 * 
 * @author shannah
 */
public class UI extends EntityType implements ActionCategories, WidgetTypes {
    private FormNode root;
    private static EntityViewFactory defaultEntityViewFactory;
    private static ActionViewFactory defaultActionViewFactory;
    private static EventFactory defaultEventFactory;
    private static PropertyViewFactory defaultPropertyViewFactory;
    private static TableCellRenderer defaultTableCellRenderer;
    private static TableCellEditor defaultTableCellEditor;
    private static EntityListCellRenderer defaultListCellRenderer;
    private static File tmpDir;
    
    private static StrongCache cache;
    
    public static StrongCache getCache() {
        if (cache == null) {
            cache = new StrongCache();
        }
        return cache;
    }
    
    public static void setDefaultListCellRenderer(EntityListCellRenderer renderer) {
        defaultListCellRenderer = renderer;
    }
    
    public static EntityListCellRenderer getDefaultListCellRenderer() {
        if (defaultListCellRenderer == null) {
            defaultListCellRenderer = new DefaultEntityListCellRenderer();
        }
        return defaultListCellRenderer;
    }
    
    public static void setDefaultEntityViewFactory(EntityViewFactory factory) {
        defaultEntityViewFactory = factory;
    }
    
    public static ActionsNode removeAction(ActionNode removeAction) {
        return actions(LIST_REMOVE_ACTION, removeAction);
    }
    
    public static ActionsNode selectAction(ActionNode selectAction) {
        return actions(LIST_SELECT_ACTION, selectAction);
    }
    
    public static ActionsNode addAction(ActionNode insertAction) {
        return actions(LIST_ADD_ACTION, insertAction);
    }
    
    public static EntityViewFactory getDefaultEntityViewFactory() {
        if (defaultEntityViewFactory == null) {
            defaultEntityViewFactory = new DefaultEntityViewFactory();
        }
        return defaultEntityViewFactory;
    }
    
    public static ActionViewFactory getDefaultActionViewFactory() {
        if (defaultActionViewFactory == null) {
            defaultActionViewFactory = new DefaultActionViewFactory();
        }
        return defaultActionViewFactory;
    }
    
    public static void setDefaultActionViewFactory(ActionViewFactory factory) {
        defaultActionViewFactory = factory;
    }
    
    public static EventFactory getDefaultEventFactory() {
        if (defaultEventFactory == null) {
            defaultEventFactory = new DefaultEventFactory();
        }
        return defaultEventFactory;
    }
    
    public static void setDefaultEventFactory(EventFactory factory) {
        defaultEventFactory = factory;
    }
    
    public static PropertyViewFactory getDefaultPropertyViewFactory() {
        if (defaultPropertyViewFactory == null) {
            defaultPropertyViewFactory = new DefaultPropertyViewFactory();
        }
        return defaultPropertyViewFactory;
    }
    
    public static void setDefaultPropertyViewFactory(PropertyViewFactory factory) {
        defaultPropertyViewFactory = factory;
    }
    
    public static TableCellRenderer getDefaultTableCellRenderer() {
        if (defaultTableCellRenderer == null) {
            defaultTableCellRenderer = new DefaultTableCellRenderer();
        }
        return defaultTableCellRenderer;
    }
    
    public static void setDefaultTableCellRenderer(TableCellRenderer renderer) {
        defaultTableCellRenderer = renderer;
    }
    
    public static TableCellEditor getDefaultTableCellEditor() {
        if (defaultTableCellEditor == null) {
            defaultTableCellEditor = new DefaultTableCellEditor();
        }
        return defaultTableCellEditor;
    }
    
    public static void setDefaultTableCellEditor(TableCellEditor editor) {
        defaultTableCellEditor = editor;
    }
    
    public static File getTempDir() {
        if (tmpDir == null) {
            tmpDir = new File("CodeRadTmp");
            tmpDir.mkdirs();
        }
        return tmpDir;
    }
    
    public static File getTempFile(String name) {
        if (name.startsWith("file:/")) {
            return new File(name);
        } else {
            return new File(getTempDir(), name);
        }
    }
    
    
    public static EntityViewFactoryNode viewFactory(EntityViewFactory factory, Attribute... atts) {
        return new EntityViewFactoryNode(factory, atts);
    }
    
    public static ActionViewFactoryNode actionViewFactory(ActionViewFactory factory, Attribute... atts) {
        return new ActionViewFactoryNode(factory, atts);
    }
    
    public static ActionViewFactoryNode viewFactory(ActionViewFactory factory, Attribute... atts) {
        return actionViewFactory(factory, atts);
    }
    

    private static EasyThread imageProcessingThread;
    protected FormNode form(Attribute... atts) {
        root = new FormNode(atts);
        return root;
    }
    
    public static UIID uiid(String uiid) {
        return new UIID(uiid);
    }
    
    public static IconUIID iconUiid(String uiid) {
        return new IconUIID(uiid);
    }
    
    public static BadgeUIID badgeUiid(String uiid) {
        return new BadgeUIID(uiid);
    }
    
    public static Badge badge(String badgeText) {
        return new Badge(badgeText);
    }
    
    public static Badge badge(String badgeText, StringProvider provider) {
        return new Badge(badgeText, provider);
    }
    
    public static SectionNode section(Attribute... atts) {
        return new SectionNode(atts);
    }
    
    public static FieldNode field(Attribute... atts) {
        return new FieldNode(atts);
    }
    
    public static Columns columns(int cols) {
        return new Columns(cols);
    }
    
    public static PropertyNode property(Property prop, Attribute... atts) {
        return new PropertyNode(prop, atts);
    }
    
    public static ActionStyleAttribute actionStyle(ActionStyle style) {
        return new ActionStyleAttribute(style);
    }
    
    public static NodeDecoratorAttribute decorator(NodeDecorator decorator) {
        return new NodeDecoratorAttribute(decorator);
    }
    
    public static FieldNode textField(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(TEXT);
        return fieldNode;
    }
    
    public static FieldNode textArea(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(TEXTAREA);
        return fieldNode;
    }
    
    public static FieldNode comboBox(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(COMBOBOX);
        return fieldNode;
    }
    
    public static TableColumns columns(FieldNode... atts) {
        TableColumns columnsNode = new TableColumns(atts);
        return columnsNode;
        
    }
    
    public static FieldNode table(Attribute... atts) {
        FieldNode fieldNode = new FieldNode(atts);
        fieldNode.setAttributes(TABLE);
        return fieldNode;
    }
    
    public static TableCellRendererAttribute cellRenderer(TableCellRenderer renderer) {
        return new TableCellRendererAttribute(renderer);
    }
    
    public static TableCellEditorAttribute cellEditor(TableCellEditor editor) {
        return new TableCellEditorAttribute(editor);
    }
    
    public static ListCellRendererAttribute cellRenderer(EntityListCellRenderer renderer) {
        return new ListCellRendererAttribute(renderer);
    }
    
    public static  PropertyViewFactoryNode propertyViewFactory(PropertyViewFactory factory, Attribute... atts) {
        return new PropertyViewFactoryNode(factory, atts);
    }
    
    public static  PropertyViewFactoryNode viewFactoryFactory(PropertyViewFactory factory, Attribute... atts) {
        return new PropertyViewFactoryNode(factory, atts);
    }
    
    public static PropertyViewFactoryNode factory(PropertyViewFactory factory, Attribute... atts) {
        return propertyViewFactory(factory, atts);
    }
    
    public static PropertyViewFactoryNode propertyView(PropertyViewFactory factory, Attribute... atts) {
        return propertyViewFactory(factory, atts);
    }
    
    public static EventFactoryNode eventFactory(EventFactory factory, Attribute... atts) {
        return new EventFactoryNode(factory, atts);
    }
    
    public static EventFactoryNode event(EventFactory factory, Attribute... atts) {
        return eventFactory(factory, atts);
    }
    
    public static EventFactoryNode factory(EventFactory factory, Attribute... atts) {
        return eventFactory(factory, atts);
    }
    
    public static ActionNode action(Attribute... atts) {
        return new ActionNode(atts);
    }
    
    public static SelectedCondition selectedCondition(EntityTest test) {
        return new SelectedCondition(test);
    }
    
    public static EnabledCondition enabledCondition(EntityTest test) {
        return new EnabledCondition(test);
    }
    
    public static Condition condition(EntityTest test) {
        return new Condition(test);
    }
    
    public static ActionNode.Selected selected(Attribute... atts) {
        return new ActionNode.Selected(atts);
    }
    
    public static ActionNode.Disabled disabled(Attribute... atts) {
        return new ActionNode.Disabled(atts);
    }
    
    public static ActionNode.Pressed pressed(Attribute... atts) {
        return new ActionNode.Pressed(atts);
    }
    
    public static ActionsNode actions(Attribute...atts) {
        return new ActionsNode(atts);
    }
    
    public static ActionsNode actions(Category category, Actions actions) {
        ActionsNode out = actions(actions.toArray());
        out.setAttributes(category);
        return out;
    }
    
    public static MaterialIcon icon(char icon) {
        return new MaterialIcon(icon);
    }
    
    public static ImageIcon icon(Image icon) {
        return new ImageIcon(icon);
    }
    
    public static DateFormatterAttribute dateFormat(DateFormatter fmt) {
        return new DateFormatterAttribute(fmt);
    }
    
    public static DateFormatterAttribute shortDateFormat() {
        return dateFormat(new LocalDateShortStyleFormatter());
    }
    
    public static DateFormatterAttribute longDateFormat() {
        return dateFormat(new LocalDateLongStyleFormatter());
    }
    
    public static DateFormatterAttribute dateTimeFormat() {
        return dateFormat(new LocalDateTimeFormatter());
    }
    
    public static DateFormatterAttribute shortDateTimeFormat() {
        return dateFormat(new LocalDateTimeShortStyleFormatter());
    }
    
    public static DateFormatterAttribute mediumDateTimeFormat() {
        return dateFormat(new LocalDateTimeMediumStyleFormatter());
    }
    
    public static DateFormatterAttribute timeAgoFormat() {
        return dateFormat(new TimeAgoDateFormatter());
    }
    
    public static NumberFormatterAttribute decimalFormat(int decimalPlaces) {
        return new NumberFormatterAttribute(new DecimalNumberFormatter(decimalPlaces));
    }
    
    public static NumberFormatterAttribute currencyFormat() {
        return new NumberFormatterAttribute(new CurrencyFormatter());
    }
    
    public static NumberFormatterAttribute intFormat() {
        return new NumberFormatterAttribute(new IntegerFormatter());
    }
    
    public static Editable editable(boolean editable) {
        return new Editable(editable);
    }
    
    
    public FormNode getRoot() {
        return root;
    }
    
    public FieldNode[] getAllFields() {
        return getRoot().getAllFields();
    }
    
    
    
    public static RowTemplateNode rowTemplate(Attribute... atts) {
        ViewNode node = new ViewNode(atts);
        return new RowTemplateNode(node);
    }
    
    public static ListNode list(Attribute... atts) {
        return new ListNode(atts);
    }
    
    public static IconRendererAttribute iconRenderer(EntityImageRenderer renderer) {
        return new IconRendererAttribute(renderer);
    }
    
    
    public static ViewNode view(Attribute... atts) {
        return new ViewNode(atts);
    }
    
    public static <T> ViewPropertyParameterAttribute<T> param(ViewProperty<T> prop, T value) {
        return new ViewPropertyParameterAttribute<T>(ViewPropertyParameter.createValueParam(prop, value));
    }
    
    public static <T> ViewPropertyParameterAttribute<T> param(ViewProperty<T> prop, Tag... tags) {
        return new ViewPropertyParameterAttribute<T>(ViewPropertyParameter.createBindingParam(prop, tags));
    }
    
    public static TextIcon icon(String text) {
        return new TextIcon(text);
    }
    
    public static TextIcon icon(String text, StringProvider provider) {
        return new TextIcon(text, provider);
    }
    
    public static TextIcon icon(StringProvider provider) {
        return new TextIcon("", provider);
    }
            
    
    public static Label label(String label) {
        return new Label(label);
    }
    
    public static Label label(String label, StringProvider provider) {
        return new Label(label, provider);
    }
    
    public static Label label(StringProvider provider) {
        return new Label("", provider);
    }
    
    public synchronized static void runOnImageProcessingThread(Runnable r) {
        if (imageProcessingThread == null) {
            imageProcessingThread = EasyThread.start("ImageProcessingThread");
            
        }
        imageProcessingThread.run(r);
    }
    
    
    
}
