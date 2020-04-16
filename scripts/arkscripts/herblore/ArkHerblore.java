package scripts.arkscripts.herblore;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Player;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.MousePainting;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Starting;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Task;
import scripts.api.ark.TaskSet;

@ScriptManifest(authors = {
		"Marcusihno" }, category = "Herblore", name = "ARKHerblore", version = 1.25, description = "Fast, safe Herblore trainer with Necklace of Chemistry support. ABC2/10 compliant.", gameMode = 1)

public class ArkHerblore extends Script implements Starting, Painting, MessageListening07, MousePainting {

	public static ArkHerblore instance;

	public int lastXPCount = 0;
	public long lastActivityTime = 0;
	public int lastInventoryValue = 0;

	// GUI-set variables
	public int[] ingredientOne;
	public int[] ingredientTwo;
	public boolean useAmuletOfChemistry = false;
	public float reactionTimeMultiplier = 1;
	public boolean useEscapeExitBanking = false;

	public ABCUtil abc;

	public boolean runScript = true;
	public boolean guiEnded = false;

	public TaskSet tasks;

	// static factory method for obtaining our main instance, allowing us to avoid
	// static variables
	public static ArkHerblore getInstance() {
		return instance;
	}

	@Override
	public void onStart() {
		currentStatus = "Starting Up...";

		// initiate antiban
		abc = new ABCUtil();

		// set initial xp variables
		startHerbloreXP = SKILLS.HERBLORE.getXP();
		startHerbloreLevel = SKILLS.HERBLORE.getActualLevel();
		lastXPCount = startHerbloreXP;

		instance = this;

		// start swing GUI
		HerbloreGUI.initiatliseGUI();
	}

	@Override
	public void run() {

		while (runScript) {
			if (guiEnded) {
				// Using Encoded's task framework
				Task task = tasks.getValidTask();
				if (task != null) {
					task.execute();
				}

				// When resting, we should check our abc2 status
				abcCheck();
				
				// If we get an increase in xp, add a successful count and reset our idle timer
				if (SKILLS.HERBLORE.getXP() > lastXPCount) {
					timesSucceeded++;
					lastXPCount = SKILLS.HERBLORE.getXP();
					lastActivityTime = System.currentTimeMillis();
				}

				// Another easy way of resetting our idle timer - useful when mixing unfinished
				// potions which do not give xp
				if (Player.getAnimation() != Constants.IDLE_ANIMATION) {
					lastActivityTime = System.currentTimeMillis();
				}

			} else {
				abcCheck();
				currentStatus = "Waiting for GUI completion...";
			}
		}
	}

	public void checkIfInventoryTotalValueChanged() {
		// Checks if our inventory total value has gone up or down for profit
		// calculation
		int priceOfInventory = ArkUtility.getPriceOfInventory();
		totalProfitOrLoss += (priceOfInventory - lastInventoryValue);
		lastInventoryValue = priceOfInventory;
	}

	public void abcCheck() {
		if (abc.shouldLeaveGame()) {
			currentStatus = "ABC2: Leaving client screen";
			General.println("[Antiban] ABC2: Leaving client screen");
			abc.leaveGame();
		}
		
		if (abc.shouldCheckTabs()) {
			currentStatus = "ABC2: Checking Tabs";
			General.println("[Antiban] ABC2: Checking Tabs");
			abc.checkTabs();
		}

		if (abc.shouldCheckXP()) {
			currentStatus = "ABC2: Checking XP";
			General.println("[Antiban] ABC2: Checking XP");
			abc.checkXP();
		}

		if (abc.shouldExamineEntity()) {
			currentStatus = "ABC2: Examining Entities";
			General.println("[Antiban] ABC2: Examining Entities");
			abc.examineEntity();
		}

		if (abc.shouldMoveMouse()) {
			currentStatus = "ABC2: Randomly moving mouse";
			General.println("[Antiban] ABC2: Randomly moving mouse");
			abc.moveMouse();
		}

		if (abc.shouldPickupMouse()) {
			currentStatus = "ABC2: Picking up mouse";
			General.println("[Antiban] ABC2: Picking up mouse");
			abc.pickupMouse();
		}

		if (abc.shouldRightClick()) {
			currentStatus = "ABC2: Right clicking";
			General.println("[Antiban] ABC2: Right clicking");
			abc.rightClick();
		}

		if (abc.shouldRotateCamera()) {
			currentStatus = "ABC2: Rotating camera";
			General.println("[Antiban] ABC2: Rotating camera");
			abc.rotateCamera();
		}
	}

