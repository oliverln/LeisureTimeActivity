import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by lunan on 17-11-28.
 */
public class EchoServerTest {
    EchoServer echoS;
    @Before
    public void setUp() throws Exception {
        echoS=new EchoServer();
    }

    @Test
    public void nettyServer() throws Exception {
        echoS.nettyServer(8989);
    }

    public void main()throws Exception{
        nettyServer();
    }

}