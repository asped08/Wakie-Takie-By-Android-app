package audiocast.ui;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ToggleButton;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import audiocast.audio.Play;
import audiocast.audio.Record;
import co324.audiocast.R;

/**
* @author (C) Supun Athukorala (E/11/024) / Irunika Weerarathne (E/11/431)
 *
*/
public class AudiocastActivity extends Activity {
final static int SAMPLE_HZ = 11025, BACKLOG = 8;

Record rec;
Play play;
Server Servr;

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_audiocast);

    WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    if (wifi != null) {
        WifiManager.MulticastLock lock =
                wifi.createMulticastLock("Audiocast");
        lock.setReferenceCounted(true);
        lock.acquire();
    } else {
        Log.e("Audiocast", "Unable to acquire multicast lock");
        finish();
    }
}

@Override
protected void onStart() {
    super.onStart();

    BlockingQueue<byte[]> SendBuf = new ArrayBlockingQueue<byte[]>(BACKLOG);
    BlockingQueue<byte[]> RecvBuf = new ArrayBlockingQueue<byte[]>(BACKLOG);
    rec = new Record(SAMPLE_HZ, SendBuf);
    play = new Play(SAMPLE_HZ, RecvBuf);
    Servr = new Server(RecvBuf,SendBuf);


    findViewById(R.id.Record).setOnClickListener(new OnClickListener() {
        @Override
        public void onClick(View v) {

            boolean on=((ToggleButton) v).isChecked();
            rec.pause(!on);
            Server.Broadcast = on;

            Server.ReciveSound = !on;
            play.pause(on);

        }
    });


    Log.i("Audiocast", "Starting recording/playback threads");
    rec.start();
    play.start();
    Servr.start(); //Server thread started
}

@Override
protected void onStop() {
    super.onStop();

    Log.i("Audiocast", "Stopping recording/playback threads");
    rec.interrupt();
    play.interrupt();
    Servr.interrupt();
}
}
