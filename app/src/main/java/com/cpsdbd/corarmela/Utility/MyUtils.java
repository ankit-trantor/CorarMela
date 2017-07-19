package com.cpsdbd.corarmela.Utility;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.cpsdbd.corarmela.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Genius 03 on 6/19/2017.
 */

public class MyUtils {

    public static void colorStatusBar(Activity activity) {

        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(ResourcesCompat.getColor(activity.getResources(),R.color.transparent_color,null));
        }

    }

    public static Bitmap resizeBitmap(Bitmap bitmap, int width, int height){
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    public static int getDeviceWidth(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        //int height = displayMetrics.heightPixels;
        return displayMetrics.widthPixels;

    }

    public static String getDateText(long timeStamp){

        String returnText = null;

        long currentTime = System.currentTimeMillis();
        long timeDiff = currentTime-timeStamp;

        if(timeDiff<60*1000){
            returnText =(int)(timeDiff/1000)+" seconds ago";
        }else if(timeDiff<60*60*1000){
            returnText = (int)(timeDiff/(1000*60))+" minutes ago";
        }else if(timeDiff<24*60*60*1000){
            returnText = (int)(timeDiff/(1000*60*60))+" hours ago";
        }else{
            returnText = (int)(timeDiff/(1000*60*60*24))+" days ago";
        }

        return returnText;

    }

    public static void hideKey(View view){
        InputMethodManager imm = (InputMethodManager)view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String getActualFormat(String date){
        // remove last 4 char
        String newStr = date.substring(0,date.length()-5);
        newStr =newStr.replace("T"," ");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date myDate = null;
        try {
            myDate = sdf.parse(newStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long millis = myDate.getTime()+6*60*60*1000;


        return getDateText(millis);

    }



}
