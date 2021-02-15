package cn.jarkata.netcross.client;

import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertTrue;

/**
 * Unit test for simple App.
 */
public class NetCrossClientTest {
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws Exception {
        NetCrossClient netCrossClient = new NetCrossClient("www.baidu.com", 80);
        netCrossClient.send(Unpooled.copiedBuffer("1235".getBytes(StandardCharsets.UTF_8)));
        assertTrue(true);
    }
}
