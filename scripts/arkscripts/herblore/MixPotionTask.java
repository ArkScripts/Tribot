package scripts.arkscripts.herblore;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Keyboard;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Interfaces;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSInterface;
import org.tribot.api2007.types.RSItem;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;

public class MixPotionTask implements Task {
	
	private ArkHerblore main = ArkHerblore.getInstance();

	@Override
	public Priority priority() {
		return Priority.MEDIUM;
	}

	@Override
	public boolean validate() {
		return Timing.timeFromMark(main.lastActivityTime) >= General.random(Constants.IDLE_WAIT_MIN, Constants.IDLE_WAIT_MAX);
	}

	@Override
	public void execute() {
		main.currentStatus = "Combining Ingredients";
		
		if (Banking.close()) {
			// combine the two items
			// gets the last of the ingredient 1 items to mix with the first of the
			// ingredient 2 items in the inventory - more efficient
			RSItem[] allIngredientOne = Inventory.find(main.ingredientOne);
			
			RSItem lastIngredientOneInInv = null;
			
			if(allIngredientOne.length > 0) {
				lastIngredientOneInInv = allIngredientOne[allIngredientOne.length - 1];
			}

			if (lastIngredientOneInInv != null) {
				lastIngredientOneInInv.click("Use");
			}
			
			RSItem ingredientTwoInInv = ArkUtility.getInventoryItem(main.ingredientTwo);
			
			if (ingredientTwoInInv != null) {
				if (ingredientTwoInInv.click("Use")) {
					
					// wait for the make all interface, up to 2 seconds
					Timing.waitCondition(() -> ArkUtility.getMainInterface(Constants.MAKE_ALL_INTERFACE) != null && !ArkUtility.getMainInterface(Constants.MAKE_ALL_INTERFACE).isHidden(), ArkUtility.getShortTimeout());
					
					RSInterface makeAllInterface = ArkUtility.getMainInterface(Constants.MAKE_ALL_INTERFACE);
					if (makeAllInterface != null && !makeAllInterface.isHidden()) {
						//Only do this wait and press the key if the interface is there, not just if the timer timed out
						main.currentStatus = "Reaction time wait";
						// wait a very short, randomised amount of time
						ArkUtility.reactionTimeWait(main.reactionTimeMultiplier, Constants.MINIMUM_WAIT_ACCEPT_INTERFACE, Constants.MAXIMUM_WAIT_ACCEPT_INTERFACE);
						main.currentStatus = "Pressing All";
						Keyboard.pressKey(' ');
						main.lastActivityTime = System.currentTimeMillis();
						main.currentStatus = "Mixing Ingredients";
					}
				}
			}

		}
	}

}
