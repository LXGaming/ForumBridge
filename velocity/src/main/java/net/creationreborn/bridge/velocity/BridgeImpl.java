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

package net.creationreborn.bridge.velocity;

import net.creationreborn.bridge.api.Bridge;
import net.creationreborn.bridge.api.configuration.Config;
import net.creationreborn.bridge.api.network.NetworkHandler;
import net.creationreborn.bridge.common.manager.IntegrationManager;
import net.creationreborn.bridge.common.manager.PacketManager;
import net.creationreborn.bridge.common.util.LoggerImpl;
import net.creationreborn.bridge.velocity.configuration.VelocityConfig;
import net.creationreborn.bridge.velocity.configuration.VelocityConfiguration;
import net.creationreborn.bridge.velocity.util.NetworkHandlerImpl;

import java.util.Optional;

public class BridgeImpl extends Bridge {
    
    private VelocityConfiguration configuration;
    
    BridgeImpl() {
        this.logger = new LoggerImpl();
        this.configuration = new VelocityConfiguration(VelocityPlugin.getInstance().getPath());
    }
    
    @Override
    public void load() {
        getLogger().info("Initializing...");
        reload();
        PacketManager.buildPackets();
        registerNetworkHandler(NetworkHandlerImpl.class);
        getLogger().info("{} v{} has loaded", Bridge.NAME, Bridge.VERSION);
    }
    
    @Override
    public boolean reload() {
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
    
    public static BridgeImpl getInstance() {
        return (BridgeImpl) Bridge.getInstance();
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