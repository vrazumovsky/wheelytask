package ru.razomovsky.wheelytask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by vadim on 25/10/16.
 */

public class MarkerView extends RelativeLayout {

    public MarkerView(Context context) {
        super(context);
        initView(context);
    }

    public MarkerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MarkerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    void initView(Context context) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        layoutInflater.inflate(R.layout.view_cab_marker, this);
    }

    public Bitmap createBitmap() {

        setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));

        measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        layout(0, 0, getMeasuredWidth(), getMeasuredHeight());

        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(),
                getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);

        draw(c);
        return bitmap;
    }


    public void setCabId(int id) {
        TextView textView = (TextView) findViewById(R.id.cab_id);
        textView.setText(String.valueOf(id));
    }
}
