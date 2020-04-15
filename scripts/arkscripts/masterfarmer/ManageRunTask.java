package scripts.arkscripts.masterfarmer;

import org.tribot.api2007.Game;
import org.tribot.api2007.Options;

import scripts.api.ark.Priority;
import scripts.api.ark.Task;

public class ManageRunTask implements Task {

	private ArkMasterFarmer main = ArkMasterFarmer.getInstance();
	
	@Override
	public Priority priority() {
		return Priority.HIGH;
	}

	@Override
	public boolean validate() {
		return !Game.isRunOn() && Game.getRunEnergy() >= main.pctToRunAt;
	}

	@Override
	public void execute() {
		//turn on run
		if (Options.setRunEnabled(true)) {
			main.pctToRunAt = main.abc.generateRunActivation();
		}
	}
}
