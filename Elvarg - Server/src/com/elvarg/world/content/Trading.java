package com.elvarg.world.content;

import com.elvarg.util.Misc;
import com.elvarg.world.entity.impl.player.Player;
import com.elvarg.world.model.Item;
import com.elvarg.world.model.PlayerStatus;
import com.elvarg.world.model.SecondsTimer;
import com.elvarg.world.model.container.ItemContainer;
import com.elvarg.world.model.container.StackType;
import com.elvarg.world.model.container.impl.Inventory;

/**
 * Handles the entire trading system. Should be dupe-free.
 * 
 * @author Swiffy
 */

public class Trading {

	// Interface data
	private static final int INTERFACE = 3323;
	public static final int CONTAINER_INTERFACE_ID = 3415;
	private static final int CONTAINER_INTERFACE_ID_2 = 3416;
	private static final int CONTAINER_INVENTORY_INTERFACE = 3321;
	public static final int INVENTORY_CONTAINER_INTERFACE = 3322;

	private static final int CONFIRM_SCREEN_INTERFACE = 3443;

	// Frames data
	private static final int TRADING_WITH_FRAME = 3417;
	private static final int STATUS_FRAME_1 = 3431;
	private static final int STATUS_FRAME_2 = 3535;
	private static final int ITEM_LIST_1_FRAME = 3557;
	private static final int ITEM_LIST_2_FRAME = 3558;
	private static final int ITEM_VALUE_1_FRAME = 24209;
	private static final int ITEM_VALUE_2_FRAME = 24210;

	// Nonstatic
	private Player player;
	private Player interact;
	private TradeState state = TradeState.NONE;
	private ItemContainer container;

	// Delays!!
	private SecondsTimer button_delay = new SecondsTimer();
	private SecondsTimer request_delay = new SecondsTimer();

	// The possible states during a trade
	private enum TradeState {
		NONE, REQUESTED_TRADE, TRADE_SCREEN, ACCEPTED_TRADE_SCREEN, CONFIRM_SCREEN, ACCEPTED_CONFIRM_SCREEN;
	}

	// Constructor
	public Trading(Player player) {
		this.player = player;

		// The container which will hold all our offered items.
		this.container = new ItemContainer(player) {
			@Override
			public StackType stackType() {
				return StackType.DEFAULT;
			}

			@Override
			public ItemContainer refreshItems() {
				player.getPacketSender().sendInterfaceSet(INTERFACE, CONTAINER_INVENTORY_INTERFACE);
				player.getPacketSender().sendItemContainer(container, CONTAINER_INTERFACE_ID);
				player.getPacketSender().sendItemContainer(player.getInventory(), INVENTORY_CONTAINER_INTERFACE);
				player.getPacketSender().sendItemContainer(interact.getTrading().getContainer(),
						CONTAINER_INTERFACE_ID_2);
				interact.getPacketSender().sendItemContainer(player.getTrading().getContainer(),
						CONTAINER_INTERFACE_ID_2);
				return this;
			}

			@Override
			public ItemContainer full() {
				getPlayer().getPacketSender().sendMessage("You cannot trade more items.");
				return this;
			}

			@Override
			public int capacity() {
				return 28;
			}
		};
	}

	public void requestTrade(Player t_) {

		// Make sure to not allow flooding!
		if (!request_delay.finished()) {
			int seconds = request_delay.secondsRemaining();
			player.getPacketSender().sendMessage("You must wait another "
					+ (seconds == 1 ? "second" : "" + seconds + " seconds") + " before sending more trade requests.");
			return;
		}

		// The other players' current trade state.
		final TradeState t_state = t_.getTrading().getState();

		// Should we initiate the trade or simply send a request?
		boolean initiateTrade = false;

		// Update this instance...
		this.setInteract(t_);
		this.setState(TradeState.REQUESTED_TRADE);

		// Check if target requested a trade with us...
		if (t_state == TradeState.REQUESTED_TRADE) {
			if (t_.getTrading().getInteract() != null && t_.getTrading().getInteract() == player) {
				initiateTrade = true;
			}
		}

		// Initiate trade for both players with eachother?
		if (initiateTrade) {
			player.getTrading().initiateTrade();
			t_.getTrading().initiateTrade();
		} else {
			player.getPacketSender().sendMessage("You've sent a trade request to " + t_.getUsername() + ".");
			t_.getPacketSender().sendMessage(player.getUsername() + ":tradereq:");
		}

		// Set the request delay to 2 seconds at least.
		request_delay.start(2);
	}

	public void initiateTrade() {

		// Update statuses
		player.setStatus(PlayerStatus.TRADING);
		player.getTrading().setState(TradeState.TRADE_SCREEN);

		// Update strings on interface
		player.getPacketSender().sendString(TRADING_WITH_FRAME, "Trading with: @whi@" + interact.getUsername());
		player.getPacketSender().sendString(STATUS_FRAME_1, "")
				.sendString(STATUS_FRAME_2, "Are you sure you want to make this trade?")
				.sendString(ITEM_VALUE_1_FRAME, "0 gp").sendString(ITEM_VALUE_2_FRAME, "0 gp");

		// Reset container
		container.resetItems();

		// Refresh and send container...
		container.refreshItems();

	}

