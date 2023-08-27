package io.github.idoomful.bukkitutils.statics;

import dev.dbassett.skullcreator.SkullCreator;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.*;

public class ItemBuilder {
    private static final Map<String, Enchantment> enchants = new LinkedHashMap<>();
    private static String itemID = "";
    private static int attributeIndex = 0;
    private static int totalAttributeCount = 0;
    private static boolean setAttackDamage = false;

    static {
        enchants.put("protection", Enchantment.PROTECTION_ENVIRONMENTAL);
        enchants.put("fire_protection", Enchantment.PROTECTION_FIRE);
        enchants.put("feather_falling", Enchantment.PROTECTION_FALL);
        enchants.put("blast_protection", Enchantment.PROTECTION_EXPLOSIONS);
        enchants.put("projectile_protection", Enchantment.PROTECTION_PROJECTILE);
        enchants.put("respiration", Enchantment.OXYGEN);
        enchants.put("aqua_affinity", Enchantment.WATER_WORKER);
        enchants.put("thorns", Enchantment.THORNS);
        enchants.put("depth_strider", Enchantment.DEPTH_STRIDER);
        enchants.put("sharpness", Enchantment.DAMAGE_ALL);
        enchants.put("smite", Enchantment.DAMAGE_UNDEAD);
        enchants.put("bane_of_arthropods", Enchantment.DAMAGE_ARTHROPODS);
        enchants.put("knockback", Enchantment.KNOCKBACK);
        enchants.put("fire_aspect", Enchantment.FIRE_ASPECT);
        enchants.put("looting", Enchantment.LOOT_BONUS_MOBS);
        enchants.put("efficiency", Enchantment.DIG_SPEED);
        enchants.put("silk_touch", Enchantment.SILK_TOUCH);
        enchants.put("unbreaking", Enchantment.DURABILITY);
        enchants.put("fortune", Enchantment.LOOT_BONUS_BLOCKS);
        enchants.put("power", Enchantment.ARROW_DAMAGE);
        enchants.put("punch", Enchantment.ARROW_KNOCKBACK);
        enchants.put("infinity", Enchantment.ARROW_INFINITE);
        enchants.put("luck_of_the_sea", Enchantment.LUCK);
        enchants.put("lure", Enchantment.LURE);

        if (!VersionUtils.usesVersionBetween("1.1.x", "1.8.x")) {
            enchants.put("mending", Enchantment.MENDING);
            enchants.put("curse_of_vanishing", Enchantment.VANISHING_CURSE);
            enchants.put("curse_of_binding", Enchantment.BINDING_CURSE);
            enchants.put("frost_walker", Enchantment.FROST_WALKER);
            enchants.put("sweeping_edge", Enchantment.SWEEPING_EDGE);
        }

        if (!VersionUtils.usesVersionBetween("1.1.x", "1.12.x")) {
            enchants.put("loyalty", Enchantment.LOYALTY);
            enchants.put("impaling", Enchantment.IMPALING);
            enchants.put("channeling", Enchantment.CHANNELING);
            enchants.put("riptide", Enchantment.RIPTIDE);
        }

        if (!VersionUtils.usesVersionBetween("1.1.x", "1.13.x")) {
            enchants.put("multishot", Enchantment.MULTISHOT);
            enchants.put("piercing", Enchantment.PIERCING);
            enchants.put("quick_charge", Enchantment.QUICK_CHARGE);
        }

        if (!VersionUtils.usesVersionBetween("1.1.x", "1.15.x")) {
            enchants.put("soul_speed", Enchantment.SOUL_SPEED);
        }
    }

