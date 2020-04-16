package scripts.api.ark;

import org.tribot.api.Timing;
import org.tribot.api.interfaces.Clickable;
import org.tribot.api2007.Camera;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.PathFinding;
import org.tribot.api2007.Player;
import org.tribot.api2007.Walking;
import org.tribot.api2007.types.RSCharacter;
import org.tribot.api2007.types.RSGroundItem;
import org.tribot.api2007.types.RSItem;
import org.tribot.api2007.types.RSModel;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import org.tribot.api2007.util.DPathNavigator;

import scripts.dax_api.walker.utils.AccurateMouse;

public class ArkInteraction {


	private static final int[] VIAL = { 229 };
	private static DPathNavigator navigator = new DPathNavigator();
	
	/**
	 * Interacts with an item in our inventory using the defined interaction string.
	 * Also drops any vials left behind after the interaction is complete.
	 * 
	 * @param lookingForID The ID of the item to interact with - will interact with
	 *                     the first item found
	 * @param interaction  The interaction string to use
	 * @return If we successfully carried out the interaction required.
	 */
	public static Boolean checkForInInventoryAndInteract(int[] lookingForID, String interaction) {

		RSItem item = ArkUtility.getInventoryItem(lookingForID);

		if (item != null) {

			int originalCount = Inventory.find(item.getID()).length;

			if (item.click(interaction)) {
				Timing.waitCondition(() -> Inventory.find(item.getID()).length != originalCount, ArkUtility.getShortTimeout());
				if (ArkUtility.getInventoryItem(VIAL) != null) {
					Inventory.drop(VIAL);
				}
				return true;
			}
		}

		return false;
	}


	/**
	 * Equips an item and checks if it succeeded.
	 * 
	 * @return The equipped item is now present in the equipment slot.
	 */
	public static Boolean equipItem(int[] itemId, RSItem currentSlotItem) {
		if (ArkUtility.getInventoryItem(itemId) != null
				&& ArkUtility.getInventoryItem(itemId).click(ArkUtility.getEquipString(itemId))) {
			Timing.waitCondition(() -> ArkUtility.getEquipmentItem(itemId) != null, ArkUtility.getShortTimeout());
		}
		return ArkUtility.getInventoryItem(new int[] { currentSlotItem.getID() }) != null;
	}
	
	/**
	 * Rotates to the required RSModel and attempts to make sure it is on screen.
	 * Checks it successful and returns
	 * 
	 * @param target The NPC to look at
	 * @return Whether the target Model is clickable (aka, on screen and
	 *         interactable)
	 */
	public static Boolean rotateToAndAngle(RSModel target) {
		if (target != null && !target.isClickable()) {
			Camera.turnToTile(target);
			// If still not on screen
			if (target != null && !target.isClickable()) {
				int angle = Camera.getOptimalAngleForPositionable(target);
				Camera.setCameraAngle(angle);
			}
		}

		return target != null && target.isClickable();
	}

	/**
	 * Checks that the RSModel we are trying to reach is able to be reached and
	 * attempts to reach it if not possible using DPathNavigator's travers (which
	 * handles doors)
	 * 
	 * @param entity The target Model to reach
	 * @return Whether the target model has been made reachable
	 */
	public static Boolean makeSureWeCanReachEntity(RSModel entity) {

		RSTile tileNearEntity = ArkUtility.approximateNearTile(entity.getPosition(), 1);
		if (entity != null && !PathFinding.canReach(tileNearEntity, true)) {
			if (navigator.traverse(tileNearEntity)) {
				ArkUtility.waitForWalkTo();
			}
		}
		return entity != null && PathFinding.canReach(tileNearEntity, true);
	}

	/**
	 * If more than 8 spaces away, will attempt to walk to the target to get it on
	 * screen. If closer, it will call rotateToAndAngle, which attempts to rotate
	 * the camera to view the target model.
	 * 
	 * @param entity The model we are trying to cause to be clickable
	 * @return If the target is clickable, returns true.
	 */
	public static Boolean attemptToGetEntityOnScreen(RSModel entity) {
		if (entity != null && !entity.isClickable()) {
			if (entity.getPosition().distanceTo(Player.getPosition()) > 8 || !rotateToAndAngle(entity)) {
				if (!Player.isMoving()) {
					Walking.blindWalkTo(entity.getPosition());
				}
			}
		}
		return entity != null && entity.isClickable();
	}

	/**
	 * All in one method that will attempt to interact with a clickable entity using
	 * the target string. Will null check, identify the type, make sure the entity
	 * can be reached, attempt to force the entity to be on screen and then interact
	 * using Dax's AccurateMouse class.
	 * 
	 * @param entity      The clickable entity we are attempting to interact with
	 * @param interaction The interaction string to use
	 * 
	 * @return Whether we successfully interacted with the entity.
	 */
	public static Boolean interactWithEntity(Clickable entity, String interaction) {
		boolean isObject = false;
		if (entity != null) {
			RSModel model = null;
			if (entity instanceof RSCharacter) {
				RSCharacter rsCharacter = ((RSCharacter) entity);
				model = rsCharacter.getModel();
			} else if (entity instanceof RSGroundItem) {
				RSGroundItem rsGroundItem = ((RSGroundItem) entity);
				model = rsGroundItem.getModel();
			} else if (entity instanceof RSObject) {
				isObject = true;
				RSObject rsObject = ((RSObject) entity);
				model = rsObject.getModel();
			}

			if (!isObject || makeSureWeCanReachEntity(model)) {
				if (attemptToGetEntityOnScreen(model)) {
					if (AccurateMouse.click(entity, interaction)) {
						ArkUtility.waitForWalkTo();
						return true;
					}
				}
			}
		}
		return false;
	}

	
}
