package request;

import node.NodeData;

/**
 * Created by Oshada on 2020-02-06.
 */
public class LeaveReq extends Request {

    NodeData nodeData;

    public LeaveReq(NodeData nodeData) {
        this.nodeData = nodeData;
        this.type = "LEAVE";
    }

    @Override
    public String getRequest() {
        return setMessageLength();
    }

    private String setMessageLength() {
        int lengthOfMessage;
        int lengthOfCompulsoryPart = nodeData.getIp().length() + nodeData.getRecvPort().length();
        if (lengthOfCompulsoryPart + 12 < 9999) {
            lengthOfMessage = lengthOfCompulsoryPart + 12;
        } else {
            lengthOfMessage = lengthOfCompulsoryPart + String.valueOf(lengthOfCompulsoryPart).length();
        }
        String message = String.valueOf(lengthOfMessage) + " LEAVE " + nodeData.getIp() + " " + nodeData.getRecvPort();
        while (message.length() < lengthOfMessage) {
            message = "0" + message;
        }
        return message;
    }
}