    public static ItemStack build(String value) {
        ItemStack result = new ItemStack(Material.STONE, 1);
        final List<String> tags = Arrays.asList(value.split(" "));

        final OfflinePlayer player = tags.stream().anyMatch(tg -> tg.startsWith("player:"))
                ? tags.stream()
                .filter(tg -> tg.startsWith("player:"))
                .map(tg -> Bukkit.getOfflinePlayer(tg.split(":")[1]))
                .map(OfflinePlayer.class::cast)
                .findFirst().orElse(null)
                : null;

        for (String tag : tags) {
            final String newTag = player != null ? TextUtils.placeholder(player, tag) : tag;

            if (tag.startsWith("id:")) {
                setID(result, newTag);
                continue;
            }

            if (tag.startsWith("amount:")) {
                setAmount(result, tag);
                continue;
            }

            if (tag.startsWith("name:")) {
                setDisplayName(result, newTag);
                continue;
            }

            if (tag.startsWith("lore:")) {
                setLore(result, newTag);
                continue;
            }

            if (tag.startsWith("effect:")) {
                addEffect(result, tag, value);
                continue;
            }

            if((tag.startsWith("url-code:") || tag.startsWith("urlCode:"))) {
                result = setUrlCode(result, tag);
                continue;
            }

            if (tag.startsWith("player:")) {
                setSkullFromPlayerName(result, tag);
                continue;
            }

            if (tag.startsWith("pattern:")) {
                setBannerPattern(result, tag);
                continue;
            }

            if (tag.startsWith("color:")) {
                setArmorColor(result, tag);
                continue;
            }

            if (tag.equalsIgnoreCase("hide-flags") || tag.equalsIgnoreCase("hideFlags")) {
                hideFlags(result);
                continue;
            }

            if (tag.startsWith("hide-flag:") || tag.startsWith("hideFlag:")) {
                hideFlag(result, tag);
                continue;
            }

            if (tag.startsWith("customModelData:") || tag.startsWith("custom-model-data:")) {
                setCustomModelData(result, tag);
                continue;
            }

            if (tag.equalsIgnoreCase("unbreakable")) {
                result = setUnbreakable(result);
                continue;
            }

            if (tag.startsWith("nbt-string:") || tag.startsWith("nbtString:")) {
                result = setNBTString(result, tag);
                continue;
            }

            if (tag.startsWith("nbt-byte:") || tag.startsWith("nbtByte:")) {
                result = setNBTNumber(result, NBTNumber.BYTE, tag);
                continue;
            }

            if (tag.startsWith("nbt-short:") || tag.startsWith("nbtShort:")) {
                result = setNBTNumber(result, NBTNumber.SHORT, tag);
                continue;
            }

            if (tag.startsWith("nbt-int:") || tag.startsWith("nbtInt:")) {
                result = setNBTNumber(result, NBTNumber.INTEGER, tag);
                continue;
            }

            if (tag.startsWith("nbt-float:") || tag.startsWith("nbtFloat:")) {
                result = setNBTNumber(result, NBTNumber.FLOAT, tag);
                continue;
            }

            if (tag.startsWith("nbt-double:") || tag.startsWith("nbtDouble:")) {
                result = setNBTNumber(result, NBTNumber.DOUBLE, tag);
                continue;
            }

            if(tag.startsWith("attribute:")) {
                result = setAttribute(result, tag);
                continue;
            }

            if(tag.contains(":")) {
                addEnchantment(result, tag);
            }
        }

        itemID = "";
        attributeIndex = 0;
        totalAttributeCount = 0;
        setAttackDamage = false;

        return result;
    }

    // TODO Check for ID
    @SuppressWarnings("deprecation")
    private static void setID(ItemStack input, String tag) {
        String id = tag.substring(tag.indexOf(":") + 1);

        if (id.contains(":")) {
            final String[] separate = id.split(":");

            id = separate[0];
            final int damage = Integer.parseInt(separate[1]);

            input.setType(Objects.requireNonNull(MaterialUtils.getMaterialByID(id)));

            try {
                if (damage > 9999) {
                    input.setDurability((short) 1);
                    return;
                }

                input.setDurability((short) damage);
            } catch (NumberFormatException e) {
                input.setDurability((short) 1);
            }
        } else {
            itemID = id;

            if(itemID.equalsIgnoreCase("splash_potion"))
                input.setType(Objects.requireNonNull(MaterialUtils.getMaterialByID("potion")));
            else if(itemID.equalsIgnoreCase("sign") && !VersionUtils.usesVersionBetween("1.4.x", "1.13.x")) {
                input.setType(Objects.requireNonNull(MaterialUtils.getMaterialByID("oak_sign")));
            } else if(itemID.equalsIgnoreCase("bed") && !VersionUtils.usesVersionBetween("1.4.x", "1.12.x")) {
                input.setType(Objects.requireNonNull(MaterialUtils.getMaterialByID("red_bed")));
            } else if(itemID.equalsIgnoreCase("boat") && !VersionUtils.usesVersionBetween("1.4.x", "1.8.x")) {
                input.setType(Objects.requireNonNull(MaterialUtils.getMaterialByID("oak_boat")));
            } else {
                final Material mat = MaterialUtils.getMaterialByID(id);

                if(mat != null) input.setType(Objects.requireNonNull(MaterialUtils.getMaterialByID(id)));
                else input.setType(Material.STONE);
            }
        }
    }

