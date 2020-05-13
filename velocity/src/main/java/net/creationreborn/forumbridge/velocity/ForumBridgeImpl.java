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

package net.creationreborn.forumbridge.velocity;

import net.creationreborn.forumbridge.api.ForumBridge;
import net.creationreborn.forumbridge.api.configuration.Config;
import net.creationreborn.forumbridge.api.network.NetworkHandler;
import net.creationreborn.forumbridge.common.manager.IntegrationManager;
import net.creationreborn.forumbridge.common.manager.PacketManager;
import net.creationreborn.forumbridge.common.util.LoggerImpl;
import net.creationreborn.forumbridge.velocity.configuration.VelocityConfig;
import net.creationreborn.forumbridge.velocity.configuration.VelocityConfiguration;
import net.creationreborn.forumbridge.velocity.util.NetworkHandlerImpl;

import java.util.Optional;

public class ForumBridgeImpl extends ForumBridge {
    
    private VelocityConfiguration configuration;
    
    ForumBridgeImpl() {
        this.logger = new LoggerImpl();
        this.configuration = new VelocityConfiguration(VelocityPlugin.getInstance().getPath());
    }
    
    @Override
    public void loadForumBridge() {
        getLogger().info("Initializing...");
        reloadForumBridge();
        PacketManager.buildPackets();
        registerNetworkHandler(NetworkHandlerImpl.class);
        getLogger().info("{} v{} has loaded", ForumBridge.NAME, ForumBridge.VERSION);
    }
    
    @Override
    public boolean reloadForumBridge() {
        if (!getConfiguration().loadConfiguration()) {
            return false;
        }
        
        getConfiguration().saveConfiguration();
        if (getConfig().map(Config::isDebug).orElse(false)) {
            getLogger().debug("Debug mode enabled");
        } else {
            getLogger().info("Debug mode disabled");
        }
        
        if (IntegrationManager.prepare()) {
            getLogger().info("Successfully reloaded");
            return true;
        } else {
            getLogger().error("Failed to reload");
            return false;
        }
    }
    
    @Override
    public boolean registerNetworkHandler(Class<? extends NetworkHandler> networkHandlerClass) {
        return PacketManager.registerNetworkHandler(networkHandlerClass);
    }
    
    public static ForumBridgeImpl getInstance() {
        return (ForumBridgeImpl) ForumBridge.getInstance();
    }
    
    public VelocityConfiguration getConfiguration() {
        return configuration;
    }
    
    public Optional<VelocityConfig> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
}