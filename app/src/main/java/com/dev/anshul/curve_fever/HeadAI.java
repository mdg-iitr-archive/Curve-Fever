package com.dev.anshul.curve_fever;

import android.graphics.Color;

/**
 * Created by anshul on 1/6/15.
 */
public class HeadAI extends Head {

    //variable declaration for function call optimization
    private double d1,d2,d3,distance;
    private int x2,y2,w,h,dx1,dy1,dx2,dy2,longest,shortest,numerator,intMax = Integer.MAX_VALUE;

    public HeadAI(int headX, int headY) {
        super(headX, headY);
        headPaint.setColor(Color.MAGENTA);
        pathPaint.setColor(Color.RED);
//        this.headVelocity = 25;
    }

    public void takeDecision(){
        d1 = rayDistance((int)headX,(int)headY,(int)angle - angularChange);
        d2 = rayDistance((int)headX,(int)headY,(int)angle);
        d3 = rayDistance((int)headX,(int)headY,(int)angle + angularChange);
        if(d1>d2){
            if(d1>d3){
                angle-=angularChange;
            }
            else {
                angle+=angularChange;
            }
        }
        else {
            if(d2<d3){
                angle+=angularChange;
            }
        }
    }

    @Override
    public boolean moveForward(int width) {
        return super.moveForward(width);
    }

    public double rayDistance(int x,int y,int angle){
        tempx = x;
        tempy = y;
        distance = 0;
        x2 = (int) (x + 10000*Math.cos(angle * Math.PI / 180));
        y2 = (int) (y + 10000*Math.sin(angle * Math.PI / 180));

        w = x2 - x ;
        h = y2 - y ;
        dx1 = 0; dy1 = 0; dx2 = 0; dy2 = 0 ;
        if (w<0) dx1 = -1 ; else if (w>0) dx1 = 1 ;
        if (h<0) dy1 = -1 ; else if (h>0) dy1 = 1 ;
        if (w<0) dx2 = -1 ; else if (w>0) dx2 = 1 ;

        longest = Math.abs(w) ;
        shortest = Math.abs(h) ;
        if (!(longest>shortest)) {
            longest = Math.abs(h) ;
            shortest = Math.abs(w) ;
            if (h<0) dy2 = -1 ; else if (h>0) dy2 = 1 ;
            dx2 = 0 ;
        }
        numerator = longest >> 1 ;

        //removing first point
        numerator += shortest ;
        if (!(numerator<longest)) {
            numerator -= longest ;
            x += dx1 ;
            y += dy1 ;
        } else {
            x += dx2 ;
            y += dy2 ;
        }
        //end
        if(x<0||y<0||x>GameActivity.mScreenSize.x||y>GameActivity.mScreenSize.y)
            return intMax;

        for (int i=1;i<longest;i++) {

            //plotting logic
            if(points[y][x]){
                distance = Math.sqrt((tempx-x)*(tempx-x)+(tempy-y)*(tempy-y));
                break;
            }
            //plotting logic ends

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

        return distance;
    }

}
