package cn.icuter.hybercube;

import cn.icuter.hybercube.exception.DecodingException;
import cn.icuter.hybercube.exception.EncodingException;
import cn.icuter.hybercube.protocol.Protocol;
import cn.icuter.hybercube.util.ChannelUtil;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.function.Function;

/**
 * @author edward
 * @since 2018-10-23
 */
public class ChannelWorker implements Runnable {
    private SelectionKey selectionKey;
    private Function<SelectionKey, Void> callback;

    ChannelWorker(SelectionKey selectionKey, Function<SelectionKey, Void> callback) {
        this.selectionKey = selectionKey;
        this.callback = callback;
    }

    @Override
    public void run() {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        try {
            try {
                Protocol protocol = (Protocol) selectionKey.attachment();
                byte[] decoded = protocol.decode(selectionKey);
                if (decoded == null) {
                    return;
                }
                byte[] result = protocol.handle(decoded);
                if (result != null) {
                    int written = ChannelUtil.writeFully(socketChannel, protocol.encode(result));
                    System.out.printf("written bits: %d", written);
                }
            } catch (DecodingException e) {
                if (e.getErrorResponse() != null) {
                    ChannelUtil.writeFully(socketChannel, e.getErrorResponse().getBytes());
                }
                e.printStackTrace();
            } catch (EncodingException e) {
                if (e.getErrorResponse() != null) {
                    ChannelUtil.writeFully(socketChannel, e.getErrorResponse().getBytes());
                }
                e.printStackTrace();
            }
            if (callback != null) {
                callback.apply(selectionKey);
            }
        } catch (IOException e) {
            // TODO maybe write error message to SocketChannel is better than closing it ?
            ChannelUtil.closeChannelSilently(socketChannel);
            // TODO log
            e.printStackTrace();
        }
    }
}
