/*
 * Copyright 2021 creationreborn.net
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

package net.creationreborn.bridge.common;

import net.creationreborn.bridge.api.Bridge;
import net.creationreborn.bridge.common.configuration.Config;
import net.creationreborn.bridge.common.manager.IntegrationManager;
import net.creationreborn.bridge.common.manager.MessageManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BridgeImpl extends Bridge {
    
    private final Logger logger;
    private Config config;
    
    private BridgeImpl() {
        this.logger = LoggerFactory.getLogger(Bridge.ID);
    }
    
    public static boolean init() {
        if (isAvailable()) {
            return false;
        }
        
        new BridgeImpl();
        return true;
    }
    
    public boolean reload() {
        if (getConfig() == null) {
            getLogger().error("Config is not set");
            return false;
        }
        
        MessageManager.prepare();
        
        if (IntegrationManager.prepare()) {
            getLogger().info("Successfully loaded");
            return true;
        } else {
            getLogger().error("Failed to load");
            return false;
        }
    }
    
    public void debug(String format, Object... arguments) {
        if (getConfig() != null && getConfig().isDebug()) {
            getLogger().info(format, arguments);
        }
    }
    
    public static BridgeImpl getInstance() {
        return (BridgeImpl) Bridge.getInstance();
    }
    
    public Logger getLogger() {
        return logger;
    }
    
    public Config getConfig() {
        return config;
    }
    
    public void setConfig(Config config) {
        this.config = config;
    }
}