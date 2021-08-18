package io.github.idoomful.bukkitutils.object;

import io.github.idoomful.bukkitutils.statics.TextUtils;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class ClickableMessageProcess {
	private final String message;
	private final Player receiver;
	private final FileConfiguration source;
	private TextComponent toSend;

	private String clickEventType, clickEventText;
	private String hoverEventType, hoverEventText;
	private boolean clickSetText = false, hoverSetText = false;
	private boolean clickIsArray = false, hoverIsArray = false;
	private String[] clickSetText_ph_arr, clickSetText_repl_arr, hoverSetText_ph_arr, hoverSetText_repl_arr;
	private String clickSetText_ph_str = "", clickSetText_repl_str = "", hoverSetText_ph_str = "", hoverSetText_repl_str = "";

	public ClickableMessageProcess(Player receiver, String message, FileConfiguration source) {
        this.message = TextUtils.placeholder(receiver, message);
		this.receiver = receiver;
		this.source = source;
	}
	
	public final ClickableMessageProcess build(UnaryOperator<String> editReplacement) {
		StringBuilder words = new StringBuilder();
		TextComponent finalProduct = new TextComponent("");
		boolean componentAfterComponent = false;

		for (String word : message.split(" ")) {
			List<String> availablePlaceholders = new ArrayList<>(source.getConfigurationSection("events").getKeys(false));

			if (!availablePlaceholders.contains(word)) {
				words.append(word).append(" ");
				componentAfterComponent = false;
			} else {
				finalProduct.addExtra(TextUtils.color(words.toString()));
				if (componentAfterComponent) finalProduct.addExtra(" ");

				finalProduct.addExtra(createPlaceholderComponent(word, editReplacement));
				words = new StringBuilder();
				componentAfterComponent = true;
			}
		}
		
		if(!words.toString().equals("")) {
			finalProduct.addExtra(" ");
			finalProduct.addExtra(TextUtils.color(words.toString()));
		}

		toSend = finalProduct;
		return this;
	}
	
	private TextComponent createPlaceholderComponent(String placeholder, UnaryOperator<String> editReplacement) {
		String compText;

		final String replacement = source.getString("events." + placeholder + ".replacement");
		final boolean hasHoverEvent = source.getConfigurationSection("events." + placeholder + ".hover-event") != null;
		final boolean hasClickEvent = source.getConfigurationSection("events." + placeholder + ".click-event") != null;

		if(replacement == null) compText = "(string not found)";
		else compText = replacement;

		TextComponent component;

		if(editReplacement == null) component = new TextComponent(TextUtils.color(compText));
		else component = new TextComponent(TextUtils.color(editReplacement.apply(compText)));
		
		if(hasHoverEvent) {
			if(hoverEventType == null) hoverEventType = source.getString("events." + placeholder + ".hover-event.type");

			if (hoverEventType.equalsIgnoreCase("SHOW_TEXT")) {
				if (hoverEventText == null) hoverEventText = TextUtils.color(source.getString("events." + placeholder + ".hover-event.text"));

				if (hoverIsArray) {
					for (int i = 0; i < hoverSetText_ph_arr.length; i++) {
						final String text_ = hoverEventText.replace(hoverSetText_ph_arr[i], hoverSetText_repl_arr[i]);
						if (hoverSetText) hoverEventText = TextUtils.color(text_);
					}
				} else {
					final String text_ = hoverEventText.replace(hoverSetText_ph_str, hoverSetText_repl_str);
					if (hoverSetText) hoverEventText = TextUtils.color(text_);
				}

				component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(TextUtils.color(hoverEventText))));
			}
		}
		
		if(hasClickEvent) {
			if(clickEventType == null) clickEventType = source.getString("events." + placeholder + ".click-event.type");
			if(clickEventText == null) clickEventText = source.getString("events." + placeholder + ".click-event.text");
			
			if (clickIsArray) {
				for(int i = 0; i < clickSetText_ph_arr.length; i++) {
                    if(clickSetText) clickEventText = clickEventText.replace(clickSetText_ph_arr[i], clickSetText_repl_arr[i]);
                }
			} else {
				if(clickSetText) clickEventText = clickEventText.replace(clickSetText_ph_str, clickSetText_repl_str);
			}
			
			switch (clickEventType) {
				case "OPEN_URL":
					component.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, clickEventText));
					break;
				case "RUN_COMMAND":
				    if(clickEventText.contains("[message]")) {
				        final String message = clickEventText
								.replace("[message] ", "")
								.replace("[message]", "");

				        String cmd = "/displayclickmessage ";
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, cmd + message));
                    } else {
                        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickEventText));
                    }
					break;
				case "SUGGEST_COMMAND":
					component.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, clickEventText));
					break;
			}
		}
		return component;
	}
	
	public final ClickEventManager getClickEvent() {
		return new ClickEventManager();
	}
	
	public final HoverEventManager getHoverEvent() {
		return new HoverEventManager();
	}
	
	public final void send() {
		receiver.spigot().sendMessage(toSend);
	}
	
	public class ClickEventManager {
		public ClickEventManager setType(ClickEvent.Action type) {
			switch(type) {
				case OPEN_URL: clickEventType = "OPEN_URL"; break;
				case RUN_COMMAND: clickEventType = "RUN_COMMAND"; break;
				case SUGGEST_COMMAND: clickEventType = "SUGGEST_COMMAND"; break;
				default: break;
			}
			return this;
		}
		public ClickEventManager setText(String text) {
			clickEventText = text;
			return this;
		}
		
		public ClickEventManager setPlaceholderInText(String[] placeholder, String[] replacement) {
			clickSetText = true;
			clickIsArray = true;
			clickSetText_ph_arr = placeholder;
			clickSetText_repl_arr = replacement;
			return this;
		}
		
		public ClickEventManager setPlaceholderInText(String placeholder, String replacement) {
			clickSetText = true;
			clickSetText_ph_str = placeholder;
			clickSetText_repl_str = replacement;
			return this;
		}
		public ClickableMessageProcess saveChanges() {
			return ClickableMessageProcess.this;
		}
	}
	
	public class HoverEventManager {
		public HoverEventManager setType(HoverEvent.Action type) {
			if (type == HoverEvent.Action.SHOW_TEXT) hoverEventType = "SHOW_TEXT";
			return this;
		}
		public HoverEventManager setText(String text) {
			hoverEventText = text;
			return this;
		}
		
		public HoverEventManager setPlaceholderInText(String[] placeholder, String[] replacement) {
			hoverSetText = true;
			hoverIsArray = true;
			hoverSetText_ph_arr = placeholder;
			hoverSetText_repl_arr = replacement;
			return this;
		}
		
		public HoverEventManager setPlaceholderInText(String placeholder, String replacement) {
			hoverSetText = true;
			hoverSetText_ph_str = placeholder;
			hoverSetText_repl_str = replacement;
			return this;
		}
		public ClickableMessageProcess saveChanges() {
			return ClickableMessageProcess.this;
		}
	}
}
