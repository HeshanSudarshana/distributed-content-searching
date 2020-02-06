package request;

public class UnregReq extends Request {

    private String IPAddress, port, uName;


    public UnregReq(String IPAddress, String port, String uName) {
        this.IPAddress = IPAddress;
        this.port = port;
        this.uName = uName;
        this.type = "UNREG";
    }

    @Override
    public String getRequest() {
        return setMessageLength();
    }

    private String setMessageLength() {
        int lengthOfMessage;
        int lengthOfCompulsoryPart = IPAddress.length() + port.length() + uName.length();
        if (lengthOfCompulsoryPart + 13 < 9999) {
            lengthOfMessage = lengthOfCompulsoryPart + 13;
        } else {
            lengthOfMessage = lengthOfCompulsoryPart + String.valueOf(lengthOfCompulsoryPart).length();
        }
        String message = String.valueOf(lengthOfMessage) + " UNREG " + IPAddress + " " + port + " " + uName;
        while (message.length() < lengthOfMessage) {
            message = "0" + message;
        }
        return message;
    }


}
