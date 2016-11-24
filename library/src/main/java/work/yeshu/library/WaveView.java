package work.yeshu.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by yeshu on 2016/11/21.
 * wave view
 */

public class WaveView extends View {
    private static final int DEFAULT_FRONT_WAVE_COLOR = 0xffffffff;
    private static final int DEFAULT_BEHIND_WAVE_COLOR = 0xff76b6f7;
    private static final int DEFAULT_AMPLITUDE = 8;
    private static final int DEFAULT_WATER_HEIGHT = 16;
    private static final int DEFAULT_FRONT_WAVE_SPEED = 4;
    private static final int DEFAULT_BEHIND_WAVE_SPEED = 8;
    private static final int VIEW_UPDATE_INTERVAL = 10; //the interval time of update view

    private Paint mFrontWavePaint;
    private Paint mBehindWavePaint;
    private Path mFrontWavePath;
    private Path mBehindWavePath;

    private int mFrontWaveColor = DEFAULT_FRONT_WAVE_COLOR;
    private int mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR;

    private int mWavelength; //default value is the view width
    private int mAmplitude;
    private int mWaterHeight; //正弦曲线到底部到距离

    private int mFrontWaveSpeed;
    private int mBehindWaveSpeed;

    private int mFrontWaveOffset;
    private int mBehindWaveOffset;

    private Float[] mBehindWavePoints;
    private Float[] mFrontWavePoints;
    private Float[] mPoints;


    public WaveView(Context context) {
        super(context);

        mFrontWaveColor = DEFAULT_FRONT_WAVE_COLOR;
        mBehindWaveColor = DEFAULT_BEHIND_WAVE_COLOR;
        //default value will be the view width
        mWavelength = 0;
        mAmplitude = UIUtils.dp2px(getContext(), DEFAULT_AMPLITUDE);
        mWaterHeight = UIUtils.dp2px(getContext(), DEFAULT_WATER_HEIGHT);
        mFrontWaveSpeed = DEFAULT_FRONT_WAVE_SPEED;
        mBehindWaveSpeed = DEFAULT_BEHIND_WAVE_SPEED;

        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.WaveView, defStyleAttr, 0);

        try {
            mFrontWaveColor = a.getColor(R.styleable.WaveView_frontWaveColor, DEFAULT_FRONT_WAVE_COLOR);
            mBehindWaveColor = a.getColor(R.styleable.WaveView_behindWaveColor, DEFAULT_BEHIND_WAVE_COLOR);
            mWavelength = a.getDimensionPixelOffset(R.styleable.WaveView_wavelength, 0);
            mAmplitude = a.getDimensionPixelOffset(R.styleable.WaveView_amplitude, DEFAULT_AMPLITUDE);
            mWaterHeight = a.getDimensionPixelOffset(R.styleable.WaveView_waterHeight, DEFAULT_WATER_HEIGHT);
            mFrontWaveSpeed = a.getInteger(R.styleable.WaveView_frontWaveSpeed, DEFAULT_FRONT_WAVE_SPEED);
            mBehindWaveSpeed = a.getInt(R.styleable.WaveView_behindWaveSpeed, DEFAULT_BEHIND_WAVE_SPEED);
        } finally {
            a.recycle();
        }

