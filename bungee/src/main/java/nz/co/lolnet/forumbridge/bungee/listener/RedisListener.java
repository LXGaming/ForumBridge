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

package nz.co.lolnet.forumbridge.bungee.listener;

import com.google.gson.JsonObject;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import nz.co.lolnet.forumbridge.common.manager.PacketManager;
import nz.co.lolnet.forumbridge.common.util.Toolbox;

public class RedisListener implements Listener {
    
    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent event) {
        if (Toolbox.isNotBlank(event.getChannel()) && event.getChannel().equals("forum")) {
            Toolbox.parseJson(event.getMessage(), JsonObject.class).ifPresent(PacketManager::process);
        }
    }
}