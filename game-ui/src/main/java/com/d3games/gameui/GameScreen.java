package com.d3games.gameui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;

import javax.swing.JPanel;

import com.d3games.engine.battle.Battle;
import com.d3games.engine.map.GameMap;
import com.d3games.engine.map.Player;
import com.d3games.engine.map.Unit;
import com.d3games.engine.menu.AttackMenu;
import com.d3games.engine.menu.Menu;

public class GameScreen extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int MARGIN = 45;
	private static final int MAIN_BOX_SIZE = 260;
	private static final int MAIN_BOX_BOTTOM = MARGIN + MAIN_BOX_SIZE;
	private static final int BOX_GAP = 10;
	private static final int BATTLE_MENU_HEIGHT = 140;
	private static final int BATTLE_MENU_BOTTOM = MAIN_BOX_BOTTOM + BOX_GAP + BATTLE_MENU_HEIGHT;
	private static final int MESSAGE_Y = BATTLE_MENU_BOTTOM + 22;
	private static final int MESSAGE_LINE_HEIGHT = 16;
	private static final int PANEL_WIDTH = MARGIN * 2 + MAIN_BOX_SIZE;
	private static final int PANEL_HEIGHT = MESSAGE_Y + MESSAGE_LINE_HEIGHT * 2 + 15;

	private final GameController controller;

	public GameScreen() {
		this(false);
	}

	public GameScreen(boolean skipTitleScreen) {
		controller = new GameController(this::repaint, skipTitleScreen);
		addKeyListener(controller);
		setFocusable(true);
		setBackground(Color.BLACK);
		setDoubleBuffered(true);
		setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(PANEL_WIDTH, PANEL_HEIGHT);
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		g2d.setFont(getFont());

		GameMode gameMode = controller.getGameMode();
		if (gameMode == GameMode.TITLE) {
			paintTitleScreen(g2d);
			Toolkit.getDefaultToolkit().sync();
			g.dispose();
			return;
		}

		Player player = controller.getPlayer();
		GameMap playerMap = player.getMap();

		String worldName = playerMap.getWorldName();
		if (worldName != null && !worldName.isEmpty()) {
			g2d.setColor(Color.WHITE);
			g2d.drawString(worldName + " - Map " + playerMap.getId(), MARGIN + 5, 22);
		}

		g2d.setColor(Color.WHITE);
		g2d.drawRect(MARGIN, MARGIN, MAIN_BOX_SIZE, MAIN_BOX_SIZE);

		if (gameMode == GameMode.WORLD_MAP) {
			paintMapPlayerFixed(playerMap, player, g2d);
		} else if (gameMode == GameMode.MENU) {
			paintMenu(controller.getActiveMenu(), g2d);
		} else if (gameMode == GameMode.BATTLE) {
			paintBattleGraphics(player, controller.getBattle(), controller.getEnemyType(), g2d);
			paintBattleMenu(controller.getActiveMenu(), g2d);
		} else if (gameMode == GameMode.GAME_OVER) {
			paintGameOver(g2d);
		}

		String message = controller.getMessage();
		if (message != null) {
			g2d.setColor(Color.WHITE);
			drawWrapped(g2d, message, MARGIN + 5, MESSAGE_Y, MAIN_BOX_SIZE - 10, MESSAGE_LINE_HEIGHT);
		}

		Toolkit.getDefaultToolkit().sync();
		g.dispose();
	}

	private void paintTitleScreen(Graphics2D g2d) {
		g2d.setColor(Color.WHITE);
		g2d.drawString("GAME TITLE", MARGIN + 75, PANEL_HEIGHT / 2 - 20);
		g2d.drawString("Press CONFIRM to start", MARGIN + 40, PANEL_HEIGHT / 2 + 10);
	}

	private void drawWrapped(Graphics2D g2d, String text, int x, int y, int maxWidth, int lineHeight) {
		FontMetrics metrics = g2d.getFontMetrics();
		StringBuilder line = new StringBuilder();
		int lineY = y;
		for (String word : text.split(" ")) {
			String candidate = line.length() == 0 ? word : line + " " + word;
			if (metrics.stringWidth(candidate) > maxWidth && line.length() > 0) {
				g2d.drawString(line.toString(), x, lineY);
				lineY += lineHeight;
				line = new StringBuilder(word);
			} else {
				line = new StringBuilder(candidate);
			}
		}
		if (line.length() > 0)
			g2d.drawString(line.toString(), x, lineY);
	}

	private void paintMenu(Menu activeMenu, Graphics2D g2d) {
		g2d.fillRect(MARGIN + 5, MARGIN + 5, MAIN_BOX_SIZE - 10, MAIN_BOX_SIZE - 10);
		g2d.setColor(Color.BLACK);
		int counter = 0;
		for (String item : activeMenu.getDisplayNames()) {
			String selector = activeMenu.getSelected().equals(item) ? "> " : "    ";
			g2d.drawString(selector + item, MARGIN + 30, MARGIN + 30 + 25 * counter);
			counter++;
		}
	}

	private void paintGameOver(Graphics2D g2d) {
		g2d.setColor(Color.BLACK);
		g2d.fillRect(MARGIN + 5, MARGIN + 5, MAIN_BOX_SIZE - 10, MAIN_BOX_SIZE - 10);
		g2d.setColor(Color.RED);
		g2d.drawString("GAME OVER", MARGIN + 80, MARGIN + 120);
		g2d.setColor(Color.WHITE);
		g2d.drawString("Press CONFIRM to continue", MARGIN + 50, MARGIN + 150);
	}

	private void paintBattleMenu(Menu activeMenu, Graphics2D g2d) {
		int menuX = MARGIN;
		int menuY = MAIN_BOX_BOTTOM + BOX_GAP;

		g2d.setColor(Color.WHITE);
		g2d.drawRect(menuX, menuY, MAIN_BOX_SIZE, BATTLE_MENU_HEIGHT);
		g2d.setColor(Color.BLACK);
		g2d.fillRect(menuX + 2, menuY + 2, MAIN_BOX_SIZE - 4, BATTLE_MENU_HEIGHT - 4);
		g2d.setColor(Color.WHITE);
		String title = activeMenu instanceof AttackMenu ? "SELECT ATTACK" : "BATTLE";
		g2d.drawString(title, menuX + 15, menuY + 22);
		g2d.drawLine(menuX + 10, menuY + 30, menuX + MAIN_BOX_SIZE - 10, menuY + 30);

		int rowY = menuY + 52;
		for (String item : activeMenu.getDisplayNames()) {
			String selector = activeMenu.getSelected().equals(item) ? "> " : "   ";
			g2d.drawString(selector + item, menuX + 20, rowY);
			rowY += 24;
		}
	}

	private void paintBattleGraphics(Player player, Battle battle, EnemyType enemyType, Graphics2D g2d) {
		FontMetrics metrics = g2d.getFontMetrics();
		int boxRight = MARGIN + MAIN_BOX_SIZE - 10;

		g2d.drawImage(ImageResources.getBattleEnemyImage(enemyType), MARGIN + 165, MARGIN + 25, 64, 64, this);
		g2d.drawImage(ImageResources.getImage(player), MARGIN + 30, MARGIN + 150, 64, 64, this);

		String enemyLabel = battle != null
				? battle.getEnemy().getName() + " Lv" + battle.getEnemy().getLevel()
				: "ENEMY";
		g2d.drawString(enemyLabel, boxRight - metrics.stringWidth(enemyLabel), MARGIN + 110);
		g2d.drawString("PLAYER", MARGIN + 30, MARGIN + 232);

		if (battle != null) {
			String enemyHp = "HP " + battle.getEnemy().getHealth() + "/" + battle.getEnemy().getMaximumHealth();
			g2d.drawString(enemyHp, boxRight - metrics.stringWidth(enemyHp), MARGIN + 125);
			g2d.drawString("Lv" + battle.getPlayer().getLevel() + "  HP " + battle.getPlayer().getHealth() + "/"
					+ battle.getPlayer().getMaximumHealth(), MARGIN + 30, MARGIN + 247);
		}
	}

	@SuppressWarnings("unused")
	private void paintFixedMap(GameMap playerMap, Player player, Graphics2D g2d) {
		for (int i = 0; i < playerMap.getWidth(); i++) {
			for (int j = 0; j < playerMap.getHeight(); j++) {
				if (playerMap.get(i, j) != null) {
					Unit unit = playerMap.get(i, j);
					g2d.drawImage(ImageResources.getImage(unit), i * 50, j * 50, this);
					if (unit.hasCharacter())
						g2d.drawImage(ImageResources.getImage(unit.getCharacter()),
								i * 50, j * 50, this);
				}
			}
		}
		g2d.drawImage(ImageResources.getImage(player), player.getXPos() * 50,
				player.getYPos() * 50, this);
	}

	private void paintMapPlayerFixed(GameMap playerMap, Player player, Graphics2D g2d) {
		for (int i = -2; i < 3; i++) {
			for (int j = -2; j < 3; j++) {
				if (playerMap.get(player.getXPos() + i, player.getYPos() + j) != null) {
					Unit unit = playerMap.get(player.getXPos() + i, player.getYPos() + j);
					g2d.drawImage(ImageResources.getImage(unit), (i + 3) * 50, (j + 3) * 50, this);
					if (unit.hasCharacter())
						g2d.drawImage(ImageResources.getImage(unit.getCharacter()),
								(i + 3) * 50, (j + 3) * 50, this);
				}
			}
		}
		g2d.drawImage(ImageResources.getImage(player), 3 * 50, 3 * 50, this);
	}
}
