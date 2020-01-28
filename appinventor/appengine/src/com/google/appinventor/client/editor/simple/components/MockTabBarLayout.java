package com.google.appinventor.client.editor.simple.components;

import com.google.appinventor.components.common.ComponentConstants;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

import java.util.Map;

/**
 * A layout that arranges its children in different tabs.
 * (Number of tabs should be greater than or equal to 2)
 *
 * @author jsuyash1514@gmail.com (Suyash Jain)
 */
final class MockTabBarLayout extends MockLayout {
    private int currentTabIdx;
    private int nTabs;

    private static class Tab {
        int idx;

        private Tab(int idx) {
            this.idx = idx;
        }
    }

    private class TabBarLayoutInfo extends LayoutInfo {
        MockComponent[] tabChildren;

        TabBarLayoutInfo(Map<MockComponent, LayoutInfo> layoutInfoMap, MockComponent tabbar) {
            super(layoutInfoMap, tabbar);

            tabChildren = new MockComponent[nTabs];
        }

        @Override
        protected void prepareToGatherDimensions() {
            prepareForLayout(this);
            super.prepareToGatherDimensions();
        }

        @Override
        int calculateAutomaticWidth() {
            return MockVisibleComponent.LENGTH_FILL_PARENT;
        }

        @Override
        int calculateAutomaticHeight() {
            return MockVisibleComponent.LENGTH_FILL_PARENT;
        }

        @Override
        void cleanUp() {
            super.cleanUp();
            tabChildren = null;
        }
    }

    // The color of the drop-target area's border
    private static final String DROP_TARGET_AREA_COLOR = "#0000ff";

    // The DIV element that displays the drop-target area.
    private Element dropTargetArea; // lazily initialized

    // The cell onto which the currently hovering component will be dropped onto.
    private Tab dropTargetTab;

    public MockTabBarLayout() {
        nTabs = 2;
        currentTabIdx = 0;
    }

    public void setCurrentTabIdx(int currentTabIdx) {
        this.currentTabIdx = currentTabIdx;
        container.refreshForm();
    }

    public void setNumberOfTabs(int nTabs) {
        this.nTabs = nTabs;
        container.refreshForm();
    }

    // Drop target area

    private void ensureDropTargetArea() {
        if (dropTargetArea == null) {
            dropTargetArea = DOM.createDiv();
            setDropTargetAreaVisible(false);
            DOM.setStyleAttribute(dropTargetArea, "border", "2px solid " + DROP_TARGET_AREA_COLOR);
            DOM.appendChild(container.getRootPanel().getElement(), dropTargetArea);
        }
    }

    private void setDropTargetTab(Tab tab) {
        dropTargetTab = tab;
        if (dropTargetTab != null) {
            // Display the drop target area at the cell that the user is hovering over.
            setDropTargetAreaBoundsAndShow(
                    0, ComponentConstants.TAB_PREFERRED_HEIGHT,
                    container.getForm().usableScreenWidth, container.getForm().usableScreenHeight);
        } else {
            setDropTargetAreaVisible(false);
        }
    }

    private void setDropTargetAreaVisible(boolean visible) {
        DOM.setStyleAttribute(dropTargetArea, "visibility", visible ? "visible" : "hidden");
    }

    private void setDropTargetAreaBoundsAndShow(int x, int y, int width, int height) {
        DOM.setStyleAttribute(dropTargetArea, "position", "absolute");
        DOM.setStyleAttribute(dropTargetArea, "left", x + "px");
        DOM.setStyleAttribute(dropTargetArea, "top", y + "px");
        DOM.setStyleAttribute(dropTargetArea, "width", width + "px");
        DOM.setStyleAttribute(dropTargetArea, "height", height + "px");
        setDropTargetAreaVisible(true);
    }


    private Tab getTabOfChild(MockComponent child) {
        String tabString = child.getPropertyValue(MockVisibleComponent.PROPERTY_NAME_TAB_IDX);
        return new Tab(Integer.parseInt(tabString));
    }

