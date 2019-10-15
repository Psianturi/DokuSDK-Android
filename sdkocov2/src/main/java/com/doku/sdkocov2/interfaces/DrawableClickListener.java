package com.doku.sdkocov2.interfaces;

/**
 * Created by zaki on 2/16/16.
 */
public interface DrawableClickListener {

    public void onClick(DrawablePosition target);


    public static enum DrawablePosition {TOP, BOTTOM, LEFT, RIGHT}

}
