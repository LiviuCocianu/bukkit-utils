package io.github.idoomful.bukkitutils.statics;

import dev.dbassett.skullcreator.SkullCreator;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Material;
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
    @SuppressWarnings("deprecation")
    public static ItemStack build(String value) {
        ItemStack result = new ItemStack(Material.STONE, 1);
        String ID = "";
        int attributeIndex = 0;
        int totalAttributeCount = 0;
        boolean setAttackDamage = false;

        String[] data = value.split(" ");

        final Map<String, Enchantment> enchants = new LinkedHashMap<>();
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

        for (String aData : data) {
            // TODO Check for ID
            if (aData.startsWith("id:")) {
                String id = aData.substring(aData.indexOf(":") + 1);

                if (id.contains(":")) {
                    String[] separate = id.split(":");

                    id = separate[0];
                    int damage = Integer.parseInt(separate[1]);

                    result.setType(MaterialUtils.getMaterialByID(id));

                    try {
                        if (damage > 9999) {
                            result.setDurability((short) 1);
                            continue;
                        }
                        result.setDurability((short) damage);

                    } catch (NumberFormatException e) {
                        result.setDurability((short) 1);
                    }
                } else {
                    ID = id;

                    if(ID.equalsIgnoreCase("splash_potion")) {
                        result.setType(MaterialUtils.getMaterialByID("potion"));
                    } else {
                        result.setType(MaterialUtils.getMaterialByID(id));
                    }
                    continue;
                }
            }
            // TODO Check for amount
            if (aData.startsWith("amount:")) {
                try {
                    int amount = Integer.parseInt(aData.split(":")[1]);
                    result.setAmount(Math.max(1, Math.min(amount, 64)));
                } catch (NumberFormatException e) {
                    result.setAmount(1);
                }
            }

            // TODO Check for "player"
            if (aData.startsWith("player:") && (VersionUtils.usesVersionBetween("1.1.x", "1.12.x")
                    ? result.getType().toString().equals("SKULL_ITEM")
                    : result.getType().toString().equals("PLAYER_HEAD")
            ) && (!VersionUtils.usesVersionBetween("1.1.x", "1.12.x") || result.getDurability() == 3)
            ) {
                String playername = aData.split(":")[1];

                SkullMeta sm = (SkullMeta) result.getItemMeta();
                assert sm != null;

                if(VersionUtils.usesVersionBetween("1.1.x", "1.11.x")) sm.setOwner(playername);
                else sm.setOwningPlayer(Bukkit.getOfflinePlayer(playername));

                result.setItemMeta(sm);
            }

            // TODO Check for "pattern"
            if (aData.startsWith("pattern:") && result.getType().name().contains("BANNER")) {
                String pattern = aData.split(":")[1];
                String type = pattern.split(",")[0];
                String color = pattern.split(",")[1];

                BannerMeta bm = (BannerMeta) result.getItemMeta();
                assert bm != null;

                bm.addPattern(new Pattern(DyeColor.valueOf(color.toUpperCase()), PatternType.valueOf(type.toUpperCase())));

                result.setItemMeta(bm);
            }

            // TODO Check for "urlCode"
            if((aData.startsWith("url-code:") || aData.startsWith("urlCode:"))
                    && (VersionUtils.usesVersionBetween("1.1.x", "1.12.x")
                    ? result.getType().toString().equals("SKULL_ITEM")
                    : result.getType().toString().equals("PLAYER_HEAD")
            ) && (!VersionUtils.usesVersionBetween("1.1.x", "1.12.x") || result.getDurability() == 3)
            ) {
                String code = aData.split(":")[1];

                if(code.startsWith("ey")) result = SkullCreator.itemWithBase64(result, code);
                else result = SkullCreator.itemWithUrl(result, code);
            }

            // TODO Check for "color"
            if (aData.startsWith("color:") && (result.getType().toString().contains("LEATHER"))) {
                String[] colors = aData.split(":")[1].split(",");
                LeatherArmorMeta lam = (LeatherArmorMeta) result.getItemMeta();
                assert lam != null;

                try {
                    int red = Integer.parseInt(colors[0]);
                    int green = Integer.parseInt(colors[1]);
                    int blue = Integer.parseInt(colors[2]);

                    try {
                        lam.setColor(Color.fromRGB(red, green, blue));
                        result.setItemMeta(lam);
                    } catch (IllegalArgumentException e) {
                        lam.setColor(Color.fromRGB(0, 0, 0));
                        result.setItemMeta(lam);
                    }

                } catch (NumberFormatException e) {
                    lam.setColor(Color.fromRGB(0, 0, 0));
                    result.setItemMeta(lam);
                }
            }

            // TODO Check for name
            if (aData.startsWith("name:")) {
                ItemMeta im = result.getItemMeta();
                assert im != null;

                String name = aData.substring(aData.indexOf(":") + 1);
                name = TextUtils.color(name.replace("_", " ").replace("{us}", "_"));

                im.setDisplayName(name);
                result.setItemMeta(im);
            }

            // TODO Check for lore
            if (aData.startsWith("lore:")) {
                List<String> lore = new ArrayList<>();
                ItemMeta im = result.getItemMeta();
                assert im != null;

                if(aData.contains("|")) {
                    String[] lines = aData.substring(aData.indexOf(":") + 1).split("\\|");

                    for (String line : lines) {
                        String action = line.replace("_", " ").replace("{us}", "_");
                        lore.add(TextUtils.color(action));
                    }
                } else {
                    final String line = aData.substring(aData.indexOf(":") + 1);
                    lore.add(TextUtils.color(line.replace("_", " ").replace("{us}", "_")));
                }

                im.setLore(lore);
                result.setItemMeta(im);
            }

            // TODO Check for enchantments
            if(aData.contains(":")) {
                for (int n = 0; n < enchants.size(); n++) {
                    String property = aData.split(":")[0];
                    ItemMeta im = result.getItemMeta();
                    assert im != null;

                    final List<String> enchantIDs = new ArrayList<>(enchants.keySet());

                    final String enchantID = enchantIDs.get(n);
                    final Enchantment enchantment = enchants.get(enchantID);

                    if (enchantID.equalsIgnoreCase(property)
                            || (enchantID.replace("_", "").equalsIgnoreCase(property))
                    ) {
                        try {
                            int level;
                            if(aData.split(":").length < 2) level = 1;
                            else level = Integer.parseInt(aData.split(":")[1]);

                            im.addEnchant(enchantment, level, true);
                            result.setItemMeta(im);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }

            // TODO Check for "hideFlags"
            if (aData.equalsIgnoreCase("hide-flags") || aData.equalsIgnoreCase("hideFlags")) {
                ItemMeta im = result.getItemMeta();
                assert im != null;

                im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS,
                        ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_UNBREAKABLE);

                if(!VersionUtils.usesVersionBetween("1.1.x", "1.15.x")) im.addItemFlags(ItemFlag.HIDE_DYE);

                result.setItemMeta(im);
            }

            // TODO Check for "hideFlag"
            if (aData.startsWith("hide-flag:") || aData.startsWith("hideFlag:")) {
                ItemMeta im = result.getItemMeta();
                assert im != null;
                String flagStr = aData.split(":")[1];

                try {
                    if(!flagStr.startsWith("hide")) flagStr = "hide_" + flagStr;
                    ItemFlag flag = ItemFlag.valueOf(flagStr.toUpperCase());
                    im.addItemFlags(flag);

                    result.setItemMeta(im);
                } catch(IllegalArgumentException ia) {
                    continue;
                }
            }

            // TODO Check for "customModelData"
            if (aData.startsWith("customModelData:")) {
                if(!VersionUtils.usesVersionBetween("1.1.x", "1.13.x")) {
                    ItemMeta im = result.getItemMeta();
                    assert im != null;

                    try {
                        int model = Integer.parseInt(aData.split(":")[1]);
                        im.setCustomModelData(model);
                        result.setItemMeta(im);
                    } catch(NumberFormatException ignored) {}
                }
            }

            // TODO Check for "unbreakable"
            if (aData.equalsIgnoreCase("unbreakable")) {
                ItemMeta im = result.getItemMeta();
                assert im != null;

                if (!VersionUtils.usesVersionBetween("1.1.x", "1.8.x"))
                    im.setUnbreakable(true);

                result.setItemMeta(im);

                if (VersionUtils.usesVersionBetween("1.1.x", "1.8.x"))
                    result = NBTEditor.set(result, (byte) 1, "Unbreakable");
            }

            // TODO Check for "nbt-string"
            if (aData.startsWith("nbt-string:") || aData.startsWith("nbtString:")) {
                if(aData.split(":").length < 3) continue;

                String tag = aData.split(":")[1];
                String string = aData.split(":")[2].replace("_", " ").replace("{us}", "_");

                result = NBTEditor.set(result, string, tag);
            }

            // TODO Check for "nbt-int"
            if (aData.startsWith("nbt-int:") || aData.startsWith("nbtInt:")) {
                if(aData.split(":").length < 3) continue;
                String tag = aData.split(":")[1];

                try {
                    int integer = Integer.parseInt(aData.split(":")[2]);
                    result = NBTEditor.set(result, integer, tag);
                } catch(NumberFormatException ignored) {}
            }

            // TODO Check for "nbt-float"
            if (aData.startsWith("nbt-float:") || aData.startsWith("nbtFloat:")) {
                if(aData.split(":").length < 3) continue;
                String tag = aData.split(":")[1];

                try {
                    float floating = Float.parseFloat(aData.split(":")[2]);
                    result = NBTEditor.set(result, floating, tag);
                } catch(NumberFormatException ignored) {}
            }

            // TODO Check for "nbt-double"
            if (aData.startsWith("nbt-double:") || aData.startsWith("nbtDouble:")) {
                if(aData.split(":").length < 3) continue;
                String tag = aData.split(":")[1];

                try {
                    double doubl = Double.parseDouble(aData.split(":")[2]);
                    result = NBTEditor.set(result, doubl, tag);
                } catch(NumberFormatException ignored) {}
            }

            // TODO Check for "attribute"
            if(aData.startsWith("attribute:")) {
                String val = aData.split(":")[1];
                String newAtr = val.split("/")[0];
                String atr = val.split("/")[0];

                if(VersionUtils.usesVersionBetween("1.1.x", "1.15.x")) {
                    atr = WordUtils.capitalizeFully(atr.replace("_", " "))
                            .replace(" ", "");
                    atr = (atr.charAt(0) + "").toLowerCase() + atr.substring(1);
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

                String slot = "mainhand";
                boolean hasSlot = true;

                if(val.split("/").length <= 3) {
                    hasSlot = false;
                } else {
                    slot = val.split("/")[3];
                }

                if(!VersionUtils.usesVersionBetween("1.1.x", "1.12.x")) {
                    ItemMeta im = result.getItemMeta();
                    assert im != null;

                    AttributeModifier.Operation op = operation == 0
                            ? AttributeModifier.Operation.ADD_NUMBER
                            : operation == 1 ? AttributeModifier.Operation.ADD_SCALAR
                            : operation == 2 ? AttributeModifier.Operation.MULTIPLY_SCALAR_1
                            : AttributeModifier.Operation.ADD_NUMBER;

                    slot = "hand";

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

                    result.setItemMeta(im);
                    continue;
                }

                NBTEditor.NBTCompound compound = NBTEditor.getNBTCompound(result);

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
                        if(result.getType().name().equals(tooldmg.name())) {
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

                result = NBTEditor.getItemFromTag(compound);
            }

            // TODO Check for potion effects
            if (aData.startsWith("effect:") && result.getType().equals(Material.POTION)) {
                String[] v = aData.split("/");
                String effect = v[0].split(":")[1];
                String effectPower = v[1];
                String effectDuration = v[2];

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

                PotionMeta im = (PotionMeta) result.getItemMeta();

                if (!value.contains("name:")) im.setDisplayName(TextUtils.color("&dCustom potion"));

                if (effect.contains(",")) {
                    String[] effectList = effect.split(",");
                    for (String effect2 : effectList) addEffect(effect2, im, power, duration);
                } else {
                    addEffect(effect, im, power, duration);

                    if(ID.equalsIgnoreCase("splash_potion")) {
                        PotionEffect first = im.getCustomEffects().stream().findAny().get();
                        String effStr = first.getType().getName();

                        String eff = effStr.equals("HARM")
                                ? "INSTANT_DAMAGE"
                                : effStr.equals("HEAL") ? "INSTANT_HEAL" : effStr;

                        Potion pot = new Potion(PotionType.valueOf(eff), power);
                        pot.setSplash(true);

                        if(!eff.contains("INSTANT_DAMAGE") && !eff.contains("HEAL"))
                            pot.setHasExtendedDuration(duration == 1);

                        pot.apply(result);
                        continue;
                    }
                }

                result.setItemMeta(im);
            }
        }
        return result;
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
