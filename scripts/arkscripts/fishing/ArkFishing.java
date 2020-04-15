package scripts.arkscripts.fishing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.tribot.api.General;
import org.tribot.api.input.Mouse;
import org.tribot.api.util.abc.ABCUtil;
import org.tribot.api2007.Skills.SKILLS;
import org.tribot.api2007.types.RSTile;
import org.tribot.script.Script;
import org.tribot.script.ScriptManifest;
import org.tribot.script.interfaces.MessageListening07;
import org.tribot.script.interfaces.MousePainting;
import org.tribot.script.interfaces.Painting;
import org.tribot.script.interfaces.Starting;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.Task;
import scripts.api.ark.TaskSet;
import scripts.dax_api.api_lib.WebWalkerServerApi;
import scripts.dax_api.api_lib.models.DaxCredentials;
import scripts.dax_api.api_lib.models.DaxCredentialsProvider;

@ScriptManifest(authors = {"Marcusihno"}, category = "Fishing", name = "ARKFisher", version = 1.15, description = "Fast, safe AIO Fishing trainer with dropping and banking. ABC2/10 compliant.", gameMode = 1)

public class ArkFishing extends Script implements Starting, Painting, MessageListening07, MousePainting {

	public static ArkFishing instance;

	public int lastXPCount = 0;
	public int lastInventoryValue = 0;
	
	//GUI-chosen variables
	public FishingModel chosenFishingModel;
	public float reactionWaitMultiplier = 1;
	
	public ArrayList<RSTile> fishingTilesIveBeenTo = new ArrayList<RSTile>();

	public int pctToRunAt = 0;
	
	public ABCUtil abc;

	public boolean runScript = true;
	public boolean guiEnded = false;
	
	public TaskSet tasks;
	
	//static factory method for obtaining our main instance, allowing us to avoid static variables
    public static ArkFishing getInstance() {
        return instance;
    }

	@Override
	public void onStart() {
		currentStatus = "Starting Up...";
		
		// initiate antiban
		abc = new ABCUtil();
		
		pctToRunAt = abc.generateRunActivation();
		
		// set initial xp variables
		startFishingXP = SKILLS.FISHING.getXP();
		startFishingLevel = SKILLS.FISHING.getActualLevel();
		lastXPCount = startFishingXP;
		
		General.println("Initialising ArkFisher");
		currentStatus = "Initialising Web Walker";
		//Credentials hidden as I use a purchased key
		WebWalkerServerApi.getInstance().setDaxCredentialsProvider(new DaxCredentialsProvider() {
			public DaxCredentials getDaxCredentials() {
				return new DaxCredentials("", "");
			}
		});
		
		instance = this;
		
		// start swing GUI
		FishingGUI.initiatliseGUI();
	}

	@Override
	public void run() {

		while (runScript) {
			if (guiEnded) {
				//using Encoded's task framework
				Task task = tasks.getValidTask();
				if (task != null) {
					task.execute();
				} else {
					//when resting, we should check our abc2 status
					abcCheck();
				}

				// if we get an increase in xp, add a successful count to our fish caught counter
				if (SKILLS.FISHING.getXP() > lastXPCount) {
					timesSucceeded++;
					lastXPCount = SKILLS.FISHING.getXP();
				}
				
			} else {
				abcCheck();
				currentStatus = "Waiting for GUI completion...";
			}
		}
	}

	public void checkIfInventoryTotalValueChanged() {
		//checks if our inventory total value has gone up or down for profit calculation
		int priceOfInventory = ArkUtility.getPriceOfInventory();
		totalProfitOrLoss += (priceOfInventory - lastInventoryValue);
		lastInventoryValue = priceOfInventory;
	}

