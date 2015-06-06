package com.dev.anshul.curve_fever;

/**
 * Created by anshul on 12/5/15.
 */
import android.graphics.Color;
import android.graphics.Paint;
import android.renderscript.Long4;
import android.util.Log;

public class Head {

    protected double headX;
    protected double headY;
    protected int headVelocity = 5;

    protected Paint headPaint = new Paint();
    protected Paint pathPaint = new Paint();

    protected int headRadius = 5;

    protected double angle  = 0;
    protected int angularChange = 3;

    public static boolean[][] points;
    protected int tempx,tempy;


    //variables for optimized function calls
    private int sx,sy,i,j;
    private double absAngle;

    public Head(int headX,int headY)
    {
        Log.d("random",headX+","+headY);
        points = new boolean[GameActivity.mScreenSize.y+1][GameActivity.mScreenSize.x+1];

        this.headX = headX;
        this.headY = headY;

        angle = Math.toDegrees(Math.atan2((GameActivity.mScreenSize.y / 2 - headY), (GameActivity.mScreenSize.x / 2 - headX)));

        points[headY][headX] = true;

        headPaint.setColor(Color.parseColor("#013ADF"));
        pathPaint.setStrokeWidth(3);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setColor(Color.GREEN);
    }

    public void followFinger(double fingerX, double fingerY){
        if (fingerX >= GameActivity.mScreenSize.x / 2) {
            angle += angularChange;
        } else {
            angle -= angularChange;
        }
    }
    public void followFinger(boolean left){
        if(left){
            angle -=angularChange;
        }
        else {
            angle +=angularChange;
        }
    }

    //returns false if collided
    public boolean moveForward(int width){
        tempx=(int)headX;
        tempy=(int)headY;

        width/=2;
        absAngle=Math.abs(angle);

        headX+=this.headVelocity*Math.cos(angle * Math.PI / 180);
        headY+=this.headVelocity*Math.sin(angle * Math.PI / 180);

        if(line(tempx, tempy, (int) headX, (int) headY)){
            if(absAngle>45.0&&absAngle<=135.0){
                for(i=-width;i<=width;i++){
                    if(i==0) continue;
                    line(tempx+i,tempy,(int)headX+i,(int)headY);
                }
            }
            else{
                for(i=-width;i<=width;i++){
                    if(i==0) continue;
                    line(tempx,tempy+i,(int)headX,(int)headY+i);
                }
            }
        }
        else
            return false;

        return true;
    }

    //returns false if collided
    //Bresenham line drawing algorithm
    public boolean line(int x,int y,int x2, int y2) {
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

        for (int i=1;i<=longest;i++) {

            //plotting logic
            if(!points[y][x]){
                points[y][x] = true;
            }
            else {
                return false;
            }

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

    //not used
    //returns false if collided
    //Bresenham thick line drawing algorithm
    public boolean line(int x0, int y0, int x1, int y1, float wd)
    {
        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx-dy, e2, x2, y2;                          /* error value e_xy */
        float ed = dx+dy == 0 ? 1 : (float) Math.sqrt((float) dx * dx + (float) dy * dy);

        for (wd = (wd+1)/2; ; ) {                                   /* pixel loop */

            //plotting logic
            if(!points[y0][x0]){
                points[y0][x0] = true;

            }
            else if(x0!=tempx&&y0!=tempy)
                return false;
            //plotting logic ends

            e2 = err; x2 = x0;
            if (2*e2 >= -dx) {                                           /* x step */
                for (e2 += dy, y2 = y0; e2 < ed*wd && (y1 != y2 || dx > dy); e2 += dx)

                //plotting logic
                    if(!points[y0][x0]){
                        points[y0][x0] = true;

                    }
                    else if(x0!=tempx&&y0!=tempy)
                        return false;
                //plotting logic ends

                if (x0 == x1) break;
                e2 = err; err -= dy; x0 += sx;
            }
            if (2*e2 <= dy) {                                            /* y step */
                for (e2 = dx-e2; e2 < ed*wd && (x1 != x2 || dx < dy); e2 += dy)

                //plotting logic
                    if(!points[y0][x0]){
                        points[y0][x0] = true;

                    }
                    else if(x0!=tempx&&y0!=tempy)
                        return false;
                //plotting logic ends

                if (y0 == y1) break;
                err += dx; y0 += sy;
            }
        }
        return true;
    }

}