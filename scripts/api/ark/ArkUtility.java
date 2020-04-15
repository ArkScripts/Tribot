package scripts.api.ark;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Objects;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSItemDefinition;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSNPC;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSObjectDefinition;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;

import scripts.dax_api.shared.helpers.RSItemHelper;
import scripts.dax_api.walker.utils.AccurateMouse;
import scripts.wastedbro.api.rsitem_services.GrandExchange;

/**
 * @author Marcusihno from Tribot
 */

public class ArkUtility {

	public static final int IDLE_ANIMATION = -1;

	private static final int[] VIAL = { 229 };

	private static final char[] c = new char[] { 'k', 'm', 'b', 't' };
	
	private static int minTimeToWaitToGoOffScreen;
	private static long lastOffScreen;

	private static DPathNavigator navigator = new DPathNavigator();

	/**
	 * Attempts to withdraw this item for a maximum of the default timeout.
	 * 
	 * @param number The number to withdraw
	 * @param item   The id/s of the item to withdraw
	 */
	public static void withdrawFromBank(int number, int[] item) {
		if (bankContains(item)) {
			Timing.waitCondition(() -> attemptWithdrawal(number, item), getDefaultTimeout());
		}
	}

	/**
	 * Carries out a physical withdrawal. Makes sure the bank is open. Waits up to 2
	 * seconds after a "successful" withdraw before returning.
	 * 
	 * @return Whether the item we are trying to withdraw is in the inventory yet.
	 */
	private static Boolean attemptWithdrawal(int number, int[] item) {
		if (Banking.openBank()) {
			if (Banking.withdraw(number, item)) {
				Timing.waitCondition(() -> getInventoryItem(item) != null, getShortTimeout());
			}
		}
		return getInventoryItem(item) != null;
	}

	/**
	 * Checks if the bank contains this item
	 * 
	 * @return Whether the item is present in the bank.
	 */
	private static Boolean bankContains(int[] item) {
		return Banking.find(item).length > 0;
	}

	/**
	 * Attempts to deposit all items until the inventory is empty, up to a maximum
	 * of the default timeout.
	 */
	public static void depositAllItems() {
		if (Inventory.getAll().length > 0) {
			Timing.waitCondition(() -> attemptDeposit(), getDefaultTimeout());
		}
	}

	/**
	 * Deposits all items, after making sure the bank is open
	 * 
	 * @return Whether the inventory is empty.
	 */
	private static Boolean attemptDeposit() {
		if (Banking.openBank()) {
			Banking.depositAll();
			Timing.waitCondition(() -> Inventory.getAll().length == 0, General.random(800, 1000));
		}
		return Inventory.getAll().length == 0;
	}

	/**
	 * Attempts to close the bank, up to a maximum of the default timeout. Will use
	 * either thr standard way by clicking close, or by pressing escape.
	 * 
	 * @param useEscapeClosing Whether to press Escape to exit the bank interface
	 *                         instead of clicking
	 */
	public static void closeBank(boolean useEscapeClosing) {
		if (!useEscapeClosing) {
			Timing.waitCondition(() -> Banking.close() && !Banking.isBankScreenOpen(), getDefaultTimeout());
		} else {
			Timing.waitCondition(() -> escapeCloseBank() && !Banking.isBankScreenOpen(), getDefaultTimeout());
		}
	}

	/**
	 * Closes the bank by pressing the Escape key
	 * 
	 * @return Whether the bank is closed
	 */
	private static boolean escapeCloseBank() {
		Keyboard.sendPress(KeyEvent.CHAR_UNDEFINED, KeyEvent.VK_ESCAPE);
		Timing.waitCondition(() -> !Banking.isBankScreenOpen(), getShortTimeout());
		return !Banking.isBankScreenOpen();
	}

	/**
	 * Gets the first RSItem found in the inventory and returns it.
	 * 
	 * @return An RSItem if present, otherwise null.
	 */
	public static RSItem getInventoryItem(int[] id) {
		RSItem[] items = Inventory.find(id);

		if (items.length > 0 && items[0] != null) {
			return items[0];
		} else {
			return null;
		}
	}

