package com.dev.anshul.curve_fever;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by anshul on 8/7/15.
 */

public class BTGameView extends SurfaceView {

    private SurfaceHolder mHolder;
    private BTGameThread mThread = null;

    private int mScreenWidth;
    private int mScreenHeight;

    private boolean is_game_started;
    private boolean is_game_paused;
    private Head mHead;
    private Head mHeadOpponent;
    private Path mtrailPath=new Path();
    private Path mtrailPathOpponent =new Path();
    private Context mContext;
    private BluetoothChatService mChatService;
    private static boolean touchHeldLeft = false,touchHeldRight = false;
    private boolean opponentInitialised = false;

    public BTGameView(Context context,BluetoothChatService chatService){
        super(context);
        this.mContext = getContext();
        mChatService  = chatService;

        this.mScreenWidth = BTGameActivity.mScreenSize.x;
        this.mScreenHeight= BTGameActivity.mScreenSize.y;


        Log.i("screen", this.mScreenWidth + " x " + this.mScreenHeight);

        this.mThread = new BTGameThread(this);
        this.mHolder = this.getHolder();
        this.is_game_paused = false;
        this.mChatService = chatService;

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

        touchHeldLeft=touchHeldRight=false;
        Random random = new Random();
        int initX = random.nextInt(BTGameActivity.mScreenSize.x);
        int initY = random.nextInt(BTGameActivity.mScreenSize.y);
        mHead  = new Head(initX,initY,true);
        mHead.setVelocity(2);

        //Creating a line at the boundaries
        for(int i=0;i<=BTGameActivity.mScreenSize.x;i++){
            Head.points[0][i] = true;
            Head.points[BTGameActivity.mScreenSize.y][i] = true;

            Head.points[1][i] = true;
            Head.points[BTGameActivity.mScreenSize.y-1][i] = true;
        }
        for(int i=0;i<=BTGameActivity.mScreenSize.y;i++){
            Head.points[i][0] = true;
            Head.points[i][BTGameActivity.mScreenSize.x] = true;

            Head.points[i][1] = true;
            Head.points[i][BTGameActivity.mScreenSize.x-1] = true;
        }

        sendMessage(initX,initY);
        mtrailPath.moveTo(initX, initY);
    }

    public void opponentMovement(int x,int y){
        if(!opponentInitialised){
            mHeadOpponent = new Head(x,y,true);
            mHeadOpponent.pathPaint.setColor(Color.RED);
            mHeadOpponent.setVelocity(2);
            opponentInitialised = true;
            Log.d("BT","Opponent Initialised");
            //Creating a line at the boundaries
            for(int i=0;i<=BTGameActivity.mScreenSize.x;i++){
                Head.points[0][i] = true;
                Head.points[BTGameActivity.mScreenSize.y][i] = true;

                Head.points[1][i] = true;
                Head.points[BTGameActivity.mScreenSize.y-1][i] = true;
            }
            for(int i=0;i<=BTGameActivity.mScreenSize.y;i++){
                Head.points[i][0] = true;
                Head.points[i][BTGameActivity.mScreenSize.x] = true;

                Head.points[i][1] = true;
                Head.points[i][BTGameActivity.mScreenSize.x-1] = true;
            }
        }
        else {
            if (!mHeadOpponent.moveTo(x, y, 2)) {
                tryGameOver("You");
                Log.d("BT", "Opp. moved to "+x+","+y);
            }
        }
        mtrailPathOpponent.moveTo(x, y);
    }

    public void update()
    {
        if(opponentInitialised) {
            if (touchHeldRight) {
                mHead.followFinger(false);
            } else if (touchHeldLeft) {
                mHead.followFinger(true);
            }

            //User moves forward
            if (!mHead.moveForward(2)) {
                tryGameOver("Opponent");
            }
            sendMessage((int) mHead.headX, (int) mHead.headY);
        }
    }

    public void sendMessage(int x,int y){
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            tryGameOver("No One\nConnectionLost");
            return;
        }

        // Check that there's actually something to send
        String message = x+","+y;
        byte[] send = message.getBytes();
        mChatService.write(send);
    }

    public void draw(Canvas canvas)
    {
        canvas.drawColor(Color.WHITE);

        if(opponentInitialised) {
            mtrailPathOpponent.lineTo((float) mHeadOpponent.headX, (float) mHeadOpponent.headY);
            canvas.drawPath(mtrailPathOpponent, mHeadOpponent.pathPaint);
            canvas.drawCircle((float) mHeadOpponent.headX, (float) mHeadOpponent.headY, (float) mHeadOpponent.headRadius, mHeadOpponent.headPaint);

            mtrailPath.lineTo((float) mHead.headX, (float) mHead.headY);
            canvas.drawPath(mtrailPath, mHead.pathPaint);
            canvas.drawCircle((float) mHead.headX, (float) mHead.headY, (float) mHead.headRadius, mHead.headPaint);
        }
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
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("winner", winner);
        mContext.startActivity(intent);
//        ((Activity)mContext).finish();
    }

}