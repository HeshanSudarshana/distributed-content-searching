package request;

import node.NodeData;

public class SearchReq extends Request {
    private String fileName;
    //NodeData of the file requesting Node
    private NodeData nodeData;
    private int hopCount;

    public SearchReq(String fileName, NodeData nodeData, int hopCount) {
        this.fileName = "\"" + fileName + "\"";
        this.nodeData = nodeData;
        this.hopCount = hopCount;
        this.type = "SER";
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public NodeData getNodeData() {
        return nodeData;
    }

    public void setNodeData(NodeData nodeData) {
        this.nodeData = nodeData;
    }

    @Override
    public String getRequest() {
        return setMessageLength();
    }


    private String setMessageLength() {
        int lengthOfMessage;
        int lengthOfCompulsoryPart = nodeData.getIp().length() + nodeData.getRecvPort().length() + fileName.length() +
                String.valueOf(hopCount).length() + 3;
        if (lengthOfCompulsoryPart + 9 < 9999) {
            lengthOfMessage = lengthOfCompulsoryPart + 9;
        } else {
            lengthOfMessage = lengthOfCompulsoryPart + String.valueOf(lengthOfCompulsoryPart).length();
        }
        String message = String.valueOf(lengthOfMessage) + " SER " + nodeData.getIp() + " " +
                nodeData.getRecvPort() + " " + fileName + " " + String.valueOf(hopCount);
        while (message.length() < lengthOfMessage) {
            message = "0" + message;
        }
        return message;
    }
}
