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

package net.creationreborn.bridge.velocity.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.proxy.Player;
import net.creationreborn.bridge.common.manager.IntegrationManager;
import net.creationreborn.bridge.common.util.StringUtils;
import net.creationreborn.bridge.velocity.VelocityPlugin;
import net.creationreborn.bridge.velocity.event.PaymentEventImpl;
import net.creationreborn.bridge.velocity.event.RegistrationEventImpl;

public class VelocityListener {
    
    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        VelocityPlugin.getInstance().getProxy().getScheduler().buildTask(VelocityPlugin.getInstance(), () -> {
            IntegrationManager.updateUser(event.getPlayer().getUniqueId(), event.getPlayer().getUsername());
            IntegrationManager.updateGroups(event.getPlayer().getUniqueId());
        }).schedule();
    }
    
    @Subscribe
    public void onPayment(PaymentEventImpl event) {
        if (event.getModel().getMinecraftUniqueId() == null) {
            return;
        }
        
        Player player = VelocityPlugin.getInstance().getProxy().getPlayer(event.getModel().getMinecraftUniqueId()).orElse(null);
        if (player == null || !player.isActive()) {
            return;
        }
        
        IntegrationManager.updateGroups(player.getUniqueId());
    }
    
    @Subscribe
    public void onRegistration(RegistrationEventImpl event) {
        if (StringUtils.isBlank(event.getModel().getMinecraftUsername())) {
            return;
        }
        
        Player player = VelocityPlugin.getInstance().getProxy().getPlayer(event.getModel().getMinecraftUsername()).orElse(null);
        if (player == null || !player.isActive()) {
            return;
        }
        
        IntegrationManager.updateUser(player.getUniqueId(), player.getUsername());
        IntegrationManager.updateGroups(player.getUniqueId());
    }
}