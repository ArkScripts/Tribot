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
			
			main.currentStatus = "Reaction time wait before Banking";
			
			ArkUtility.reactionTimeWait(main.reactionWaitMultiplier, Constants.MINIMUM_REACTION_TIME_WAIT, Constants.MAXIMUM_REACTION_TIME_WAIT);
			
			main.currentStatus = "Travelling to Bank...";
			try {
				DaxWalker.getInstance().walkToBank();
			} catch (Exception e) {
				General.println("Dax Walker said no.");
			}
		} else {
			main.currentStatus = "Banking...";
			
			/* Calculates profit */
			main.checkIfInventoryTotalValueChanged();
			
			if (Banking.openBank()) {
				//Deposit all items except fishing equipment
				Timing.waitCondition(() -> Banking.depositAllExcept(Constants.FISHING_EQUIPMENT) == 0, ArkUtility.MEDIUM_TIMEOUT);
				
				ArkUtility.reactionTimeWait(main.reactionWaitMultiplier, Constants.MINIMUM_BANKING_TIME_WAIT,
						Constants.MAXIMUM_BANKING_TIME_WAIT);
				
				ArkUtility.closeBank();
				
				//Reset our inventory value total for profit calculations
				main.lastInventoryValue = ArkUtility.getPriceOfInventory();
			}
		}
	}

}
