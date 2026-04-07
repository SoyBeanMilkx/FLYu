package com.yuuki.flyu.ui.widget.collapse;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.view.View;

final class IndentGuideView extends View {

    private final Paint paint;

    IndentGuideView(Context context) {
        super(context);
        float density = context.getResources().getDisplayMetrics().density;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0x28000000);
        paint.setStrokeWidth(density);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(
                new float[]{4 * density, 3 * density}, 0));
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float x = getWidth() / 2f;
        canvas.drawLine(x, 0, x, getHeight(), paint);
        if (getLayerType() != LAYER_TYPE_HARDWARE) {
            post(() -> setLayerType(LAYER_TYPE_HARDWARE, null));
        }
    }
}
