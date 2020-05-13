/*
 * Copyright 2019 creationreborn.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.creationreborn.forumbridge.common.manager;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import net.creationreborn.forumbridge.api.ForumBridge;
import net.creationreborn.forumbridge.api.network.NetworkHandler;
import net.creationreborn.forumbridge.api.network.Packet;
import net.creationreborn.forumbridge.api.network.packet.PaymentPacket;
import net.creationreborn.forumbridge.api.network.packet.RegistrationPacket;
import net.creationreborn.forumbridge.common.util.Toolbox;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PacketManager {
    
    private static final Set<NetworkHandler> NETWORK_HANDLERS = Sets.newHashSet();
    private static final Set<Class<? extends NetworkHandler>> NETWORK_HANDLER_CLASSES = Sets.newHashSet();
    private static final Map<String, Class<? extends Packet>> PACKET_CLASSES = Maps.newHashMap();
    
    public static void buildPackets() {
        registerPacket("forum:payment", PaymentPacket.class);
        registerPacket("forum:registration", RegistrationPacket.class);
    }
    
    public static void process(JsonObject jsonObject) {
        String packetId = Toolbox.parseJson(jsonObject.get("id"), String.class).orElse(null);
        if (Toolbox.isBlank(packetId)) {
            ForumBridge.getInstance().getLogger().warn("Received invalid packet");
            return;
        }
        
        Class<? extends Packet> packetClass = getPacketClass(packetId).orElse(null);
        if (packetClass == null) {
            ForumBridge.getInstance().getLogger().warn("Received packet with unknown id {}", packetId);
            return;
        }
        
        Packet packet = Toolbox.parseJson(jsonObject.get("data"), packetClass).orElse(null);
        if (packet == null) {
            ForumBridge.getInstance().getLogger().warn("Failed to deserialize packet {}: {}", packetClass.getName(), Toolbox.GSON.toJson(jsonObject));
            return;
        }
        
        ForumBridge.getInstance().getLogger().debug("Processing {} ({}): {}", Toolbox.getClassSimpleName(packetClass), packetId, Toolbox.GSON.toJson(packet));
        for (NetworkHandler networkHandler : NETWORK_HANDLERS) {
            try {
                if (networkHandler.handle(packet)) {
                    packet.process(networkHandler);
                }
            } catch (Exception ex) {
                ForumBridge.getInstance().getLogger().error("Encountered an error processing {}::process", networkHandler.getClass().getName(), ex);
            }
        }
    }
    
    public static boolean registerNetworkHandler(Class<? extends NetworkHandler> networkHandlerClass) {
        if (NETWORK_HANDLER_CLASSES.contains(networkHandlerClass)) {
            ForumBridge.getInstance().getLogger().warn("{} is already registered", networkHandlerClass.getSimpleName());
            return false;
        }
        
        NETWORK_HANDLER_CLASSES.add(networkHandlerClass);
        NetworkHandler networkHandler = Toolbox.newInstance(networkHandlerClass).orElse(null);
        if (networkHandler == null) {
            ForumBridge.getInstance().getLogger().error("{} failed to initialize", networkHandlerClass.getSimpleName());
            return false;
        }
        
        NETWORK_HANDLERS.add(networkHandler);
        ForumBridge.getInstance().getLogger().debug("{} registered", networkHandlerClass.getSimpleName());
        return true;
    }
    
    public static boolean registerPacket(String id, Class<? extends Packet> packetClass) {
        if (PACKET_CLASSES.containsKey(id)) {
            ForumBridge.getInstance().getLogger().warn("{} is already registered", id);
            return false;
        }
        
        if (PACKET_CLASSES.containsValue(packetClass)) {
            ForumBridge.getInstance().getLogger().warn("{} is already registered", packetClass.getSimpleName());
            return false;
        }
        
        PACKET_CLASSES.put(id, packetClass);
        return true;
    }
    
    public static Optional<Class<? extends Packet>> getPacketClass(String packetId) {
        return Optional.ofNullable(PACKET_CLASSES.get(packetId));
    }
}