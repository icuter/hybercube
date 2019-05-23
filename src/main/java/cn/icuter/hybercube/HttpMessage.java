package cn.icuter.hybercube;

/**
 * @author edward
 * @since 2018-10-25
 */
public class HttpMessage {

    enum MessageType {
        START_LINE,
        HEADER,
        BODY,
    }

    private MessageType currentMessageType = MessageType.START_LINE;
    private MessageType nextMessageType;
    private byte[] startLine;
    private byte[] header;
    private byte[] body;
    private long contentLength;
    private boolean completed;

    public long getContentLength() {
        return contentLength;
    }

    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public MessageType getCurrentMessageType() {
        return currentMessageType;
    }

    public void setCurrentMessageType(MessageType currentMessageType) {
        this.currentMessageType = currentMessageType;
    }

    public MessageType getNextMessageType() {
        return nextMessageType;
    }

    public void setNextMessageType(MessageType nextMessageType) {
        this.nextMessageType = nextMessageType;
    }

    public byte[] getStartLine() {
        return startLine;
    }

    public void setStartLine(byte[] startLine) {
        this.startLine = startLine;
    }

    public byte[] getHeader() {
        return header;
    }

    public void setHeader(byte[] header) {
        this.header = header;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}
