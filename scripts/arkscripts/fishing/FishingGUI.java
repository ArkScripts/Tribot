package scripts.arkscripts.fishing;

import java.awt.EventQueue;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SwingConstants;

import org.tribot.api.General;
import org.tribot.api2007.Player;
import org.tribot.util.Util;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.TaskSet;

public class FishingGUI {

	private static JFrame frmArkFishingAio;
	private static JButton startButton;
	
	private static JRadioButton droppingRadioButton;
	private static JRadioButton bankingRadioButton;
	private static JComboBox<String> modeChoiceComboBox;
	private static JSlider reactionTimeSlider;
	
	private String[] modes = new String[] {"Small Net Fishing (eg. Shrimp, Anchovies)", "Fly Fishing (eg. Trout, Salmon)", "Barbarian Fishing (eg. Leaping Trout, Leaping Salmon)", "Caging (eg. Lobsters)", "Harpooning (eg. Swordfish/Shark)", "Big Net Fishing (eg. Bass, Mackerel)"};

	public static final File PATH = new File(Util.getWorkingDirectory().getAbsolutePath(),
			"arkfishingaio_" + "settings.ini");

	private static Properties prop;
	private final ButtonGroup buttonGroup = new ButtonGroup();

	/**
	 * Launch the application.
	 */
	public static void initiatliseGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FishingGUI window = new FishingGUI();
					window.frmArkFishingAio.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	public FishingGUI() throws MalformedURLException, IOException {
		FishingGUI.prop = new Properties();
		initialize();
	}

	private static void saveSettings() {
		try {
			prop.clear();
			
			prop.put("droppingRadioButton", String.valueOf(droppingRadioButton.isSelected()));
			prop.put("bankingRadioButton", String.valueOf(bankingRadioButton.isSelected()));
			
			prop.put("modeChoiceComboBox", String.valueOf(modeChoiceComboBox.getModel().getSelectedItem()));
			
			prop.put("reactionTimeSlider", String.valueOf(reactionTimeSlider.getValue()));

			prop.store(new FileOutputStream(FishingGUI.PATH), "ARKFishing AIO");
			General.println("Saved GUI settings at: " + FishingGUI.PATH);
		} catch (Exception e1) {
			General.println("Unable to save settings");
			e1.printStackTrace();
		}
	}

