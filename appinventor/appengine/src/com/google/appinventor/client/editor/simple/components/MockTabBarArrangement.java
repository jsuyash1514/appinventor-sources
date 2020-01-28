package com.google.appinventor.client.editor.simple.components;

import com.google.appinventor.client.editor.simple.SimpleEditor;
import com.google.appinventor.components.common.ComponentConstants;
import com.google.gwt.user.client.ui.*;

/**
 * Mock TabBarArrangement component.
 *
 * @author jsuyash1514@gmail.com (Suyash Jain)
 */

public final class MockTabBarArrangement extends MockContainer {

    /**
     * Component type name.
     */
    public static final String TYPE = "TabBarArrangement";
    private static final String DEFAULT_TAB_TEXT_COLOR_ACTIVE = "&HFFFFFFFF";
    private static final String DEFAULT_TAB_TEXT_COLOR_INACTIVE = "&HFFFFFF77";
    private static final String DEFAULT_TAB_BG_COLOR_ACTIVE = ComponentConstants.DEFAULT_PRIMARY_DARK_COLOR;
    private static final String DEFAULT_TAB_BG_COLOR_INACTIVE = ComponentConstants.DEFAULT_PRIMARY_COLOR;
    // Form UI components
    private final AbsolutePanel layoutWidget;
    private HorizontalPanel tabViewWidget;
    private InlineLabel labelInItem;
    private SimplePanel panelForItem;
    private String[] currentTabLabels;
    private int currentTabIndex = 0;
    private int nTabs = 2;

    /**
     * Creates a new MockTabBarArrangement component.
     *
     * @param editor editor of source file the component belongs to
     */
    public MockTabBarArrangement(SimpleEditor editor) {
        super(editor, TYPE, images.tablayout(), new MockTabBarLayout());


        layoutWidget = new AbsolutePanel();
        layoutWidget.setSize("100%", "100%");
        layoutWidget.setStylePrimaryName("ode-SimpleMockContainer");

        tabViewWidget = new HorizontalPanel();
        tabViewWidget.setSize("100%", ComponentConstants.TAB_PREFERRED_HEIGHT + "px");
        tabViewWidget.setStylePrimaryName("ode-SimpleMockComponent");
        tabViewWidget.setStyleName("tabBarViewComponentStyle", true);
        layoutWidget.add(tabViewWidget,0,0);

        rootPanel.setSize("100%", "100%");
        rootPanel.setStylePrimaryName("ode-SimpleMockContainer");
        rootPanel.setStyleName("tabBarContainerStyle", true);
        layoutWidget.add(rootPanel,0,0);


        initComponent(layoutWidget);
        MockComponentsUtil.setWidgetBackgroundColor(tabViewWidget, ComponentConstants.DEFAULT_PRIMARY_COLOR);
        setElementsFromStringProperty("");
    }

    public void removeComponent(MockComponent component, boolean permanentlyDeleted) {
        component.changeProperty(MockVisibleComponent.PROPERTY_NAME_TAB_IDX,
                "" + ComponentConstants.DEFAULT_TAB);
        component.changeProperty(MockVisibleComponent.PROPERTY_NAME_COLUMN,
                "" + ComponentConstants.DEFAULT_TAB);
        super.removeComponent(component, permanentlyDeleted);
    }

    /*
     * Sets the text to be added in the listview
     */
    private void setElementsFromStringProperty(String text) {
        currentTabLabels = text.split(",");

        tabViewWidget.clear();

        for (int i = 0; i < nTabs; i++) {
            createLabelItem(i);
            createLabelPanel(i);
        }

    }

    private void createLabelItem(int i) {
        if (i < currentTabLabels.length && !currentTabLabels[i].equals(""))
            labelInItem = new InlineLabel(currentTabLabels[i]);
        else labelInItem = new InlineLabel("Tab " + String.valueOf(i + 1));
        labelInItem.setSize(ComponentConstants.TAB_PREFERRED_WIDTH + "px", ComponentConstants.TAB_PREFERRED_HEIGHT + "px");
        labelInItem.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
        if (i == currentTabIndex) {
            MockComponentsUtil.setWidgetTextColorWithAlpha(labelInItem, DEFAULT_TAB_TEXT_COLOR_ACTIVE);
        } else {
            MockComponentsUtil.setWidgetTextColorWithAlpha(labelInItem, DEFAULT_TAB_TEXT_COLOR_INACTIVE);
        }
    }

    private void createLabelPanel(int i) {
        panelForItem = new SimplePanel();
        panelForItem.setStylePrimaryName("tabItemStyle");
        panelForItem.setSize(ComponentConstants.TAB_PREFERRED_WIDTH + "px",
                ComponentConstants.TAB_PREFERRED_HEIGHT + "px");
        if (i == currentTabIndex) {
            MockComponentsUtil.setWidgetBackgroundColor(panelForItem, DEFAULT_TAB_BG_COLOR_ACTIVE);
        } else {
            MockComponentsUtil.setWidgetBackgroundColor(panelForItem, DEFAULT_TAB_BG_COLOR_INACTIVE);
        }
        panelForItem.add(labelInItem);
        tabViewWidget.add(panelForItem);
    }

    private void setCurrentTabIndex(String newValue) {
        try {
            int currIdx = Integer.parseInt(newValue);
            if (currIdx >= 0 && currIdx < currentTabLabels.length) {
                currentTabIndex = currIdx;
                ((MockTabBarLayout) layout).setCurrentTabIdx(currIdx);
            }
        } catch (NumberFormatException e) {
            // Ignore this. If we throw an exception here, the project is unrecoverable.
        }
    }

    private void setNumberOfTabs(String newValue) {
        try {
            int numberOfTabs = Integer.parseInt(newValue);
            if (numberOfTabs >= 2) {
                nTabs = numberOfTabs;
                ((MockTabBarLayout) layout).setNumberOfTabs(numberOfTabs);
            }
        } catch (NumberFormatException e) {
            // Ignore this. If we throw an exception here, the project is unrecoverable.
        }
    }

    @Override
    public void onPropertyChange(String propertyName, String newValue) {
        super.onPropertyChange(propertyName, newValue);

        // Apply changed properties to the mock component
        if (propertyName.equals(PROPERTY_NAME_TAB_LABEL)) {
            setElementsFromStringProperty(newValue);
            refreshForm();
        } else if (propertyName.equals(PROPERTY_NAME_NUMBER_OF_TABS)) {
            setNumberOfTabs(newValue);

        } else if (propertyName.equals(PROPERTY_NAME_TAB_IDX)) {
            setCurrentTabIndex(newValue);
        }

    }
}
