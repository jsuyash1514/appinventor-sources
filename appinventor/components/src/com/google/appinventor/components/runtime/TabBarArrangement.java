package com.google.appinventor.components.runtime;

import android.app.Activity;
import android.view.View;
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.YaVersion;


@DesignerComponent(version = YaVersion.TAB_BAR_COMPONENT_VERSION,
        description = "<p>A formatting element in which to place components " +
                "that should be displayed in tab form.</p>",
        category = ComponentCategory.LAYOUT)
@SimpleObject
public class TabBarArrangement extends AndroidViewComponent
        implements Component, ComponentContainer {
    /**
     * Creates a new AndroidViewComponent.
     *
     * @param container container, component will be placed in
     */
    protected TabBarArrangement(ComponentContainer container) {
        super(container);
    }

    @Override
    public View getView() {
        return null;
    }

    @Override
    public Activity $context() {
        return null;
    }

    @Override
    public Form $form() {
        return null;
    }

    @Override
    public void $add(AndroidViewComponent component) {

    }

    @Override
    public void setChildWidth(AndroidViewComponent component, int width) {

    }

    @Override
    public void setChildHeight(AndroidViewComponent component, int height) {

    }
}
