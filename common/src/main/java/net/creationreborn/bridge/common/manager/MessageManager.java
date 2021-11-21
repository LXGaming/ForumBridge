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

package net.creationreborn.bridge.common.manager;

import com.google.gson.JsonObject;
import net.creationreborn.bridge.api.model.PaymentModel;
import net.creationreborn.bridge.api.model.RegistrationModel;
import net.creationreborn.bridge.common.BridgeImpl;
import net.creationreborn.bridge.common.util.StringUtils;
import net.creationreborn.bridge.common.util.Toolbox;

import java.util.HashMap;
import java.util.Map;

public final class MessageManager {
    
    private static final Map<String, Class<?>> MESSAGE_CLASSES = new HashMap<>();
    
    public static void prepare() {
        MESSAGE_CLASSES.put("forum:payment", PaymentModel.class);
        MESSAGE_CLASSES.put("forum:registration", RegistrationModel.class);
    }
    
    public static Object parse(String message) {
        JsonObject jsonObject = Toolbox.GSON.fromJson(message, JsonObject.class);
        if (jsonObject == null) {
            BridgeImpl.getInstance().getLogger().warn("Received invalid message");
            return null;
        }
        
        String id = Toolbox.GSON.fromJson(jsonObject.get("id"), String.class);
        if (StringUtils.isBlank(id)) {
            BridgeImpl.getInstance().getLogger().warn("Received invalid message");
            return null;
        }
        
        Class<?> messageClass = MESSAGE_CLASSES.get(id);
        if (messageClass == null) {
            BridgeImpl.getInstance().getLogger().warn("Received message with unknown id {}", id);
            return null;
        }
        
        Object data = Toolbox.GSON.fromJson(jsonObject.get("data"), messageClass);
        if (data == null) {
            BridgeImpl.getInstance().getLogger().warn("Failed to deserialize message {}: {}", Toolbox.getClassSimpleName(messageClass), Toolbox.GSON.toJson(jsonObject));
            return null;
        }
        
        return data;
    }
}