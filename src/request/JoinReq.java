package request;

import node.NodeData;

/**
 * Created by Oshada on 2020-02-05.
 */
public class JoinReq extends Request {

    NodeData joinNodeData;

    public JoinReq(NodeData joinNodeData) {
        this.joinNodeData = joinNodeData;
        this.type = "JOIN";
    }

    @Override
    public String getRequest() {
        return setMessageLength();
    }

    private String setMessageLength() {
        int lengthOfMessage;
        int lengthOfCompulsoryPart = joinNodeData.getIp().length() + joinNodeData.getRecvPort().length();
        if (lengthOfCompulsoryPart + 11 < 9999) {
            lengthOfMessage = lengthOfCompulsoryPart + 11;
        } else {
            lengthOfMessage = lengthOfCompulsoryPart + String.valueOf(lengthOfCompulsoryPart).length();
        }
        String message = String.valueOf(lengthOfMessage) + " JOIN " + joinNodeData.getIp() + " " + joinNodeData.getRecvPort();
        while (message.length() < lengthOfMessage) {
            message = "0" + message;
        }
        return message;
    }
}
