/*
 * Minecraft Forge
 * Copyright (c) 2016-2019.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 2.1
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */

package net.minecraftforge.fml.network.simple;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectArrayMap;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class IndexedMessageCodec
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker SIMPLENET = MarkerManager.getMarker("SIMPLENET");
    private final Short2ObjectArrayMap<MessageHandler<?>> indicies = new Short2ObjectArrayMap<>();
    private final Object2ObjectArrayMap<Class<?>, MessageHandler<?>> types = new Object2ObjectArrayMap<>();

    @SuppressWarnings("unchecked")
    public <MSG> MessageHandler<MSG> findMessageType(final MSG msgToReply) {
        return (MessageHandler<MSG>) types.get(msgToReply.getClass());
    }

    @SuppressWarnings("unchecked")
    <MSG> MessageHandler<MSG> findIndex(final short i) {
        return (MessageHandler<MSG>) indicies.get(i);
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    class MessageHandler<MSG>
    {
        private final Optional<BiConsumer<MSG, PacketBuffer>> encoder;
        private final Optional<Function<PacketBuffer, MSG>> decoder;
        private final int index;
        private final BiConsumer<MSG,Supplier<NetworkEvent.Context>> messageConsumer;
        private final Class<MSG> messageType;
        private Optional<BiConsumer<MSG, Integer>> loginIndexSetter;
        private Optional<Function<MSG, Integer>> loginIndexGetter;

        public MessageHandler(int index, Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer)
        {
            this.index = index;
            this.messageType = messageType;
            this.encoder = Optional.ofNullable(encoder);
            this.decoder = Optional.ofNullable(decoder);
            this.messageConsumer = messageConsumer;
            this.loginIndexGetter = Optional.empty();
            this.loginIndexSetter = Optional.empty();
            indicies.put((short)(index & 0xff), this);
            types.put(messageType, this);
        }

        void setLoginIndexSetter(BiConsumer<MSG, Integer> loginIndexSetter)
        {
            this.loginIndexSetter = Optional.of(loginIndexSetter);
        }

        Optional<BiConsumer<MSG, Integer>> getLoginIndexSetter() {
            return this.loginIndexSetter;
        }

        void setLoginIndexGetter(Function<MSG, Integer> loginIndexGetter) {
            this.loginIndexGetter = Optional.of(loginIndexGetter);
        }

        public Optional<Function<MSG, Integer>> getLoginIndexGetter() {
            return this.loginIndexGetter;
        }

        MSG newInstance() {
            try {
                return messageType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Invalid login message", e);
                throw new RuntimeException(e);
            }
        }
    }

    private static <M> void tryDecode(PacketBuffer payload, Supplier<NetworkEvent.Context> context, int payloadIndex, MessageHandler<M> codec)
    {
        codec.decoder.map(d->d.apply(payload)).
                map(p->{
                    // Only run the loginIndex function for payloadIndexed packets (login)
                    if (payloadIndex != Integer.MIN_VALUE)
                    {
                        codec.getLoginIndexSetter().ifPresent(f-> f.accept(p, payloadIndex));
                    }
                    return p;
                }).ifPresent(m->codec.messageConsumer.accept(m, context));
    }

    private static <M> int tryEncode(PacketBuffer target, M message, MessageHandler<M> codec) {
        codec.encoder.ifPresent(encoder->{
            target.writeByte(codec.index & 0xff);
            encoder.accept(message, target);
        });
        return codec.loginIndexGetter.orElse(m -> Integer.MIN_VALUE).apply(message);
    }

    public <MSG> int build(MSG message, PacketBuffer target)
    {
        @SuppressWarnings("unchecked")
        MessageHandler<MSG> messageHandler = (MessageHandler<MSG>)types.get(message.getClass());
        if (messageHandler == null) {
            LOGGER.error(SIMPLENET, "Received invalid message {}", message.getClass().getName());
            throw new IllegalArgumentException("Invalid message "+message.getClass().getName());
        }
        return tryEncode(target, message, messageHandler);
    }

    void consume(PacketBuffer payload, int payloadIndex, Supplier<NetworkEvent.Context> context) {
        if (payload == null) {
            LOGGER.error(SIMPLENET, "Received empty payload");
            return;
        }
        short discriminator = payload.readUnsignedByte();
        final MessageHandler<?> messageHandler = indicies.get(discriminator);
        if (messageHandler == null) {
            LOGGER.error(SIMPLENET, "Received invalid discriminator byte {}", discriminator);
            return;
        }
        tryDecode(payload, context, payloadIndex, messageHandler);
    }

    <MSG> MessageHandler<MSG> addCodecIndex(int index, Class<MSG> messageType, BiConsumer<MSG, PacketBuffer> encoder, Function<PacketBuffer, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        return new MessageHandler<>(index, messageType, encoder, decoder, messageConsumer);
    }
}