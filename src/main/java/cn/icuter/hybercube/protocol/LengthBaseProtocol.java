package cn.icuter.hybercube.protocol;


import cn.icuter.hybercube.exception.DecodingException;
import cn.icuter.hybercube.exception.EncodingException;
import cn.icuter.hybercube.util.ChannelUtil;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * @author edward
 * @since 2018-10-30
 */
public abstract class LengthBaseProtocol implements Protocol {

    /**
     * indicate how many bit at the start of data for data length description
     */
    private static final int DATA_LENGTH_BIT = 4;

    private ByteBuffer lengthBuffer = ByteBuffer.allocate(DATA_LENGTH_BIT);
    private ByteBuffer dataBuffer;

    // <DataLength><DataContent>
    @Override
    public byte[] decode(SelectionKey selectionKey) throws DecodingException {
        try {
            SocketChannel channel = (SocketChannel) selectionKey.channel();
            return tryReadData(channel);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] tryReadData(SocketChannel channel) throws IOException {
        if (dataBuffer != null) {
            return tryReadReadyData(channel);
        } else {
            if (!isByteBufferFulfilled(lengthBuffer)) {
                ChannelUtil.readChannel(channel, lengthBuffer);
            }
            if (isByteBufferFulfilled(lengthBuffer)) {
                lengthBuffer.flip();
                int dataLength = lengthBuffer.getInt();
                if (dataLength <= 0) {
                    System.out.printf("Invalid data length: %s \n", dataLength);
                    // TODO should throw Exception for error request
                    return null;
                }
                dataBuffer = ByteBuffer.allocate(dataLength);
                return tryReadReadyData(channel);
            }
        }
        return null;
    }

    private byte[] tryReadReadyData(SocketChannel channel) throws IOException {
        int read = ChannelUtil.readChannel(channel, dataBuffer);
        if (read > 0 && isByteBufferFulfilled(dataBuffer)) {
            dataBuffer.flip();
            byte[] data = dataBuffer.array();
            clear();
            return data;
        }
        return null;
    }

    private boolean isByteBufferFulfilled(ByteBuffer buffer) {
        return buffer.capacity() == buffer.position();
    }

    private void clear() {
        lengthBuffer.clear();
        dataBuffer = null;
    }

    @Override
    public byte[] encode(byte[] handledResult) throws EncodingException {
        ByteBuffer encodedBuffer = ByteBuffer.allocate(DATA_LENGTH_BIT + handledResult.length);
        encodedBuffer.putInt(handledResult.length);
        encodedBuffer.put(handledResult);
        return encodedBuffer.array();
    }
}
