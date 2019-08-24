package com.imsi.imei.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.imsi.imei.R;


public class MainActivity extends Activity implements OnClickListener {

    private static final int REQUEST_READ_PHONE_STATE = 1123;
    private static final int REQUEST_READ_PHONE_STATE_CLICK = 1124;
    private Button get;
    private Button btnDeviceInfo;
    private TextView imsiText, imeiText;
    private TextView tvUdid;
    private ImageView ivIMSI, ivIMEI, ivUDID;
    private RelativeLayout rlMain;
    private AdView adView;
    private InterstitialAd mInterstitialAd;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, getString(R.string.app_ad_id));

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }

        get = findViewById(R.id.getButton);
        get.setOnClickListener(this);

        btnDeviceInfo = findViewById(R.id.btn_device_info);
        btnDeviceInfo.setOnClickListener(this);

        ivIMSI = findViewById(R.id.iv_imsi);
        ivIMSI.setOnClickListener(this);

        ivIMEI = findViewById(R.id.iv_imei);
        ivIMEI.setOnClickListener(this);

        ivUDID = findViewById(R.id.iv_udid);
        ivUDID.setOnClickListener(this);

        rlMain = findViewById(R.id.rl_main);


        imsiText = findViewById(R.id.imsiTextView);
        imsiText.setText("IMSI: ");
        imeiText = findViewById(R.id.imeiTextView);
        imeiText.setText("IMEI: ");
        tvUdid = findViewById(R.id.tv_udid);
        tvUdid.setText("UDID: ");

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID
        // set in
        // values/strings.xml.
//        mAdView = findViewById(R.id.adView);
        showBannerAd();
        showInterstitialAd();

    }

    private void showBannerAd() {
        adView = findViewById(R.id.adView);
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
//                requestNewInterstitial();
            }

            @Override
            public void onAdLoaded() {
                showInterstitial();
            }
        });

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        switch (v.getId()) {

            case R.id.getButton:

                int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE_CLICK);
                } else {
//                    showInterstitialAd();
                    showData();
                }

                break;

            case R.id.btn_device_info:
                Intent intent = new Intent(MainActivity.this, DeviceInfoActivity.class);
                startActivity(intent);
                break;

            case R.id.iv_imsi:
                if (!imsiText.getText().toString().equals("IMSI: ")) {
                    copyToClipboard("IMSI", imsiText.getText().toString());
                }
                break;

            case R.id.iv_imei:
                if (!imeiText.getText().toString().equals("IMEI: ")) {
                    copyToClipboard("IMEI", imeiText.getText().toString());
                }
                break;

            case R.id.iv_udid:
                if (!tvUdid.getText().toString().equals("UDID: ")) {
                    copyToClipboard("UDID", tvUdid.getText().toString());
                }
                break;

        }

    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);

        Snackbar snackbar = Snackbar
                .make(rlMain, "Copied to clipboard", Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(R.id.snackbar_text);
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent));

        sbView.setBackgroundResource(R.color.colorPrimary);

        snackbar.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_PHONE_STATE:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    get = (Button) findViewById(R.id.getButton);
                    get.setOnClickListener(this);
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
                break;

            case REQUEST_READ_PHONE_STATE_CLICK:
                if ((grantResults.length > 0) && (grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    showData();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }
    }

    private void showData() {

//        showInterstitialAd();

        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String imsi = mTelephonyMgr.getSubscriberId();

        if (TextUtils.isEmpty(imsi)) {
            imsiText.setText("IMSI:" + "N/A");
        } else {
            imsiText.setText("IMSI:\n" + imsi);
        }

        String imei = mTelephonyMgr.getDeviceId();

        imeiText.setText("IMEI:\n" + imei);

        String udid = Settings.Secure.getString(
                MainActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        tvUdid.setText("UDID:\n" + udid);

    }

    private void showInterstitial() {
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        }
    }

    private void requestNewInterstitial() {
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
//                .build();

        AdRequest adRequest = new AdRequest.Builder().build();

        mInterstitialAd.loadAd(adRequest);
    }

    /**
     * Called when leaving the activity
     */
    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
//            requestNewInterstitial();
//            showInterstitial();
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

}
