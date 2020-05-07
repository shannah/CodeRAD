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
package com.codename1.rad.ui.entityviews;

import ca.weblite.shared.components.CollapsibleHeaderContainer.ScrollableContainer;
import com.codename1.rad.attributes.UIID;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.Property.Label;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.nodes.ListNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.EntityView;
import com.codename1.rad.ui.EntityViewFactory;
import com.codename1.rad.ui.UI;
import static com.codename1.rad.ui.entityviews.EntityListView.SCROLLABLE_Y;
import com.codename1.ui.Component;
import com.codename1.ui.Container;
import com.codename1.ui.Tabs;
import com.codename1.ui.layouts.BorderLayout;

/**
 * An Tabs component that is backed by a view model.  You can specify the tabs in this
 * view using the UI descriptor.
 * 
 * === View Model
 * 
 * The view model does not need to conform necessarily to any particular schema, as the UI descriptor
 * is fully responsible for the contents of the tabs.  The UI descriptor may refer to elements of the 
 * view model for defining its contents though.
 * 
 * .Example subclass of TabsEntityView  defining two tabs.
 * image::https://shannah.github.io/CodeRAD/manual/images/TabsEntityView.png[]
 * 
 * === UI Descriptor
 * 
 * The UI descriptor is expected to be a {@link ViewNode}, where its child nodes each correspond to a 
 * tab in this tab component.  Each child node may be either a {@link ListNode} or a {@link ViewNode}.  
 * The tab label should be specified by the {@link Label} attribute.  The tab contents will be defined
 * by the {@link EntityViewFactory} for a view node, and the {@link EntityListCellRenderer} for a list
 * node.  They should also each specify either a `property` or `tags` attribute that points to the view
 * model, to retrieve the "sub" view model that should be used for the particular tab.
 * 
 * 
 * === Source Examples
 * 
 * .Example UI descriptor that specifies two tabs, each that renders a list.
 * [source,java]
 * ----
 * ViewNode vn = new ViewNode(
        list(
            property(SearchTabsViewModel.forYou),
            label("For You"),
            cellRenderer(new TWTNewsList.TWTNewsListCellRenderer())
        ),
        list(
            property(SearchTabsViewModel.trending),
            label("Trending"),
            cellRenderer(new TWTNewsList.TWTNewsListCellRenderer())
        ),
        actions(TWTSearchButton.SEARCH_ACTION, search)
  );
 * ----
 * 
 * .Corresponding view model for above UI descriptor.
 * [source,java]
 * ----
 * public static class SearchTabsViewModel extends Entity {
        public static Property forYou, trending, news, sports, fun;
        public static final EntityType TYPE = new EntityType() {{
            forYou = list(EntityList.class);
            trending = list(EntityList.class);
            news = list(EntityList.class);
            sports = list(EntityList.class);
            fun = list(EntityList.class);
        }};
        {
            setEntityType(TYPE);
        }
    }
 * ----
 * 
 * .Full View controller from the TweetAppUIKit demo that uses TabsEntityView with two tabs.  In this case,
 * both tabs are list tabs.
 * [source,java]
 * ----

package com.codename1.demos.twitterui;

import ca.weblite.shared.components.CollapsibleHeaderContainer;
import com.codename1.demos.twitterui.models.NewsItem;
import com.codename1.demos.twitterui.models.UserProfile;
import com.codename1.rad.controllers.Controller;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.models.EntityType;
import com.codename1.rad.models.Property;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.schemas.Thing;
import com.codename1.twitterui.models.TWTAuthor;
import com.codename1.twitterui.schemas.TWTNewsItem;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import static com.codename1.rad.ui.UI.*;
import com.codename1.rad.ui.entityviews.TabsEntityView;
import com.codename1.twitterui.views.TWTNewsList;
import com.codename1.twitterui.views.TWTSearchButton;
import com.codename1.twitterui.views.TWTTabsView;
import com.codename1.twitterui.views.TWTTitleComponent;
import com.codename1.ui.Form;


public class SearchTabsController extends BaseFormController {
    public static final ActionNode search = action(
            
    );
    private Map<String,Entity> authors = new HashMap<>();
    
    public SearchTabsController(Controller parent) {
        super(parent);
        SearchTabsViewModel model = createViewModel();
        addLookup(model);
        TabsEntityView view = new TWTTabsView(model, getViewNode());
       
        CollapsibleHeaderContainer wrapper = new CollapsibleHeaderContainer(
                new TWTTitleComponent(lookup(UserProfile.class), getViewNode(), new TWTSearchButton(lookup(UserProfile.class), getViewNode())),
                view, 
                view
        );
        setView(wrapper);
        
        addActionListener(search, evt->{
            evt.consume();
            Form form = new SearchFormController(this).getView();
            form.show();
        });
        
    }

    @Override
    protected ViewNode createViewNode() {
        ViewNode vn = new ViewNode(
                list(
                    property(SearchTabsViewModel.forYou),
                    label("For You"),
                    cellRenderer(new TWTNewsList.TWTNewsListCellRenderer())
                ),
                list(
                    property(SearchTabsViewModel.trending),
                    label("Trending"),
                    cellRenderer(new TWTNewsList.TWTNewsListCellRenderer())
                ),
                actions(TWTSearchButton.SEARCH_ACTION, search)
        );
        return vn;
    }
    
    
    
    public static class SearchTabsViewModel extends Entity {
        public static Property forYou, trending, news, sports, fun;
        public static final EntityType TYPE = new EntityType() {{
            forYou = list(EntityList.class);
            trending = list(EntityList.class);
            news = list(EntityList.class);
            sports = list(EntityList.class);
            fun = list(EntityList.class);
        }};
        {
            setEntityType(TYPE);
        }
    }
    
    private NewsItem createItem(Entity creator, Date date, String headline, String thumbnailUrl) {
        NewsItem item = new NewsItem();
        item.setEntity(TWTNewsItem.creator, creator);
        item.setDate(TWTNewsItem.date, date);
        item.setText(TWTNewsItem.headline, headline);
        item.setText(TWTNewsItem.thumbnailUrl, thumbnailUrl);
        item.setText(TWTNewsItem.image, thumbnailUrl);
        return item;
    }
    
    private EntityList createSection() {
        EntityList out = new EntityList();
        String georgeThumb = "https://weblite.ca/cn1tests/radchat/george.jpg";
        String kramerThumb = "https://weblite.ca/cn1tests/radchat/kramer.jpg";
        String jerryThumb = "https://weblite.ca/cn1tests/radchat/jerry.jpg";
        Entity george = getOrCreateAuthor("George", "@kostanza", georgeThumb);
        Entity kramer = getOrCreateAuthor("Kramer", "@kramer", kramerThumb);
        Entity jerry = getOrCreateAuthor("Jerry", "@jerry", jerryThumb);
        long SECOND = 1000l;
        long MINUTE = SECOND * 60;
        long HOUR = MINUTE * 60;
        long DAY = HOUR * 24;
        long t = System.currentTimeMillis() - 2 * DAY;
        out.add(createItem(jerry, new Date(t), "Stunning Public Domain Waterfall Photos", "https://weblite.ca/cn1tests/radchat/waterfalls.jpg"));
        out.add(createItem(george, new Date(t), "Use the RADChatRoom library to quickly and easily add a nice-looking, ...", "https://www.codenameone.com/img/blog/chat-ui-kit-feature.jpg"));
        out.add(createItem(kramer, new Date(t), "EasyThread  makes it much easier to write multi-threaded code in Codename One.", "https://www.codenameone.com/img/blog/new-features.jpg"));
        out.add(createItem(jerry, new Date(t), "XCODE 11 IS NOW THE DEFAULT", "https://www.codenameone.com/img/blog/xcode-7.png"));
        out.add(createItem(kramer, new Date(t), "CSS styles can now be distributed inside a cn1lib.", "https://www.codenameone.com/img/blog/css-header.jpg"));
        out.add(createItem(george, new Date(t), "Iran's vast coronavirus burial pits are visible by satellite", "https://pbs.twimg.com/media/ES6gV-xXkAQwZ0Y?format=jpg&name=small"));
        out.add(createItem(george, new Date(t), "We've added the ability to position your Sheets in different locations on the screen.", "https://www.codenameone.com/img/blog/new-features.jpg"));
        return out;
    }
    
    private EntityList createTrendingSection() {
        EntityList out = new EntityList();
        String georgeThumb = "https://weblite.ca/cn1tests/radchat/george.jpg";
        String kramerThumb = "https://weblite.ca/cn1tests/radchat/kramer.jpg";
        Entity george = getOrCreateAuthor("George", "@kostanza", georgeThumb);
        Entity kramer = getOrCreateAuthor("Kramer", "@kramer", kramerThumb);
        long SECOND = 1000l;
        long MINUTE = SECOND * 60;
        long HOUR = MINUTE * 60;
        long DAY = HOUR * 24;
        long t = System.currentTimeMillis() - 2 * DAY;
        
        out.add(createItem(george, new Date(t), "Now you can create CN1Libs with embedded CSS Styles", "https://www.codenameone.com/img/blog/css-header.jpg"));
        out.add(createItem(george, new Date(t), "We now have a reliable way to avoid clipping the Notch and Task bar on the iPhone X.", "https://www.codenameone.com/img/blog/new-features.jpg"));
        return out;
    }
    
    private SearchTabsViewModel createViewModel() {
        SearchTabsViewModel out = new SearchTabsViewModel();
        out.set(SearchTabsViewModel.forYou, createSection());
        out.set(SearchTabsViewModel.fun, createSection());
        out.set(SearchTabsViewModel.news, createSection());
        out.set(SearchTabsViewModel.sports, createSection());
        out.set(SearchTabsViewModel.trending, createTrendingSection());
        return out;
    }
    
     private Entity getOrCreateAuthor(String name, String id, String iconUrl) {
        if (authors.containsKey(id)) {
            return authors.get(id);
        }
        Entity author = new TWTAuthor();
        author.set(Thing.name, name);
        author.set(Thing.identifier, id);
        author.set(Thing.thumbnailUrl, iconUrl);
        authors.put(id, author);
        return author;
        
    }
}

 * ----
 * @author shannah
 */
