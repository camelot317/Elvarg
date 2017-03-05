package com.elvarg.engine.task.impl;

import com.elvarg.engine.task.Task;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.container.impl.Shop;

public class ShopRestockTask extends Task {

	public ShopRestockTask(Shop shop) {
		super(8);
		this.shop = shop;
	}

	private final Shop shop;

	@Override
	protected void execute() {
		if (shop.fullyRestocked()) {
			stop();
			return;
		}
		if (shop.getDefinition().getId() != Shop.GENERAL_STORE) {
			for (int shopItemIndex = 0; shopItemIndex < shop.getDefinition()
					.getOriginalStock().length; shopItemIndex++) {

				int originalStockAmount = shop.getDefinition().getOriginalStock()[shopItemIndex].getAmount();
				int currentStockAmount = shop.getItems()[shopItemIndex].getAmount();

				if (originalStockAmount > currentStockAmount) {
					shop.add(new Item(shop.getItems()[shopItemIndex].getId(), 1), false);
				} else if (originalStockAmount < currentStockAmount) {
					shop.delete(shop.getItems()[shopItemIndex].getId(),
							getDeleteRatio(shop.getItems()[shopItemIndex].getAmount()), false);
				}

			}
		} else {
			for (Item it : shop.getValidItems()) {
				int delete = getDeleteRatio(it.getAmount());
				shop.delete(it.getId(), delete > 1 ? delete : 1, false);
			}
		}
		Shop.sendPublicUpdate(shop);
		shop.refreshItems();
		if (shop.fullyRestocked())
			stop();
	}

	@Override
	public void stop() {
		setEventRunning(false);
		shop.setRestockingItems(false);
	}

	public static int getRestockAmount(int amountMissing) {
		return (int) (Math.pow(amountMissing, 1.2) / 30 + 1);
	}

	public static int getDeleteRatio(int x) {
		return (int) (Math.pow(x, 1.05) / 50 + 1);
	}
}
