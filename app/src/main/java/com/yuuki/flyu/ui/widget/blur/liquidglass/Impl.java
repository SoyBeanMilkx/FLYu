package com.yuuki.flyu.ui.widget.blur.liquidglass;

import android.graphics.Canvas;

public interface Impl {
    void onSizeChanged(int w, int h);
    void onPreDraw();
    void draw(Canvas c);
    default void dispose() {}
}
