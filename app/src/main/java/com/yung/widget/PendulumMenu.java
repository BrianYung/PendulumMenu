package com.yung.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;

/**
 * 菜单功能界面
 * Created by Brian on 2016-10-14.
 */

public class PendulumMenu extends View {
    private float speed = 0.3f;
    private int speedduration = 20;
    private int graph = 0;
    private int circlesize = 0;//图形尺寸(正方形)
    private int stroke = 0;//线条长度
    private int strokesize = 0;//线条宽度
    private SparseArray<Integer> arrres;
    private SparseArray<Integer> linecolors;

    private SparseArray<CircleCollision> listc;//记录各个子控件摆动情况
    private int degress = 20;//左右旋转度数
    private int width = 0, height = 0;//控件本身的高宽
    private Paint linepaint;

    private CircleCollisionTesting collision;
    private onMenuItemListener monMenuItemListener;


    public PendulumMenu(Context context) {
        this(context, null);
    }

    public PendulumMenu(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.PendulumMenuDefalut);
    }

    public PendulumMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        listc = new SparseArray<CircleCollision>();
        iniTypeArray(context.obtainStyledAttributes(attrs, R.styleable.PendulumMenu, defStyleAttr, R.style.PendulumMenuDefalutValues));
    }

    /**
     * 自定义控件的属性处理
     *
     * @param tp
     */
    private void iniTypeArray(TypedArray tp) {
        speed = tp.getFloat(R.styleable.PendulumMenu_speed, speed);
        graph = tp.getInt(R.styleable.PendulumMenu_graph, 0);
        speedduration = tp.getInt(R.styleable.PendulumMenu_speedduration, speedduration);
        circlesize = tp.getDimensionPixelOffset(R.styleable.PendulumMenu_circlesize, 20);
        stroke = tp.getDimensionPixelOffset(R.styleable.PendulumMenu_stroke, 20);
        strokesize = tp.getDimensionPixelOffset(R.styleable.PendulumMenu_strokesize, 2);
        //必须释放
        tp.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthmode = MeasureSpec.getMode(widthMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        int heightmode = MeasureSpec.getMode(heightMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        //不为精确匹配时直接赋值屏幕宽度
        if (widthmode != MeasureSpec.EXACTLY) {
            width = getScreenWidth();
        } else//精确模式时，取内部图形总宽度与控件宽度之间的最大值
            width = width > circlesize * getChildCount() ? width : circlesize * getChildCount();
        //高度计算（精确模式时，取内部图形与控件高度之间的最大值）
        if (heightmode == MeasureSpec.EXACTLY)
            height = height > (circlesize + stroke) ? height : (circlesize + stroke);
        else//高度为不精确模式时，取内部图形高度
            height = circlesize + stroke;
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 0; i < getChildCount(); i++) {
            CircleCollision pp = getPoint(i);
            canvas.drawLine(pp.getIniX(), pp.getIniY(), pp.getCenterX(), pp.getCenterY(), getLinePaint(i));
            canvas.drawBitmap(pp.getBitmap(), pp.getCenterX() - circlesize / 2, pp.getCenterY() - circlesize / 2, new Paint());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (monMenuItemListener != null)
                monMenuItemListener.onMenuClick(getTouchItem(event.getX(), event.getY()));
        }
        //此处只有直接返回true才能对手指抬起的坐标点进行获取
        return true;
    }

    public void start() {
        //绘画完成进行开始进行刷新界面，展示动画
        handler.sendEmptyMessageDelayed(1, speedduration);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                invalidate();
                if (collision == null)
                    collision = CircleCollisionTesting.getInstance();
                collision.setonCollisionListener(new CircleCollisionTesting.onCollisionListener() {
                    @Override
                    public void onCollision(int index, boolean isCollision) {
                        if (isCollision)//进行了碰撞
                            collisionOprate(index);
                    }
                });
                collision.setCollisionList(listc);
                handler.sendEmptyMessageDelayed(1, speedduration);
            }
        }
    };

    private Paint getLinePaint(int index) {
        if (linepaint == null) {
            linepaint = new Paint();
            linepaint.setStrokeWidth(strokesize);
            linepaint.setAntiAlias(true);
            //断点出为圆角
            linepaint.setStrokeCap(Paint.Cap.ROUND);
        }
        linepaint.setColor(linecolors.get(index));
        return linepaint;
    }

    /**
     * 碰撞检测操作
     */
    private void collisionOprate(int index) {
        //排除刚好位于最大角度时，此时的摆动标志right会自动更改（若此处在人为进行修改，则会出现角度出现大于degress的情况）
        if (index > listc.size() || index == -1 || Math.abs(listc.get(index).getRadians()) >= degress)
            return;//操作索引判定
        //确定为碰撞后，更改图形摆动方向
        listc.get(index).setRight(!listc.get(index).isRight());
    }

    /**
     * 根据传入的xy坐标返回对应的点击项
     *
     * @param touchx
     * @param touchy
     * @return
     */
    private int getTouchItem(float touchx, float touchy) {
        int index = -1;
        for (int i = 0; i < listc.size(); i++) {
            float xdis = listc.get(i).getCenterX() - touchx;
            float ydis = listc.get(i).getCenterY() - touchy;
            //如果点击点位于bitmap的圆形区域内，则出发对应的点击事件(两点间距离进行判断)
            if (xdis * xdis + ydis * ydis <= listc.get(i).getRadius() * listc.get(i).getRadius()) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 根据子菜单索引获取对应的坐标
     *
     * @param index
     * @return
     */
    private CircleCollision getPoint(int index) {
        CircleCollision pp;
        //对当前控件进行缓存处理
        if (listc.get(index) == null) {
            pp = new CircleCollision();
            Bitmap bmp = getBitmap(arrres.get(index));
            pp.setBitmap(bmp);
            //不摆动时的垂直位置
            pp.setIniX(getXLocation(index));
            pp.setCenterX(getXLocation(index));
            pp.setIniY(5);
            pp.setCenterY(getRealLineSize() + 5);
            Random randow = new Random();
            pp.setTickradius(getRealLineSize());
//            pp.setTickradius(randow.nextInt(getRealLineSize() / 2) + getRealLineSize() / 2);//随机钟摆半径(介于最大钟摆半径～与一半钟摆半径之间)
            //随机定向摆动
            pp.setRight(randow.nextInt(2) == 1);//产生0,1来进行随机左右摇摆
            Log.e("setRight", pp.isRight() + "");
            //每个索引项对应生成一个初期的随机角度(尚未转换为弧度)(介于正负degress区间)
            pp.setRadius(circlesize / 2);
            pp.setRadians(0);//(double) (randow.nextInt(degress * 2) - degress);
            pp.setConwidth(width);
            pp.setConheight(height);
            listc.put(index, pp);
        } else//获取缓存信息
            pp = listc.get(index);

        if (pp.isRight())//向右
            pp.setRadians(pp.getRadians() + speed);
        else
            pp.setRadians(pp.getRadians() - speed);
        if (Math.abs(pp.getRadians()) >= degress)//没有摆动到最大角度
            pp.setRight(!pp.isRight());
        //根据角度计算xy当前坐标
        pp.setCenterX(pp.getIniX() + Math.round((float) (Math.sin(Math.toRadians(pp.getRadians())) * pp.getTickradius())));
        pp.setCenterY(pp.getIniY() + Math.round((float) (Math.cos(Math.toRadians(pp.getRadians())) * pp.getTickradius())));
        return pp;
    }

    /**
     * 根据图片资到子菜单视图
     *
     * @param res
     * @return
     */
    private Bitmap getBitmap(int res) {
//        return BitmapFactory.decodeResource(getResources(), res);
        return zoomImage(BitmapFactory.decodeResource(getResources(), res), circlesize, circlesize);
    }


    /***
     * 图片的缩放方法
     *
     * @param bgimage   ：源图片资源
     * @param newWidth  ：缩放后宽度
     * @param newHeight ：缩放后高度
     * @return
     */
    private static Bitmap zoomImage(Bitmap bgimage, double newWidth, double newHeight) {
        // 获取这个图片的宽和高
        float width = bgimage.getWidth();
        float height = bgimage.getHeight();
        // 创建操作图片用的matrix对象
        Matrix matrix = new Matrix();
        // 计算宽高缩放率
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 缩放图片动作
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0, (int) width,
                (int) height, matrix, true);
        return bitmap;
    }

    /**
     * 获取子菜单的个数
     *
     * @return
     */
    public int getChildCount() {
        if (arrres == null)
            return 0;
        return arrres.size();
    }

    /**
     * 根据控件位置获取控件对应的初始X坐标(左边从0开始计算)
     *
     * @param index from 0 to ...
     * @return
     */
    private int getXLocation(int index) {
        //每个子图形平均宽度的X中心位置+对应的控件位置索引相对应的位置偏移距离
        return getRealWidth() / getChildCount() / 2 + getRealWidth() / getChildCount() * index;
    }

    /**
     * 得到线条对应的长度
     * (同时也是钟摆的最大半径)
     *
     * @return 线条长度+图形的半径=真实长度
     */
    public int getRealLineSize() {
        return stroke + circlesize / 2;
    }

    /**
     * 得到最后测量所得的控件宽度
     *
     * @return
     */
    public int getRealWidth() {
        return width;
    }

    /**
     * 得到最后测量所得的控件高度
     *
     * @return
     */
    public int getRealHeight() {
        return height;
    }

    /**
     * 设置文字与模块图片(具体以图片数量为准)
     *
     * @param imgRes
     * @param linecos 颜色值 ARGB
     */
    public void setTextsAndImages(int[] imgRes, int[] linecos) {
        arrres = new SparseArray<Integer>();
        linecolors = new SparseArray<Integer>();
        for (int i = 0; i < imgRes.length; i++) {
            arrres.put(i, imgRes[i]);
            if (linecos == null) continue;
            if (i < linecos.length)
                linecolors.put(i, linecos[i]);
            else
                linecolors.put(i, Color.YELLOW);
        }
    }

    private int getScreenWidth() {
        return ((Activity) getContext()).getWindow().getDecorView().getMeasuredWidth();
    }

    public void setonMenuItemListener(onMenuItemListener monMenuItemListener) {
        this.monMenuItemListener = monMenuItemListener;
    }

    public interface onMenuItemListener {
        void onMenuClick(int index);
    }
}
