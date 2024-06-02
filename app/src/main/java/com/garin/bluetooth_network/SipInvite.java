package com.garin.bluetooth_network;

public class SipInvite extends AbstractSipStack {
    public void run() {
        this.sipSend(getSipData());
    }

    protected String getSipData()
    {
        String sipHost = getSipHost();

        return "INVITE sip:user@" + sipHost + " SIP/2.0\r\n"
                + "Via: SIP/2.0/UDP " + sipHost + " ;branch=z9hG4bK776asdhds\r\n"
                + "Max-Forwards: 70\r\n"
                + "To: <sip:user@" + sipHost + ">\r\n"
                + "From: <sip:user@" + sipHost + ">;tag=77477\r\n"
                + "Call-ID: a84b4c76e66710\r\n"
                + "CSeq: 1 INVITE\r\n"
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
    }
}
