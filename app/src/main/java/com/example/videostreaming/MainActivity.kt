package com.example.videostreaming

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import kotlin.concurrent.fixedRateTimer

class MainActivity : AppCompatActivity(), MediaPlayer.OnCompletionListener,
    MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener, SeekBar.OnSeekBarChangeListener,
    View.OnClickListener {

    private val HLS_STREAMING_SAMPLE = "https://cdn.flowplayer.com/a30bd6bc-f98b-47bc-abf5-97633d4faea0/hls/de3f6ca7-2db3-4689-8160-0f574a5996ad/playlist.m3u8"
    private var sampleVideoView: VideoView? = null
    private var seekBar: SeekBar? = null
    private var playPauseButton: ImageView? = null
    private var stopButton: ImageView? = null
    private var runningTime: TextView? = null
    private var currentPosition: Int = 0
    private var isRunning = false

    //Always device to run this App
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sampleVideoView = findViewById<VideoView>(R.id.videoView)
        sampleVideoView?.setVideoURI(Uri.parse(HLS_STREAMING_SAMPLE))

        playPauseButton = findViewById<ImageView>(R.id.playPauseButton)
        playPauseButton?.setOnClickListener(this)

        stopButton = findViewById<ImageView>(R.id.stopButton)
        stopButton?.setOnClickListener(this)

        seekBar = findViewById<SeekBar>(R.id.seekBar)
        seekBar?.setOnSeekBarChangeListener(this)

        runningTime = findViewById<TextView>(R.id.runningTime)
        runningTime?.setText("00:00")

        Toast.makeText(this, "Buffering...Please wait", Toast.LENGTH_LONG).show()

        //Add the listeners
        sampleVideoView?.setOnCompletionListener(this)
        sampleVideoView?.setOnErrorListener(this)
        sampleVideoView?.setOnPreparedListener(this)
    }

    override fun onCompletion(mp: MediaPlayer?) {
        Toast.makeText(baseContext, "Play finished", Toast.LENGTH_LONG).show()
    }

    override fun onError(mp: MediaPlayer?, what: Int, extra: Int): Boolean {
        Log.e("video", "setOnErrorListener ")
        return true
    }

    override fun onPrepared(mp: MediaPlayer?) {
        seekBar?.setMax(sampleVideoView?.getDuration()!!)
        sampleVideoView?.start()

        val fixedRateTimer = fixedRateTimer(name = "hello-timer",
            initialDelay = 0, period = 1000) {
            refreshSeek()
        }

        playPauseButton?.setImageResource(R.mipmap.pause_button)
    }

    fun refreshSeek() {
        seekBar?.setProgress(sampleVideoView?.getCurrentPosition()!!);

        if (sampleVideoView?.isPlaying()!! == false) {
            return
        }

        var time = sampleVideoView?.getCurrentPosition()!! / 1000;
        var minute = time / 60;
        var second = time % 60;

        runOnUiThread {
            runningTime?.setText(minute.toString() + ":" + second.toString());
        }
    }

    var refreshTime = Runnable() {
        fun run() {

        }
    };

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        //do nothing
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        //do nothing
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        sampleVideoView?.seekTo(seekBar?.getProgress()!!)
    }

    override fun onClick(v: View?) {
        if (v?.getId() == R.id.playPauseButton) {
            //Play video
            if (!isRunning) {
                isRunning = true
                sampleVideoView?.resume()
                sampleVideoView?.seekTo(currentPosition)
                playPauseButton?.setImageResource(R.mipmap.pause_button)
            } else { //Pause video
                isRunning = false
                sampleVideoView?.pause()
                currentPosition = sampleVideoView?.getCurrentPosition()!!
                playPauseButton?.setImageResource(R.mipmap.play_button)
            }
        } else if (v?.getId() == R.id.stopButton) {
            playPauseButton?.setImageResource(R.mipmap.play_button)
            sampleVideoView?.stopPlayback()
            currentPosition = 0
        }
    }
}