package com.dev.anshul.curve_fever;

/**
 * Created by anshul on 12/5/15.
 */
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;



public class Head {

    protected double headX;
    protected double headY;
    protected int headVelocity;

    protected Paint headPaint = new Paint();
    protected Paint pathPaint = new Paint();

    protected int headRadius = 5;

    protected int curveRadius = 30;
    protected double angle  = 90;

    public Head(int headX,int headY)
    {

        this.headX = headX;
        this.headY = headY;

        this.headVelocity = 10;

        headPaint.setColor(Color.parseColor("#013ADF"));
        pathPaint.setStrokeWidth(2);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setColor(Color.GREEN);
    }

    public void followFinger(int fingerX, int fingerY)
    {
        headX -= ((double)headX - (double)fingerX) * (double)headVelocity / 100.0;
        headY -= ((double)headY - (double)fingerY) * (double)headVelocity / 100.0;
    }

    public void followFinger2(double fingerX,double fingerY){
        if (fingerX >= GameActivity.mScreenSize.x / 2) {
            angle += 5;
        } else {
            angle -= 5;
        }
    }
    public void followFinger2(boolean left){
        if(left){
            angle -=1;
        }
        else {
            angle +=1;
        }
    }

    public void moveForward(){
        headX+=(curveRadius/10.0)*Math.cos(angle * Math.PI / 180);
        headY+=(curveRadius/10.0)*Math.sin(angle * Math.PI / 180);
    }
}