	/**
	 * Gets the nearest NPC by ID and returns it.
	 * 
	 * @return An RSNPC if present, otherwise null.
	 */
	public static RSNPC getNPC(int[] id) {
		RSNPC[] npc = NPCs.findNearest(id);

		if (npc.length > 0 && npc[0] != null) {
			return npc[0];
		} else {
			return null;
		}
	}

	/**
	 * Gets the first RSItem found in the equipment slots
	 * 
	 * @return An RSItem if present, otherwise null.
	 */
	public static RSItem getEquipmentItem(int[] id) {
		RSItem[] items = Equipment.find(id);

		if (items.length > 0 && items[0] != null) {
			return items[0];
		} else {
			return null;
		}
	}

	/**
	 * Gets the first RSObject found within a 10 tile radius and returns it.
	 * 
	 * @return An RSObject if present, otherwise null.
	 */
	public static RSObject getObject(int[] id) {
		RSObject[] objects = Objects.find(11, id);

		if (objects.length > 0 && objects[0] != null) {
			return objects[0];
		} else {
			return null;
		}
	}

	/**
	 * Interacts with an item in our inventory using the defined interaction string.
	 * Also drops any vials left behind after the interaction is complete.
	 * 
	 * @param lookingForID The ID of the item to interact with - will interact with
	 *                     the first item found
	 * @param interaction  The interaction string to use
	 * @return If we successfully carried out the interaction required.
	 */
	public static Boolean checkForInInventoryAndInteract(int[] lookingForID, String interaction) {

		RSItem item = ArkUtility.getInventoryItem(lookingForID);

		if (item != null) {

			int originalCount = Inventory.find(item.getID()).length;

			if (item.click(interaction)) {
				Timing.waitCondition(() -> Inventory.find(item.getID()).length != originalCount, getShortTimeout());
				if (ArkUtility.getInventoryItem(VIAL) != null) {
					Inventory.drop(VIAL);
				}
				return true;
			}
		}

		return false;
	}

	/**
	 * Happens in bank screen. Withdraws the required item, then interacts with it
	 * to replace it with the item currently in that item's worn equipment slot
	 * (while still in banking screen).
	 * 
	 * @param slot        The slot of the item to replace
	 * @param interaction The item to replace it with
	 * @return If we successfully switched out the items - based on whether the new
	 *         item is now present in the equipment slot.
	 */
	public static Boolean replaceEquipmentItem(Equipment.SLOTS slot, int[] itemId) {

		if (getEquipmentItem(itemId) != null) {
			return true;
		}

		RSItem currentSlotItem = slot.getItem();

		// withdraw the item we need to wear
		int numberToWithdraw = 1;
		if (slot == Equipment.SLOTS.ARROW) {
			numberToWithdraw = 0;
		}

		ArkUtility.withdrawFromBank(numberToWithdraw, itemId);

		if (currentSlotItem != null) {
			// let's wait until the switch has occurred
			Timing.waitCondition(() -> equipItem(itemId, currentSlotItem), getDefaultTimeout());
			Timing.waitCondition(() -> getInventoryItem(new int[] { currentSlotItem.getID() }) == null
					|| Banking.deposit(0, currentSlotItem.getID()), getDefaultTimeout());
		} else {
			// we aren't replacing the item, just adding a new item
			if (getInventoryItem(itemId) != null && getInventoryItem(itemId).click(getEquipString(itemId))) {
				Timing.waitCondition(() -> getEquipmentItem(itemId) != null, getShortTimeout());
			}
		}
		return getEquipmentItem(itemId) != null;
	}

	/**
	 * Equips an item and checks if it succeeded.
	 * 
	 * @return The equipped item is now present in the equipment slot.
	 */
	public static Boolean equipItem(int[] itemId, RSItem currentSlotItem) {
		if (ArkUtility.getInventoryItem(itemId) != null
				&& ArkUtility.getInventoryItem(itemId).click(getEquipString(itemId))) {
			Timing.waitCondition(() -> getEquipmentItem(itemId) != null, getShortTimeout());
		}
		return ArkUtility.getInventoryItem(new int[] { currentSlotItem.getID() }) != null;
	}

