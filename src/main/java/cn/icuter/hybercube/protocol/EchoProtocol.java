package cn.icuter.hybercube.protocol;

/**
 * @author edward
 * @since 2018-10-23
 */
public class EchoProtocol extends LengthBaseProtocol {

    @Override
    public byte[] handle(byte[] params) {
        System.out.println("echo: " + new String(params));
        return params;
    }
}
