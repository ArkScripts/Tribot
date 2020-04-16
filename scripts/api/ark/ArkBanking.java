package scripts.api.ark;

import java.awt.event.KeyEvent;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;

public class ArkBanking {

	/**
	 * Attempts to withdraw this item for a maximum of the default timeout.
	 * 
	 * @param number The number to withdraw
	 * @param item   The id/s of the item to withdraw
	 */
	public static void withdrawFromBank(int number, int[] item) {
		if (bankContains(item)) {
			Timing.waitCondition(() -> attemptWithdrawal(number, item), ArkUtility.getDefaultTimeout());
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
				Timing.waitCondition(() -> ArkUtility.getInventoryItem(item) != null, ArkUtility.getShortTimeout());
			}
		}
		return ArkUtility.getInventoryItem(item) != null;
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
			Timing.waitCondition(() -> attemptDeposit(), ArkUtility.getDefaultTimeout());
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
			Timing.waitCondition(() -> Banking.close() && !Banking.isBankScreenOpen(), ArkUtility.getDefaultTimeout());
		} else {
			Timing.waitCondition(() -> escapeCloseBank() && !Banking.isBankScreenOpen(), ArkUtility.getDefaultTimeout());
		}
	}

	/**
	 * Closes the bank by pressing the Escape key
	 * 
	 * @return Whether the bank is closed
	 */
	private static boolean escapeCloseBank() {
		Keyboard.sendPress(KeyEvent.CHAR_UNDEFINED, KeyEvent.VK_ESCAPE);
		Timing.waitCondition(() -> !Banking.isBankScreenOpen(), ArkUtility.getShortTimeout());
		return !Banking.isBankScreenOpen();
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

		if (ArkUtility.getEquipmentItem(itemId) != null) {
			return true;
		}

		RSItem currentSlotItem = slot.getItem();

		// withdraw the item we need to wear
		int numberToWithdraw = 1;
		if (slot == Equipment.SLOTS.ARROW) {
			numberToWithdraw = 0;
		}

		withdrawFromBank(numberToWithdraw, itemId);

		if (currentSlotItem != null) {
			// let's wait until the switch has occurred
			Timing.waitCondition(() -> ArkInteraction.equipItem(itemId, currentSlotItem), ArkUtility.getDefaultTimeout());
			Timing.waitCondition(() -> ArkUtility.getInventoryItem(new int[] { currentSlotItem.getID() }) == null
					|| Banking.deposit(0, currentSlotItem.getID()), ArkUtility.getDefaultTimeout());
		} else {
			// we aren't replacing the item, just adding a new item
			if (ArkUtility.getInventoryItem(itemId) != null && ArkUtility.getInventoryItem(itemId).click(ArkUtility.getEquipString(itemId))) {
				Timing.waitCondition(() -> ArkUtility.getEquipmentItem(itemId) != null, ArkUtility.getShortTimeout());
			}
		}
		return ArkUtility.getEquipmentItem(itemId) != null;
	}

	
}