public class TabsEntityView extends AbstractEntityView implements ScrollableContainer {
    private ViewNode node;
    private Tabs tabs;
    
    public TabsEntityView(Entity entity, ViewNode node) {
        super(entity);
        this.node = node;
        initUI();
    }
    
    protected Tabs createTabs() {
        Tabs out = new Tabs();
        out.setTabPlacement(TOP);
        UIID uiid = (UIID)node.findAttribute(UIID.class);
        if (uiid != null) {
            out.setUIID(uiid.getValue());
        }
        
        return out;
    }
    
    private void initUI() {
        tabs = createTabs();
        for (Node n : getViewNode().getChildNodes()) {
            UIID uiid = (UIID)n.findAttribute(UIID.class);
            if (uiid != null) {
                tabs.setTabUIID(uiid.getValue());
            }
            Label l = (Label)n.findAttribute(Label.class);
            if (l == null) {
                continue;
            }
            EntityView content = createTab(n);
            if (content == null) {
                continue;
            }
            tabs.addTab(l.getValue(getEntity()), (Component)content);
        }
        setLayout(new BorderLayout());
        add(BorderLayout.CENTER, tabs);
    }
    
    
    private EntityView createTab(Node node) {
        ViewNode vn = (ViewNode)node.as(ViewNode.class);
        if (vn != null) {
            return createViewTab(vn);
            
        }
        ListNode ln = (ListNode)node.as(ListNode.class);
        if (ln != null) {
            return createListTab(ln);
        }
        return null;
    }
    
