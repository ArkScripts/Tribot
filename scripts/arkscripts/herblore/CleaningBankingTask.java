package scripts.arkscripts.herblore;

import org.tribot.api.General;
import org.tribot.api2007.Banking;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Priority;
import scripts.api.ark.Task;

public class CleaningBankingTask implements Task {

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
		return ArkUtility.getInventoryItem(main.ingredientOne) == null;
	}

	@Override
	public void execute() {
		bankItems();
	}

	public void bankItems() {

		main.currentStatus = "Banking...";
		main.checkIfInventoryTotalValueChanged();

		// Open the bank
		if (Banking.openBank()) {
			// Deposit items if we need to (this method checks if needed and then does it)
			ArkUtility.depositAllItems();

			main.currentStatus = "Withdrawing Ingredients";

			if (Banking.find(main.ingredientOne).length == 0) {
				General.println("[End Case] We ran out of Ingredients.");
				main.runScript = false;
			} else {
				ArkUtility.withdrawFromBank(0, main.ingredientOne);
			}
			ArkHerblore.getInstance().lastInventoryValue = ArkUtility.getPriceOfInventory();
			ArkUtility.closeBank(main.useEscapeExitBanking);
			main.abcCheck();
		}
	}
}
