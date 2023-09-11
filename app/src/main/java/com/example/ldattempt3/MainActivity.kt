package com.example.ldattempt3

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ldattempt3.ui.theme.LDAttempt3Theme


class MainActivity : ComponentActivity() {
    private lateinit var webView: WebView
    override fun onDestroy(){
        super.onDestroy()
        val accelerometerServiceIntent = Intent(this, AccelerometerService::class.java)
        stopService(accelerometerServiceIntent)
    }

    private val broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val data = intent.getStringExtra("someKey")
            if (data != null) {
                webView.evaluateJavascript(
                    data,
                    null
                )
            }
            //do your coding here using intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val channel = NotificationChannel(
            "SOME_CHANNEL_ID",
            "Some Channel Name",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)

        // doesnt do shit (I want fullscreen):  window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        webView = WebView(this)
        webView.webViewClient = WebViewClient()
        if (webView != null) {
            webView.setVisibility(View.VISIBLE)
            //webView.getSettings().setJavaScriptEnabled(true)
            val webSettings = webView.settings
            webSettings.javaScriptEnabled = true
            webSettings.allowContentAccess = true
            webView.settings.loadWithOverviewMode = true
            webView.settings.useWideViewPort = true
            //WebView.setWebContentsDebuggingEnabled(true) // allows Chrome dev tools from PC
            val builder = AlertDialog.Builder(this)
            builder.setTitle("URL")
            val input = EditText(this)
            input.inputType = InputType.TYPE_CLASS_TEXT
            input.setText("http://192.168.0.12:8080/ld.html.android")
            builder.setView(input)
            builder.setPositiveButton(
                "OK"
            ) {
                    dialog, which -> webView.loadUrl(input.text.toString())
            }
            builder.setNegativeButton(
                "Cancel"
            ) {
                    dialog, which -> dialog.cancel()
            }
            builder.show()
        } else {
            Log.e("WebViewInitError", "WebView not found in layout")
        }
        setContentView(webView)

        val accelerometerServiceIntent = Intent(this, AccelerometerService::class.java)
        startService(accelerometerServiceIntent)
        val filter = IntentFilter("SOME_ACTION")
        //filter.addCategory(Intent.CATEGORY_DEFAULT)
        registerReceiver(broadcastReceiver, filter)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LDAttempt3Theme {
        Greeting("Android")
    }
}