    private Tab getTabContainingPoint(int x, int y) {
        if (x < 0 || y < ComponentConstants.TAB_PREFERRED_HEIGHT) {
            return null;
        }

        return new Tab(currentTabIdx);
    }

    @Override
    LayoutInfo createContainerLayoutInfo(Map<MockComponent, LayoutInfo> layoutInfoMap) {
        ensureDropTargetArea();
        return new TabBarLayoutInfo(layoutInfoMap,container);
    }

    private void prepareForLayout(TabBarLayoutInfo tabBarLayoutInfo) {
        // Figure out which child (if any) will be in each tab.
        // If multiple children claim to be in the same tab, only the last child in the children list
        // will be visible.

        for (MockComponent child : tabBarLayoutInfo.visibleChildren) {
            Tab tab = getTabOfChild(child);
            if (tab.idx != currentTabIdx) {
                // This child has an tab which is currently inactive.
                child.setVisible(false);
                continue;
            }

            if (tabBarLayoutInfo.tabChildren[tab.idx] != null) {
                // A previous child claimed to be in this tab.
                // Since the rule is that the last child in the children list wins, we hide the previous
                // child. The hidden component will still show up in the Components list.
                MockComponent previousCellChild = tabBarLayoutInfo.tabChildren[tab.idx];
                previousCellChild.setVisible(false);
            }

            tabBarLayoutInfo.tabChildren[tab.idx] = child;
        }

        for (int idx = 0; idx < nTabs; idx++) {
            MockComponent tabChild = tabBarLayoutInfo.tabChildren[idx];
            if (tabChild != null) {
                LayoutInfo childLayoutInfo = tabBarLayoutInfo.layoutInfoMap.get(tabChild);
                childLayoutInfo.width = tabChild.getPreferredWidth();
                childLayoutInfo.height = tabChild.getPreferredHeight();
            }
        }
    }

    @Override
    void layoutChildren(LayoutInfo containerLayoutInfo) {
        TabBarLayoutInfo tabBarLayoutInfo = (TabBarLayoutInfo) containerLayoutInfo;

        for(int tabIdx = 0; tabIdx < nTabs; tabIdx++){
            MockComponent tabChild  = tabBarLayoutInfo.tabChildren[tabIdx];
            if(tabChild!=null){
                LayoutInfo childLayoutInfo = tabBarLayoutInfo.layoutInfoMap.get(tabChild);
                if(tabChild instanceof MockContainer){
                    ((MockContainer) tabChild).getLayout().layoutChildren(childLayoutInfo);
                }
                container.setChildSizeAndPosition(tabChild, childLayoutInfo, 0, ComponentConstants.TAB_PREFERRED_HEIGHT);
            }
        }
    }

    @Override
    void onDragContinue(int x, int y) {
        // Find the cell the user is hovering over.
        setDropTargetTab(getTabContainingPoint(x, y));
    }

    @Override
    void onDragLeave() {
        // Hide the drop target area and clean up.
        setDropTargetTab(null);
    }

    @Override
    boolean onDrop(MockComponent source, int x, int y, int offsetX, int offsetY) {
        if (dropTargetTab != null) {
            Tab destTab = dropTargetTab;

            // Hide the drop target area and clean up.
            setDropTargetTab(null);

            // Perform drop.
            MockContainer srcContainer = source.getContainer();
            if (srcContainer != null) {
                // Pass false to indicate that the component isn't being permanently deleted.
                // It's just being moved from one container to another.
                srcContainer.removeComponent(source, false);
            }
            source.changeProperty(MockVisibleComponent.PROPERTY_NAME_TAB_IDX, "" + destTab.idx);
            container.addComponent(source);
            return true;
        }
        return false;
    }

    @Override
    void dispose() {
        if (dropTargetArea != null) {
            DOM.removeChild(container.getRootPanel().getElement(), dropTargetArea);
        }
    }
}
