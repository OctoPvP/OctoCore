package net.octopvp.octocore.rpg.util;

import lombok.SneakyThrows;
import net.octopvp.octocore.common.util.CC;
import net.octopvp.octocore.rpg.object.StatMod;
import net.octopvp.octocore.rpg.object.StatModifier;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

public class LoreUtils {
    private static final NumberFormat format = NumberFormat.getInstance();

    static {
        format.setGroupingUsed(true);
        format.setRoundingMode(RoundingMode.HALF_EVEN);
    }

    @SneakyThrows
    public static List<String> getStatInfo(StatModifier statModifier) {
        if (statModifier == null)
            return new ArrayList<>();
        List<String> lore = new ArrayList<>();

        for (Field field : statModifier.getClass().getDeclaredFields()) {
            if (field.getType().isAssignableFrom(int.class) || field.getType().isAssignableFrom(double.class) || field.getType().isAssignableFrom(float.class)) {
                field.setAccessible(true);
                String s;
                Object val = field.get(statModifier);
                double f = Double.parseDouble(val.toString());
                if (f == 0)
                    continue;
                if (f < 0)
                    s = CC.RED + "-" + format.format(f);
                else
                    s = CC.GREEN + "+" + format.format(f);

                if (s.endsWith(".00"))
                    s = s.substring(0, s.length() - 3);
                if (s.endsWith(".0"))
                    s = s.substring(0, s.length() - 2);

                lore.add(CC.GRAY + " - " + StringUtils.capitalize(field.getName()) + ": " + s);
            }
        }
        return lore;
    }

    public static List<String> getStatInfo(StatMod statModifier) {
        return getStatInfo(new StatModifier(statModifier));
    }
}
