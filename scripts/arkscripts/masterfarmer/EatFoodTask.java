package scripts.arkscripts.masterfarmer;

import org.tribot.api2007.Combat;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;

public class EatFoodTask implements Task {

	private ArkMasterFarmer main = ArkMasterFarmer.getInstance();

	@Override
	public Priority priority() {
		return Priority.MEDIUM;
	}

	@Override
	public boolean validate() {
		return Combat.getHPRatio() <= main.pctToEatAt;
	}

	@Override
	public void execute() {
		ArkUtility.reactionTimeWait(main.reactionTimeMultiplier, Constants.MINIMUM_TIME_BEFORE_EATING,
				Constants.MAXIMUM_TIME_BEFORE_EATING);
		if(eatFood()) {
			main.pctToEatAt = main.abc.generateEatAtHP(); // Generate a new HP percentage to eat at
		}
	}

	public Boolean eatFood() {
		main.currentStatus = "Eating Food";
		return ArkUtility.checkForInInventoryAndInteract(main.foodID, "Eat");
	}

}
