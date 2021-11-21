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

package net.creationreborn.forumbridge.velocity.command;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.Command;
import com.velocitypowered.api.command.CommandSource;
import net.creationreborn.forumbridge.velocity.ForumBridgeImpl;
import net.creationreborn.forumbridge.velocity.VelocityPlugin;
import net.creationreborn.forumbridge.velocity.util.VelocityToolbox;
import net.kyori.text.TextComponent;
import net.kyori.text.format.TextColor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class ForumBridgeCommand implements Command {
    
    @Override
    public void execute(@NonNull CommandSource source, String[] args) {
        if (args.length == 1 && args[0].equalsIgnoreCase("reload") && source.hasPermission("forumbridge.reload.base")) {
            VelocityPlugin.getInstance().getProxy().getScheduler().buildTask(VelocityPlugin.getInstance(), () -> {
                if (ForumBridgeImpl.getInstance().reload()) {
                    source.sendMessage(VelocityToolbox.getTextPrefix().append(TextComponent.of("Configuration reloaded", TextColor.GREEN)));
                } else {
                    source.sendMessage(VelocityToolbox.getTextPrefix().append(TextComponent.of("An error occurred. Please check the console", TextColor.RED)));
                }
            }).schedule();
            return;
        }
        
        source.sendMessage(VelocityToolbox.getPluginInformation());
    }
    
    @Override
    public List<String> suggest(@NonNull CommandSource source, String[] currentArgs) {
        if (currentArgs.length == 0 && source.hasPermission("forumbridge.reload.base")) {
            return ImmutableList.of("reload");
        }
        
        return ImmutableList.of();
    }
    
    @Override
    public boolean hasPermission(@NonNull CommandSource source, String[] args) {
        return true;
    }
}