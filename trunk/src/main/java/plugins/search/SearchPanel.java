package plugins.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

import freemind.modes.ModeController;
import plugins.search.Search.SearchResult;

public class SearchPanel extends JDialog implements ListSelectionListener {

	private JTextField searchTermsField = new JTextField();
	private JRadioButton rdbtnOpen;
	private JRadioButton rdbtnDirectorySearch;
	private final ButtonGroup directoryButtonGroup = new ButtonGroup();
	private JButton btnChooseDirectoryButton;
	private JTextField selectedDirectoryField = new JTextField();
	private SearchNodeHook searchNodeHook;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		class TestHook extends SearchNodeHook {
			private File[] files;

			public TestHook(File[] files) {
				this.files = files;
			}

			@Override
			public Logger getLogger(Class className) {
				return Logger.getLogger(className.getName());
			}

			@Override
			public File[] getFilesOfOpenTabs() {
				return this.files;
			}
		}

		File[] files = new File[] {new File("data/freemind.mm")};
		SearchPanel searchPanel = new SearchPanel(new TestHook(files),
				new JFrame());
		searchPanel.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public SearchPanel(JFrame frame, Logger logger) {
		super(frame, "Search Multiple Maps", true);
		this._logger = logger;
		initialize();
	}

	private static Logger _logger = null;

	public SearchPanel() {
		this(null, Logger.getLogger(SearchPanel.class.getName()));
	}

	public SearchPanel(SearchNodeHook searchNodeHook, JFrame jFrame) {
		this(jFrame, searchNodeHook
				.getLogger(SearchPanel.class));
		this.searchNodeHook = searchNodeHook;
	}

	private File selectedDirectory;
	private JButton btnGoButton = new JButton("Search");

	private JSplitPane splitPane;
	private JTextArea scorePanel;
	private JScrollPane resultsListPane;
	private IndexSearcher searcher;
	private FreeMindFileIndexer indexer;
	private ScoreDoc[] hits;

	/**
	 * Initialize the contents of the frame.
	 */
	/**
	 * 
	 */
	private void initialize() {
		final JPanel content = new JPanel();
		setContentPane(content);
		JPanel criteriaPanel = new JPanel();
		updateSelectedFolderField();

		// / Bottom pane
		scorePanel = new JTextArea("");
		Dimension minimumSize = new Dimension(100, 0);
		String[] listing = new String[] { "No results" };
		resultsList = new JList<Object>(listing);
		resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsList.setSelectedIndex(0);
		resultsList.addListSelectionListener(this);
		resultsListPane = new JScrollPane(resultsList);

		scorePanel.setMinimumSize(minimumSize);

		splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resultsListPane,
				scorePanel);
		splitPane.setDividerLocation(0.5);
		setMainPanelText("Choose search terms and select go");
		content.setLayout(new BorderLayout(0, 0));

		content.add(criteriaPanel, BorderLayout.NORTH);
		GridBagLayout gbl_criteriaPanel = new GridBagLayout();
		gbl_criteriaPanel.columnWidths = new int[] { 224, 224, 0 };
		gbl_criteriaPanel.rowHeights = new int[] { 25, 25, 25, 0 };
		gbl_criteriaPanel.columnWeights = new double[] { 0.0, 0.0,
				Double.MIN_VALUE };
		gbl_criteriaPanel.rowWeights = new double[] { 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		criteriaPanel.setLayout(gbl_criteriaPanel);

		searchTermsField = new JTextField();
		searchTermsField.setColumns(30);
		searchTermsField.setMinimumSize(searchTermsField.getPreferredSize());

		GridBagConstraints gbc_searchTermsField = new GridBagConstraints();
		gbc_searchTermsField.fill = GridBagConstraints.BOTH;
		gbc_searchTermsField.insets = new Insets(0, 0, 5, 5);
		gbc_searchTermsField.gridx = 0;
		gbc_searchTermsField.gridy = 0;
		criteriaPanel.add(searchTermsField, gbc_searchTermsField);
		btnGoButton.addActionListener(new AbstractAction() {
			{
				putValue(NAME, "Go");
				putValue(SHORT_DESCRIPTION, "Search for the chosen terms");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					runSearch();
				} catch (IOException | ParseException e1) {
					_logger.warning("Failed:" + e1.getLocalizedMessage());
				}
			}

		});
		GridBagConstraints gbc_btnGoButton = new GridBagConstraints();
		gbc_btnGoButton.fill = GridBagConstraints.BOTH;
		gbc_btnGoButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnGoButton.gridx = 1;
		gbc_btnGoButton.gridy = 0;
		criteriaPanel.add(btnGoButton, gbc_btnGoButton);

		rdbtnDirectorySearch = new JRadioButton("Directory Search");
		rdbtnDirectorySearch.setSelected(true);
		rdbtnDirectorySearch.setAction(new AbstractAction() {

			{
				putValue(NAME, "Directory Search");
				putValue(SHORT_DESCRIPTION,
						"Choose a folder with Freemind maps");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				btnChooseDirectoryButton.setEnabled(true);
				updateSelectedFolderField();
			}
		});

		directoryButtonGroup.add(rdbtnDirectorySearch);
		GridBagConstraints gbc_rdbtnDirectorySearch = new GridBagConstraints();
		gbc_rdbtnDirectorySearch.fill = GridBagConstraints.BOTH;
		gbc_rdbtnDirectorySearch.insets = new Insets(0, 0, 5, 5);
		gbc_rdbtnDirectorySearch.gridx = 0;
		gbc_rdbtnDirectorySearch.gridy = 1;
		criteriaPanel.add(rdbtnDirectorySearch, gbc_rdbtnDirectorySearch);
		rdbtnOpen = new JRadioButton("Open Maps");
		rdbtnOpen.setSelected(false);
		rdbtnOpen.setAction(new AbstractAction() {

			{
				putValue(NAME, "Open Maps");
				putValue(SHORT_DESCRIPTION,
						"Search the maps currently open in the application");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				btnChooseDirectoryButton.setEnabled(false);
				btnGoButton.setEnabled(true);
			}
		});

