package scripts.arkscripts.herblore;

import java.awt.EventQueue;
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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.tribot.api.General;
import org.tribot.util.Util;

import scripts.api.ark.ArkUtility;
import scripts.api.ark.TaskSet;
import javax.swing.JSlider;

public class HerbloreGUI {

	private static JFrame frmArkherbloreAio;
	private static JTextField txtIngredientOne;
	private static JTextField txtIngredientTwo;
	private static JTextField herbIDField;
	private static JCheckBox useAmuletOfChemistryCheck;
	private static JButton startButton;
	private static JSlider reactionTimeSlider;
	
	private static JCheckBox useEscapeExitCheckbox;

	private static JTabbedPane tabbedPane;
	
	public static final File PATH = new File(Util.getWorkingDirectory().getAbsolutePath(),
			"arkherbloreaio_" + "settings.ini");

	private static Properties prop;

	/**
	 * Launch the application.
	 */
	public static void initiatliseGUI() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HerbloreGUI window = new HerbloreGUI();
					window.frmArkherbloreAio.setVisible(true);
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
	public HerbloreGUI() throws MalformedURLException, IOException {
		this.prop = new Properties();
		initialize();
	}
	
	private static void saveSettings() {
		try {
			prop.clear();

			prop.put("useAmuletOfChemistryCheck", String.valueOf(useAmuletOfChemistryCheck.isSelected()));
			prop.put("useAmuletOfChemistryCheck", String.valueOf(useAmuletOfChemistryCheck.isSelected()));
			
			prop.put("useEscapeExitCheckbox", String.valueOf(useEscapeExitCheckbox.isSelected()));

			prop.put("txtIngredientOne", txtIngredientOne.getText());
			prop.put("txtIngredientTwo", txtIngredientTwo.getText());
			prop.put("herbIDField", herbIDField.getText());
			
			prop.put("reactionTimeSlider", String.valueOf(reactionTimeSlider.getValue()));

			prop.store(new FileOutputStream(HerbloreGUI.PATH), "ARKHerblore AIO");
			General.println("Saved GUI settings at: " + HerbloreGUI.PATH);
		} catch (Exception e1) {
			General.println("Unable to save settings");
			e1.printStackTrace();
		}
	}
	
