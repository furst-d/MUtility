package com.mens.mutility.bungeecord.chat.json;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.chat.ComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@SuppressWarnings("unused")
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

    public JsonBuilder(String... text ) {
        extras = new ArrayList<>();
        for( String extra : text ) {
            this.parse(extra);
        }
    }

    public void parse(String text ) {
        String regex = "[&§]([a-fA-Fl-oL-O0-9])";
        text = text.replaceAll( regex, "§$1" );
        if( !Pattern.compile( regex ).matcher( text ).find() ) {
            text( text );
            return;
        }

        String[] words = text.split( regex );
        int index = words[0].length();
        for( String word : words ) {
            if( index != words[0].length() )
                text( word ).hoverEvent( "§" + text.charAt( index - 1 ) );
            index += word.length() + 2;
        }
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

    public void hoverEvent(String color) {
        while( color.length() != 1 )
            color = color.substring(1).trim();
        color( ChatColor.of(color));
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
        StringBuilder text = new StringBuilder();
        for (String extra : extras ) {
            text.append(extra).append(",");
        }
        text = new StringBuilder(text.substring(0, text.length() - 1));
        return text.toString();
    }

    public String getRawData(String json) {
        StringBuilder sb = new StringBuilder();
        String[] parts = json.split("\"text\":\"");
        for (String part : parts) {
            if(!part.equals("{")) {
                for (int i = 0; i < part.length(); i++) {
                    if(part.charAt(i) == '"') {
                        break;
                    }
                    sb.append(part.charAt(i));
                }
            }
        }
        return sb.toString();
    }

    public void clear() {
        extras.clear();
    }

    public void toPlayer( ProxiedPlayer... players ) {
        for(ProxiedPlayer p : players)
            this.toPlayer( p );

    }

    public void toPlayer( List<ProxiedPlayer> players ) {
        for(ProxiedPlayer p : players)
            this.toPlayer( p );
    }

    public void toPlayer(ProxiedPlayer player) {
        player.sendMessage(ComponentSerializer.parse(toString()));
    }

    public void toConsole() {
        ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), toString());
    }

    @Override
    public String toString() {
        if( extras.size() <= 1 ) {
            return extras.size() == 0 ? "{text:\"\"}" : extras.get(0);
        }
        StringBuilder text = new StringBuilder("[\"\"," + extras.get(0).substring(0, extras.get(0).length() - 1) + "},");
        for (int i = 0; i < extras.size(); i++) {
            if(i != 0) {
                if(!extras.get(i).isEmpty()) {
                    text.append(extras.get(i)).append(",");
                }
            }
        }
        text = new StringBuilder(text.substring(0, text.length() - 1) + "]");
        return text.toString();
    }

    private void addSegment(String segment) {
        String lastText = extras.get( extras.size() - 1 );
        lastText = lastText.substring(0, lastText.length() - 1) + "," + segment + "}";
        extras.remove( extras.size() - 1 );
        extras.add(lastText);
    }
}
