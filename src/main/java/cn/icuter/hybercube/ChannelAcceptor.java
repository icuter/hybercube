package cn.icuter.hybercube;

import cn.icuter.hybercube.protocol.Protocol;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author edward
 * @since 2018-10-28
 */
public class ChannelAcceptor {

    private ExecutorService executorService;
    private ChannelReaderListener[] readerListeners;
    private int subReactorIndex;
    private Class<? extends Protocol> protocolClass;
    private volatile boolean shutdown;
    private Selector selector;
    private int port = 8888;

    public void setProtocolClass(Class<? extends Protocol> protocolClass) {
        this.protocolClass = protocolClass;
    }

    ChannelAcceptor() throws IOException {
        executorService = Executors.newFixedThreadPool(32);
        readerListeners = new ChannelReaderListener[Runtime.getRuntime().availableProcessors()];
        selector = Selector.open();
    }

    public void start() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(port));
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while (selector.select() > 0 && !shutdown) {
            Set<SelectionKey> selectionKeySet = selector.selectedKeys();
            selectionKeySet.removeIf(selectionKey -> {
                ServerSocketChannel acceptedChannel = (ServerSocketChannel) selectionKey.channel();
                try {
                    SocketChannel socketChannel = acceptedChannel.accept();
                    socketChannel.configureBlocking(false);
                    subReactorIndex = (subReactorIndex % readerListeners.length) + 1;
                    int curIndex = subReactorIndex - 1;
                    ChannelReaderListener channelReaderListener = readerListeners[curIndex];
                    Protocol protocol = protocolClass != null ? protocolClass.newInstance() : null;
                    if (channelReaderListener == null) {
                        channelReaderListener = new ChannelReaderListener(executorService);
                        readerListeners[curIndex] = channelReaderListener;
                        channelReaderListener.registerChannel(socketChannel, protocol);
                        channelReaderListener.start();
                    } else {
                        channelReaderListener.registerChannel(socketChannel, protocol);
                    }
                } catch (IOException | IllegalAccessException | InstantiationException e) {
                    // TODO log here
                    e.printStackTrace();
                }
                return true;
            });
        }
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void shutdown() {
        for (ChannelReaderListener listener : readerListeners) {
            listener.shutdown();
            listener.wakeup();
        }
        shutdown = true;
        selector.wakeup();
    }
}
