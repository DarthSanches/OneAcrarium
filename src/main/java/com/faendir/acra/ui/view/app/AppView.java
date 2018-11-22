package com.faendir.acra.ui.view.app;

import com.faendir.acra.i18n.Messages;
import com.faendir.acra.model.App;
import com.faendir.acra.ui.base.ActiveChildAware;
import com.faendir.acra.ui.base.ParentLayout;
import com.faendir.acra.ui.component.Tab;
import com.faendir.acra.ui.view.MainView;
import com.faendir.acra.ui.view.app.tabs.AdminTab;
import com.faendir.acra.ui.view.app.tabs.AppTab;
import com.faendir.acra.ui.view.app.tabs.BugTab;
import com.faendir.acra.ui.view.app.tabs.ReportTab;
import com.faendir.acra.ui.view.app.tabs.StatisticsTab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RoutePrefix;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.stream.Stream;

/**
 * @author lukas
 * @since 13.07.18
 */
@UIScope
@SpringComponent
@RoutePrefix("app")
@com.vaadin.flow.router.ParentLayout(MainView.class)
public class AppView extends ParentLayout implements ActiveChildAware<AppTab<?>, App> {
    private App app;
    private final Tabs tabs;

    @Autowired
    public AppView() {
        tabs = new Tabs(Stream.of(TabDef.values()).map(def -> new Tab(def.labelId)).toArray(Tab[]::new));
        tabs.addSelectedChangeListener(e -> getUI().ifPresent(ui -> ui.navigate(TabDef.values()[e.getSource().getSelectedIndex()].tabClass, app.getId())));
        setSizeFull();
        ParentLayout content = new ParentLayout();
        content.setWidthFull();
        expand(content);
        content.getStyle().set("overflow","auto");
        setRouterRoot(content);
        getStyle().set("flex-direction","column");
        removeAll();
        add(tabs, content);
    }

    @Override
    public void setActiveChild(AppTab<?> child, App parameter) {
        app = parameter;
        Stream.of(TabDef.values()).filter(def -> def.tabClass.equals(child.getClass())).findAny().ifPresent(def -> tabs.setSelectedIndex(def.ordinal()));
    }

    private enum TabDef {
        BUG(Messages.BUGS, BugTab.class),
        REPORT(Messages.REPORTS, ReportTab.class),
        STATISTICS(Messages.STATISTICS, StatisticsTab.class),
        ADMIN(Messages.ADMIN, AdminTab.class);
        private String labelId;
        private Class<? extends AppTab<?>> tabClass;

        TabDef(String labelId, Class<? extends AppTab<?>> tabClass) {
            this.labelId = labelId;
            this.tabClass = tabClass;
        }
    }
}
