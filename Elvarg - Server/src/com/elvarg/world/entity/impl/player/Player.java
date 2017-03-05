
package com.elvarg.world.entity.impl.player;

import java.util.LinkedList;
import java.util.List;

import com.elvarg.GameConstants;
import com.elvarg.cache.impl.definitions.ItemDefinition;
import com.elvarg.cache.impl.definitions.WeaponInterfaces;
import com.elvarg.engine.task.TaskManager;
import com.elvarg.engine.task.impl.CombatPoisonEffect;
import com.elvarg.engine.task.impl.OverloadPotionTask;
import com.elvarg.engine.task.impl.PlayerDeathTask;
import com.elvarg.engine.task.impl.PlayerSpecialAmountTask;
import com.elvarg.engine.task.impl.WalkToTask;
import com.elvarg.net.PlayerSession;
import com.elvarg.net.SessionState;
import com.elvarg.net.packet.PacketSender;
import com.elvarg.util.FrameUpdater;
import com.elvarg.util.Stopwatch;
import com.elvarg.world.World;
import com.elvarg.world.content.PrayerHandler;
import com.elvarg.world.content.ServerFeed;
import com.elvarg.world.content.Trading;
import com.elvarg.world.content.clan.ClanChat;
import com.elvarg.world.content.clan.ClanChatManager;
import com.elvarg.world.content.skills.SkillManager;
import com.elvarg.world.entity.combat.CombatFactory;
import com.elvarg.world.entity.combat.CombatSpecial;
import com.elvarg.world.entity.combat.CombatType;
import com.elvarg.world.entity.combat.magic.Autocasting;
import com.elvarg.world.entity.combat.pvp.BountyHunter;
import com.elvarg.world.entity.combat.ranged.RangedData;
import com.elvarg.world.entity.impl.Character;
import com.elvarg.world.entity.impl.npc.NPC;
import com.elvarg.world.entity.impl.npc.NpcAggression;
import com.elvarg.world.model.Animation;
import com.elvarg.world.model.Appearance;
import com.elvarg.world.model.ChatMessage;
import com.elvarg.world.model.EffectTimer;
import com.elvarg.world.model.Flag;
import com.elvarg.world.model.ForceMovement;
import com.elvarg.world.model.Locations;
import com.elvarg.world.model.MagicSpellbook;
import com.elvarg.world.model.PlayerInteractingOption;
import com.elvarg.world.model.PlayerRelations;
import com.elvarg.world.model.PlayerRights;
import com.elvarg.world.model.PlayerStatus;
import com.elvarg.world.model.SecondsTimer;
import com.elvarg.world.model.Skill;
import com.elvarg.world.model.container.impl.Bank;
import com.elvarg.world.model.container.impl.Equipment;
import com.elvarg.world.model.container.impl.Inventory;
import com.elvarg.world.model.container.impl.PriceChecker;
import com.elvarg.world.model.container.impl.Shop;
import com.elvarg.world.model.dialogue.Dialogue;
import com.elvarg.world.model.dialogue.DialogueOptions;
import com.elvarg.world.model.equipment.BonusManager;
import com.elvarg.world.model.movement.MovementStatus;
import com.elvarg.world.model.syntax.EnterSyntax;

public class Player extends Character {

	public Player(PlayerSession playerIO) {
		super(GameConstants.DEFAULT_POSITION.copy());
		this.session = playerIO;
	}

	@Override
	public void appendDeath() {
		if (!isDying) {
			isDying = true;
			TaskManager.submit(new PlayerDeathTask(this));
		}
	}

	@Override
	public int getHitpoints() {
		return getSkillManager().getCurrentLevel(Skill.HITPOINTS);
	}

	@Override
	public int getAttackAnim() {
		int anim = getCombat().getFightType().getAnimation();
		return anim;
	}

	@Override
	public int getBlockAnim() {
		ItemDefinition def = getEquipment().getItems()[Equipment.WEAPON_SLOT].getDefinition();
		int anim = def.getBlockAnim();
		return anim;
	}

