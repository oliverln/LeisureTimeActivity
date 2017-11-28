import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.util.CharsetUtil;
/**
 * Created by lunan on 17-11-28.
 */
public class ClientHandler extends ChannelInboundHandlerAdapter{
    private ByteBuf clientMessage;

    public ClientHandler(String input) {

        byte [] req = input.getBytes();
        clientMessage = Unpooled.buffer(req.length);
        clientMessage.writeBytes(req);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(clientMessage);

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf)msg;
        byte [] req = new byte[buf.readableBytes()];

        buf.readBytes(req);

        String message = new String(req,"UTF-8");

        System.out.println("Netty-Client:Receive Message,"+ message);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        ctx.close();
    }




}
