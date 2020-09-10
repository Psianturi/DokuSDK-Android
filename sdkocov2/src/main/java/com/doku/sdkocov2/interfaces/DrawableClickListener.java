package com.doku.sdkocov2.interfaces;

/**
 * Created by zaki on 2/16/16.
 */
public interface DrawableClickListener {
    void onClick(DrawablePosition target);
    enum DrawablePosition {TOP, BOTTOM, LEFT, RIGHT}
}
