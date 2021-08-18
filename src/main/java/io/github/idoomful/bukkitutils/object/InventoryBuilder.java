package io.github.idoomful.bukkitutils.object;

import io.github.idoomful.bukkitutils.statics.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

/*
 * How to use:
 *
 * - Create a new instance of InventoryBuilder and give it the inventory you want to affect as the argument
 * - Call setConfigItemList(.., ..), which takes a list of strings with symbols and their associated item that looks like this:
 *
 *   items:                               "A" will represent the item in the layout later
 *     'A': "id:stone name:Example_item"   I recommend you use only one character and not something like "cool_stone"
 *                                         It will look better in the layout when you have just one character
 *
 *   .. and a player, if you have any PAPI placeholders in the items' name/lore, if not, just pass null
 * - Call setConfigItemArrangement(..), which takes a list of strings that represent the layout of the inventory
 *   The layout is defined through arranging the symbols from before in desired slots, here's an example:
 *
 *   layout:
 *   - "- - - - - - - - -"      Here we defined the layout for an inventory with 3 rows
 *   - "- - A - A - A - -"      Empty slots are marked with dashes. ALWAYS HAVE 9 SYMBOLS SEPARATED BY A SPACE NO MATTER WHAT
 *   - "- - - - - - - - -"      Place the symbol associates with the item in the item list to put the item in the slot
 *
 * All done! The items will be automatically set after you executed the last step
 */

public class InventoryBuilder {
	private final Map<String, ItemStack> symbolMatching = new HashMap<>();
    private final FileConfiguration source;
	private Inventory inventory;

	private PlayerInventory playerInventory;
	private Inventory playerInv;
	private final boolean isPlayerInv;
	
	public InventoryBuilder(Inventory inventory, FileConfiguration source) {
		this.inventory = inventory;
		this.source = source;
		isPlayerInv = false;
	}

	public InventoryBuilder(PlayerInventory playerInventory, FileConfiguration source) {
	    this.playerInventory = playerInventory;
	    this.source = source;
	    playerInv = Bukkit.createInventory(null, 9 * 4, "");
	    isPlayerInv = true;
    }
	
	public void setItems(String ... symbols) {
		if((isPlayerInv ? playerInv.getSize() : inventory.getSize()) < symbols.length) {
			return;
		}
		
		for (int i = 0; i < symbols.length; i++) {
			if (symbolMatching.containsKey(symbols[i])) {
				ItemStack item = symbolMatching.get(symbols[i]);
				if (item.getType().equals(Material.AIR)) continue;
				if(isPlayerInv) playerInv.setItem(i, item);
				else inventory.setItem(i, item);
			}
		}
		symbolMatching.clear();
	}

    public void setConfigItemArrangement(String inventoryName) {
        int index = 0;
        List<String> arrangements = source.getStringList("inventories." + inventoryName + ".layout");

        for(String row : arrangements) {
            if(index >= (isPlayerInv ? playerInv.getSize() : inventory.getSize())) {
                return;
            }

            for(String ch : row.split(" ")) {
                if(ch.equals("-")) {
                    index += 1;
                    continue;
                }
                if (symbolMatching.containsKey(ch)) {
                    if(isPlayerInv) playerInv.setItem(index++, symbolMatching.get(ch));
                    else inventory.setItem(index++, symbolMatching.get(ch));
                }
            }
        }
        symbolMatching.clear();
    }

    public void setItemStack(String symbol, ItemStack item) {
        symbolMatching.put(symbol, item);
    }

    public void setConfigItemList(String inventoryName, Player player) {
        ArrayList<String> symbols = new ArrayList<>(source.getConfigurationSection("inventories." + inventoryName + ".items").getKeys(false));
        List<String> items = new ArrayList<>();
        symbols.forEach(s -> items.add(s + " " + source.getString("inventories." + inventoryName + ".items." + s)));

        for(String item : items) {
            final String symbol = item.split(" ")[0];
            String itemString = item.substring(item.indexOf(item.split(" ")[1]));

            if(player != null) itemString = itemString.replace("$player$", player.getName());
            final ItemStack is = ItemBuilder.build(itemString);

            symbolMatching.put(symbol, is);
        }
    }

    public void setRow(int row, String ... symbols) {
	    if(symbols.length > 9 || row > 6) return;

	    final int beginning = (9 * row) - 9;
	    int symbolIndex = 0;

        for (int i = beginning; i < beginning + 9; i++) {
            String symbol = symbols[symbolIndex];
            symbolIndex += 1;

            if (symbol.equals("-")) {
                if(isPlayerInv) playerInv.setItem(i, null);
                else inventory.setItem(i, null);
                continue;
            }
            if (symbolMatching.containsKey(symbol)) {
                final ItemStack item = symbolMatching.get(symbol);
                if(isPlayerInv) playerInv.setItem(i, item);
                else inventory.setItem(i, item);
            }
        }
    }

    public void setConfigRow(int rowNum, String row) {
	    int beginning = (9 * rowNum) - 9;

        for (String ch : row.split(" ")) {
            if (ch.equals("-")) {
                beginning += 1;
                continue;
            }

            if (symbolMatching.containsKey(ch)) {
                if (isPlayerInv) playerInv.setItem(beginning++, symbolMatching.get(ch));
                else inventory.setItem(beginning++, symbolMatching.get(ch));
            }
        }

        symbolMatching.clear();
    }

	public Inventory getInventory() {
	    return isPlayerInv ? playerInv : inventory;
    }

    public List<ItemStack> getAddedItems() {
	    return new ArrayList<>(symbolMatching.values());
    }

    public void overrideAddedItems(List<ItemStack> list) {
	    int index = 0;
	    for(Map.Entry<String, ItemStack> set : symbolMatching.entrySet()) {
	        symbolMatching.put(set.getKey(), list.get(index));
	        index++;
        }
    }

    public void setHotbar() {
	    if(!isPlayerInv) return;

	    List<ItemStack> hotbar = Arrays.asList(playerInv.getContents()).subList(27, 36);
	    ItemStack[] modifiedContents = playerInventory.getContents();

	    for(int i = 0; i < 9; i++) {
	        modifiedContents[i] = hotbar.get(i);
        }

	    playerInventory.setContents(modifiedContents);
    }

    public void setStorage() {
        if(!isPlayerInv) return;

        List<ItemStack> hotbar = Arrays.asList(playerInv.getContents()).subList(0, 27);
        ItemStack[] modifiedContents = playerInventory.getContents();

        for(int i = 9; i < 36; i++) {
            modifiedContents[i] = hotbar.get(i - 9);
        }

        playerInventory.setContents(modifiedContents);
    }

    public void setPlayerInventory() {
	    setStorage();
	    setHotbar();
    }
}
