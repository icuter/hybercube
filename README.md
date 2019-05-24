多 Reactor 多线程 nio 服务器
=====

## 架构图
![Reactor](https://github.com/edwardleejan/images/blob/master/hybercube.png?raw=true)

### 流程说明
1. 通过主 `Reactor` 接收请求连接
2. 再通过子 `Reactors` 处理 `Channel` 中的 `Buffer` 数据
3. 直到数据完整后，通过 `Protocol` 进行数据 decode
4. 继而利用 `Protocol` 中的 `Handler` 进行数据的业务处理
5. 最后通过 `Protocol` 中的 encode 进行编码返回

> 初次提交仅实现 `FixedLengthProtocol`，即固定长度报文协议

## 启动服务器

以 `EchoProtocol` 为例，需要说明的是 `EchoProtocol` 作为 `LengthBaseProtocol` 子集，具备固定长度报文协议的能力
```java
public class NioServer {

    public static void main(String[] args) throws IOException {
        ChannelAcceptor acceptor = new ChannelAcceptor();
        acceptor.setProtocolClass(EchoProtocol.class);
        acceptor.start();
    }
}
```

## FixedLengthProtocol

客户端开发
```java
public class NioClient {

    private static final int DATA_LENGTH_BIT = 4;
    Socket socket;

    public NioClient(String ip, int port) throws IOException {
        socket = new Socket(ip, port);
        socket.setKeepAlive(true);
        socket.setTcpNoDelay(true);
        socket.setReuseAddress(false);
    }

    public byte[] sendData(byte[] data) throws IOException, InterruptedException {
        if (socket.isConnected()) {
            ByteBuffer buffer = ByteBuffer.allocate(DATA_LENGTH_BIT + data.length);
            buffer.putInt(data.length);
            buffer.put(data);
            socket.getOutputStream().write(buffer.array());

            byte[] received = new byte[data.length + DATA_LENGTH_BIT];
            socket.getInputStream().read(received);
            ByteBuffer resultBuffer = ByteBuffer.allocate(data.length);
            resultBuffer.put(received, DATA_LENGTH_BIT, data.length);
            return resultBuffer.array();
        }
        return "Not Connected".getBytes();
    }

    public void close() throws IOException {
        socket.close();
    }
}
```