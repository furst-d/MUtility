package com.mens.mutility.spigot.chat.json;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.chat.ComponentSerializer;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class JsonBuilder {

    public enum ClickAction {
        RUN_COMMAND,
        SUGGEST_COMMAND,
        OPEN_URL
    }

    public enum HoverAction {
        SHOW_TEXT
    }

    public enum Effects {
        BOLD,
        ITALIC,
        STRIKETHROUGH,
        UNDERLINED,
        OBFUSCATED
    }

    protected List<String> extras;

    public JsonBuilder( String... text ) {
        extras = new ArrayList();
        for( String extra : text ) {
            this.parse(extra);
        }
    }

    public JsonBuilder parse( String text ) {
        String regex = "[&§]{1}([a-fA-Fl-oL-O0-9]){1}";
        text = text.replaceAll( regex, "§$1" );
        if( !Pattern.compile( regex ).matcher( text ).find() ) {
            text( text );
            return this;
        }

        String[] words = text.split( regex );
        int index = words[0].length();
        for( String word : words ) {
            if( index != words[0].length() )
                text( word ).hoverEvent( "§" + text.charAt( index - 1 ) );
            index += word.length() + 2;
        }
        return this;
    }

    public JsonBuilder text( String text ) {
        extras.add( "{\"text\":\"" + text + "\"}" );
        return this;
    }

    public JsonBuilder color(ChatColor color) {
        String c = color.getName().toLowerCase();
        addSegment("\"color\":\"" + c + "\"");
        return this;
    }

    public JsonBuilder color(String color) {
        addSegment("\"color\":\"" + color + "\"");
        return this;
    }

    public JsonBuilder effect(Effects effect) {
        addSegment("\"" + effect.toString().toLowerCase() + "\":true");
        return this;
    }

    public JsonBuilder clickEvent( ClickAction action, String value ) {
        addSegment( "\"clickEvent\":{\"action\":\"" + action.toString().toLowerCase() + "\",\"value\":\"" + value + "\"}" );
        return this;
    }

    public JsonBuilder hoverEvent(String color) {
        while( color.length() != 1 )
            color = color.substring(1).trim();
        color( ChatColor.of(color));
        return this;
    }

    public JsonBuilder hoverEvent(HoverAction action, String value, boolean json) {
        value = ChatColor.translateAlternateColorCodes( '&', value );
        if(json) {
            addSegment( "\"hoverEvent\":{\"action\":\"" + action.toString().toLowerCase() + "\",\"contents\":" + value + "}" );
        } else {
            addSegment( "\"hoverEvent\":{\"action\":\"" + action.toString().toLowerCase() + "\",\"contents\":\"" + value + "\"}" );
        }
        return this;
    }

    public JsonBuilder addJsonSegment(String json) {
        if(!json.isEmpty()) {
            if(json.charAt(0) == ',') {
                json = json.substring(1);
            }
        }
        extras.add(json);
        return this;
    }

    public String getJsonSegments() {
        String text = "";
        for (String extra : extras ) {
            text += extra + ",";
        }
        text = text.substring(0, text.length() - 1);
        return text;
    }

    public JsonBuilder clear() {
        extras.clear();
        return this;
    }

    public void toPlayer( Player... players ) {
        for(Player p : players)
            this.toPlayer( p );

    }

    public void toPlayer( List<Player> players ) {
        for(Player p : players)
            this.toPlayer( p );

    }

    public void toPlayer(Player player) {
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(
                new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a(toString()), ChatMessageType.CHAT, player.getUniqueId()));
    }

    public void toConsole() {
        Bukkit.getConsoleSender().spigot().sendMessage(ComponentSerializer.parse(toString()));
    }

    @Override
    public String toString() {
        if( extras.size() <= 1 ) {
            return extras.size() == 0 ? "{text:\"\"}" : extras.get(0);
        }
        String text = "[\"\"," + extras.get(0).substring( 0, extras.get(0).length() - 1 ) + "},";
        for (int i = 0; i < extras.size(); i++) {
            if(i != 0) {
                if(!extras.get(i).isEmpty()) {
                    text += extras.get(i) + ",";
                }
            }
        }
        text = text.substring(0, text.length() - 1) + "]";
        return text;
    }

    private void addSegment(String segment) {
        String lastText = extras.get( extras.size() - 1 );
        lastText = lastText.substring(0, lastText.length() - 1) + "," + segment + "}";
        extras.remove( extras.size() - 1 );
        extras.add(lastText);
    }
}
