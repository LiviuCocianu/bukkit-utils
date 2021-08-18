package io.github.idoomful.bukkitutils.object;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class Paginable {
	private int page = 1;
	private int currentIndex;

	protected abstract List<ItemStack> bodyList();
	protected abstract int[] skippingPoints();
	protected abstract int itemsPerPage();
	protected abstract ItemStack createNextButton();
	protected abstract ItemStack createPreviousButton();
	protected abstract ItemStack createItemBeforeReplacement();
	protected abstract int[] nextButtonSlots();
	protected abstract int[] previousButtonSlots();
	protected abstract Inventory getInventory();

	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}

	public void nextPage() { isNextPage(true); }
	public void previousPage() {
		isNextPage(false);
	}

	protected final void createFirstPage() {
		int atSlot = 0;

		listIteration: for (int index = 0; index < itemsPerPage(); index++) {
			inventoryIteration: for (int slot = atSlot; slot < getInventory().getSize(); slot++) {
				for(int point : skippingPoints()) {
					if(point == slot) {
						atSlot += 1;
						continue inventoryIteration;
					}
				}

				if(index > bodyList().size() - 1) break listIteration;
				currentIndex += 1;

				getInventory().setItem(slot, bodyList().get(index));
				atSlot += 1;

				continue listIteration;
			}
		}
		updateButtons();
	}

	private void isNextPage(Boolean mode) {
	    if(mode != null) {
            if(mode) {
                page += 1;
            } else {
                page -= 1;
                currentIndex -= itemsPerPage() * 2;
            }
        }

		final int listStartPoint = getPage() * itemsPerPage() - itemsPerPage();
		final int listEndPoint = getPage() * itemsPerPage() - 1;

		final int inventoryStartPoint = getInventoryStartingPoint(skippingPoints());
		final int inventoryEndPoint = skippingPoints()[skippingPoints().length - 1] - 1;

		int inventoryIndex = -1;

		listIteration: for (int index = listStartPoint; index < listEndPoint + 1; index++) {
			if(inventoryIndex == -1) inventoryIndex = inventoryStartPoint;

			if(inventoryIndex < inventoryEndPoint + 1) {
				for (int point : skippingPoints()) if (point == inventoryIndex) {
					inventoryIndex += 1;
					index -= 1;
					continue listIteration;
				}

				if (index > bodyList().size() - 1) {
					getInventory().setItem(inventoryIndex, null);
					inventoryIndex += 1;
					continue;
				}

				getInventory().setItem(inventoryIndex, bodyList().get(index));

                if (mode != null) currentIndex += 1;
				inventoryIndex += 1;
			}
		}

		if(mode != null) updateButtons();
	}

	private void updateButtons() {
		// PREVIOUS PAGE CODE
		if(getPage() > 1) {
			for(int i = 0; i < previousButtonSlots().length; i++) {
				getInventory().setItem(previousButtonSlots()[i], createPreviousButton());
			}
		} else {
			for(int i = 0; i < previousButtonSlots().length; i++) {
				getInventory().setItem(previousButtonSlots()[i], createItemBeforeReplacement());
			}
		}

		// NEXT PAGE CODE
		if(currentIndex % itemsPerPage() == 0) {
			if(currentIndex >= bodyList().size()) {
				for(int i = 0; i < nextButtonSlots().length; i++) {
					getInventory().setItem(nextButtonSlots()[i], createItemBeforeReplacement());
				}
				return;
			}
			for(int i = 0; i < nextButtonSlots().length; i++) {
				getInventory().setItem(nextButtonSlots()[i], createNextButton());
			}

		} else {
			for(int i = 0; i < nextButtonSlots().length; i++) {
				getInventory().setItem(nextButtonSlots()[i], createItemBeforeReplacement());
			}
			currentIndex = getPage() * itemsPerPage();
		}
	}

	protected void refreshPage() {
	    isNextPage(null);
    }

	private int getInventoryStartingPoint(int[] input) {
		int output = 0;
		for(int index = input[0]; index < 56; index++) {
			if(index != input[index]) {
				output = input[index - 1] + 1;
				return output;
			}
		}
		return output;
	}
}
