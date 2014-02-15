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
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

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
	private final JLabel _previewLbl = new JLabel("PreviewText");

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
		final JPanel content = new JPanel(new GridBagLayout());
		final GridBagConstraints gbc = new GridBagConstraints();

		setContentPane(content);
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		content.add(createChoosePanel());

		gbc.gridx = 0;
		++gbc.gridy;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		content.add(createPreviewPanel(), gbc);

		++gbc.gridy;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		pack();
		setResizable(true);
	}

	private JPanel createPreviewPanel() {
		final JPanel pnl = new JPanel(new BorderLayout());
		pnl.setBorder(BorderFactory.createTitledBorder("PreviewTitle"));
		Dimension prefSize = _previewLbl.getPreferredSize();
		prefSize.height = 50;
		_previewLbl.setPreferredSize(prefSize);
		pnl.add(_previewLbl, BorderLayout.CENTER);
		setupPreviewLabel();

		return pnl;
	}

	private File selectedDirectory;

	private JPanel createChoosePanel() {
		JPanel pnl = new JPanel();

		JButton okBtn = new JButton("Choose Directory");
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
		pnl.add(okBtn);
		getRootPane().setDefaultButton(okBtn);

		String path = "<NO SELECTION>";
		if (null != this.selectedDirectory) {
			path = this.selectedDirectory.getPath();
		}
		JLabel label = new JLabel(path);
		pnl.add(label);
		return pnl;
	}

	private void setupPreviewLabel() {
		_previewLbl.setFont(_font);
	}

	private final class PreviewLabelUpdater implements ActionListener {
		public void actionPerformed(ActionEvent evt) {
			setupPreviewLabel();
		}
	}
}

/*
 * Copyright (C) 2001-2003 Colin Bell colbell@users.sourceforge.net
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

class FontInfo implements Cloneable, Serializable {
	private static final long serialVersionUID = 1L;

	public interface IPropertyNames {
		String FAMILY = "family";
		String IS_BOLD = "isBold";
		String IS_ITALIC = "isItalic";
		String SIZE = "size";
	}

	private static String DEFAULT_FAMILY = "Monospaced";

	private String _familyName;
	private boolean _isBold;
	private boolean _isItalic;
	private int _size;

	public FontInfo() {
		super();
		setFamily(DEFAULT_FAMILY);
		setSize(12);
	}

	public FontInfo(Font font) {
		super();
		if (font == null) {
			throw new IllegalArgumentException("Null Font passed");
		}
		setFont(font);
	}

	/**
	 * Return a copy of this object.
	 */
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex.getMessage()); // Impossible.
		}
	}

	public String getFamily() {
		return _familyName;
	}

	public void setFamily(String value) {
		_familyName = value != null ? value : DEFAULT_FAMILY;
	}

	public boolean isBold() {
		return _isBold;
	}

	public void setIsBold(boolean value) {
		_isBold = value;
	}

	public boolean isItalic() {
		return _isItalic;
	}

	public void setIsItalic(boolean value) {
		_isItalic = value;
	}

	public int getSize() {
		return _size;
	}

	public void setSize(int value) {
		_size = value;
	}

	public void setFont(Font font) throws IllegalArgumentException {
		if (font == null) {
			throw new IllegalArgumentException("Null Font passed");
		}
		_familyName = font.getFamily();
		_isBold = font.isBold();
		_isItalic = font.isItalic();
		_size = font.getSize();
	}

	public boolean doesFontMatch(Font font) {
		if (font == null) {
			return false;
		}
		return font.getFamily().equals(_familyName)
				&& font.getSize() == getSize()
				&& font.getStyle() == generateStyle();
	}

	public int generateStyle() {
		int style = 0;
		if (!_isBold && !_isItalic) {
			style = Font.PLAIN;
		} else {
			if (_isBold) {
				style |= Font.BOLD;
			}
			if (_isItalic) {
				style |= Font.ITALIC;
			}
		}
		return style;
	}

	public Font createFont() {
		return new Font(_familyName, generateStyle(), _size);
	}

	// i18n ? What is this used for?
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(_familyName).append(", " + _size);
		if (_isBold) {
			buf.append(", bold");
		}
		if (_isItalic) {
			buf.append(", italic");
		}
		return buf.toString();
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((_familyName == null) ? 0 : _familyName.hashCode());
		result = PRIME * result + (_isBold ? 1231 : 1237);
		result = PRIME * result + (_isItalic ? 1231 : 1237);
		result = PRIME * result + _size;
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FontInfo other = (FontInfo) obj;
		if (_familyName == null) {
			if (other._familyName != null)
				return false;
		} else if (!_familyName.equals(other._familyName))
			return false;
		if (_isBold != other._isBold)
			return false;
		if (_isItalic != other._isItalic)
			return false;
		if (_size != other._size)
			return false;
		return true;
	}
}