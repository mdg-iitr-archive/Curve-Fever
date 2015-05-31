package com.dev.anshul.curve_fever;

/**
 * Created by anshul on 12/5/15.
 */
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.Random;



public class Head {

    protected double headX;
    protected double headY;
    protected int headVelocity = 5;

    protected Paint headPaint = new Paint();
    protected Paint pathPaint = new Paint();

    protected int headRadius = 5;

    //protected int curveRadius = 50;
    protected double angle  = 0;
    private int angularChange = 3;

    public boolean[][] points;
    private int tempx,tempy;

    public Head(int headX,int headY)
    {
        Log.d("random",headX+","+headY);
        points = new boolean[GameActivity.mScreenSize.y+1][GameActivity.mScreenSize.x+1];

        this.headX = headX;
        this.headY = headY;

        angle = Math.toDegrees(Math.atan((GameActivity.mScreenSize.y/2-headY)/(GameActivity.mScreenSize.x/2-headX)));

        points[headY][headX] = true;

        headPaint.setColor(Color.parseColor("#013ADF"));
        pathPaint.setStrokeWidth(2);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setColor(Color.GREEN);
    }

//    public void followFinger(int fingerX, int fingerY)
//    {
//        headX -= ((double)headX - (double)fingerX) * (double)headVelocity / 100.0;
//        headY -= ((double)headY - (double)fingerY) * (double)headVelocity / 100.0;
//    }

    public void followFinger2(double fingerX,double fingerY){
        if (fingerX >= GameActivity.mScreenSize.x / 2) {
            angle += angularChange;
        } else {
            angle -= angularChange;
        }
    }
    public void followFinger2(boolean left){
        if(left){
            angle -=angularChange;
        }
        else {
            angle +=angularChange;
        }
    }

    //returns false if collided
    public boolean moveForward(){
        tempx=(int)headX;
        tempy=(int)headY;

        headX+=headVelocity*Math.cos(angle * Math.PI / 180);
        headY+=headVelocity*Math.sin(angle * Math.PI / 180);
        if(headX<GameActivity.mScreenSize.x&&headY<GameActivity.mScreenSize.y) {
            return line(tempx, tempy, (int) headX, (int) headY);
        }
        else {
            return false;
        }

    }

    //returns false if collided
    public boolean line(int x,int y,int x2, int y2) {
        if(x2<0||y2<0||x2>GameActivity.mScreenSize.x||y2>GameActivity.mScreenSize.y)
            return false;
        int w = x2 - x ;
        int h = y2 - y ;
        int dx1 = 0, dy1 = 0, dx2 = 0, dy2 = 0 ;
        if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
        if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
        if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;
        int longest = Math.abs(w) ;
        int shortest = Math.abs(h) ;
        if (!(longest>shortest)) {
            longest = Math.abs(h) ;
            shortest = Math.abs(w) ;
            if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
            dx2 = 0 ;
        }
        int numerator = longest >> 1 ;
        for (int i=0;i<=longest;i++) {

            if(!points[y][x])
                points[y][x] = true;
            else if(points[y][x]&&(x!=tempx&&y!=tempy))
                return false;

            numerator += shortest ;
            if (!(numerator<longest)) {
                numerator -= longest ;
                x += dx1 ;
                y += dy1 ;
            } else {
                x += dx2 ;
                y += dy2 ;
            }
        }
        return true;
    }
}