    private EntityView createListTab(ListNode ln) {
        PropertySelector selector = ln.createPropertySelector(getEntity());
        if (selector == null) {
            return null;
        }
        EntityList tabEntity = selector.getEntityList(null);
        if (tabEntity == null) {
            return null;
        }
        ln.setAttributesIfNotExists(
                UI.param(SCROLLABLE_Y, true)
        );
        return new EntityListView(tabEntity, ln);
    }
    
    private EntityView createViewTab(ViewNode vn) {
       
        
        
        EntityViewFactory factory = vn.getViewFactory(null);
        if (factory == null) {
            return null;
        }
        
        Entity tabEntity = null;
        PropertySelector sel = vn.createPropertySelector(getEntity());
        if (sel == null) {
            return null;
        }
        tabEntity = sel.getEntity(null);
        if (tabEntity == null) {
            return null;
        }
        
        return factory.createView(tabEntity, vn);

        
    }
    
    @Override
    public void update() {
        
    }

    @Override
    public void commit() {
        
    }

    @Override
    public Node getViewNode() {
        return node;
    }

    /**
     * For use with CollapsibleHeaderContainer.  When tabs are used as the body
     * it needs to let the collapsible header container which container us
     * the current scrollable.
     * @return 
     */
    @Override
    public Container getVerticalScroller() {
        Component cmp = tabs.getSelectedComponent();
        if (cmp == null) {
            return null;
        }
        if (cmp instanceof ScrollableContainer) {
            cmp = ((ScrollableContainer)cmp).getVerticalScroller();
        }
        if (cmp instanceof Container) {
            return (Container)cmp;
        }
        return cmp.getParent();
    }
    
    
    
}
