package com.devative.littledoor.ChatUi.liveStreaming;

import io.agora.rtc2.IRtcEngineEventHandler.LastmileProbeResult;
import io.agora.rtc2.IRtcEngineEventHandler.LocalAudioStats;
import io.agora.rtc2.IRtcEngineEventHandler.LocalVideoStats;
import io.agora.rtc2.IRtcEngineEventHandler.RemoteAudioStats;
import io.agora.rtc2.IRtcEngineEventHandler.RemoteVideoStats;
import io.agora.rtc2.IRtcEngineEventHandler.RtcStats;

public class StatisticsInfo {
    private LocalVideoStats localVideoStats = new LocalVideoStats();
    private LocalAudioStats localAudioStats = new LocalAudioStats();
    private RemoteVideoStats remoteVideoStats = new RemoteVideoStats();
    private RemoteAudioStats remoteAudioStats = new RemoteAudioStats();
    private RtcStats rtcStats = new RtcStats();
    private int quality;
    private LastmileProbeResult lastMileProbeResult;

    public void setLocalVideoStats(LocalVideoStats localVideoStats) {
        this.localVideoStats = localVideoStats;
    }

    public void setLocalAudioStats(LocalAudioStats localAudioStats) {
        this.localAudioStats = localAudioStats;
    }

    public void setRemoteVideoStats(RemoteVideoStats remoteVideoStats) {
        this.remoteVideoStats = remoteVideoStats;
    }

    public void setRemoteAudioStats(RemoteAudioStats remoteAudioStats) {
        this.remoteAudioStats = remoteAudioStats;
    }

    public void setRtcStats(RtcStats rtcStats) {
        this.rtcStats = rtcStats;
    }

    public String getLocalVideoStats() {
        StringBuilder builder = new StringBuilder();
        return "";
    }

    public String getRemoteVideoStats() {
        StringBuilder builder = new StringBuilder();
        return "";
    }

    public void setLastMileQuality(int quality) {
        this.quality = quality;
    }

    public String getLastMileQuality(){
        switch (quality){
            case 1:
                return "EXCELLENT";
            case 2:
                return "GOOD";
            case 3:
                return "POOR";
            case 4:
                return "BAD";
            case 5:
                return "VERY BAD";
            case 6:
                return "DOWN";
            case 7:
                return "UNSUPPORTED";
            case 8:
                return "DETECTING";
            default:
                return "UNKNOWN";
        }
    }

    public String getLastMileResult() {
        if(lastMileProbeResult == null)
            return null;
        String stringBuilder = "Rtt: " +
                lastMileProbeResult.rtt +
                "ms" +
                "\n" +
                "DownlinkAvailableBandwidth: " +
                lastMileProbeResult.downlinkReport.availableBandwidth +
                "Kbps" +
                "\n" +
                "DownlinkJitter: " +
                lastMileProbeResult.downlinkReport.jitter +
                "ms" +
                "\n" +
                "DownlinkLoss: " +
                lastMileProbeResult.downlinkReport.packetLossRate +
                "%" +
                "\n" +
                "UplinkAvailableBandwidth: " +
                lastMileProbeResult.uplinkReport.availableBandwidth +
                "Kbps" +
                "\n" +
                "UplinkJitter: " +
                lastMileProbeResult.uplinkReport.jitter +
                "ms" +
                "\n" +
                "UplinkLoss: " +
                lastMileProbeResult.uplinkReport.packetLossRate +
                "%";
        return stringBuilder;
    }

    public void setLastMileProbeResult(LastmileProbeResult lastmileProbeResult) {
        this.lastMileProbeResult = lastmileProbeResult;
    }

}
