package scripts.arkscripts.herblore;

import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api2007.Banking;
import org.tribot.api2007.Equipment;
import org.tribot.api2007.Skills.SKILLS;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;

public class MixingBankingTask implements Task {
	
	private ArkHerblore main = ArkHerblore.getInstance();
	
    @Override
    public Priority priority() {
        return Priority.HIGH;
    }

    @Override
    public boolean validate() {
        return shouldBank();
    }
    
	public Boolean shouldBank() {
		//returns true if we don't have one of these items in our inventory
		return ArkUtility.getInventoryItem(main.ingredientOne) == null || ArkUtility.getInventoryItem(main.ingredientTwo) == null;
	}

    @Override
    public void execute() {
    	bankItems();
    }
    
	public void bankItems() {

		main.currentStatus = "Banking...";
		
		//Short, randomised sleep, controlled by the user's reaction time slider setting in GUI
		ArkUtility.reactionTimeWait(main.reactionTimeMultiplier, Constants.MINIMUM_WAIT_BANKING, Constants.MAXIMUM_WAIT_BANKING);
		
		main.checkIfInventoryTotalValueChanged();

		// open the bank
		if (Banking.openBank()) {
			//reset our activity indicators
			main.lastActivityTime = 0;
			main.lastXPCount = SKILLS.HERBLORE.getXP();
			
			// deposit items if we need to, otherwise skip - handled in my utility methods
			ArkUtility.depositAllItems();
			if (main.useAmuletOfChemistry && ArkUtility.getEquipmentItem(Constants.CHEMISTRY_NECKLACE_ID) == null) {
				main.currentStatus = "Replacing Necklace of Chemistry";
				if (Banking.find(Constants.CHEMISTRY_NECKLACE_ID).length == 0) {
					//kill the script if we ran out of amulets
					General.println("[End Case] We ran out of Amulets of Chemistry.");
					main.runScript = false;
				}
				//waits for us to replace our amulet of chemistry
				Timing.waitCondition(() -> ArkUtility.replaceEquipmentItem(Equipment.SLOTS.AMULET, Constants.CHEMISTRY_NECKLACE_ID), ArkUtility.getMediumTimeout());
			}
			main.currentStatus = "Withdrawing Ingredients";
			if (Banking.find(main.ingredientOne).length == 0 || Banking.find(main.ingredientTwo).length == 0) {
				General.println("[End Case] We ran out of Ingredients.");
				main.runScript = false;
			} else {
				ArkUtility.withdrawFromBank(14, main.ingredientOne);
				ArkUtility.withdrawFromBank(14, main.ingredientTwo);
			}
			main.lastInventoryValue = ArkUtility.getPriceOfInventory();
			ArkUtility.closeBank(main.useEscapeExitBanking);
			main.abcCheck();
		}
	}
    
}
