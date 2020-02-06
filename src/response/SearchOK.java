package response;

import node.NodeData;
import utils.DFile;

import java.util.ArrayList;

public class SearchOK extends Response {
    ArrayList<DFile> matchingFiles;
    NodeData currentNode;
    int hopCount;

    public SearchOK(ArrayList<DFile> matchingFiles, NodeData currentNode, int hopCount) {
        this.matchingFiles = matchingFiles;
        this.currentNode = currentNode;
        this.hopCount = hopCount;
        this.type = "SEARCHOK";
    }

    private String buildMessage() {
        String message = "SEROK " + matchingFiles.size() + " " + currentNode.getIp() + " " + currentNode.getRecvPort() + " " +
                String.valueOf(hopCount) + " ";
        for (DFile file : matchingFiles) {
            message = message + file.getFileName() + " ";
        }
        return message.trim();
    }

    public String getResponse() {
        return setMessageLength();
    }

    private String setMessageLength() {
        String message = buildMessage();
        int lengthOfMessage;
        int lengthOfCompulsoryPart = message.length();
        if (lengthOfCompulsoryPart + 5 < 9999) {
            lengthOfMessage = lengthOfCompulsoryPart + 5;
        } else {
            lengthOfMessage = lengthOfCompulsoryPart + String.valueOf(lengthOfCompulsoryPart).length();
        }
        message = String.valueOf(lengthOfMessage) + " " + message;
        while (message.length() < lengthOfMessage) {
            message = "0" + message;
        }
        return message;
    }


}
