package cn.icuter.hybercube.protocol;

import cn.icuter.hybercube.exception.DecodingException;
import cn.icuter.hybercube.exception.EncodingException;

import java.nio.channels.SelectionKey;

/**
 * @author edward
 * @since 2018-10-23
 */
public interface Protocol {
    byte[] decode(SelectionKey selectionKey) throws DecodingException;
    byte[] handle(byte[] params);
    byte[] encode(byte[] result) throws EncodingException;
}
