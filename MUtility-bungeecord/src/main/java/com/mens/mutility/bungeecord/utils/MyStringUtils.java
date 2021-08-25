package com.mens.mutility.bungeecord.utils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

public class MyStringUtils {
    public String getStringFromArgs(String[] args, int startIndex) {
        StringBuilder str = new StringBuilder();
        for (int i = startIndex; i < args.length; i++) {
            str.append(args[i]);
            if(i != args.length-1) {
                str.append(" ");
            }
        }
        return str.toString().replace("\"", "'");
    }

    public double getStringWidth(String str) throws IOException, FontFormatException {
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        InputStream is = PageList.class.getResourceAsStream("/minecraft_font.ttf");
        Font font = Font.createFont(Font.TRUETYPE_FONT, is);
        return font.getStringBounds(str,  frc).getWidth();
    }

    public String generateKey() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 12;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public String getCurrentFormattedDate() {
        LocalDateTime myDateObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String date = myDateObj.format(myFormatObj);
        return myDateObj.format(myFormatObj);
    }
}
