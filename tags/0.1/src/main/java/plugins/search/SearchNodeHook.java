/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2006 Joerg Mueller, Daniel Polansky, Christian Foltin, Dimitri Polivaev and others.
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
 */
/** this is only a test class */
package plugins.search;

import java.awt.Component;
import java.awt.Container;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.JComponent;

import freemind.main.FreeMindMain;
import freemind.main.XMLElement;
import freemind.modes.MindMapNode;
import freemind.modes.mindmapmode.hooks.PermanentMindMapNodeHookAdapter;
import freemind.view.MapModule;
import freemind.view.mindmapview.NodeView;

/**
 * 
 * 
 * 
 * @author Stephen Leonard
 * @since 14 Feb 2014
 * 
 * @version $Author:: $: Author of last commit
 * @version $Rev:: $: Revision of last commit
 * @version $Date:: $: Date of last commit
 * 
 */
public class SearchNodeHook extends PermanentMindMapNodeHookAdapter {
	private String equation;
	private Set viewers;

	/**
	 */
	public SearchNodeHook() {
		super();
		equation = "\\mbox{I}^\\fgcolor{ff0000}{\\heartsuit}\\mbox{HotEqn}";
		viewers = new LinkedHashSet();
		if (logger == null) {
			logger = getController().getFrame().getLogger(
					this.getClass().getName());
		}
	}

	public Logger getLogger(Class className) {
		return getController().getFrame().getLogger(className.getName());
	}

	public void onViewCreatedHook(NodeView nodeView) {
		createViewer(nodeView);
		super.onViewCreatedHook(nodeView);
	}

	public void onViewRemovedHook(NodeView nodeView) {
		deleteViewer(nodeView);
		super.onViewRemovedHook(nodeView);
	}

	private void deleteViewer(NodeView nodeView) {
		if (viewers.isEmpty()) {
			return;
		}
		final Container contentPane = nodeView.getContentPane();
		final int componentCount = contentPane.getComponentCount();
		for (int i = 0; i < componentCount; i++) {
			Component component = contentPane.getComponent(i);
			if (viewers.contains(component)) {
				viewers.remove(component);
				contentPane.remove(i);
				return;
			}
		}

	}

	/**
	 * 
	 */
	public void invoke(MindMapNode node) {
		Iterator iterator = node.getViewers().iterator();
		while (iterator.hasNext()) {
			NodeView view = (NodeView) iterator.next();
			createViewer(view);
		}
		super.invoke(node);
	}

	private void createViewer(NodeView view) {
		SearchPanel panel = new SearchPanel(this, getController().getFrame()
				.getJFrame());
		panel.setVisible(true);
	}

	public void openMap(String mapModule) {
		logger.fine("open map :" + mapModule);
		getController().loadURL(mapModule);
	}

	public File[] getFilesOfOpenTabs() {
		@SuppressWarnings("unchecked")
		List<MapModule> maps = getController().getFrame().getController()
				.getMapModuleManager().getMapModuleVector();
		File[] mapFiles = new File[maps.size()];
		for (int i = 0; i < mapFiles.length; i++) {
			mapFiles[i] = maps.get(i).getModel().getFile();
		}
		return mapFiles;
	}

	public void setContent(String key, String content) {
		Iterator iterator = viewers.iterator();
		getController().nodeChanged(getNode());
	}

	public void loadFrom(XMLElement child) {
		equation = child.getAttribute("EQUATION", equation).toString();
		super.loadFrom(child);
	}

	public void save(XMLElement xml) {
		super.save(xml);
		xml.setAttribute("EQUATION", equation);
	}

	public void shutdownMapHook() {
		Iterator iterator = viewers.iterator();
		while (iterator.hasNext()) {
		}
		super.shutdownMapHook();
	}

}
