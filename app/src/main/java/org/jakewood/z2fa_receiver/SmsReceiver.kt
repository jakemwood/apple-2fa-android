package org.jakewood.z2fa_receiver

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage
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
    @SuppressLint("ApplySharedPref")
    override fun onReceive(ctx: Context?, intent: Intent?) {
        if (intent != null && intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val bundle = intent.extras
            if (bundle != null) {
                val pduObjs = bundle.get("pdus") as? Array<ByteArray>
                if (pduObjs != null) {
                    for (pdu in pduObjs) {
                        val sms = getIncomingMessage(pdu, bundle)

                        val settings = PreferenceManager.getDefaultSharedPreferences(ctx)
                        val phone = settings.getString("twilio_phone", null)

                        val body = sms.displayMessageBody.toString()
                        val from = sms.displayOriginatingAddress.toString()

                        println("From $from")
                        println("Body $body")

                        if (phone == from && body.startsWith("http")) {
                            // Set our setting
                            println("SET THE URL!!!")
                            val editSettings = settings.edit()
                            editSettings.putString("url", body)
                            editSettings.commit()
                        }

                        if (body.contains("Your Apple ID Verification Code")) {
                            println("APPLE ID RECEIVED!!!")

                            // Last 6 digits
                            val code = body.substring(body.length - 6)

                            // Send it!
                            val url = settings.getString("url", null)
                            url?.httpPost()?.jsonBody("{\"code\": \"$code\"}")?.response { response ->
                                println(response)
                                response.
                            }
                        }
                    }
                }
            }
        }
    }
}