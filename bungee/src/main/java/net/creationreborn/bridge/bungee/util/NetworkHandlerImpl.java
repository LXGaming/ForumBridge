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

package net.creationreborn.bridge.bungee.util;

import net.creationreborn.bridge.api.network.NetworkHandler;
import net.creationreborn.bridge.api.network.Packet;
import net.creationreborn.bridge.api.network.packet.PaymentPacket;
import net.creationreborn.bridge.api.network.packet.RegistrationPacket;
import net.creationreborn.bridge.bungee.BungeePlugin;
import net.creationreborn.bridge.common.manager.IntegrationManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class NetworkHandlerImpl implements NetworkHandler {
    
    @Override
    public boolean handle(Packet packet) {
        return true;
    }
    
    @Override
    public void handle(PaymentPacket packet) {
        ProxiedPlayer player = BungeePlugin.getInstance().getProxy().getPlayer(packet.getMinecraftUniqueId());
        if (player == null || !player.isConnected()) {
            return;
        }
        
        IntegrationManager.updateGroups(player.getUniqueId());
    }
    
    @Override
    public void handle(RegistrationPacket packet) {
        ProxiedPlayer player = BungeePlugin.getInstance().getProxy().getPlayer(packet.getMinecraftUsername());
        if (player == null || !player.isConnected()) {
            return;
        }
        
        IntegrationManager.updateUser(player.getUniqueId(), player.getName());
        IntegrationManager.updateGroups(player.getUniqueId());
    }
}