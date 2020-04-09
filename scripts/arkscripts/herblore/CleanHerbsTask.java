package scripts.arkscripts.herblore;

import org.tribot.api.General;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;

public class CleanHerbsTask implements Task {

	private int RANDOM_HERB_MISS_MAX_INT = 110;
	
	private ArkHerblore main = ArkHerblore.getInstance();

	@Override
	public Priority priority() {
		return Priority.MEDIUM;
	}

	@Override
	public boolean validate() {
		return ArkUtility.getInventoryItem(main.ingredientOne) != null;
	}

	@Override
	public void execute() {

		main.currentStatus = "Cleaning Herbs";

		if (Banking.close()) {
			// get all of the herbs in our inventory
			RSItem[] allHerbs = Inventory.find(main.ingredientOne);

			if (allHerbs.length > 0) {
				for (RSItem herb : allHerbs) {
					if (herb != null && ArkUtility.sameIdAs(herb.getID(), main.ingredientOne)) {
						// randomly miss a herb and then go back to clean it after - 1 in 111 chance of
						// missing a herb
						int randomNumber = General.random(0, RANDOM_HERB_MISS_MAX_INT);
						if (randomNumber != RANDOM_HERB_MISS_MAX_INT) {
							if (herb.click("Clean")) {
								General.sleep((Constants.MINIMUM_WAIT_HERB_CLEAN * main.reactionTimeMultiplier),
										(Constants.MAXIMUM_WAIT_HERB_CLEAN * main.reactionTimeMultiplier));
								main.timesSucceeded++;
								main.abcCheck();
							}
						}

					}
				}
				// Short, randomised sleep after all herbs have been cleaned, controlled by the
				// user's reaction time slider setting in GUI - allows the script to wait and
				// assess if any herbs were missed that it needs to go back for
				ArkUtility.reactionTimeWait(main.reactionTimeMultiplier, Constants.MINIMUM_WAIT_BANKING,
						Constants.MAXIMUM_WAIT_BANKING);
			}

		}
	}

}
