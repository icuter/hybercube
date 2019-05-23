package cn.icuter.hybercube;

import cn.icuter.hybercube.protocol.Protocol;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * @author edward
 * @since 2018-10-23
 */
public class ChannelReaderListener extends Thread {
    private final Selector selector;
    private ExecutorService executorService;
    private volatile boolean shutdown;

    ChannelReaderListener(ExecutorService executorService) throws IOException {
        this.selector = Selector.open();
        this.executorService = executorService;
    }

    public void registerChannel(SocketChannel channel, Protocol protocol) throws IOException {
        channel.register(selector, SelectionKey.OP_READ, protocol);
        // TODO trace logging
        System.out.println("accepted channel: " + channel.getRemoteAddress());
    }

    public void wakeup() {
        selector.wakeup();
    }

    public void shutdown() {
        shutdown = true;
    }

    public boolean isShutdown() {
        return shutdown;
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                // 通过 select(timeout) 防止 CPU 利用率100%
                if (selector.select(1L) > 0) {
                    Set<SelectionKey> selectionKeySet = selector.selectedKeys();
                    selectionKeySet.removeIf(selectionKey -> {
                        if (!selectionKey.isValid()) {
                            System.out.println("SelectionKey is invalid");
                            return true;
                        }
                        // 利用线程池并发处理时，将使数据并发从 Channel 读取，这里将 Channel 禁止被其他线程读取
                        // 再通过 ChannelWorker 的 callback 函数恢复
                        selectionKey.interestOps(0);
                        executorService.submit(new ChannelWorker(selectionKey, key -> {
                            // 必须配合 select(timeout) 使用，修改 interest ops 后必须重新进行 select 才生效
                            key.interestOps(SelectionKey.OP_READ);
                            return null;
                        }));
                        return true;
                    });
                }
            }
        } catch (IOException e) {
            // TODO log here
            shutdown = true;
            try {
                selector.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }
}