	// paint-related things
	private int startHerbloreXP = 0;
	private int startHerbloreLevel = 0;
	private int herbloreGained = 0;
	private int herbloreRate = 0;

	private int totalProfitOrLoss = 0;
	private int financeRate = 0;
	private int potionsRate = 0;

	public int timesSucceeded = 0;
	public String currentStatus = "";

	private Image getImage(String url) {
		try {
			return ImageIO.read(new URL(url));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Images temporarily removed
	final Image paintBg = getImage("");

	final Image logo = getImage("");

	final Image timeIcon = getImage("");

	final Image totalIcon = getImage("");

	final Image cursor = getImage("");

	final Image lootIcon = getImage("");

	final Image herbloreIcon = getImage("");

	private final RenderingHints aa = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);

	Font myMainFont = new Font("Calibri", 1, 12);

	public void onPaint(Graphics ui) {

		Graphics2D gg = (Graphics2D) ui;
		gg.setRenderingHints(aa);

		ui.setFont(myMainFont);

		ui.setColor(new Color(1, 1, 1, 0.4f));
		ui.drawImage(paintBg, 275, 208, 240, 130, null);

		ui.drawImage(logo, 282, 213, 200, 33, null);

		ui.setColor(Color.white);
		ui.drawString("Status: " + currentStatus, 284, 260);

		ui.setColor(Color.LIGHT_GRAY);
		ui.drawImage(timeIcon, 284, 268, 10, 10, null);
		ui.drawString("Runtime: " + ArkUtility.millisToTime(this.getRunningTime()), 298, 277);

		herbloreGained = SKILLS.HERBLORE.getXP() - startHerbloreXP;

		ui.drawImage(herbloreIcon, 284, 285, 10, 10, null);
		herbloreRate = (int) Math.round(herbloreGained / ArkUtility.millisToHours(this.getRunningTime()));
		int levelsGained = SKILLS.HERBLORE.getActualLevel() - startHerbloreLevel;
		ui.drawString("Herblore (" + SKILLS.HERBLORE.getActualLevel() + " (+" + levelsGained + ")): +"
				+ (ArkUtility.toReadableNumber(herbloreGained, 0)) + " xp ("
				+ ArkUtility.toReadableNumber(herbloreRate, 0) + "/hour)", 298, 294);

		ui.drawImage(lootIcon, 284, 302, 10, 10, null);
		if (totalProfitOrLoss != 0) {
			financeRate = (int) Math.round(totalProfitOrLoss / ArkUtility.millisToHours(this.getRunningTime()));
		} else {
			financeRate = 0;
		}
		ui.drawString("Profit/Loss: " + ArkUtility.toReadableNumber(totalProfitOrLoss, 0) + " ("
				+ ArkUtility.toReadableNumber(financeRate, 0) + " gp/hour)", 298, 311);

		ui.drawImage(totalIcon, 284, 319, 10, 10, null);
		potionsRate = (int) Math.round(timesSucceeded / ArkUtility.millisToHours(this.getRunningTime()));
		ui.drawString(
				"Potions Made: " + timesSucceeded + " (" + ArkUtility.toReadableNumber(potionsRate, 0) + " pots/hour)",
				298, 328);

	}

	/**
	 * Defines how to draw the mouse image and where in the paintMouse class
	 * 
	 */
	public void drawMouse(Graphics g) {
		g.setColor(Color.BLACK);
		int mouseY = (int) Mouse.getPos().getY();
		int mouseX = (int) Mouse.getPos().getX();
		g.drawImage(cursor, mouseX, mouseY, 30, 30, null);
	}

	@Override
	public void paintMouse(Graphics g, Point arg1, Point arg2) {
		drawMouse(g);
	}

}