    // TODO Check for amount
    private static void setAmount(ItemStack input, String tag) {
        try {
            final int amount = Integer.parseInt(tag.split(":")[1]);
            input.setAmount(Math.max(1, Math.min(amount, 64)));
        } catch (NumberFormatException e) {
            input.setAmount(1);
        }
    }

    // TODO Check for "player"
    @SuppressWarnings("deprecation")
    private static void setSkullFromPlayerName(ItemStack input, String tag) {
        if ((VersionUtils.usesVersionBetween("1.1.x", "1.12.x")
                ? input.getType().toString().equals("SKULL_ITEM")
                : input.getType().toString().equals("PLAYER_HEAD")
        ) && (!VersionUtils.usesVersionBetween("1.1.x", "1.12.x") || input.getDurability() == 3)
        ) {
            String playername = tag.split(":")[1];

            SkullMeta sm = (SkullMeta) input.getItemMeta();
            assert sm != null;

            if(VersionUtils.usesVersionBetween("1.1.x", "1.11.x")) sm.setOwner(playername);
            else sm.setOwningPlayer(Bukkit.getOfflinePlayer(playername));

            input.setItemMeta(sm);
        }
    }

    // TODO Check for "pattern"
    private static void setBannerPattern(ItemStack input, String tag) {
        if(input.getType().name().contains("BANNER")) {
            String pattern = tag.split(":")[1];
            String type = pattern.split(",")[0];
            String color = pattern.split(",")[1];

            BannerMeta bm = (BannerMeta) input.getItemMeta();
            assert bm != null;

            bm.addPattern(new Pattern(DyeColor.valueOf(color.toUpperCase()), PatternType.valueOf(type.toUpperCase())));

            input.setItemMeta(bm);
        }
    }

    // TODO Check for "urlCode"
    @SuppressWarnings("deprecation")
    private static ItemStack setUrlCode(ItemStack input, String tag) {
        if((VersionUtils.usesVersionBetween("1.1.x", "1.12.x")
                ? input.getType().toString().equals("SKULL_ITEM")
                : input.getType().toString().equals("PLAYER_HEAD")
        ) && (!VersionUtils.usesVersionBetween("1.1.x", "1.12.x") || input.getDurability() == 3)
        ) {
            final String code = tag.split(":")[1];

            if(code.startsWith("ey")) return SkullCreator.itemWithBase64(input, code);
            else return SkullCreator.itemWithUrl(input, code);
        }

        return input;
    }

    // TODO Check for "color"
    private static void setArmorColor(ItemStack input, String tag) {
        if (input.getType().toString().contains("LEATHER")) {
            if(input.hasItemMeta() && !(input.getItemMeta() instanceof LeatherArmorMeta)) return;

            final String[] colors = tag.split(":")[1].split(",");
            final LeatherArmorMeta lam = (LeatherArmorMeta) input.getItemMeta();
            assert lam != null;

            try {
                int red = Integer.parseInt(colors[0]);
                int green = Integer.parseInt(colors[1]);
                int blue = Integer.parseInt(colors[2]);

                try {
                    lam.setColor(Color.fromRGB(red, green, blue));
                    input.setItemMeta(lam);
                } catch (IllegalArgumentException e) {
                    lam.setColor(Color.fromRGB(0, 0, 0));
                    input.setItemMeta(lam);
                }

            } catch (NumberFormatException e) {
                lam.setColor(Color.fromRGB(0, 0, 0));
                input.setItemMeta(lam);
            }
        }
    }

