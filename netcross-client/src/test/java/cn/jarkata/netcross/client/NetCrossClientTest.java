package cn.jarkata.netcross.client;

import cn.jarkata.netcross.wrap.MessageWrap;
import org.junit.Test;

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
        NetCrossClient netCrossClient = new NetCrossClient("localhost", 8089);
        netCrossClient.send(new MessageWrap("client", "1235"));
        assertTrue(true);
    }
}
