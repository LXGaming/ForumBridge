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

package net.creationreborn.bridge.bungee.listener;

import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.creationreborn.bridge.api.model.PaymentModel;
import net.creationreborn.bridge.api.model.RegistrationModel;
import net.creationreborn.bridge.bungee.BungeePlugin;
import net.creationreborn.bridge.bungee.event.PaymentEventImpl;
import net.creationreborn.bridge.bungee.event.RegistrationEventImpl;
import net.creationreborn.bridge.common.BridgeImpl;
import net.creationreborn.bridge.common.manager.MessageManager;
import net.creationreborn.bridge.common.util.StringUtils;
import net.creationreborn.bridge.common.util.Toolbox;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class RedisListener implements Listener {
    
    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent event) {
        if (StringUtils.isBlank(event.getChannel()) || !event.getChannel().equals("forum")) {
            return;
        }
        
        Object data = MessageManager.parse(event.getMessage());
        if (data == null) {
            // no-op
        } else if (data instanceof PaymentModel) {
            BungeePlugin.getInstance().getProxy().getPluginManager().callEvent(new PaymentEventImpl((PaymentModel) data));
        } else if (data instanceof RegistrationModel) {
            BungeePlugin.getInstance().getProxy().getPluginManager().callEvent(new RegistrationEventImpl((RegistrationModel) data));
        } else {
            BridgeImpl.getInstance().getLogger().warn("Unhandled message {}", Toolbox.getClassSimpleName(data.getClass()));
        }
    }
}