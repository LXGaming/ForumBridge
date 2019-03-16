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

package nz.co.lolnet.forumbridge.bungee;

import nz.co.lolnet.forumbridge.api.ForumBridge;
import nz.co.lolnet.forumbridge.api.configuration.Config;
import nz.co.lolnet.forumbridge.api.network.NetworkHandler;
import nz.co.lolnet.forumbridge.api.util.Reference;
import nz.co.lolnet.forumbridge.bungee.configuration.BungeeConfig;
import nz.co.lolnet.forumbridge.bungee.configuration.BungeeConfiguration;
import nz.co.lolnet.forumbridge.bungee.util.NetworkHandlerImpl;
import nz.co.lolnet.forumbridge.common.manager.IntegrationManager;
import nz.co.lolnet.forumbridge.common.manager.PacketManager;
import nz.co.lolnet.forumbridge.common.util.LoggerImpl;

import java.util.Optional;

public class ForumBridgeImpl extends ForumBridge {
    
    private BungeeConfiguration configuration;
    
    ForumBridgeImpl() {
        this.logger = new LoggerImpl();
        this.configuration = new BungeeConfiguration(BungeePlugin.getInstance().getDataFolder().toPath());
    }
    
    @Override
    public void loadForumBridge() {
        getLogger().info("Initializing...");
        reloadForumBridge();
        PacketManager.buildPackets();
        registerNetworkHandler(NetworkHandlerImpl.class);
        getLogger().info("{} v{} has loaded", Reference.NAME, Reference.VERSION);
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
        
        if (IntegrationManager.buildNodes()) {
            getLogger().info("Successfully reloaded");
            return true;
        } else {
            getLogger().error("Failed to reload");
            return false;
        }
    }
    
    @Override
    public boolean registerNetworkHandler(Class<? extends NetworkHandler> networkHandlerClass) {
        return false;
    }
    
    public static ForumBridgeImpl getInstance() {
        return (ForumBridgeImpl) ForumBridge.getInstance();
    }
    
    public BungeeConfiguration getConfiguration() {
        return configuration;
    }
    
    public Optional<BungeeConfig> getConfig() {
        if (getConfiguration() != null) {
            return Optional.ofNullable(getConfiguration().getConfig());
        }
        
        return Optional.empty();
    }
}