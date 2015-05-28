package com.dev.anshul.curve_fever;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.util.List;

public class GameActivity extends Activity {

    private Display mGameDisplay;
    public static Point mScreenSize = new Point(0,0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // getWindow().getDecorView().setSystemUiVisibility(
        //         View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        //                 | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        //                 | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        //                 | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
        //                 | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
        //                 | View.SYSTEM_UI_FLAG_IMMERSIVE);

        mGameDisplay = getWindowManager().getDefaultDisplay();
        mGameDisplay.getSize(mScreenSize);
        // Xperia T2 has 720 x 1208 px in non-immersive mode
        //           and 720 x 1280 px in immersive mode
        // Nexus 5 emulator  has 1080 x 1920 px

        setContentView(new GameView(this));
        onDestroy();
    }

    public void onPause(){
        super.onPause();
    }

    public void onResume(){
        super.onResume();
    }

}
