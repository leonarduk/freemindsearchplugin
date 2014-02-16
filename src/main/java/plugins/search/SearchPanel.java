package plugins.search;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;

public class SearchPanel extends JDialog implements ListSelectionListener {

	private JTextField searchTermsField;
	private JRadioButton rdbtnOpen;
	private JRadioButton rdbtnDirectorySearch;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton btnChooseDirectoryButton;
	private JTextField selectedDirectoryField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SearchPanel searchPanel = new SearchPanel();
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

	private File selectedDirectory;
	private JButton btnGoButton;
	private JSplitPane splitPane;
	private JTextArea scorePanel;
	private JScrollPane resultsListPane;
	private IndexSearcher searcher;
	private FreeMindFileIndexer indexer;
	private ScoreDoc[] hits;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		final JPanel content = new JPanel();
		;
		setContentPane(content);
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));

		JPanel criteriaPanel = new JPanel();
		content.add(criteriaPanel);

		JLabel lblSearchLabel = new JLabel("Search for...");

		searchTermsField = new JTextField();
		searchTermsField.setColumns(30);

		rdbtnOpen = new JRadioButton("Open Maps");
		rdbtnOpen.setSelected(false);
		rdbtnOpen.setAction(new AbstractAction() {

			{
				putValue(NAME, "Open Maps");
				putValue(SHORT_DESCRIPTION, "Some short description");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				btnChooseDirectoryButton.setEnabled(false);
			}
		});

		buttonGroup.add(rdbtnOpen);

		rdbtnDirectorySearch = new JRadioButton("Directory Search");
		rdbtnDirectorySearch.setSelected(true);
		rdbtnDirectorySearch.setAction(new AbstractAction() {

			{
				putValue(NAME, "Directory Search");
				putValue(SHORT_DESCRIPTION, "Some short description");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				btnChooseDirectoryButton.setEnabled(true);
			}
		});
		btnGoButton = new JButton("Go");
		btnGoButton.addActionListener(new AbstractAction() {
			{
				putValue(NAME, "Go");
				putValue(SHORT_DESCRIPTION, "Some short description");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					runSearch();
				} catch (IOException | ParseException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		buttonGroup.add(rdbtnDirectorySearch);

		btnChooseDirectoryButton = new JButton("Choose Directory");
		btnChooseDirectoryButton.setEnabled(true);
		btnChooseDirectoryButton.setAction(new AbstractAction() {
			{
				putValue(NAME, "Choose Directory");
				putValue(SHORT_DESCRIPTION, "Some short description");
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
		selectedDirectoryField = new JTextField();
		selectedDirectoryField.setEditable(false);
		selectedDirectoryField.setColumns(10);
		updateSelectedFolderField();
		GroupLayout gl_criteriaPanel = new GroupLayout(criteriaPanel);
		gl_criteriaPanel
				.setHorizontalGroup(gl_criteriaPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_criteriaPanel
										.createSequentialGroup()
										.addGap(6)
										.addGroup(
												gl_criteriaPanel
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_criteriaPanel
																		.createSequentialGroup()
																		.addComponent(
																				lblSearchLabel)
																		.addGap(74)
																		.addComponent(
																				searchTermsField,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE)
																		.addGap(6)
																		.addComponent(
																				btnGoButton))
														.addGroup(
																gl_criteriaPanel
																		.createSequentialGroup()
																		.addComponent(
																				rdbtnDirectorySearch)
																		.addGap(19)
																		.addComponent(
																				rdbtnOpen))
														.addGroup(
																gl_criteriaPanel
																		.createSequentialGroup()
																		.addComponent(
																				btnChooseDirectoryButton)
																		.addGap(6)
																		.addComponent(
																				selectedDirectoryField,
																				GroupLayout.PREFERRED_SIZE,
																				393,
																				GroupLayout.PREFERRED_SIZE)))));
		gl_criteriaPanel
				.setVerticalGroup(gl_criteriaPanel
						.createParallelGroup(Alignment.LEADING)
						.addGroup(
								gl_criteriaPanel
										.createSequentialGroup()
										.addGap(6)
										.addGroup(
												gl_criteriaPanel
														.createParallelGroup(
																Alignment.LEADING)
														.addGroup(
																gl_criteriaPanel
																		.createSequentialGroup()
																		.addGap(5)
																		.addComponent(
																				lblSearchLabel))
														.addComponent(
																searchTermsField,
																GroupLayout.PREFERRED_SIZE,
																GroupLayout.DEFAULT_SIZE,
																GroupLayout.PREFERRED_SIZE)
														.addComponent(
																btnGoButton))
										.addGap(6)
										.addGroup(
												gl_criteriaPanel
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																rdbtnDirectorySearch)
														.addComponent(rdbtnOpen))
										.addGap(6)
										.addGroup(
												gl_criteriaPanel
														.createParallelGroup(
																Alignment.LEADING)
														.addComponent(
																btnChooseDirectoryButton)
														.addGroup(
																gl_criteriaPanel
																		.createSequentialGroup()
																		.addGap(3)
																		.addComponent(
																				selectedDirectoryField,
																				GroupLayout.PREFERRED_SIZE,
																				GroupLayout.DEFAULT_SIZE,
																				GroupLayout.PREFERRED_SIZE)))));
		criteriaPanel.setLayout(gl_criteriaPanel);

		// / Bottom pane
		resultsListPane = new JScrollPane();
		scorePanel = new JTextArea("");
		Dimension minimumSize = new Dimension(100, 0);
		String[] listing = new String[] { "No results" };
		resultsList = new JList<Object>(listing);
		resultsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		resultsList.setSelectedIndex(0);
		resultsList.addListSelectionListener(this);
		resultsListPane.setMinimumSize(minimumSize);
		resultsListPane.add(resultsList);

		scorePanel.setMinimumSize(minimumSize);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				resultsListPane, scorePanel);
		splitPane.setDividerLocation(0.5);
		content.add(splitPane, BorderLayout.CENTER);
		setMainPanelText("Choose search terms and select go");
		
		criteriaPanel.setMinimumSize(criteriaPanel.getPreferredSize());
		content.setMinimumSize(content.getPreferredSize());
		setMinimumSize(getPreferredSize());

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
		String searchString = this.searchTermsField.getText();
		File mapsDir = this.selectedDirectory;
		setMainPanelText("Building search index...");
		indexer = new FreeMindFileIndexer(_logger);
		Directory index = indexer.indexFileOrDirectory(mapsDir);
		DirectoryReader reader = DirectoryReader.open(index);
		setMainPanelText("Building search index... Running search...");

		searcher = indexer.getSearcher(index);
		Query query = indexer.getQuery(searchString);
		TopDocs results = indexer.doSearch(query, reader.numDocs(), searcher);
		hits = results.scoreDocs;
		Object[] listData = new String[hits.length];
		for (int i = 0; i < hits.length; i++) {
			Document d;
			try {
				int docId = hits[i].doc;
				d = searcher.doc(docId);
				listData[i] = indexer.getFilename(d);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		resultsList.setListData(listData);
		updateScorePanel();
	}

	private JList<Object> resultsList;

	@Override
	public void valueChanged(ListSelectionEvent arg0) {
		updateScorePanel();
	}

	public void updateScorePanel() {
		int selectedIndex = this.resultsList.getSelectedIndex();
		if (selectedIndex < 0) {
			setMainPanelText("No results");
		} else {

			StringBuilder buf = new StringBuilder();
			int docId = hits[selectedIndex].doc;
			Document d;
			try {
				d = searcher.doc(docId);
				buf.append(indexer.getFilename(d));
				scorePanel.setText(buf.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void setMainPanelText(String text) {
		_logger.info("Set panel text to : " + text);
		scorePanel.setText(text);
	}
}
