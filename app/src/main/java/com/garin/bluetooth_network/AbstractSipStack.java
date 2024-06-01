package com.garin.bluetooth_network;
import android.util.Log;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import io.github.cdimascio.dotenv.Dotenv;

public class AbstractSipStack {
    public void sendSipInvite() {
        new Thread(new Runnable() {
            private static final String TAG = "[AbstractSipStack]";
            @Override
            public void run() {
                String sipHost = getSipHost();

                String sipMessage = "INVITE sip:user@" + sipHost + " SIP/2.0\r\n"
                        + "Via: SIP/2.0/UDP " + sipHost + " ;branch=z9hG4bK776asdhds\r\n"
                        + "Max-Forwards: 70\r\n"
                        + "To: <sip:user@" + sipHost + ">\r\n"
                        + "From: <sip:user@" + sipHost + ">;tag=77477\r\n"
                        + "Call-ID: a84b4c76e66710\r\n"
                        + "CSeq: 314159 INVITE\r\n"
                        + "Contact: <sip:user@" + sipHost + ">\r\n"
                        + "Content-Type: application/sdp\r\n"
                        + "\r\n"
                        + "v=0\r\n"
                        + "o=user 53655765 2353687637 IN IP4 "+ sipHost + "\r\n"
                        + "s=-\r\n"
                        + "c=IN IP4 " + sipHost + "\r\n"
                        + "t=0 0\r\n"
                        + "m=audio 1234 RTP/AVP 0\r\n"
                        + "a=rtpmap:0 PCMU/8000\r\n";

                try {
                    DatagramSocket socket = new DatagramSocket();
                    InetAddress address = InetAddress.getByName(sipHost);
                    Log.i(TAG, "SIP host:");
                    Log.i(TAG, String.valueOf(address));
                    byte[] buffer = sipMessage.getBytes();
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, getSipPort());

                    // kamailio stress test
                    /*
                    int n = 1000;
                    for (int i = 1; i <= n; ++i) {
                        socket.send(packet);
                    }
                    */

                    socket.send(packet);
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    protected String getSipHost()
    {
        Dotenv dotenv = Dotenv.configure()
                .directory("/assets")
                .filename("env")
                .load();

        return dotenv.get("SIP_HOST");

    }

    protected int getSipPort()
    {
        Dotenv dotenv = Dotenv.configure()
                .directory("/assets")
                .filename("env")
                .load();

        return Integer.parseInt(dotenv.get("SIP_PORT"));
    }


}
