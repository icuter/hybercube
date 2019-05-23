package cn.icuter.hybercube;

import cn.icuter.hybercube.exception.DecodingException;
import cn.icuter.hybercube.exception.EncodingException;
import cn.icuter.hybercube.protocol.Protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author edward
 * @since 2018-10-24
 */
public class HttpProtocol implements Protocol {
    private static final int UNIT_BUFFER_SIZE = 128;

    private HttpMessage message = new HttpMessage();

    @Override
    public byte[] decode(SelectionKey selectionKey) throws DecodingException {
        List<ByteBuffer> bufferList = null;
        try {
            bufferList = readAll(selectionKey);
        } catch (IOException e) {
            // TODO error logging
            e.printStackTrace();
        }
        if (bufferList == null || bufferList.isEmpty()) {
            return null;
        }
        ByteBuffer allBuffer;
        if (bufferList.size() > 1) {
            allBuffer = ByteBuffer.allocate(UNIT_BUFFER_SIZE * bufferList.size());
            for (ByteBuffer buffer : bufferList) {
                buffer.flip();
                allBuffer.put(buffer);
            }
        } else {
            allBuffer = bufferList.get(0);
        }
        HttpMessage message = new HttpMessage();
        byte[] readBytes = allBuffer.array();
        int startLineEndIndex = setHttpStartLine(message, readBytes);
        if (startLineEndIndex != -1) {
            setHttpHeader(message, readBytes);
        }
        setHttpBody(message, readBytes);

        return new byte[0];
    }

    private int setHttpStartLine(HttpMessage message, byte[] readBytes) {
        HttpMessage.MessageType currentMessageType = message.getCurrentMessageType();
        int endIndex = -1;
        if (currentMessageType == HttpMessage.MessageType.START_LINE) {
            for (int i = 0; i < readBytes.length; i++) {
                if (readBytes[i] == '\n') {
                    if (i < 1) {
                        throw new IllegalArgumentException("start line is invalid");
                    }
                    if (readBytes[i - 1] == '\r') {
                        endIndex = i;
                    }
                }
            }
        }
        if (endIndex != -1) {
            message.setCurrentMessageType(HttpMessage.MessageType.HEADER);
            message.setStartLine(Arrays.copyOfRange(readBytes, 0, endIndex));
        }
        return endIndex;
    }

    private void setHttpHeader(HttpMessage message, byte[] readBytes) {
        HttpMessage.MessageType currentMessageType = message.getCurrentMessageType();
        if (currentMessageType == HttpMessage.MessageType.HEADER) {
        }
    }

    private void setHttpBody(HttpMessage message, byte[] readBytes) {
        HttpMessage.MessageType currentMessageType = message.getCurrentMessageType();
        if (currentMessageType == HttpMessage.MessageType.BODY) {
        }
    }

    private List<ByteBuffer> readAll(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        List<ByteBuffer> bufferList = new LinkedList<>();
        int read;
        do {
            ByteBuffer buffer = ByteBuffer.allocate(UNIT_BUFFER_SIZE);
            read = socketChannel.read(buffer);
            if (read == -1) {
                selectionKey.attach(null);
                break;
            }
            while (read > 0 && buffer.hasRemaining()) {
                read = socketChannel.read(buffer);
            }
            bufferList.add(buffer);
        } while (read > 0);
        return bufferList;
    }

    @Override
    public byte[] handle(byte[] params) {
        return new byte[0];
    }

    @Override
    public byte[] encode(byte[] result) throws EncodingException {
        return new byte[0];
    }
}
