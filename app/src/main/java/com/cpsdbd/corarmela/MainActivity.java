package com.cpsdbd.corarmela;

import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cpsdbd.corarmela.Fragments.OffLineFragment;
import com.cpsdbd.corarmela.Utility.MyYoutubeHelper;
import com.cpsdbd.corarmela.Utility.UserLocalStore;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.youtube.YouTubeScopes;
import com.cpsdbd.corarmela.Fragments.YoutubeMainFragment;
import com.cpsdbd.corarmela.Utility.DollUtil;

import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
        ,EasyPermissions.PermissionCallbacks{

    private GoogleAccountCredential mCredential;

    private DollUtil dollUtil;

    private static final String PREF_ACCOUNT_NAME = "thisApp";
    private static final String[] SCOPES = { YouTubeScopes.YOUTUBE_READONLY,YouTubeScopes.YOUTUBE, YouTubeScopes.YOUTUBE_FORCE_SSL,YouTubeScopes.YOUTUBEPARTNER};


    public static final int REQUEST_ACCOUNT_PICKER = 1000;
    public static final int REQUEST_AUTHORIZATION = 1001;
    public static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;





    //private MyFloatingAction myFloatingAction;

    private YoutubeMainFragment mainFragment;
    private Observable<Boolean> subStat;


    // View
    private FloatingActionButton fabMute;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fabMute = (FloatingActionButton) findViewById(R.id.fab);
        fabMute.setOnClickListener(this);


        dollUtil = new DollUtil(this);

        // Initialize credentials and service object.
        mCredential = GoogleAccountCredential.usingOAuth2(
                this, Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());

        getResultsFromApi();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!UserLocalStore.getInstance(this).getMute()){
            backGroundMusicOn();
            fabMute.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_unmute));
        }else{
            backGroundMusicOff();
            fabMute.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_mute));
        }


    }

    private void backGroundMusicOn() {
        Intent intent = new Intent(this,MyBackgroundService.class);
        startService(intent);
        UserLocalStore.getInstance(this).setMute(false);
    }

    private void backGroundMusicOff() {
        Intent intent = new Intent(this,MyBackgroundService.class);
        stopService(intent);
        UserLocalStore.getInstance(this).setMute(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this,MyBackgroundService.class);
        stopService(intent);
    }

    private void getResultsFromApi() {
        if (! dollUtil.isGooglePlayServicesAvailable()) {
            //acquireGooglePlayServices();
            dollUtil.acquireGooglePlayServices(REQUEST_GOOGLE_PLAY_SERVICES);
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (! dollUtil.isDeviceOnline()) {
            Toast.makeText(this, "Please Turn on your internet Connection", Toast.LENGTH_SHORT).show();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new OffLineFragment()).commit();
        } else {
            //new MakeRequestTask(mCredential).execute();
            // If Every thing is OK
            //setUpNavigationDrawer();

            if(UserLocalStore.getInstance(this).getSubscribe()){
                mainFragment = new YoutubeMainFragment();
                mainFragment.setCredential(mCredential);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,mainFragment).commit();
            }else{
                // Subscribe the Channel
               subscribe();
            }


        }
    }


    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, android.Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                getResultsFromApi();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(
                requestCode, permissions, grantResults, this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fab:
                if(!UserLocalStore.getInstance(MainActivity.this).getMute()){
                    backGroundMusicOff();
                    fabMute.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_mute));

                }else{
                    backGroundMusicOn();
                    fabMute.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_unmute));
                }
                break;
        }

    }

    private void subscribe(){
        subStat= Observable.create(new Observable.OnSubscribe<Boolean>() {
            @Override
            public void call(Subscriber<? super Boolean> subscriber) {

                try {
                    boolean data = MyYoutubeHelper.getInstance(MainActivity.this,mCredential).subscribeChannel();
                    subscriber.onNext(data); // Emit the contents of the URL
                    subscriber.onCompleted(); // Nothing more to emit
                } catch (Exception e) {
                    subscriber.onError(e); // In case there are network errors
                }

            }
        });

        subStat.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if(aBoolean){
                            UserLocalStore.getInstance(MainActivity.this).setSubscribe(true);
                            mainFragment = new YoutubeMainFragment();
                            mainFragment.setCredential(mCredential);
                            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,mainFragment).commit();
                        }
                    }
                });
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(this, "This app requires Google Play Services. Please install " +
                            "Google Play Services on your device and relaunch this app.", Toast.LENGTH_LONG).show();
                } else {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accountName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accountName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if (resultCode == RESULT_OK) {
                    getResultsFromApi();
                }
                break;
        }
    }

}
