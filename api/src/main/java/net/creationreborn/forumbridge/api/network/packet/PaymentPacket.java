/*
 * Copyright 2019 creationreborn.net
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

package net.creationreborn.forumbridge.api.network.packet;

import com.google.gson.annotations.SerializedName;
import net.creationreborn.forumbridge.api.network.NetworkHandler;
import net.creationreborn.forumbridge.api.network.Packet;

import java.util.UUID;

public class PaymentPacket implements Packet {
    
    private String item;
    private String cost;
    private String currency;
    private String username;
    
    @SerializedName("minecraft_unique_id")
    private UUID minecraftUniqueId;
    
    private Result result;
    private String provider;
    
    @Override
    public void process(NetworkHandler networkHandler) {
        networkHandler.handle(this);
    }
    
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
        PAYMENT_RECEIVED(1, "Received"),
        
        @SerializedName("2")
        PAYMENT_REVERSED(2, "Reversed"),
        
        @SerializedName("3")
        PAYMENT_REINSTATED(3, "Reinstated");
        
        private final int ordinal;
        private final String name;
        
        Result(int ordinal, String name) {
            this.ordinal = ordinal;
            this.name = name;
        }
        
        public String toString() {
            return name;
        }
    }
}