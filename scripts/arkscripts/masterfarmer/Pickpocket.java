package scripts.arkscripts.masterfarmer;

import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSNPC;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;

public class Pickpocket implements Task {

	public ArkMasterFarmer main = ArkMasterFarmer.getInstance();

	@Override
	public Priority priority() {
		return Priority.LOW;
	}

	@Override
	public boolean validate() {
		return ArkUtility.getNPC(Constants.MASTER_FARMER) != null
				&& Timing.timeFromMark(main.timeOfStun) > main.randomStunTime;
	}

	@Override
	public void execute() {
		RSNPC masterFarmer = ArkUtility.getNPC(Constants.MASTER_FARMER);

		main.currentStatus = "Pickpocketing";
		if (masterFarmer != null && !masterFarmer.isOnScreen()) {
			main.currentStatus = "Walking to Master Farmer";
			Walking.blindWalkTo(masterFarmer.getPosition());
		} else if (masterFarmer != null && masterFarmer.isOnScreen()) {
			main.currentStatus = "Pickpocketing...";
			if (DynamicClicking.clickRSNPC(masterFarmer, "Pickpocket")) {
				General.sleep(Math.round(Constants.MINIMUM_WAIT_AFTER_PICKPOCKET * main.reactionTimeMultiplier),
						Math.round(Constants.MAXIMUM_WAIT_AFTER_PICKPOCKET * main.reactionTimeMultiplier));
				
			}
		}

	}

}
