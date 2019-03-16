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

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import nz.co.lolnet.forumbridge.api.ForumBridge;
import nz.co.lolnet.forumbridge.api.configuration.Config;
import nz.co.lolnet.forumbridge.api.util.Logger;
import nz.co.lolnet.forumbridge.api.util.Reference;
import nz.co.lolnet.forumbridge.velocity.command.ForumBridgeCommand;
import nz.co.lolnet.forumbridge.velocity.listener.RedisListener;
import nz.co.lolnet.forumbridge.velocity.listener.VelocityListener;
import nz.co.lolnet.redisvelocity.api.RedisVelocity;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

@Plugin(
        id = Reference.ID,
        name = Reference.NAME,
        version = Reference.VERSION,
        description = Reference.DESCRIPTION,
        url = Reference.WEBSITE,
        authors = {Reference.AUTHORS},
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
        ForumBridgeImpl forumBridge = new ForumBridgeImpl();
        forumBridge.getLogger()
                .add(Logger.Level.INFO, LoggerFactory.getLogger(Reference.NAME)::info)
                .add(Logger.Level.WARN, LoggerFactory.getLogger(Reference.NAME)::warn)
                .add(Logger.Level.ERROR, LoggerFactory.getLogger(Reference.NAME)::error)
                .add(Logger.Level.DEBUG, message -> {
                    if (ForumBridge.getInstance().getConfig().map(Config::isDebug).orElse(false)) {
                        LoggerFactory.getLogger(Reference.NAME).info(message);
                    }
                });
        
        forumBridge.loadForumBridge();
        
        getProxy().getCommandManager().register(new ForumBridgeCommand(), "forumbridge");
        getProxy().getEventManager().register(getInstance(), new VelocityListener());
        
        if (getProxy().getPluginManager().isLoaded("redisvelocity")) {
            ForumBridge.getInstance().getLogger().info("RedisVelocity detected");
            getProxy().getEventManager().register(getInstance(), new RedisListener());
            RedisVelocity.getInstance().registerChannels("forum");
        }
    }
    
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        RedisVelocity.getInstance().unregisterChannels("forum");
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