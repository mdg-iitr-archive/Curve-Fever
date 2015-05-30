package com.dev.anshul.curve_fever;

/**
 * Created by anshul on 12/5/15.
 */
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Random;


public class GameView extends SurfaceView {

    private SurfaceHolder mHolder;
    private GameThread mThread = null;

    private int mScreenWidth;
    private int mScreenHeight;

    private boolean is_game_started;
    private boolean is_game_paused;
    private Head mHead;
    private Region r;
    Path mtrailPath=new Path();
    private Context mContext;
    private boolean[][] points;
    private static boolean touchHeldLeft = false,touchHeldRight = false;

//    public static boolean touchHeld = false;

    public GameView(Context context){
        super(context);
        this.mContext = getContext();

        this.mScreenWidth = GameActivity.mScreenSize.x;
        this.mScreenHeight= GameActivity.mScreenSize.y;

        points = new boolean[this.mScreenHeight][this.mScreenWidth];

        Log.i("screen", this.mScreenWidth+" x "+this.mScreenHeight);

        this.mThread = new GameThread(this);
        this.mHolder = this.getHolder();
        this.is_game_paused = false;

        this.mHolder.addCallback(new SurfaceHolder.Callback() {

            @Override
            public void surfaceCreated(SurfaceHolder mHolder)
            {
                mThread.setRunning(true);
                mThread.start();
            }
            @Override
            public void surfaceChanged(SurfaceHolder mHolder, int format, int width, int height) {}
            @Override
            public void surfaceDestroyed(SurfaceHolder mHolder)
            {
                mThread.setRunning(false);
                boolean retry = true;

                while(retry)
                {
                    try {
                        mThread.join();
                        retry = false;
                    } catch (InterruptedException e) {}
                }
            }
        });

        Random random = new Random();
        int initX = random.nextInt(GameActivity.mScreenSize.x-200)+100;
        int initY = random.nextInt(GameActivity.mScreenSize.y-200)+100;
        mHead  = new Head(initX,initY);
        mtrailPath.moveTo(initX,initY);

    }

    public void update()
    {
        //checkCollision();

        if(touchHeldRight){
            mHead.followFinger2(false);
        }
        else if(touchHeldLeft){
            mHead.followFinger2(true);
        }

        if(!mHead.moveForward()){
            tryGameOver();
        }
    }

    public void draw(Canvas canvas)
    {
        canvas.drawColor(Color.WHITE);
        mtrailPath.lineTo((float) mHead.headX, (float) mHead.headY);
        canvas.drawPath (mtrailPath, mHead.pathPaint);
        canvas.drawCircle((float)mHead.headX, (float)mHead.headY, (float)mHead.headRadius, mHead.headPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        is_game_started = true;

        if(!is_game_paused)
        {
            switch(event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    if(event.getX()>=this.mScreenWidth/2) {
                        touchHeldRight = true;
                        touchHeldLeft = false;
                    }
                    else if(event.getX()<this.mScreenWidth/2) {
                        touchHeldRight = false;
                        touchHeldLeft = true;
                    }

                        //Do Something here.
//                    mHead.followFinger2((int)event.getX(),(int)event.getY());
                    break;

                case MotionEvent.ACTION_UP:
                    touchHeldLeft=touchHeldRight=false;
                    //Do Something here.
                    break;

                case MotionEvent.ACTION_MOVE:
                    //Do Something here.
                    mHead.followFinger2((int)event.getX(),(int)event.getY());
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mThread.setRunning(false);
                    break;
            }
        }
        return true;
    }

    public void tryGameOver(){
//        Toast.makeText(getContext(),"Game Over",Toast.LENGTH_SHORT).show();
        mThread.setRunning(false);
        Intent intent = new Intent(mContext, MainActivity.class);
        ((Activity)mContext).finish();
        mContext.startActivity(intent);
    }


}