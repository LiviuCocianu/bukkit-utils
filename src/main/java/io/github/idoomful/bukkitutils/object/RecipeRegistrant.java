package io.github.idoomful.bukkitutils.object;

import io.github.idoomful.bukkitutils.statics.Events;
import io.github.idoomful.bukkitutils.statics.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class RecipeRegistrant {
    private final FileConfiguration source;
    private final HashMap<String, Recipe> recipes = new HashMap<>();

    private RecipeRegistrant() {
        source = null;
    }

    /**
     * Once initialized, it will register all specified recipes in the provided source
     * @param source The configuration file where the recipes are
     */
    public RecipeRegistrant(JavaPlugin main, FileConfiguration source) {
        this.source = source;

        for(String namespacedKey : source.getConfigurationSection("recipes").getKeys(false)) {
            recipes.put(namespacedKey, new Recipe(namespacedKey));
        }

        Events.listen(main, PrepareItemCraftEvent.class, e -> {
            for(Recipe recipe : recipes.values()) {
                boolean pass = true;

                for(int i = 0; i < recipe.getMatrix().length; i++) {
                    ItemStack recipeItem = recipe.getMatrix()[i];
                    ItemStack eventItem = e.getInventory().getMatrix()[i];

                    if(recipeItem.getType() == Material.AIR) continue;

                    if(!recipeItem.equals(eventItem)) {
                        pass = false;
                        break;
                    }
                }

                if(pass)
                    e.getInventory().setResult(recipe.getResult());
            }
        });

        Events.listen(main, InventoryClickEvent.class, e -> {
            if(e.getClickedInventory() == null) return;
            if(e.getCurrentItem() == null) return;

            final ItemStack clicked = e.getCurrentItem();
            final Inventory inv = e.getClickedInventory();

            if(inv.getType() == InventoryType.WORKBENCH) {
                // If clicked on the result slot
                if(e.getSlot() == 0 && (e.getCursor() == null || e.getCursor().getType() == Material.AIR)) {
                    // Check if result came from a custom recipe
                    for(Recipe recipe : recipes.values()) {
                        ItemStack result = recipe.getResult();

                        if(clicked.equals(result)) {
                            Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                                // Subtract required items after taking the result
                                for(int i = 1; i <= 9; i++) {
                                    ItemStack matrixItem = inv.getItem(i);
                                    if(matrixItem == null) continue;

                                    matrixItem.setAmount(matrixItem.getAmount() - recipe.getMatrix()[i - 1].getAmount());
                                    inv.setItem(i, matrixItem);
                                }
                            }, 1);

                            break;
                        }
                    }
                }
            }
        });
    }

    /**
     * Reload the recipes
     */
    public void reload() {
        recipes.clear();

        for(String namespacedKey : source.getConfigurationSection("recipes").getKeys(false)) {
            recipes.put(namespacedKey, new Recipe(namespacedKey));
        }
    }

    /**
     * Get the Recipe object for the specified namespaced key
     * @param namespacedKey The name of the crafting recipe
     */
    public Recipe getRecipe(String namespacedKey) {
        return recipes.get(namespacedKey);
    }

    private class Recipe {
        private final String namespacedKey;
        private final HashMap<String, ItemStack> items = new HashMap<>();
        private final ItemStack[] matrix = new ItemStack[9];
        private ItemStack result;

        public Recipe(String namespacedKey) {
            this.namespacedKey = namespacedKey;

            if(!source.getConfigurationSection("recipes").getKeys(false).contains(namespacedKey)) return;

            final String path = "recipes." + namespacedKey;

            for(String symbol : source.getConfigurationSection("recipes." + namespacedKey + ".items").getKeys(false)) {
                items.put(symbol, ItemBuilder.build(source.getString(path + ".items." + symbol)));
            }

            for(int i = 0; i < source.getStringList("recipes." + namespacedKey + ".matrix").size(); i++) {
                final String row = source.getStringList("recipes." + namespacedKey + ".matrix").get(i);
                final String[] symbols = row.split(" ");

                for(int j = 0; j < symbols.length; j++) {
                    String symbol = symbols[j];
                    if(symbol.equals("-")) {
                        matrix[(i * 3) + j] = new ItemStack(Material.AIR);
                        continue;
                    }
                    matrix[(i * 3) + j] = items.get(symbol);
                }
            }

            result = ItemBuilder.build(source.getString(path + ".result"));
        }

        public String getNamespacedKey() {
            return namespacedKey;
        }

        public HashMap<String, ItemStack> getItems() {
            return items;
        }

        public ItemStack[] getMatrix() {
            return matrix;
        }

        public ItemStack getResult() {
            return result;
        }
    }
}
