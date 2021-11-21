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

package net.creationreborn.bridge.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import io.github.lxgaming.redisvelocity.api.RedisVelocity;
import net.creationreborn.bridge.api.Bridge;
import net.creationreborn.bridge.common.BridgeImpl;
import net.creationreborn.bridge.velocity.command.BridgeCommand;
import net.creationreborn.bridge.velocity.configuration.ConfigImpl;
import net.creationreborn.bridge.velocity.configuration.ConfigurationImpl;
import net.creationreborn.bridge.velocity.listener.RedisListener;
import net.creationreborn.bridge.velocity.listener.VelocityListener;

import java.nio.file.Path;
import java.util.Optional;

@Plugin(
        id = Bridge.ID,
        name = Bridge.NAME,
        version = Bridge.VERSION,
        description = Bridge.DESCRIPTION,
        url = Bridge.WEBSITE,
        authors = {Bridge.AUTHORS},
        dependencies = {
                @Dependency(id = "luckperms"),
                @Dependency(id = "redisvelocity", optional = true)
        }
)
public class VelocityPlugin {
    
    private static VelocityPlugin instance;
    private ConfigurationImpl configuration;
    
    @Inject
    private ProxyServer proxy;
    
    @Inject
    @DataDirectory
    private Path path;
    
    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        instance = this;
        
        this.configuration = new ConfigurationImpl(path);
        
        BridgeImpl.init();
        
        if (!getConfiguration().loadConfiguration()) {
            BridgeImpl.getInstance().getLogger().error("Failed to load");
            return;
        }
        
        if (BridgeImpl.getInstance().getConfig() == null) {
            getConfig().ifPresent(BridgeImpl.getInstance()::setConfig);
        }
        
        if (!BridgeImpl.getInstance().reload()) {
            return;
        }
        
        getConfiguration().saveConfiguration();
        
        getProxy().getCommandManager().register("bridge", new BridgeCommand());
        getProxy().getEventManager().register(getInstance(), new VelocityListener());
        
        if (getProxy().getPluginManager().isLoaded("redisvelocity")) {
            BridgeImpl.getInstance().getLogger().info("RedisVelocity detected");
            getProxy().getEventManager().register(getInstance(), new RedisListener());
            RedisVelocity.getInstance().registerChannels("forum");
        }
        
        BridgeImpl.getInstance().getLogger().info("{} v{} has started.", Bridge.NAME, Bridge.VERSION);
    }
    
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (!BridgeImpl.isAvailable()) {
            return;
        }
        
        if (getProxy().getPluginManager().isLoaded("redisvelocity")) {
            RedisVelocity.getInstance().unregisterChannels("forum");
        }
        
        BridgeImpl.getInstance().getLogger().info("{} v{} unloaded", Bridge.NAME, Bridge.VERSION);
    }
    
    public static VelocityPlugin getInstance() {
        return instance;
    }
    
    public ConfigurationImpl getConfiguration() {
        return configuration;
    }
    
    public Optional<ConfigImpl> getConfig() {
        return Optional.ofNullable(getConfiguration().getConfig());
    }
    
    public ProxyServer getProxy() {
        return proxy;
    }
    
    public Path getPath() {
        return path;
    }
}