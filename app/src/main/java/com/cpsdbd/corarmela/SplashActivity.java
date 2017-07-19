package com.cpsdbd.corarmela;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new MyThread().start();
    }

    private class MyThread extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(2000);
                Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
