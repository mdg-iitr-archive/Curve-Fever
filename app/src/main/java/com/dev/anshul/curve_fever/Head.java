package com.dev.anshul.curve_fever;

/**
 * Created by anshul on 12/5/15.
 */
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;



public class Head {

    protected int headX;
    protected int headY;
    protected int headVelocity;

    protected Paint headPaint = new Paint();
    protected final int headRadius = 20;

    public Head()
    {
        Random random = new Random();

//        this.headY = random.nextInt(200);
//        this.headX = random.nextInt(200);

        this.headX=200;
        this.headY=200;
        this.headVelocity = 10;

        if(this.headX == 1)
            this.headX = GameActivity.mScreenSize.x;

        headPaint.setColor(Color.parseColor("#013ADF"));
    }

    public void followFinger(int fingerX, int fingerY)
    {
        headX -= ((double)headX - (double)fingerX) * (double)headVelocity / 100.0;
        headY -= ((double)headY - (double)fingerY) * (double)headVelocity / 100.0;
    }
}