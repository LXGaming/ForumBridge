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

package net.creationreborn.bridge.bungee.configuration;

import net.creationreborn.bridge.common.configuration.Config;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ConfigImpl implements Config {
    
    private Map<String, String> groups = new LinkedHashMap<>();
    private Set<String> externalGroups = new LinkedHashSet<>();
    
    @Override
    public Map<String, String> getGroups() {
        return groups;
    }
    
    @Override
    public Set<String> getExternalGroups() {
        return externalGroups;
    }
}