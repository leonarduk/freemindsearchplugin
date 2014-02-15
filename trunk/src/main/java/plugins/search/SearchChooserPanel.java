package plugins.search;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

public class SearchChooserPanel extends JDialog {

	private static final Logger _logger = Logger
			.getLogger(SearchChooserPanel.class);
	private JFrame frame;
	private JTextField searchFieldstextField;
	private JTextField selectedDirectoryField;
	private JTable resultsTable;
	private final Action chooseDirectoryAction = new ChooseDirectoryAction();

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
		JPanel selectionPanel = new JPanel();
		selectionPanel.setBounds(0, 0, width, 100);

		JLabel lblSearchFor = new JLabel("Search for");
		selectionPanel.add(lblSearchFor);

		searchFieldstextField = new JTextField();
		selectionPanel.add(searchFieldstextField);
		searchFieldstextField.setColumns(30);
		this.setLayout(new GridLayout(2, 0, 0, 0));
		this.add(selectionPanel);

		JPanel radioButtonPanel = new JPanel();
		selectionPanel.add(radioButtonPanel); 
		radioButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
 
		JRadioButton searchOpenMapsButton = new JRadioButton("Search Open Maps");
		searchOpenMapsButton.setSelected(true);
		radioButtonPanel.add(searchOpenMapsButton);

		JRadioButton searchDirectoryButton = new JRadioButton("Search Directory");
		radioButtonPanel.add(searchDirectoryButton);

	    // Group the radio buttons.
	    ButtonGroup group = new ButtonGroup();
	    group.add(searchOpenMapsButton);
	    group.add(searchDirectoryButton);
	    
		JPanel directoryChooserPanel = new JPanel();
		selectionPanel.add(directoryChooserPanel);

		JButton btnChooseDirectoryButton = new JButton("Choose Directory");
		btnChooseDirectoryButton.setAction(chooseDirectoryAction);
		directoryChooserPanel.add(btnChooseDirectoryButton);

		selectedDirectoryField = new JTextField();
		selectedDirectoryField.setEditable(false);
		directoryChooserPanel.add(selectedDirectoryField);
		selectedDirectoryField.setColumns(20);

		JPanel resultsPanel = new JPanel();
		this.add(resultsPanel);

		resultsTable = new JTable();
		resultsPanel.add(resultsTable);
	}

	private class ChooseDirectoryAction extends AbstractAction {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2799316312136042279L;
		private File selectedDirectory;

		public ChooseDirectoryAction() {
			putValue(NAME, "Choose Directory");
			putValue(SHORT_DESCRIPTION, "Choose directory to search");
		}

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
				_logger.info("Selected : " + selectedDirectory);
			} else {
				_logger.debug("Cancelled");
			}
		}
	}

}
