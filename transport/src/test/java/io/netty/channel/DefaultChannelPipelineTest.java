/*
 * Copyright 2011 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.channel;

import static org.junit.Assert.*;
import io.netty.channel.local.LocalChannel;

import org.junit.Test;

public class DefaultChannelPipelineTest {
    @Test
    public void testReplaceChannelHandler() {
        DefaultChannelPipeline pipeline = new DefaultChannelPipeline(new LocalChannel());

        ChannelHandler handler1 = newHandler();
        pipeline.addLast("handler1", handler1);
        pipeline.addLast("handler2", handler1);
        pipeline.addLast("handler3", handler1);
        assertTrue(pipeline.get("handler1") == handler1);
        assertTrue(pipeline.get("handler2") == handler1);
        assertTrue(pipeline.get("handler3") == handler1);

        ChannelHandler newHandler1 = newHandler();
        pipeline.replace("handler1", "handler1", newHandler1);
        assertTrue(pipeline.get("handler1") == newHandler1);

        ChannelHandler newHandler3 = newHandler();
        pipeline.replace("handler3", "handler3", newHandler3);
        assertTrue(pipeline.get("handler3") == newHandler3);

        ChannelHandler newHandler2 = newHandler();
        pipeline.replace("handler2", "handler2", newHandler2);
        assertTrue(pipeline.get("handler2") == newHandler2);
    }

    private static ChannelHandler newHandler() {
        return new ChannelHandlerAdapter<Byte, Byte>() {
            @Override
            public ChannelBufferHolder<Byte> newInboundBuffer(
                    ChannelInboundHandlerContext<Byte> ctx) throws Exception {
                return ChannelBufferHolders.byteBuffer();
            }

            @Override
            public ChannelBufferHolder<Byte> newOutboundBuffer(
                    ChannelOutboundHandlerContext<Byte> ctx) throws Exception {
                return ChannelBufferHolders.byteBuffer();
            }
        };
    }
}