	public void loadSettings() {
		try {
			if (!FishingGUI.PATH.exists()) { // make sure file exists
				General.println("[GUI Loading] Settings path doesn't exist");
				FishingGUI.PATH.createNewFile(); // or make a new one
				
				reactionTimeSlider.setValue(2);
			}
			prop.load(new FileInputStream(FishingGUI.PATH));
			
			String[] checkBoxNames = { "droppingRadioButton", "bankingRadioButton"};
			JRadioButton[] boxes = { droppingRadioButton, bankingRadioButton };
			for (int i = 0; i < checkBoxNames.length; i++) {
				String value = prop.getProperty(checkBoxNames[i]);
				boxes[i].setSelected(Boolean.valueOf(value));
			}
			
			modeChoiceComboBox.setSelectedItem(prop.getProperty("modeChoiceComboBox"));
			
			reactionTimeSlider.setValue(10);

		} catch (Exception e2) {
			System.out.print("Unable to load settings");
			e2.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	private void initialize() throws MalformedURLException, IOException {
		frmArkFishingAio = new JFrame();
		frmArkFishingAio.setTitle("ARKFishing AIO");
		frmArkFishingAio.setBounds(100, 100, 450, 423);
		frmArkFishingAio.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmArkFishingAio.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 414, 362);
		frmArkFishingAio.getContentPane().add(panel);
		panel.setLayout(null);
		
		BufferedImage img = ImageIO.read(new URL("https://simplemed.co.uk/images/Tribot/ArkFisherLogoV4.png"));
		ImageIcon icon = new ImageIcon(img);
		
		JLabel iconLabel = new JLabel("");
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel.setBounds(10, 11, 394, 67);
		iconLabel.setIcon(icon);
		panel.add(iconLabel);

		JLabel welcomeLabel = new JLabel("<html><b>Welcome to ARKFisher by Marcusihno</b></html>");
		welcomeLabel.setBounds(45, 89, 322, 14);
		panel.add(welcomeLabel);
		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel modeLabel = new JLabel("Start at your desired fishing location, select your settings and press start!");
		modeLabel.setBounds(10, 101, 404, 30);
		modeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(modeLabel);


		
		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(SystemColor.menu);
		mainPanel.setBounds(10, 152, 394, 160);
		panel.add(mainPanel);
		mainPanel.setLayout(null);
		
		JLabel modeChoiceLabel = new JLabel("<html><b>Choose a mode:</b><html>");
		modeChoiceLabel.setBounds(10, 11, 106, 20);
		mainPanel.add(modeChoiceLabel);
		
		modeChoiceComboBox = new JComboBox(modes);
		modeChoiceComboBox.setBounds(10, 41, 374, 22);
		mainPanel.add(modeChoiceComboBox);
		
		droppingRadioButton = new JRadioButton("Drop");
		droppingRadioButton.setSelected(true);
		buttonGroup.add(droppingRadioButton);
		droppingRadioButton.setBounds(10, 104, 89, 23);
		mainPanel.add(droppingRadioButton);
		
		bankingRadioButton = new JRadioButton("Bank");
		buttonGroup.add(bankingRadioButton);
		bankingRadioButton.setBounds(10, 130, 89, 23);
		mainPanel.add(bankingRadioButton);
		
		reactionTimeSlider = new JSlider();
		reactionTimeSlider.setMajorTickSpacing(2);
		reactionTimeSlider.setPaintTicks(true);
		reactionTimeSlider.setSnapToTicks(true);
		reactionTimeSlider.setMaximum(20);
		reactionTimeSlider.setMinimum(0);
		reactionTimeSlider.setBounds(194, 119, 190, 26);
		mainPanel.add(reactionTimeSlider);
		
		JLabel lblNewLabel_1 = new JLabel("<html><b>Reaction Times</b><html>");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setBounds(194, 74, 190, 14);
		mainPanel.add(lblNewLabel_1);
		
		JLabel lblLeftFaster = new JLabel("(Left = Faster | Right = Slower)");
		lblLeftFaster.setHorizontalAlignment(SwingConstants.CENTER);
		lblLeftFaster.setBounds(194, 93, 190, 20);
		mainPanel.add(lblLeftFaster);
		
		JLabel lblNewLabel = new JLabel("What should we do with the fish?");
		lblNewLabel.setBounds(10, 83, 164, 14);
		mainPanel.add(lblNewLabel);
		
		loadSettings();

		startButton = new JButton("Start!");
		startButton.setBounds(10, 323, 394, 28);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startScript();
			}
		});
		panel.add(startButton);
		
		JLabel lblDoNotWear = new JLabel("Do not wear any teleportation items eg. Games Necklace if using banking.");
		lblDoNotWear.setHorizontalAlignment(SwingConstants.CENTER);
		lblDoNotWear.setBounds(10, 123, 404, 30);
		panel.add(lblDoNotWear);
		


	}

	public static FishingModel getFishingModel(String name) {
		switch (name) {
		case "Small Net Fishing (eg. Shrimp, Anchovies)":
			return new FishingModel(new String[] {"Net", "Small Net"});
		case "Fly Fishing (eg. Trout, Salmon)":
			return new FishingModel(new String[] {"Lure"});
		case "Caging (eg. Lobsters)":
			return new FishingModel(new String[] {"Cage"});
		case "Harpooning (eg. Swordfish/Shark)":
			return new FishingModel(new String[] {"Harpoon"});
		case "Barbarian Fishing (eg. Leaping Trout, Leaping Salmon)":
			return new FishingModel(new String[] {"Use-rod"});
		case "Big Net Fishing (eg. Bass, Mackerel)":
			return new FishingModel(new String[] {"Net"});
		default:
			return new FishingModel(new String[] {"Lure"});
		}
	}

	public static void startScript() {
		ArkFishing main = ArkFishing.getInstance();

		saveSettings();
		frmArkFishingAio.setVisible(false);
		// set the tasks to carry out
		main.chosenFishingModel = getFishingModel((String)modeChoiceComboBox.getModel().getSelectedItem());
		if(droppingRadioButton.isSelected()) {
			//Dropping Tasks
			main.tasks = new TaskSet(new BasicFishingInteraction(), new DropInventory(), new NavigateBackToFishingSpot() , new ManageRunTask());
		} else {
			//Banking Tasks
			main.tasks = new TaskSet(new BasicFishingInteraction(), new BankingTask(), new NavigateBackToFishingSpot(), new ManageRunTask());
		}
		main.reactionWaitMultiplier = ((float)reactionTimeSlider.getValue())/10;
		main.lastInventoryValue = ArkUtility.getPriceOfInventory();
		main.fishingTilesIveBeenTo.add(Player.getPosition());
		main.currentStatus = "Script started!";
		
		main.guiEnded = true;
	}
}
