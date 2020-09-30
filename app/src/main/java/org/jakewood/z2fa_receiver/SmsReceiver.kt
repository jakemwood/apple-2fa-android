package org.jakewood.z2fa_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context?, intent: Intent?) {
        if (intent != null && intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {

        }
    }
}