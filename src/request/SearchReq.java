package request;

import node.NodeData;

public class SearchReq {
    private String fileName;
    private NodeData nodeData;

    public SearchReq(String fileName, NodeData nodeData) {
        this.fileName = fileName;
        this.nodeData = nodeData;
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
}
