package com.garin.bluetooth_network;

public class SipMessage extends AbstractSipStack {
    public void send(String message) {
        this.sipSend(getSipData(message));
    }

    protected String getSipData(String message)
    {
        String sipHost = getSipHost();

        return "MESSAGE sip:user@" + sipHost + " SIP/2.0\r\n"
                + "Via: SIP/2.0/UDP " + sipHost + " ;branch=z9hG4bK776asdhds\r\n"
                + "Max-Forwards: 70\r\n"
                + "To: <sip:user@" + sipHost + ">\r\n"
                + "From: <sip:user@" + sipHost + ">;tag=77477\r\n"
                + "Call-ID: a84b4c76e66710\r\n"
                + "CSeq: 1 MESSAGE\r\n"
                + "Content-Type: text/plain\r\n"
                + "Content-Length: 5\r\n"
                + message;
    }
}
