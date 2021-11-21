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
import net.creationreborn.bridge.api.configuration.Config;
import net.creationreborn.bridge.api.util.Logger;
import net.creationreborn.bridge.velocity.command.BridgeCommand;
import net.creationreborn.bridge.velocity.listener.RedisListener;
import net.creationreborn.bridge.velocity.listener.VelocityListener;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

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
    
    @Inject
    private ProxyServer proxy;
    
    @Inject
    @DataDirectory
    private Path path;
    
    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        instance = this;
        BridgeImpl bridge = new BridgeImpl();
        bridge.getLogger()
                .add(Logger.Level.INFO, LoggerFactory.getLogger(Bridge.NAME)::info)
                .add(Logger.Level.WARN, LoggerFactory.getLogger(Bridge.NAME)::warn)
                .add(Logger.Level.ERROR, LoggerFactory.getLogger(Bridge.NAME)::error)
                .add(Logger.Level.DEBUG, message -> {
                    if (Bridge.getInstance().getConfig().map(Config::isDebug).orElse(false)) {
                        LoggerFactory.getLogger(Bridge.NAME).info(message);
                    }
                });
    
        bridge.load();
        
        getProxy().getCommandManager().register("bridge", new BridgeCommand());
        getProxy().getEventManager().register(getInstance(), new VelocityListener());
        
        if (getProxy().getPluginManager().isLoaded("redisvelocity")) {
            Bridge.getInstance().getLogger().info("RedisVelocity detected");
            getProxy().getEventManager().register(getInstance(), new RedisListener());
            RedisVelocity.getInstance().registerChannels("forum");
        }
    }
    
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        if (getProxy().getPluginManager().isLoaded("redisvelocity")) {
            RedisVelocity.getInstance().unregisterChannels("forum");
        }
    }
    
    public static VelocityPlugin getInstance() {
        return instance;
    }
    
    public ProxyServer getProxy() {
        return proxy;
    }
    
    public Path getPath() {
        return path;
    }
}