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
import android.graphics.Region;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
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
    private HeadAI mHeadAI;
    private Region r;
    private Path mtrailPath=new Path();
    private Path mtrailPathAI=new Path();
    private Context mContext;
    private static boolean touchHeldLeft = false,touchHeldRight = false;

    public static int delayAI = 5,countAI = 0;

//    public static boolean touchHeld = false;

    public GameView(Context context){
        super(context);
        this.mContext = getContext();

        this.mScreenWidth = GameActivity.mScreenSize.x;
        this.mScreenHeight= GameActivity.mScreenSize.y;


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
        mtrailPath.moveTo(initX, initY);

        initX = random.nextInt(GameActivity.mScreenSize.x-200)+100;
        initY = random.nextInt(GameActivity.mScreenSize.y-200)+100;
        mHeadAI = new HeadAI(initX,initY);
        mtrailPathAI.moveTo(initX,initY);

        //Creating a line at the boundaries
        for(int i=0;i<=GameActivity.mScreenSize.x;i++){
            Head.points[0][i] = true;
            Head.points[GameActivity.mScreenSize.y][i] = true;

            Head.points[1][i] = true;
            Head.points[GameActivity.mScreenSize.y-1][i] = true;
        }
        for(int i=0;i<=GameActivity.mScreenSize.y;i++){
            Head.points[i][0] = true;
            Head.points[i][GameActivity.mScreenSize.x] = true;

            Head.points[i][1] = true;
            Head.points[i][GameActivity.mScreenSize.x-1] = true;
        }
    }

    public void update()
    {
        if(touchHeldRight){
            mHead.followFinger(false);
        }
        else if(touchHeldLeft){
            mHead.followFinger(true);
        }

//        if(countAI==delayAI){
//            mHeadAI.takeDecision();
//            countAI=0;
//        }else{
//            countAI++;
//        }

        //User moves forward
        if(!mHead.moveForward(2)){
            tryGameOver("Computer");
        }

        //AI moves forward
        if(!mHeadAI.moveForward(2)){
            tryGameOver("User");
        }
        if(countAI>=delayAI) {
            mHeadAI.takeDecision();
            countAI=0;
        }
        countAI++;
    }

    public void draw(Canvas canvas)
    {
        canvas.drawColor(Color.WHITE);

        mtrailPathAI.lineTo((float) mHeadAI.headX, (float) mHeadAI.headY);
        canvas.drawPath (mtrailPathAI, mHeadAI.pathPaint);
        canvas.drawCircle((float)mHeadAI.headX, (float)mHeadAI.headY, (float)mHeadAI.headRadius, mHeadAI.headPaint);

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
//                    mHead.followFinger((int)event.getX(),(int)event.getY());
                    break;

                case MotionEvent.ACTION_UP:
                    touchHeldLeft=touchHeldRight=false;
                    //Do Something here.
                    break;

                case MotionEvent.ACTION_MOVE:
                    //Do Something here.
                    mHead.followFinger((int) event.getX(), (int) event.getY());
                    break;
                case MotionEvent.ACTION_CANCEL:
                    mThread.setRunning(false);
                    break;
            }
        }
        return true;
    }

    public void tryGameOver(String winner){
        mThread.setRunning(false);
        Intent intent = new Intent(mContext, GameOverActivity.class);
        intent.putExtra("winner", winner);
        mContext.startActivity(intent);
        ((Activity)mContext).finish();
    }

}