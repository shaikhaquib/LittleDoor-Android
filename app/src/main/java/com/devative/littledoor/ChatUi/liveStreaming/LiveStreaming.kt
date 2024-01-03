package com.devative.littledoor.ChatUi.liveStreaming

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceView
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.devative.littledoor.R
import com.devative.littledoor.databinding.ActivityLiveStreamingBinding
import com.devative.littledoor.databinding.FragmentLiveStreamingSettingBinding
import com.devative.littledoor.databinding.FragmentLiveStreamingVideoTrackingBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import es.dmoral.toasty.Toasty
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.ClientRoleOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.Constants.MEDIA_TRACE_EVENT
import io.agora.rtc2.Constants.REMOTE_VIDEO_STATE_REASON_LOCAL_MUTED
import io.agora.rtc2.Constants.REMOTE_VIDEO_STATE_REASON_LOCAL_UNMUTED
import io.agora.rtc2.Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED
import io.agora.rtc2.Constants.REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED
import io.agora.rtc2.Constants.VideoSourceType
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.video.VideoEncoderConfiguration
import io.agora.rtc2.video.VideoEncoderConfiguration.AdvanceOptions
import io.agora.rtc2.video.VideoEncoderConfiguration.ENCODING_PREFERENCE
import io.agora.rtc2.video.WatermarkOptions
import java.io.File
import java.util.Locale

class LiveStreaming : AppCompatActivity(), View.OnClickListener {
    private val TAG = LiveStreaming::class.java.simpleName

    lateinit var mRootBinding: ActivityLiveStreamingBinding
    lateinit var mSettingBinding: FragmentLiveStreamingSettingBinding
    private var mSettingDialog: BottomSheetDialog? = null
    lateinit var handler: Handler

    private var localVideo: VideoReportLayout? = null
    private var remoteVideo: VideoReportLayout? = null
    private var isLocalVideoForeground = false

