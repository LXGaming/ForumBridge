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

package net.creationreborn.forumbridge.velocity.listener;

import com.google.gson.JsonObject;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import io.github.lxgaming.redisvelocity.api.event.RedisMessageEvent;
import net.creationreborn.forumbridge.common.manager.PacketManager;
import net.creationreborn.forumbridge.common.util.Toolbox;

public class RedisListener {
    
    @Subscribe(order = PostOrder.LATE)
    public void onRedisMessage(RedisMessageEvent event) {
        if (Toolbox.isNotBlank(event.getChannel()) && event.getChannel().equals("forum")) {
            Toolbox.parseJson(event.getMessage(), JsonObject.class).ifPresent(PacketManager::process);
        }
    }
}