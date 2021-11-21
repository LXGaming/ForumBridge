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
import net.creationreborn.bridge.api.configuration.Config;
import net.creationreborn.bridge.api.util.Logger;
import net.creationreborn.bridge.bungee.command.BridgeCommand;
import net.creationreborn.bridge.bungee.listener.BungeeListener;
import net.creationreborn.bridge.bungee.listener.RedisListener;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeePlugin extends Plugin {
    
    private static BungeePlugin instance;
    
    @Override
    public void onEnable() {
        instance = this;
        BridgeImpl bridge = new BridgeImpl();
        bridge.getLogger()
                .add(Logger.Level.INFO, getLogger()::info)
                .add(Logger.Level.WARN, getLogger()::warning)
                .add(Logger.Level.ERROR, getLogger()::severe)
                .add(Logger.Level.DEBUG, message -> {
                    if (Bridge.getInstance().getConfig().map(Config::isDebug).orElse(false)) {
                        BungeePlugin.getInstance().getLogger().info(message);
                    }
                });
    
        bridge.load();
        
        getProxy().getPluginManager().registerCommand(getInstance(), new BridgeCommand());
        getProxy().getPluginManager().registerListener(getInstance(), new BungeeListener());
        
        if (getProxy().getPluginManager().getPlugin("RedisBungee") != null) {
            Bridge.getInstance().getLogger().info("RedisBungee detected");
            getProxy().getPluginManager().registerListener(getInstance(), new RedisListener());
            RedisBungee.getApi().registerPubSubChannels("forum");
        }
    }
    
    @Override
    public void onDisable() {
        if (getProxy().getPluginManager().getPlugin("RedisBungee") != null) {
            RedisBungee.getApi().registerPubSubChannels("forum");
        }
    }
    
    public static BungeePlugin getInstance() {
        return instance;
    }
}