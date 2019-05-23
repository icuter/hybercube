package cn.icuter.hybercube;

import cn.icuter.hybercube.protocol.EchoProtocol;

import java.io.IOException;

/**
 * @author edward
 * @since 2018-10-21
 */
public class NioServer {

    public static void main(String[] args) throws IOException {
        ChannelAcceptor acceptor = new ChannelAcceptor();
        acceptor.setProtocolClass(EchoProtocol.class);
        acceptor.start();
    }
}
