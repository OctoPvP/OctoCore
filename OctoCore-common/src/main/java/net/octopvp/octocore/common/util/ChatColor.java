package net.octopvp.octocore.common.util;

import com.google.common.collect.Maps;

import java.awt.*;
import java.util.Map;
import java.util.regex.Pattern;

public enum ChatColor {
    BLACK('0', 0) {
        Color getColor() {
            return Color.BLACK;
        }
    },
    DARK_BLUE('1', 1) {
        Color getColor() {
            return new Color(0, 0, 170);
        }
    },
    DARK_GREEN('2', 2) {
        Color getColor() {
            return new Color(0, 170, 0);
        }
    },
    DARK_AQUA('3', 3) {
        Color getColor() {
            return new Color(0, 170, 170);
        }
    },
    DARK_RED('4', 4) {
        Color getColor() {
            return new Color(170, 0, 0);
        }
    },
    DARK_PURPLE('5', 5) {
        Color getColor() {
            return new Color(170, 0, 170);
        }
    },
    GOLD('6', 6) {
        Color getColor() {
            return new Color(255, 170, 0);
        }
    },
    GRAY('7', 7) {
        Color getColor() {
            return new Color(170, 170, 170);
        }
    },
    DARK_GRAY('8', 8) {
        Color getColor() {
            return new Color(85, 85, 85);
        }
    },
    BLUE('9', 9) {
        Color getColor() {
            return new Color(85, 85, 255);
        }
    },
    GREEN('a', 10) {
        Color getColor() {
            return Color.GREEN;
        }
    },
    AQUA('b', 11) {
        Color getColor() {
            return new Color(85, 255, 255);
        }
    },
    RED('c', 12) {
        Color getColor() {
            return Color.RED;
        }
    },
    LIGHT_PURPLE('d', 13) {
        Color getColor() {
            return new Color(255, 85, 255);
        }
    },
    YELLOW('e', 14) {
        Color getColor() {
            return Color.YELLOW;
        }
    },
    WHITE('f', 15) {
        Color getColor() {
            return Color.WHITE;
        }
    },
    MAGIC('k', 16, true),
    BOLD('l', 17, true),
    STRIKETHROUGH('m', 18, true),
    UNDERLINE('n', 19, true),
    ITALIC('o', 20, true),
    RESET('r', 21);

    public static final char COLOR_CHAR = '§';
    private static final Pattern STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + '§' + "[0-9A-FK-OR]");
    private static final Map<Integer, ChatColor> BY_ID = Maps.newHashMap();
    private static final Map<Character, ChatColor> BY_CHAR = Maps.newHashMap();
    public static ChatColor[] ALL_CHATCOLORS = new ChatColor[]{BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, WHITE, YELLOW, LIGHT_PURPLE, STRIKETHROUGH, MAGIC, UNDERLINE, BOLD, RESET, ITALIC};

    static {
        ChatColor[] var0 = values();
        int var1 = var0.length;

        for (int var2 = 0; var2 < var1; ++var2) {
            ChatColor color = var0[var2];
            BY_ID.put(color.intCode, color);
            BY_CHAR.put(color.code, color);
        }

    }

    private final int intCode;
    private final char code;
    private final boolean isFormat;
    private final String toString;

    ChatColor(char code, int intCode) {
        this(code, intCode, false);
    }

    ChatColor(char code, int intCode, boolean isFormat) {
        this.code = code;
        this.intCode = intCode;
        this.isFormat = isFormat;
        this.toString = new String(new char[]{'§', code});
    }

    public static ChatColor getByChar(char code) {
        return BY_CHAR.get(code);
    }

    public static ChatColor getByChar(String code) {
        //Validate.notNull(code, "Code cannot be null");
        //Validate.isTrue(code.length() > 0, "Code must have at least one char");
        if (code == null) throw new NullPointerException("Code cannot be null");
        if (code.length() <= 0) throw new IllegalArgumentException("Code must have at least one char");
        return BY_CHAR.get(code.charAt(0));
    }

    public static String stripColor(String input) {
        return input == null ? null : STRIP_COLOR_PATTERN.matcher(input).replaceAll("");
    }

    public static String translateAlternateColorCodes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for (int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    public static String getLastColors(String input) {
        String result = "";
        int length = input.length();

        for (int index = length - 1; index > -1; --index) {
            char section = input.charAt(index);
            if (section == 167 && index < length - 1) {
                char c = input.charAt(index + 1);
                ChatColor color = getByChar(c);
                if (color != null) {
                    result = color + result;
                    if (color.isColor() || color.equals(RESET)) {
                        break;
                    }
                }
            }
        }

        return result;
    }

    public static ChatColor from(Enum<?> e) {
        return valueOf(e.name());
    }

    public char getChar() {
        return this.code;
    }

    public String toString() {
        return this.toString;
    }

    public boolean isFormat() {
        return this.isFormat;
    }

    public boolean isColor() {
        return !this.isFormat && this != RESET;
    }
}
