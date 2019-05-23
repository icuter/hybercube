package cn.icuter.hybercube;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * @author edward
 * @since 2018-10-23
 */
public class ChannelMessageReader {
    List<ByteBuffer> byteBufferList = new LinkedList<>();
    private List<ChannelMessage> channelMessages = new LinkedList<>();
    private SelectionKey selectionKey;
    private boolean closed;

    ChannelMessageReader(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    public boolean isClosed() {
        return closed;
    }

    public void close() {
        closed = true;
    }

    public int read(ByteBuffer byteBuffer) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        int read = socketChannel.read(byteBuffer);
        while (read > 0 && byteBuffer.hasRemaining()) {
            read = socketChannel.read(byteBuffer);
        }
        if (read == 0) {
            return read;
        }
        if (read == -1) {
            close();
            return read;
        }
        byteBufferList.add(byteBuffer);
        return read;
    }
}
