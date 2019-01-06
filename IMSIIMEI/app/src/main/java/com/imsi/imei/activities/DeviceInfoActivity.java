package com.imsi.imei.activities;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.imsi.imei.R;

public class DeviceInfoActivity extends Activity {

    private TextView tvRootStatus;
    private AdView adView;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_info);

        tvRootStatus = findViewById(R.id.tv_root);
//        mAdView = (AdView) findViewById(R.id.adView);

        showBannerAd();

        showInterstitialAd();

        checkRoot();
    }

    private void showBannerAd() {
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        adView.loadAd(adRequest);
    }

    private void showInterstitialAd() {
        // Create an ad request. Check your logcat output for the hashed device
        // ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(
//                AdRequest.DEVICE_ID_EMULATOR).build();

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_id));

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdLoaded() {
                showInterstitial();
            }
        });

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private void checkRoot() {

        String data = "Root Status: ";

        if (RootUtil.isDeviceRooted()) {
            data = data + "Device Rooted";
        } else {
            data = data + "Device not Rooted";
        }
        String newLine = "\n";
        data = data + newLine + "Android Version: " + currentVersion() + newLine + "Manufacturer: " + Build.MANUFACTURER + newLine
                + "Model: " + Build.MODEL + newLine + "Brand: " + Build.BRAND
                + newLine + "Board: " + Build.BOARD;

        tvRootStatus.setText(data);
    }

    //Current Android version data
    public String currentVersion() {
        String no = Build.VERSION.RELEASE;
        double release;
        if (no.length() > 2) {
            release = Double.parseDouble(no.substring(0, 2));
        } else {
            release = Double.parseDouble(no);
        }
        String codeName = "Unsupported";//below Jelly bean OR above nougat
        if (release >= 4.1 && release < 4.4) codeName = "Jelly Bean";
        else if (release < 5) codeName = "Kit Kat";
        else if (release < 6) codeName = "Lollipop";
        else if (release < 7) codeName = "Marshmallow";
        else if (release < 8) codeName = "Nougat";
        return codeName + " v" + no + "\nAPI Level: " + Build.VERSION.SDK_INT;
    }
}
