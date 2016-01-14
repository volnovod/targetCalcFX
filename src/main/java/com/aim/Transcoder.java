package com.aim;

import com.sun.jna.NativeLibrary;
import sun.plugin2.util.NativeLibLoader;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import java.io.IOException;

/**
 * Created by root on 10.09.15.
 */
public class Transcoder {

    public static void main(String[] args) throws IOException {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcName(), "/usr/lib/vlc");

        MediaPlayerFactory factory = new MediaPlayerFactory("-vvv", "--sout-mux-caching=50000");
        MediaPlayer player = factory.newHeadlessMediaPlayer();
        String mrl="rtsp://192.168.2.130:8000/live";
        String options=":sout=#transcode{vcodec=h264,venc=x264{cfr=16},scale=1,acodec=mp4a,ab=160,channels=2,samplerate=44100}"
                + ":rtp{dst=127.0.0.1,port=10010,mux=ts}";

        player.playMedia(mrl, options);
        System.in.read();
    }
}
