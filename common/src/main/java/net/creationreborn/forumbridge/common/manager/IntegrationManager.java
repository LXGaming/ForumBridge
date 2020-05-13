/*
 * Copyright 2018 creationreborn.net
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

package net.creationreborn.forumbridge.common.manager;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.DataMutateResult;
import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.api.User;
import net.creationreborn.api.CRAPI;
import net.creationreborn.api.data.IdentityData;
import net.creationreborn.forumbridge.api.ForumBridge;
import net.creationreborn.forumbridge.api.configuration.Config;
import net.creationreborn.forumbridge.common.util.Toolbox;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public final class IntegrationManager {
    
    private static final Map<String, String> GROUPS = Maps.newLinkedHashMap();
    private static final Set<String> EXTERNAL_GROUPS = Sets.newHashSet();
    private static final Map<String, Node> NODES = Maps.newLinkedHashMap();
    
    public static boolean prepare() {
        Map<String, String> groups = ForumBridge.getInstance().getConfig().map(Config::getGroups).orElse(null);
        if (groups == null) {
            ForumBridge.getInstance().getLogger().error("Failed to get groups from config");
            return false;
        }
        
        Set<String> externalGroups = ForumBridge.getInstance().getConfig().map(Config::getExternalGroups).orElse(null);
        if (externalGroups == null) {
            ForumBridge.getInstance().getLogger().error("Failed to get external groups from config");
            return false;
        }
        
        GROUPS.clear();
        EXTERNAL_GROUPS.clear();
        NODES.clear();
        
        GROUPS.putAll(groups);
        GROUPS.putIfAbsent("Default", "default");
        for (Map.Entry<String, String> entry : GROUPS.entrySet()) {
            if (externalGroups.contains(entry.getKey())) {
                EXTERNAL_GROUPS.add(entry.getKey());
            }
            
            LuckPerms.getApiSafe()
                    .map(api -> api.buildNode("group." + entry.getValue()).build())
                    .ifPresent(node -> NODES.put(entry.getKey(), node));
        }
        
        return true;
    }
    
    public static boolean updateGroups(UUID uniqueId) {
        try {
            if (GROUPS.isEmpty() || NODES.isEmpty()) {
                return false;
            }
            
            User user = LuckPerms.getApi().getUserManager().loadUser(uniqueId).get(30000L, TimeUnit.MILLISECONDS);
            if (user == null) {
                return false;
            }
            
            IdentityData identity = CRAPI.getInstance().getForumEndpoint().getIdentity(uniqueId).sync();
            Collection<String> groups = CRAPI.getInstance().getForumEndpoint().getGroups(identity.getUserId()).sync();
            
            ForumBridge.getInstance().getLogger().debug("Found {} groups for {}", groups.size(), uniqueId);
            
            boolean primaryGroup = false;
            for (Map.Entry<String, Node> entry : NODES.entrySet()) {
                if (EXTERNAL_GROUPS.contains(entry.getKey())) {
                    if (!user.hasPermission(entry.getValue()).asBoolean()) {
                        continue;
                    }
                    
                    if (!primaryGroup) {
                        String group = GROUPS.get(entry.getKey());
                        primaryGroup = setPrimaryGroup(user, group);
                    }
                    
                    continue;
                }
                
                if (groups.contains(entry.getKey())) {
                    if (!user.hasPermission(entry.getValue()).asBoolean()) {
                        user.setPermission(entry.getValue());
                    }
                    
                    if (!primaryGroup) {
                        String group = GROUPS.get(entry.getKey());
                        primaryGroup = setPrimaryGroup(user, group);
                    }
                    
                    continue;
                }
                
                user.unsetPermission(entry.getValue());
            }
            
            if (!primaryGroup) {
                if (setPrimaryGroup(user, "default")) {
                    ForumBridge.getInstance().getLogger().debug("Set default as primary group for {}", uniqueId);
                } else {
                    ForumBridge.getInstance().getLogger().warn("Failed to set primary group for {}", uniqueId);
                }
            }
            
            LuckPerms.getApi().getUserManager().saveUser(user).get(30000L, TimeUnit.MILLISECONDS);
            ForumBridge.getInstance().getLogger().debug("Successfully updated groups for {}", uniqueId);
            return true;
        } catch (Exception ex) {
            ForumBridge.getInstance().getLogger().debug("Failed to update groups for {}: {}", uniqueId, ex.getMessage());
            return false;
        }
    }
    
    public static boolean updateUser(UUID uniqueId, String username) {
        try {
            if (CRAPI.getInstance().getForumEndpoint().updateMinecraftUser(uniqueId, username).sync()) {
                ForumBridge.getInstance().getLogger().debug("Successfully updated username for {}", uniqueId.toString());
                return true;
            }
            
            return false;
        } catch (Exception ex) {
            ForumBridge.getInstance().getLogger().debug("Failed to update username for {}: {}", uniqueId.toString(), ex.getMessage());
            return false;
        }
    }
    
    private static boolean setPrimaryGroup(User user, String group) {
        if (user == null || Toolbox.isBlank(group)) {
            return false;
        }
        
        DataMutateResult result = user.setPrimaryGroup(group);
        if (result == DataMutateResult.SUCCESS) {
            ForumBridge.getInstance().getLogger().debug("Set {} as primary group for {} ({})", group, user.getName(), user.getUuid());
            return true;
        } else if (result == DataMutateResult.ALREADY_HAS) {
            return true;
        } else {
            ForumBridge.getInstance().getLogger().warn("Failed to set primary group to {} for {} ({})", group, user.getName(), user.getUuid());
            return false;
        }
    }
}