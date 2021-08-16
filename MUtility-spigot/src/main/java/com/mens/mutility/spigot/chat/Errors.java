package com.mens.mutility.spigot.chat;

import com.mens.mutility.spigot.chat.json.JsonBuilder;

public class Errors {
    private final PluginColors colors;
    private String errorMessage;

    public Errors() {
        colors = new PluginColors();
    }

    public PluginColors getColors() {
        return colors;
    }

    public String errTooMuchArguments(boolean hexColor, boolean json) {
        String errMessage = "Příliš mnoho argumentů!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        return errMessage;
    }

    public String errNotEnoughArguments(boolean hexColor, boolean json) {
        String errMessage = "Nedostatek argumentů!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        return errMessage;
    }

    public String errWrongArgument(String arg, boolean hexColor, boolean json) {
        String errMessage = "Chybný argument ";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .text(arg)
                        .color(colors.getPrimaryColorHEX())
                        .text("!")
                        .color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .text(arg)
                    .color(colors.getConsolePrimaryColor())
                    .text("!")
                    .color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        if(hexColor) {
            return errMessage + colors.getPrimaryColor() + arg + colors.getSecondaryColor() + "!";
        }
        return errMessage + colors.getConsolePrimaryColor() + arg + colors.getConsoleSecondaryColor() + "!";
    }

    public String errWrongArgumentNumber(String arg, boolean hexColor, boolean json) {
        String errMessage = "Chybný argument ";
        String errMessageDesc = " - argument musí být číslo!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .text(arg)
                        .color(colors.getPrimaryColorHEX())
                        .text(errMessageDesc)
                        .color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .text(arg)
                    .color(colors.getConsolePrimaryColor())
                    .text(errMessageDesc)
                    .color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        if(hexColor) {
            return errMessage + colors.getPrimaryColor() + arg + getColors().getSecondaryColor()
                    + errMessageDesc;
        }
        return errMessage + colors.getConsolePrimaryColor() + arg + getColors().getConsoleSecondaryColor()
                + errMessageDesc;
    }

    public String errWrongArgumentBoolean(String arg, boolean hexColor, boolean json) {
        String errMessage = "Chybný argument ";
        String errMessageDesc = " - argument musí být True nebo False!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .text(arg)
                        .color(colors.getPrimaryColorHEX())
                        .text(errMessageDesc)
                        .color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .text(arg)
                    .color(colors.getConsolePrimaryColor())
                    .text(errMessageDesc)
                    .color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        if(hexColor) {
            return errMessage + colors.getPrimaryColor() + arg + getColors().getSecondaryColor()
                    + errMessageDesc;
        }
        return errMessage + colors.getConsolePrimaryColor() + arg + getColors().getConsoleSecondaryColor()
                + errMessageDesc;
    }

    public String errWrongArgumentDate(String arg, boolean hexColor, boolean json) {
        String errMessage = "Chybný argument ";
        String errMessageDesc = " - argument musí být datum!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .text(arg)
                        .color(colors.getPrimaryColorHEX())
                        .text(errMessageDesc)
                        .color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .text(arg)
                    .color(colors.getConsolePrimaryColor())
                    .text(errMessageDesc)
                    .color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        if(hexColor) {
            return errMessage + colors.getPrimaryColor() + arg + getColors().getSecondaryColor()
                    + errMessageDesc;
        }
        return errMessage + colors.getConsolePrimaryColor() + arg + getColors().getConsoleSecondaryColor()
                + errMessageDesc;
    }

    public String errWrongArgumentOnlinePlayer(String arg, boolean hexColor, boolean json) {
        String errMessage = "Chybný argument ";
        String errMessageDesc = " - argument musí být jméno online hráče!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .text(arg)
                        .color(colors.getPrimaryColorHEX())
                        .text(errMessageDesc)
                        .color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .text(arg)
                    .color(colors.getConsolePrimaryColor())
                    .text(errMessageDesc)
                    .color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        if(hexColor) {
            return errMessage + colors.getPrimaryColor() + arg + getColors().getSecondaryColor()
                    + errMessageDesc;
        }
        return errMessage + colors.getConsolePrimaryColor() + arg + getColors().getConsoleSecondaryColor()
                + errMessageDesc;
    }

    public String errWrongArgumentPlayer(String arg, boolean hexColor, boolean json) {
        String errMessage = "Chybný argument ";
        String errMessageDesc = " - hráč nenalezen!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .text(arg)
                        .color(colors.getPrimaryColorHEX())
                        .text(errMessageDesc)
                        .color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .text(arg)
                    .color(colors.getConsolePrimaryColor())
                    .text(errMessageDesc)
                    .color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        if(hexColor) {
            return errMessage + colors.getPrimaryColor() + arg + getColors().getSecondaryColor()
                    + errMessageDesc;
        }
        return errMessage + colors.getConsolePrimaryColor() + arg + getColors().getConsoleSecondaryColor()
                + errMessageDesc;
    }

    public String errWrongArgumentPositiveNumber(String arg, boolean hexColor, boolean json) {
        String errMessage = "Chybný argument ";
        String errMessageDesc = " - argument musí být kladné číslo!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .text(arg)
                        .color(colors.getPrimaryColorHEX())
                        .text(errMessageDesc)
                        .color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .text(arg)
                    .color(colors.getConsolePrimaryColor())
                    .text(errMessageDesc)
                    .color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        if(hexColor) {
            return errMessage + colors.getPrimaryColor() + arg + getColors().getSecondaryColor()
                    + errMessageDesc;
        }
        return errMessage + colors.getConsolePrimaryColor() + arg + getColors().getConsoleSecondaryColor()
                + errMessageDesc;
    }

    public String errNotInGame(boolean hexColor, boolean json) {
        String errMessage = "Tento příkaz musí být zadán ve hře!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        return errMessage;
    }

    public String errNotInConsole(boolean hexColor, boolean json) {
        String errMessage = "Tento příkaz musí být zadán z konzole!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        return errMessage;
    }

    public String errNoPermission(boolean hexColor, boolean json) {
        String errMessage = "Na použití tohoto příkazu nemáte dostatečná oprávnění!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        return errMessage;
    }

    public String errWrongNick(boolean hexColor, boolean json) {
        String errMessage = "Hráč nebyl nalezen!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        return errMessage;
    }

    public String errDenied(boolean hexColor, boolean json) {
        String errMessage = "Přístup zamítnut!";
        if(json) {
            JsonBuilder jb = new JsonBuilder(errMessage);
            if(hexColor) {
                return jb.color(colors.getSecondaryColorHEX())
                        .getJsonSegments();
            }
            return jb.color(colors.getConsoleSecondaryColor())
                    .getJsonSegments();
        }
        return errMessage;
    }

}
