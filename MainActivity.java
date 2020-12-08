package com.example.assignment3_tetris;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.view.View.OnClickListener;

import com.example.assignment3_tetris.views.CustomView;
import com.example.assignment3_tetris.views.GridView;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {
    Canvas canvas;
    Tetris tetris = new Tetris();
    boolean game = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton left_arrow = (ImageButton)findViewById(R.id.left_arrow);
        left_arrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tetris.movePieceLeftRight(-1);
            }
        });

        ImageButton right_arrow = (ImageButton)findViewById(R.id.right_arrow);
        right_arrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tetris.movePieceLeftRight(1);
            }
        });

        ImageButton down_arrow = (ImageButton)findViewById(R.id.down_arrow);
        down_arrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tetris.moveDown();
            }
        });

        ImageButton rotate_right= (ImageButton)findViewById(R.id.rotate_right);
        rotate_right.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tetris.rotatePiece(-1);
            }
        });

        ImageButton rotate_left= (ImageButton)findViewById(R.id.rotate_left);
        rotate_left.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tetris.rotatePiece(1);
            }
        });

        ImageButton drop= (ImageButton)findViewById(R.id.drop);
        drop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tetris.dropImmediately();
            }
        });

        ImageButton hold= (ImageButton)findViewById(R.id.hold);
        hold.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                tetris.holdPiece();
            }
        });
    }


    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event){


        if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
            tetris.movePieceLeftRight(-1);
            return false;
        }
        else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
            tetris.movePieceLeftRight(1);
            return false;
        }
        else if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
            tetris.moveDown();
            return false;
        }
        else if (keyCode == KeyEvent.KEYCODE_Q){
            tetris.rotatePiece(1);
            return false;
        }
        else if(keyCode == KeyEvent.KEYCODE_E){
            tetris.rotatePiece(-1);
            return false;
        }
        else if(keyCode == KeyEvent.KEYCODE_F){
            tetris.holdPiece();
            return false;
        }
        else if(keyCode == KeyEvent.KEYCODE_S){
            tetris.dropImmediately();
            return false;
        }

        return super.onKeyDown(keyCode,event);
    }


}