	public void closeTrade() {
		if (state != TradeState.NONE) {

			// Cache the current interact
			final Player interact_ = interact;

			// Return all items...
			for (Item t : container.getValidItems()) {
				container.switchItem(player.getInventory(), t.copy(), false, false);
			}

			// Refresh inventory
			player.getInventory().refreshItems();

			// Reset all attributes...
			resetAttributes();

			// Send decline message
			player.getPacketSender().sendMessage("Trade declined.");
			player.getPacketSender().sendInterfaceRemoval();

			// Reset trade for other player aswell (the cached interact)
			if (interact_ != null) {
				if (interact_.getStatus() == PlayerStatus.TRADING) {
					if (interact_.getTrading().getInteract() != null
							&& interact_.getTrading().getInteract() == player) {
						// Send interface removal.
						// This will call the closeTrade function
						interact_.getPacketSender().sendInterfaceRemoval();
					}
				}
			}
		}
	}

	public void acceptTrade() {

		// Check if we're actually trading..
		if (player.getStatus() != PlayerStatus.TRADING) {
			return;
		}

		// Verify interact...
		if (interact == null || interact.getStatus() != PlayerStatus.TRADING
				|| interact.getTrading().getInteract() == null || interact.getTrading().getInteract() != player) {
			return;
		}

		// Check button delay...
		if (!button_delay.finished()) {
			return;
		}

		// Cache the interact...
		final Player interact_ = interact;

		// Interact's current trade state.
		final TradeState t_state = interact_.getTrading().getState();

		// Check which action to take..
		if (state == TradeState.TRADE_SCREEN) {

			// Both are in the same state. Do the first-stage accept.
			setState(TradeState.ACCEPTED_TRADE_SCREEN);

			// Update status...
			player.getPacketSender().sendString(STATUS_FRAME_1, "Waiting for other player..");
			interact_.getPacketSender().sendString(STATUS_FRAME_1, "" + player.getUsername() + " has accepted.");

			// Check if both have accepted..
			if (state == TradeState.ACCEPTED_TRADE_SCREEN && t_state == TradeState.ACCEPTED_TRADE_SCREEN) {

				// Technically here, both have accepted.
				// Go into confirm screen!
				player.getTrading().confirmScreen();
				interact_.getTrading().confirmScreen();
			}
		} else if (state == TradeState.CONFIRM_SCREEN) {

			// Both are in the same state. Do the second-stage accept.
			setState(TradeState.ACCEPTED_CONFIRM_SCREEN);

			// Update status...
			player.getPacketSender().sendString(STATUS_FRAME_2,
					"Waiting for " + interact_.getUsername() + "'s confirmation..");
			interact_.getPacketSender().sendString(STATUS_FRAME_2,
					"" + player.getUsername() + " has accepted. Do you wish to do the same?");

			// Check if both have accepted..
			if (state == TradeState.ACCEPTED_CONFIRM_SCREEN && t_state == TradeState.ACCEPTED_CONFIRM_SCREEN) {

				// Give items to both players...
				for (Item item : interact_.getTrading().getContainer().getValidItems()) {
					player.getInventory().add(item);
				}
				for (Item item : player.getTrading().getContainer().getValidItems()) {
					interact_.getInventory().add(item);
				}

				// Reset attributes for both players...
				player.getTrading().resetAttributes();
				interact_.getTrading().resetAttributes();

				// Send interface removal for both players...
				player.getPacketSender().sendInterfaceRemoval();
				interact_.getPacketSender().sendInterfaceRemoval();

				// Send successful trade message!
				player.getPacketSender().sendMessage("Trade accepted!");
				interact_.getPacketSender().sendMessage("Trade accepted!");
			}
		}

		button_delay.start(1);
	}

	private void confirmScreen() {

		// Update state
		player.getTrading().setState(TradeState.CONFIRM_SCREEN);

		// Send new interface
		player.getPacketSender().sendInterfaceSet(CONFIRM_SCREEN_INTERFACE, CONTAINER_INVENTORY_INTERFACE);
		player.getPacketSender().sendItemContainer(player.getInventory(), INVENTORY_CONTAINER_INTERFACE);

		// Send new interface frames
		String this_items = listItems(container);
		String interact_item = listItems(interact.getTrading().getContainer());
		player.getPacketSender().sendString(ITEM_LIST_1_FRAME, this_items);
		player.getPacketSender().sendString(ITEM_LIST_2_FRAME, interact_item);
	}

