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

package net.creationreborn.bridge.velocity.util;

import net.creationreborn.bridge.api.Bridge;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

public class VelocityToolbox {
    
    public static TextComponent getTextPrefix() {
        TextComponent.Builder textBuilder = Component.text();
        textBuilder.hoverEvent(HoverEvent.showText(getPluginInformation()));
        textBuilder.content("[" + Bridge.NAME + "]").color(NamedTextColor.BLUE).decoration(TextDecoration.BOLD, true);
        return Component.text("").append(textBuilder.build()).append(Component.text(" "));
    }
    
    public static TextComponent getPluginInformation() {
        TextComponent.Builder textBuilder = Component.text();
        textBuilder.append(Component.text(Bridge.NAME, NamedTextColor.BLUE).decoration(TextDecoration.BOLD, true)).append(Component.newline());
        textBuilder.append(Component.text("    Version: ", NamedTextColor.DARK_GRAY)).append(Component.text(Bridge.VERSION, NamedTextColor.WHITE)).append(Component.newline());
        textBuilder.append(Component.text("    Authors: ", NamedTextColor.DARK_GRAY)).append(Component.text(Bridge.AUTHORS, NamedTextColor.WHITE)).append(Component.newline());
        textBuilder.append(Component.text("    Source: ", NamedTextColor.DARK_GRAY)).append(getURLTextAction(Bridge.SOURCE)).append(Component.newline());
        textBuilder.append(Component.text("    Website: ", NamedTextColor.DARK_GRAY)).append(getURLTextAction(Bridge.WEBSITE));
        return textBuilder.build();
    }
    
    public static TextComponent getURLTextAction(String url) {
        TextComponent.Builder textBuilder = Component.text();
        textBuilder.clickEvent(ClickEvent.openUrl(url));
        textBuilder.content(url).color(NamedTextColor.BLUE);
        return textBuilder.build();
    }
}