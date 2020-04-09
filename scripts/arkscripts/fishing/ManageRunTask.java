package scripts.arkscripts.fishing;

import org.tribot.api2007.Game;
import org.tribot.api2007.Options;

import scripts.api.ark.Priority;
import scripts.api.ark.Task;

public class ManageRunTask implements Task {

	private ArkFishing main = ArkFishing.getInstance();
	
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
		if (Options.setRunEnabled(true)) {
			main.currentStatus = "Activating Run";
			main.pctToRunAt = ArkFishing.getInstance().abc.generateRunActivation();
		}
	}
}
