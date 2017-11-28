import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
/**
 * Created by lunan on 17-11-28.
 */
public class ServerHandler extends ChannelInboundHandlerAdapter{
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        ByteBuf buf = (ByteBuf)msg;
        byte [] req = new byte[buf.readableBytes()];

        buf.readBytes(req);

        String message = new String(req,"UTF-8");

        System.out.println("Netty-Server:Receive Message: "+ message);

    }
}
