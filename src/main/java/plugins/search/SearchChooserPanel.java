package plugins.search;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import javax.swing.Action;

public class SearchChooserPanel extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 711092098054705805L;
	private static final Logger _logger = Logger
			.getLogger(SearchChooserPanel.class);
	private JTextField searchFieldstextField;
	private JTextField selectedDirectoryField;
	private JTable resultsTable;
	private static JButton btnChooseDirectoryButton;
	private JPanel selectionPanel;
	private final Action action = new SwingAction();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SearchChooserPanel window = new SearchChooserPanel();
		window.setVisible(true);
	}

	/**
	 * Create the application.
	 */
	public SearchChooserPanel() {
		this(null);
	}

	public SearchChooserPanel(Frame owner) {
		super(owner, "Font Chooser", true);
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		int width = 800;
		int height = 600;
		this.setBounds(0, 0, width, height);

		// Top panel
		selectionPanel = new JPanel();
		selectionPanel.setLayout(new MigLayout());
		getContentPane().add(selectionPanel);

		selectionPanel.add(createSearchTermsPanel(), "wrap");
		selectionPanel.add(createRadioButtonsPanel(), "wrap");
		selectionPanel.add(createDirectoryChooserPanel());

		// Bottom panel
		JPanel resultsPanel = new JPanel();
		getContentPane().add(resultsPanel);
		
		JButton btnNewButton = new JButton("New button");
		btnNewButton.setAction(action);
		resultsPanel.add(btnNewButton);

		resultsTable = new JTable();
		resultsPanel.add(resultsTable);
	}

	public JPanel createRadioButtonsPanel() {

		JPanel radioButtonPanel = new JPanel();
		radioButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JRadioButton searchOpenMapsButton = new JRadioButton("Search Open Maps");
		searchOpenMapsButton.setAction(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				btnChooseDirectoryButton.setEnabled(false);
			}

		});
		searchOpenMapsButton.setSelected(true);
		radioButtonPanel.add(searchOpenMapsButton);

		JRadioButton searchDirectoryButton = new JRadioButton(
				"Search Directory");
		searchDirectoryButton.setText(getName());
		searchDirectoryButton.setAction(new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				btnChooseDirectoryButton.setEnabled(true);
			}
		});
		radioButtonPanel.add(searchDirectoryButton);

		// Group the radio buttons.
		ButtonGroup radioGroup = new ButtonGroup();
		radioGroup.add(searchOpenMapsButton);
		radioGroup.add(searchDirectoryButton);
		return radioButtonPanel;
	}

	public JPanel createDirectoryChooserPanel() {
		JPanel directoryChooserPanel = new JPanel();
		btnChooseDirectoryButton = new JButton("Choose Directory");
		btnChooseDirectoryButton.setEnabled(false);
		btnChooseDirectoryButton.setAction(new AbstractAction() {

			private File selectedDirectory;

			@Override
			public void actionPerformed(ActionEvent e) {
				_logger.debug("Opened file chooser");
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
					selectedDirectoryField.setText(selectedDirectory.getPath());
					_logger.info("Selected : " + selectedDirectory);
				} else {
					_logger.debug("Cancelled");
				}
			}
		});
		directoryChooserPanel.add(btnChooseDirectoryButton);

		selectedDirectoryField = new JTextField();
		selectedDirectoryField.setEditable(false);
		directoryChooserPanel.add(selectedDirectoryField);
		selectedDirectoryField.setColumns(20);

		return directoryChooserPanel;
	}

	public JPanel createSearchTermsPanel() {
		JPanel searchTermsPanel = new JPanel();
		JLabel lblSearchFor = new JLabel("Search for");
		searchTermsPanel.add(lblSearchFor);

		searchFieldstextField = new JTextField();
		searchTermsPanel.add(searchFieldstextField);
		searchFieldstextField.setColumns(30);
		return searchTermsPanel;
	}
	private class SwingAction extends AbstractAction {
		public SwingAction() {
			putValue(NAME, "SwingAction");
			putValue(SHORT_DESCRIPTION, "Some short description");
		}
		public void actionPerformed(ActionEvent e) {
		}
	}
}
