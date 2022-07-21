package io.github.idoomful.bukkitutils.object;

import io.github.idoomful.bukkitutils.statics.TextUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class DynamicComponent {
    private String placeholder;
    private String replacement;
    private TextUtils.ColorType color;

    private HoverEvent onHover;
    private ClickEvent onClick;

    public DynamicComponent() {
        this.placeholder = "";
        this.replacement = "";
        this.color = TextUtils.ColorType.NONE;

        onHover = null;
        onClick = null;
    }

    public DynamicComponent(String placeholder, String replacement, TextUtils.ColorType color) {
        this.placeholder = placeholder;
        this.replacement = replacement;
        this.color = color;

        onHover = null;
        onClick = null;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public String getReplacement() {
        return replacement;
    }

    public HoverEvent getOnHover() {
        return onHover;
    }

    public ClickEvent getOnClick() {
        return onClick;
    }

    public TextUtils.ColorType getColorType() {
        return color;
    }

    public boolean listensToHover() {
        return onHover != null;
    }

    public boolean listensToClick() {
        return onClick != null;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public void setReplacement(String replacement) {
        this.replacement = replacement;
    }

    public void setOnHover(HoverEvent onHover) {
        this.onHover = onHover;
    }

    public void setOnClick(ClickEvent onClick) {
        this.onClick = onClick;
    }

    public void setColorType(TextUtils.ColorType color) {
        this.color = color;
    }

    public static class ClickEvent {
        private String text;
        private Action action;

        public ClickEvent() {
            text = "";
        }

        public ClickEvent(String text, Action action) {
            this.text = text;
            this.action = action;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Action getAction() {
            return action;
        }

        public void setAction(Action action) {
            this.action = action;
        }
    }

    public static class HoverEvent {
        private List<String> text;
        private ItemStack item;

        public HoverEvent() {
            text = new ArrayList<>();
            item = null;
        }

        public HoverEvent(List<String> text) {
            this.text = text;
            item = null;
        }

        public HoverEvent(ItemStack itemToShow) {
            item = itemToShow;
        }

        public List<String> getText() {
            return text;
        }

        public void setText(List<String> text) {
            this.text = text;
        }

        public ItemStack getItem() {
            return item;
        }

        public void setItem(ItemStack item) {
            this.item = item;
        }
    }
}
