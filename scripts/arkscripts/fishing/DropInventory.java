package scripts.arkscripts.fishing;

import org.tribot.api.Timing;
import org.tribot.api2007.Inventory;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;

public class DropInventory implements Task {

	private ArkFishing main = ArkFishing.getInstance();
	
	@Override
	public Priority priority() {
		return Priority.MEDIUM;
	}

	@Override
	public boolean validate() {
		return Inventory.isFull();
	}

	@Override
	public void execute() {
		
		main.currentStatus = "Reaction time wait before Dropping...";
		
		ArkUtility.reactionTimeWait(main.reactionWaitMultiplier, Constants.MINIMUM_REACTION_TIME_WAIT, Constants.MAXIMUM_REACTION_TIME_WAIT);
		
		main.currentStatus = "Dropping our Inventory";
		
		Timing.waitCondition(() -> Inventory.dropAllExcept(Constants.FISHING_EQUIPMENT) == 0, ArkUtility.LONG_TIMEOUT);
		
	}
	


}
