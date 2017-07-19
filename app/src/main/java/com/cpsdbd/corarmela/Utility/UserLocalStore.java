package com.cpsdbd.corarmela.Utility;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Genius 03 on 6/6/2017.
 */

public class UserLocalStore {
    private static final String SP_NAME ="userDetail";
    private static final String CURRENT_STORE_ID="CURRENT_STORE_ID";
    private static final String LIMIT="LIMIT";

    SharedPreferences userLocalDatabase;

    private static UserLocalStore userLocalStore = null;

    private UserLocalStore(Context context){
        userLocalDatabase = context.getSharedPreferences(SP_NAME,0);
    }

    public static UserLocalStore getInstance(Context context){
        if(userLocalStore==null){
            userLocalStore= new UserLocalStore(context);
        }

        return userLocalStore;
    }


    // StoreUserData Method



    public  void setSubscribe(boolean loggedin){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("Subscribe",loggedin);
        spEditor.commit();

    }

    public boolean getSubscribe(){
        return userLocalDatabase.getBoolean("Subscribe",false);
    }

    public  void setMute(boolean mute){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putBoolean("Mute",mute);
        spEditor.commit();

    }


    public boolean getMute(){
        return userLocalDatabase.getBoolean("Mute",false);
    }



    public void clearUserData(){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.clear();
        spEditor.apply();
    }



    public void setProjectName(String userName){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString("projectname",userName);
        spEditor.apply();
    }

    public String getProjectName(){
        return userLocalDatabase.getString("projectname","");
    }

    public void setCurrentStoreId(String storeId){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putString(CURRENT_STORE_ID,storeId);
        spEditor.apply();
    }

    public String getCurrentStoreId(){
        return userLocalDatabase.getString(CURRENT_STORE_ID,"");
    }



    public void setLimit(int limit){
        SharedPreferences.Editor spEditor = userLocalDatabase.edit();
        spEditor.putInt(LIMIT,limit);
        spEditor.apply();
    }


    public int getLimit(){
        return userLocalDatabase.getInt(LIMIT,0) ;
    }
}
