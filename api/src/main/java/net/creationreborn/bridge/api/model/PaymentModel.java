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

package net.creationreborn.bridge.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.UUID;

public class PaymentModel {
    
    private String item;
    private String cost;
    private String currency;
    private String username;
    
    @SerializedName("minecraft_unique_id")
    private UUID minecraftUniqueId;
    
    private Result result;
    private String provider;
    
    public String getItem() {
        return item;
    }
    
    public String getCost() {
        return cost;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public String getUsername() {
        return username;
    }
    
    public UUID getMinecraftUniqueId() {
        return minecraftUniqueId;
    }
    
    public Result getResult() {
        return result;
    }
    
    public String getProvider() {
        return provider;
    }
    
    public enum Result {
        
        @SerializedName("1")
        PAYMENT_RECEIVED("Received"),
        
        @SerializedName("2")
        PAYMENT_REVERSED("Reversed"),
        
        @SerializedName("3")
        PAYMENT_REINSTATED("Reinstated");
        
        private final String name;
        
        Result(String name) {
            this.name = name;
        }
        
        public String toString() {
            return name;
        }
    }
}