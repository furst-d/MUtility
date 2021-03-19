package com.mens.mutility.spigot.utils;

import com.mens.mutility.spigot.chat.Button;
import com.mens.mutility.spigot.chat.PluginColors;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageList {
    private final int limit;
    private int index;
    private int maxPage;
    private final String title;
    private final String command;
    private final List<TextComponent> rows;

    public PageList(int limit, String title, String command) {
        this.limit = limit;
        this.title = title;
        this.command = command;
        index = 0;
        maxPage = 1;
        rows = new ArrayList<>();
    }

    public int getLimit() {
        return limit;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getMaxPage() {
        return maxPage;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public String getTitle() {
        return title;
    }

    public String getCommand() {
        return command;
    }

    public List<TextComponent> getRows() {
        return rows;
    }

    public void add(TextComponent component) {
        if(getIndex() == getLimit()) {
            setMaxPage(getMaxPage() + 1);
            setIndex(0);
        } else {
            setIndex(getIndex() + 1);
        }
        rows.add(component);
    }

    public void clear() {
        index = 0;
        maxPage = 1;
        rows.clear();
    }

    public ComponentBuilder getList(int pageNumber) {
        ComponentBuilder cb = new ComponentBuilder();
        PluginColors colors = new PluginColors();
        String[] lines = getLines(getTitle(), colors.getSecondaryColor(), 50);
        cb.append(lines[0]);
        cb.append("\n");
        boolean error = false;
        if(pageNumber > maxPage) {
            pageNumber = maxPage;
        }
        for (int i = pageNumber * limit - limit; i < pageNumber * limit; i++) {
            try {
                cb.append(getRows().get(i));
                if(getMaxPage() > 1) {
                    cb.append("\n");
                }
            } catch (IndexOutOfBoundsException e) {
                if(i == pageNumber * limit - limit) {
                    error = true;
                    if(pageNumber == 1) {
                        cb.append(colors.getPrimaryColor() + "\n   Seznam je prázdný!");
                    }
                    cb.append("\n");
                }
                break;
            }
        }
        if(!error) {
            if(pageNumber != 1) {
                TextComponent btnFirstPage = new Button(colors.getPrimaryColor() + "◀◀", HoverEvent.Action.SHOW_TEXT,
                        colors.getSecondaryColor() + ">> " + colors.getPrimaryColor() + "První strana " + colors.getSecondaryColor() + "<<",
                        ClickEvent.Action.RUN_COMMAND, getCommand() + " page 1").getButton();
                TextComponent btnPrevPage = new Button(colors.getPrimaryColor() + "◀", HoverEvent.Action.SHOW_TEXT,
                        colors.getSecondaryColor() + ">> " + colors.getPrimaryColor() + "Předchozí strana " + colors.getSecondaryColor() + "<<",
                        ClickEvent.Action.RUN_COMMAND, getCommand() + " page " + (pageNumber - 1)).getButton();
                cb.append(btnFirstPage);
                cb.append(" ");
                cb.append(btnPrevPage);
                cb.append(" ");
            }
            if(getMaxPage() > 1)  {
                cb.event((HoverEvent) null).event((ClickEvent) null);
                TextComponent pages = new TextComponent(colors.getSecondaryColor() + "Strana " + colors.getPrimaryColor() + pageNumber + colors.getSecondaryColor() + " z " + colors.getPrimaryColor() + getMaxPage());
                cb.append(pages);
            }
            if(pageNumber != getMaxPage()) {
                TextComponent btnNextPage = new Button(colors.getPrimaryColor() + "▶", HoverEvent.Action.SHOW_TEXT,
                        colors.getSecondaryColor() + ">> " + colors.getPrimaryColor() + "Následující strana " + colors.getSecondaryColor() + "<<",
                        ClickEvent.Action.RUN_COMMAND, getCommand() + " page " + (pageNumber + 1)).getButton();
                TextComponent btnLastPage = new Button(colors.getPrimaryColor() + "▶▶", HoverEvent.Action.SHOW_TEXT,
                        colors.getSecondaryColor() + ">> " + colors.getPrimaryColor() + "Poslední strana " + colors.getSecondaryColor() + "<<",
                        ClickEvent.Action.RUN_COMMAND, getCommand() + " page " + getMaxPage()).getButton();
                cb.append(" ");
                cb.append(btnNextPage);
                cb.append(" ");
                cb.append(btnLastPage);
            }
        }
        cb.append("\n");
        cb.event((HoverEvent) null).event((ClickEvent) null);
        cb.append(lines[1]);
        return cb;
    }

    private String[] getLines(String title, ChatColor color, int length) {
        MyStringUtils strUt = new MyStringUtils();
        String[] lines = new String[2];
        title = title.replace(" ", "");
        try {
            int extraDistance = 0;
            double spaceLength = 0.4921875;
            double titleLength = strUt.getStringWidth(ChatColor.stripColor(title));
            double bottomLineLength = strUt.getStringWidth(ChatColor.stripColor(StringUtils.repeat("I", length)));
            double topHalfLineLength = ((bottomLineLength - titleLength) - (2 * spaceLength)) / 2;
            int topLineNumber = (int) Math.round(topHalfLineLength / spaceLength);
            double topLineLength = (2 * topLineNumber * spaceLength) + (2 * spaceLength) + titleLength;
            if(Math.abs(topLineLength - bottomLineLength) > Math.abs(topLineLength-(bottomLineLength + spaceLength))) {
                extraDistance = topLineLength > bottomLineLength? 1 : -1;
            }
            lines[0] = color + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", topLineNumber) + ChatColor.RESET
                    + " " + title + " " + color + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", topLineNumber) + ChatColor.RESET;
            lines[1] = color + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", (length + extraDistance));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
