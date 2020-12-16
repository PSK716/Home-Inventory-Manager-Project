package homeInventory;

import javax.swing.*;
import javax.swing.filechooser.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import com.toedter.calendar.*;
import java.awt.geom.*;
import java.io.*;
import java.util.*;
import java.text.*;
import java.awt.print.*;
public class HomeInventory extends JFrame{
	 JTextField itemTextField;
	JTextField serialTextField;
	JTextField priceTextField;
	JTextField storeTextField;
	 JTextField noteTextField;
	JButton newButton,deleteButton,saveButton,previousButton,nextButton,printButton,exitButton,photoButton;
	homeInventory.PhotoPanel photoPanel;
	JPanel searchPanel;
	JTextArea photoTextArea;
	JComboBox locationComboBox;
	JCheckBox markedCheckBox;
	JDateChooser dateDateChooser;
	JButton[] searchButton = new JButton[26];
	static final int maximumEntries = 300;
	static int numberEntries;
	static InventoryItem[] myInventory = new InventoryItem[maximumEntries];
	int currentEntry;
	static final int entriesPerPage = 2;
	static int lastPage;
	
	public HomeInventory(){
		setTitle("Home Inventory Manager");
		setSize(650,525);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);
		
		addWindowListener(new WindowAdapter()
		{
		public void windowClosing(WindowEvent evt)
		{
		exitForm(evt);
		}
		});
		
		JToolBar inventoryToolBar = new JToolBar();
		inventoryToolBar.setBounds(0, 0, 72, 435);
		inventoryToolBar.setOrientation(SwingConstants.VERTICAL);
		inventoryToolBar.setFloatable(false);
		inventoryToolBar.setBackground(Color.BLUE);
		getContentPane().add(inventoryToolBar);
		
