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

package nz.co.lolnet.forumbridge.common.manager;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import nz.co.lolnet.api.LolnetAPI;
import nz.co.lolnet.api.data.IdentityData;
import nz.co.lolnet.forumbridge.common.ForumBridge;
import nz.co.lolnet.forumbridge.common.configuration.Config;
import nz.co.lolnet.forumbridge.common.util.Toolbox;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class IntegrationManager {
    
    private static final Map<String, Node> NODES = Toolbox.newHashMap();
    
    public static boolean buildNodes() {
        Map<String, String> groups = ForumBridge.getInstance().getConfig().map(Config::getGroups).orElse(null);
        if (groups == null) {
            ForumBridge.getInstance().getLogger().error("Failed to get groups from config");
            return false;
        }
        
        NODES.clear();
        groups.putIfAbsent("Default", "default");
        for (Map.Entry<String, String> entry : groups.entrySet()) {
            LuckPerms.getApiSafe()
                    .map(api -> api.buildNode("group." + entry.getValue()).build())
                    .ifPresent(node -> NODES.put(entry.getKey(), node));
        }
        
        return true;
    }
    
    public static boolean updateGroups(UUID uniqueId) {
        try {
            if (NODES.isEmpty()) {
                return false;
            }
            
            User user = LuckPerms.getApi().getUserManager().loadUser(uniqueId).get(30000L, TimeUnit.MILLISECONDS);
            if (user == null) {
                return false;
            }
            
            IdentityData identity = LolnetAPI.getInstance().getForumEndpoint().getIdentity(uniqueId).sync();
            Collection<String> groups = LolnetAPI.getInstance().getForumEndpoint().getGroups(identity.getUserId()).sync();
            
            ForumBridge.getInstance().getLogger().debug("Found {} groups for {}", groups.size(), uniqueId.toString());
            
            for (Map.Entry<String, Node> entry : NODES.entrySet()) {
                if (groups.contains(entry.getKey())) {
                    if (!user.hasPermission(entry.getValue()).asBoolean()) {
                        user.setPermission(entry.getValue());
                    }
                    
                    continue;
                }
                
                user.unsetPermission(entry.getValue());
            }
            
            LuckPerms.getApi().getUserManager().saveUser(user).get(30000L, TimeUnit.MILLISECONDS);
            ForumBridge.getInstance().getLogger().debug("Successfully updated groups for {}", uniqueId.toString());
            return true;
        } catch (Exception ex) {
            ForumBridge.getInstance().getLogger().debug("Failed to update groups for {}: {}", uniqueId.toString(), ex.getMessage());
            return false;
        }
    }
    
    public static boolean updateUsername(UUID uniqueId, String username) {
        try {
            if (LolnetAPI.getInstance().getForumEndpoint().updateMinecraftUsername(uniqueId, username).sync()) {
                ForumBridge.getInstance().getLogger().debug("Successfully updated username for {}", uniqueId.toString());
                return true;
            }
            
            return false;
        } catch (Exception ex) {
            ForumBridge.getInstance().getLogger().debug("Failed to update username for {}: {}", uniqueId.toString(), ex.getMessage());
            return false;
        }
    }
}