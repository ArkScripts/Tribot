package scripts.arkscripts.fishing;

import org.tribot.api.Timing;
import org.tribot.api2007.NPCChat;
import org.tribot.api2007.NPCs;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSNPC;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;

public class BasicFishingInteraction implements Task {

	private ArkFishing main = ArkFishing.getInstance();

	@Override
	public Priority priority() {
		return Priority.LOW;
	}

	@Override
	public boolean validate() {
		return !isFishing() && !Player.isMoving();
	}

	private Boolean isFishing() {
		// Checks if our array containing all of the possible fishing animations
		// contains our current animation, whether we are interacting with an NPC
		// (fishing spots are NPCs) and whether we have a level up message
		return ArkUtility.arrayContainsInt(Constants.FISHING_ANIMATIONS, Player.getAnimation())
				&& Player.getRSPlayer().getInteractingCharacter() != null && NPCChat.getMessage() == null;
	}

	@Override
	public void execute() {
		
		main.currentStatus = "Reaction time wait before Fishing...";
		
		ArkUtility.reactionTimeWait(main.reactionWaitMultiplier, Constants.MINIMUM_REACTION_TIME_WAIT, Constants.MAXIMUM_REACTION_TIME_WAIT);

		RSNPC[] ourSpot = NPCs.find(Filters.NPCs.actionsContains(main.chosenFishingModel.getInteractionString()));

		if (ourSpot.length > 0) {

			// Determine the interaction string to use on this spot
			String interactionStringToUse = "";
			for (String action : ourSpot[0].getActions()) {
				for (String interactionOptions : main.chosenFishingModel.getInteractionString()) {
					if (action.equals(interactionOptions)) {
						interactionStringToUse = action;
						break;
					}
				}
			}

			main.currentStatus = "Interacting with fishing spot";

			if (ArkUtility.interactWithEntity(ourSpot[0], interactionStringToUse)) {
				// Waits until our animation is a fishing animation
				Timing.waitCondition(
						() -> ArkUtility.arrayContainsInt(Constants.FISHING_ANIMATIONS, Player.getAnimation()),
						ArkUtility.SHORT_TIMEOUT);

				if (ArkUtility.arrayContainsInt(Constants.FISHING_ANIMATIONS, Player.getAnimation())) {
					// Add this tile to our list of potential tiles to return to if we lose
					// ourselves and can't find any more fish
					if (!main.fishingTilesIveBeenTo.contains(Player.getPosition())) {
						main.fishingTilesIveBeenTo.add(Player.getPosition());
					}
					main.currentStatus = "Fishing";
				}

			}

		} else {
			main.currentStatus = "Waiting for a spot to spawn";
		}
	}

}