	/**
	 * Gets the interaction strings for the itemId and returns if any of them
	 * corresponds to an equippable item string
	 * 
	 * @return An equippable item interaction string.
	 */
	static String getEquipString(int[] itemId) {
		String actionWord = "";
		if (ArkUtility.getInventoryItem(itemId) != null) {
			String[] allActionsAvailable = ArkUtility.getInventoryItem(itemId).getDefinition().getActions();
			for (String s : allActionsAvailable) {
				if (s.equals("Wear") || s.equals("Wield") || s.equals("Equip")) {
					actionWord = s;
				}
			}
		}
		return actionWord;
	}

	/**
	 * Waits for the animation to start and end, then offers a small randomised
	 * sleep at the end
	 * 
	 * @param animationID The animation ID to wait for
	 */
	public static void waitForAnimationToEnd(int animationID) {
		Timing.waitCondition(() -> Player.getAnimation() != IDLE_ANIMATION, getShortTimeout());
		Timing.waitCondition(() -> Player.getAnimation() != animationID, getDefaultTimeout());
		General.sleep(400, 600);
	}

	/**
	 * Waits for the player to start walking and then stop, then offers a small
	 * randomised sleep at the end
	 * 
	 */
	public static void waitForWalkTo() {
		Timing.waitCondition(() -> Player.isMoving(), getShortTimeout());
		Timing.waitCondition(() -> !Player.isMoving(), getLongTimeout());
		General.sleep(400, 600);
	}

	/**
	 * Rotates to the required RSModel and attempts to make sure it is on screen.
	 * Checks it successful and returns
	 * 
	 * @param target The NPC to look at
	 * @return Whether the target Model is clickable (aka, on screen and
	 *         interactable)
	 */
	public static Boolean rotateToAndAngle(RSModel target) {
		if (target != null && !target.isClickable()) {
			Camera.turnToTile(target);
			// If still not on screen
			if (target != null && !target.isClickable()) {
				int angle = Camera.getOptimalAngleForPositionable(target);
				Camera.setCameraAngle(angle);
			}
		}

		return target != null && target.isClickable();
	}

	/**
	 * Checks that the RSModel we are trying to reach is able to be reached and
	 * attempts to reach it if not possible using DPathNavigator's travers (which
	 * handles doors)
	 * 
	 * @param entity The target Model to reach
	 * @return Whether the target model has been made reachable
	 */
	public static Boolean makeSureWeCanReachEntity(RSModel entity) {

		RSTile tileNearEntity = approximateNearTile(entity.getPosition(), 1);
		if (entity != null && !PathFinding.canReach(tileNearEntity, true)) {
			if (navigator.traverse(tileNearEntity)) {
				ArkUtility.waitForWalkTo();
			}
		}
		return entity != null && PathFinding.canReach(tileNearEntity, true);
	}

	/**
	 * If more than 8 spaces away, will attempt to walk to the target to get it on
	 * screen. If closer, it will call rotateToAndAngle, which attempts to rotate
	 * the camera to view the target model.
	 * 
	 * @param entity The model we are trying to cause to be clickable
	 * @return If the target is clickable, returns true.
	 */
	public static Boolean attemptToGetEntityOnScreen(RSModel entity) {
		if (entity != null && !entity.isClickable()) {
			if (entity.getPosition().distanceTo(Player.getPosition()) > 8 || !rotateToAndAngle(entity)) {
				if (!Player.isMoving()) {
					Walking.blindWalkTo(entity.getPosition());
				}
			}
		}
		return entity != null && entity.isClickable();
	}

