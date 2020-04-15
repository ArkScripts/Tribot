package scripts.arkscripts.masterfarmer;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Combat;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Inventory;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;
import scripts.dax_api.api_lib.DaxWalker;

public class BankingTask implements Task {

	private ArkMasterFarmer main = ArkMasterFarmer.getInstance();

	@Override
	public Priority priority() {
		return Priority.HIGH;
	}

	@Override
	public boolean validate() {
		return shouldBank();
	}

	public Boolean shouldBank() {
		return (Timing.timeFromMark(main.timeOfStun) > main.randomStunTime
				&& (Combat.isUnderAttack() || Inventory.isFull() || (main.useDodgyNecklaces && ArkUtility.getEquipmentItem(Constants.DODGY_NECKLACE_ID) == null))
				|| (Combat.getHP() < main.pctToEatAt && ArkUtility.getInventoryItem(main.foodID) == null));
	}

	@Override
	public void execute() {

		if (!Banking.isInBank()) {

			if(!Combat.isUnderAttack()) {
			main.currentStatus = "Reaction time wait before Banking";

			ArkUtility.reactionTimeWait(main.reactionTimeMultiplier, Constants.MINIMUM_BANKING_TIME_WAIT,
					Constants.MAXIMUM_BANKING_TIME_WAIT);
			
			main.currentStatus = "Travelling to Bank...";
			} else {
				main.currentStatus = "Under Attack! Resetting at Bank...";
			}

			try {
				DaxWalker.getInstance().walkToBank();
			} catch (Exception e) {
				General.println("Dax Walker said no.");
			}
			ArkUtility.waitForWalkTo();

		} else {
			main.currentStatus = "Banking...";

			if (Banking.openBank()) {

				ArkUtility.depositAllItems();

				if (main.useDodgyNecklaces && ArkUtility.getEquipmentItem(Constants.DODGY_NECKLACE_ID) == null) {
					main.currentStatus = "Replacing Dodgy Necklace";
					if (Banking.find(Constants.DODGY_NECKLACE_ID) == null) {
						// We ran out of dodgy necklaces, we should stop
						General.println("[Stop Script] We ran out of Dodgy Necklaces, stopping script");
						main.runScript = false;
					}
					Timing.waitCondition(
							() -> ArkUtility.replaceEquipmentItem(Equipment.SLOTS.AMULET, Constants.DODGY_NECKLACE_ID),
							ArkUtility.getMediumTimeout());
				}

				main.currentStatus = "Withdrawing Food";

				if (Banking.find(main.foodID) == null) {
					General.println("[Stop Script] We ran out of Food, stopping script");
					main.runScript = false;
				}

				ArkUtility.withdrawFromBank(main.numberOfFoodToWithdrawPerTrip, main.foodID);

				main.lastInventoryValue = ArkUtility.getPriceOfInventory();

				ArkUtility.closeBank(false);
			}
		}
	}

}
