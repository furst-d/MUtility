package com.mens.mutility.bungeecord.utils;

import com.mens.mutility.bungeecord.chat.PluginColors;
import com.mens.mutility.bungeecord.chat.json.JsonBuilder;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class PageList {
    private final int limit;
    private int index;
    private int maxPage;
    private String titleRaw;
    private String titleJson;
    private String command;
    private final JsonBuilder jb;
    private List<String> rows;
    private JsonBuilder head;
    private String emptyMessage;
    private int extraDistance;
    private double titleLength;
    private double topLineFinalLength;

    public PageList(int limit, String titleJson, String command) {
        this.limit = limit;
        this.titleJson = titleJson;
        this.command = command;
        index = 0;
        maxPage = 1;
        rows = new ArrayList<>();
        head = null;
        jb = new JsonBuilder();
        extraDistance = 0;
        titleLength = 0;
        topLineFinalLength = 0;
        titleRaw = "";
        emptyMessage = "   Seznam je prázdný! ";
    }

    public PageList(PageList clone) {
        this.limit = clone.getLimit();
        this.titleJson = clone.getTitleJson();
        this.command = clone.getCommand();
        index = 0;
        maxPage = 1;
        rows = new ArrayList<>();
        head = null;
        jb = new JsonBuilder();
        extraDistance = 0;
        titleLength = 0;
        topLineFinalLength = 0;
        titleRaw = "";
        emptyMessage = "   Seznam je prázdný! ";
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

    public String getTitleJson() {
        return titleJson;
    }

    public void setMaxPage(int maxPage) {
        this.maxPage = maxPage;
    }

    public String getCommand() {
        return command;
    }

    public List<String> getRows() {
        return rows;
    }

    public void setRows(List<String> rows) {
        this.rows = rows;
    }

    public JsonBuilder getHead() {
        return head;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setHead(JsonBuilder head) {
        this.head = head;
    }

    public String getEmptyMessage() {
        return emptyMessage;
    }

    public void setEmptyMessage(String emptyMessage) {
        this.emptyMessage = emptyMessage;
    }

    public void setTitleJson(String titleJson) {
        this.titleJson = titleJson;
    }

    public void add(String row) {
        rows.add(row);
        setIndex(getIndex() + 1);
        checkIndexes();
    }

    public void add(int i, String row) {
        rows.add(i, row);
        setIndex(getIndex() + 1);
        checkIndexes();
    }

    public void remove(String row) {
        if(rows.remove(row)) {
            setIndex(getIndex() - 1);
            checkIndexes();
        }
    }

    private void checkIndexes() {
        if(getIndex() == getLimit()) {
            setMaxPage(getMaxPage() + 1);
            setIndex(0);
        } else if(getIndex() == -1) {
            setMaxPage(getMaxPage() - 1);
            setIndex(getLimit() - 1);
        }
    }

    public void clear() {
        index = 0;
        maxPage = 1;
        rows.clear();
    }

    public JsonBuilder getList(int pageNumber, PageList filteredList) {
        PageList temp = this;
        if(filteredList != null) {
            temp = filteredList;
            temp.setMaxPage(filteredList.getRows().size() <= filteredList.getLimit()
                    ? 1 : filteredList.getRows().size() % filteredList.getLimit() == 0
                    ? filteredList.getRows().size() / filteredList.getLimit() : filteredList.getRows().size() / filteredList.getLimit() + 1);
            temp.setIndex(filteredList.getRows().size() % filteredList.getLimit() == 0 ? 9 : filteredList.getRows().size() % filteredList.getLimit() - 1);
        }
        jb.clear();
        PluginColors colors = new PluginColors();
        StringBuilder sb = new StringBuilder();
        JsonBuilder firstPageHover = new JsonBuilder();
        JsonBuilder firstPage = new JsonBuilder("[");
        JsonBuilder previousPageHover = new JsonBuilder();
        JsonBuilder previousPage = new JsonBuilder("[");
        JsonBuilder nextPageHover = new JsonBuilder();
        JsonBuilder nextPage = new JsonBuilder("[");
        JsonBuilder lastPageHover = new JsonBuilder();
        JsonBuilder lastPage = new JsonBuilder("[");
        jb.addJsonSegment(temp.getTopLine(colors.getSecondaryColorHEX()));

        // Head
        if(!temp.getRows().isEmpty() && temp.getHead() != null) {
            sb.append(",{\"text\":\"\n \"},");
            sb.append(temp.getHead().getJsonSegments());
            jb.addJsonSegment(sb.toString());
        }

        boolean error = false;
        if(pageNumber > temp.getMaxPage()) {
            pageNumber = temp.getMaxPage();
        }
        // Body
        sb = new StringBuilder();
        for (int i = pageNumber * temp.getLimit() - temp.getLimit(); i < pageNumber * temp.getLimit(); i++) {
            try {
                if(temp.getRows().get(i) != null) {
                    sb.append(",{\"text\":\"\n \"},");
                }
                sb.append(temp.getRows().get(i));
            } catch (IndexOutOfBoundsException e) {
                if(i == pageNumber * temp.getLimit() - temp.getLimit()) {
                    error = true;
                    if(pageNumber == 1 && temp.getRows().isEmpty()) {
                        sb.append(",{\"text\":\"\n\n\"},");
                        sb.append("{\"text\":\"").append(temp.getEmptyMessage()).append("\n\",");
                        sb.append("\"color\":\"");
                        sb.append(colors.getPrimaryColorHEX());
                        sb.append("\"}");
                    }
                }
                break;
            }
        }
        jb.addJsonSegment(sb.toString());
        sb = new StringBuilder();
        if(!error) {
            if(pageNumber != 1) {
                firstPageHover
                        .text(">> ")
                        .color(colors.getSecondaryColorHEX())
                        .text("První strana")
                        .color(colors.getPrimaryColorHEX())
                        .text(" <<")
                        .color(colors.getSecondaryColorHEX());
                firstPage
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, firstPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page 1")
                        .text("◀◀")
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, firstPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page 1")
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, firstPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page 1");

                previousPageHover
                        .text(">> ")
                        .color(colors.getSecondaryColorHEX())
                        .text("Předchozí strana")
                        .color(colors.getPrimaryColorHEX())
                        .text(" <<")
                        .color(colors.getSecondaryColorHEX());
                previousPage
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, previousPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page " + (pageNumber - 1))
                        .text("◀")
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, previousPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page " + (pageNumber - 1))
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, previousPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page " + (pageNumber - 1));
            } else {
                firstPageHover
                        .text(">> ")
                        .color(colors.getSecondaryColorHEX())
                        .text("Již se nacházíš na první straně!")
                        .color(colors.getDisableColorHEX())
                        .text(" <<")
                        .color(colors.getSecondaryColorHEX());
                firstPage
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, firstPageHover.toString(), true)
                        .text("◀◀")
                        .color(colors.getDisableColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, firstPageHover.toString(), true)
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, firstPageHover.toString(), true);

                previousPageHover
                        .text(">> ")
                        .color(colors.getSecondaryColorHEX())
                        .text("Již se nacházíš na první straně!")
                        .color(colors.getDisableColorHEX())
                        .text(" <<")
                        .color(colors.getSecondaryColorHEX());
                previousPage
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, previousPageHover.toString(), true)
                        .text("◀")
                        .color(colors.getDisableColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, previousPageHover.toString(), true)
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, previousPageHover.toString(), true);
            }
            if(pageNumber != temp.getMaxPage()) {
                nextPageHover
                        .text(">> ")
                        .color(colors.getSecondaryColorHEX())
                        .text("Následující strana")
                        .color(colors.getPrimaryColorHEX())
                        .text(" <<")
                        .color(colors.getSecondaryColorHEX());
                nextPage
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, nextPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page " + (pageNumber + 1))
                        .text("▶")
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, nextPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page " + (pageNumber + 1))
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, nextPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page " + (pageNumber + 1));

                lastPageHover
                        .text(">> ")
                        .color(colors.getSecondaryColorHEX())
                        .text("Poslední strana")
                        .color(colors.getPrimaryColorHEX())
                        .text(" <<")
                        .color(colors.getSecondaryColorHEX());
                lastPage
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, lastPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page " + temp.getMaxPage())
                        .text("▶▶")
                        .color(colors.getPrimaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, lastPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page " + temp.getMaxPage())
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, lastPageHover.toString(), true)
                        .clickEvent(JsonBuilder.ClickAction.RUN_COMMAND, temp.getCommand() + " page " + temp.getMaxPage());
            } else {
                nextPageHover
                        .text(">> ")
                        .color(colors.getSecondaryColorHEX())
                        .text("Již se nacházíš na poslední straně!")
                        .color(colors.getDisableColorHEX())
                        .text(" <<")
                        .color(colors.getSecondaryColorHEX());
                nextPage
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, nextPageHover.toString(), true)
                        .text("▶")
                        .color(colors.getDisableColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, nextPageHover.toString(), true)
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, nextPageHover.toString(), true);

                lastPageHover
                        .text(">> ")
                        .color(colors.getSecondaryColorHEX())
                        .text("Již se nacházíš na poslední straně!")
                        .color(colors.getDisableColorHEX())
                        .text(" <<")
                        .color(colors.getSecondaryColorHEX());
                lastPage
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, lastPageHover.toString(), true)
                        .text("▶▶")
                        .color(colors.getDisableColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, lastPageHover.toString(), true)
                        .text("]")
                        .color(colors.getSecondaryColorHEX())
                        .hoverEvent(JsonBuilder.HoverAction.SHOW_TEXT, lastPageHover.toString(), true);
            }
        }
        jb.addJsonSegment(sb.toString());
        if(temp.getMaxPage() > 1) {
            jb.addJsonSegment(temp.getBottomLine(colors.getSecondaryColorHEX(), true, pageNumber, firstPage, previousPage, nextPage, lastPage));
        } else {
            jb.addJsonSegment(temp.getBottomLine(colors.getSecondaryColorHEX(), false, pageNumber, firstPage, previousPage, nextPage, lastPage));
        }
        return jb;
    }

    private String getTopLine(String color) {
        MyStringUtils strUt = new MyStringUtils();
        StringBuilder sb = new StringBuilder();
        titleRaw = jb.getRawData(titleJson);
        try {
            double spaceLength = 0.4921875;
            titleLength = strUt.getStringWidth(ChatColor.stripColor(titleRaw));
            double bottomLineLength = strUt.getStringWidth(ChatColor.stripColor(StringUtils.repeat("I", 50)));
            double topHalfLineLength = ((bottomLineLength - titleLength) - (2 * spaceLength)) / 2;
            int topLineNumber = (int) Math.round(topHalfLineLength / spaceLength);
            topLineFinalLength = 2 * topLineNumber * spaceLength + titleLength;
            double topLineLength = (2 * topLineNumber * spaceLength) + (2 * spaceLength) + titleLength;
            if(Math.abs(topLineLength - bottomLineLength) > Math.abs(topLineLength-(bottomLineLength + spaceLength))) {
                extraDistance = topLineLength > bottomLineLength? 1 : -1;
            }

            sb.append("{\"text\":\"\n\"},");
            sb.append("{\"text\":\"");
            sb.append(StringUtils.repeat(" ", topLineNumber));
            sb.append("\",\"strikethrough\":true,\"color\":\"");
            sb.append(color);
            sb.append("\"},");
            sb.append(titleJson);
            sb.append(",{\"text\":\"");
            sb.append(StringUtils.repeat(" ", topLineNumber));
            sb.append("\",\"strikethrough\":true,\"color\":\"");
            sb.append(color);
            sb.append("\"}");
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    private String getBottomLine(String color, boolean showPages, int pageNumber, JsonBuilder firstPage, JsonBuilder previousPage, JsonBuilder nextPage, JsonBuilder lastPage) {
        MyStringUtils strUt = new MyStringUtils();
        StringBuilder sb = new StringBuilder();
        PluginColors colors = new PluginColors();
        try {
            double spaceLength = 0.4921875;
            double titleLength = strUt.getStringWidth("(" + pageNumber + " | " + getMaxPage() + ")");
            double arrowLength = 9;
            int bottomLineSpaces = (int)Math.round((topLineFinalLength - (arrowLength + titleLength)) / 6 / spaceLength);
            double bottomFinalLength;
            if(showPages) {
                bottomFinalLength = bottomLineSpaces * spaceLength * 6 + arrowLength + titleLength;
                extraDistance = (int)Math.round((topLineFinalLength - bottomFinalLength) / spaceLength);
                String pagesJson = new JsonBuilder("(")
                        .color(color)
                        .text(String.valueOf(pageNumber))
                        .color(colors.getPrimaryColorHEX())
                        .text(" | ")
                        .color(color)
                        .text(String.valueOf(getMaxPage()))
                        .color(colors.getPrimaryColorHEX())
                         .text(")")
                        .color(color)
                        .getJsonSegments();
                sb.append("{\"text\":\"\n\"},");
                sb.append("{\"text\":\"");
                sb.append(StringUtils.repeat(" ", bottomLineSpaces));
                sb.append("\",\"strikethrough\":true,\"color\":\"");
                sb.append(color);
                sb.append("\"},");
                sb.append(firstPage.getJsonSegments());
                sb.append(",{\"text\":\"");
                sb.append(StringUtils.repeat(" ", bottomLineSpaces));
                sb.append("\",\"strikethrough\":true,\"color\":\"");
                sb.append(color);
                sb.append("\"},");
                sb.append(previousPage.getJsonSegments());
                sb.append(",{\"text\":\"");
                sb.append(StringUtils.repeat(" ", bottomLineSpaces));
                sb.append("\",\"strikethrough\":true,\"color\":\"");
                sb.append(color);
                sb.append("\"},");
                sb.append(pagesJson);
                sb.append(",{\"text\":\"");
                sb.append(StringUtils.repeat(" ", bottomLineSpaces));
                sb.append("\",\"strikethrough\":true,\"color\":\"");
                sb.append(color);
                sb.append("\"},");
                sb.append(nextPage.getJsonSegments());
                sb.append(",{\"text\":\"");
                sb.append(StringUtils.repeat(" ", bottomLineSpaces));
                sb.append("\",\"strikethrough\":true,\"color\":\"");
                sb.append(color);
                sb.append("\"},");
                sb.append(lastPage.getJsonSegments());
                sb.append(",{\"text\":\"");
                sb.append(StringUtils.repeat(" ", (bottomLineSpaces + extraDistance)));
            } else {
                bottomFinalLength = strUt.getStringWidth(ChatColor.stripColor(StringUtils.repeat("I", 50)));
                extraDistance = (int)((topLineFinalLength - bottomFinalLength) / 0.6);
                sb.append("{\"text\":\"\n\"},");
                sb.append("{\"text\":\"");
                sb.append(StringUtils.repeat(" ", (50 + extraDistance)));
            }
            sb.append("\",\"strikethrough\":true,\"color\":\"");
            sb.append(color);
            sb.append("\"},");
            sb.append("{\"text\":\"\n\"}");
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }
}