	/**
	 * All in one method that will attempt to interact with a clickable entity using
	 * the target string. Will null check, identify the type, make sure the entity
	 * can be reached, attempt to force the entity to be on screen and then interact
	 * using Dax's AccurateMouse class.
	 * 
	 * @param entity      The clickable entity we are attempting to interact with
	 * @param interaction The interaction string to use
	 * 
	 * @return Whether we successfully interacted with the entity.
	 */
	public static Boolean interactWithEntity(Clickable entity, String interaction) {
		boolean isObject = false;
		if (entity != null) {
			RSModel model = null;
			if (entity instanceof RSCharacter) {
				RSCharacter rsCharacter = ((RSCharacter) entity);
				model = rsCharacter.getModel();
			} else if (entity instanceof RSGroundItem) {
				RSGroundItem rsGroundItem = ((RSGroundItem) entity);
				model = rsGroundItem.getModel();
			} else if (entity instanceof RSObject) {
				isObject = true;
				RSObject rsObject = ((RSObject) entity);
				model = rsObject.getModel();
			}

			if (!isObject || makeSureWeCanReachEntity(model)) {
				if (attemptToGetEntityOnScreen(model)) {
					if (AccurateMouse.click(entity, interaction)) {
						ArkUtility.waitForWalkTo();
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Gets a random tile within a certain radius of the initial tile
	 * 
	 * @param tile   The central tile around which the random tile should be
	 * @param radius The max radius away from the central tile
	 * 
	 * @return A random RSTile within the radius defined
	 */
	public static RSTile approximateNearTile(RSTile tile, int radius) {
		return new RSTile(tile.getX() + General.random(-radius, radius), tile.getY() + General.random(-radius, radius),
				tile.getPlane());
	}

	/**
	 * Returns a random tile from a list of RSTiles
	 * 
	 * @param List of RSTiles to get a random tile from.
	 */
	public static RSTile getRandomTileFromList(List<RSTile> items) {
		return items.get(new Random().nextInt(items.size()));
	}

	/**
	 * Gets the price of an item using WastedBro's GE API - applies necessary edits
	 * to the ID value to always return the correct price (eg. for noted items or
	 * ensouled heads).
	 * 
	 * @param id ID of the item
	 */
	public static int getPriceOfItem(int id) {

		int idToUse = id;

		if (RSItemDefinition.get(id) != null) {
			if (RSItemDefinition.get(id).isNoted()) {
				idToUse = id - 1;
			}

			if (RSItemDefinition.get(id).getName().contains("Ensouled")) {
				idToUse = id + 1;
			}
		}

		return GrandExchange.getPrice(idToUse);
	}

	/**
	 * Gets the total price of the current inventory.
	 * 
	 * @return total price of all items.
	 */
	public static int getPriceOfInventory() {

		RSItem[] allInventoryItems = Inventory.getAll();

		int totalCost = 0;

		for (RSItem item : allInventoryItems) {
			int cost = getPriceOfItem(item.getID()) * item.getStack();
			totalCost += cost;
		}

		return totalCost;

	}

	/**
	 * Converts a double to a readable number (eg. 1000 to 1k)
	 * 
	 * @param n         The number to convert
	 * @param iteration The iteration of the algorithm to start on
	 */
	public static String toReadableNumber(double n, int iteration) {
		double d = ((long) n / 100) / 10.0;
		boolean isRound = (d * 10) % 10 == 0;// true if the decimal part is equal to 0 (then it's trimmed anyway)
		return (d < 1000 ? // this determines the class, i.e. 'k', 'm' etc
				((d > 99.9 || isRound || (!isRound && d > 9.99) ? // this decides whether to trim the decimals
						(int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
				) + "" + c[iteration]) : toReadableNumber(d, iteration + 1));

	}

	/**
	 * Gets the time as a 00:00:00 format string from a millis value
	 * 
	 * @return Time as a string (00:00:00)
	 */
	public static String millisToTime(long millis) {
		String time = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
				TimeUnit.MILLISECONDS.toMinutes(millis)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
				TimeUnit.MILLISECONDS.toSeconds(millis)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
		return time;
	}

	/**
	 * Converts millis to hours
	 * 
	 */
	public static double millisToHours(long millis) {
		double hours = (millis / 1000) / 3600.0;
		return hours;
	}

	/**
	 * Iterates through an int[] to check if the int value is present in that array
	 * 
	 * @return number is present in array
	 */
	public static boolean arrayContainsInt(int[] array, int number) {
		if (array != null) {
			for (int i : array) {
				if (i == number) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Wrapper for array contains int - checks if an array contains the int declared
	 * here
	 * 
	 * @param id           Int to check presence of in array
	 * @param arrayToCheck Array to iterate through
	 */
	public static boolean sameIdAs(int id, int[] arrayToCheck) {
		return arrayContainsInt(arrayToCheck, id);
	}

	/**
	 * Takes an item ID array and converts every value to its noted form by removing
	 * 1 from each
	 * 
	 * @param itemIds Array of this item's ids
	 * @return A modified list of ints
	 */
	public static int[] convertItemIdsToNoted(int[] itemIds) {
		ArrayList<Integer> tempList = new ArrayList<Integer>();

		for (int i : itemIds) {
			tempList.add(i - 1);
		}

		return convertIntListToArray(tempList);
	}

	/**
	 * Converts a List of integers to an array of ints
	 * 
	 * @param integers List of Integers
	 * @return List as int[]
	 */
	public static int[] convertIntListToArray(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}

	/**
	 * Wrapper for getting a main interface
	 * 
	 * @return A main interface
	 */
	public static RSInterface getMainInterface(int id) {
		return Interfaces.get(id);
	}

	/**
	 * Gets the child of a main interface with relevant null checks
	 * 
	 * @return The child of the main interface, null if either the main or child are
	 *         null
	 */
	public static RSInterface getChildOfMainInterface(int mainID, int childID) {
		if (getMainInterface(mainID) != null) {
			RSInterface main = getMainInterface(mainID);
			if (main.getChild(childID) != null) {
				return main.getChild(childID);
			}
		}
		return null;
	}

	/**
	 * Gets the child of a child of a main interface with relevant null checks
	 * 
	 * @return The child of a child of the main interface, null if either the main
	 *         or child or child of child are null
	 */
	public static RSInterface getChildOfChildInterface(int mainID, int childID, int secondChildID) {
		if (getMainInterface(mainID) != null) {
			RSInterface main = getMainInterface(mainID);
			if (main.getChild(childID) != null) {
				RSInterface child = main.getChild(childID);
				if (child.getChild(secondChildID) != null) {
					return child.getChild(secondChildID);
				}
			}
		}
		return null;
	}

	/**
	 * Waits for a random time between the specific wait times and applies a set
	 * multiplier to these values for user GUI control
	 * 
	 * @param multiplier  The number to multiply the min and max values by - default
	 *                    1
	 * @param minimumWait Minimum amount of time to wait
	 * @param maximumWait Maximum amount of time to wait
	 */
	public static void reactionTimeWait(float multiplier, int minimumWait, int maximumWait) {
		int waitTime = General.random(Math.round(minimumWait * multiplier), Math.round(maximumWait * multiplier));
		General.println("[Reaction Time] Randomised, reaction-time wait (Antiban)");
		General.sleep(waitTime);
	}

	/**
	 * Returns a randomised short timeout between 1800 and 2200 milliseconds -
	 * designed for Timing.WaitCondition(()->booleanOperator, TIMEOUT) methods
	 */
	public static int getShortTimeout() {
		return General.random(1800, 2200);
	}

	/**
	 * Returns a randomised timeout between 3900 and 4400 milliseconds - designed
	 * for Timing.WaitCondition(()->booleanOperator, TIMEOUT) methods
	 */
	public static int getDefaultTimeout() {
		return General.random(3900, 4400);
	}

	/**
	 * Returns a randomised medium timeout between 9500 and 10500 milliseconds -
	 * designed for Timing.WaitCondition(()->booleanOperator, TIMEOUT) methods
	 */
	public static int getMediumTimeout() {
		return General.random(9500, 10500);
	}

	/**
	 * Returns a randomised long timeout between 19000 and 21000 seconds - designed
	 * for Timing.WaitCondition(()->booleanOperator, TIMEOUT) methods
	 */
	public static int getLongTimeout() {
		return General.random(19000, 21000);
	}

}
