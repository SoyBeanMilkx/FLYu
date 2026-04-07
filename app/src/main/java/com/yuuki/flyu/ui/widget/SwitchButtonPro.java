package com.yuuki.flyu.ui.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Checkable;

import com.yuuki.flyu.ui.Colors;

public class SwitchButtonPro extends View implements Checkable {
    private static final int DEFAULT_WIDTH = dp2pxInt(62);
    private static final int DEFAULT_HEIGHT = dp2pxInt(42);

    private final int ANIMATE_STATE_NONE = 0;
    private final int ANIMATE_STATE_PENDING_DRAG = 1;
    private final int ANIMATE_STATE_DRAGING = 2;
    private final int ANIMATE_STATE_PENDING_RESET = 3;
    private final int ANIMATE_STATE_PENDING_SETTLE = 4;
    private final int ANIMATE_STATE_SWITCH = 5;

    public SwitchButtonPro(Context context) {
        super(context);
        init(context, null);
    }

    public SwitchButtonPro(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SwitchButtonPro(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public SwitchButtonPro(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    public final void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(0, 0, 0, 0);
    }

    private void init(Context context, AttributeSet attrs) {
        shadowEffect = true;
        isChecked = false;
        showIndicator = true;
        enableEffect = true;
        uncheckCircleWidth = dp2pxInt(0f);
        uncheckCircleOffsetX = dp2px(0);
        uncheckCircleRadius = dp2px(0);
        checkedLineOffsetX = dp2px(0);
        checkedLineOffsetY = dp2px(0);
        shadowRadius = dp2pxInt(5.5f);
        shadowOffset = dp2pxInt(3.5f);
        checkLineWidth = dp2pxInt(0f);
        checkLineLength = dp2px(0);
        borderWidth = dp2pxInt(0);
        shadowColor = 0X1A000000;
        uncheckColor = 0XffE0E0E0;
        checkedColor = Colors.PRIMARY;
        uncheckCircleColor = 0XffBDBDBD;
        checkLineColor = Color.WHITE;
        int buttonColor = Color.WHITE;
        background = Color.WHITE;
        uncheckButtonColor = buttonColor;
        checkedButtonColor = buttonColor;
        int effectDuration = 200;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        buttonPaint.setColor(buttonColor);
        if(shadowEffect){
            buttonPaint.setShadowLayer(
                    shadowRadius,
                    0, shadowOffset,
                    shadowColor);
        }
        viewState = new ViewState();
        beforeState = new ViewState();
        afterState = new ViewState();
        valueAnimator = ValueAnimator.ofFloat(0f, 1f);
        valueAnimator.setDuration(effectDuration);
        valueAnimator.setRepeatCount(0);
        valueAnimator.addUpdateListener(animatorUpdateListener);
        valueAnimator.addListener(animatorListener);
        super.setClickable(true);
        this.setPadding(0, 0, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if(widthMode == MeasureSpec.UNSPECIFIED
                || widthMode == MeasureSpec.AT_MOST){
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_WIDTH, MeasureSpec.EXACTLY);
        }
        if(heightMode == MeasureSpec.UNSPECIFIED
                || heightMode == MeasureSpec.AT_MOST){
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(DEFAULT_HEIGHT, MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        float viewPadding = Math.max(shadowRadius + shadowOffset, borderWidth);
        height = h - viewPadding - viewPadding;
        width = w - viewPadding - viewPadding;
        viewRadius = height * .5f;
        buttonRadius = viewRadius - borderWidth;
        left = viewPadding;
        top = viewPadding;
        right = w - viewPadding;
        bottom = h - viewPadding;
        centerX = (left + right) * .5f;
        centerY = (top + bottom) * .5f;
        buttonMinX = left + viewRadius;
        buttonMaxX = right - viewRadius;
        if(isChecked()){
            setCheckedViewState(viewState);
        }else{
            setUncheckViewState(viewState);
        }
        isUiInited = true;
        postInvalidate();
    }

    private void setUncheckViewState(ViewState viewState){
        viewState.radius = 0;
        viewState.checkStateColor = uncheckColor;
        viewState.checkedLineColor = Color.TRANSPARENT;
        viewState.buttonX = buttonMinX;
        buttonPaint.setColor(uncheckButtonColor);
    }

    private void setCheckedViewState(ViewState viewState){
        viewState.radius = viewRadius;
        viewState.checkStateColor = checkedColor;
        viewState.checkedLineColor = checkLineColor;
        viewState.buttonX = buttonMaxX;
        buttonPaint.setColor(checkedButtonColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setStrokeWidth(borderWidth);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(background);
        drawRoundRect(canvas,
                left, top, right, bottom,
                viewRadius, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(uncheckColor);
        drawRoundRect(canvas,
                left, top, right, bottom,
                viewRadius, paint);
        if(showIndicator){
            drawUncheckIndicator(canvas);
        }
        float des = viewState.radius * .5f;
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(viewState.checkStateColor);
        paint.setStrokeWidth(borderWidth + des * 2f);
        drawRoundRect(canvas,
                left + des, top + des, right - des, bottom - des,
                viewRadius, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(1);
        drawArc(canvas,
                left, top,
                left + 2 * viewRadius, top + 2 * viewRadius,
                90, 180, paint);
        canvas.drawRect(
                left + viewRadius, top,
                viewState.buttonX, top + 2 * viewRadius,
                paint);
        if(showIndicator){
            drawCheckedIndicator(canvas);
        }
        drawButton(canvas, viewState.buttonX, centerY);
    }

    protected void drawCheckedIndicator(Canvas canvas) {
        drawCheckedIndicator(canvas,
                viewState.checkedLineColor,
                checkLineWidth,
                left + viewRadius - checkedLineOffsetX, centerY - checkLineLength,
                left + viewRadius - checkedLineOffsetY, centerY + checkLineLength,
                paint);
    }

    protected void drawCheckedIndicator(Canvas canvas,
                                        int color,
                                        float lineWidth,
                                        float sx, float sy, float ex, float ey,
                                        Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(lineWidth);
        canvas.drawLine(
                sx, sy, ex, ey,
                paint);
    }

    private void drawUncheckIndicator(Canvas canvas) {
        drawUncheckIndicator(canvas,
                uncheckCircleColor,
                uncheckCircleWidth,
                right - uncheckCircleOffsetX, centerY,
                uncheckCircleRadius,
                paint);
    }

    protected void drawUncheckIndicator(Canvas canvas,
                                        int color,
                                        float lineWidth,
                                        float centerX, float centerY,
                                        float radius,
                                        Paint paint) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(color);
        paint.setStrokeWidth(lineWidth);
        canvas.drawCircle(centerX, centerY, radius, paint);
    }

    private void drawArc(Canvas canvas,
                         float left, float top,
                         float right, float bottom,
                         float startAngle, float sweepAngle,
                         Paint paint){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawArc(left, top, right, bottom,
                    startAngle, sweepAngle, true, paint);
        }else{
            rect.set(left, top, right, bottom);
            canvas.drawArc(rect,
                    startAngle, sweepAngle, true, paint);
        }
    }

    private void drawRoundRect(Canvas canvas,
                               float left, float top,
                               float right, float bottom,
                               float backgroundRadius,
                               Paint paint){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(left, top, right, bottom,
                    backgroundRadius, backgroundRadius, paint);
        }else{
            rect.set(left, top, right, bottom);
            canvas.drawRoundRect(rect,
                    backgroundRadius, backgroundRadius, paint);
        }
    }

    private void drawButton(Canvas canvas, float x, float y) {
        canvas.drawCircle(x, y, buttonRadius, buttonPaint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
        paint.setColor(0XffDDDDDD);
        canvas.drawCircle(x, y, buttonRadius, paint);
    }

    @Override
    public void setChecked(boolean checked) {
        if(checked == isChecked()){
            postInvalidate();
            return;
        }
        toggle(enableEffect, true);
    }

    public void setCheckedSilent(boolean checked) {
        if(checked == isChecked()){
            postInvalidate();
            return;
        }
        toggle(enableEffect, false);
    }

    public void setCheckedImmediately(boolean checked) {
        if (valueAnimator.isRunning()) {
            valueAnimator.cancel();
        }
        animateState = ANIMATE_STATE_NONE;
        isChecked = checked;
        if (isUiInited) {
            if (isChecked) {
                setCheckedViewState(viewState);
            } else {
                setUncheckViewState(viewState);
            }
        }
        postInvalidate();
    }

    @Override
    public boolean isChecked() {
        return isChecked;
    }

    @Override
    public void toggle() {
        toggle(true);
    }

    public void toggle(boolean animate) {
        toggle(animate, true);
    }

    private boolean pendingBroadcast = false;

    private void toggle(boolean animate, boolean broadcast) {
        if(!isEnabled()){return;}
        if(isEventBroadcast){
            throw new RuntimeException("should NOT switch the state in method: [onCheckedChanged]!");
        }
        if(!isUiInited){
            isChecked = !isChecked;
            if(broadcast){
                broadcastEvent();
            }
            return;
        }
        if(valueAnimator.isRunning()){
            valueAnimator.cancel();
        }
        pendingBroadcast = broadcast;
        if(!enableEffect || !animate){
            isChecked = !isChecked;
            if(isChecked()){
                setCheckedViewState(viewState);
            }else{
                setUncheckViewState(viewState);
            }
            postInvalidate();
            if(broadcast){
                broadcastEvent();
            }
            return;
        }
        animateState = ANIMATE_STATE_SWITCH;
        beforeState.copy(viewState);
        if(isChecked()){
            setUncheckViewState(afterState);
        }else{
            setCheckedViewState(afterState);
        }
        valueAnimator.start();
    }

    private void broadcastEvent() {
        if(onCheckedChangeListener != null){
            isEventBroadcast = true;
            onCheckedChangeListener.onCheckedChanged(this, isChecked());
        }
        isEventBroadcast = false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(!isEnabled()){return false;}
        if(event.getActionMasked() == MotionEvent.ACTION_UP){
            toggle();
        }
        return true;
    }

    private boolean isInAnimating(){
        return animateState != ANIMATE_STATE_NONE;
    }

    private boolean isPendingDragState(){
        return animateState == ANIMATE_STATE_PENDING_DRAG
                || animateState == ANIMATE_STATE_PENDING_RESET;
    }

    private boolean isDragState(){
        return animateState == ANIMATE_STATE_DRAGING;
    }

    public void setShadowEffect(boolean shadowEffect) {
        if(this.shadowEffect == shadowEffect){return;}
        this.shadowEffect = shadowEffect;
        if(this.shadowEffect){
            buttonPaint.setShadowLayer(
                    shadowRadius,
                    0, shadowOffset,
                    shadowColor);
        }else{
            buttonPaint.setShadowLayer(
                    0,
                    0, 0,
                    0);
        }
    }

    public void setEnableEffect(boolean enable){
        this.enableEffect = enable;
    }

    private void pendingDragState() {
        if(isInAnimating()){return;}
        if(!isTouchingDown){return;}
        if(valueAnimator.isRunning()){
            valueAnimator.cancel();
        }
        animateState = ANIMATE_STATE_PENDING_DRAG;
        beforeState.copy(viewState);
        afterState.copy(viewState);
        if(isChecked()){
            afterState.checkStateColor = checkedColor;
            afterState.buttonX = buttonMaxX;
            afterState.checkedLineColor = checkedColor;
        }else{
            afterState.checkStateColor = uncheckColor;
            afterState.buttonX = buttonMinX;
            afterState.radius = viewRadius;
        }
        valueAnimator.start();
    }

    private void pendingCancelDragState() {
        if(isDragState() || isPendingDragState()){
            if(valueAnimator.isRunning()){
                valueAnimator.cancel();
            }
            animateState = ANIMATE_STATE_PENDING_RESET;
            beforeState.copy(viewState);
            if(isChecked()){
                setCheckedViewState(afterState);
            }else{
                setUncheckViewState(afterState);
            }
            valueAnimator.start();
        }
    }

    private void pendingSettleState() {
        if(valueAnimator.isRunning()){
            valueAnimator.cancel();
        }
        animateState = ANIMATE_STATE_PENDING_SETTLE;
        beforeState.copy(viewState);
        if(isChecked()){
            setCheckedViewState(afterState);
        }else{
            setUncheckViewState(afterState);
        }
        valueAnimator.start();
    }

    @Override
    public final void setOnClickListener(OnClickListener l) {}

    @Override
    public final void setOnLongClickListener(OnLongClickListener l) {}

    public void setOnCheckedChangeListener(OnCheckedChangeListener l){
        onCheckedChangeListener = l;
    }

    public interface OnCheckedChangeListener{
        void onCheckedChanged(SwitchButtonPro view, boolean isChecked);
    }

    private static float dp2px(float dp){
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    private static int dp2pxInt(float dp){
        return (int) dp2px(dp);
    }

    private static int optInt(TypedArray typedArray,
                              int index,
                              int def) {
        if(typedArray == null){return def;}
        return typedArray.getInt(index, def);
    }

    private static float optPixelSize(TypedArray typedArray,
                                      int index,
                                      float def) {
        if(typedArray == null){return def;}
        return typedArray.getDimension(index, def);
    }

    private static int optPixelSize(TypedArray typedArray,
                                    int index,
                                    int def) {
        if(typedArray == null){return def;}
        return typedArray.getDimensionPixelOffset(index, def);
    }

    private static int optColor(TypedArray typedArray,
                                int index,
                                int def) {
        if(typedArray == null){return def;}
        return typedArray.getColor(index, def);
    }

    private static boolean optBoolean(TypedArray typedArray,
                                      int index,
                                      boolean def) {
        if(typedArray == null){return def;}
        return typedArray.getBoolean(index, def);
    }

    private int shadowRadius;
    private int shadowOffset;
    private int shadowColor ;

    private float viewRadius;
    private float buttonRadius;

    private float height ;
    private float width;
    private float left   ;
    private float top    ;
    private float right  ;
    private float bottom ;
    private float centerX;
    private float centerY;

    private int background;
    private int uncheckColor;
    private int checkedColor;
    private int borderWidth;

    private int checkLineColor;
    private int checkLineWidth;
    private float checkLineLength;
    private int uncheckCircleColor;
    private int uncheckCircleWidth;
    private float uncheckCircleOffsetX;
    private float uncheckCircleRadius;
    private float checkedLineOffsetX;
    private float checkedLineOffsetY;
    private int uncheckButtonColor;
    private int checkedButtonColor;

    private float buttonMinX;
    private float buttonMaxX;

    private Paint buttonPaint;
    private Paint paint;

    private ViewState viewState;
    private ViewState beforeState;
    private ViewState afterState;

    private RectF rect = new RectF();
    private int animateState = ANIMATE_STATE_NONE;

    private ValueAnimator valueAnimator;

    private final android.animation.ArgbEvaluator argbEvaluator
            = new android.animation.ArgbEvaluator();

    private boolean isChecked;
    private boolean enableEffect;
    private boolean shadowEffect;
    private boolean showIndicator;
    private boolean isTouchingDown = false;
    private boolean isUiInited = false;
    private boolean isEventBroadcast = false;

    private OnCheckedChangeListener onCheckedChangeListener;

    private long touchDownTime;
    private float touchDownX;

    private Runnable postPendingDrag = () -> {
        if(!isInAnimating()){
            pendingDragState();
        }
    };

    private ValueAnimator.AnimatorUpdateListener animatorUpdateListener
            = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            float value = (Float) animation.getAnimatedValue();
            switch (animateState) {
                case ANIMATE_STATE_PENDING_SETTLE: {
                }
                case ANIMATE_STATE_PENDING_RESET: {
                }
                case ANIMATE_STATE_PENDING_DRAG: {
                    viewState.checkedLineColor = (int) argbEvaluator.evaluate(
                            value,
                            beforeState.checkedLineColor,
                            afterState.checkedLineColor
                    );

                    viewState.radius = beforeState.radius
                            + (afterState.radius - beforeState.radius) * value;

                    if(animateState != ANIMATE_STATE_PENDING_DRAG){
                        viewState.buttonX = beforeState.buttonX
                                + (afterState.buttonX - beforeState.buttonX) * value;
                    }

                    viewState.checkStateColor = (int) argbEvaluator.evaluate(
                            value,
                            beforeState.checkStateColor,
                            afterState.checkStateColor
                    );

                    break;
                }
                case ANIMATE_STATE_SWITCH: {
                    viewState.buttonX = beforeState.buttonX
                            + (afterState.buttonX - beforeState.buttonX) * value;

                    float fraction = (viewState.buttonX - buttonMinX) / (buttonMaxX - buttonMinX);

                    viewState.checkStateColor = (int) argbEvaluator.evaluate(
                            fraction,
                            uncheckColor,
                            checkedColor
                    );

                    viewState.radius = fraction * viewRadius;
                    viewState.checkedLineColor = (int) argbEvaluator.evaluate(
                            fraction,
                            Color.TRANSPARENT,
                            checkLineColor
                    );
                    break;
                }
                default:
                case ANIMATE_STATE_DRAGING: {
                }
                case ANIMATE_STATE_NONE: {
                    break;
                }
            }
            postInvalidate();
        }
    };

    private Animator.AnimatorListener animatorListener
            = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            switch (animateState) {
                case ANIMATE_STATE_PENDING_DRAG: {
                    animateState = ANIMATE_STATE_DRAGING;
                    viewState.checkedLineColor = Color.TRANSPARENT;

                    postInvalidate();
                    break;
                }
                case ANIMATE_STATE_PENDING_RESET: {
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    break;
                }
                case ANIMATE_STATE_PENDING_SETTLE: {
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    if(pendingBroadcast) {
                        broadcastEvent();
                    }
                    break;
                }
                case ANIMATE_STATE_SWITCH: {
                    isChecked = !isChecked;
                    animateState = ANIMATE_STATE_NONE;
                    postInvalidate();
                    if(pendingBroadcast) {
                        broadcastEvent();
                    }
                    break;
                }
                default:
                case ANIMATE_STATE_NONE:
                case ANIMATE_STATE_DRAGING: {
                    break;
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    private static class ViewState {

        float buttonX;

        int checkStateColor;

        int checkedLineColor;

        float radius;
        ViewState(){}
        private void copy(ViewState source){
            this.buttonX = source.buttonX;
            this.checkStateColor = source.checkStateColor;
            this.checkedLineColor = source.checkedLineColor;
            this.radius = source.radius;
        }
    }

}