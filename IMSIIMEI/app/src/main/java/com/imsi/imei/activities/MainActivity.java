package com.imsi.imei.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
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
import com.imsi.imei.R;


public class MainActivity extends Activity implements OnClickListener {

    private static final int REQUEST_READ_PHONE_STATE = 1123;
    private static final int REQUEST_READ_PHONE_STATE_CLICK = 1124;
    private Button get;
    private TextView imsiText, imeiText;
    private TextView tvUdid;
    private ImageView ivIMSI, ivIMEI, ivUDID;
    private RelativeLayout rlMain;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);

        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_READ_PHONE_STATE);
        }

        get = (Button) findViewById(R.id.getButton);
        get.setOnClickListener(this);

        ivIMSI = (ImageView) findViewById(R.id.iv_imsi);
        ivIMSI.setOnClickListener(this);

        ivIMEI = (ImageView) findViewById(R.id.iv_imei);
        ivIMEI.setOnClickListener(this);

        ivUDID = (ImageView) findViewById(R.id.iv_udid);
        ivUDID.setOnClickListener(this);

        rlMain = (RelativeLayout) findViewById(R.id.rl_main);


        imsiText = (TextView) findViewById(R.id.imsiTextView);
        imsiText.setText("IMSI: ");
        imeiText = (TextView) findViewById(R.id.imeiTextView);
        imeiText.setText("IMEI: ");
        tvUdid = (TextView) findViewById(R.id.tv_udid);
        tvUdid.setText("UDID: ");

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID
        // set in
        // values/strings.xml.
        mAdView = (AdView) findViewById(R.id.adView);

        // Create an ad request. Check your logcat output for the hashed device
        // ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(
//                AdRequest.DEVICE_ID_EMULATOR).build();

        AdRequest adRequest = new AdRequest.Builder().build();

        // Start loading the ad in the background.
        mAdView.loadAd(adRequest);

        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.interstitial_ad_id));

        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
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
                    showData();
                }

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


        // TelephonyManager tMgr =
        // (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        // String mPhoneNumber = tMgr.getLine1Number();
        // imeiText.setText(mPhoneNumber);

    }

    private void copyToClipboard(String label, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(label, text);
        clipboard.setPrimaryClip(clip);

        Snackbar snackbar = Snackbar
                .make(rlMain, "Copied to clipboard", Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
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

        showInterstitial();

        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

//        TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(this);
//        boolean isSIM1Ready = telephonyInfo.isSIM1Ready();
//        boolean isSIM2Ready = telephonyInfo.isSIM2Ready();
//
//        final List<CellInfo> allCellInfo = mTelephonyMgr.getAllCellInfo();
//        for (CellInfo cellInfo : allCellInfo) {
//            if (cellInfo instanceof CellInfoGsm) {
//                CellIdentityGsm cellIdentity = ((CellInfoGsm) cellInfo).getCellIdentity();
//                //TODO Use cellIdentity to check MCC/MNC code, for instance.
//            } else if (cellInfo instanceof CellInfoWcdma) {
//                CellIdentityWcdma cellIdentity = ((CellInfoWcdma) cellInfo).getCellIdentity();
//            } else if (cellInfo instanceof CellInfoLte) {
//                CellIdentityLte cellIdentity = ((CellInfoLte) cellInfo).getCellIdentity();
//            } else if (cellInfo instanceof CellInfoCdma) {
//                CellIdentityCdma cellIdentity = ((CellInfoCdma) cellInfo).getCellIdentity();
//            }
//        }


        String imsi = mTelephonyMgr.getSubscriberId();

        imsiText.setText("IMSI: " + imsi);

        String imei = mTelephonyMgr.getDeviceId();

        imeiText.setText("IMEI: " + imei);

        String udid = Settings.Secure.getString(
                MainActivity.this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        tvUdid.setText("UDID: " + udid);
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
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    /**
     * Called when returning to the activity
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    /**
     * Called before the activity is destroyed
     */
    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

}
