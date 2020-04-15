package scripts.arkscripts.fishing;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.Player;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;
import scripts.dax_api.api_lib.DaxWalker;

public class BankingTask implements Task {

	private ArkFishing main = ArkFishing.getInstance();

	@Override
	public Priority priority() {
		return Priority.MEDIUM;
	}

	@Override
	public boolean validate() {
		return Inventory.isFull() && !Player.isMoving();
	}

	@Override
	public void execute() {
		
		if (!Banking.isInBank()) {
			main.currentStatus = "Travelling to Bank...";
			main.currentStatus = "Reaction time wait before Banking...";

			ArkUtility.reactionTimeWait(main.reactionWaitMultiplier, Constants.MINIMUM_REACTION_TIME_WAIT_POST_FISHING,
					Constants.MAXIMUM_REACTION_TIME_WAIT_POST_FISHING);
			try {
				DaxWalker.getInstance().walkToBank();
			} catch (Exception e) {
				General.println("Dax Walker said: " + e.getMessage());
			}

		}

		if (Banking.isInBank()) {
			main.currentStatus = "Banking...";
			/* Calculates profit */
			main.checkIfInventoryTotalValueChanged();

			if (Banking.openBank()) {
				// Deposit all items except fishing equipment
				Timing.waitCondition(() -> depositedAllExcept(), ArkUtility.getDefaultTimeout());
				ArkUtility.closeBank(false);
				// Reset our inventory value total for profit calculations
				main.lastInventoryValue = ArkUtility.getPriceOfInventory();
			}
		}
	}
	
	private boolean depositedAllExcept() {
		ArkUtility.reactionTimeWait(main.reactionWaitMultiplier,
				Constants.MINIMUM_REACTION_TIME_WAIT_POST_DEPOSIT,
				Constants.MAXIMUM_REACTION_TIME_WAIT_POST_DEPOSIT);
		return Banking.depositAllExcept(Constants.FISHING_EQUIPMENT) == 0;
	}

}
