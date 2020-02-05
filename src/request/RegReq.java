package request;


public class RegReq extends Request {
    private String IPAddress, port, uName;

    public RegReq(String IPAddress, String port, String uName) {
        this.IPAddress = IPAddress;
        this.port = port;
        this.uName = uName;
        this.type = "REG";
    }

    public String getRequest() {
        return setMessageLength();

    }

    private String setMessageLength() {
        int lengthOfMessage;
        int lengthOfCompulsoryPart = IPAddress.length() + port.length() + uName.length();
        if (lengthOfCompulsoryPart + 11 < 9999) {
            lengthOfMessage = lengthOfCompulsoryPart + 11;
        } else {
            lengthOfMessage = lengthOfCompulsoryPart + String.valueOf(lengthOfCompulsoryPart).length();
        }
        String message = String.valueOf(lengthOfMessage) + " REG " + IPAddress + " " + port + " " + uName;
        while (message.length() < lengthOfMessage) {
            message = "0" + message;
        }
        return message;
    }


}
