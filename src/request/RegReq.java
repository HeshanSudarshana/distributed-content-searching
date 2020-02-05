package request;


public class RegReq {
    private String IPAddress, port, uName;

    public RegReq(String IPAddress, String port, String uName) {
        this.IPAddress = IPAddress;
        this.port = port;
        this.uName = uName;
    }

    public String getRequest() {
        return setMessageLength();

    }

    private String setMessageLength() {
        int lengthOfMessage;
        int lengthOfCompulsoryPart = IPAddress.length() + port.length() + uName.length();
        if (lengthOfCompulsoryPart + 7 < 9999) {
            lengthOfMessage = lengthOfCompulsoryPart + 7;
        } else {
            lengthOfMessage = lengthOfCompulsoryPart + String.valueOf(lengthOfCompulsoryPart).length();
        }
        String message = String.valueOf(lengthOfMessage) + " " + IPAddress + " " + port + " " + uName;
        while (message.length() < lengthOfMessage) {
            message = "0" + message;
        }
        return message;
    }
}