	// Deposit or withdraw an item....
	public void handleItem(int id, int amount, int slot, ItemContainer from, ItemContainer to) {
		if (player.getStatus() == PlayerStatus.TRADING && player.getInterfaceId() == INTERFACE) {

			// Verify interact...
			if (interact == null || interact.getStatus() != PlayerStatus.TRADING
					|| interact.getTrading().getInteract() == null || interact.getTrading().getInteract() != player) {
				return;
			}

			// Check if the trade was previously accepted (and now modified)...
			boolean modified = false;
			if (state == TradeState.ACCEPTED_TRADE_SCREEN) {
				state = TradeState.TRADE_SCREEN;
				modified = true;
			}
			if (interact.getTrading().getState() == TradeState.ACCEPTED_TRADE_SCREEN) {
				interact.getTrading().setState(TradeState.TRADE_SCREEN);
				modified = true;
			}
			if (modified) {
				player.getPacketSender().sendString(STATUS_FRAME_1, "@red@TRADE MODIFIED!");
				interact.getPacketSender().sendString(STATUS_FRAME_1, "@red@TRADE MODIFIED!");
			}

			// Handle the item switch..
			if (state == TradeState.TRADE_SCREEN && interact.getTrading().getState() == TradeState.TRADE_SCREEN) {

				// Check if the item is in the right place
				if (from.getItems()[slot].getId() == id) {

					final Item item = new Item(id, amount);

					// Check if interact has proper inventory slots
					// Before we offer the item..
					if (from instanceof Inventory) {

						// First check if our container is full..
						if (container.getFreeSlots() == 0) {
							if (!(item.getDefinition().isStackable() && container.contains(id))) {
								container.full();
								return;
							}
						}

						// Check total slots needed...
						// If this item isn't in their inventory, slots needed
						// += 1
						int slotsNeeded = item.getDefinition().isStackable() && interact.getInventory().contains(id) ? 0
								: 1;

						// Go through the rest of the items we've already
						// offered.
						// if they aren't in their inventopry, slots needed += 1
						for (Item t : to.getValidItems()) {
							if (!(t.getDefinition().isStackable() && interact.getInventory().contains(t.getId()))) {
								slotsNeeded++;
							}
						}

						// Check total free slots..
						int freeSlots = interact.getInventory().getFreeSlots();
						if (slotsNeeded > freeSlots) {
							player.getPacketSender().sendMessage("")
									.sendMessage(
											"@or3@" + interact.getUsername() + " will not be able to hold that item.")
									.sendMessage("@or3@They have " + freeSlots + " free inventory slot"
											+ (freeSlots == 1 ? "." : "s."));
							return;
						}
					}

					// Do the switch!
					if (item.getAmount() == 1) {
						from.switchItem(to, item, slot, false, true);
					} else {
						from.switchItem(to, item, false, true);
					}

					// Update value frames for both players
					String plr_value = container.getTotalValue();
					String other_plr_value = interact.getTrading().getContainer().getTotalValue();
					player.getPacketSender().sendString(ITEM_VALUE_1_FRAME,
							Misc.insertCommasToNumber(plr_value) + " gp");
					player.getPacketSender().sendString(ITEM_VALUE_2_FRAME,
							Misc.insertCommasToNumber(other_plr_value) + " gp");
					interact.getPacketSender().sendString(ITEM_VALUE_1_FRAME,
							Misc.insertCommasToNumber(other_plr_value) + " gp");
					interact.getPacketSender().sendString(ITEM_VALUE_2_FRAME,
							Misc.insertCommasToNumber(plr_value) + " gp");
				}
			} else {
				player.getPacketSender().sendInterfaceRemoval();
			}
		}
	}

	public void resetAttributes() {
		// Reset trade attributes
		setInteract(null);
		setState(TradeState.NONE);

		// Reset player status if it's trading.
		if (player.getStatus() == PlayerStatus.TRADING) {
			player.setStatus(PlayerStatus.NONE);
		}

		// Reset container..
		container.resetItems();

		// Send the new empty container to the interface
		// Just to clear the items there.
		player.getPacketSender().sendItemContainer(container, CONTAINER_INTERFACE_ID);
	}

	private static String listItems(ItemContainer items) {
		String string = "";
		int item_counter = 0;
		for (Item item : items.getValidItems()) {
			if (item_counter > 0) {
				string += "\\n";
			}
			string += item.getDefinition().getName().replaceAll("_", " ");
			if (item.getDefinition().isStackable()) {
				String amt = "" + Misc.format(item.getAmount());
				if (item.getAmount() >= 1000000000) {
					amt = "@gre@" + (item.getAmount() / 1000000000) + " billion @whi@(" + Misc.format(item.getAmount())
							+ ")";
				} else if (item.getAmount() >= 1000000) {
					amt = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + Misc.format(item.getAmount())
							+ ")";
				} else if (item.getAmount() >= 1000) {
					amt = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + Misc.format(item.getAmount()) + ")";
				}
				string += " x " + amt;
			}
			item_counter++;
		}
		if (item_counter == 0) {
			string = "Absolutely nothing!";
		}
		return string;
	}

	public TradeState getState() {
		return state;
	}

	public void setState(TradeState state) {
		this.state = state;
	}

	public Player getInteract() {
		return interact;
	}

	public void setInteract(Player interact) {
		this.interact = interact;
	}

	public ItemContainer getContainer() {
		return container;
	}
}
