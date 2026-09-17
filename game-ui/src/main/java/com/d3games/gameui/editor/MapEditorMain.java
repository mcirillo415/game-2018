package com.d3games.gameui.editor;

import javax.swing.SwingUtilities;

public class MapEditorMain {

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> new MapEditorFrame().setVisible(true));
	}
}
