package stm.msu.mapa.stm_andzela_mario_klient;

import android.content.Context;


import java.util.ArrayList;
        import java.util.List;

        import android.annotation.SuppressLint;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Path;
        import android.util.AttributeSet;
        import android.view.MotionEvent;

public class MapView extends android.support.v7.widget.AppCompatImageView {

    private int color = Color.parseColor("#000000");
    private float width = 4f;
    private List<Holder> holderList = new ArrayList<Holder>();
    float startX;
    float startY;
    float tempX;
    float tempY;
    float endX;
    float endY;

    private class Holder {
        Path path;
        Paint paint;
        float x;
        float y;

        Holder(int color, float width) {
            path = new Path();
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(width);
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }
        Holder(int color, float width, float x, float y) {
            this.x = x;
            this.y = y;
            path = new Path();
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setStrokeWidth(width);
            paint.setColor(color);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
        }
    }

    public MapView(Context context) {
        super(context);
        init();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MapView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        holderList.add(new Holder(color, width));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Holder holder : holderList) {
            canvas.drawRect(startX, startY, tempX, tempY, holder.paint);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float eventX = event.getX();
        float eventY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = eventX;
                startY = eventY;
                holderList.add(new Holder(color,width, eventX, eventY));
                holderList.get(holderList.size() - 1).path.moveTo(eventX, eventY);
                return true;
            case MotionEvent.ACTION_MOVE:
                holderList.add(new Holder(color,width, eventX, eventY));
                tempX = eventX;
                tempY = eventY;
                holderList.get(holderList.size() - 1).path.lineTo(eventX, eventY);
                break;
            case MotionEvent.ACTION_UP:
                endX = eventX;
                endY = eventY;
                break;
            default:
                return false;
        }

        invalidate();
        return true;
    }

    public void resetPaths() {
        for (Holder holder : holderList) {
            holder.path.reset();
        }
        invalidate();
    }

    public void setBrushColor(int color) {
        this.color = color;
    }

    public void setWidth(float width) {
        this.width = width;
    }
}