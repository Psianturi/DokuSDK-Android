package com.doku.sdkocov2.model;

import android.graphics.drawable.Drawable;

/**
 * Created by zaki on 2/29/16.
 */
public class LayoutItems {

    String fontPath;
    String toolbarColor;
    String toolbarTextColor;
    String fontColor;
    String backgroundColor;
    String labelTextColor;
    String buttonTextColor;
    Drawable buttonBackground;

    public LayoutItems() {}

    public String getButtonTextColor() {
        return buttonTextColor;
    }

    public void setButtonTextColor(String buttonTextColor) {
        this.buttonTextColor = buttonTextColor;
    }

    public Drawable getButtonBackground() {
        return buttonBackground;
    }

    public void setButtonBackground(Drawable buttonBackground) {
        this.buttonBackground = buttonBackground;
    }

    public String getLabelTextColor() {
        return labelTextColor;
    }

    public void setLabelTextColor(String labelTextColor) {
        this.labelTextColor = labelTextColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getFontColor() {
        return fontColor;
    }

    public void setFontColor(String fontColor) {
        this.fontColor = fontColor;
    }

    public String getToolbarTextColor() {
        return toolbarTextColor;
    }

    public void setToolbarTextColor(String toolbarTextColor) {
        this.toolbarTextColor = toolbarTextColor;
    }

    public String getToolbarColor() {
        return toolbarColor;
    }

    public void setToolbarColor(String toolbarColor) {
        this.toolbarColor = toolbarColor;
    }

    public String getFontPath() {
        return fontPath;
    }

    public void setFontPath(String fontPath) {
        this.fontPath = fontPath;
    }

}
