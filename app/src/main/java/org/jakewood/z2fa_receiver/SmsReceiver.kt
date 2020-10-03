package org.jakewood.z2fa_receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import android.widget.Toast
import androidx.preference.PreferenceManager
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.httpPost

class SmsReceiver : BroadcastReceiver() {
    private fun getIncomingMessage(obj: ByteArray, bundle: Bundle): SmsMessage {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val format = bundle.getString("format")
            SmsMessage.createFromPdu(obj, format)
        } else {
            SmsMessage.createFromPdu(obj)
        }
    }

    private fun log(msg: String) {
        Log.i("expo-apple-2fa", msg)
    }

    @SuppressLint("ApplySharedPref")
    override fun onReceive(ctx: Context?, intent: Intent?) {
        this.log("expo-apple-2fa is working!")

        if (!intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            return
        }

        val bundle = intent!!.extras ?: return
        val pduObjs = bundle.get("pdus") as? Array<ByteArray> ?: return

        for (pdu in pduObjs) {
            val sms = getIncomingMessage(pdu, bundle)

            val settings = PreferenceManager.getDefaultSharedPreferences(ctx)
            val phone = settings.getString("twilio_phone", null)

            val body = sms.displayMessageBody.toString()
            val from = sms.displayOriginatingAddress.toString()

            if (settings.getString("discovery_mode", null) == "sms") {
                this.log("Discovery mode is SMS...")
                if (phone == from && body.startsWith("http")) {
                    this.log("Phone number matches and contains a URL!")
                    // Set our setting
                    val editSettings = settings.edit()
                    editSettings.putString("url", body)
                    editSettings.commit()
                }
            }

            if (body.contains("Your Apple ID Verification Code")) {
                // Last 6 digits
                val code = body.substring(body.length - 6)

                // Send it!
                val url = settings.getString("url", null)
                url?.httpPost()?.jsonBody("{\"code\": \"$code\"}")?.responseString { _ ->
                    Toast.makeText(ctx, "Apple ID successfully transferred!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
