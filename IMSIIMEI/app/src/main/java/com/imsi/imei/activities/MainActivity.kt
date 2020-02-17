package com.imsi.imei.activities

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.provider.Settings
import com.google.android.material.snackbar.Snackbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast

import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import com.imsi.imei.R
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.Exception
import java.util.*


class MainActivity : Activity(), OnClickListener {

    var perms: Array<String> =
            arrayOf(Manifest.permission.READ_PHONE_STATE)


    private var get: Button? = null
    private var btnDeviceInfo: Button? = null
    private var imsiText: TextView? = null
    private var imeiText: TextView? = null
    private var tvUdid: TextView? = null
    private var ivIMSI: ImageView? = null
    private var ivIMEI: ImageView? = null
    private var ivUDID: ImageView? = null
    private var rlMain: RelativeLayout? = null
    private var adView: AdView? = null
    private var mInterstitialAd: InterstitialAd? = null
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this, getString(R.string.app_ad_id))

        ActivityCompat.requestPermissions(this, perms, REQUEST_READ_PHONE_STATE)

        get = findViewById(R.id.getButton)
        get!!.setOnClickListener(this)

        btnDeviceInfo = findViewById(R.id.btn_device_info)
        btnDeviceInfo!!.setOnClickListener(this)

        ivIMSI = findViewById(R.id.iv_imsi)
        ivIMSI!!.setOnClickListener(this)

        ivIMEI = findViewById(R.id.iv_imei)
        ivIMEI!!.setOnClickListener(this)

        ivUDID = findViewById(R.id.iv_udid)
        ivUDID!!.setOnClickListener(this)

        rlMain = findViewById(R.id.rl_main)


        imsiText = findViewById(R.id.imsiTextView)
        imsiText!!.text = "IMSI: "
        imeiText = findViewById(R.id.imeiTextView)
        imeiText!!.text = "IMEI: "
        tvUdid = findViewById(R.id.tv_udid)
        tvUdid!!.text = "UDID: "

        // Gets the ad view defined in layout/ad_fragment.xml with ad unit ID
        // set in
        // values/strings.xml.
        //        mAdView = findViewById(R.id.adView);
        showBannerAd()
        showInterstitialAd()

    }

    private fun showBannerAd() {
        adView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView!!.loadAd(adRequest)
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
            override fun onAdClosed() {
                //                requestNewInterstitial();
            }

            override fun onAdLoaded() {
                showInterstitial()
            }
        }

    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.getButton -> {

                ActivityCompat.requestPermissions(this, perms, REQUEST_READ_PHONE_STATE_CLICK)
            }

            R.id.btn_device_info -> {
                val intent = Intent(this@MainActivity, DeviceInfoActivity::class.java)
                startActivity(intent)
            }

            R.id.iv_imsi -> if (imsiText!!.text.toString() != "IMSI: ") {
                copyToClipboard("IMSI", imsiText!!.text.toString())
            }

            R.id.iv_imei -> if (imeiText!!.text.toString() != "IMEI: ") {
                copyToClipboard("IMEI", imeiText!!.text.toString())
            }

            R.id.iv_udid -> if (tvUdid!!.text.toString() != "UDID: ") {
                copyToClipboard("UDID", tvUdid!!.text.toString())
            }
        }

    }

    private fun copyToClipboard(label: String, text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)

        val snackbar = Snackbar
                .make(rlMain!!, "Copied to clipboard", Snackbar.LENGTH_LONG)

        val sbView = snackbar.view
        val textView = sbView.findViewById<TextView>(R.id.snackbar_text)
        textView.setTextColor(ContextCompat.getColor(this, R.color.colorAccent))

        sbView.setBackgroundResource(R.color.colorPrimary)

        snackbar.show()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {

            REQUEST_READ_PHONE_STATE -> {
                var phoneStateAccepted: Boolean = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (phoneStateAccepted) {
                    get = findViewById<View>(R.id.getButton) as Button
                    get!!.setOnClickListener(this)
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show()
                }
            }
            REQUEST_READ_PHONE_STATE_CLICK -> {
                var phoneStateAccepted: Boolean = grantResults[0] == PackageManager.PERMISSION_GRANTED
                if (phoneStateAccepted) {
                    showData()
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun showData() {

        //        showInterstitialAd();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        val mTelephonyMgr: TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

        /*var value = Settings.System.getString(contentResolver, Settings.Secure.ANDROID_ID) //bfdd68bbcfa32a46
        value = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID) //bfdd68bbcfa32a46*/

        tvPhone.text = "Phone number: N/A"

        tvPhone.text = mTelephonyMgr.line1Number?.let { "Phone number: $it" }

        imsiText!!.text = "IMSI: " + "N/A"
        try {

            mTelephonyMgr!!.subscriberId?.let {
                imsiText?.text = "IMSI:\n${it}"
            }

        } catch (e: Exception) {
        }

        imeiText!!.text = "IMEI: N/A"
        try {
            mTelephonyMgr?.deviceId?.let { imeiText!!.text = "IMEI:\n${it}" }
        } catch (e: Exception) {
        }

        val udid = Settings.Secure.getString(
                this@MainActivity.contentResolver,
                Settings.Secure.ANDROID_ID)

        tvUdid!!.text = "UDID:\n$udid"

    }

    private fun showInterstitial() {
        if (mInterstitialAd!!.isLoaded) {
            val timer = object : CountDownTimer(6000, 1000) {
                override fun onTick(p0: Long) {
                    tvTimer.setText("Advertisement will show in: " + (p0 / 1000).toString())
                }

                override fun onFinish() {
                    tvTimer.setText("")
                    mInterstitialAd!!.show()
                }
            }
            timer.start()
        }
    }

    private fun requestNewInterstitial() {
        //        AdRequest adRequest = new AdRequest.Builder()
        //                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
        //                .build();

        val adRequest = AdRequest.Builder().build()

        mInterstitialAd!!.loadAd(adRequest)
    }

    /**
     * Called when leaving the activity
     */
    public override fun onPause() {
        if (adView != null) {
            adView!!.pause()
        }
        super.onPause()
    }

    /**
     * Called when returning to the activity
     */
    public override fun onResume() {
        super.onResume()
        if (adView != null) {
            adView!!.resume()
        }
    }

    /**
     * Called before the activity is destroyed
     */
    public override fun onDestroy() {
        if (adView != null) {
            adView!!.destroy()
        }
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            //            requestNewInterstitial();
            //            showInterstitial();
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }

    companion object {

        private val REQUEST_READ_PHONE_STATE = 1123
        private val REQUEST_READ_PHONE_STATE_CLICK = 1124
    }

    fun solution(A: IntArray): Int {
        // write your code in Kotlin
        var depth = 0
        var P = 0
        var Q = -1
        var R = -1
        var i: Int = 1;
        while (i < A.size) {

            if (Q < 0 && A[i] >= A[i - 1])
                Q = i - 1

            if ((Q >= 0 && R < 0) &&
                    (A[i] <= A[i - 1])
            ) {
                if (A[i] <= A[i - 1])
                    R = i - 1
                else
                    R = i
                depth = Math.max(depth, Math.min(A[P] - A[Q], A[R] - A[Q]))
                P = i - 1
                Q = R
                //R = -1
            }

            i++
        }
        if (depth == 0)
            depth = -1
        return depth
    }

}
