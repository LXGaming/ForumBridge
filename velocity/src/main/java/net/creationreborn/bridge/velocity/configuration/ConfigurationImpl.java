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

package net.creationreborn.bridge.velocity.configuration;

import net.creationreborn.bridge.common.configuration.Configuration;

import java.nio.file.Path;

public class ConfigurationImpl extends Configuration {
    
    public ConfigurationImpl(Path path) {
        super(path);
    }
    
    @Override
    public boolean loadConfiguration() {
        ConfigImpl config = loadFile(path.resolve("config.json"), ConfigImpl.class);
        if (config != null) {
            this.config = config;
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean saveConfiguration() {
        return saveFile(path.resolve("config.json"), config);
    }
    
    @Override
    public ConfigImpl getConfig() {
        return (ConfigImpl) super.getConfig();
    }
}