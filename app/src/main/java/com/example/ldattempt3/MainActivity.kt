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
    <meta http-equiv="Access-Control-Allow-Origin" content="http://192.168.0.12:8080"/>
<style>input{width:100vw;}body{background:black;color:white;}*{margin:0;padding:0;}.blackout{width:100vw;height:100vh;}</style>
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
	<div id="audios_container"></div>
	<button id="addaudio">+</button>
	<button id="playbtn">Play</button>
	<button id="pausebtn">Pause</button>
	<script>
const audios = [];
const audio_mimetypes = [];
const audio_volumemults = [];
const audio_delays = [];
const audio_offsets = [];
const audio_endats = [];
const timeoutids = [];
const bg_audio_sources = [69];
var bg_audio_source_indx = 0;
const media_url2node = {};
const tagid2classname = [
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
];
const tagsid2classnames = [
	[0],
	[1],
	[2],
	[3], // 3
	[4],
	[5], // 5
	[6],
	[7],
	[8],
	[9],
	[10],
	[11],
];
const tagid2audioindices = [
	[],
	[],
	[],
	[], // 3
	[],
	[], // 5
	[],
	[],
	[],
	[],
	[],
	[],
];
const audios_container = document.getElementById("audios_container");
const try_load_again = document.getElementById("try_load_again");
const bg_audio = document.getElementById("bg_audio");
const bg_audio_source = document.getElementById("bg_audio_source");
const fg_audio = document.getElementById("fg_audio");
const fg_audio_source = document.getElementById("fg_audio_source");
const errors_container = document.getElementById("errors_container");
const rooturl_input = document.getElementById("rooturl_input");
var should_select_and_play_next_audio = false;
//var setintervalrunning = false;
var prev_mediaindx = 0;
var is_playing_playlist = 0;
fg_audio.addEventListener("error", e=>{
	errors_container.innerText = e;
});
function maybe_select_and_play_next_audio(){
	if (should_select_and_play_next_audio){
		let mediaindx = 0;

		if (is_playing_playlist === 10){
			const indx = tagid2audioindices[is_playing_playlist].indexOf(prev_mediaindx) + 1;
			if (indx === tagid2audioindices[is_playing_playlist].length){
				is_playing_playlist = 0;
			} else {
				mediaindx = tagid2audioindices[is_playing_playlist][indx];
			}
		}

		const t = new Date().valueOf();

		if (mediaindx === 0){

		let option_indices = [4,11];
		const t_diff = t - last_movement_at_t;
		if (t_diff > 3600000){ // 1 hour
			option_indices = [0,1,2,4,5,6,7,8];
		}
		if (t_diff > 7200000){ // 2 hours
			option_indices.push(3,10);
		}

		const tagid = option_indices[parseInt(option_indices.length*Math.random())];

		if (tagid === 10){
			is_playing_playlist = tagid;
			mediaindx = tagid2audioindices[is_playing_playlist][0];
		}

		const ls = tagid2audioindices[tagid];

		do {
			mediaindx = ls[parseInt(ls.length*Math.random())];
		} while (mediaindx === prev_mediaindx);

		}

		prev_mediaindx = mediaindx;

		fetch(`${'$'}{rooturl_input.value}/ld.html`, {credentials:"include", method:"POST", mode:'no-cors', body:JSON.stringify([1,t,`${'$'}{audios[mediaindx]} ${'$'}{audio_offsets[mediaindx]}-${'$'}{audio_endats[mediaindx]}`])});

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
function addaudio_from_url(tagsid, relative_volume, has_video, mimetype, url, delay, beginat, endat, namestr){
	const newcontainer = document.createElement("div");
	const name = document.createElement("h3");
	const newcontrols  = document.createElement("div");

	if (namestr === "")
		namestr = "[Untitled]";
	name.innerText = namestr;

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
	audio_delays.push(1000 * delay);
	audio_offsets.push(beginat);
	audio_endats.push(endat);
	audio_volumemults.push(1.0/relative_volume);
	const tagids = tagsid2classnames[tagsid];
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
	if (tagsid !== 0){
		for (let tagid of tagsid2classnames[tagsid]){
			newcontainer.classList.add(`tag_${'$'}{tagid2classname[tagid]}`);
		}
	}

	audios_container.appendChild(newcontainer);
}
const addaudio_from_url__queue = [];
let addaudio_from_url__queue__indx = 0;
function addaudio_from_url__queued(tagsid, relative_volume, has_video, mimetype, url, delay, beginat, endat, namestr){
	let is_dupl = false;
	for (let ls of addaudio_from_url__queue){
		is_dupl |= ((ls[4] === url) && (ls[6] === beginat) && (ls[7] === endat));
		if (is_dupl)
			break;
	}
	if (is_dupl)
		console.warn("Duplicate entry", tagsid, relative_volume, has_video, mimetype, url, delay, beginat, endat, namestr);
	else
		addaudio_from_url(tagsid,relative_volume,has_video,mimetype,url,delay,beginat,endat,namestr);
}
function stopallevents(){
	for (let i = 0;  i < 2*timeoutids.length;  ++i){
		clearTimeout(timeoutids[i]);
	}
}
document.getElementById("addaudio").addEventListener("pointerup", ()=>{
	const url = prompt("URL");
	if (url){
		addaudio_from_url(0, 0, "audio/mpeg", url, 0, 0.0, 0.0, "");
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
document.getElementById("bg_audio_source_selector").addEventListener("pointerup", ()=>{
	++bg_audio_source_indx;
	if (bg_audio_source_indx === bg_audio_sources.length){
		bg_audio_source_indx = 0;
	}
	bg_audio_source.src = `${'$'}{rooturl_input.value}/ld.html/audio/${'$'}{bg_audio_sources[bg_audio_source_indx]}`;
	bg_audio.load();
});
document.getElementById("auto_select_and_play_next_audio").addEventListener("pointerup", ()=>{
	should_select_and_play_next_audio = true;
	maybe_select_and_play_next_audio();
});
document.getElementById("bg_audio_volume").addEventListener("change", e=>{
	bg_audio.volume = e.currentTarget.value;
});

var last_movement_at_t = new Date().valueOf();
function set_movement_t_now(distance){
	last_movement_at_t = new Date().valueOf();
	fetch(`${'$'}{rooturl_input.value}/ld.html`, {credentials:"include", method:"POST", mode:'no-cors', body:`[2,${'$'}{last_movement_at_t},${'$'}{distance}]`});
}

if (true){ // space-lecture
addaudio_from_url__queued(4, 46.465057373046875, 1, "video/webm", `/ld.html/audio/54`, 0, 95.96, 112.72, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 44.96531677246094, 1, "video/webm", `/ld.html/audio/54`, 0, 0.0, 37.24, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 11.280272483825684, 1, "video/webm", `/ld.html/audio/54`, 0, 31.28, 32.52, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 45.62453079223633, 1, "video/webm", `/ld.html/audio/54`, 0, 37.28, 51.96, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.292759895324707, 1, "video/webm", `/ld.html/audio/54`, 0, 70.6, 73.28, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 47.21921157836914, 1, "video/webm", `/ld.html/audio/54`, 0, 62.44, 80.84, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.00738525390625, 1, "video/webm", `/ld.html/audio/54`, 0, 70.6, 71.4, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.298529624938965, 1, "video/webm", `/ld.html/audio/54`, 0, 70.68, 73.28, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 37.46869659423828, 1, "video/webm", `/ld.html/audio/54`, 0, 85.72, 95.92, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.281943321228027, 1, "video/webm", `/ld.html/audio/54`, 0, 70.64, 73.28, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.301870346069336, 1, "video/webm", `/ld.html/audio/54`, 0, 70.8, 71.24, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.233186721801758, 1, "video/webm", `/ld.html/audio/54`, 0, 70.64, 73.2, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.4224214553833, 1, "video/webm", `/ld.html/audio/54`, 0, 71.8, 73.28, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 46.475894927978516, 1, "video/webm", `/ld.html/audio/54`, 0, 112.76, 125.84, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.291014671325684, 1, "video/webm", `/ld.html/audio/54`, 0, 70.92, 73.28, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.429489135742188, 1, "video/webm", `/ld.html/audio/54`, 0, 71.44, 73.28, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.568525314331055, 1, "video/webm", `/ld.html/audio/54`, 0, 71.56, 73.28, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 46.573001861572266, 1, "video/webm", `/ld.html/audio/54`, 0, 125.88, 139.56, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.543122291564941, 1, "video/webm", `/ld.html/audio/54`, 0, 71.64, 73.28, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 42.44693374633789, 1, "video/webm", `/ld.html/audio/54`, 0, 139.6, 147.92, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.569360733032227, 1, "video/webm", `/ld.html/audio/54`, 0, 71.6, 73.28, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 46.69899368286133, 1, "video/webm", `/ld.html/audio/54`, 0, 147.96, 163.88, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
// probably noise: addaudio_from_url__queued(4, 8.30782413482666, 1, "video/webm", `/ld.html/audio/54`, 0, 72.04, 73.28, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 47.22103500366211, 1, "video/webm", `/ld.html/audio/54`, 0, 163.92, 200.68, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 46.40129852294922, 1, "video/webm", `/ld.html/audio/54`, 0, 200.72, 237.16, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 48.62520217895508, 1, "video/webm", `/ld.html/audio/54`, 0, 237.2, 456.36, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 6.615916728973389, 1, "video/webm", `/ld.html/audio/54`, 0, 456.4, 458.92, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");
addaudio_from_url__queued(4, 42.88405990600586, 1, "video/webm", `/ld.html/audio/54`, 0, 456.4, 527.96, "What Does the Center of the Milky Way Look Like？ A Journey to the Heart of Our Galaxy! (4K UHD) [yCo2Tz4sRX8].webm");

addaudio_from_url__queued(5, 14.668871879577637, 0, "audio/x-m4a", `/ld.html/audio/65`, 0, 57.28, 63.64, "Inception (2010) - You're in a Dream Scene (2 of 10) | Movieclips.m4a");
addaudio_from_url__queued(5, 12.25837230682373, 0, "audio/x-m4a", `/ld.html/audio/65`, 0, 64.0, 65.84, "Inception (2010) - You're in a Dream Scene (2 of 10) | Movieclips.m4a");
addaudio_from_url__queued(5, 13.09231948852539, 0, "audio/x-m4a", `/ld.html/audio/65`, 0, 67.32, 68.92, "Inception (2010) - You're in a Dream Scene (2 of 10) | Movieclips.m4a");
addaudio_from_url__queued(5, 6.0301594734191895, 0, "audio/x-m4a", `/ld.html/audio/65`, 0, 71.56, 72.16, "Inception (2010) - You're in a Dream Scene (2 of 10) | Movieclips.m4a");
addaudio_from_url__queued(5, 6.451741695404053, 0, "audio/x-m4a", `/ld.html/audio/65`, 0, 72.92, 81.88, "Inception (2010) - You're in a Dream Scene (2 of 10) | Movieclips.m4a");

addaudio_from_url__queued(0, 19.956623077392578, 0, "audio/x-m4a", `/ld.html/audio/64`, 0, 181.56, 186.96, "part20001.m4a");
addaudio_from_url__queued(0, 18.41073989868164, 0, "audio/x-m4a", `/ld.html/audio/64`, 0, 188.52, 192.52, "part20001.m4a");
addaudio_from_url__queued(0, 18.3088436126709, 0, "audio/x-m4a", `/ld.html/audio/64`, 0, 203.52, 210.68, "part20001.m4a");
addaudio_from_url__queued(0, 19.850515365600586, 0, "audio/x-m4a", `/ld.html/audio/64`, 0, 4438.84, 4440.92, "part20001.m4a");
addaudio_from_url__queued(4, 12.245783805847168, 1, "video/mp4", `/ld.html/audio/2`, 0, 30.6, 34.44, "ytH_-yewV6hkwAPO.mp4");
addaudio_from_url__queued(4, 15.102410316467285, 1, "video/mp4", `/ld.html/audio/2`, 0, 34.48, 39.28, "ytH_-yewV6hkwAPO.mp4");
addaudio_from_url__queued(0, 16.678377151489258, 0, "audio/x-m4a", `/ld.html/audio/64`, 0, 210.72, 216.16, "part20001.m4a");
addaudio_from_url__queued(0, 19.291946411132812, 0, "audio/x-m4a", `/ld.html/audio/66`, 0, 53.32, 62.08, "part40001.m4a");
addaudio_from_url__queued(0, 24.688779830932617, 0, "audio/x-m4a", `/ld.html/audio/67`, 0, 2326.72, 2330.92, "part30001.m4a");
addaudio_from_url__queued(0, 19.211572647094727, 0, "audio/x-m4a", `/ld.html/audio/64`, 0, 5094.44, 5102.84, "part20001.m4a");
addaudio_from_url__queued(0, 19.5963134765625, 0, "audio/x-m4a", `/ld.html/audio/67`, 0, 1813.76, 1817.92, "part30001.m4a");
addaudio_from_url__queued(0, 12.03065013885498, 0, "audio/x-m4a", `/ld.html/audio/64`, 0, 159.24, 161.2, "part20001.m4a");
addaudio_from_url__queued(0, 20.535804748535156, 0, "audio/x-m4a", `/ld.html/audio/67`, 0, 1990.24, 1999.2, "part30001.m4a");
addaudio_from_url__queued(0, 20.888694763183594, 0, "audio/x-m4a", `/ld.html/audio/67`, 0, 531.08, 533.04, "part30001.m4a");
addaudio_from_url__queued(0, 20.313322067260742, 0, "audio/x-m4a", `/ld.html/audio/67`, 0, 1849.68, 1860.68, "part30001.m4a");
addaudio_from_url__queued(0, 22.851179122924805, 0, "audio/x-m4a", `/ld.html/audio/67`, 0, 1175.48, 1180.96, "part30001.m4a");
addaudio_from_url__queued(0, 20.585693359375, 0, "audio/x-m4a", `/ld.html/audio/67`, 0, 1181.0, 1183.28, "part30001.m4a");
addaudio_from_url__queued(0, 14.871894836425781, 0, "audio/x-m4a", `/ld.html/audio/67`, 0, 1270.0, 1273.24, "part30001.m4a");
addaudio_from_url__queued(0, 19.558223724365234, 0, "audio/x-m4a", `/ld.html/audio/64`, 0, 189.28, 192.52, "part20001.m4a");
addaudio_from_url__queued(0, 19.776073455810547, 0, "audio/x-m4a", `/ld.html/audio/64`, 0, 203.52, 207.84, "part20001.m4a");
addaudio_from_url__queued(0, 15.929011344909668, 0, "audio/x-m4a", `/ld.html/audio/64`, 0, 207.88, 210.68, "part20001.m4a");
addaudio_from_url__queued(5, 21.09070587158203, 0, "audio/x-m4a", `/ld.html/audio/65`, 0, 0.2, 0.84, "Inception (2010) - You're in a Dream Scene (2 of 10) | Movieclips.m4a");
addaudio_from_url__queued(1, 19.99064826965332, 0, "audio/x-m4a", `/ld.html/audio/64`, 0, 3215.84, 3222.28, "part20001.m4a");
}

// holodeck
addaudio_from_url__queued(6, 7.369682788848877, 0, "audio/x-m4a", `/ld.html/audio/71`, 0, 0.0, 45.000272, "youtube--tTl975HazA.m4a");
addaudio_from_url__queued(6, 3.741504192352295, 0, "audio/x-m4a", `/ld.html/audio/72`, 0, 0.0, 42.010249, "youtube-Oy5DAxGhV_c.m4a");

// half-life relaxing lore
addaudio_from_url__queued(11, 27.647859573364258, 1, "video/webm", `/ld.html/audio/98`, 0, 0.0, 352.45573696145124, "youtube-RJGLuBAORNc.webm");
//
addaudio_from_url__queued(11, 24.7135009765625, 0, "audio/x-m4a", `/ld.html/audio/99`, 0, 0.0, 270.0016326530612, "youtube-wLvl-H8bpWo.1.m4a");
addaudio_from_url__queued(11, 26.645709991455078, 0, "audio/x-m4a", `/ld.html/audio/100`, 0, 0.0, 321.01750566893423, "youtube-wLvl-H8bpWo.2.m4a");
addaudio_from_url__queued(11, 26.12750244140625, 0, "audio/x-m4a", `/ld.html/audio/101`, 0, 0.0, 510.020589569161, "youtube-wLvl-H8bpWo.3.m4a");
addaudio_from_url__queued(11, 23.489084243774414, 0, "audio/x-m4a", `/ld.html/audio/102`, 0, 0.0, 407.0199546485261, "youtube-wLvl-H8bpWo.4.m4a");
addaudio_from_url__queued(11, 16.792722702026367, 0, "audio/x-m4a", `/ld.html/audio/103`, 0, 0.0, 860.0206802721088, "youtube-wMpaqKyskX4.cut.m4a");
addaudio_from_url__queued(11, 21.04252052307129, 0, "audio/x-m4a", `/ld.html/audio/104`, 0, 0.0, 792.9150113378685, "youtube-JbclO4G0170.cut.m4a");

addaudio_from_url__queued(5, 43.03313064575195, 0, "audio/x-m4a", `/ld.html/audio/87`, 0, 0.0, 88.00362811791383, "youtube-XUece-2WmuM.m4a");

// Music:
addaudio_from_url__queued(9, 58.761837005615234, 0, "audio/mpeg", `/ld.html/audio/89`, 0, 0.0, 598.0614965986395, "Best German Songs 2014b.mp3");


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
