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

package net.creationreborn.bridge.bungee;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.creationreborn.bridge.api.Bridge;
import net.creationreborn.bridge.bungee.command.BridgeCommand;
import net.creationreborn.bridge.bungee.configuration.ConfigImpl;
import net.creationreborn.bridge.bungee.configuration.ConfigurationImpl;
import net.creationreborn.bridge.bungee.listener.BungeeListener;
import net.creationreborn.bridge.bungee.listener.RedisListener;
import net.creationreborn.bridge.common.BridgeImpl;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Optional;

public class BungeePlugin extends Plugin {
    
    private static BungeePlugin instance;
    private ConfigurationImpl configuration;
    
    @Override
    public void onEnable() {
        instance = this;
        
        if (getProxy().getName().equalsIgnoreCase("BungeeCord")) {
            getLogger().severe("\n\n"
                    + "  BungeeCord is not supported - https://github.com/SpigotMC/BungeeCord/pull/1877\n"
                    + "\n"
                    + "  Use Waterfall - https://github.com/PaperMC/Waterfall\n"
            );
            return;
        }
        
        this.configuration = new ConfigurationImpl(getDataFolder().toPath());
        
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
        
        getProxy().getPluginManager().registerCommand(getInstance(), new BridgeCommand());
        getProxy().getPluginManager().registerListener(getInstance(), new BungeeListener());
        
        if (getProxy().getPluginManager().getPlugin("RedisBungee") != null) {
            BridgeImpl.getInstance().getLogger().info("RedisBungee detected");
            getProxy().getPluginManager().registerListener(getInstance(), new RedisListener());
            RedisBungee.getApi().registerPubSubChannels("forum");
        }
        
        BridgeImpl.getInstance().getLogger().info("{} v{} has started.", Bridge.NAME, Bridge.VERSION);
    }
    
    @Override
    public void onDisable() {
        if (!BridgeImpl.isAvailable()) {
            return;
        }
        
        if (getProxy().getPluginManager().getPlugin("RedisBungee") != null) {
            RedisBungee.getApi().registerPubSubChannels("forum");
        }
        
        BridgeImpl.getInstance().getLogger().info("{} v{} unloaded", Bridge.NAME, Bridge.VERSION);
    }
    
    public static BungeePlugin getInstance() {
        return instance;
    }
    
    public ConfigurationImpl getConfiguration() {
        return configuration;
    }
    
    public Optional<ConfigImpl> getConfig() {
        return Optional.ofNullable(getConfiguration().getConfig());
    }
}