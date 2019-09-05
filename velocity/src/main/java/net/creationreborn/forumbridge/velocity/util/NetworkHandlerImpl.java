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

package net.creationreborn.forumbridge.velocity.util;

import com.velocitypowered.api.proxy.Player;
import net.creationreborn.forumbridge.api.network.NetworkHandler;
import net.creationreborn.forumbridge.api.network.Packet;
import net.creationreborn.forumbridge.api.network.packet.PaymentPacket;
import net.creationreborn.forumbridge.api.network.packet.RegistrationPacket;
import net.creationreborn.forumbridge.common.manager.IntegrationManager;
import net.creationreborn.forumbridge.velocity.VelocityPlugin;

public class NetworkHandlerImpl implements NetworkHandler {
    
    @Override
    public boolean handle(Packet packet) {
        return true;
    }
    
    @Override
    public void handlePayment(PaymentPacket packet) {
        Player player = VelocityPlugin.getInstance().getProxy().getPlayer(packet.getMinecraftUniqueId()).orElse(null);
        if (player == null || !player.isActive()) {
            return;
        }
        
        IntegrationManager.updateGroups(player.getUniqueId());
    }
    
    @Override
    public void handleRegistration(RegistrationPacket packet) {
        Player player = VelocityPlugin.getInstance().getProxy().getPlayer(packet.getMinecraftUsername()).orElse(null);
        if (player == null || !player.isActive()) {
            return;
        }
        
        IntegrationManager.updateGroups(player.getUniqueId());
        IntegrationManager.updateUsername(player.getUniqueId(), player.getUsername());
    }
}