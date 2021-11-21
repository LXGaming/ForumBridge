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

package net.creationreborn.bridge.velocity.command;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.creationreborn.bridge.common.BridgeImpl;
import net.creationreborn.bridge.velocity.VelocityPlugin;
import net.creationreborn.bridge.velocity.util.VelocityToolbox;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;

public class BridgeCommand implements SimpleCommand {
    
    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] arguments = invocation.arguments();
        if (arguments.length == 1 && arguments[0].equalsIgnoreCase("reload") && source.hasPermission("bridge.reload.base")) {
            VelocityPlugin.getInstance().getProxy().getScheduler().buildTask(VelocityPlugin.getInstance(), () -> {
                if (BridgeImpl.getInstance().reload()) {
                    source.sendMessage(VelocityToolbox.getTextPrefix().append(Component.text("Configuration reloaded", NamedTextColor.GREEN)));
                } else {
                    source.sendMessage(VelocityToolbox.getTextPrefix().append(Component.text("An error occurred. Please check the console", NamedTextColor.RED)));
                }
            }).schedule();
            return;
        }
        
        source.sendMessage(VelocityToolbox.getPluginInformation());
    }
    
    @Override
    public List<String> suggest(Invocation invocation) {
        if (invocation.arguments().length == 0 && invocation.source().hasPermission("bridge.reload.base")) {
            return ImmutableList.of("reload");
        }
        
        return ImmutableList.of();
    }
    
    @Override
    public boolean hasPermission(Invocation invocation) {
        return true;
    }
}