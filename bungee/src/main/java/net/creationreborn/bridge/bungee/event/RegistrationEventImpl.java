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

package net.creationreborn.bridge.bungee.event;

import net.creationreborn.bridge.api.event.RegistrationEvent;
import net.creationreborn.bridge.api.model.RegistrationModel;
import net.md_5.bungee.api.plugin.Event;

public class RegistrationEventImpl extends Event implements RegistrationEvent {
    
    private final RegistrationModel model;
    
    public RegistrationEventImpl(RegistrationModel model) {
        this.model = model;
    }
    
    @Override
    public RegistrationModel getModel() {
        return model;
    }
}