package co.winds.smsotp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.content.LocalBroadcastManager
import android.telephony.SmsMessage
import android.util.Log
import java.util.regex.Pattern


class IncomingSms : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val bundle = intent.extras
        val  p = Pattern.compile("(|^)\\d{6}");
        try {
            if (bundle != null) {

                val pdusObj = bundle.get("pdus") as Array<*>
                for (i in pdusObj.indices) {
                    val smsMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                   // val sender = smsMessage.displayOriginatingAddress mobile no
                    val messageBody: String? = smsMessage.messageBody
                    Log.d("SmsReceiver", "Exception smsReceiver matcher 1: $messageBody")
                    if(messageBody!!.contains("winds captain")){
                        Log.d("SmsReceiver", "Exception smsReceiver matcher 2 : $messageBody")
                        val m = p.matcher(messageBody)
                        if (m.find()) {
                                val myIntent = Intent("otp")
                                val otp= m.group(0)
                                Log.d("SmsReceiver", "smsReceiver otp : $otp")
                                myIntent.putExtra("message",otp)
                                LocalBroadcastManager.getInstance(context).sendBroadcast(myIntent)
                            }
                        }
                } // end for loop
            } // bundle is null

        } catch (e: Exception) {
            Log.e("SmsReceiver", "Exception smsReceiver$e")

        }
    }
}