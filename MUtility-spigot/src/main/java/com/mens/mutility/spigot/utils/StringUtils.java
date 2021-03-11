package com.mens.mutility.spigot.utils;

public class StringUtils {
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
}
