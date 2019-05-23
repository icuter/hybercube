package cn.icuter.hybercube;

import java.nio.ByteBuffer;

/**
 * @author edward
 * @since 2018-10-25
 */
public class ChannelMessage {
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    private byte[] data;
    private boolean completed;

    public byte[] getData() {
        return data;
    }

    public void setBuffer(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted() {
        this.completed = true;
    }
}
