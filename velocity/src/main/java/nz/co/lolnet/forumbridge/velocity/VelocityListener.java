/*
 * Copyright 2018 lolnet.co.nz
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

package nz.co.lolnet.forumbridge.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import nz.co.lolnet.api.LolnetAPI;
import nz.co.lolnet.forumbridge.common.ForumBridge;

public class VelocityListener {
    
    @Subscribe
    public void onPostLogin(PostLoginEvent event) {
        LolnetAPI.getInstance().getForumEndpoint().updateMinecraftUsername(event.getPlayer().getUniqueId(), event.getPlayer().getUsername()).async(success -> {
            if (success) {
                ForumBridge.getInstance().getLogger().debug("Successfully updated {} ({})", event.getPlayer().getUsername(), event.getPlayer().getUniqueId());
            }
        }, failure -> {
            ForumBridge.getInstance().getLogger().debug("Failed to update {} ({})", event.getPlayer().getUsername(), event.getPlayer().getUniqueId(), failure);
        });
    }
}