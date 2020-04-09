package scripts.arkscripts.fishing;

import org.tribot.api.General;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSTile;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;
import scripts.dax_api.api_lib.DaxWalker;

public class NavigateBackToFishingSpot implements Task {

	private ArkFishing main = ArkFishing.getInstance();

	@Override
	public Priority priority() {
		return Priority.HIGH;
	}

	@Override
	public boolean validate() {
		// Called either if we have just banked, but also if we find ourselves not near
		// any more fishing spots - the script will return to a previous spot that it
		// has been to before where it had been successful in fishing, or our starting
		// tile
		return !Inventory.isFull() && NPCs.find(Filters.NPCs
				.actionsContains(ArkFishing.getInstance().chosenFishingModel.getInteractionString())).length == 0;
	}

	@Override
	public void execute() {
		RSTile selectedTile = ArkUtility.approximateNearTile(ArkUtility.getRandomTileFromList(main.fishingTilesIveBeenTo), 2);

		if (Player.getPosition().distanceTo(selectedTile) > Constants.MAX_DISTANCE_FROM_KNOWN_TILE) {
			try {
				main.currentStatus = "Navigating back to fishing spot";
				DaxWalker.getInstance().walkTo(selectedTile);
			} catch (Exception e) {
				General.println("Dax Walker wasn't happy.");
			}
		} else {
			main.currentStatus = "Waiting for a fishing spot to spawn";
		}
	}

}
