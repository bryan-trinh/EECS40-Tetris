package com.example.assignment3_tetris.views;


import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import com.example.assignment3_tetris.R;

import java.security.Key;

public class CustomView extends View {

    public static final int SQUARE_SIZE_DEF = 100;

    private Rect mRectSquare;
    private Paint mPaintSquare;
    private int mSquareColor;
    private int mSquareSize;

    private float mX = 0;
    private float mY = 0;
    private int MoveStep = 25;



    public CustomView(Context context) {
        super(context);

        init (null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public CustomView(Context context,  AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set){

        mRectSquare = new Rect();
        mPaintSquare = new Paint(Paint.ANTI_ALIAS_FLAG);

        if(set == null)
            return;

        TypedArray ta = getContext().obtainStyledAttributes(set, R.styleable.CustomView);

        mSquareColor = ta.getColor(R.styleable.CustomView_square_color, Color.CYAN);
        mSquareSize = ta.getDimensionPixelSize(R.styleable.CustomView_square_size,SQUARE_SIZE_DEF);
        mPaintSquare.setColor(mSquareColor);

        ta.recycle();

    }

    @Override
    protected void onDraw(Canvas canvas){

        mRectSquare.left = 10;
        mRectSquare.top = 10;
        mRectSquare.right = mRectSquare.left + mSquareSize;
        mRectSquare.bottom = mRectSquare.top + mSquareSize;


        canvas.drawRect(mRectSquare,mPaintSquare);
    }
/*
    @Override
    public boolean onTouchEvent(MotionEvent event){
        boolean value = super.onTouchEvent(event);

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:{
                return true;
            }
            case MotionEvent.ACTION_MOVE:{
                return true;
            }
        }
        return value;
    }*/

    @Override
    public boolean onKeyDown(int KeyCode, KeyEvent event){
        switch (KeyCode){
            case KeyEvent.KEYCODE_DPAD_UP:
                mX -=MoveStep;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mY+=MoveStep;
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mX -= MoveStep;
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mY+=MoveStep;
                break;
        }
        invalidate();
        return super.onKeyDown(KeyCode,event);
    }

}