        init();
    }

    private void init() {
        mFrontWavePaint = new Paint();
        mBehindWavePaint = new Paint();

        mFrontWavePaint.setStyle(Paint.Style.FILL);
        mFrontWavePaint.setColor(mFrontWaveColor);
        mFrontWavePaint.setAntiAlias(true);

        mBehindWavePaint.setStyle(Paint.Style.FILL);
        mBehindWavePaint.setAntiAlias(true);
        mBehindWavePaint.setColor(mBehindWaveColor);

        mFrontWavePath = new Path();
        mBehindWavePath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawWave(canvas);
        moveWavePoints();

        postInvalidateDelayed(VIEW_UPDATE_INTERVAL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = 2 * mAmplitude + mWaterHeight;
        setMeasuredDimension(widthMeasureSpec, resolveSize(height, heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mWavelength <= 0) {
            mWavelength = getMeasuredWidth();
        }
        computeWavePoints();
    }

    public void setFrontWaveColor(int frontWaveColor) {
        mFrontWaveColor = frontWaveColor;
        mFrontWavePaint.setColor(mFrontWaveColor);
    }

    public void setBehindWaveColor(int behindWaveColor) {
        mBehindWaveColor = behindWaveColor;
        mBehindWavePaint.setColor(mBehindWaveColor);
    }

    public void setWavelength(int wavelength) {
        mWavelength = wavelength;
        computeWavePoints();
        invalidate();
    }

    public void setAmplitude(int amplitude) {
        mAmplitude = amplitude;
        requestLayout();
    }

    public void setWaterHeight(int waterHeight) {
        mWaterHeight = waterHeight;
        requestLayout();
    }

    public void setFrontWaveSpeed(int frontWaveSpeed) {
        mFrontWaveSpeed = frontWaveSpeed;
    }

    public void setBehindWaveSpeed(int behindWaveSpeed) {
        mBehindWaveSpeed = behindWaveSpeed;
    }

    private void computeWavePoints() {
        //点的个数必须是一个完整的波形的倍数，否则移动的时候会出现不连续的效果
        //计算需要生成多少个点 n * mWavelength >= getMeasuredWidth()
        int pointNum;
        if (mWavelength >= getMeasuredWidth()) {
            pointNum = mWavelength;
        } else {
            if (0 == getMeasuredWidth() / mWavelength) {
                pointNum = getMeasuredWidth() / mWavelength * mWavelength;
            } else {
                pointNum = (getMeasuredWidth() / mWavelength + 1) * mWavelength;
            }
        }
        mPoints = new Float[pointNum];
        mFrontWavePoints = new Float[pointNum];
        mBehindWavePoints = new Float[pointNum];

        //波长为2PI,对应长度为mWaveLength
        float cycle = (float) (2 * Math.PI / mWavelength);
        for (int i = 0; i < pointNum; i++) {
            mPoints[i] = (float) (mAmplitude * Math.sin(cycle * i));
        }

        System.arraycopy(mPoints, 0, mBehindWavePoints, 0, mPoints.length);
        System.arraycopy(mPoints, 0, mFrontWavePoints, 0, mPoints.length);
    }

    private void drawWave(Canvas canvas) {
        int height = getMeasuredHeight();

        mFrontWavePath.reset();
        mBehindWavePath.reset();
        for (int i = 0; i < mPoints.length; i++) {
            if (i == 0) {
                mFrontWavePath.moveTo(0, getMeasuredHeight());
                mBehindWavePath.moveTo(0, getMeasuredHeight());
            }

            mFrontWavePath.lineTo(i, height - mFrontWavePoints[i] - mWaterHeight - mAmplitude);
            mBehindWavePath.lineTo(i, height - mBehindWavePoints[i] - mWaterHeight - mAmplitude);

            if (i >= getMeasuredWidth() - 1) {
                //超出视图边界就不画了
                mFrontWavePath.lineTo(getMeasuredWidth(), getMeasuredHeight());
                mFrontWavePath.lineTo(0, getMeasuredHeight());

                mBehindWavePath.lineTo(getMeasuredWidth(), getMeasuredHeight());
                mBehindWavePath.lineTo(0, getMeasuredHeight());
                break;
            }
        }

        canvas.drawPath(mBehindWavePath, mBehindWavePaint);
        canvas.drawPath(mFrontWavePath, mFrontWavePaint);
    }

    //向右移动
    private void moveWavePoints() {
        // 改变两条波纹的移动点
        mFrontWaveOffset += mFrontWaveSpeed;
        mBehindWaveOffset += mBehindWaveSpeed;

        // 如果已经移动到结尾处，则重头记录
        if (mFrontWaveOffset >= mPoints.length) {
            mFrontWaveOffset = 0;
        }
        if (mBehindWaveOffset > mPoints.length) {
            mBehindWaveOffset = 0;
        }


        int behindWaveInterval = mPoints.length - mBehindWaveOffset;
        System.arraycopy(mPoints, behindWaveInterval, mBehindWavePoints, 0, mBehindWaveOffset);
        System.arraycopy(mPoints, 0, mBehindWavePoints, mBehindWaveOffset, behindWaveInterval);


        int frontWaveInterval = mPoints.length - mFrontWaveOffset;
        System.arraycopy(mPoints, frontWaveInterval, mFrontWavePoints, 0, mFrontWaveOffset);
        System.arraycopy(mPoints, 0, mFrontWavePoints, mFrontWaveOffset, frontWaveInterval);
    }
}
