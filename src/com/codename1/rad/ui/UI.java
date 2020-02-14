/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.codename1.rad.ui;


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
import com.codename1.rad.models.Attribute;
import com.codename1.rad.models.DateFormatterAttribute;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.NumberFormatterAttribute;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.Property.Editable;
import com.codename1.rad.models.Tag;
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
 *
 * @author shannah
 */
public class UI extends EntityType implements ActionCategories, WidgetTypes {
    private FormNode root;
    private static EntityViewFactory defaultEntityViewFactory;
    
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
    
    public static EntityViewFactoryNode viewFactory(EntityViewFactory factory) {
        return new EntityViewFactoryNode(factory);
    }

    private static EasyThread imageProcessingThread;
    protected FormNode form(Attribute... atts) {
        root = new FormNode(atts);
        return root;
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
    
    public static  PropertyViewFactoryNode factory(PropertyViewFactory factory, Attribute... atts) {
        return new PropertyViewFactoryNode(factory, atts);
    }
    
    public static EventFactoryNode event(EventFactory factory, Attribute... atts) {
        return new EventFactoryNode(factory, atts);
    }
    
    public static ActionNode action(Attribute... atts) {
        return new ActionNode(atts);
    }
    
    public static ActionsNode actions(Attribute...atts) {
        return new ActionsNode(atts);
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
    
    
    public synchronized static void runOnImageProcessingThread(Runnable r) {
        if (imageProcessingThread == null) {
            imageProcessingThread = EasyThread.start("ImageProcessingThread");
            
        }
        imageProcessingThread.run(r);
    }
    
    
}
