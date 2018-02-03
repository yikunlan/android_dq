package com.modo.core.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.core_library.R;

public class MyProgressBar extends View {
    private final static String TAG = MyProgressBar.class.getSimpleName();
    private Paint mPaint;
    RectF oval;
    private float currProgress;  // 当前进度
    private String textmessage = ""; // 显示的消息

    public String getTextmessage() {
        return textmessage;
    }

    public void setTextmessage(String textmessage) {
        this.textmessage = textmessage;
    }


    public float getCurrProgress() {
        return currProgress;
    }

    public void setCurrProgress(float currProgress) {
        this.currProgress = currProgress;
    }

//    Bitmap t_bg = BitmapFactory.decodeResource(getResources(),R.drawable.background); // 整张图的背景
    Bitmap t_bg = BitmapFactory.decodeResource(getResources(), R.drawable.loading00001);

//    Bitmap pg_logo = BitmapFactory.decodeResource(getResources(), R.drawable.logo); // logo
//    Bitmap pg_txtmu = BitmapFactory.decodeResource(getResources(), R.drawable.progressbar_mu); // 奇迹文字图标
//    Bitmap pg_webzen = BitmapFactory.decodeResource(getResources(), R.drawable.progressbar_weapon); // webzen标识

    Bitmap pg_bar = BitmapFactory.decodeResource(getResources(), R.drawable.progressbar_bak); // 进度条灰色背景
    Bitmap pg_all = BitmapFactory.decodeResource(getResources(), R.drawable.progressbar_all); // 进度条黄色进度
    Bitmap pg_fillet = BitmapFactory.decodeResource(getResources(), R.drawable.progressbar_fillet); // 进度条圆角
    Bitmap pg_part = BitmapFactory.decodeResource(getResources(), R.drawable.progressbar_part); // 进度条进度
    Bitmap pg_point = BitmapFactory.decodeResource(getResources(), R.drawable.progressbar_point); // 进度条闪光效果
    Bitmap pg_weapon = BitmapFactory.decodeResource(getResources(), R.drawable.progressbar_weapon); // 进度条武器

    public MyProgressBar(Context context) {
        super(context);
        init();
    }

    public MyProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (currProgress>=100) {
            currProgress = 100;
        }
        float totalLength = pg_all.getWidth() - pg_fillet.getWidth(); //可用的进度条进度
        float totalBL = totalLength / pg_part.getWidth();       // 算出多少个可以铺满整个进度条
        float currentBL = totalBL / 100 * currProgress == 0 ? 1 : totalBL / 100 * currProgress;       //计算当前进度应该屏幕多少个
        int currentWidth = (int) (currentBL * pg_part.getWidth()); //计算宽度
        Bitmap newBitmap = createRepeater(currentWidth, pg_part); //当前进度

        float diff_w = (pg_bar.getWidth() - pg_all.getWidth()) / 2;         // 进度条背景和进度条内容宽差
        float diff_h = (pg_bar.getHeight() - pg_all.getHeight()) / 2;       // 进度条背景和进度条内容高差

//        float left_logo = getWidth() - pg_logo.getWidth() / 2;              // log背景图左边距
//        float top_logo  = (getHeight() - pg_logo.getHeight()) / 3 * 2;      // logo背景图上边距

        float left_bak = (getWidth() - pg_bar.getWidth()) / 2;          // 进度条背景左边距，居中显示
        float top_bak = (getHeight() - pg_bar.getHeight()) / 6 * 5;   // 进度条背景上边距，居屏幕5/6处

        float text_size = getWidth()/30;                                      // 文字字体大小
        float text_left = (getWidth() - textmessage.length() * text_size) / 2; // 文字说明的左边距，居中显示
        float text_top = top_bak + text_size * 2.6f;                               // 文字说明的上边距

        float left_fillet = left_bak + diff_w;                            // 圆角左边距
        float top_fillet = top_bak + diff_h;                              // 圆角上边距

        float left_part = left_fillet + pg_fillet.getWidth();               // 进度条左边距
        float top_part = top_fillet;                                // 进度条上边距

        float top_weapon = top_bak - pg_weapon.getHeight() + pg_weapon.getHeight() / 10;        // 武器上边距
        float left_weapon = left_bak + diff_w + newBitmap.getWidth();               // 武器左边距

        float left_point = left_weapon - 10;                                                      // 进度条闪光点左边距，等于武器左边距
        float top_point = top_bak + diff_h - (pg_point.getHeight() - pg_part.getHeight()) / 2;         // 进度条闪光点上边距，等于进度条内容上边距

//        Rect src = new Rect();// 图片 >>原矩形
        Rect dst = new Rect();// 屏幕 >>目标矩形
        dst.left = 0;
        dst.top = 0;
        dst.right = getWidth();
        dst.bottom = getHeight();
        // 画出指定的位图，位图将自动--》缩放/自动转换，以填补目标矩形
        // 这个方法的意思就像 将一个位图按照需求重画一遍，画后的位图就是我们需要的了
        canvas.drawBitmap(t_bg, null, dst, mPaint);  // 绘制整个背景
//        src = null;
        dst = null;

        canvas.drawBitmap(pg_bar, left_bak, top_bak, mPaint); //绘制进度条背景

        canvas.drawBitmap(pg_fillet, left_fillet, top_fillet, mPaint); // 绘制进度条圆角

        canvas.drawBitmap(pg_weapon, left_weapon - 2, top_weapon, mPaint); // 绘制武器

        canvas.drawBitmap(newBitmap, left_part, top_part, mPaint);  // 绘制当前进度条

        canvas.drawBitmap(pg_point, left_point, top_point, mPaint); // 绘制进度条闪光点

        mPaint.setTypeface(Typeface.SANS_SERIF);
        mPaint.setTextSize(text_size);
        mPaint.setColor(Color.rgb(53,54,73));
        canvas.drawText(textmessage, text_left, text_top, mPaint); // 绘制文字
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        oval = new RectF();
    }


    public static Bitmap createRepeater(int width, Bitmap src) {
        if (width <= 1) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }
        int count = (width + src.getWidth() - 1) / src.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, src.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        for (int idx = 0; idx < count; ++idx) {
            canvas.drawBitmap(src, idx * src.getWidth(), 0, null);
        }
        return bitmap;
    }
}
