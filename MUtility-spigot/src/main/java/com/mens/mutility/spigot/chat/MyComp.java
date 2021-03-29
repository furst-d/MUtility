package com.mens.mutility.spigot.chat;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;

public class MyComp {
    private TextComponent comp;
    private String text;
    private HoverEvent.Action hoverAction;
    private String hover;
    private ClickEvent.Action clickAction;
    private String command;

    public MyComp() {
    }

    public MyComp(String text) {
        this.text = text;
    }

    public MyComp(HoverEvent.Action hoverAction) {
        this.hoverAction = hoverAction;
    }

    public MyComp(HoverEvent.Action hoverAction, String hover) {
        this.hoverAction = hoverAction;
        this.hover = hover;
    }

    public MyComp(ClickEvent.Action clickAction) {
        this.clickAction = clickAction;
    }

    public MyComp(ClickEvent.Action clickAction, String command) {
        this.clickAction = clickAction;
        this.command = command;
    }

    public MyComp(HoverEvent.Action hoverAction, ClickEvent.Action clickAction) {
        this.hoverAction = hoverAction;
        this.clickAction = clickAction;
    }

    public MyComp(String text, HoverEvent.Action hoverAction, String hover) {
        this.text = text;
        this.hoverAction = hoverAction;
        this.hover = hover;
    }

    public MyComp(HoverEvent.Action hoverAction, String hover, ClickEvent.Action clickAction, String command) {
        this.hoverAction = hoverAction;
        this.hover = hover;
        this.clickAction = clickAction;
        this.command = command;
    }

    public MyComp(String text, HoverEvent.Action hoverAction, String hover, ClickEvent.Action clickAction, String command) {
        this.text = text;
        this.hoverAction = hoverAction;
        this.hover = hover;
        this.clickAction = clickAction;
        this.command = command;
    }

    public MyComp(String text, ClickEvent.Action clickAction, String command) {
        this.text = text;
        this.clickAction = clickAction;
        this.command = command;
    }

    public void setComp(TextComponent button) {
        this.comp = button;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public HoverEvent.Action getHoverAction() {
        return hoverAction;
    }

    public void setHoverAction(HoverEvent.Action hoverAction) {
        this.hoverAction = hoverAction;
    }

    public String getHover() {
        return hover;
    }

    public void setHover(String hover) {
        this.hover = hover;
    }

    public ClickEvent.Action getClickAction() {
        return clickAction;
    }

    public void setClickAction(ClickEvent.Action clickAction) {
        this.clickAction = clickAction;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public TextComponent getComp() {
        comp = new TextComponent(text);
        if(getHover() != null) {
            if(!getHover().isEmpty()) {
                comp.setHoverEvent(new HoverEvent(getHoverAction(), new Text(getHover())));
            }
        }
        if(getCommand() != null) {
            if(!getCommand().isEmpty()) {
                switch (getClickAction()) {
                    case RUN_COMMAND:
                    case SUGGEST_COMMAND:
                        comp.setClickEvent(new ClickEvent(getClickAction(), getCommand()));
                        break;
                }
            }
        }
        return comp;
    }
}
