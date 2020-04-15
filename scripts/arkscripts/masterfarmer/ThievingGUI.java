package scripts.arkscripts.masterfarmer;

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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.commons.lang3.math.NumberUtils;
import org.tribot.api.General;
import org.tribot.util.Util;

import scripts.api.ark.TaskSet;

public class ThievingGUI {

	private static JFrame frmArkMasterFarmer;
	private static JButton startButton;
	private static JSlider reactionTimeSlider;
	private static 	JSlider amountOfFoodPerTripSlider;
	private static JCheckBox useDodgyNecklacesCheckbox;

	public static final File PATH = new File(Util.getWorkingDirectory().getAbsolutePath(),
			"arkmasterfarmer_" + "settings.ini");

	private static Properties prop;
	private static JTextField foodIDField;

	/**
	 * Launch the application.
	 */
	public static void initiatliseGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ThievingGUI window = new ThievingGUI();
					window.frmArkMasterFarmer.setVisible(true);
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
	public ThievingGUI() throws MalformedURLException, IOException {
		ThievingGUI.prop = new Properties();
		initialize();
	}

	private static void saveSettings() {
		try {
			prop.clear();

			prop.put("foodIDField", foodIDField.getText());
			
			prop.put("useDodgyNecklacesCheckbox", String.valueOf(useDodgyNecklacesCheckbox.isSelected()));
			
			prop.put("amountOfFoodPerTripSlider", String.valueOf(amountOfFoodPerTripSlider.getValue()));
			
			prop.put("reactionTimeSlider", String.valueOf(reactionTimeSlider.getValue()));

			prop.store(new FileOutputStream(ThievingGUI.PATH), "ARKMasterFarmer");
			General.println("Saved GUI settings at: " + ThievingGUI.PATH);
		} catch (Exception e1) {
			General.println("Unable to save settings");
			e1.printStackTrace();
		}
	}

