package com.github.zahisuku.powergrid_illuminations.utility;

import java.util.ArrayList;

import com.github.zahisuku.powergrid_illuminations.PowerGridIlluminations;

import org.patryk3211.powergrid.utility.Unit;
import org.patryk3211.powergrid.utility.NumberFormats;

import net.createmod.catnip.lang.LangBuilder;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.List;

// powergrid_illuminations用のLang
public class Lang extends org.patryk3211.powergrid.utility.Lang{
    public static MutableComponent translateDirect(String key, Object... args) {
        return builder().translate(key, args).component();
    }

    public static LangBuilder builder() {
        return Lang.builder(PowerGridIlluminations.MOD_ID);
    }

    public static LangBuilder translate(String langKey, Object... args) {
        return builder().translate(langKey, args);
    }

    public static LangBuilder unit(String unit) {
        return builder().translate("generic.unit." + unit);
    }

    public static LangBuilder unit(Unit unit) {
        return builder().translate(unit.getTranslationKey());
    }

    public static LangBuilder text(String literal) {
        return builder().text(literal);
    }

    public static LangBuilder number(double n) {
        return builder().text(NumberFormats.formatPrecise(n));
    }

    public static LangBuilder numberConstant(double n) {
        return builder().text(NumberFormats.formatConstant(n));
    }

    public static List<Component> translatedOptions(String prefix, String... keys) {
        List<Component> result = new ArrayList<>(keys.length);
        for (String key : keys)
            result.add(translate((prefix != null ? prefix + "." : "") + key).component());
        return result;
    }
}
