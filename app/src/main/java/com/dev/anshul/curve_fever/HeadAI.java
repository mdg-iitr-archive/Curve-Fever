package com.dev.anshul.curve_fever;

import android.graphics.Color;

/**
 * Created by anshul on 1/6/15.
 */
public class HeadAI extends Head {

    //variable declaration for function call optimization
    private double d1,d2,d3,distance,maxdist=0;
    private int x2,y2,w,h,dx1,dy1,dx2,dy2,longest,shortest,numerator,intMax = Integer.MAX_VALUE;

    //for takeDecision2()
    private double devCount;
    private double viewAngle;
    private boolean increaseDeviation = true;

    //for takeDecision3()
    private double angleScan[] = new double[360];
    private double currentRayDistance = 0,maxScanDist = 0;
    private int scanAngle = 0,maxScanAngle = 0;
    private int steps = 10;

    public HeadAI(int headX, int headY) {
        super(headX, headY);
        headPaint.setColor(Color.MAGENTA);
        pathPaint.setColor(Color.RED);
        viewAngle = this.angle;
        devCount = 0;
//        this.headVelocity = 25;
    }
    public void takeDecision(){

        d1 = rayDistance((int)headX,(int)headY,(int)angle - angularChange);
        d2 = rayDistance((int)headX,(int)headY,(int)angle);
        d3 = rayDistance((int)headX,(int)headY,(int)angle + angularChange);

        if(d1>d2){
            if(d1>d3){
                angle-= angularChange;
            }
            else {
                angle+= angularChange;
            }
        }
        else {
            if(d2<d3){
                angle+= angularChange;
            }
        }

    }

    public void takeDecision3(){
        currentRayDistance = rayDistance((int)headX,(int)headY,(int)angle);
        maxScanDist = currentRayDistance;
        maxScanAngle = (int)angle;
        if(scanAngle>=360||scanAngle+steps>=369){
            scanAngle = 0;
        }

        for(int i=0;i<steps;i++){
            angleScan[scanAngle+i] = rayDistance((int)headX,(int)headY,scanAngle+i);
            if(angleScan[scanAngle+i] > maxScanDist){
                maxScanDist = angleScan[scanAngle+i];
                maxScanAngle = scanAngle + i;
            }
        }
        scanAngle += steps;

        if(Math.abs(angle-maxScanAngle)<=100){
//            if(currentRayDistance < angleScan[maxScanAngle-1] && currentRayDistance < angleScan[maxScanAngle+1])
                angle = maxScanAngle;
        }




    }

    public void takeDecision2(){
        if(increaseDeviation){
            devCount+=1;
            if(devCount == 90){
                increaseDeviation = false;
                maxdist = 0;
            }
        }
        else{
            devCount-=1;
            if(devCount ==-90){
                increaseDeviation = true;
                maxdist = 0;
            }
        }

        viewAngle = angle + devCount;

        d1 = rayDistance((int)headX,(int)headY,(int)viewAngle - angularChange);
        d2 = rayDistance((int)headX,(int)headY,(int)viewAngle);
        d3 = rayDistance((int)headX,(int)headY,(int)viewAngle - angularChange);

        if(d1>d2){
            if(d1>d3){
                if(d3 > maxdist)
                    angle=viewAngle - angularChange;
            }
            else {
                if(d1 > maxdist)
                    angle=viewAngle + angularChange;
            }
        }
        else {
            if(d2<d3){
                if(d3 > maxdist)
                    angle=viewAngle + angularChange;
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
