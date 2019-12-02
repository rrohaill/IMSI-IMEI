package com.imsi.imei.activities

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.imsi.imei.R
import kotlinx.android.synthetic.main.activity_main.*

class DeviceInfoActivity : Activity() {

    private var tvRootStatus: TextView? = null
    private val adView: AdView? = null
    private var mInterstitialAd: InterstitialAd? = null

    private var timerGlobal = null

    val timer = object : CountDownTimer(6000, 1000) {
        override fun onTick(p0: Long) {
            tvTimer.setText("Advertisement will show in: " + (p0 / 1000).toString())
        }

        override fun onFinish() {
            tvTimer.setText("")
            mInterstitialAd!!.show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_info)

        tvRootStatus = findViewById(R.id.tv_root)
        //        mAdView = (AdView) findViewById(R.id.adView);

        showBannerAd()

        showInterstitialAd()

        checkRoot()
    }

    private fun showBannerAd() {
        val adView = findViewById<AdView>(R.id.adView)
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }

    private fun showInterstitialAd() {
        // Create an ad request. Check your logcat output for the hashed device
        // ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        //        AdRequest adRequest = new AdRequest.Builder().addTestDevice(
        //                AdRequest.DEVICE_ID_EMULATOR).build();

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd!!.adUnitId = getString(R.string.interstitial_ad_id)

        mInterstitialAd!!.loadAd(AdRequest.Builder().build())

        mInterstitialAd!!.adListener = object : AdListener() {
            override fun onAdClosed() {}

            override fun onAdLoaded() {
                showInterstitial()
            }
        }

    }

    private fun showInterstitial() {
        if (mInterstitialAd!!.isLoaded) {

            timer.start()
        }
    }

    private fun checkRoot() {

        var data = "Root Status: "

        if (RootUtil.isDeviceRooted()) {
            data = data + "Device Rooted"
        } else {
            data = data + "Device not Rooted"
        }
        val newLine = "\n"
        data = (data + newLine + "Android Version: " + currentVersion() + newLine + "Manufacturer: " + Build.MANUFACTURER + newLine
                + "Model: " + Build.MODEL + newLine + "Brand: " + Build.BRAND
                + newLine + "Board: " + Build.BOARD)

        tvRootStatus!!.text = data
    }

    //Current Android version data
    fun currentVersion(): String {
        val no = Build.VERSION.RELEASE
        val release: Double
        if (no.length > 2) {
            release = java.lang.Double.parseDouble(no.substring(0, 2))
        } else {
            release = java.lang.Double.parseDouble(no)
        }
        var codeName = "Unsupported"//below Jelly bean OR above nougat
        if (release >= 4.1 && release < 4.4)
            codeName = "Jelly Bean"
        else if (release < 5)
            codeName = "Kit Kat"
        else if (release < 6)
            codeName = "Lollipop"
        else if (release < 7)
            codeName = "Marshmallow"
        else if (release < 8) codeName = "Nougat"
        return codeName + " v" + no + "\nAPI Level: " + Build.VERSION.SDK_INT
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }
}
