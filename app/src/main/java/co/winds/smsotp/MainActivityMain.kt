package co.winds.smsotp

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_gps_permission.view.*
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivityMain : AppCompatActivity() {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action!!.equals("otp", ignoreCase = true)) {
                val message = intent.getStringExtra("message")
                Log.d("SmsReceiver", "smsReceiver otp 2 : $message")
                pin_otp.value=message
                //Do whatever you want with the code here
            }
        }
    }

    companion object {
        val REQUEST_ID_MULTIPLE_PERMISSIONS = 1
        val TAG ="OTP SCREEN"
    }

    var secondTime=120
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        initView()
        startTimer(secondTime)



    }


    private fun startTimer(noOfMinutes: Int) {
          object : CountDownTimer(noOfMinutes.toLong()*1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                reSend(false)
                val hms = String.format("%02d:%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)))
                tv_timer.text = hms
            }

            override fun onFinish() {
                reSend(true)
                tv_timer.text = minSecond(secondTime)
            }
        }.start()

    }



    private fun reSend(flag:Boolean){
        if(flag){
            resentOTP.isEnabled=true
            resentOTP.setTextColor(Color.parseColor("#0101C4"))
        }else{
            resentOTP.isEnabled=false
            resentOTP.setTextColor(Color.parseColor("#9797E0"))
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initView(){

        resentOTP.setOnClickListener {
            startTimer(secondTime)
        }

        pin_otp.setPinViewEventListener { pinview, b ->
            Log.d(TAG,"OTP Edit : ${pinview.value} -  true $b")
        }


        btn.setOnClickListener {
            pin_otp.value
            val OTP=pin_otp.value
            val currentNewPass = edt_new_password.text.toString()
            when {
                OTP.isEmpty() -> {
                    hideSoftKeyboard()
                    showSnackBar("Please enter OTP Code")
                }
                OTP.length <= 5 -> {
                    hideSoftKeyboard()
                    showSnackBar("OTP must be at least 6 characters long")
                }
                currentNewPass.isBlank() -> {
                    hideSoftKeyboard()
                    showSnackBar("Enter new password")
                }
                currentNewPass.length <= 7 -> {
                    hideSoftKeyboard()
                    showSnackBar("valdation password")
                }else -> showSnackBar("Ok........")
            }
        }

    }


    override fun onStart() {
        super.onStart()
        if(checkAndRequestPermissions()){
            Toast.makeText(applicationContext,"OK create..",Toast.LENGTH_SHORT).show()

        }
    }

    public override fun onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter("otp"))
        super.onResume()
    }

    public override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    fun checkAndRequestPermissions(): Boolean {
        val read_smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
        val receive_smsPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
        val listPermissionsNeeded = ArrayList<String>()
        if (read_smsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS)
        }
        if (receive_smsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_SMS)
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(),
                REQUEST_ID_MULTIPLE_PERMISSIONS
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d(TAG, "Permission callback called-------")
        when (requestCode) {
            REQUEST_ID_MULTIPLE_PERMISSIONS -> {
                val perms = HashMap<String, Int>()
                perms[Manifest.permission.READ_SMS] = PackageManager.PERMISSION_GRANTED
                perms[Manifest.permission.RECEIVE_SMS] = PackageManager.PERMISSION_GRANTED
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices)
                        perms[permissions[i]] = grantResults[i]
                    if ( perms[Manifest.permission.READ_SMS] == PackageManager.PERMISSION_GRANTED
                        && perms[Manifest.permission.RECEIVE_SMS] == PackageManager.PERMISSION_GRANTED) {
                     Toast.makeText(applicationContext,"OK..",Toast.LENGTH_SHORT).show()

                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ")
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)
                            || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECEIVE_SMS)) {
                            openBottomSheetSMS()
                        } else {
                            openBottomSheetDenyPermissionSettings()
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }//permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                    }
                }
            }
        }


    }

    private fun openBottomSheetSMS() {
        val view = layoutInflater.inflate(R.layout.bottom_sheet_gps_permission, null)
        val mBottomSheetDialog = Dialog(this@MainActivityMain, R.style.MaterialDialogSheet)
        mBottomSheetDialog.setContentView(view)
        mBottomSheetDialog.setCancelable(false)
        mBottomSheetDialog.window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        mBottomSheetDialog.window.setGravity(Gravity.BOTTOM)
        mBottomSheetDialog.show()
        view.tv_content.text="SMS should be enabled automatic sms verification"
        view.btn.text="Allow Permission"
        view.btn.setOnClickListener {
                mBottomSheetDialog.dismiss()
                recreate()
        }



        // btnDone.setOnClickListener(View.OnClickListener { mBottomSheetDialog.dismiss() })

    }

    private fun openBottomSheetDenyPermissionSettings() {
        val view = layoutInflater.inflate(R.layout.bottom_sheet_gps_permission, null)
        val mBottomSheetDialog = Dialog(this@MainActivityMain, R.style.MaterialDialogSheet)
        mBottomSheetDialog.setContentView(view)
        mBottomSheetDialog.setCancelable(false)
        mBottomSheetDialog.window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        mBottomSheetDialog.window.setGravity(Gravity.BOTTOM)
        mBottomSheetDialog.show()
        view.tv_content.text = "You need to give some mandatory permissions to continue. Do you want to go to app settings?"
        view.btn.text = "Enable Permission Go to settings"
        view.btn.setOnClickListener {
            mBottomSheetDialog.dismiss()
            val myAppSettings =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:$packageName"))
            myAppSettings.addCategory(Intent.CATEGORY_DEFAULT)
            myAppSettings.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(myAppSettings)
        }
    }
}
