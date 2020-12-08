package com.example.assignment3_tetris.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.example.assignment3_tetris.R;
import com.example.assignment3_tetris.Tetris;

import java.util.concurrent.TimeUnit;


public class GridView extends View {



    public GridView(Context context) {
        super(context);
        init(null);
    }

    public GridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public GridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {


    }

    static int doOnce = 0;
    static int seconds = 0;
    static int minutes = 0;
    static int timeout = 1;

    @Override
    public void onDraw(Canvas canvas) {

        Paint blankspace = new Paint();
        blankspace.setColor(Color.WHITE);
        blankspace.setStyle(Paint.Style.STROKE);
        blankspace.setStrokeWidth(8);

        Paint piece = new Paint();
        piece.setColor(Color.CYAN);
        final Tetris tetris = new Tetris();

        Paint border = new Paint();
        border.setColor(Color.RED);

        Paint lock = new Paint();
        lock.setColor(Color.GREEN);

        if(tetris.game) {
            if (doOnce == 0) {
                tetris.fillPieceBuffer();
                tetris.getNewPiece();
                tetris.initBoard();
                tetris.updateBoard();
                doOnce = 1;
            }
            //thread
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(timeout);
                    } catch (InterruptedException e) {

                    }
                    if(tetris.game) {
                        seconds++;
                    }
                    tetris.moveDown();
                    if (tetris.isBlockedNextRow(tetris.currentPiece, tetris.rotationNum, tetris.refPoint.x, tetris.refPoint.y)) {
                        tetris.lockPieces(tetris.currentPiece, tetris.rotationNum, tetris.refPoint.x, tetris.refPoint.y);
                        tetris.checkForFullRows();
                        if (tetris.checkGameOver()) {
                            tetris.game = false;
                            timeout = 999999999;
                        }
                    }
                    tetris.updateBoard();
                }
            });

            if (tetris.isBlockedNextRow(tetris.currentPiece, tetris.rotationNum, tetris.refPoint.x, tetris.refPoint.y)) {
                tetris.lockPieces(tetris.currentPiece, tetris.rotationNum, tetris.refPoint.x, tetris.refPoint.y);
                tetris.checkForFullRows();
                if (tetris.checkGameOver()) {
                    tetris.game = false;
                }
            }
            tetris.updateBoard();
        }
        for (int r = 0; r < 23; r++) {
            for (int c = 0; c < 12; c++) {
                int gm = 60;
                int top = r * gm;
                int bottom = top + gm;
                int left = c * gm;
                int right = left + gm;

                if (tetris.board[c][r] == 0) {
                    canvas.drawRect(left, top, right, bottom, blankspace);
                } else if (tetris.board[c][r] == 3) {
                    canvas.drawRect(left, top, right, bottom, piece);
                } else if (tetris.board[c][r] == 2) {
                    canvas.drawRect(left, top, right, bottom, border);
                } else if (tetris.board[c][r] == 1) {
                    canvas.drawRect(left, top, right, bottom, lock);
                    //System.out.println("Left = "+left);
                    //System.out.println("Right = "+right);
                    //System.out.println("Top = "+top);
                    //System.out.println("Bottom = "+bottom);
                }
            }
        }
        Paint nextPiecePaint = new Paint();
        nextPiecePaint.setColor(Color.LTGRAY);
        nextPiecePaint.setTextSize(55);
        canvas.drawText("The next piece is: " + tetris.pieceName(tetris.nextPiece), 20, 1450, nextPiecePaint);

        Paint heldPiecePaint = new Paint();
        heldPiecePaint.setColor(Color.GRAY);
        heldPiecePaint.setTextSize(55);
        canvas.drawText("The held piece is: " + tetris.pieceName(tetris.holdPiece), 20, 1515, heldPiecePaint);

        Paint instructions = new Paint();
        instructions.setColor(Color.WHITE);
        instructions.setTextSize(38);
        canvas.drawText("Q - rotate left", 740, 1045, instructions);
        canvas.drawText("E - rotate right", 740, 1090, instructions);
        canvas.drawText("F - hold/swap piece", 740, 1135, instructions);
        canvas.drawText("S - drop piece", 740, 1180, instructions);


        Paint score = new Paint();
        score.setColor(Color.WHITE);
        score.setTextSize(40);
        canvas.drawText("Score: ", 740, 100, score);
        canvas.drawText(Integer.toString(tetris.score), 740, 150, score);

        if(seconds >60){
            seconds = 0;
            minutes++;
        }
        String secondsText = "0";
        if(seconds < 10){
            secondsText += Integer.toString(seconds);
        }
        else{
            secondsText = Integer.toString(seconds);
        }
        canvas.drawText("Time: ", 740, 200, score);
        canvas.drawText(minutes + ":" + secondsText, 740, 250, score);

        if(!tetris.game){
            Paint gameOver = new Paint();
            gameOver.setColor(Color.DKGRAY);

            Paint gameOverText = new Paint();
            gameOverText.setColor(Color.CYAN);
            gameOverText.setTextSize(100);

            canvas.drawRect(100,50,950,300, gameOver); //-500
            canvas.drawText("GAME OVER",250,150,gameOverText);
            canvas.drawText("Score: "+Integer.toString(tetris.score),250,250,gameOverText);
        }
        invalidate();
    }
}








