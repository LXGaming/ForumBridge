/*
 * Copyright 2019 lolnet.co.nz
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

package nz.co.lolnet.forumbridge.bungee.util;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import nz.co.lolnet.forumbridge.api.network.NetworkHandler;
import nz.co.lolnet.forumbridge.api.network.Packet;
import nz.co.lolnet.forumbridge.api.network.packet.PaymentPacket;
import nz.co.lolnet.forumbridge.api.network.packet.RegistrationPacket;
import nz.co.lolnet.forumbridge.bungee.BungeePlugin;
import nz.co.lolnet.forumbridge.common.manager.IntegrationManager;

public class NetworkHandlerImpl implements NetworkHandler {
    
    @Override
    public boolean handle(Packet packet) {
        return true;
    }
    
    @Override
    public void handlePayment(PaymentPacket packet) {
        ProxiedPlayer player = BungeePlugin.getInstance().getProxy().getPlayer(packet.getMinecraftUniqueId());
        if (player == null || !player.isConnected()) {
            return;
        }
        
        IntegrationManager.updateGroups(player.getUniqueId());
    }
    
    @Override
    public void handleRegistration(RegistrationPacket packet) {
        ProxiedPlayer player = BungeePlugin.getInstance().getProxy().getPlayer(packet.getMinecraftUsername());
        if (player == null || !player.isConnected()) {
            return;
        }
        
        IntegrationManager.updateGroups(player.getUniqueId());
        IntegrationManager.updateUsername(player.getUniqueId(), player.getName());
    }
}