	@Override
	public Character setHitpoints(int hitpoints) {
		if (isDying) {
			return this;
		}

		skillManager.setCurrentLevel(Skill.HITPOINTS, hitpoints);
		packetSender.sendSkill(Skill.HITPOINTS);
		if (getHitpoints() <= 0 && !isDying)
			appendDeath();
		return this;
	}

	@Override
	public void heal(int amount) {
		int level = skillManager.getMaxLevel(Skill.HITPOINTS);
		if ((skillManager.getCurrentLevel(Skill.HITPOINTS) + amount) >= level) {
			setHitpoints(level);
		} else {
			setHitpoints(skillManager.getCurrentLevel(Skill.HITPOINTS) + amount);
		}
	}

	@Override
	public int getBaseAttack(CombatType type) {
		if (type == CombatType.RANGED)
			return skillManager.getCurrentLevel(Skill.RANGED);
		else if (type == CombatType.MAGIC)
			return skillManager.getCurrentLevel(Skill.MAGIC);
		return skillManager.getCurrentLevel(Skill.ATTACK);
	}

	@Override
	public int getBaseDefence(CombatType type) {
		if (type == CombatType.MAGIC)
			return skillManager.getCurrentLevel(Skill.MAGIC);
		return skillManager.getCurrentLevel(Skill.DEFENCE);
	}

