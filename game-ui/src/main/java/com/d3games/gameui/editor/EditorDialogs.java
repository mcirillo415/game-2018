package com.d3games.gameui.editor;

import java.awt.Component;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.d3games.engine.editor.EditorCell;
import com.d3games.engine.editor.EditorCell.LockType;
import com.d3games.engine.editor.EditorRoom;
import com.d3games.engine.editor.EditorWorld;

/** Small modal dialogs that fill in the extra fields a palette tool needs at the moment a cell is placed. */
public class EditorDialogs {

	private static final String[] ITEM_KINDS = { "CURRENCY", "POTION", "POKEBALL", "REVIVE" };

	private EditorDialogs() {
	}

	public static boolean configureDoor(Component parent, EditorWorld world, EditorRoom currentRoom, EditorCell cell) {
		List<String> roomChoices = new ArrayList<>();
		for (EditorRoom room : world.getRooms())
			roomChoices.add(room.getName() + " (" + room.getId() + ")");

		JComboBox<String> targetCombo = new JComboBox<>(roomChoices.toArray(new String[0]));
		targetCombo.setEditable(true);
		if (cell.getTargetRoom() != null)
			targetCombo.setSelectedItem(cell.getTargetRoom());

		JTextField xField = new JTextField(String.valueOf(cell.getTargetX()));
		JTextField yField = new JTextField(String.valueOf(cell.getTargetY()));

		JComboBox<LockType> lockCombo = new JComboBox<>(LockType.values());
		lockCombo.setSelectedItem(cell.getLockType());
		JTextField keyNameField = new JTextField(cell.getLockKeyName() != null ? cell.getLockKeyName() : "");
		JTextField npcRoomField = new JTextField(cell.getLockNpcRoom() != null ? cell.getLockNpcRoom() : "");
		JTextField npcXField = new JTextField(String.valueOf(cell.getLockNpcX()));
		JTextField npcYField = new JTextField(String.valueOf(cell.getLockNpcY()));

		JPanel panel = new JPanel(new GridLayout(0, 2, 4, 4));
		panel.add(new JLabel("Target room:"));
		panel.add(targetCombo);
		panel.add(new JLabel("Target X:"));
		panel.add(xField);
		panel.add(new JLabel("Target Y:"));
		panel.add(yField);
		panel.add(new JLabel("Lock type:"));
		panel.add(lockCombo);
		panel.add(new JLabel("Lock key name (if KEY):"));
		panel.add(keyNameField);
		panel.add(new JLabel("Lock NPC room id (if NPC):"));
		panel.add(npcRoomField);
		panel.add(new JLabel("Lock NPC X (if NPC):"));
		panel.add(npcXField);
		panel.add(new JLabel("Lock NPC Y (if NPC):"));
		panel.add(npcYField);

		int result = JOptionPane.showConfirmDialog(parent, panel, "Configure Door", JOptionPane.OK_CANCEL_OPTION);
		if (result != JOptionPane.OK_OPTION)
			return false;

		try {
			String targetText = String.valueOf(targetCombo.getSelectedItem()).trim();
			String targetRoomId = resolveRoomId(world, targetText);
			if (targetRoomId == null) {
				JOptionPane.showMessageDialog(parent, "Unknown target room: " + targetText, "Invalid door",
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
			cell.setTargetRoom(targetRoomId);
			cell.setTargetX(Integer.parseInt(xField.getText().trim()));
			cell.setTargetY(Integer.parseInt(yField.getText().trim()));

			LockType lockType = (LockType) lockCombo.getSelectedItem();
			cell.setLockType(lockType);
			if (lockType == LockType.KEY) {
				cell.setLockKeyName(keyNameField.getText().trim());
			} else if (lockType == LockType.NPC) {
				cell.setLockNpcRoom(npcRoomField.getText().trim());
				cell.setLockNpcX(Integer.parseInt(npcXField.getText().trim()));
				cell.setLockNpcY(Integer.parseInt(npcYField.getText().trim()));
			}
			return true;
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(parent, "X/Y fields must be numbers.", "Invalid door",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private static String resolveRoomId(EditorWorld world, String text) {
		for (EditorRoom room : world.getRooms()) {
			if (text.equals(room.getName() + " (" + room.getId() + ")"))
				return String.valueOf(room.getId());
		}
		EditorRoom byName = world.getRoomByName(text);
		if (byName != null)
			return String.valueOf(byName.getId());
		try {
			Integer.parseInt(text);
			return text;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static boolean configureItemPickup(Component parent, EditorCell cell) {
		JComboBox<String> kindCombo = new JComboBox<>(ITEM_KINDS);
		if (cell.getPickupKind() != null)
			kindCombo.setSelectedItem(cell.getPickupKind());
		JTextField amountField = new JTextField(String.valueOf(cell.getPickupAmount() > 0 ? cell.getPickupAmount() : 1));

		JPanel panel = new JPanel(new GridLayout(0, 2, 4, 4));
		panel.add(new JLabel("Item kind:"));
		panel.add(kindCombo);
		panel.add(new JLabel("Amount:"));
		panel.add(amountField);

		int result = JOptionPane.showConfirmDialog(parent, panel, "Configure Item Pickup", JOptionPane.OK_CANCEL_OPTION);
		if (result != JOptionPane.OK_OPTION)
			return false;

		try {
			cell.setPickupKind((String) kindCombo.getSelectedItem());
			cell.setPickupAmount(Integer.parseInt(amountField.getText().trim()));
			return true;
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(parent, "Amount must be a number.", "Invalid pickup",
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	public static boolean configureDialogue(Component parent, EditorCell cell) {
		JTextField textField = new JTextField(cell.getDialogueText() != null ? cell.getDialogueText() : "");
		textField.setColumns(30);

		JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
		panel.add(new JLabel("Dialogue text:"));
		panel.add(textField);

		int result = JOptionPane.showConfirmDialog(parent, panel, "Configure Dialogue NPC", JOptionPane.OK_CANCEL_OPTION);
		if (result != JOptionPane.OK_OPTION)
			return false;

		cell.setDialogueText(textField.getText());
		return true;
	}

	public static boolean configureNpc(Component parent, EditorCell cell) {
		JCheckBox initiatesBattleBox = new JCheckBox("Initiates battle on approach", cell.isNpcInitiatesBattle());

		JPanel panel = new JPanel(new GridLayout(0, 1, 4, 4));
		panel.add(initiatesBattleBox);

		int result = JOptionPane.showConfirmDialog(parent, panel, "Configure NPC", JOptionPane.OK_CANCEL_OPTION);
		if (result != JOptionPane.OK_OPTION)
			return false;

		cell.setNpcInitiatesBattle(initiatesBattleBox.isSelected());
		return true;
	}
}
