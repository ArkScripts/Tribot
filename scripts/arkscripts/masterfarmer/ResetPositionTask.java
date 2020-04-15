package scripts.arkscripts.masterfarmer;

import org.tribot.api.General;
import org.tribot.api2007.Player;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;
import scripts.dax_api.api_lib.DaxWalker;

public class ResetPositionTask implements Task {

	ArkMasterFarmer main = ArkMasterFarmer.getInstance();
	
	@Override
	public Priority priority() {
		return Priority.MEDIUM;
	}

	@Override
	public boolean validate() {
		return ArkUtility.getNPC(Constants.MASTER_FARMER) == null && !Player.isMoving();
	}

	@Override
	public void execute() {
		try {
			main.currentStatus = "Resetting Position";
			DaxWalker.getInstance().walkTo(Constants.MARKETPLACE);
		} catch (Exception e) {
			General.println("Dax Walker said: " + e.getMessage());
		}
	}

}
