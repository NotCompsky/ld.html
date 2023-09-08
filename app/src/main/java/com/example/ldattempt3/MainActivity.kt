package com.example.ldattempt3

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
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
            webView.loadData(
                """<!DOCTYPE html>
<html>
<head>
	<title>Lucid Dreaming Audio</title>
	<meta content="width=device-width, initial-scale=1" name="viewport"/>
	<style>input[type=range]{width:100vw;}body{background:black;color:white;}*{margin:0;padding:0;}.blackout{width:100vw;height:100vh;}</style>
</head>
<body>
	<div class="blackout" id="blackout"></div>
	<div id="errors_container"></div>
	<input id="rooturl_input" type="text" value="http://192.168.0.12:8080" placeholder="http://192.168.0.123"/>
	<label for="rooturl_input">Root URL</label>
	<audio id="bg_audio" controls loop>
		<source id="bg_audio_source" src="data:," type="audio/webm"></source>
	</audio>
	<hr>
	Volume <input id="bg_audio_volume" type="range" value="0.1" min="0.01" max="1.0" step="0.01"/>
	<hr>
	<audio id="fg_audio">
		<source id="fg_audio_source" src="data:," type="audio/webm"></source>
	</audio>
	<button id="bg_audio_source_selector">Change BG audio</button>
	<button id="auto_select_and_play_next_audio">Autoplay</button>
	<div id="delays_inputs_containers"></div>
	<div id="audios_container"></div>
	<button id="addaudio">+</button>
	<button id="playbtn">Play</button>
	<button id="pausebtn">Pause</button>
	<script>
const audios = [];
const audio_namestrs = [];
const audio_mimetypes = [];
const audio_volumemults = [];
const audio_delays = [];
const audio_offsets = [];
const audio_endats = [];
const timeoutids = [];
var bg_audio_source_indx = 0;
const media_url2node = {};

var current_profile = {
	bg_audio_sources: ["/ld.html/audio/69"],
	tagid2name: [
		"UNTAGGED",
		"environmental_clues_UNUSED",
		"voices_of_people_UNUSED",
		"saying_name_UNUSED",
		"space",
		"realitycheck", // 5
		"star_trek",
		"half_life",
		"lecture_UNUSED",
		"music", // 9
		"dream_guide_1",
		"half_life_lore",
		"runningsounds_UNUSED", // 12
		"dangersounds_UNUSED", // 13
		"__DONT_USE__", // 14 (basically within playlist - only accessed from the FIRST media in the playlist)
		"read_text__not_explicitly_lucid_dreaming", // 15  TODO: Need more
		"mentioning_lucid_dreaming",
		"otherlanguages_UNUSED"
	],
	tagids_in_delay: [[4,11], [1,2,5,7,8,12,13,15,16,17], [1,2,5,7,8,12,13,15,16,17,3,10]],
	delay_times: [0,1800000,2700000],
	medias: [
		// space-lecture
		[[4], 46.465057373046875, 1, "video/webm", "/ld.html/audio/54", 0, 95.96, 112.72, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 44.96531677246094, 1, "video/webm", "/ld.html/audio/54", 0, 0.0, 37.24, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 11.280272483825684, 1, "video/webm", "/ld.html/audio/54", 0, 31.28, 32.52, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 45.62453079223633, 1, "video/webm", "/ld.html/audio/54", 0, 37.28, 51.96, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.292759895324707, 1, "video/webm", "/ld.html/audio/54", 0, 70.6, 73.28, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 47.21921157836914, 1, "video/webm", "/ld.html/audio/54", 0, 62.44, 80.84, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.00738525390625, 1, "video/webm", "/ld.html/audio/54", 0, 70.6, 71.4, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.298529624938965, 1, "video/webm", "/ld.html/audio/54", 0, 70.68, 73.28, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 37.46869659423828, 1, "video/webm", "/ld.html/audio/54", 0, 85.72, 95.92, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.281943321228027, 1, "video/webm", "/ld.html/audio/54", 0, 70.64, 73.28, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.301870346069336, 1, "video/webm", "/ld.html/audio/54", 0, 70.8, 71.24, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.233186721801758, 1, "video/webm", "/ld.html/audio/54", 0, 70.64, 73.2, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.4224214553833, 1, "video/webm", "/ld.html/audio/54", 0, 71.8, 73.28, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 46.475894927978516, 1, "video/webm", "/ld.html/audio/54", 0, 112.76, 125.84, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.291014671325684, 1, "video/webm", "/ld.html/audio/54", 0, 70.92, 73.28, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.429489135742188, 1, "video/webm", "/ld.html/audio/54", 0, 71.44, 73.28, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.568525314331055, 1, "video/webm", "/ld.html/audio/54", 0, 71.56, 73.28, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 46.573001861572266, 1, "video/webm", "/ld.html/audio/54", 0, 125.88, 139.56, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.543122291564941, 1, "video/webm", "/ld.html/audio/54", 0, 71.64, 73.28, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 42.44693374633789, 1, "video/webm", "/ld.html/audio/54", 0, 139.6, 147.92, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.569360733032227, 1, "video/webm", "/ld.html/audio/54", 0, 71.6, 73.28, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 46.69899368286133, 1, "video/webm", "/ld.html/audio/54", 0, 147.96, 163.88, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		// probably noise: [[4], 8.30782413482666, 1, "video/webm", "/ld.html/audio/54", 0, 72.04, 73.28, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 47.22103500366211, 1, "video/webm", "/ld.html/audio/54", 0, 163.92, 200.68, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 46.40129852294922, 1, "video/webm", "/ld.html/audio/54", 0, 200.72, 237.16, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 48.62520217895508, 1, "video/webm", "/ld.html/audio/54", 0, 237.2, 456.36, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 6.615916728973389, 1, "video/webm", "/ld.html/audio/54", 0, 456.4, 458.92, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],
		[[4], 42.88405990600586, 1, "video/webm", "/ld.html/audio/54", 0, 456.4, 527.96, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/space/What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm"],

		[[5], 14.668871879577637, 0, "audio/x-m4a", "/ld.html/audio/65", 0, 57.28, 63.64, "Let me ask you a question. You, never really remember the beginning of a dream, do you? You always wind up"],
		[[], 12.25837230682373, 0, "audio/x-m4a", "/ld.html/audio/65", 0, 64.0, 65.84, "in the middle of what's going on."],
		[[], 13.09231948852539, 0, "audio/x-m4a", "/ld.html/audio/65", 0, 67.32, 68.92, "So how did we end up here?"],
		[[], 6.0301594734191895, 0, "audio/x-m4a", "/ld.html/audio/65", 0, 71.56, 72.16, "Think about it"],
		[[], 6.451741695404053, 0, "audio/x-m4a", "/ld.html/audio/65", 0, 72.92, 81.88, "How did you get here? Where are you right now? [Suspenseful music, then female] We're dreaming?"],

		[[5], 21.09070587158203, 0, "audio/x-m4a", "/ld.html/audio/65", 0, 0.2, 0.84, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/RealityChecks/Inception (2010) - You're in a Dream Scene (2 of 10) | Movieclips.m4a"],
		[[5], 41.00811004638672, 0, "audio/x-m4a", "/ld.html/audio/68", 0, 31.48, 32.84, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/RealityChecks/GYhWq0ntvHg.2.m4a"],
		[[5], 37.52949523925781, 0, "audio/x-m4a", "/ld.html/audio/68", 0, 17.56, 26.64, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/RealityChecks/GYhWq0ntvHg.2.m4a"],
		[[5], 43.60905456542969, 0, "audio/x-m4a", "/ld.html/audio/68", 0, 74.6, 87.92, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/RealityChecks/GYhWq0ntvHg.2.m4a"],

		[[5], 39.18122482299805, 0, "audio/x-m4a", "/ld.html/audio/70", 0, 0.4, 73.22, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/RealityChecks/youtube-mU2TE_vTRas.m4a"],

		// holodeck
		[[6], 7.369682788848877, 0, "audio/x-m4a", "/ld.html/audio/71", 0, 0.0, 45.000272, "youtube--tTl975HazA.m4a"],
		// [[6], 3.741504192352295, 0, "audio/x-m4a", "/ld.html/audio/72", 0, 0.0, 42.010249, "This woodland pattern is quite popular sir ... [StarTrek TNG holodeck]"],  Seemed to wake me up, 2023-09-07

		[[7], 23.564111709594727, 0, "audio/x-m4a", "/ld.html/audio/74", 0, 0.0, 45.013786, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-nXZFnIWquB8.01-32-43.m4a"],
		[[7], 20.947532653808594, 0, "audio/x-m4a", "/ld.html/audio/75", 0, 0.0, 4.020589, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-nXZFnIWquB8.18-17.m4a"],
		[[7], 11.569991111755371, 0, "audio/x-m4a", "/ld.html/audio/76", 0, 0.0, 4.012879, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-nXZFnIWquB8.18-22.m4a"],
		[[7], 9.805381774902344, 0, "audio/x-m4a", "/ld.html/audio/77", 0, 0.0, 13.017505, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-nXZFnIWquB8.26-42.m4a"],
		[[7], 22.623088836669922, 0, "audio/x-m4a", "/ld.html/audio/78", 0, 0.0, 178.009795, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-nXZFnIWquB8.28-18.m4a"],
		[[7], 6.459517955780029, 0, "audio/x-m4a", "/ld.html/audio/79", 0, 0.0, 32.021768, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-nXZFnIWquB8.42-24.m4a"],
		[[7], 19.9863338470459, 0, "audio/x-m4a", "/ld.html/audio/80", 0, 0.0, 29.002902, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-nXZFnIWquB8.50-10.m4a"],
		[[7], 17.411558151245117, 0, "audio/x-m4a", "/ld.html/audio/81", 0, 0.0, 130.01, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-nXZFnIWquB8.54-57.m4a"],

		// half-life relaxing lore
		[[11], 27.647859573364258, 1, "video/webm", "/ld.html/audio/98", 0, 0.0, 352.45573696145124, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-RJGLuBAORNc.webm"],
		//
		[[11], 24.7135009765625, 0, "audio/x-m4a", "/ld.html/audio/99", 0, 0.0, 270.0016326530612, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-wLvl-H8bpWo.1.m4a"],
		[[11], 26.645709991455078, 0, "audio/x-m4a", "/ld.html/audio/100", 0, 0.0, 321.01750566893423, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-wLvl-H8bpWo.2.m4a"],
		[[11], 26.12750244140625, 0, "audio/x-m4a", "/ld.html/audio/101", 0, 0.0, 510.020589569161, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-wLvl-H8bpWo.3.m4a"],
		[[11], 23.489084243774414, 0, "audio/x-m4a", "/ld.html/audio/102", 0, 0.0, 407.0199546485261, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-wLvl-H8bpWo.4.m4a"],
		[[11], 16.792722702026367, 0, "audio/x-m4a", "/ld.html/audio/103", 0, 0.0, 860.0206802721088, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-wMpaqKyskX4.cut.m4a"],
		[[11], 21.04252052307129, 0, "audio/x-m4a", "/ld.html/audio/104", 0, 0.0, 792.9150113378685, "/media/vangelic/DATA/media/audio/for-lucid-dreaming/mix1/half-life/youtube-JbclO4G0170.cut.m4a"],
		[[11], 20.665712356567383, 0, "audio/x-m4a", "/ld.html/audio/122", 0, 0.0, 1539.0185941043085, "[HalfLife headcrabs lore I think]"],
		[[11], 20.104951858520508, 0, "audio/x-m4a", "/ld.html/audio/123", 0, 0.0, 1292.85, "[HalfLife Civil Protection lore]"],

		// Music:
		[[9], 58.761837005615234, 0, "audio/mpeg", "/ld.html/audio/89", 0, 0.0, 598.0614965986395, "/media/vangelic/DATA/Adam/Music/DeutschSprachige/DeutschsprachigeSangen_Pop/Best German Songs 2014b.mp3"],

		[[16], 30.629322052001953, 0, "audio/x-m4a", "/ld.html/audio/115", 0, 0.0, 163.10648526077097, "[Interview between 2 men about lucid dreaming flying techniques] ... that's a big sandal ... Lets make it fly!"],

		// Prompt to read text (not explicitly lucid dreaming)
		[[15], 20.401750564575195, 0, "audio/x-m4a", "/ld.html/audio/118", 0, 0.0, 99.5, "It is the Bell Jar by Sylvia Platz. ... This is 11 peoples' favourite book ... haven't read it since high school, so definitely due for a re-read ..."],
		// Keyboard guide

		[[15], 1.1853692531585693, 0, "audio/x-m4a", "/ld.html/audio/120", 0, 0.0, 40.00798185941043, "[Typing Noises]"],
		[[], 14.678925514221191, 0, "audio/x-m4a", "/ld.html/audio/119", 0, 0.0, 324.0208616780045, "[Keyboard tutorial, including instructions to look at his fingers and screen]"],
		//
		[[15], 8.72439193725586, 0, "audio/x-m4a", "/ld.html/audio/121", 0, 0.0, 881.3146485260771, "[Scribbling on paper noises]"],
	],
};

const tagid2audioindices = [];
const mediaindx2chain_next_audio = {};
const audios_container = document.getElementById("audios_container");
const try_load_again = document.getElementById("try_load_again");
const bg_audio = document.getElementById("bg_audio");
const bg_audio_source = document.getElementById("bg_audio_source");
const fg_audio = document.getElementById("fg_audio");
const fg_audio_source = document.getElementById("fg_audio_source");
const errors_container = document.getElementById("errors_container");
const rooturl_input = document.getElementById("rooturl_input");
const delays_inputs_containers = document.getElementById("delays_inputs_containers");
var should_select_and_play_next_audio = false;
//var setintervalrunning = false;
fg_audio.addEventListener("error", e=>{
	errors_container.innerText = e;
});
var prev2_mediaindx= -1;
var prev_mediaindx = -1;
var should_play_tagid_next = -1;
var last_movement_at_t = 0;
function set_movement_t_now(distance){
	last_movement_at_t = new Date().valueOf();
	fetch(document.location, {credentials:"include", method:"POST", mode:"no-cors", body:`[2,${'$'}{last_movement_at_t},${'$'}{distance}]`});
}
function update_for_updated_tagids(){
	let html = "";
	for (let j = 0;  j < current_profile.delay_times.length;  ++j){
		html += "<form>";
		html += `<input class="delay_input" data-i="${'$'}{j}" id="delay${'$'}{j}" type="number" value="${'$'}{current_profile.delay_times[j]/1000}" autocomplete="off"/><label for="delay${'$'}{j}">Delay${'$'}{j}</label>`;
		for (let i = 0;  i < current_profile.tagid2name.length;  ++i){
			html += `<input class="delay2tagid tagid${'$'}{i}" id="delay1tagid${'$'}{i}" data-ls="${'$'}{j}" data-i="${'$'}{i}" type="checkbox" ${'$'}{['','checked'][current_profile.tagids_in_delay[j].includes(i)|0]}/><label for="delay1tagid${'$'}{i}">${'$'}{current_profile.tagid2name[i]}</label>`;
		}
		html += "</form>";
	}
	delays_inputs_containers.innerHTML = html;
	for (let node of document.getElementsByClassName("delay2tagid")){
		node.addEventListener("change", e=>{
			const ls = current_profile.tagids_in_delay[e.currentTarget.dataset.ls|0];
			const tagid = e.currentTarget.dataset.i|0;
			if (ls.includes(tagid)){
				ls.sort((a,b) => (a===tagid)>(b===tagid));
				ls.length -= 1;
			} else {
				ls.push(tagid);
			}
		});
	}
	for (let node of document.getElementsByClassName("delay_input")){
		node.addEventListener("change", e=>{
			current_profile.delay_times[e.currentTarget.dataset.i|0] = 1000 * (e.currentTarget.value|0);
		});
	}
}
function maybe_select_and_play_next_audio(){
	if (should_select_and_play_next_audio){
		let mediaindx = -1;

		if (mediaindx2chain_next_audio[prev_mediaindx] !== undefined){
			mediaindx = mediaindx2chain_next_audio[prev_mediaindx];
		}

		const t = new Date().valueOf();

		if (mediaindx === -1){

		const t_diff = t - last_movement_at_t;
		let which_time_era_indx = -1;
		for (let j = 0;  j < current_profile.delay_times.length;  ++j){
			if (t_diff > current_profile.delay_times[j])
				++which_time_era_indx;
		}
		if (which_time_era_indx === -1){
			console.log("setTimeout(maybe_select_and_play_next_audio, (current_profile.delay_times[0] - t_diff));");
			setTimeout(maybe_select_and_play_next_audio, (current_profile.delay_times[0] - t_diff));
		}

		const option_indices = current_profile.tagids_in_delay[which_time_era_indx];
		// fetch(document.location, {credentials:"include", method:"POST", mode:"no-cors", body:`[0,"t_diff == ${'$'}{t_diff}"]`});

		let tagid = should_play_tagid_next;
		if (tagid === -1){
			tagid = option_indices[parseInt(option_indices.length*Math.random())];
			if ((tagid === 3) || (tagid === 5)){ // Saying Adam or Reality Check
				tagid = 3;
				should_play_tagid_next = 5;
			}
			// TODO: Text tattoos (inspirational quotes etc) to prompt lucid dream check of text
		} else {
			should_play_tagid_next = -1;
		}

		const ls = tagid2audioindices[tagid];

		do {
			mediaindx = ls[parseInt(ls.length*Math.random())];
		} while (((ls.length>1) && (mediaindx === prev_mediaindx)) || ((ls.length>2) && (mediaindx === prev2_mediaindx)));

		}

		prev2_mediaindx= prev_mediaindx;
		prev_mediaindx = mediaindx;

		fetch(document.location, {credentials:"include", method:"POST", mode:"no-cors", body:JSON.stringify([1,t,`${'$'}{audio_namestrs[mediaindx]} ${'$'}{audio_offsets[mediaindx]}-${'$'}{audio_endats[mediaindx]}`])});

		setTimeout(()=>play_nth_audio(mediaindx), 500);
	}
}
function int2timestr(n){
	const date = new Date(1000*n);
	return `${'$'}{date.getUTCHours()}:${'$'}{date.getUTCMinutes()}:${'$'}{date.getUTCSeconds()}`;
}
function setdelaychanged(e){
	const tgt = e.currentTarget;
	const value = tgt.value|0;
	tgt.nextSibling.innerText = int2timestr(value);
	const audio_indx = tgt.parentNode.parentNode.dataset.indx;
	if (audio_indx !== undefined)
		audio_delays[audio_indx|0] = 1000*value;
}
function setoffsetchanged(e){
	const tgt = e.currentTarget;
	const value = tgt.value|0;
	tgt.nextSibling.innerText = int2timestr(value);
	const audio_indx = tgt.parentNode.parentNode.dataset.indx;
	if (audio_indx !== undefined)
		audio_offsets[audio_indx|0] = value;
}
function setendatchanged(e){
	const tgt = e.currentTarget;
	const value = tgt.value|0;
	tgt.nextSibling.innerText = int2timestr(value);
	const audio_indx = tgt.parentNode.parentNode.dataset.indx;
	if (audio_indx !== undefined)
		audio_endats[audio_indx|0] = value;
}
function playthisaudio(ev){
	play_nth_audio(ev.currentTarget.parentNode.dataset.indx|0);
}
/*
There are 7 events that fire in this order when an audio file is loaded:

    loadstart
    durationchange
    loadedmetadata
    loadeddata
    progress
    canplay
    canplaythrough
*/
function addaudio_from_url(tagids, relative_volume, has_video, mimetype, url, delay, beginat, endat, namestr){
	const newcontainer = document.createElement("div");
	const name = document.createElement("h3");
	const newcontrols  = document.createElement("div");

	if (namestr === "")
		namestr = "[Untitled]";
	name.innerText = `[${'$'}{audios.length}] ${'$'}{namestr}`;

	if (tagids.length === 0){
		mediaindx2chain_next_audio[audios.length-1] = audios.length;
	}

	const playbtn = document.createElement("button");
	playbtn.innerText = "Play";
	playbtn.addEventListener("pointerup", playthisaudio);

	{
	const label1 = document.createElement("label");
	label1.innerText = "Delay";
	const valuedisplay = document.createElement("span");
	valuedisplay.innerText = int2timestr(delay);
	const setdelay = document.createElement("input");
	setdelay.setAttribute("type","range");
	setdelay.setAttribute("step","1");
	setdelay.setAttribute("value",`${'$'}{delay}`);
	setdelay.setAttribute("min","0");
	setdelay.setAttribute("max","36"); //000");
	setdelay.addEventListener("change", setdelaychanged);
	newcontrols.appendChild(label1);
	newcontrols.appendChild(setdelay);
	newcontrols.appendChild(valuedisplay);
	}

	{
	const label1 = document.createElement("label");
	label1.innerText = "Offset";
	const valuedisplay = document.createElement("span");
	valuedisplay.innerText = int2timestr(beginat);
	const setdelay = document.createElement("input");
	setdelay.setAttribute("type","text");
	setdelay.setAttribute("value",`${'$'}{beginat}`);
	setdelay.addEventListener("change", setoffsetchanged);
	newcontrols.appendChild(label1);
	newcontrols.appendChild(setdelay);
	newcontrols.appendChild(valuedisplay);
	}

	{
	const label1 = document.createElement("label");
	label1.innerText = "End at";
	const valuedisplay = document.createElement("span");
	valuedisplay.innerText = int2timestr(endat);
	const setdelay = document.createElement("input");
	setdelay.setAttribute("type","text");
	setdelay.setAttribute("value",`${'$'}{endat}`);
	setdelay.addEventListener("change", setendatchanged);
	newcontrols.appendChild(label1);
	newcontrols.appendChild(setdelay);
	newcontrols.appendChild(valuedisplay);
	}

	newcontainer.appendChild(name);
	newcontainer.appendChild(playbtn);

	newcontainer.dataset.indx = audios.length;
	audio_namestrs.push(namestr);
	audio_delays.push(1000 * delay);
	audio_offsets.push(beginat);
	audio_endats.push(endat);
	audio_volumemults.push(1.0/relative_volume);
	for (let tagid of tagids){
		tagid2audioindices[tagid].push(audios.length);
	}
	switch(mimetype){
		case "video/mpeg":
		case "video/mp4":
			mimetype = "audio/mpeg";
			break;
		case "video/webm":
			mimetype = "audio/webm";
			break;
		case "video/avi":
			mimetype = "audio/x-wav"; // audio/vnd.dts, audio/x-wav
			break;
		case "video/quicktime":
			mimetype = "audio/mpeg"; // audio/aac, audio/mpeg, audio/quicktime
			break;
		case "video/x-matroska":
			mimetype = "audio/vorbis";
			break;
		case "video/3gpp":
			mimetype = "audio/3gpp";
			break;
		case "video/x-m4v":
			mimetype = "audio/mp4";
			break;
	}
	if (mimetype.startsWith("video/"))
		console.warn(mimetype, "for", namestr);
	audio_mimetypes.push(mimetype);
	audios.push(url);
	timeoutids.push(0,0);

	newcontainer.appendChild(newcontrols);
	for (let tagid of tagids){
		newcontainer.classList.add(`tagid${'$'}{tagid}`);
	}

	audios_container.appendChild(newcontainer);
}
const addaudio_from_url__queue = [];
let addaudio_from_url__queue__indx = 0;
function addaudio_from_url__queued(tagids, relative_volume, has_video, mimetype, url, delay, beginat, endat, namestr){
	let is_dupl = false;
	for (let ls of addaudio_from_url__queue){
		is_dupl |= ((ls[4] === url) && (ls[6] === beginat) && (ls[7] === endat));
		if (is_dupl)
			break;
	}
	if (is_dupl)
		console.warn("Duplicate entry", tagids, relative_volume, has_video, mimetype, url, delay, beginat, endat, namestr);
	addaudio_from_url(tagids,relative_volume,has_video,mimetype,url,delay,beginat,endat,namestr);
}
function stopallevents(){
	for (let i = 0;  i < 2*timeoutids.length;  ++i){
		clearTimeout(timeoutids[i]);
	}
}
document.getElementById("addaudio").addEventListener("pointerup", ()=>{
	const url = prompt("URL");
	if (url){
		addaudio_from_url([], 0, "audio/mpeg", url, 0, 0.0, 0.0, "");
	}
});
function play_nth_audio(i){
	fg_audio.src = `${'$'}{(audios[i][0] !== '/') ? '' : rooturl_input.value}${'$'}{audios[i]}`;
	fg_audio.type = audio_mimetypes[i];
	fg_audio.load();
	fg_audio.volume = audio_volumemults[i];
	fg_audio.currentTime = audio_offsets[i];
	fg_audio.play();
	if (audio_endats[i] === 0.0){
		audio_endats[i] = fg_audio.duration;
	}
		timeoutids[2*i+1] = setTimeout(()=>end_nth_audio_if_currentTime_matches(i), end_nth_audio_if_currentTime_matches__delay+audio_delays[i]+(audio_endats[i]-audio_offsets[i])*1000.0);
}
var prev_currentTime = 0.0;
var n_times_called__end_nth_audio_if_currentTime_matches = 0;
var n_times_called__end_nth_audio_if_currentTime_matches__prev = 0;
function end_nth_audio_if_currentTime_matches(i){
	++n_times_called__end_nth_audio_if_currentTime_matches;
	let t_diff = fg_audio.currentTime - audio_endats[i];
	if (t_diff > -0.03){
		fg_audio.pause();
		//console.log("end_nth_audio_if_currentTime_matches", t_diff);
		maybe_select_and_play_next_audio();
	} else if (fg_audio.currentTime === prev_currentTime){
		console.error("end_nth_audio_if_currentTime_matches", "Media loading error?", i);
		fg_audio.pause();
		maybe_select_and_play_next_audio();
	} else if (t_diff < 0.0){
		prev_currentTime = fg_audio.currentTime;
		if (t_diff < -5.0)
			t_diff = -5.0;
		setTimeout(()=>end_nth_audio_if_currentTime_matches(i), -t_diff*1000.0);
		//console.log("end_nth_audio_if_currentTime_matches", "NO", i, audios[i], fg_audio.currentTime-fg_audio.duration, t_diff);
	} else {
		console.log("end_nth_audio_if_currentTime_matches", "ERROR: Reached end before endat for audio", i, t_diff);
		maybe_select_and_play_next_audio();
	}
}
function rotate_bg_audio(){
	++bg_audio_source_indx;
	if (bg_audio_source_indx === current_profile.bg_audio_sources.length){
		bg_audio_source_indx = 0;
	}
	bg_audio_source.src = `${'$'}{rooturl_input.value}${'$'}{current_profile.bg_audio_sources[bg_audio_source_indx]}`;
	bg_audio.load();
}
const end_nth_audio_if_currentTime_matches__delay = 40; // Arbitrary delay to allow for how long it takes for the media to start playing
document.getElementById("playbtn").addEventListener("pointerup", ()=>{
	stopallevents();
	for (let i = 0;  i < audio_delays.length;  ++i){
		timeoutids[2*i] = setTimeout(()=>play_nth_audio(i), audio_delays[i]);
	}
});
document.getElementById("pausebtn").addEventListener("pointerup", ()=>{
	stopallevents();
	for (let i = 0;  i < audio_delays.length;  ++i){
		fg_audio.pause();
	}
});
document.getElementById("bg_audio_source_selector").addEventListener("pointerup", rotate_bg_audio);
document.getElementById("auto_select_and_play_next_audio").addEventListener("pointerup", ()=>{
	stopallevents();
	prev2_mediaindx= -1;
	prev_mediaindx = -1;
	should_play_tagid_next = -1;
	should_select_and_play_next_audio = true;
	set_movement_t_now();
	maybe_select_and_play_next_audio();
});
document.getElementById("bg_audio_volume").addEventListener("change", e=>{
	bg_audio.volume = e.currentTarget.value;
});

tagid2audioindices.length = 0;
for (let i = 0;  i < current_profile.tagid2name.length;  ++i)
	tagid2audioindices.push([]);
rotate_bg_audio();
update_for_updated_tagids();
for (let ls of current_profile.medias)
	addaudio_from_url__queued(...ls);

	</script>
</body>
</html>""", "text/html", "UTF-8"
            )
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
