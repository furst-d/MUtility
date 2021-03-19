package com.mens.mutility.spigot.utils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;

public class MyStringUtils {
    public String getStringFromArgs(String[] args, int startIndex) {
        StringBuilder str = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            str.append(args[i]);
            if(i != args.length-1) {
                str.append(" ");
            }
        }
        return str.toString();
    }

    public double getStringWidth(String str) throws IOException, FontFormatException {
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        InputStream is = PageList.class.getResourceAsStream("/minecraft_font.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        return font.getStringBounds(str,  frc).getWidth();
    }
}
