package plugins.search;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.HeadlessException;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JSpinner;

import org.apache.log4j.Logger;

public class Test {

	private static final Logger _logger = Logger.getLogger(Test.class);
	private JFrame frame;
	private JTextField searchTermsField;
	private JRadioButton rdbtnOpen;
	private JRadioButton rdbtnDirectorySearch;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JButton btnChooseDirectoryButton;
	private JTextField selectedDirectoryField;
	private JSpinner spinner;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Test window = new Test();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Test() {
		initialize();
	}

	private File selectedDirectory;
	private JButton btnGoButton;

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel criteriaPanel = new JPanel();
		frame.getContentPane().add(criteriaPanel, BorderLayout.NORTH);
		criteriaPanel.setLayout(new MigLayout("", "[70px][114px][grow][]",
				"[19px][][]"));

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
		selectedDirectoryField = new JTextField();
		selectedDirectoryField.setEditable(false);
		selectedDirectoryField.setColumns(10);

		btnGoButton = new JButton("Go");
		btnGoButton.addActionListener(new AbstractAction() {
			{
				putValue(NAME, "Go");
				putValue(SHORT_DESCRIPTION, "Some short description");
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				spinner.setValue("test");
			}
		});

		criteriaPanel.add(lblSearchLabel, "cell 0 0,alignx left,aligny center");
		criteriaPanel
				.add(searchTermsField, "cell 1 0 2,alignx left,aligny top");
		criteriaPanel.add(btnGoButton, "cell 3 0 ");

		criteriaPanel.add(rdbtnDirectorySearch, "cell 0 1");
		criteriaPanel.add(rdbtnOpen, "cell 1 1");

		criteriaPanel.add(btnChooseDirectoryButton, "cell 0 2");
		criteriaPanel.add(selectedDirectoryField, "cell 1 2 3,growx");

		spinner = new JSpinner();
		frame.getContentPane().add(spinner, BorderLayout.CENTER);
	}

}