	public void abcCheck() {
		if (abc.shouldCheckTabs()) {
			currentStatus = "ABC2: Checking Tabs";
			General.println("[Antiban] ABC2: Checking Tabs");
			abc.checkTabs();
		}

		if (abc.shouldCheckXP()) {
			abc.checkXP();
			currentStatus = "ABC2: Checking XP";
			General.println("[Antiban] ABC2: Checking XP");
		}

		if (abc.shouldExamineEntity()) {
			abc.examineEntity();
			currentStatus = "ABC2: Examining Entities";
			General.println("[Antiban] ABC2: Examining Entities");
		}

		if (abc.shouldMoveMouse()) {
			abc.moveMouse();
			currentStatus = "ABC2: Randomly moving mouse";
			General.println("[Antiban] ABC2: Randomly moving mouse");
		}

		if (abc.shouldPickupMouse()) {
			abc.pickupMouse();
			currentStatus = "ABC2: Picking up mouse";
			General.println("[Antiban] ABC2: Picking up mouse");
		}

		if (abc.shouldRightClick()) {
			abc.rightClick();
			currentStatus = "ABC2: Right clicking";
			General.println("[Antiban] ABC2: Right clicking");
		}

		if (abc.shouldRotateCamera()) {
			abc.rotateCamera();
			currentStatus = "ABC2: Rotating camera";
			General.println("[Antiban] ABC2: Rotating camera");
		}

		if (abc.shouldLeaveGame()) {
			abc.leaveGame();
			currentStatus = "ABC2: Leaving client screen";
			General.println("[Antiban] ABC2: Leaving client screen");
		}
	}

	
	//paint-related things
	private int startFishingXP = 0;
	private int startFishingLevel = 0;
	private int fishingGained = 0;
	private int fishingRate = 0;

	private int totalProfitOrLoss = 0;
	private int financeRate = 0;
	private int fishCaughtRate = 0;
	
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

	
	//Images temporarily removed
	final Image paintBg = getImage("");

	final Image logo = getImage("");

	final Image timeIcon = getImage("");

	final Image totalIcon = getImage("");

	final Image cursor = getImage("");

	final Image lootIcon = getImage("");

	final Image fishingIcon = getImage("");

	private final RenderingHints aa = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
			RenderingHints.VALUE_ANTIALIAS_ON);

	Font myMainFont = new Font("Calibri", 1, 12);

	public void onPaint(Graphics ui) {
		
		Graphics2D gg = (Graphics2D) ui;
		gg.setRenderingHints(aa);

		ui.setFont(myMainFont);

		ui.setColor(new Color(1, 1, 1, 0.4f));
		ui.drawImage(paintBg, 275, 208, 240, 130, null);

		ui.drawImage(logo, 282, 213, 185, 33, null);

		ui.setColor(Color.white);
		ui.drawString("Status: " + currentStatus, 284, 260);

		ui.setColor(Color.LIGHT_GRAY);
		ui.drawImage(timeIcon, 284, 268, 10, 10, null);
		ui.drawString("Runtime: " + ArkUtility.millisToTime(this.getRunningTime()), 298, 277);

		fishingGained = SKILLS.FISHING.getXP() - startFishingXP;

		ui.drawImage(fishingIcon, 284, 285, 10, 10, null);
		fishingRate = (int) Math.round(fishingGained / ArkUtility.millisToHours(this.getRunningTime()));
		int levelsGained = SKILLS.FISHING.getActualLevel() - startFishingLevel;
		ui.drawString("Fishing (" + SKILLS.FISHING.getActualLevel() + " (+" + levelsGained + ")): +"
				+ (ArkUtility.toReadableNumber(fishingGained, 0)) + " xp ("
				+ ArkUtility.toReadableNumber(fishingRate, 0) + "/hour)", 298, 294);

		ui.drawImage(lootIcon, 284, 302, 10, 10, null);
		if (totalProfitOrLoss != 0) {
			financeRate = (int) Math.round(totalProfitOrLoss / ArkUtility.millisToHours(this.getRunningTime()));
		} else {
			financeRate = 0;
		}
		ui.drawString("Profit/Loss: " + ArkUtility.toReadableNumber(totalProfitOrLoss, 0) + " ("+ ArkUtility.toReadableNumber(financeRate, 0) + " gp/hour)", 298, 311);


		ui.drawImage(totalIcon, 284, 319, 10, 10, null);
		fishCaughtRate = (int) Math.round(timesSucceeded / ArkUtility.millisToHours(this.getRunningTime()));
		ui.drawString(
				"Fish caught: " + timesSucceeded + " (" + fishCaughtRate + " fish/hour)",
				298, 328);

	}

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