    private var engine: RtcEngine? = null
    private var myUid = 0
    private var remoteUid = 0
    private var joined = true
    private var isHost = false
    private val videoEncoderConfiguration = VideoEncoderConfiguration()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mRootBinding = ActivityLiveStreamingBinding.inflate(layoutInflater)
        setContentView(mRootBinding.root)
        localVideo = mRootBinding.foregroundLayout.localContainer
        remoteVideo = mRootBinding.remoteContainer
        //    mRootBinding.btnSetting.setOnClickListener(this)
        mRootBinding.btnJoin.setOnClickListener(this)
        mRootBinding.btnPublish.setOnClickListener(this)
        mRootBinding.btnRemoteScreenshot.setOnClickListener(this)
        localVideo!!.setOnClickListener(this)
        handler = Handler(Looper.getMainLooper())
        isHost = intent.getBooleanExtra("IS_HOST", false)
        if (isHost) {
            localVideo = mRootBinding.foregroundLayout.localContainer
            remoteVideo = mRootBinding.remoteContainer
        }
        mSettingBinding =
            FragmentLiveStreamingSettingBinding.inflate(LayoutInflater.from(applicationContext))
        mSettingBinding.switchWatermark.setOnCheckedChangeListener { buttonView, isChecked ->
            enableWatermark(
                isChecked
            )
        }
        mSettingBinding.switchBFrame.setOnCheckedChangeListener { buttonView, isChecked ->
            enableBFrame(
                isChecked
            )
        }
        mSettingBinding.switchLowLatency.setOnCheckedChangeListener { buttonView, isChecked ->
            enableLowLegacy(
                isChecked
            )
        }
        mSettingBinding.switchLowStream.setOnCheckedChangeListener { buttonView, isChecked ->
            enableLowStream(
                isChecked
            )
        }
        mSettingBinding.switchFirstFrame.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                AlertDialog.Builder(this).setTitle("tip")
                    .setMessage("first frame optimization")
                    .setNegativeButton("cancel") { dialog, which ->
                        buttonView.isChecked = false
                        dialog.dismiss()
                    }.setPositiveButton("confirm") { dialog, which ->
                        // Enable FirstFrame Optimization
                        engine!!.enableInstantMediaRendering()
                        buttonView.isEnabled = false
                        dialog.dismiss()
                    }.show()
            }
        }
        mSettingBinding.spEncoderType.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View, position: Int, id: Long
                ) {
                    setEncodingPreference(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        mSettingDialog = BottomSheetDialog(this)
        mSettingDialog?.setContentView(mSettingBinding.root)
        onActivityCreated()
        joinedChannel(intent.getStringExtra("CHANNEL_ID"))


        mRootBinding.chAudioMute.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            toggleAudioOutput(b)
        }
        mRootBinding.chMute.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            toggleMicrophoneMute(b)
        }
        mRootBinding.chVideoMute.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            toggleVideoInput(b)
        }
    }

    private fun joinedChannel(channelId: String?) {
        // Check permission
        val permissionArray = arrayListOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH_CONNECT
        )
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            permissionArray.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        Dexter.withContext(this)
            .withPermissions(
                permissionArray
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    // Check if all permissions are granted
                    if (report?.areAllPermissionsGranted() == true) {
                        channelId?.let { joinChannel(it) }
                    } else {
                        Toasty.error(applicationContext, "All permission required")
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                    // Show rationale for permission request if needed
                    token?.continuePermissionRequest()
                }
            })
            .check()

    }

    fun onActivityCreated() {
        // Check if the context is valid
        val context: Context = applicationContext ?: return
        try {

            /*
             * Creates an RtcEngine instance.
             * @param context The context of Android Activity
             * @param appId The App ID issued to you by Agora. See <a href="https://docs.agora.io/en/Agora%20Platform/token#get-an-app-id">
             *              How to get the App ID</a>
             * @param handler IRtcEngineEventHandler is an abstract class providing default implementation.
             *                The SDK uses this class to report to the app on SDK runtime events.*/
            val rtcEngineConfig = RtcEngineConfig()
            rtcEngineConfig.mAppId = getString(R.string.agora_app_id)
            rtcEngineConfig.mContext = context.applicationContext
            rtcEngineConfig.mEventHandler =
                iRtcEngineEventHandler/* Sets the channel profile of the Agora RtcEngine. */
            rtcEngineConfig.mChannelProfile =
                Constants.CHANNEL_PROFILE_LIVE_BROADCASTING
            rtcEngineConfig.mAudioScenario =
                Constants.AudioScenario.getValue(Constants.AudioScenario.DEFAULT)
            rtcEngineConfig.mAreaCode = getGlobalSettings().areaCode
            engine = RtcEngine.create(rtcEngineConfig)/*
             * This parameter is for reporting the usages of APIExample to agora background.
             * Generally, it is not necessary for you to set this parameter.
             */
            engine?.setParameters(
                "{" + "\"rtc.report_app_scenario\":" + "{" + "\"appScenario\":" + 100 + "," + "\"serviceType\":" + 11 + "," + "\"appVersion\":\"" + RtcEngine.getSdkVersion() + "\"" + "}" + "}"
            )/* setting the local access point if the private cloud ip was set, otherwise the config will be invalid.*/
            engine?.setLocalAccessPoint(
                getGlobalSettings().privateCloudConfig
            )
            engine?.setVideoEncoderConfiguration(videoEncoderConfiguration)
            engine?.enableDualStreamMode(true)

        } catch (e: Exception) {
            onBackPressed()
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /*leaveChannel and Destroy the RtcEngine instance*/
        if (engine != null) {
            engine?.leaveChannel()
        }
        handler.post { RtcEngine.destroy() }
        engine = null
    }

    override fun onClick(v: View) {
        if (v.id == R.id.btn_join) {
            joined = false
            isHost = false
            finish()
        } else if (v.id == R.id.btn_publish) {
            isHost = !isHost
            if (isHost) {
                engine!!.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
            } else {
                val clientRoleOptions = ClientRoleOptions()
                clientRoleOptions.audienceLatencyLevel =
                    if (mSettingBinding.switchLowLatency.isChecked) Constants.AUDIENCE_LATENCY_LEVEL_ULTRA_LOW_LATENCY else Constants.AUDIENCE_LATENCY_LEVEL_LOW_LATENCY
                engine!!.setClientRole(Constants.CLIENT_ROLE_AUDIENCE, clientRoleOptions)
            }
            mRootBinding.btnPublish.isEnabled = false
            mRootBinding.btnPublish.text =
                if (isHost) getString(R.string.disnable_publish) else getString(R.string.enable_publish)
        } else if (v.id == R.id.localContainer) {
            isLocalVideoForeground = !isLocalVideoForeground
            val foreGroundReportId = localVideo!!.reportUid
            localVideo!!.reportUid = remoteVideo!!.reportUid
            remoteVideo!!.reportUid = foreGroundReportId
            if (localVideo!!.childCount > 0) {
                localVideo!!.removeAllViews()
            }
            if (remoteVideo!!.childCount > 0) {
                remoteVideo!!.removeAllViews()
            }
            // Create render view by RtcEngine
            val localView = SurfaceView(applicationContext)
            val remoteView = SurfaceView(applicationContext)
            if (isLocalVideoForeground) {
                // Add to the local container
                localVideo!!.addView(
                    localView, FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                // Add to the remote container
                remoteVideo!!.addView(
                    remoteView, FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                // Setup remote video to render
                engine!!.setupRemoteVideo(
                    VideoCanvas(
                        remoteView, VideoCanvas.RENDER_MODE_HIDDEN, remoteUid
                    )
                )
                // Setup local video to render your local camera preview
                engine!!.setupLocalVideo(VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
                localView.setZOrderMediaOverlay(true)
                localView.setZOrderOnTop(true)
            } else {
                // Add to the local container
                localVideo!!.addView(
                    remoteView, FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                // Add to the remote container
                remoteVideo!!.addView(
                    localView, FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                // Setup local video to render your local camera preview
                engine!!.setupLocalVideo(VideoCanvas(localView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
                // Setup remote video to render
                engine!!.setupRemoteVideo(
                    VideoCanvas(
                        remoteView, VideoCanvas.RENDER_MODE_HIDDEN, remoteUid
                    )
                )
                remoteView.setZOrderMediaOverlay(true)
                remoteView.setZOrderOnTop(true)
            }
        } else if (v.id == R.id.btn_remote_screenshot) {
            takeSnapshot(remoteUid)
        }
    }

    private fun joinChannel(channelId: String) {
        // Check if the context is valid
        val context: Context = applicationContext ?: return
        isLocalVideoForeground = false
        // Create render view by RtcEngine
        val surfaceView = SurfaceView(context)
        if (remoteVideo!!.childCount > 0) {
            remoteVideo!!.removeAllViews()
        }
        // Add to the local container
        remoteVideo!!.addView(
            surfaceView, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        // Setup local video to render your local camera preview
        engine!!.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, 0))
        // Set audio route to microPhone
        engine!!.setDefaultAudioRoutetoSpeakerphone(true)
        engine!!.startPreview()
        // Set audio route to microPhone
        engine!!.setDefaultAudioRoutetoSpeakerphone(true)

        /*In the demo, the default is to enter as the anchor.*/
        if (isHost) {
            engine!!.setClientRole(Constants.CLIENT_ROLE_BROADCASTER)
        } else {
            engine!!.setClientRole(
                IRtcEngineEventHandler.ClientRole.CLIENT_ROLE_AUDIENCE
            )
        }
        // Enable video module
        engine!!.enableVideo()
        // Setup video encoding configs
        engine!!.setVideoEncoderConfiguration(
            VideoEncoderConfiguration(
                getGlobalSettings().videoEncodingDimensionObject,
                VideoEncoderConfiguration.FRAME_RATE.valueOf(
                    getGlobalSettings().videoEncodingFrameRate
                ),
                VideoEncoderConfiguration.STANDARD_BITRATE,
                VideoEncoderConfiguration.ORIENTATION_MODE.valueOf(
                    getGlobalSettings().videoEncodingOrientation
                )
            )
        )
        engine!!.startMediaRenderingTracing()

        /*
         * Please configure accessToken in the string_config file.
         * A temporary token generated in Console. A temporary token is valid for 24 hours. For details, see
         *      https://docs.agora.io/en/Agora%20Platform/token?platform=All%20Platforms#get-a-temporary-token
         * A token generated at the server. This applies to scenarios with high-security requirements. For details, see
         *      https://docs.agora.io/en/cloud-recording/token_server_java?platform=Java*/
        TokenUtils.gen(this, channelId, 0) { ret ->
            /* Allows a user to join a channel.
         if you do not specify the uid, we will generate the uid for you*/
            val option = ChannelMediaOptions()
            option.autoSubscribeAudio = true
            option.autoSubscribeVideo = true
            val res = engine!!.joinChannel(ret, channelId, 0, option)
            if (res != 0) {
                engine!!.stopPreview()
                // Usually happens with invalid parameters
                // Error code description can be found at:
                // en: https://docs.agora.io/en/Voice/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler_1_1_error_code.html
                // cn: https://docs.agora.io/cn/Voice/API%20Reference/java/classio_1_1agora_1_1rtc_1_1_i_rtc_engine_event_handler_1_1_error_code.html
                showAlert(RtcEngine.getErrorDescription(Math.abs(res)))
                return@gen
            }
            // Prevent repeated entry
            mRootBinding.btnJoin.isEnabled = false
        }
    }

    private fun enableWatermark(enable: Boolean) {
        if (enable) {
            val watermarkOptions = WatermarkOptions()
            val size: Int = getGlobalSettings()
                .videoEncodingDimensionObject.width / 6
            val height: Int = getGlobalSettings()
                .videoEncodingDimensionObject.height
            watermarkOptions.positionInPortraitMode =
                WatermarkOptions.Rectangle(10, height / 2, size, size)
            watermarkOptions.positionInLandscapeMode =
                WatermarkOptions.Rectangle(10, height / 2, size, size)
            watermarkOptions.visibleInPreview = true
            val ret = engine!!.addVideoWatermark(
                "https://www.zentechinfo.com/assets/images/heading-logo.svg",
                watermarkOptions
            )
            if (ret != Constants.ERR_OK) {
                Log.e(
                    TAG,
                    "addVideoWatermark error=" + ret + ", msg=" + RtcEngine.getErrorDescription(ret)
                )
            }
        } else {
            engine!!.clearVideoWatermarks()
        }
    }

    private fun enableLowStream(enable: Boolean) {
        engine!!.setRemoteDefaultVideoStreamType(if (enable) Constants.VIDEO_STREAM_LOW else Constants.VIDEO_STREAM_HIGH)
        if (remoteUid != 0) {
            engine!!.setRemoteVideoStreamType(
                remoteUid, if (enable) Constants.VIDEO_STREAM_LOW else Constants.VIDEO_STREAM_HIGH
            )
        }
    }

    private fun setEncodingPreference(index: Int) {
        val preferences = arrayOf(
            ENCODING_PREFERENCE.PREFER_AUTO,
            ENCODING_PREFERENCE.PREFER_HARDWARE,
            ENCODING_PREFERENCE.PREFER_SOFTWARE
        )
        val advanceOptions = AdvanceOptions()
        advanceOptions.encodingPreference = preferences[index]
        videoEncoderConfiguration.advanceOptions = advanceOptions
        engine!!.setVideoEncoderConfiguration(videoEncoderConfiguration)
    }

    private fun enableBFrame(enable: Boolean) {
        videoEncoderConfiguration.advanceOptions.compressionPreference =
            if (enable) VideoEncoderConfiguration.COMPRESSION_PREFERENCE.PREFER_QUALITY else VideoEncoderConfiguration.COMPRESSION_PREFERENCE.PREFER_LOW_LATENCY
        engine!!.setVideoEncoderConfiguration(videoEncoderConfiguration)
    }

    private fun enableLowLegacy(enable: Boolean) {
        if (isHost) {
            return
        }
        val clientRoleOptions = ClientRoleOptions()
        clientRoleOptions.audienceLatencyLevel =
            if (enable) Constants.AUDIENCE_LATENCY_LEVEL_ULTRA_LOW_LATENCY else Constants.AUDIENCE_LATENCY_LEVEL_LOW_LATENCY
        engine!!.setClientRole(Constants.CLIENT_ROLE_AUDIENCE, clientRoleOptions)
    }

    private fun takeSnapshot(uid: Int) {
        if (uid != 0) {
            val filePath: String = this.externalCacheDir
                ?.absolutePath + File.separator + "livestreaming_snapshot.png"
            val ret = engine!!.takeSnapshot(uid, filePath)
            if (ret != Constants.ERR_OK) {

            }
        } else {
        }
    }


    /**
     * IRtcEngineEventHandler is an abstract class providing default implementation.
     * The SDK uses this class to report to the app on SDK runtime events.
     */
    private val iRtcEngineEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        /**
         * Error code description can be found at:
         * en: https://api-ref.agora.io/en/video-sdk/android/4.x/API/class_irtcengineeventhandler.html#callback_irtcengineeventhandler_onerror
         * cn: https://docs.agora.io/cn/video-call-4.x/API%20Reference/java_ng/API/class_irtcengineeventhandler.html#callback_irtcengineeventhandler_onerror
         */
        override fun onError(err: Int) {
            Log.e(
                TAG,
                String.format("onError code %d message %s", err, RtcEngine.getErrorDescription(err))
            )
            showAlert(
                "onError code %d message %s  $err, ${RtcEngine.getErrorDescription(err)}"
            )
        }

        /**Occurs when a user leaves the channel.
         * @param stats With this callback, the application retrieves the channel information,
         * such as the call duration and statistics.
         */
        override fun onLeaveChannel(stats: RtcStats) {
            super.onLeaveChannel(stats)
            Log.i(TAG, String.format("local user %d leaveChannel!", myUid))
        }

        /**Occurs when the local user joins a specified channel.
         * The channel name assignment is based on channelName specified in the joinChannel method.
         * If the uid is not specified when joinChannel is called, the server automatically assigns a uid.
         * @param channel Channel name
         * @param uid User ID
         * @param elapsed Time elapsed (ms) from the user calling joinChannel until this callback is triggered
         */
        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            Log.i(
                TAG, String.format("onJoinChannelSuccess channel %s uid %d", channel, uid)
            )
            myUid = uid
            joined = true
            handler.post(Runnable {
                mRootBinding.btnJoin.isEnabled = true
                // mRootBinding.btnJoin.text = getString(R.string.leave)
                mRootBinding.btnPublish.isEnabled = true
                localVideo!!.reportUid = uid
            })
        }

        /**Since v2.9.0.
         * Occurs when the remote video state changes.
         * PS: This callback does not work properly when the number of users (in the Communication
         * profile) or broadcasters (in the Live-broadcast profile) in the channel exceeds 17.
         * @param uid ID of the remote user whose video state changes.
         * @param state State of the remote video:
         * REMOTE_VIDEO_STATE_STOPPED(0): The remote video is in the default state, probably due
         * to REMOTE_VIDEO_STATE_REASON_LOCAL_MUTED(3), REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED(5),
         * or REMOTE_VIDEO_STATE_REASON_REMOTE_OFFLINE(7).
         * REMOTE_VIDEO_STATE_STARTING(1): The first remote video packet is received.
         * REMOTE_VIDEO_STATE_DECODING(2): The remote video stream is decoded and plays normally,
         * probably due to REMOTE_VIDEO_STATE_REASON_NETWORK_RECOVERY (2),
         * REMOTE_VIDEO_STATE_REASON_LOCAL_UNMUTED(4), REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED(6),
         * or REMOTE_VIDEO_STATE_REASON_AUDIO_FALLBACK_RECOVERY(9).
         * REMOTE_VIDEO_STATE_FROZEN(3): The remote video is frozen, probably due to
         * REMOTE_VIDEO_STATE_REASON_NETWORK_CONGESTION(1) or REMOTE_VIDEO_STATE_REASON_AUDIO_FALLBACK(8).
         * REMOTE_VIDEO_STATE_FAILED(4): The remote video fails to start, probably due to
         * REMOTE_VIDEO_STATE_REASON_INTERNAL(0).
         * @param reason The reason of the remote video state change:
         * REMOTE_VIDEO_STATE_REASON_INTERNAL(0): Internal reasons.
         * REMOTE_VIDEO_STATE_REASON_NETWORK_CONGESTION(1): Network congestion.
         * REMOTE_VIDEO_STATE_REASON_NETWORK_RECOVERY(2): Network recovery.
         * REMOTE_VIDEO_STATE_REASON_LOCAL_MUTED(3): The local user stops receiving the remote
         * video stream or disables the video module.
         * REMOTE_VIDEO_STATE_REASON_LOCAL_UNMUTED(4): The local user resumes receiving the remote
         * video stream or enables the video module.
         * REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED(5): The remote user stops sending the video
         * stream or disables the video module.
         * REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED(6): The remote user resumes sending the video
         * stream or enables the video module.
         * REMOTE_VIDEO_STATE_REASON_REMOTE_OFFLINE(7): The remote user leaves the channel.
         * REMOTE_VIDEO_STATE_REASON_AUDIO_FALLBACK(8): The remote media stream falls back to the
         * audio-only stream due to poor network conditions.
         * REMOTE_VIDEO_STATE_REASON_AUDIO_FALLBACK_RECOVERY(9): The remote media stream switches
         * back to the video stream after the network conditions improve.
         * @param elapsed Time elapsed (ms) from the local user calling the joinChannel method until
         * the SDK triggers this callback.
         */
        override fun onRemoteVideoStateChanged(uid: Int, state: Int, reason: Int, elapsed: Int) {
            super.onRemoteVideoStateChanged(uid, state, reason, elapsed)
            /*Log.i(
                TAG, "onRemoteVideoStateChanged->$uid, state->$state, reason->$reason"
            )*/
            if(reason == REMOTE_VIDEO_STATE_REASON_REMOTE_MUTED){
                Handler(Looper.getMainLooper()).postDelayed({
                    localVideo?.performClick()
                    mRootBinding.foregroundLayout.container.visibility == View.GONE
                }, 100)
                showLongToast("Remote User has muted Video")
            }else if (reason == REMOTE_VIDEO_STATE_REASON_REMOTE_UNMUTED){
                Handler(Looper.getMainLooper()).postDelayed({
                    localVideo?.performClick()
                    mRootBinding.foregroundLayout.container.visibility = View.VISIBLE
                }, 100)

            }
        }

        /**Occurs when a remote user (Communication)/host (Live Broadcast) joins the channel.
         * @param uid ID of the user whose audio state changes.
         * @param elapsed Time delay (ms) from the local user calling joinChannel/setClientRole
         * until this callback is triggered.
         */
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            Log.i(TAG, "onUserJoined->$uid")
            showLongToast("Remote User Joined")
            /**Check if the context is correct */
            /**Check if the context is correct */
            val context: Context = applicationContext ?: return
            remoteUid = if (remoteUid != 0) {
                return
            } else {
                uid
            }
            handler.post {
                val videoContainer =
                    if (isLocalVideoForeground) remoteVideo!! else localVideo!!

                /**Display remote video stream */
                var surfaceView: SurfaceView? = null
                if (videoContainer.childCount > 0) {
                    videoContainer.removeAllViews()
                }
                // Create render view by RtcEngine
                surfaceView = SurfaceView(context)
                surfaceView.setZOrderMediaOverlay(true)
                // Add to the remote container
                videoContainer.addView(
                    surfaceView, FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
                    )
                )
                videoContainer.reportUid = remoteUid
                // Setup remote video to render
                engine!!.setupRemoteVideo(
                    VideoCanvas(
                        surfaceView, VideoCanvas.RENDER_MODE_HIDDEN, remoteUid
                    )
                )
            }
            Handler(Looper.getMainLooper()).postDelayed({
                mRootBinding.foregroundLayout.container.visibility = View.VISIBLE
                localVideo?.performClick()
            }, 100)

        }

        /**Occurs when a remote user (Communication)/host (Live Broadcast) leaves the channel.
         * @param uid ID of the user whose audio state changes.
         * @param reason Reason why the user goes offline:
         * USER_OFFLINE_QUIT(0): The user left the current channel.
         * USER_OFFLINE_DROPPED(1): The SDK timed out and the user dropped offline because no data
         * packet was received within a certain period of time. If a user quits the
         * call and the message is not passed to the SDK (due to an unreliable channel),
         * the SDK assumes the user dropped offline.
         * USER_OFFLINE_BECOME_AUDIENCE(2): (Live broadcast only.) The client role switched from
         * the host to the audience.
         */
        override fun onUserOffline(uid: Int, reason: Int) {
            Log.i(TAG, String.format("user %d offline! reason:%d", uid, reason))
            showLongToast("user offline")
            if (uid == remoteUid) {
                remoteUid = 0
                runOnUiThread(Runnable {
                    /**Clear render view
                     * Note: The video will stay at its last frame, to completely remove it you will need to
                     * remove the SurfaceView from its parent */
                    /**Clear render view
                     * Note: The video will stay at its last frame, to completely remove it you will need to
                     * remove the SurfaceView from its parent */
                    /**Clear render view
                     * Note: The video will stay at its last frame, to completely remove it you will need to
                     * remove the SurfaceView from its parent */

                    /**Clear render view
                     * Note: The video will stay at its last frame, to completely remove it you will need to
                     * remove the SurfaceView from its parent */
                    engine!!.setupRemoteVideo(
                        VideoCanvas(
                            null, VideoCanvas.RENDER_MODE_HIDDEN, uid
                        )
                    )
                    Handler(Looper.getMainLooper()).postDelayed({
                        localVideo?.performClick()
                        mRootBinding.foregroundLayout.container.visibility = View.GONE
                    }, 100)
                })
            }
        }

        /**
         * Occurs when the user role switches in a live streaming. For example, from a host to an audience or vice versa.
         *
         * The SDK triggers this callback when the local user switches the user role by calling the setClientRole method after joining the channel.
         * @param oldRole Role that the user switches from.
         * @param newRole Role that the user switches to.
         */
        override fun onClientRoleChanged(
            oldRole: Int, newRole: Int, newRoleOptions: ClientRoleOptions
        ) {
            super.onClientRoleChanged(oldRole, newRole, newRoleOptions)
            Log.i(
                TAG, String.format("client role changed from state %d to %d", oldRole, newRole)
            )
            runOnUiThread {
                mRootBinding.btnPublish.isEnabled = true
            }
        }

        override fun onSnapshotTaken(
            uid: Int, filePath: String, width: Int, height: Int, errCode: Int
        ) {
            super.onSnapshotTaken(uid, filePath, width, height, errCode)
            Log.d(
                TAG, String.format(
                    Locale.US,
                    "onSnapshotTaken uid=%d, filePath=%s, width=%d, height=%d, errorCode=%d",
                    uid,
                    filePath,
                    width,
                    height,
                    errCode
                )
            )
            if (errCode == 0) {
            } else {
            }
        }

        override fun onLocalVideoStats(source: VideoSourceType, stats: LocalVideoStats) {
            super.onLocalVideoStats(source, stats)
            if (isLocalVideoForeground) {
                localVideo!!.setLocalVideoStats(stats)
            } else {
                remoteVideo!!.setLocalVideoStats(stats)
            }
        }

        override fun onLocalAudioStats(stats: LocalAudioStats) {
            super.onLocalAudioStats(stats)
            if (isLocalVideoForeground) {
                localVideo!!.setLocalAudioStats(stats)
            } else {
                remoteVideo!!.setLocalAudioStats(stats)
            }
        }

        override fun onRemoteVideoStats(stats: RemoteVideoStats) {
            super.onRemoteVideoStats(stats)
            if (!isLocalVideoForeground) {
                localVideo!!.setRemoteVideoStats(stats)
            } else {
                remoteVideo!!.setRemoteVideoStats(stats)
            }
        }

        override fun onRemoteAudioStats(stats: RemoteAudioStats) {
            super.onRemoteAudioStats(stats)
            if (!isLocalVideoForeground) {
                localVideo!!.setRemoteAudioStats(stats)
            } else {
                remoteVideo!!.setRemoteAudioStats(stats)
            }
        }

        override fun onVideoRenderingTracingResult(
            uid: Int, currentEvent: MEDIA_TRACE_EVENT, tracingInfo: VideoRenderingTracingInfo
        ) {
            super.onVideoRenderingTracingResult(uid, currentEvent, tracingInfo)
            runOnUiThread {
                val videoTrackingLayout: FragmentLiveStreamingVideoTrackingBinding =
                    mRootBinding.videoTrackingLayout
                videoTrackingLayout.root.visibility = View.VISIBLE
                videoTrackingLayout.tvUid.text = uid.toString()
                videoTrackingLayout.tvEvent.text = currentEvent.value.toString()
                videoTrackingLayout.tvElapsedTime.text = String.format(
                    Locale.US, "%d ms", tracingInfo.elapsedTime
                )
                videoTrackingLayout.tvStart2JoinChannel.text = String.format(
                    Locale.US, "%d ms", tracingInfo.start2JoinChannel
                )
                videoTrackingLayout.tvJoin2JoinSuccess.text = String.format(
                    Locale.US, "%d ms", tracingInfo.join2JoinSuccess
                )
                videoTrackingLayout.tvJoinSuccess2RemoteJoined.text = String.format(
                    Locale.US, "%d ms", tracingInfo.joinSuccess2RemoteJoined
                )
                videoTrackingLayout.tvRemoteJoined2SetView.text = String.format(
                    Locale.US, "%d ms", tracingInfo.remoteJoined2SetView
                )
                videoTrackingLayout.tvRemoteJoined2UnmuteVideo.text = String.format(
                    Locale.US, "%d ms", tracingInfo.remoteJoined2UnmuteVideo
                )
                videoTrackingLayout.tvRemoteJoined2PacketReceived.text = String.format(
                    Locale.US, "%d ms", tracingInfo.remoteJoined2PacketReceived
                )
            }
        }
    }

    fun showLongToast(msg: String?) {
        runOnUiThread(Runnable {
            Toast.makeText(applicationContext, msg, Toast.LENGTH_LONG).show()
        })
    }

    fun getGlobalSettings(): GlobalSettings {
        return GlobalSettings()
    }

    fun showAlert(message: String) {
        runOnUiThread {
            val context: Context = this
            val mAlertDialog = AlertDialog.Builder(context).setTitle("Tips").setPositiveButton(
                "OK"
            ) { dialog: DialogInterface, which: Int -> dialog.dismiss() }.create()

            mAlertDialog.setMessage(message)
            mAlertDialog.show()
        }
    }

    fun toggleAudioOutput(mute: Boolean) {
        engine?.setEnableSpeakerphone(mute)
    }

    fun toggleMicrophoneMute(mute: Boolean) {
        engine?.muteLocalAudioStream(mute)
    }

    fun toggleVideoInput(mute: Boolean) {
        engine?.muteLocalVideoStream(mute)
    }
}
