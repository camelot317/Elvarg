package com.elvarg.world.model.container.impl;

import java.util.List;

import com.elvarg.util.Misc;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.PlayerStatus;
import com.elvarg.world.model.container.ItemContainer;
import com.elvarg.world.model.container.StackType;

public class PriceChecker extends ItemContainer {

	public PriceChecker(Player player) {
		super(player);
	}

	public ItemContainer open() {
		getPlayer().setStatus(PlayerStatus.PRICE_CHECKING);
		getPlayer().getMovementQueue().reset();
		refreshItems();
		return this;
	}

	@Override
	public int capacity() {
		return 24;
	}

	@Override
	public StackType stackType() {
		return StackType.DEFAULT;
	}

	@Override
	public ItemContainer refreshItems() {

		List<Item> items_ = getValidItems();

		if (items_.size() > 0) {
			getPlayer().getPacketSender().sendString(18355, "").sendString(18351,
					"" + Misc.insertCommasToNumber(getTotalValue())); // TOTAL
																		// VALUE

			// Send item prices
			for (int i = 0; i < capacity(); i++) {
				String itemPrice = "";
				String totalPrice = "";

				if (getItems()[i].getId() > 0 && getItems()[i].getAmount() > 0) {
					int itemAmt = getItems()[i].getAmount();
					int price = getItems()[i].getDefinition().getValue();
					int total_price = itemAmt * price;

					itemPrice = "" + Misc.insertCommasToNumber("" + price + "") + " x" + itemAmt;
					totalPrice = " = " + Misc.insertCommasToNumber("" + total_price);
				}

				getPlayer().getPacketSender().sendString(TEXT_START_ROW_1 + i, itemPrice);
				getPlayer().getPacketSender().sendString(TEXT_START_ROW_2 + i, totalPrice);
			}

		} else {
			getPlayer().getPacketSender().sendString(18355, "Click an item in your inventory to check it's wealth.")
					.sendString(18351, "0"); // TOTAL VALUE

			// Reset item prices
			for (int i = 0; i < capacity(); i++) {
				getPlayer().getPacketSender().sendString(TEXT_START_ROW_1 + i, "");
				getPlayer().getPacketSender().sendString(TEXT_START_ROW_2 + i, "");
			}
		}

		getPlayer().getPacketSender().sendInterfaceSet(INTERFACE_ID, 3321);
		getPlayer().getPacketSender().sendItemContainer(this, CONTAINER_ID);
		getPlayer().getPacketSender().sendItemContainer(getPlayer().getInventory(), 3322);
		return this;
	}

	@Override
	public ItemContainer full() {
		getPlayer().getPacketSender().sendMessage("The pricechecker cannot hold any more items.");
		return this;
	}

	public void withdrawAll() {
		if (getPlayer().getStatus() == PlayerStatus.PRICE_CHECKING && getPlayer().getInterfaceId() == INTERFACE_ID) {
			for (Item item : getValidItems()) {
				switchItem(getPlayer().getInventory(), item.copy(), false, false);
			}
			refreshItems();
			getPlayer().getInventory().refreshItems();
		}
	}

	public void depositAll() {
		if (getPlayer().getStatus() == PlayerStatus.PRICE_CHECKING && getPlayer().getInterfaceId() == INTERFACE_ID) {
			for (Item item : getPlayer().getInventory().getValidItems()) {
				getPlayer().getInventory().switchItem(this, item.copy(), false, false);
			}
			refreshItems();
			getPlayer().getInventory().refreshItems();
		}
	}

	public boolean deposit(int id, int amount, int slot) {
		if (getPlayer().getStatus() == PlayerStatus.PRICE_CHECKING && getPlayer().getInterfaceId() == INTERFACE_ID) {

			// Verify item
			if (getPlayer().getInventory().getItems()[slot].getId() == id) {

				// Perform switch
				final Item item = new Item(id, amount);
				if (item.getAmount() == 1) {
					getPlayer().getInventory().switchItem(this, item, slot, false, true);
				} else {
					getPlayer().getInventory().switchItem(this, item, false, true);
				}
			}
			return true;
		}
		return false;
	}

	public boolean withdraw(int id, int amount, int slot) {
		if (getPlayer().getStatus() == PlayerStatus.PRICE_CHECKING && getPlayer().getInterfaceId() == INTERFACE_ID) {

			// Verify item
			if (getItems()[slot].getId() == id) {

				// Perform switch
				final Item item = new Item(id, amount);
				if (item.getAmount() == 1) {
					switchItem(getPlayer().getInventory(), item, slot, false, true);
				} else {
					switchItem(getPlayer().getInventory(), item, false, true);
				}
			}
			return true;
		}
		return false;
	}

	public static final int INTERFACE_ID = 42000, CONTAINER_ID = 18500;
	private static final int TEXT_START_ROW_1 = 18300;
	private static final int TEXT_START_ROW_2 = 18400;
}
