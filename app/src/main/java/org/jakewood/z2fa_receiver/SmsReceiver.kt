package org.jakewood.z2fa_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.telephony.SmsMessage

class SmsReceiver : BroadcastReceiver() {
    private fun getIncomingMessage(obj: ByteArray, bundle: Bundle): SmsMessage {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val format = bundle.getString("format")
            SmsMessage.createFromPdu(obj, format)
        } else {
            SmsMessage.createFromPdu(obj)
        }
    }
    override fun onReceive(ctx: Context?, intent: Intent?) {
        if (intent != null && intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val bundle = intent.extras
            if (bundle != null) {
                val pduObjs = bundle.get("pdus") as? Array<ByteArray>
                if (pduObjs != null) {
                    for (pdu in pduObjs) {
                        val sms = getIncomingMessage(pdu, bundle)
                        println("Body is " + sms.displayMessageBody)
                    }
                }
            }
        }
    }
}