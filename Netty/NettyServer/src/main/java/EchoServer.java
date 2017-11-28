
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by lunan on 17-11-28.
 */
public class EchoServer {
    private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{
        @Override
        protected void initChannel(SocketChannel ch) throws Exception {

            ch.pipeline().addLast(new ServerHandler());
        }
    }

    public void nettyServer(int port){

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new EchoServer.ChildChannelHandler());

            //绑定端口、同步等待
            ChannelFuture futrue = serverBootstrap.bind(port).sync();

            //等待服务监听端口关闭
            futrue.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            //退出，释放线程等相关资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }


    }
}