	public void loadSettings() {
		try {
			if (!HerbloreGUI.PATH.exists()) { // make sure file exists
				General.println("[GUI Loading] Settings path doesn't exist");
				HerbloreGUI.PATH.createNewFile(); // or make a new one
				reactionTimeSlider.setValue(2);
			}
				prop.load(new FileInputStream(HerbloreGUI.PATH));

				String[] checkBoxNames = { "useAmuletOfChemistryCheck", "useEscapeExitCheckbox"};
				JCheckBox[] boxes = { useAmuletOfChemistryCheck, useEscapeExitCheckbox};
				for (int i = 0; i < checkBoxNames.length; i++) {
					String value = prop.getProperty(checkBoxNames[i]);
					if(value != null) {
						boxes[i].setSelected(Boolean.valueOf(value));
					}
				}

				String[] textFieldNames = { "txtIngredientOne", "txtIngredientTwo", "herbIDField" };
				JTextField[] textFields = { txtIngredientOne, txtIngredientTwo, herbIDField };
				for (int i = 0; i < textFieldNames.length; i++) {
					String value = prop.getProperty(textFieldNames[i]);
					if (value != null) {
						textFields[i].setText(value);
					}
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
		frmArkherbloreAio = new JFrame();
		frmArkherbloreAio.setTitle("ARKHerblore AIO");
		frmArkherbloreAio.setBounds(100, 100, 492, 514);
		frmArkherbloreAio.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frmArkherbloreAio.getContentPane().setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(10, 11, 456, 453);
		frmArkherbloreAio.getContentPane().add(panel);
		panel.setLayout(null);

		JLabel welcomeLabel = new JLabel("<html><b>Welcome to ARKHerblore by Marcusihno</b></html>");
		welcomeLabel.setBounds(10, 68, 436, 14);
		panel.add(welcomeLabel);
		welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

		JLabel modeLabel = new JLabel("Choose a mode and then enter the ingredient ID/s.");
		modeLabel.setBounds(0, 93, 456, 23);
		modeLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(modeLabel);

		JLabel idInstructionLabel = new JLabel("These can be seen by going to \"Debug\" and then \"Inventory\". ");
		idInstructionLabel.setBounds(0, 111, 456, 30);
		idInstructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(idInstructionLabel);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(10, 152, 216, 164);
		panel.add(tabbedPane);

		JPanel potMixingPanel = new JPanel();
		tabbedPane.addTab("Potion Mixing", null, potMixingPanel, null);
		potMixingPanel.setLayout(null);

		txtIngredientOne = new JTextField();
		txtIngredientOne.setBounds(10, 65, 96, 20);
		potMixingPanel.add(txtIngredientOne);
		txtIngredientOne.setToolTipText("Ingredient One");
		txtIngredientOne.setColumns(10);

		txtIngredientTwo = new JTextField();
		txtIngredientTwo.setBounds(10, 25, 96, 20);
		potMixingPanel.add(txtIngredientTwo);
		txtIngredientTwo.setToolTipText("Ingredient One");
		txtIngredientTwo.setColumns(10);

		JLabel lblIngredientOne = new JLabel("Ingredient One ID");
		lblIngredientOne.setBounds(11, 10, 95, 14);
		potMixingPanel.add(lblIngredientOne);
		lblIngredientOne.setHorizontalAlignment(SwingConstants.LEFT);

		JLabel lblIngredientTwo = new JLabel("Ingredient Two ID");
		lblIngredientTwo.setBounds(10, 50, 95, 14);
		potMixingPanel.add(lblIngredientTwo);
		lblIngredientTwo.setHorizontalAlignment(SwingConstants.LEFT);

		useAmuletOfChemistryCheck = new JCheckBox("Use Amulets of Chemistry");
		useAmuletOfChemistryCheck.setBounds(10, 92, 195, 31);
		potMixingPanel.add(useAmuletOfChemistryCheck);

		JPanel herbCleaningPanel = new JPanel();
		tabbedPane.addTab("Herb Cleaning", null, herbCleaningPanel, null);
		herbCleaningPanel.setLayout(null);

		JLabel lblHerbId = new JLabel("Herb ID");
		lblHerbId.setBounds(10, 11, 113, 23);
		herbCleaningPanel.add(lblHerbId);
		lblHerbId.setHorizontalAlignment(SwingConstants.LEFT);

		herbIDField = new JTextField();
		herbIDField.setBounds(10, 33, 96, 20);
		herbCleaningPanel.add(herbIDField);
		herbIDField.setColumns(10);
		
		
		BufferedImage img = ImageIO.read(new URL("https://simplemed.co.uk/images/Tribot/ArkHerbloreLogoV2.png"));
		ImageIcon icon = new ImageIcon(img);
		
		JLabel iconLabel = new JLabel("");
		iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		iconLabel.setBounds(10, 0, 436, 62);
		iconLabel.setIcon(icon);
		panel.add(iconLabel);
		
		reactionTimeSlider = new JSlider();
		reactionTimeSlider.setMajorTickSpacing(2);
		reactionTimeSlider.setSnapToTicks(true);
		reactionTimeSlider.setPaintTicks(true);
		reactionTimeSlider.setValue(10);
		reactionTimeSlider.setMaximum(20);
		reactionTimeSlider.setBounds(125, 377, 200, 26);
		panel.add(reactionTimeSlider);
		
		JLabel lblNewLabel = new JLabel("<html><b>Reaction Time:</b><html>");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(125, 327, 200, 14);
		panel.add(lblNewLabel);
		
		JLabel lblleftFaster = new JLabel("(Left = Faster | Right = Slower)");
		lblleftFaster.setHorizontalAlignment(SwingConstants.CENTER);
		lblleftFaster.setBounds(125, 352, 200, 14);
		panel.add(lblleftFaster);
		
		useEscapeExitCheckbox = new JCheckBox("Press Escape to close bank interface");
		useEscapeExitCheckbox.setBounds(232, 179, 224, 23);
		panel.add(useEscapeExitCheckbox);
		
		JLabel lblNewLabel_1 = new JLabel("<html><b>Please note:</b> If using esc closing, you must have setup your OSRS settings (\"Settings\" -> \"F-keys\" -> \"Esc to close interfaces\")</html>");
		lblNewLabel_1.setBounds(236, 205, 210, 77);
		panel.add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("<html><b>General Settings</b></html>");
		lblNewLabel_2.setBounds(236, 158, 136, 14);
		panel.add(lblNewLabel_2);
		
		JCheckBox chckbxGrandExchangeRestocking = new JCheckBox("Grand Exchange Restocking (Soon!)");
		chckbxGrandExchangeRestocking.setEnabled(false);
		chckbxGrandExchangeRestocking.setBounds(232, 279, 224, 31);
		panel.add(chckbxGrandExchangeRestocking);
		
		loadSettings();

		startButton = new JButton("Start!");
		startButton.setBounds(10, 414, 436, 28);
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startScript();
			}
		});
		panel.add(startButton);
		
	}

	public static void startScript() {
		ArkHerblore main = ArkHerblore.getInstance();
		General.println("[Welcome] Welcome to ARKHerblore Pro by Marcusihno. If you have any feedback, please leave it on my script thread! Enjoy!");
		if (tabbedPane.getSelectedIndex() == 0) {
			//potion making tab
			//must check if the user has entered an ingredient ID for both fields, that is also a numerical value
			if (txtIngredientOne.getText().contentEquals("") || txtIngredientTwo.getText().contentEquals("")) {
				General.println("Please input a required ingredient ID that is numerical");
				startButton.getModel().setPressed(false);
			} else {
				saveSettings();
				General.println("[Task Management] Starting Potions Task");
				main.useAmuletOfChemistry = useAmuletOfChemistryCheck.getModel().isSelected();
				main.ingredientOne = new int[] { Integer.parseInt(txtIngredientOne.getText()) };
				main.ingredientTwo = new int[] { Integer.parseInt(txtIngredientTwo.getText()) };
				main.useEscapeExitBanking = useEscapeExitCheckbox.getModel().isSelected();
				main.reactionTimeMultiplier = ((float)reactionTimeSlider.getValue())/10;
				frmArkherbloreAio.setVisible(false);
				// set the tasks to carry out
				ArkHerblore.getInstance().lastInventoryValue = ArkUtility.getPriceOfInventory();
				main.tasks = new TaskSet(new MixingBankingTask(), new MixPotionTask());
				main.guiEnded = true;
			}
		} else if (tabbedPane.getSelectedIndex() == 1) {
			//herb cleaning tab
			//must check if the user has entered an ingredient ID for the herb ID field, that is also a numerical value
			if (herbIDField.getText().contentEquals("")) {
				General.println("Please input a required ingredient ID that is numerical");
				startButton.getModel().setPressed(false);
			} else {
				saveSettings();
				General.println("[Task Management] Starting Herb Task");
				main.ingredientOne = new int[] { Integer.parseInt(herbIDField.getText()) };
				main.reactionTimeMultiplier = ((float)reactionTimeSlider.getValue())/10;
				main.useEscapeExitBanking = useEscapeExitCheckbox.getModel().isSelected();
				frmArkherbloreAio.setVisible(false);
				// set the tasks to carry out
				main.lastInventoryValue = ArkUtility.getPriceOfInventory();
				main.tasks = new TaskSet(new CleaningBankingTask(), new CleanHerbsTask());
				main.guiEnded = true;
			}
		}
	}
	
}
