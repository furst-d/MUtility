package com.mens.mutility.spigot.utils;

import com.mens.mutility.spigot.chat.MyComp;
import com.mens.mutility.spigot.chat.PluginColors;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PageList2 {
    private final int limit;
    private int index;
    private int maxPage;
    private final String title;
    private String command;
    private final List<List<MyComp>> rows;
    private MyComp head;

    public PageList2(int limit, String title, String command) {
        this.limit = limit;
        this.title = title;
        this.command = command;
        index = 0;
        maxPage = 1;
        rows = new ArrayList<>();
        head = null;
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

    public List<List<MyComp>> getRows() {
        return rows;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public MyComp getHead() {
        return head;
    }

    public void setHead(MyComp head) {
        this.head = head;
    }

    public void add(MyComp... components) {
        if(getIndex() == getLimit()) {
            setMaxPage(getMaxPage() + 1);
            setIndex(0);
        } else {
            setIndex(getIndex() + 1);
        }
        rows.add(new ArrayList<>());
        for(MyComp component : components) {
            rows.get(rows.size()-1).add(component);
        }
    }

    public void clear() {
        index = 0;
        maxPage = 1;
        rows.clear();
    }

    public ComponentBuilder getList(int pageNumber) {
        ComponentBuilder cb = new ComponentBuilder();
        PluginColors colors = new PluginColors();
        cb.append("\n").event((ClickEvent) null).event((HoverEvent) null);
        String[] lines = getLines(getTitle(), colors.getSecondaryColor());
        cb.append(lines[0]).event((ClickEvent) null).event((HoverEvent) null);
        cb.append("\n").event((ClickEvent) null).event((HoverEvent) null);
        // Head
        if(!getRows().isEmpty() && getHead() != null) {
            cb.append(" ").event((ClickEvent) null).event((HoverEvent) null);
            addEvents(getHead(), cb);
            cb.append("\n").event((ClickEvent) null).event((HoverEvent) null);
        }
        boolean error = false;
        if(pageNumber > maxPage) {
            pageNumber = maxPage;
        }
        // Body
        for (int i = pageNumber * limit - limit; i < pageNumber * limit; i++) {
            try {
                cb.append(" ").event((ClickEvent) null).event((HoverEvent) null);
                for (MyComp component : getRows().get(i)) {
                    addEvents(component, cb);
                }
                if(i != pageNumber * limit - 1 && getRows().get(i+1) != null) {
                    cb.append("\n").event((ClickEvent) null).event((HoverEvent) null);
                }
            } catch (IndexOutOfBoundsException e) {
                if(i == pageNumber * limit - limit) {
                    error = true;
                    if(pageNumber == 1 && getRows().isEmpty()) {
                        cb.append(colors.getPrimaryColor() + "\n   Seznam je prázdný! \n").event((ClickEvent) null).event((HoverEvent) null);
                    }
                }
                break;
            }
        }
        if(!error) {
            if(getMaxPage() > 1) {
                cb.append("\n").event((ClickEvent) null).event((HoverEvent) null);
                cb.append(" ").event((ClickEvent) null).event((HoverEvent) null);
            }
            if(pageNumber != 1) {
                MyComp btnFirstPage = new MyComp(colors.getPrimaryColor() + "◀◀", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> " + colors.getPrimaryColor() + "První strana " + colors.getSecondaryColor() + "<<", ClickEvent.Action.RUN_COMMAND, getCommand() + " page 1");
                MyComp btnPrevPage = new MyComp(colors.getPrimaryColor() + "◀", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> " + colors.getPrimaryColor() + "Předchozí strana " + colors.getSecondaryColor() + "<<", ClickEvent.Action.RUN_COMMAND, getCommand() + " page " + (pageNumber - 1));
                addEvents(btnFirstPage, cb);
                cb.append(" ").event((ClickEvent) null).event((HoverEvent) null);
                addEvents(btnPrevPage, cb);
                cb.append(" ").event((ClickEvent) null).event((HoverEvent) null);
            }
            if(getMaxPage() > 1)  {
                MyComp pages = new MyComp(colors.getSecondaryColor() + "Strana " + colors.getPrimaryColor() + pageNumber + colors.getSecondaryColor() + " z " + colors.getPrimaryColor() + getMaxPage());
                addEvents(pages, cb);
            }
            if(pageNumber != getMaxPage()) {
                MyComp btnNextPage = new MyComp(colors.getPrimaryColor() + "▶", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> " + colors.getPrimaryColor() + "Následující strana " + colors.getSecondaryColor() + "<<", ClickEvent.Action.RUN_COMMAND, getCommand() + " page " + (pageNumber + 1));
                MyComp btnLastPage = new MyComp(colors.getPrimaryColor() + "▶▶", HoverEvent.Action.SHOW_TEXT, colors.getSecondaryColor() + ">> " + colors.getPrimaryColor() + "Poslední strana " + colors.getSecondaryColor() + "<<", ClickEvent.Action.RUN_COMMAND, getCommand() + " page " + getMaxPage());
                cb.append(" ").event((ClickEvent) null).event((HoverEvent) null);
                addEvents(btnNextPage, cb);
                cb.append(" ").event((ClickEvent) null).event((HoverEvent) null);
                addEvents(btnLastPage, cb);
            }
        }
        cb.append("\n").event((ClickEvent) null).event((HoverEvent) null);
        cb.append(lines[1]).event((ClickEvent) null).event((HoverEvent) null);
        cb.append("\n").event((ClickEvent) null).event((HoverEvent) null);
        return cb;
    }

    private void addEvents(MyComp component, ComponentBuilder cb) {
        cb.append(component.getText());
        if(component.getHoverAction() == null) {
            cb.event((HoverEvent) null);
        } else {
            cb.event(new HoverEvent(component.getHoverAction(), new Text(component.getHover())));
        }
        if(component.getClickAction() == null) {
            cb.event((ClickEvent) null);
        } else {
            cb.event(new ClickEvent(component.getClickAction(), component.getCommand()));
        }
    }

    private String[] getLines(String title, ChatColor color) {
        MyStringUtils strUt = new MyStringUtils();
        String[] lines = new String[2];
        title = title.replace(" ", "");
        try {
            int extraDistance = 0;
            double spaceLength = 0.4921875;
            double titleLength = strUt.getStringWidth(ChatColor.stripColor(title));
            double bottomLineLength = strUt.getStringWidth(ChatColor.stripColor(StringUtils.repeat("I", 50)));
            double topHalfLineLength = ((bottomLineLength - titleLength) - (2 * spaceLength)) / 2;
            int topLineNumber = (int) Math.round(topHalfLineLength / spaceLength);
            double topLineLength = (2 * topLineNumber * spaceLength) + (2 * spaceLength) + titleLength;
            if(Math.abs(topLineLength - bottomLineLength) > Math.abs(topLineLength-(bottomLineLength + spaceLength))) {
                extraDistance = topLineLength > bottomLineLength? 1 : -1;
            }
            lines[0] = color + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", topLineNumber) + ChatColor.RESET
                    + " " + title + " " + color + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", topLineNumber) + ChatColor.RESET;
            lines[1] = color + "" + ChatColor.STRIKETHROUGH + StringUtils.repeat(" ", (50 + extraDistance));
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return lines;
    }
}