    // TODO Check for name
    private static void setDisplayName(ItemStack input, String tag) {
        final ItemMeta im = input.getItemMeta();
        assert im != null;

        final String name = TextUtils.color(tag.substring(tag.indexOf(":") + 1)
                .replace("_", " ")
                .replace("{us}", "_"));

        im.setDisplayName(name);
        input.setItemMeta(im);
    }

    // TODO Check for lore
    private static void setLore(ItemStack input, String tag) {
        final List<String> lore = new ArrayList<>();
        final ItemMeta im = input.getItemMeta();
        assert im != null;

        if (tag.contains("|")) {
            final String[] lines = tag.substring(tag.indexOf(":") + 1).split("\\|");

            for (String line : lines) {
                final String action = line.replace("_", " ").replace("{us}", "_");
                lore.add(TextUtils.color(action));
            }
        } else {
            final String line = tag.substring(tag.indexOf(":") + 1);
            lore.add(TextUtils.color(line.replace("_", " ").replace("{us}", "_")));
        }

        im.setLore(lore);
        input.setItemMeta(im);
    }

    // TODO Check for enchantments
    private static void addEnchantment(ItemStack input, String tag) {
        final String enchantName = tag.split(":")[0].toLowerCase();

        if (enchants.containsKey(enchantName) || enchants.keySet().stream()
                .anyMatch(enchID -> enchID.replace("_", "").equalsIgnoreCase(enchantName))
        ) {
            final ItemMeta im = input.getItemMeta();
            assert im != null;

            if(enchants.keySet().stream()
                    .noneMatch(id -> id.replace("_", "").equalsIgnoreCase(enchantName)))
                return;

            final Enchantment enchantment = !enchants.containsKey(enchantName)
                    ? enchants.keySet().stream()
                        .filter(id -> id.replace("_", "").equalsIgnoreCase(enchantName))
                        .map(enchants::get).map(Enchantment.class::cast).findFirst().get()
                    : enchants.get(enchantName);

            try {
                final int level = tag.split(":").length >= 2
                        ? Integer.parseInt(tag.split(":")[1]) : 1;

                im.addEnchant(enchantment, level, true);
                input.setItemMeta(im);
            } catch (NumberFormatException ignored) {}
        }
    }

    // TODO Check for "hideFlags"
    private static void hideFlags(ItemStack input) {
        final ItemMeta im = input.getItemMeta();
        assert im != null;

        im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS,
                ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE);

        if(!VersionUtils.usesVersionBetween("1.1.x", "1.15.x"))
            im.addItemFlags(ItemFlag.HIDE_DYE);

