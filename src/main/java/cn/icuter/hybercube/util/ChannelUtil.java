package cn.icuter.hybercube.util;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.LinkedList;
import java.util.List;

/**
 * @author edward
 * @since 2018-10-28
 */
public abstract class ChannelUtil {

    public static int readChannel(ReadableByteChannel channel, ByteBuffer buffer) throws IOException {
        int read = channel.read(buffer);
        if (read == -1) {
            // reach the end of channel
            // TODO tracing log here
            channel.close();
        }
        return read;
    }

    public static byte[] readFully(ReadableByteChannel channel) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.allocate(10);
        List<Byte> byteList = new LinkedList<>();
        int read = readChannel(channel, byteBuffer);
        while (read > 0) {
            if ((byteBuffer.position() > 0)) {
                byteBuffer.flip();
                for (int i = 0; i < byteBuffer.limit(); i++) {
                    byteList.add(byteBuffer.get());
                }
                byteBuffer.clear();
            }
            read = readChannel(channel, byteBuffer);
        }
        if (!byteList.isEmpty()) {
            byte[] bytes = new byte[byteList.size()];
            for (int i = bytes.length - 1; i >= 0; i--) {
                bytes[i] = byteList.remove(i);
            }
            return bytes;
        }
        return new byte[0];
    }

    public static int writeFully(WritableByteChannel channel, byte[] src) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(src);
        int totalWritten = 0;
        while (buffer.hasRemaining()) {
            int written = channel.write(buffer);
            if (written <= 0) {
                break;
            }
            totalWritten += written;
        }
        return totalWritten;
    }

    public static void closeChannelSilently(Channel channel) {
        try {
            channel.close();
        } catch (IOException e) {
            // TODO log
        }
    }
}
