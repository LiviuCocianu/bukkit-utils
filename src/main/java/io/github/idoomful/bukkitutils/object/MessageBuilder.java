package io.github.idoomful.bukkitutils.object;

import io.github.idoomful.bukkitutils.statics.ItemUtils;
import io.github.idoomful.bukkitutils.statics.TextUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class MessageBuilder {
    private HashMap<String, DynamicComponent> placeholders;
    private String message;
    private TextComponent processedMessage;
    private Player player = null;
    private Function<String, String> alterFunction;

    public MessageBuilder(String message) {
        this.message = message;
        processedMessage = null;
        placeholders = new HashMap<>();
    }

    public MessageBuilder(String message, Player player) {
        this.message = message;
        processedMessage = null;
        placeholders = new HashMap<>();
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HashMap<String, DynamicComponent> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(HashMap<String, DynamicComponent> placeholders) {
        this.placeholders = placeholders;
    }

    public MessageBuilder addComponent(DynamicComponent component) {
        placeholders.put(component.getPlaceholder(), component);
        return this;
    }

    public TextComponent getProcessedMessage() {
        return processedMessage;
    }

    public CommitedMessage commit(TextUtils.ColorType chatColorType) {
        final List<String> segments = TextUtils.segmentByStrings(message, placeholders.keySet());
        processedMessage = new TextComponent();

        for(String segment : segments) {
            BaseComponent[] comp;

            if(placeholders.containsKey(segment)) {
                DynamicComponent dyn = placeholders.get(segment);
                comp = colorizeComponent(dyn.getReplacement(), dyn.getColorType());

                if (dyn.listensToHover()) {
                    HoverEvent hover;

                    if(dyn.getOnHover().getItem() != null) {
                        final String itemJson = ItemUtils.convertItemStackToJson(dyn.getOnHover().getItem());

                        final BaseComponent[] componentItem = new BaseComponent[] {
                                new TextComponent(itemJson)
                        };

                        hover = new HoverEvent(HoverEvent.Action.SHOW_ITEM, componentItem);
                    } else {
                        final String hoverText = String.join("\n", dyn.getOnHover().getText());
                        hover = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hexComponent(hoverText));
                    }

                    for (BaseComponent baseComponent : comp)
                        baseComponent.setHoverEvent(hover);
                }

                if (dyn.listensToClick()) {
                    for (BaseComponent baseComponent : comp)
                        baseComponent.setClickEvent(new ClickEvent(dyn.getOnClick().getAction(),
                                TextUtils.colorlessPlaceholder(player, dyn.getOnClick().getText())));
                }
            } else {
                comp = colorizeComponent(segment, chatColorType);
            }

            if(alterFunction != null) {
                comp = Stream.of(comp).peek(cmp -> {
                    if(cmp instanceof TextComponent) {
                        TextComponent tcmp = (TextComponent) cmp;
                        tcmp.setText(alterFunction.apply(tcmp.getText()));
                    }
                }).toArray(BaseComponent[]::new);
            }

            Stream.of(comp).forEach(processedMessage::addExtra);
        }

        return new CommitedMessage(processedMessage);
    }

    public TextComponent merge(MessageBuilder builder) {
        final TextComponent comp = new TextComponent();
        comp.addExtra(getProcessedMessage());
        comp.addExtra(builder.getProcessedMessage());
        return comp;
    }

    public TextComponent merge(Collection<MessageBuilder> builders) {
        final TextComponent comp = new TextComponent();
        comp.addExtra(getProcessedMessage());
        builders.forEach(bu -> comp.addExtra(bu.getProcessedMessage()));
        return comp;
    }

    public static void sendOne(TextComponent comp, CommandSender receiver) {
        if(receiver instanceof ConsoleCommandSender)
            receiver.sendMessage(comp.toLegacyText());
        else receiver.spigot().sendMessage(comp);
    }

    public static void sendMore(TextComponent comp, Collection<CommandSender> receivers) {
        for(CommandSender cs : receivers)
            MessageBuilder.sendOne(comp, cs);
    }

    public void alterComponentTexts(Function<String, String> alterFunction) {
        this.alterFunction = alterFunction;
    }

    private BaseComponent[] colorizeComponent(String text, TextUtils.ColorType color) {
        switch(color) {
            case STANDARD:
                return standardComponent(text);
            case HEX:
                return hexComponent(text);
            default:
                return new ComponentBuilder().append(text).create();
        }
    }

    public BaseComponent[] hexComponent(String text) {
        if(text.matches("^\\s+$"))
            return new ComponentBuilder(" ").create();

        final Pattern pat = Pattern.compile("(\\[?#[a-fA-F0-9]{6}]?)");
        final Matcher mat = pat.matcher(text);
        final List<String> hexCodes = new ArrayList<>();

        while(mat.find())
            hexCodes.add(mat.group());

        final List<String> segments = TextUtils.segmentByStrings(text, hexCodes);
        final ComponentBuilder cb = new ComponentBuilder();

        boolean colored = false;
        for(int i = 0; i < segments.size(); i++) {
            if(colored) {
                colored = false;
                continue;
            }

            final String segment = segments.get(i);

            if(segment.matches("(\\[?#[a-fA-F0-9]{6}]?)")) {
                if((i + 1) < segments.size()) {
                    final String next = segments.get(i + 1);
                    cb.append(TextUtils.placeholder(player, next))
                            .color(ChatColor.of(segment.replaceAll("[\\[\\]]", "")));
                }

                colored = true;
            } else {
                cb.append(TextUtils.placeholder(player, segment));
            }
        }

        return cb.create();
    }

    private BaseComponent[] standardComponent(String text) {
        return new ComponentBuilder()
                .append(ChatColor.translateAlternateColorCodes('&', text))
                .create();
    }

    public static class CommitedMessage {
        private final TextComponent processed;

        public CommitedMessage(TextComponent comp) {
            processed = comp;
        }

        public void sendOne(Player player) {
            if(processed != null)
                player.spigot().sendMessage(processed);
        }

        public void sendMore(Collection<Player> players) {
            for(Player player : players)
                sendOne(player);
        }

        public String plainString() {
            return processed.toLegacyText();
        }
    }
}