	public void loadSettings() {
		try {
			if (!ThievingGUI.PATH.exists()) { // make sure file exists
				General.println("[GUI Loading] Settings path doesn't exist");
				ThievingGUI.PATH.createNewFile(); // or make a new one
				
				reactionTimeSlider.setValue(2);
			}
			prop.load(new FileInputStream(ThievingGUI.PATH));
			
			String[] checkBoxNames = {"useDodgyNecklacesCheckbox"};
			JCheckBox[] boxes = {useDodgyNecklacesCheckbox};
			for (int i = 0; i < checkBoxNames.length; i++) {
				String value = prop.getProperty(checkBoxNames[i]);
				boxes[i].setSelected(Boolean.valueOf(value));
			}
			
			String[] textFieldNames = {"foodIDField"};
			JTextField[] textFields = { foodIDField };
			for (int i = 0; i < textFieldNames.length; i++) {
				String value = prop.getProperty(textFieldNames[i]);
				if (value != null) {
					textFields[i].setText(value);
				}
			}
			
			if(prop.getProperty("amountOfFoodPerTripSlider") != null) {
				amountOfFoodPerTripSlider.setValue(Integer.parseInt(prop.getProperty("amountOfFoodPerTripSlider")));
			}
			
			if(prop.getProperty("reactionTimeSlider") != null) {
				reactionTimeSlider.setValue(Integer.parseInt(prop.getProperty("reactionTimeSlider")));
			}

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
		frmArkMasterFarmer = new JFrame();
		frmArkMasterFarmer.setTitle("ARKFishing AIO");
		frmArkMasterFarmer.setBounds(100, 100, 450, 512);
		frmArkMasterFarmer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmArkMasterFarmer.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 414, 451);
		frmArkMasterFarmer.getContentPane().add(panel);
		panel.setLayout(null);
		
		BufferedImage img = ImageIO.read(new URL("https://simplemed.co.uk/images/Tribot/ArkMasterFarmerLogoV2.png"));
		ImageIcon icon = new ImageIcon(img);
		
		JLabel iconLabel = new JLabel("");
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel.setBounds(10, 11, 394, 67);
		iconLabel.setIcon(icon);
		panel.add(iconLabel);

		JLabel welcomeLabel = new JLabel("<html><b>Welcome to ARKMasterFarmer by Marcusihno</b></html>");
		welcomeLabel.setBounds(45, 89, 322, 14);
		panel.add(welcomeLabel);
		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel modeLabel = new JLabel("Select your GUI settings and press Start!");
		modeLabel.setBounds(10, 101, 404, 30);
		modeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(modeLabel);
		
		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(SystemColor.menu);
		mainPanel.setBounds(10, 135, 394, 268);
		panel.add(mainPanel);
		mainPanel.setLayout(null);
		
		reactionTimeSlider = new JSlider();
		reactionTimeSlider.setMajorTickSpacing(2);
		reactionTimeSlider.setPaintTicks(true);
		reactionTimeSlider.setSnapToTicks(true);
		reactionTimeSlider.setMaximum(20);
		reactionTimeSlider.setMinimum(0);
		reactionTimeSlider.setBounds(95, 217, 190, 26);
		mainPanel.add(reactionTimeSlider);
		
		JLabel reactionTimeLabel = new JLabel("<html><b>Reaction Time</b><html>");
		reactionTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		reactionTimeLabel.setBounds(95, 167, 190, 14);
		mainPanel.add(reactionTimeLabel);
		
		JLabel reactionTimeExplanationLabel = new JLabel("(Left = Faster | Right = Slower)");
		reactionTimeExplanationLabel.setHorizontalAlignment(SwingConstants.CENTER);
		reactionTimeExplanationLabel.setBounds(95, 186, 190, 20);
		mainPanel.add(reactionTimeExplanationLabel);
		
		JLabel foodLabel = new JLabel("Food ID:");
		foodLabel.setBounds(10, 11, 66, 14);
		mainPanel.add(foodLabel);
		
		foodIDField = new JTextField();
		foodIDField.setBounds(60, 8, 96, 20);
		mainPanel.add(foodIDField);
		foodIDField.setColumns(10);
		
		JLabel amountLabel = new JLabel("Amount to withdraw at a time:");
		amountLabel.setBounds(10, 49, 190, 14);
		mainPanel.add(amountLabel);
		
		amountOfFoodPerTripSlider = new JSlider();
		amountOfFoodPerTripSlider.setValue(12);
		amountOfFoodPerTripSlider.setMinorTickSpacing(2);
		amountOfFoodPerTripSlider.setPaintLabels(true);
		amountOfFoodPerTripSlider.setSnapToTicks(true);
		amountOfFoodPerTripSlider.setPaintTicks(true);
		amountOfFoodPerTripSlider.setMaximum(28);
		amountOfFoodPerTripSlider.setMajorTickSpacing(4);
		amountOfFoodPerTripSlider.setBounds(173, 49, 211, 39);
		mainPanel.add(amountOfFoodPerTripSlider);
		
		useDodgyNecklacesCheckbox = new JCheckBox("Use Dodgy Necklaces");
		useDodgyNecklacesCheckbox.setBounds(10, 105, 142, 23);
		mainPanel.add(useDodgyNecklacesCheckbox);
		
		JCheckBox chckbxUseGlovesOf = new JCheckBox("Use Gloves of Silence (Coming Soon)");
		chckbxUseGlovesOf.setEnabled(false);
		chckbxUseGlovesOf.setBounds(10, 131, 205, 23);
		mainPanel.add(chckbxUseGlovesOf);
		
		loadSettings();

		startButton = new JButton("Start!");
		startButton.setBounds(10, 414, 394, 28);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startScript();
			}
		});
		panel.add(startButton);
		


	}

	public static void startScript() {
		ArkMasterFarmer main = ArkMasterFarmer.getInstance();

		saveSettings();
		frmArkMasterFarmer.setVisible(false);
		
		if (foodIDField.getText().contentEquals("") || !NumberUtils.isNumber(foodIDField.getText())) {
			General.println("Please enter a valid ID for your food choice");
		} else {
			// set food ID
		main.foodID = new int[] { Integer.parseInt(foodIDField.getText()) };
		main.numberOfFoodToWithdrawPerTrip = amountOfFoodPerTripSlider.getValue();
		main.useDodgyNecklaces = useDodgyNecklacesCheckbox.getModel().isSelected();
		main.reactionTimeMultiplier = ((float)reactionTimeSlider.getValue())/10;
		
		// set the tasks to carry out
		main.tasks = new TaskSet(new BankingTask(), new EatFoodTask(), new Pickpocket(), new ManageRunTask(), new ResetPositionTask());

		main.currentStatus = "Script started!";
		
		main.guiEnded = true;
	}
	}
}
