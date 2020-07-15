package com.paystack.push_notif_test

import android.app.Activity
import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pusher.pushnotifications.PushNotifications
import kotlinx.android.synthetic.main.activity_main.*
import me.pushy.sdk.Pushy


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PushNotifications.start(this, "94b17157-3130-46a9-900c-20a9380c3887")
        PushNotifications.addDeviceInterest("hello")

        Pushy.listen(this)

        if (!Pushy.isRegistered(this)) {
            RegisterForPushNotificationsAsync(this).execute();
        }
    }


    private class RegisterForPushNotificationsAsync(var mActivity: Activity) : AsyncTask<Void?, Void?, Any>() {
        override fun doInBackground(vararg params: Void?): Any {
            return try {
                // Register the device for notifications
                val deviceToken = Pushy.register(mActivity)

                // Registration succeeded, log token to logcat
                Log.d("Pushy", "Pushy device token: $deviceToken")


                // Provide token to onPostExecute()
                return deviceToken
            } catch (exc: Exception) {
                // Registration failed, provide exception to onPostExecute()
                exc
            }
        }

        override fun onPostExecute(result: Any) {
            // Registration failed?
            if (result is Exception) {
                // Display error as toast message
                Toast.makeText(mActivity, result.toString(), Toast.LENGTH_LONG).show()
                return
            }

            Log.d("Allen", result.toString())
            // Registration succeeded, display an alert with the device token
            AlertDialog.Builder(mActivity)
                .setTitle("Registration success")
                .setMessage("Pushy device token: $result\n\n(copy from logcat)")
                .setPositiveButton(android.R.string.ok, null)
                .show()
        }



    }
}