		directoryButtonGroup.add(rdbtnOpen);
		GridBagConstraints gbc_rdbtnOpen = new GridBagConstraints();
		gbc_rdbtnOpen.fill = GridBagConstraints.BOTH;
		gbc_rdbtnOpen.insets = new Insets(0, 0, 5, 0);
		gbc_rdbtnOpen.gridx = 1;
		gbc_rdbtnOpen.gridy = 1;
		criteriaPanel.add(rdbtnOpen, gbc_rdbtnOpen);

		btnChooseDirectoryButton = new JButton("Choose Directory");
		btnChooseDirectoryButton.setEnabled(true);
		btnChooseDirectoryButton.setAction(new AbstractAction() {
			{
				putValue(NAME, "Choose Directory");
				putValue(SHORT_DESCRIPTION, "Choose Directory");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				_logger.fine("Opened file chooser");
				JFileChooser fc = new JFileChooser() {

					@Override
					protected JDialog createDialog(Component parent)
							throws HeadlessException {
						JDialog dialog = super.createDialog(parent);
						// config here as needed - just to see a difference
						dialog.setLocationByPlatform(true);
						// might help - can't know because I can't reproduce the
						// problem
						dialog.setAlwaysOnTop(true);
						return dialog;
					}

				};
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int retValue = fc.showOpenDialog(null);
				if (retValue == JFileChooser.APPROVE_OPTION) {
					selectedDirectory = fc.getSelectedFile();
					_logger.info("Selected : " + selectedDirectory);
				} else {
					_logger.fine("Cancelled");
				}
				updateSelectedFolderField();
			}
		});
		GridBagConstraints gbc_btnChooseDirectoryButton = new GridBagConstraints();
		gbc_btnChooseDirectoryButton.fill = GridBagConstraints.BOTH;
		gbc_btnChooseDirectoryButton.insets = new Insets(0, 0, 0, 5);
		gbc_btnChooseDirectoryButton.gridx = 0;
		gbc_btnChooseDirectoryButton.gridy = 2;
		criteriaPanel.add(btnChooseDirectoryButton,
				gbc_btnChooseDirectoryButton);
		selectedDirectoryField.setEditable(false);
		selectedDirectoryField.setColumns(10);
		GridBagConstraints gbc_selectedDirectoryField = new GridBagConstraints();
		gbc_selectedDirectoryField.fill = GridBagConstraints.BOTH;
		gbc_selectedDirectoryField.gridx = 1;
		gbc_selectedDirectoryField.gridy = 2;
		criteriaPanel.add(selectedDirectoryField, gbc_selectedDirectoryField);
		content.add(splitPane, BorderLayout.CENTER);

		int width = 600;
		int height = 400;
		btnGoButton.setSize(10, 10);
		splitPane.setSize(width / 2, height / 2);
		criteriaPanel.setSize(width / 2, height / 2);
		content.setSize(width, height);
		setSize(width, height);

	}

	public void updateSelectedFolderField() {
		if (null == this.selectedDirectory) {
			selectedDirectoryField.setText("[No folder selected]");
			this.btnGoButton.setEnabled(false);
			this.btnGoButton
					.setToolTipText("Choose a valid folder or open maps before doing search");

		} else {
			selectedDirectoryField.setText(this.selectedDirectory.getPath());
			this.btnGoButton.setEnabled(true);
		}
	}

	private void runSearch() throws IOException, ParseException {
		File[] mapsFiles;
		boolean isDirectoryMode = isDirectoryMode();
		if (isDirectoryMode) {
			mapsFiles = new File[] { new File(selectedDirectoryField.getText()) };
		} else {
			mapsFiles = this.searchNodeHook.getFilesOfOpenTabs();
		}

		String searchString = this.searchTermsField.getText();
		setMainPanelText("Searching [" + Arrays.asList(mapsFiles) + "] for ["
				+ searchString + "]");

		Search search = new Search(_logger);

		Object[] listData = search.runSearch(searchString, mapsFiles);
		resultsList.setListData(listData);
		updateScorePanel();

	}

	public boolean isDirectoryMode() {
		boolean directoryMode = false;
		Enumeration<AbstractButton> mode = this.directoryButtonGroup
				.getElements();
		while (mode.hasMoreElements()) {
			AbstractButton button = mode.nextElement();
			if (button.getText().equals("Directory Search")
					&& button.isSelected()) {
				directoryMode = true;
			}
		}
		return directoryMode;
	}

	private JList<Object> resultsList;

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		updateScorePanel();
	}

	public void updateScorePanel() {
		int selectedIndex = this.resultsList.getSelectedIndex();
		if (selectedIndex < 0) {
			if (this.resultsList.getModel().getSize() < 0) {
				setMainPanelText("No results");
			} else {
				selectedIndex = 0;
			}
		} else {

			StringBuilder buf = new StringBuilder();

			SearchResult selectedItem = (SearchResult) this.resultsList
					.getSelectedValue();
			scorePanel.setText(selectedItem.getPath());
		}
	}

	public void setMainPanelText(String text) {
		_logger.info("Set panel text to : " + text);
		scorePanel.setText(text);
	}
}
