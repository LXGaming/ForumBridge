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

package nz.co.lolnet.forumbridge.bungee;

import net.md_5.bungee.api.plugin.Plugin;
import nz.co.lolnet.forumbridge.bungee.command.ForumBridgeCommand;
import nz.co.lolnet.forumbridge.bungee.listener.BungeeListener;
import nz.co.lolnet.forumbridge.common.ForumBridge;
import nz.co.lolnet.forumbridge.common.Platform;
import nz.co.lolnet.forumbridge.common.configuration.Config;
import nz.co.lolnet.forumbridge.common.util.Logger;

import java.nio.file.Path;

public class BungeePlugin extends Plugin implements Platform {
    
    private static BungeePlugin instance;
    
    @Override
    public void onEnable() {
        instance = this;
        
        ForumBridge forumBridge = new ForumBridge(this);
        forumBridge.getLogger()
                .add(Logger.Level.INFO, getLogger()::info)
                .add(Logger.Level.WARN, getLogger()::warning)
                .add(Logger.Level.ERROR, getLogger()::severe)
                .add(Logger.Level.DEBUG, message -> {
                    if (ForumBridge.getInstance().getConfig().map(Config::isDebug).orElse(false)) {
                        getLogger().info(message);
                    }
                });
        
        forumBridge.loadForumBridge();
        getProxy().getPluginManager().registerCommand(getInstance(), new ForumBridgeCommand());
        getProxy().getPluginManager().registerListener(getInstance(), new BungeeListener());
    }
    
    public static BungeePlugin getInstance() {
        return instance;
    }
    
    @Override
    public Path getPath() {
        return getDataFolder().toPath();
    }
}