		newButton = new JButton((Icon) null);
		newButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		newButton.setToolTipText("Add New Item");
		newButton.setText("New");
		newButton.setPreferredSize(new Dimension(70, 50));
		newButton.setMinimumSize(new Dimension(70, 50));
		newButton.setMaximumSize(new Dimension(70, 50));
		newButton.setHorizontalTextPosition(SwingConstants.CENTER);
		newButton.setFocusable(false);
		inventoryToolBar.add(newButton);
		newButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				checkSave();
				blankValues();
				
			}
			
		});
		
		deleteButton = new JButton((Icon) null);
		deleteButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		deleteButton.setToolTipText("Delete Current Item");
		deleteButton.setText("Delete");
		deleteButton.setPreferredSize(new Dimension(70, 50));
		deleteButton.setMinimumSize(new Dimension(70, 50));
		deleteButton.setMaximumSize(new Dimension(70, 50));
		deleteButton.setHorizontalTextPosition(SwingConstants.CENTER);
		deleteButton.setFocusable(false);
		inventoryToolBar.add(deleteButton);
		deleteButton.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) {
				if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this item?",
						"Delete Inventory Item", JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
						return;
						deleteEntry(currentEntry);
						if (numberEntries == 0)
						{
						currentEntry = 0;
						blankValues();
						}
						else
						{
						currentEntry--;
						if (currentEntry == 0)
						currentEntry = 1;
						showEntry(currentEntry);
						}
				
			}
			
		});
		
		saveButton = new JButton((Icon) null);
		saveButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		saveButton.setToolTipText("Save Current Item");
		saveButton.setText("Save");
		saveButton.setPreferredSize(new Dimension(70, 50));
		saveButton.setMinimumSize(new Dimension(70, 50));
		saveButton.setMaximumSize(new Dimension(70, 50));
		saveButton.setHorizontalTextPosition(SwingConstants.CENTER);
		saveButton.setFocusable(false);
		inventoryToolBar.add(saveButton);
		saveButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				itemTextField.setText(itemTextField.getText().trim());
				if (itemTextField.getText().equals(""))
				{
				JOptionPane.showConfirmDialog(null, "Must have item description.", "Error",
				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
				itemTextField.requestFocus();
				return;
				}
				if (newButton.isEnabled())
				{
				// delete edit entry then resave
				deleteEntry(currentEntry);
				}
				// capitalize first letter
				String s = itemTextField.getText();
				itemTextField.setText(s.substring(0, 1).toUpperCase() + s.substring(1));
				numberEntries++;
				// determine new current entry location based on description
				currentEntry = 1;
				if (numberEntries != 1)
				{
				do
				{
				if
				(itemTextField.getText().compareTo(myInventory[currentEntry - 1].description) < 0)
				break;
				currentEntry++;
				}
				while (currentEntry < numberEntries);
				}
				// move all entries below new value down one position unless at end
				if (currentEntry != numberEntries)
				{
				for (int i = numberEntries; i >= currentEntry + 1; i--)
				{
				myInventory[i - 1] = myInventory[i - 2];

				myInventory[i - 2] = new InventoryItem();
				}
				}
				myInventory[currentEntry - 1] = new InventoryItem();
				myInventory[currentEntry - 1].description = itemTextField.getText();
				myInventory[currentEntry - 1].location =
				locationComboBox.getSelectedItem().toString();
				myInventory[currentEntry - 1].marked = markedCheckBox.isSelected();
				myInventory[currentEntry - 1].serialNumber = serialTextField.getText();
				myInventory[currentEntry - 1].purchasePrice = priceTextField.getText();
				myInventory[currentEntry - 1].purchaseDate =
				dateToString(dateDateChooser.getDate());
				myInventory[currentEntry - 1].purchaseLocation = storeTextField.getText();
				myInventory[currentEntry - 1].photoFile = photoTextArea.getText();
				myInventory[currentEntry - 1].note = noteTextField.getText();
				showEntry(currentEntry);
				if (numberEntries < maximumEntries)
				newButton.setEnabled(true);
				else
				newButton.setEnabled(false);
				deleteButton.setEnabled(true);
				printButton.setEnabled(true);
				
			}
			
		});
		
		previousButton = new JButton((Icon) null);
		previousButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		previousButton.setToolTipText("Display Previous Item");
		previousButton.setText("Previous");
		previousButton.setPreferredSize(new Dimension(70, 50));
		previousButton.setMinimumSize(new Dimension(70, 50));
		previousButton.setMaximumSize(new Dimension(70, 50));
		previousButton.setHorizontalTextPosition(SwingConstants.CENTER);
		previousButton.setFocusable(false);
		previousButton.setEnabled(true);
		inventoryToolBar.add(previousButton);
		previousButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkSave();
				currentEntry--;
				showEntry(currentEntry);
				
			}
			
		});
		
		nextButton = new JButton((Icon) null);
		nextButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		nextButton.setToolTipText("Display Next Item");
		nextButton.setText("Next");
		nextButton.setPreferredSize(new Dimension(70, 50));
		nextButton.setMinimumSize(new Dimension(70, 50));
		nextButton.setMaximumSize(new Dimension(70, 50));
		nextButton.setHorizontalTextPosition(SwingConstants.CENTER);
		nextButton.setFocusable(false);
		nextButton.setEnabled(true);
		inventoryToolBar.add(nextButton);
		nextButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				checkSave();
				currentEntry++;
				showEntry(currentEntry);
				
			}
			
		});
		
		printButton = new JButton((Icon) null);
		printButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		printButton.setToolTipText("Print Inventory List");
		printButton.setText("Print");
		printButton.setPreferredSize(new Dimension(70, 50));
		printButton.setMinimumSize(new Dimension(70, 50));
		printButton.setMaximumSize(new Dimension(70, 50));
		printButton.setHorizontalTextPosition(SwingConstants.CENTER);
		printButton.setFocusable(false);
		inventoryToolBar.add(printButton);
		printButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				lastPage = (int) (1 + (numberEntries - 1) / entriesPerPage);
				PrinterJob inventoryPrinterJob = PrinterJob.getPrinterJob();
				inventoryPrinterJob.setPrintable(new InventoryDocument());
				if (inventoryPrinterJob.printDialog())
				{
				try
				{
				inventoryPrinterJob.print();
				}
				catch (PrinterException ex)
				{
				JOptionPane.showConfirmDialog(null, ex.getMessage(), "Print Error",
				JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
				}
				}
				
			}
			
		});
		
		exitButton = new JButton();
		exitButton.setToolTipText("Exit Program");
		exitButton.setText("Exit");
		exitButton.setPreferredSize(new Dimension(70, 50));
		exitButton.setMinimumSize(new Dimension(70, 50));
		exitButton.setMaximumSize(new Dimension(70, 50));
		exitButton.setHorizontalTextPosition(SwingConstants.CENTER);
		exitButton.setFocusable(false);
		inventoryToolBar.add(exitButton);
		exitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				exitForm(null);
			}
			
		});
		
		JSeparator separator = new JSeparator();
		inventoryToolBar.add(separator);
		
		JLabel itemLabel = new JLabel();
		itemLabel.setText("Inventory Item");
		itemLabel.setBounds(82, 11, 97, 14);
		getContentPane().add(itemLabel);
		
		itemTextField = new JTextField();
		itemTextField.setText((String) null);
		itemTextField.setPreferredSize(new Dimension(400, 25));
		itemTextField.setBounds(204, 6, 400, 25);
		getContentPane().add(itemTextField);
		
		JLabel locationLabel = new JLabel();
		locationLabel.setText("Location");
		locationLabel.setBounds(96, 50, 83, 14);
		getContentPane().add(locationLabel);
		
		locationComboBox = new JComboBox();
		locationComboBox.setPreferredSize(new Dimension(270, 25));
		locationComboBox.setFont(new Font("Arial", Font.PLAIN, 12));
		locationComboBox.setEditable(true);
		locationComboBox.setBackground(Color.WHITE);
		locationComboBox.setBounds(204, 44, 270, 25);
		getContentPane().add(locationComboBox);
		
		markedCheckBox = new JCheckBox("Marked?");
		markedCheckBox.setBounds(507, 46, 97, 23);
		getContentPane().add(markedCheckBox);
		
		JLabel serialLabel = new JLabel();
		serialLabel.setText("Serial Number");
		serialLabel.setBounds(82, 93, 97, 14);
		getContentPane().add(serialLabel);
		
		serialTextField = new JTextField();
		serialTextField.setText((String) null);
		serialTextField.setPreferredSize(new Dimension(270, 25));
		serialTextField.setBounds(204, 88, 270, 25);
		getContentPane().add(serialTextField);
		
		JLabel priceLabel = new JLabel();
		priceLabel.setText("Purchase Price");
		priceLabel.setBounds(82, 138, 97, 14);
		getContentPane().add(priceLabel);
		
		priceTextField = new JTextField();
		priceTextField.setText((String) null);
		priceTextField.setPreferredSize(new Dimension(160, 25));
		priceTextField.setBounds(204, 133, 160, 25);
		getContentPane().add(priceTextField);
		
		JLabel dateLabel = new JLabel();
		dateLabel.setText("Date Purchased");
		dateLabel.setBounds(374, 138, 100, 14);
		getContentPane().add(dateLabel);
		
		dateDateChooser = new JDateChooser();
		dateDateChooser.setPreferredSize(new Dimension(120, 25));
		dateDateChooser.setBounds(484, 135, 120, 25);
		getContentPane().add(dateDateChooser);
		
		JLabel storeLabel = new JLabel();
		storeLabel.setText("Store/Website");
		storeLabel.setBounds(82, 186, 97, 14);
		getContentPane().add(storeLabel);
		
		storeTextField = new JTextField();
		storeTextField.setText((String) null);
		storeTextField.setPreferredSize(new Dimension(400, 25));
		storeTextField.setBounds(204, 181, 400, 25);
		getContentPane().add(storeTextField);
		
		JLabel noteLabel = new JLabel();
		noteLabel.setText("Note");
		noteLabel.setBounds(97, 227, 72, 14);
		getContentPane().add(noteLabel);
		
		noteTextField = new JTextField();
		noteTextField.setText((String) null);
		noteTextField.setPreferredSize(new Dimension(400, 25));
		noteTextField.setBounds(204, 222, 400, 25);
		getContentPane().add(noteTextField);
		
		JLabel photoLabel = new JLabel();
		photoLabel.setText("Photo");
		photoLabel.setBounds(96, 271, 83, 14);
		getContentPane().add(photoLabel);
		
		photoTextArea = new JTextArea();
		photoTextArea.setWrapStyleWord(true);
		photoTextArea.setPreferredSize(new Dimension(350, 35));
		photoTextArea.setLineWrap(true);
		photoTextArea.setFont(new Font("Arial", Font.PLAIN, 12));
		photoTextArea.setFocusable(false);
		photoTextArea.setEditable(false);
		photoTextArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		photoTextArea.setBackground(new Color(255, 255, 192));
		photoTextArea.setBounds(204, 269, 350, 35);
		getContentPane().add(photoTextArea);
		
		photoButton = new JButton();
		photoButton.setText("...");
		photoButton.setBounds(559, 267, 45, 23);
		getContentPane().add(photoButton);
		photoButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				photoButtonActionPerformed(e);
				
			}
			
		});
		
		searchPanel = new JPanel();
		searchPanel.setPreferredSize(new Dimension(240, 160));
		searchPanel.setBorder(BorderFactory.createTitledBorder("Item Search"));
		searchPanel.setBounds(82, 315, 240, 160);
		getContentPane().add(searchPanel);
		GridBagLayout gbl_searchPanel = new GridBagLayout();
		gbl_searchPanel.columnWidths = new int[]{0};
		gbl_searchPanel.rowHeights = new int[]{0};
		gbl_searchPanel.columnWeights = new double[]{Double.MIN_VALUE};
		gbl_searchPanel.rowWeights = new double[]{Double.MIN_VALUE};
		searchPanel.setLayout(gbl_searchPanel);
		
		photoPanel = new homeInventory.PhotoPanel();
		photoPanel.setPreferredSize(new Dimension(240, 160));
		photoPanel.setBounds(342, 315, 240, 160);
		getContentPane().add(photoPanel);
		
		int x = 0, y = 0;
		// create and position 26 buttons
		for (int i = 0; i < 26; i++)
		{
		// create new button
		searchButton[i] = new JButton();
		// set text property
		searchButton[i].setText(String.valueOf((char) (65 + i)));
		searchButton[i].setFont(new Font("Arial", Font.BOLD, 12));
		searchButton[i].setMargin(new Insets(-10, -10, -10, -10));
		sizeButton(searchButton[i], new Dimension(37, 27));
		searchButton[i].setBackground(Color.YELLOW);
		searchButton[i].setFocusable(false);
//		gridConstraints = new GridBagConstraints();
//		gridConstraints.gridx = x;
//		gridConstraints.gridy = y;
//		searchPanel.add(searchButton[i], gridConstraints);
		// add method
		searchButton[i].addActionListener(new ActionListener ()
		{
		public void actionPerformed(ActionEvent e)
		{
			int i;
			if (numberEntries == 0)
			return;
			// search for item letter
			String letterClicked = e.getActionCommand();
			i = 0;
			do
			{
			if (myInventory[i].description.substring(0, 1).equals(letterClicked))
			{
			currentEntry = i + 1;
			showEntry(currentEntry);
			return;
			}
			i++;
			}
			while (i < numberEntries);
			JOptionPane.showConfirmDialog(null, "No " + letterClicked + " inventory items.",
			"None Found", JOptionPane.DEFAULT_OPTION,
			JOptionPane.INFORMATION_MESSAGE);
		}
		});
		x++;
		// six buttons per row
		if (x % 6 == 0)
		{
		x = 0;

		y++;
		}
		}
		
		
		int n;
		// open file for entries
		try
		{
		BufferedReader inputFile = new BufferedReader(new FileReader("inventory1.txt"));
		numberEntries =
		Integer.valueOf(inputFile.readLine()).intValue();
		if (numberEntries != 0)
		{
		for (int i = 0; i < numberEntries; i++)
		{
		myInventory[i] = new InventoryItem();
		myInventory[i].description = inputFile.readLine();
		myInventory[i].location = inputFile.readLine();
		myInventory[i].serialNumber = inputFile.readLine();
		myInventory[i].marked =
		Boolean.valueOf(inputFile.readLine()).booleanValue();
		myInventory[i].purchasePrice =

		inputFile.readLine();
		myInventory[i].purchaseDate = inputFile.readLine();
		myInventory[i].purchaseLocation =
		inputFile.readLine();
		myInventory[i].note = inputFile.readLine();
		myInventory[i].photoFile = inputFile.readLine();
		}
		}
		// read in combo box elements
		n = Integer.valueOf(inputFile.readLine()).intValue();
		if (n != 0)
		{
		for (int i = 0; i < n; i++)
		{
		locationComboBox.addItem(inputFile.readLine());
		}
		}
		inputFile.close();
		currentEntry = 1;
		showEntry(currentEntry);
		}
		catch (Exception ex)
		{
		numberEntries = 0;
		currentEntry = 0;
		}
		if (numberEntries == 0)
		{
		newButton.setEnabled(false);
		deleteButton.setEnabled(false);
		nextButton.setEnabled(false);
		previousButton.setEnabled(false);
		printButton.setEnabled(false);
		}
		
		
		setVisible(true);
	}
	public static void main(String args[])
	{
	
	new HomeInventory().show();
	}
	private void sizeButton(JButton b, Dimension d)
	{
	b.setPreferredSize(d);
	b.setMinimumSize(d);
	b.setMaximumSize(d);
	}
	
	private void showEntry(int j)
	{
	// display entry j (1 to numberEntries)
	itemTextField.setText(myInventory[j - 1].description);
	locationComboBox.setSelectedItem(myInventory[j - 1].location);
	markedCheckBox.setSelected(myInventory[j - 1].marked);
	serialTextField.setText(myInventory[j - 1].serialNumber);
	priceTextField.setText(myInventory[j - 1].purchasePrice);
	dateDateChooser.setDate(stringToDate(myInventory[j - 1].purchaseDate));
	storeTextField.setText(myInventory[j - 1].purchaseLocation);
	noteTextField.setText(myInventory[j - 1].note);
	showPhoto(myInventory[j - 1].photoFile);
	nextButton.setEnabled(true);
	previousButton.setEnabled(true);
	if (j == 1)
	previousButton.setEnabled(false);
	if (j == numberEntries)
	nextButton.setEnabled(false);
	itemTextField.requestFocus();
	}
	private Date stringToDate(String s)
	{
	int m = Integer.valueOf(s.substring(0, 2)).intValue() - 1;
	int d = Integer.valueOf(s.substring(3, 5)).intValue();
	int y = Integer.valueOf(s.substring(6)).intValue() - 1900;
	return(new Date(y, m, d));
	}
	private String dateToString(Date dd)

	{
	String yString = String.valueOf(dd.getYear() + 1900);
	int m = dd.getMonth() + 1;
	String mString = new DecimalFormat("00").format(m);
	int d = dd.getDate();
	String dString = new DecimalFormat("00").format(d);
	return(mString + "/" + dString + "/" + yString);
	}
	private void showPhoto(String photoFile)
	{
	if (!photoFile.equals(""))
	{
	try
	{
	photoTextArea.setText(photoFile);
	}
	catch (Exception ex)
	{
	photoTextArea.setText("");
	}
	}
	else
	{
	photoTextArea.setText("");
	}
	photoPanel.repaint();
	}
	
	private void exitForm(WindowEvent evt)
	{
	if (JOptionPane.showConfirmDialog(null, "Any unsaved changes will be lost.\nAre yousure you want to exit?", "Exit Program", JOptionPane.YES_NO_OPTION,
	JOptionPane.QUESTION_MESSAGE) == JOptionPane.NO_OPTION)
	return;
	// write entries back to file
	try
	{
	PrintWriter outputFile = new PrintWriter(new BufferedWriter(new
	FileWriter("inventory.txt")));
	outputFile.println(numberEntries);
	if (numberEntries != 0)
	{
	for (int i = 0; i < numberEntries; i++)
	{
	outputFile.println(myInventory[i].description);
	outputFile.println(myInventory[i].location);
	outputFile.println(myInventory[i].serialNumber);
	outputFile.println(myInventory[i].marked);
	outputFile.println(myInventory[i].purchasePrice);
	outputFile.println(myInventory[i].purchaseDate);
	outputFile.println(myInventory[i].purchaseLocation);
	outputFile.println(myInventory[i].note);
	outputFile.println(myInventory[i].photoFile);
	}
	}
	// write combo box entries
	outputFile.println(locationComboBox.getItemCount());
	if (locationComboBox.getItemCount() != 0)
	{
	for (int i = 0; i < locationComboBox.getItemCount(); i++)
	outputFile.println(locationComboBox.getItemAt(i));
	}
	outputFile.close();
	}

	catch (Exception ex)
	{
	}
	System.exit(0);
	}
	
	private void blankValues()
	{
	// blank input screen
	newButton.setEnabled(false);
	deleteButton.setEnabled(false);
	saveButton.setEnabled(true);
	previousButton.setEnabled(false);

	nextButton.setEnabled(false);
	printButton.setEnabled(false);
	itemTextField.setText("");
	locationComboBox.setSelectedItem("");
	markedCheckBox.setSelected(false);
	serialTextField.setText("");
	priceTextField.setText("");
	dateDateChooser.setDate(new Date());
	storeTextField.setText("");
	noteTextField.setText("");
	photoTextArea.setText("");
	photoPanel.repaint();
	itemTextField.requestFocus();
	}
	
	private void checkSave()
	{
	boolean edited = false;
	if (!myInventory[currentEntry - 1].description.equals(itemTextField.getText()))
	edited = true;
	else if (!myInventory[currentEntry -

	1].location.equals(locationComboBox.getSelectedItem().toString()))
	edited = true;
	else if (myInventory[currentEntry - 1].marked != markedCheckBox.isSelected())
	edited = true;
	else if (!myInventory[currentEntry - 1].serialNumber.equals(serialTextField.getText()))
	edited = true;
	else if (!myInventory[currentEntry - 1].purchasePrice.equals(priceTextField.getText()))
	edited = true;
	else if (!myInventory[currentEntry -
	1].purchaseDate.equals(dateToString(dateDateChooser.getDate())))
	edited = true;
	else if (!myInventory[currentEntry -
	1].purchaseLocation.equals(storeTextField.getText()))
	edited = true;
	else if (!myInventory[currentEntry - 1].note.equals(noteTextField.getText()))
	edited = true;
	else if (!myInventory[currentEntry - 1].photoFile.equals(photoTextArea.getText()))
	edited = true;
	if (edited)
	{
	if (JOptionPane.showConfirmDialog(null, "You have edited this item. Do you want tosave the changes?", "Save Item", JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
	saveButton.doClick();
	}
	}
	private void deleteEntry(int j)
	{
	// delete entry j
	if (j != numberEntries)
	{
	// move all entries under j up one level
	for (int i = j; i < numberEntries; i++)
	{
	myInventory[i - 1] = new InventoryItem();
	myInventory[i - 1] = myInventory[i];
	}
	}
	numberEntries--;
	}
	private void photoButtonActionPerformed(ActionEvent e)
	{
	JFileChooser openChooser = new JFileChooser();
	openChooser.setDialogType(JFileChooser.OPEN_DIALOG);
	openChooser.setDialogTitle("Open Photo File");
	openChooser.addChoosableFileFilter(new FileNameExtensionFilter("Photo Files",
	"jpg"));
	if (openChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
	showPhoto(openChooser.getSelectedFile().toString());
	}
	private void searchButtonActionPerformed(ActionEvent e)
	{
	int i;
	if (numberEntries == 0)
	return;
	// search for item letter
	String letterClicked = e.getActionCommand();
	i = 0;
	do
	{
	if (myInventory[i].description.substring(0, 1).equals(letterClicked))
	{
	currentEntry = i + 1;
	showEntry(currentEntry);
	return;
	}
	i++;
	}
	while (i < numberEntries);
	JOptionPane.showConfirmDialog(null, "No " + letterClicked + " inventory items.",
	"None Found", JOptionPane.DEFAULT_OPTION,
	JOptionPane.INFORMATION_MESSAGE);
	}
}
class PhotoPanel extends JPanel
{
public void paintComponent(Graphics g)
{
Graphics2D g2D = (Graphics2D) g;
super.paintComponent(g2D);
// draw border
g2D.setPaint(Color.BLACK);

g2D.draw(new Rectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1));
// show photo
Image photoImage = new
ImageIcon(HomeInventory.photoTextArea.getText()).getImage();
int w = getWidth();
int h = getHeight();
double rWidth = (double) getWidth() / (double) photoImage.getWidth(null);
double rHeight = (double) getHeight() / (double) photoImage.getHeight(null);
if (rWidth > rHeight)
{
// leave height at display height, change width by amount height is changed
w = (int) (photoImage.getWidth(null) * rHeight);
}
else
{
// leave width at display width, change height by amount width is changed
h = (int) (photoImage.getHeight(null) * rWidth);
}
// center in panel
g2D.drawImage(photoImage, (int) (0.5 * (getWidth() - w)), (int) (0.5 * (getHeight() -
h)), w, h, null);
g2D.dispose();
}
}
class InventoryDocument implements Printable
{
public int print(Graphics g, PageFormat pf, int pageIndex)
{
Graphics2D g2D = (Graphics2D) g;
if ((pageIndex + 1) > HomeInventory.lastPage)
{
return NO_SUCH_PAGE;
}
int i, iEnd;
// here you decide what goes on each page and draw it

// header
g2D.setFont(new Font("Arial", Font.BOLD, 14));
g2D.drawString("Home Inventory Items - Page " + String.valueOf(pageIndex + 1),
(int) pf.getImageableX(), (int) (pf.getImageableY() + 25));
// get starting y
int dy = (int) g2D.getFont().getStringBounds("S",
g2D.getFontRenderContext()).getHeight();
int y = (int) (pf.getImageableY() + 4 * dy);
iEnd = HomeInventory.entriesPerPage * (pageIndex + 1);
if (iEnd > HomeInventory.numberEntries)
iEnd = HomeInventory.numberEntries;
for (i = 0 + HomeInventory.entriesPerPage * pageIndex; i < iEnd; i++)
{
// dividing line
Line2D.Double dividingLine = new
Line2D.Double(pf.getImageableX(), y, pf.getImageableX() + pf.getImageableWidth(), y);
g2D.draw(dividingLine);
y += dy;
g2D.setFont(new Font("Arial", Font.BOLD, 12));
g2D.drawString(HomeInventory.myInventory[i].description, (int) pf.getImageableX(), y);
y += dy;
g2D.setFont(new Font("Arial", Font.PLAIN, 12));
g2D.drawString("Location: " + HomeInventory.myInventory[i].location, (int)
(pf.getImageableX() + 25), y);
y += dy;
if (HomeInventory.myInventory[i].marked)
g2D.drawString("Item is marked with identifying information.", (int)
(pf.getImageableX() + 25), y);
else
g2D.drawString("Item is NOT marked with identifying information.", (int)
(pf.getImageableX() + 25), y);
y += dy;
g2D.drawString("Serial Number: " +
HomeInventory.myInventory[i].serialNumber, (int) (pf.getImageableX() + 25), y);
y += dy;
g2D.drawString("Price: $" + HomeInventory.myInventory[i].purchasePrice + ",Purchased on: " + HomeInventory.myInventory[i].purchaseDate, (int) (pf.getImageableX() +
25), y);
y += dy;
g2D.drawString("Purchased at: " +
HomeInventory.myInventory[i].purchaseLocation, (int) (pf.getImageableX() + 25), y);
y += dy;
g2D.drawString("Note: " + HomeInventory.myInventory[i].note, (int)
(pf.getImageableX() + 25), y);
y += dy;
try
{
// maintain original width/height ratio
Image inventoryImage = new
ImageIcon(HomeInventory.myInventory[i].photoFile).getImage();
double ratio = (double) (inventoryImage.getWidth(null)) / (double)
inventoryImage.getHeight(null);
g2D.drawImage(inventoryImage, (int) (pf.getImageableX() + 25), y, (int) (100 *
ratio), 100, null);
}
catch (Exception ex)
{
// have place to go in case image file doesn't open
}
y += 2 * dy + 100;
}
return PAGE_EXISTS;
}
}

