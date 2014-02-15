package plugins.search;

/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2007  Joerg Mueller, Daniel Polansky, Dimitri Polivaev, Christian Foltin and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * Created on 10.01.2007
 */
/*$Id: ScriptEditorPanel.java,v 1.1.2.18 2008/07/05 20:40:10 christianfoltin Exp $*/
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;

/**
 * A dialog allow selection and a font and its associated info.
 * 
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SearchPanel extends JDialog {
	private static final Logger _logger = Logger.getLogger(SearchPanel.class);

	public static void main(String[] args) {
		SearchPanel searchPanel = new SearchPanel();
		searchPanel.setVisible(true);

	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7303615911704261984L;

	private final boolean _selectStyles;

	private final JCheckBox _boldChk = new JCheckBox("Bold");
	private final JCheckBox _italicChk = new JCheckBox("Italic");

	private Font _font;

	private ActionListener _previewUpdater;

	/**
	 * Default ctor.
	 */
	public SearchPanel() {
		this((Frame) null);
	}

	/**
	 * ctor specifying whether styles can be selected.
	 * 
	 * @param selectStyles
	 *            If <TT>true</TT> bold and italic checkboxes displayed.
	 */
	public SearchPanel(boolean selectStyles) {
		this((Frame) null, selectStyles);
	}

	/**
	 * ctor specifying the parent frame.
	 * 
	 * @param owner
	 *            Parent frame.
	 */
	public SearchPanel(Frame owner) {
		super(owner, "Font Chooser", true);
		_selectStyles = true;
		createUserInterface();
	}

	/**
	 * ctor specifying the parent frame and whether styles can be selected.
	 * 
	 * @param owner
	 *            Parent frame.
	 * @param selectStyles
	 *            If <TT>true</TT> bold and italic checkboxes displayed.
	 */
	public SearchPanel(Frame owner, boolean selectStyles) {
		super(owner, "Font Chooser", true);
		_selectStyles = selectStyles;
		createUserInterface();
	}
	
	

	/**
	 * ctor specifying the parent dialog.
	 * 
	 * @param owner
	 *            Parent frame.
	 */
	public SearchPanel(Dialog owner) {
		super(owner, "Font Chooser", true);
		_selectStyles = true;
		createUserInterface();
	}

	/**
	 * ctor specifying the parent dialog and whether styles can be selected.
	 * 
	 * @param owner
	 *            Parent frame.
	 * @param selectStyles
	 *            If <TT>true</TT> bold and italic checkboxes displayed.
	 */
	public SearchPanel(Dialog owner, boolean selectStyles) {
		super(owner, "Font Chooser", true);
		_selectStyles = selectStyles;
		createUserInterface();
	}

	/**
	 * Component is being added to its parent.
	 */
	public void addNotify() {
		super.addNotify();
		if (_previewUpdater == null) {
			_previewUpdater = new PreviewLabelUpdater();
			_boldChk.addActionListener(_previewUpdater);
			_italicChk.addActionListener(_previewUpdater);
		}
	}

	/**
	 * Component is being removed from its parent.
	 */
	public void removeNotify() {
		super.removeNotify();
		if (_previewUpdater != null) {
			_boldChk.removeActionListener(_previewUpdater);
			_italicChk.removeActionListener(_previewUpdater);
			_previewUpdater = null;
		}
	}

	public Font showDialog() {
		return showDialog(null);
	}

	/**
	 * Show dialog defaulting to the passed font.
	 * 
	 * @param font
	 *            The font to default to.
	 */
	public Font showDialog(Font font) {
		if (font != null) {
			_boldChk.setSelected(_selectStyles && font.isBold());
			_italicChk.setSelected(_selectStyles && font.isItalic());
		} else {
			_boldChk.setSelected(false);
			_italicChk.setSelected(false);
		}
		setupPreviewLabel();
		setVisible(true);
		return _font;
	}

	public Font getSelectedFont() {
		return _font;
	}

	private void createUserInterface() {
		final JPanel content = new JPanel();

		setContentPane(content);
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		content.setLayout(new BoxLayout(content, BoxLayout.X_AXIS));
		content.add(createChoosePanel());
		
		JPanel panel = new JPanel();
		content.add(panel);
		
				JButton okBtn = new JButton("Choose Directory");
				panel.add(okBtn);
				okBtn.setToolTipText("Select the folder to search for maps in");
				okBtn.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent evt) {
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
				});
				getRootPane().setDefaultButton(okBtn);
		pack();
		setResizable(true);
	}

	private File selectedDirectory;
	private JTextField textField;

	private JPanel createChoosePanel() {
		JPanel pnl = new JPanel();
		pnl.setLayout(new BoxLayout(pnl, BoxLayout.X_AXIS));
		
		JLabel lblNewLabel = new JLabel("Search for");
		pnl.add(lblNewLabel);
		
		textField = new JTextField();
		pnl.add(textField);
		textField.setColumns(10);

		String path = "<NO SELECTION>";
		if (null != this.selectedDirectory) {
			path = this.selectedDirectory.getPath();
		}
		return pnl;
	}

	private void setupPreviewLabel() {
	}

	private final class PreviewLabelUpdater implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			setupPreviewLabel();
		}
	}
}
