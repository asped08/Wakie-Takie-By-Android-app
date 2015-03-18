package audiocast.ui;

import android.media.AudioTrack;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;


import java.net.UnknownHostException;
import java.util.concurrent.BlockingQueue;


/**
 * @author (C) Supun Athukorala (E/11/024) / Irunika Weerarathne (E/11/431)
 *
 */
public final class Server extends Thread {


private static final int MAXLEN = 1024;
final BlockingQueue<byte[]> RecBuf;
final BlockingQueue<byte[]> SendBuf;
private static InetAddress group = null;
public static boolean Broadcast = false;
public static boolean ReciveSound = false;

MulticastSocket Sockt = null;
MulticastSocket Sockt2 = null;


public Server(BlockingQueue<byte[]> RecQueue, BlockingQueue<byte[]> SndQueue) {

this.SendBuf = SndQueue;
this.RecBuf = RecQueue;


try {
    group = InetAddress.getByName("238.0.0.1");
    Sockt = new MulticastSocket(8963);
    Sockt.joinGroup(group);
    Sockt2 = new MulticastSocket(8963);
    Sockt2.joinGroup(group);
} catch (UnknownHostException e) {
    e.printStackTrace();
} catch (IOException e) {
    e.printStackTrace();
}


}

public void run() {

DatagramPacket hi;
DatagramPacket recv;
Log.i("Audiocast", "Starting Sending/Receiving threads");

try {
while (!Thread.interrupted()) {


        if (Broadcast) {
            byte[] pkt = SendBuf.take();

            hi = new DatagramPacket(pkt, pkt.length, group, 8963);

            Sockt.send(hi);

            Log.d("Audiocast", "send " + pkt.length + " bytes");
        }

        if (ReciveSound) {
            byte[] buf = new byte[MAXLEN];
            recv = new DatagramPacket(buf, buf.length);

            Sockt2.receive(recv);
            RecBuf.put(buf);
            Log.d("Audiocast", "recived " + buf.length + " bytes");
        }
    }
    } catch (InterruptedException ignored) {
    } catch (IOException e) {
        e.printStackTrace();
    }
}



}