        input.setItemMeta(im);
    }

    // TODO Check for "hideFlag"
    private static void hideFlag(ItemStack input, String tag) {
        final ItemMeta im = input.getItemMeta();
        assert im != null;

        try {
            String flagStr = tag.split(":")[1];

            if(!flagStr.startsWith("hide")) flagStr = "hide_" + flagStr;
            final ItemFlag flag = ItemFlag.valueOf(flagStr.toUpperCase());
            im.addItemFlags(flag);

            input.setItemMeta(im);
        } catch(IllegalArgumentException ignored) {}
    }

    // TODO Check for "customModelData"
    private static void setCustomModelData(ItemStack input, String tag) {
        if(!VersionUtils.usesVersionBetween("1.1.x", "1.13.x")) {
            final ItemMeta im = input.getItemMeta();
            assert im != null;

            try {
                im.setCustomModelData(Integer.parseInt(tag.split(":")[1]));
                input.setItemMeta(im);
            } catch(NumberFormatException ignored) {}
        }
    }

    // TODO Check for "unbreakable"
    private static ItemStack setUnbreakable(ItemStack input) {
        if (!VersionUtils.usesVersionBetween("1.1.x", "1.8.x")) {
            final ItemMeta im = input.getItemMeta();
            assert im != null;

            im.setUnbreakable(true);
            input.setItemMeta(im);

            return input;
        } else {
            return NBTEditor.set(input, (byte) 1, "Unbreakable");
        }
    }

    // TODO Check for "nbt-string"
    private static ItemStack setNBTString(ItemStack input, String tag) {
        if(tag.split(":").length < 3) return input;

        final String nbt = tag.split(":")[1];
        final String string = tag.split(":")[2]
                .replace("_", " ")
                .replace("{us}", "_");

        return NBTEditor.set(input, string, nbt);
    }

    // TODO Check for "nbt-<number>"
    private static ItemStack setNBTNumber(ItemStack input, NBTNumber type, String tag) {
        if(tag.split(":").length < 3) return input;
        final String nbt = tag.split(":")[1];

        try {
            switch(type) {
                case BYTE: return NBTEditor.set(input, Byte.parseByte(tag.split(":")[2]), nbt);
                case SHORT: return NBTEditor.set(input, Short.parseShort(tag.split(":")[2]), nbt);
                case INTEGER: return NBTEditor.set(input, Integer.parseInt(tag.split(":")[2]), nbt);
                case FLOAT: return NBTEditor.set(input, Float.parseFloat(tag.split(":")[2]), nbt);
                case DOUBLE: return NBTEditor.set(input, Double.parseDouble(tag.split(":")[2]), nbt);
            }
        } catch(NumberFormatException ignored) {}

        return input;
    }

    // TODO Check for "attribute"
    private static ItemStack setAttribute(ItemStack input, String tag) {
        final String val = tag.split(":")[1];
        final String newAtr = val.split("/")[0];
        String atr = val.split("/")[0];

        if(VersionUtils.usesVersionBetween("1.1.x", "1.15.x")) {
            atr = WordUtils.capitalizeFully(atr
                    .replace("_", " "))
                    .replace(" ", "");
            atr = Character.toString(atr.charAt(0)).toLowerCase() + atr.substring(1);
        }

        float atrVal;

        try {
            atrVal = Float.parseFloat(val.split("/")[1]);
        } catch (NumberFormatException ne) {
            atrVal = 0;
        }

        int operation;

        if(val.split("/").length <= 2) {
            operation = 0;
        } else {
            try {
                operation = Integer.parseInt(val.split("/")[2]);
            } catch (NumberFormatException ne) {
                operation = 0;
            }
        }

        String slot = !VersionUtils.usesVersionBetween("1.1.x", "1.12.x") ? "hand" : "mainhand";
        boolean hasSlot = true;

        if(val.split("/").length <= 3) hasSlot = false;
        else slot = val.split("/")[3];

        if(!VersionUtils.usesVersionBetween("1.1.x", "1.12.x")) {
            final ItemMeta im = input.getItemMeta();
            assert im != null;

            final AttributeModifier.Operation op = operation == 0
                    ? AttributeModifier.Operation.ADD_NUMBER
                    : operation == 1 ? AttributeModifier.Operation.ADD_SCALAR
                    : operation == 2 ? AttributeModifier.Operation.MULTIPLY_SCALAR_1
                    : AttributeModifier.Operation.ADD_NUMBER;

            if(hasSlot) {
                im.addAttributeModifier(Attribute.valueOf("GENERIC_" + newAtr.toUpperCase()),
                        new AttributeModifier(
                                UUID.randomUUID(),
                                "GENERIC_" + newAtr.toUpperCase(),
                                atrVal,
                                op,
                                EquipmentSlot.valueOf(slot.toUpperCase())
                        )
                );
            } else {
                im.addAttributeModifier(Attribute.valueOf("GENERIC_" + newAtr.toUpperCase()),
                        new AttributeModifier("GENERIC_" + newAtr.toUpperCase(), atrVal, op));
            }

            input.setItemMeta(im);
            return input;
        }

        final NBTEditor.NBTCompound compound = NBTEditor.getNBTCompound(input);

        if(atr.equals("attackDamage")) setAttackDamage = true;

        compound.set("generic." + atr, "tag", "AttributeModifiers", null, "AttributeName");
        compound.set("generic." + atr, "tag", "AttributeModifiers", attributeIndex, "Name");
        if(hasSlot) compound.set(slot, "tag", "AttributeModifiers", attributeIndex, "Slot");
        compound.set(operation, "tag", "AttributeModifiers", attributeIndex, "Operation");
        compound.set(atrVal, "tag", "AttributeModifiers", attributeIndex, "Amount");
        compound.set(new int[] { 0, 0, 0, 0 }, "tag", "AttributeModifiers", attributeIndex % 2 == 0 ? 0 : 1, "UUID");
        compound.set(99L, "tag", "AttributeModifiers", attributeIndex, "UUIDMost");
        compound.set(77530600L, "tag", "AttributeModifiers", attributeIndex, "UUIDLeast");

        attributeIndex++;

        if(attributeIndex == totalAttributeCount && !setAttackDamage) {
            for(ToolDamage tooldmg : ToolDamage.values()) {
                if(input.getType().name().equals(tooldmg.name())) {
                    compound.set("generic.attackDamage", "tag", "AttributeModifiers", null, "AttributeName");
                    compound.set("generic.attackDamage", "tag", "AttributeModifiers", attributeIndex, "Name");
                    compound.set("mainhand", "tag", "AttributeModifiers", attributeIndex, "Slot");
                    compound.set(0, "tag", "AttributeModifiers", attributeIndex, "Operation");
                    compound.set(tooldmg.get(), "tag", "AttributeModifiers", attributeIndex, "Amount");
                    compound.set(new int[] { 0, 0, 0, 0 }, "tag", "AttributeModifiers", attributeIndex % 2 == 0 ? 0 : 1, "UUID");
                    compound.set(99L, "tag", "AttributeModifiers", attributeIndex, "UUIDMost");
                    compound.set(77530600L, "tag", "AttributeModifiers", attributeIndex, "UUIDLeast");

                    attributeIndex++;
                    break;
                }
            }
        }

        return NBTEditor.getItemFromTag(compound);
    }

    // TODO Check for potion effects
    @SuppressWarnings("deprecation")
    private static void addEffect(ItemStack input, String tag, String tagCluster) {
        if(input.getType().equals(Material.POTION)) {
            final String[] v = tag.split("/");
            final String effect = v[0].split(":")[1];
            final String effectPower = v[1];
            final String effectDuration = v[2];

            short power;
            int duration;

            try {
                short POWER = Short.parseShort(effectPower);
                if (POWER > 256) power = 256;
                else power = POWER;
            } catch (NumberFormatException e) {
                power = 1;
            }

            try {
                int DURATION = Integer.parseInt(effectDuration);
                duration = Math.min(DURATION, 999999);
            } catch (NumberFormatException e) {
                duration = 120;
            }

            final PotionMeta im = (PotionMeta) input.getItemMeta();
            assert im != null;

            if (!tagCluster.contains("name:")) im.setDisplayName(TextUtils.color("&dCustom potion"));

            if (effect.contains(",")) {
                final String[] effectList = effect.split(",");
                for (String effect2 : effectList) addEffect(effect2, im, power, duration);
            } else {
                addEffect(effect, im, power, duration);

                if(itemID.equalsIgnoreCase("splash_potion")) {
                    final PotionEffect first = im.getCustomEffects().stream().findAny().get();
                    final String effStr = first.getType().getName();

                    final String eff = effStr.equals("HARM")
                            ? "INSTANT_DAMAGE"
                            : effStr.equals("HEAL") ? "INSTANT_HEAL" : effStr;

                    final Potion pot = new Potion(PotionType.valueOf(eff), power);
                    pot.setSplash(true);

                    if(!eff.contains("INSTANT_DAMAGE") && !eff.contains("HEAL"))
                        pot.setHasExtendedDuration(duration == 1);

                    pot.apply(input);
                    return;
                }
            }

            input.setItemMeta(im);
        }
    }

    public enum NBTNumber {
        BYTE, SHORT, INTEGER, FLOAT, DOUBLE
    }


    private static void addEffect(String effect, PotionMeta im, short power, int duration) {
        if (!VersionUtils.usesVersionBetween("1.1.x", "1.8.x")) {
            String[] v19 = new String[] {"levitation", "glowing", "luck", "unluck"};
            if(compareAndAdd(effect, v19, im, duration, power)) return;
        }

        if(!VersionUtils.usesVersionBetween("1.1.x", "1.12.x")) {
            String[] v113 = new String[] {"slow_falling", "conduit_power", "dolphins_grace"};
            if(compareAndAdd(effect, v113, im, duration, power)) return;
        }

        if(!VersionUtils.usesVersionBetween("1.1.x", "1.12.x")) {
            String[] v114 = new String[] {"bad_omen", "hero_of_the_village"};
            if(compareAndAdd(effect, v114, im, duration, power)) return;
        }

        switch (effect) {
            case "speed": im.addCustomEffect(new PotionEffect(PotionEffectType.SPEED, duration, power), false); return;
            case "slowness": im.addCustomEffect(new PotionEffect(PotionEffectType.SLOW, duration, power), false); return;
            case "haste": im.addCustomEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, duration, power), false); return;
            case "mining_fatigue": im.addCustomEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, duration, power), false); return;
            case "strength": im.addCustomEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, power), false); return;
            case "instant_health": im.addCustomEffect(new PotionEffect(PotionEffectType.HEAL, duration, power), false); return;
            case "instant_damage": im.addCustomEffect(new PotionEffect(PotionEffectType.HARM, duration, power), false); return;
            case "jump_boost": im.addCustomEffect(new PotionEffect(PotionEffectType.JUMP, duration, power), false); return;
            case "nausea": im.addCustomEffect(new PotionEffect(PotionEffectType.CONFUSION, duration, power), false); return;
            case "regeneration": im.addCustomEffect(new PotionEffect(PotionEffectType.REGENERATION, duration, power), false); return;
            case "resistance": im.addCustomEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, power), false); return;
            case "fire_resistance": im.addCustomEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, power), false); return;
            case "water_breathing": im.addCustomEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, duration, power), false); return;
            case "invisibility": im.addCustomEffect(new PotionEffect(PotionEffectType.INVISIBILITY, duration, power), false); return;
            case "blindness": im.addCustomEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, power), false); return;
            case "night_vision": im.addCustomEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, duration, power), false); return;
            case "hunger": im.addCustomEffect(new PotionEffect(PotionEffectType.HUNGER, duration, power), false); return;
            case "weakness": im.addCustomEffect(new PotionEffect(PotionEffectType.WEAKNESS, duration, power), false); return;
            case "poison": im.addCustomEffect(new PotionEffect(PotionEffectType.POISON, duration, power), false); return;
            case "wither": im.addCustomEffect(new PotionEffect(PotionEffectType.WITHER, duration, power), false); return;
            case "health_boost": im.addCustomEffect(new PotionEffect(PotionEffectType.HEALTH_BOOST, duration, power), false); return;
            case "absorption": im.addCustomEffect(new PotionEffect(PotionEffectType.ABSORPTION, duration, power), false); return;
            case "saturation": im.addCustomEffect(new PotionEffect(PotionEffectType.SATURATION, duration, power), false);
        }
    }

    public enum ToolDamage {
        WOOD_SWORD(4, 4),
        GOLD_SWORD(4, 4),
        STONE_SWORD(5, 5),
        IRON_SWORD(6, 6),
        DIAMOND_SWORD(7, 7),
        NETHERITE_SWORD(8, 8),
        WOOD_AXE(3, 7),
        GOLD_AXE(3, 7),
        STONE_AXE(4, 9),
        IRON_AXE(5, 9),
        DIAMOND_AXE(6, 9),
        NETHERITE_AXE(10, 10),
        WOOD_PICKAXE(2, 2),
        GOLD_PICKAXE(2, 2),
        STONE_PICKAXE(3, 3),
        IRON_PICKAXE(4, 4),
        DIAMOND_PICKAXE(5, 5),
        NETHERITE_PICKAXE(6, 6),
        WOOD_SPADE(1, 2),
        GOLD_SPADE(1, 2),
        STONE_SPADE(2, 3),
        IRON_SPADE(3, 3),
        DIAMOND_SPADE(4, 4),
        NETHERITE_SPADE(5, 5);

        int pre, post;

        ToolDamage(int pre, int post) {
            this.pre = pre;
            this.post = post;
        }

        public int pre19() {
            return pre;
        }
        public int post19() {
            return post;
        }
        public int get() {
            return VersionUtils.usesVersionBetween("1.1.x", "1.8.x") ? pre19() : post19();
        }
    }

    private static boolean compareAndAdd(String effect, String[] toCompare, PotionMeta im, int d, int p) {
        for(String effID : toCompare) if(effect.equals(effID)) {
            PotionEffectType ptype = PotionEffectType.getByName(effID);
            if(ptype != null) im.addCustomEffect(new PotionEffect(ptype, d, p), false);
            return true;
        }

        return false;
    }
}