	@Override
	public int getBaseAttackSpeed() {

		// Gets attack speed for player's weapon
		// If player is using magic, attack speed is
		// Calculated in the MagicCombatMethod class.

		int speed = getCombat().getWeapon().getSpeed();
		// if(getCombat().getFightType().getStyle() == FightStyle.AGGRESSIVE) {
		if (getCombat().getFightType().getStyle().toString().toLowerCase().contains("rapid")) {
			speed--;
		}

		return speed;
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Player)) {
			return false;
		}

		Player p = (Player) o;
		return p.getIndex() == getIndex() || p.getUsername().equals(username);
	}

	@Override
	public int getSize() {
		return 1;
	}

	public void onTick() {

		// Process incoming packets...
		getSession().handleQueuedPackets(false);

		// Process combat
		getCombat().onTick();
		getBountyHunter().onTick();
		NpcAggression.target(this);

		// Process walking queue..
		getMovementQueue().onTick();

		// Process walk to task..
		if (walkToTask != null) {
			walkToTask.onTick();
		}

		// Process locations
		Locations.process(this);

		// Kill feed
		if (getWalkableInterfaceId() == -1) {
			getPacketSender().sendWalkableInterface(ServerFeed.INTERFACE_ID);
		}

		// More timers...
		if (getAndDecrementSkullTimer() == 0) {
			getUpdateFlag().flag(Flag.APPEARANCE);
		}

		// Updates inventory if an update
		// has been requested
		if (isUpdateInventory()) {
			getInventory().refreshItems();
			setUpdateInventory(false);
		}
	}

	public void save() {
		if (session.getState() == SessionState.LOGGED_IN || session.getState() == SessionState.LOGGING_OUT) {
			PlayerSaving.save(this);
		}
	}

	/**
	 * Can the player logout?
	 * 
	 * @return Yes if they can logout, false otherwise.
	 */
	public boolean canLogout() {
		if (CombatFactory.isBeingAttacked(this)) {
			getPacketSender().sendMessage("You must wait a few seconds after being out of combat before doing this.");
			return false;
		}
		if (busy()) {
			getPacketSender().sendMessage("You cannot log out at the moment.");
			return false;
		}
		return true;
	}

	/**
	 * Sends the logout packet to the client. This results in the
	 * ChannelEventHandler adding the player to the logout queue.
	 */
	public void logout() {
		getSession().setState(SessionState.REQUESTED_LOG_OUT);
		getPacketSender().sendLogout();
	}

	/**
	 * Called by the world's logout queue!
	 */
	public void onLogout() {

		// Notify us
		System.out.println(
				"[World] Deregistering player - [username, host] : [" + getUsername() + ", " + getHostAddress() + "]");

		// Update session state
		getSession().setState(SessionState.LOGGING_OUT);

		// Do server stuff...
		getPacketSender().sendLogout();
		getPacketSender().sendInterfaceRemoval();
		ClanChatManager.leave(this, false);
		Locations.logout(this);
		TaskManager.cancelTasks(this);
		save();

		// Send and queue the logout. Also close channel!
		getPacketSender().sendLogout();
		session.setState(SessionState.LOGGED_OUT);
		if (getSession().getChannel().isOpen()) {
			getSession().getChannel().close();
		}
		World.getPlayerRemoveQueue().add(this);
	}

	/**
	 * Called by the world's login queue!
	 */
	public void onLogin() {
		// Attempt to register the player..
		System.out.println(
				"[World] Registering player - [username, host] : [" + getUsername() + ", " + getHostAddress() + "]");

		// Check if the player is already logged in.. If so, disconnect!!
		Player copy_ = World.getPlayerByName(getUsername());
		if (copy_ != null) {
			copy_.logout();
		}

		// Update session state
		getSession().setState(SessionState.LOGGED_IN);

		// GRANT FULL PERMISSIONS WHILST SERVER BEING DEVELOPED
		// setRights(PlayerRights.ADMINISTRATOR);

		// Packets
		getPacketSender().sendMapRegion();
		getPacketSender().sendTabs();

		// Skills
		for (Skill skill : Skill.values()) {
			// getSkillManager().setCurrentLevel(skill, 95).setMaxLevel(skill,
			// 95).setExperience(skill, SkillManager.getExperienceForLevel(95));
			getSkillManager().updateSkill(skill);
		}

		// Send friends and ignored players lists...
		getRelations().setPrivateMessageId(1).onLogin(this).updateLists(true);

		// Reset prayer configs...
		PrayerHandler.resetAll(this);
		getPacketSender().sendConfig(709, isPreserveUnlocked() ? 1 : 0);
		getPacketSender().sendConfig(711, isRigourUnlocked() ? 1 : 0);
		getPacketSender().sendConfig(713, isAuguryUnlocked() ? 1 : 0);

		// Refresh item containers..
		getInventory().refreshItems();
		getEquipment().refreshItems();

		// Interaction options on right click...
		getPacketSender().sendInteractionOption("Attack", 2, true);
		getPacketSender().sendInteractionOption("Follow", 3, false);
		getPacketSender().sendInteractionOption("Trade With", 4, false);

		// Sending run energy attributes...
		getPacketSender().sendRunStatus();
		getPacketSender().sendRunEnergy(getRunEnergy());

		// Sending player's rights..
		getPacketSender().sendRights();

		// Close all interfaces, just in case...
		getPacketSender().sendInterfaceRemoval();

		// Update weapon data and interfaces..
		WeaponInterfaces.assign(this);

		// Update weapon interface configs
		getPacketSender().sendConfig(getCombat().getFightType().getParentId(), getCombat().getFightType().getChildId())
				.sendConfig(172, getCombat().autoRetaliate() ? 1 : 0).updateSpecialAttackOrb();

		// Reset autocasting
		Autocasting.setAutocast(this, null);

		// Update combat data..
		RangedData.updateDataFor(this);

		// Update locations..
		Locations.login(this);

		// Update killfeed
		ServerFeed.updateInterface(this);

		// Join clanchat
		ClanChatManager.onLogin(this);

		// Handle timers and run tasks
		if (isPoisoned()) {
			TaskManager.submit(new CombatPoisonEffect(this));
		}
		if (getSpecialPercentage() < 100) {
			TaskManager.submit(new PlayerSpecialAmountTask(this));
		}

		if (!getCombat().getFreezeTimer().finished()) {
			getPacketSender().sendEffectTimer(getCombat().getFreezeTimer().secondsRemaining(), EffectTimer.FREEZE);
		}
		if (!getVengeanceTimer().finished()) {
			getPacketSender().sendEffectTimer(getVengeanceTimer().secondsRemaining(), EffectTimer.VENGEANCE);
		}
		if (!getOverloadTimer().finished()) {
			TaskManager.submit(new OverloadPotionTask(this));
			getPacketSender().sendEffectTimer(getOverloadTimer().secondsRemaining(), EffectTimer.OVERLOAD);
		}
		if (!getCombat().getFireImmunityTimer().finished()) {
			getPacketSender().sendEffectTimer(getCombat().getFireImmunityTimer().secondsRemaining(),
					EffectTimer.ANTIFIRE);
		}
		if (!getCombat().getTeleBlockTimer().finished()) {
			getPacketSender().sendEffectTimer(getCombat().getTeleBlockTimer().secondsRemaining(),
					EffectTimer.TELE_BLOCK);
		}

		getUpdateFlag().flag(Flag.APPEARANCE);

		// Add items if new plr
		if (isNewPlayer()) {
			for (int[] item : GameConstants.startKit) {
				getInventory().add((item[0]), item[1]);
			}
			getPacketSender().sendMessage("Available commands:  ").sendMessage("").sendMessage("::item id amount")
					.sendMessage("::setlevel skillId level").sendMessage("::master").sendMessage("::runes");

		}

		// Add the player to register queue
		World.getPlayerAddQueue().add(this);
	}

	/**
	 * Called upon being registered to the world.
	 */
	@Override
	public void onRegister() {

		// Sends details about the player, such as their player index.
		getPacketSender().sendDetails();
	}

	public void restart() {
		performAnimation(new Animation(65535));
		setSpecialPercentage(100);
		setSpecialActivated(false);
		CombatSpecial.updateBar(this);
		setHasVengeance(false);
		getCombat().getFireImmunityTimer().stop();
		getCombat().getPoisonImmunityTimer().stop();
		getCombat().getTeleBlockTimer().stop();
		getCombat().getFreezeTimer().stop();
		getCombat().getPrayerBlockTimer().stop();
		getOverloadTimer().stop();
		setSkullTimer(0);
		setPoisonDamage(0);
		setWildernessLevel(0);
		WeaponInterfaces.assign(this);
		PrayerHandler.deactivatePrayers(this);
		getEquipment().refreshItems();
		getInventory().refreshItems();
		for (Skill skill : Skill.values())
			getSkillManager().setCurrentLevel(skill, getSkillManager().getMaxLevel(skill));
		setRunEnergy(100);
		getMovementQueue().setMovementStatus(MovementStatus.NONE).reset();
		getUpdateFlag().flag(Flag.APPEARANCE);
		isDying = false;
	}

	public boolean busy() {
		return interfaceId > 0 || isDying || getHitpoints() <= 0 || isNeedsPlacement()
				|| getStatus() != PlayerStatus.NONE;
	}

	/*
	 * Fields
	 */

	private DialogueOptions dialogueOptions;
	private String username;
	private String password;
	private String hostAddress;
	private Long longUsername;
	private final Stopwatch lougoutTimer = new Stopwatch();
	private final List<Player> localPlayers = new LinkedList<Player>();
	private final List<NPC> localNpcs = new LinkedList<NPC>();
	private final PacketSender packetSender = new PacketSender(this);
	private final Appearance appearance = new Appearance(this);
	private final SkillManager skillManager = new SkillManager(this);
	private final PlayerRelations relations = new PlayerRelations(this);
	private final ChatMessage chatMessages = new ChatMessage();
	private final FrameUpdater frameUpdater = new FrameUpdater();
	private final BonusManager bonusManager = new BonusManager();
	private PlayerSession session;
	private PlayerInteractingOption playerInteractingOption = PlayerInteractingOption.NONE;
	private PlayerRights rights = PlayerRights.PLAYER;
	private PlayerStatus status = PlayerStatus.NONE;
	private ClanChat currentClanChat;
	private String clanChatName = "Elvarg";
	private Dialogue dialogue;
	private Shop shop;
	private int interfaceId = -1, walkableInterfaceId = -1, multiIcon;
	private boolean isRunning = true;
	private int runEnergy = 100;
	private boolean isDying;
	private boolean regionChange, allowRegionChangePacket;
	private boolean experienceLocked;
	private final Inventory inventory = new Inventory(this);
	private final Equipment equipment = new Equipment(this);
	private final PriceChecker priceChecker = new PriceChecker(this);
	private ForceMovement forceMovement;
	private int skillAnimation;
	private boolean drainingPrayer;
	private double prayerPointDrain;
	private final Stopwatch clickDelay = new Stopwatch();
	private final Stopwatch lastItemPickup = new Stopwatch();
	private WalkToTask walkToTask;
	private EnterSyntax enterSyntax;
	private MagicSpellbook spellbook = MagicSpellbook.NORMAL;
	private final Stopwatch foodTimer = new Stopwatch();
	private final Stopwatch potionTimer = new Stopwatch();
	private int destroyItem = -1;
	private boolean updateInventory; // Updates inventory on next tick
	private boolean newPlayer;

	// Combat
	private final Stopwatch tolerance = new Stopwatch();
	private CombatSpecial combatSpecial;
	private int specialPercentage = 100;
	private boolean specialActivated, recoveringSpecialAttack;
	private int recoilDamage;
	private boolean hasVengeance;
	private SecondsTimer vengeanceTimer = new SecondsTimer();
	private SecondsTimer overloadTimer = new SecondsTimer();
	private int wildernessLevel;
	private BountyHunter bountyHunter = new BountyHunter(this);
	private int skullTimer;
	private int pkp;

	private boolean preserveUnlocked;
	private boolean rigourUnlocked;
	private boolean auguryUnlocked;

	// Banking
	private int currentBankTab;
	private Bank[] banks = new Bank[Bank.TOTAL_BANK_TABS]; // last index is for
															// bank searches
	private boolean noteWithdrawal, insertMode, searchingBank;
	private String searchSyntax = "";

	// Trading
	private Trading trading = new Trading(this);

	/*
	 * Getters/Setters
	 */

	public PlayerSession getSession() {
		return session;
	}

	public String getUsername() {
		return username;
	}

	public Player setUsername(String username) {
		this.username = username;
		return this;
	}

	public Long getLongUsername() {
		return longUsername;
	}

	public Player setLongUsername(Long longUsername) {
		this.longUsername = longUsername;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public Player setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getHostAddress() {
		return hostAddress;
	}

	public Player setHostAddress(String hostAddress) {
		this.hostAddress = hostAddress;
		return this;
	}

	public PlayerRights getRights() {
		return rights;
	}

	public Player setRights(PlayerRights rights) {
		this.rights = rights;
		return this;
	}

	public PacketSender getPacketSender() {
		return packetSender;
	}

	public SkillManager getSkillManager() {
		return skillManager;
	}

	public Appearance getAppearance() {
		return appearance;
	}

	public Stopwatch getLogoutTimer() {
		return lougoutTimer;
	}

	public boolean isDying() {
		return isDying;
	}

	public List<Player> getLocalPlayers() {
		return localPlayers;
	}

	public List<NPC> getLocalNpcs() {
		return localNpcs;
	}

	public Player setInterfaceId(int interfaceId) {
		this.interfaceId = interfaceId;
		return this;
	}

	public int getInterfaceId() {
		return interfaceId;
	}

	public boolean experienceLocked() {
		return experienceLocked;
	}

	public void setExperienceLocked(boolean experienceLocked) {
		this.experienceLocked = experienceLocked;
	}

	public PlayerRelations getRelations() {
		return relations;
	}

	public ChatMessage getChatMessages() {
		return chatMessages;
	}

	public Dialogue getDialogue() {
		return this.dialogue;
	}

	public void setDialogue(Dialogue dialogue) {
		this.dialogue = dialogue;
	}

	public DialogueOptions getDialogueOptions() {
		return dialogueOptions;
	}

	public void setDialogueOptions(DialogueOptions dialogueOptions) {
		this.dialogueOptions = dialogueOptions;
	}

	public Player setRegionChange(boolean regionChange) {
		this.regionChange = regionChange;
		return this;
	}

	public boolean isChangingRegion() {
		return this.regionChange;
	}

	public void setAllowRegionChangePacket(boolean allowRegionChangePacket) {
		this.allowRegionChangePacket = allowRegionChangePacket;
	}

	public boolean isAllowRegionChangePacket() {
		return allowRegionChangePacket;
	}

	public int getWalkableInterfaceId() {
		return walkableInterfaceId;
	}

	public void setWalkableInterfaceId(int interfaceId2) {
		this.walkableInterfaceId = interfaceId2;
	}

	public Player setRunning(boolean isRunning) {
		this.isRunning = isRunning;
		return this;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public PlayerInteractingOption getPlayerInteractingOption() {
		return playerInteractingOption;
	}

	public Player setPlayerInteractingOption(PlayerInteractingOption playerInteractingOption) {
		this.playerInteractingOption = playerInteractingOption;
		return this;
	}

	public FrameUpdater getFrameUpdater() {
		return frameUpdater;
	}

	public BonusManager getBonusManager() {
		return bonusManager;
	}

	public int getMultiIcon() {
		return multiIcon;
	}

	public Player setMultiIcon(int multiIcon) {
		this.multiIcon = multiIcon;
		return this;
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Equipment getEquipment() {
		return equipment;
	}

	public ForceMovement getForceMovement() {
		return forceMovement;
	}

	public Player setForceMovement(ForceMovement forceMovement) {
		this.forceMovement = forceMovement;
		return this;
	}

	public int getSkillAnimation() {
		return skillAnimation;
	}

	public Player setSkillAnimation(int animation) {
		this.skillAnimation = animation;
		return this;
	}

	public int getRunEnergy() {
		return runEnergy;
	}

	public void setRunEnergy(int runEnergy) {
		this.runEnergy = runEnergy;
	}

	public boolean isDrainingPrayer() {
		return drainingPrayer;
	}

	public void setDrainingPrayer(boolean drainingPrayer) {
		this.drainingPrayer = drainingPrayer;
	}

	public double getPrayerPointDrain() {
		return prayerPointDrain;
	}

	public void setPrayerPointDrain(double prayerPointDrain) {
		this.prayerPointDrain = prayerPointDrain;
	}

	public Stopwatch getLastItemPickup() {
		return lastItemPickup;
	}

	public WalkToTask getWalkToTask() {
		return walkToTask;
	}

	public void setWalkToTask(WalkToTask walkToTask) {
		this.walkToTask = walkToTask;
	}

	public CombatSpecial getCombatSpecial() {
		return combatSpecial;
	}

	public void setCombatSpecial(CombatSpecial combatSpecial) {
		this.combatSpecial = combatSpecial;
	}

	public boolean isSpecialActivated() {
		return specialActivated;
	}

	public void setSpecialActivated(boolean specialActivated) {
		this.specialActivated = specialActivated;
	}

	public int getSpecialPercentage() {
		return specialPercentage;
	}

	public void setSpecialPercentage(int specialPercentage) {
		this.specialPercentage = specialPercentage;
	}

	public void decrementSpecialPercentage(int drainAmount) {
		this.specialPercentage -= drainAmount;

		if (specialPercentage < 0) {
			specialPercentage = 0;
		}
	}

	public void incrementSpecialPercentage(int gainAmount) {
		this.specialPercentage += gainAmount;

		if (specialPercentage > 100) {
			specialPercentage = 100;
		}
	}

	public boolean isRecoveringSpecialAttack() {
		return recoveringSpecialAttack;
	}

	public void setRecoveringSpecialAttack(boolean recoveringSpecialAttack) {
		this.recoveringSpecialAttack = recoveringSpecialAttack;
	}

	public int getRecoilDamage() {
		return recoilDamage;
	}

	public void setRecoilDamage(int recoilDamage) {
		this.recoilDamage = recoilDamage;
	}

	public MagicSpellbook getSpellbook() {
		return spellbook;
	}

	public void setSpellbook(MagicSpellbook spellbook) {
		this.spellbook = spellbook;
	}

	public SecondsTimer getVengeanceTimer() {
		return vengeanceTimer;
	}

	public boolean hasVengeance() {
		return hasVengeance;
	}

	public void setHasVengeance(boolean hasVengeance) {
		this.hasVengeance = hasVengeance;
	}

	public Stopwatch getFoodTimer() {
		return foodTimer;
	}

	public Stopwatch getPotionTimer() {
		return potionTimer;
	}

	public SecondsTimer getOverloadTimer() {
		return overloadTimer;
	}

	public int getWildernessLevel() {
		return wildernessLevel;
	}

	public void setWildernessLevel(int wildernessLevel) {
		this.wildernessLevel = wildernessLevel;
	}

	public BountyHunter getBountyHunter() {
		return bountyHunter;
	}

	public void setDestroyItem(int destroyItem) {
		this.destroyItem = destroyItem;
	}

	public int getDestroyItem() {
		return destroyItem;
	}

	public Stopwatch getTolerance() {
		return tolerance;
	}

	public boolean isSkulled() {
		return skullTimer > 0;
	}

	public void setSkullTimer(int skullTimer) {
		this.skullTimer = skullTimer;
	}

	public int getAndDecrementSkullTimer() {
		return this.skullTimer--;
	}

	public int getSkullTimer() {
		return this.skullTimer;
	}

	public int getPkp() {
		return pkp;
	}

	public void setPkp(int pkp) {
		this.pkp = pkp;
	}

	public void incrementPkp(int pkp) {
		this.pkp += pkp;
	}

	public boolean isUpdateInventory() {
		return updateInventory;
	}

	public void setUpdateInventory(boolean updateInventory) {
		this.updateInventory = updateInventory;
	}

	public Stopwatch getClickDelay() {
		return clickDelay;
	}

	public Shop getShop() {
		return shop;
	}

	public Player setShop(Shop shop) {
		this.shop = shop;
		return this;
	}

	public PlayerStatus getStatus() {
		return status;
	}

	public Player setStatus(PlayerStatus status) {
		this.status = status;
		return this;
	}

	public EnterSyntax getEnterSyntax() {
		return enterSyntax;
	}

	public void setEnterSyntax(EnterSyntax enterSyntax) {
		this.enterSyntax = enterSyntax;
	}

	public int getCurrentBankTab() {
		return currentBankTab;
	}

	public Player setCurrentBankTab(int tab) {
		this.currentBankTab = tab;
		return this;
	}

	public void setNoteWithdrawal(boolean noteWithdrawal) {
		this.noteWithdrawal = noteWithdrawal;
	}

	public boolean withdrawAsNote() {
		return noteWithdrawal;
	}

	public void setInsertMode(boolean insertMode) {
		this.insertMode = insertMode;
	}

	public boolean insertMode() {
		return insertMode;
	}

	public Bank[] getBanks() {
		return banks;
	}

	public Bank getBank(int index) {
		if (banks[index] == null) {
			banks[index] = new Bank(this);
		}
		return banks[index];
	}

	public Player setBank(int index, Bank bank) {
		this.banks[index] = bank;
		return this;
	}

	public boolean isNewPlayer() {
		return newPlayer;
	}

	public void setNewPlayer(boolean newPlayer) {
		this.newPlayer = newPlayer;
	}

	public boolean isSearchingBank() {
		return searchingBank;
	}

	public void setSearchingBank(boolean searchingBank) {
		this.searchingBank = searchingBank;
	}

	public String getSearchSyntax() {
		return searchSyntax;
	}

	public void setSearchSyntax(String searchSyntax) {
		this.searchSyntax = searchSyntax;
	}

	public boolean isPreserveUnlocked() {
		return preserveUnlocked;
	}

	public void setPreserveUnlocked(boolean preserveUnlocked) {
		this.preserveUnlocked = preserveUnlocked;
	}

	public boolean isRigourUnlocked() {
		return rigourUnlocked;
	}

	public void setRigourUnlocked(boolean rigourUnlocked) {
		this.rigourUnlocked = rigourUnlocked;
	}

	public boolean isAuguryUnlocked() {
		return auguryUnlocked;
	}

	public void setAuguryUnlocked(boolean auguryUnlocked) {
		this.auguryUnlocked = auguryUnlocked;
	}

	public PriceChecker getPriceChecker() {
		return priceChecker;
	}

	public ClanChat getCurrentClanChat() {
		return currentClanChat;
	}

	public void setCurrentClanChat(ClanChat currentClanChat) {
		this.currentClanChat = currentClanChat;
	}

	public String getClanChatName() {
		return clanChatName;
	}

	public void setClanChatName(String clanChatName) {
		this.clanChatName = clanChatName;
	}

	public Trading getTrading() {
		return trading;
	}
}
