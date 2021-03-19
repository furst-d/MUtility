package com.mens.mutility.spigot.chat;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class Button {
    private TextComponent button;
    public Button(String text) {
        button = new TextComponent(text);
    }

    public Button(String text, HoverEvent.Action hoverAction, String hover) {
        button = new TextComponent(text);
        button.setHoverEvent(new HoverEvent(hoverAction, new Text(hover)));
    }

    public Button(String text, HoverEvent.Action hoverAction, String hover, ClickEvent.Action clickAction, String command) {
        button = new TextComponent(text);
        button.setHoverEvent(new HoverEvent(hoverAction, new Text(hover)));
        switch (clickAction) {
            case RUN_COMMAND:
            case SUGGEST_COMMAND:
                button.setClickEvent(new ClickEvent(clickAction, command));
                break;
        }
    }

    public Button(String text, ClickEvent.Action clickAction, String command) {
        button = new TextComponent(text);
        switch (clickAction) {
            case RUN_COMMAND:
            case SUGGEST_COMMAND:
                button.setClickEvent(new ClickEvent(clickAction, command));
                break;
        }
    }

    public TextComponent getButton() {
        return button;
    }
}
