package com.dev.anshul.curve_fever;

/**
 * Created by anshul on 12/5/15.
 */

import android.graphics.Canvas;

public class GameThread extends Thread {
    static long FPS = 30;
    private boolean running=false;

    public Canvas mGameCanvas;
    private GameView mGameView;

    public GameThread(GameView mGameView)
    {
        this.mGameView = mGameView;
    }

    public void setRunning(boolean running)
    {
        this.running = running;
    }

    public void setFPS(int fps) {
        FPS = fps;
    }


    @Override
    public void run()
    {
        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;

        while(running)
        {
            mGameCanvas = null;
            startTime = System.currentTimeMillis();

            try
            {
                mGameCanvas = mGameView.getHolder().lockCanvas();
                synchronized (mGameView.getHolder())
                {
                    mGameView.update();
                    if(mGameCanvas != null)
                        mGameView.draw(mGameCanvas);
                }
            }
            finally
            {
                if (mGameCanvas != null)
                {
                    mGameView.getHolder().unlockCanvasAndPost(mGameCanvas);
                }
            }
            sleepTime = ticksPS - (System.currentTimeMillis() - startTime);
            try
            {
                if(sleepTime > 0)
                    sleep(sleepTime);
                else
                    sleep(10);
            } catch (Exception e) {}
        }
    }
}