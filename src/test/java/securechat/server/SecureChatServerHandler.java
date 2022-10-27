package securechat.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.net.InetAddress;
import java.util.Locale;

/**
 * @author CoffeeCatRailway
 * Created: 26/10/2022
 */
public class SecureChatServerHandler extends SimpleChannelInboundHandler<String>
{
    private static ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception
    {
        // Once session is secured, send a greeting and register the channel to the global channel
        // list so the channel received the messages from others.
        ctx.pipeline().get(SslHandler.class).handshakeFuture().addListener(new GenericFutureListener<Future<Channel>>()
        {
            @Override
            public void operationComplete(Future<Channel> future) throws Exception
            {
                ctx.writeAndFlush("Welcome to " + InetAddress.getLocalHost().getHostAddress() + " secure chat service!\n");
                ctx.writeAndFlush("Your session is protected by " + ctx.pipeline().get(SslHandler.class).engine().getSession().getCipherSuite() + " cipher suite.\n");
                channels.add(ctx.channel());
            }
        });
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception
    {
        // Send the received message to all channels but the current one.
        for (Channel channel : channels)
        {
            if (channel != ctx.channel())
                channel.writeAndFlush("[" + ctx.channel().remoteAddress() + "] " + msg + "\n");
            else
                channel.writeAndFlush("[you] " + msg + "\n");
        }

        // Close the connection if the client has sent 'bye'.
        if ("bye".equals(msg.toLowerCase(Locale.ROOT)))
            ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception
    {
        cause.printStackTrace();
        ctx.close();
    }
}
