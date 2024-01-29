package com.photoeditor.photoeffect3d.application;

import android.app.Application;
import android.graphics.Bitmap;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class LogoApplication extends Application {
    public Bitmap bitmapResult = null;
    public double showingPromt = 0.0d;

    @Override
    public void onCreate() {
        super.onCreate();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

    }

    public double getShowingPromt() {
        return this.showingPromt;
    }

    public void setShowingPromt(double showingPromt) {
        this.showingPromt = showingPromt;
    }

    public Bitmap getBitmapResult() {
        return this.bitmapResult;
    }

    public void setBitmapResult(Bitmap bitmapResult) {
        this.bitmapResult = bitmapResult;
    }
}
