package com.paystack.push_notif_test

import android.app.Activity
import android.app.AlertDialog
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.pusher.client.Pusher
import com.pusher.client.PusherOptions
import com.pusher.client.channel.PusherEvent
import com.pusher.client.channel.SubscriptionEventListener
import com.pusher.client.connection.ConnectionEventListener
import com.pusher.client.connection.ConnectionState
import com.pusher.client.connection.ConnectionStateChange
import me.pushy.sdk.Pushy


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val options = PusherOptions()
        options.setCluster("eu")

        val pusher = Pusher("df133bc4c03d50d42f26", options)
        pusher.connect()

        pusher.connect(object : ConnectionEventListener {
            override fun onConnectionStateChange(change: ConnectionStateChange) {
                Log.i(
                    "Pusher", "State changed from " + change.previousState +
                            " to " + change.currentState
                )
            }

            override fun onError(
                message: String,
                code: String,
                e: java.lang.Exception
            ) {
                Log.i(
                    "Pusher", """
     There was a problem connecting! 
     code: $code
     message: $message
     Exception: $e
     """.trimIndent()
                )
            }
        }, ConnectionState.ALL)

        val channel = pusher.subscribe("my-channel")

        channel.bind("my-event") { event ->
            println(event?.data)
            Log.i("Pusher", "Received event with data: $event");
        }

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
