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

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import io.github.lxgaming.redisvelocity.api.event.RedisMessageEvent;
import net.creationreborn.bridge.api.model.PaymentModel;
import net.creationreborn.bridge.api.model.RegistrationModel;
import net.creationreborn.bridge.common.BridgeImpl;
import net.creationreborn.bridge.common.manager.MessageManager;
import net.creationreborn.bridge.common.util.StringUtils;
import net.creationreborn.bridge.common.util.Toolbox;
import net.creationreborn.bridge.velocity.VelocityPlugin;
import net.creationreborn.bridge.velocity.event.PaymentEventImpl;
import net.creationreborn.bridge.velocity.event.RegistrationEventImpl;

public class RedisListener {
    
    @Subscribe(order = PostOrder.LATE)
    public void onRedisMessage(RedisMessageEvent event) {
        if (StringUtils.isBlank(event.getChannel()) || !event.getChannel().equals("forum")) {
            return;
        }
        
        Object data = MessageManager.parse(event.getMessage());
        if (data == null) {
            // no-op
        } else if (data instanceof PaymentModel) {
            VelocityPlugin.getInstance().getProxy().getEventManager().fireAndForget(new PaymentEventImpl((PaymentModel) data));
        } else if (data instanceof RegistrationModel) {
            VelocityPlugin.getInstance().getProxy().getEventManager().fireAndForget(new RegistrationEventImpl((RegistrationModel) data));
        } else {
            BridgeImpl.getInstance().getLogger().warn("Unhandled message {}", Toolbox.getClassSimpleName(data.getClass()));
        }
    }
}