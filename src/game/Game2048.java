package game;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


public class Game2048 extends JPanel{

	
	public Grids[][] allGrids;
	private boolean win;
	private boolean lose;
	private int score = 0;
	
	/*
	 * restart the game with default values
	 */
	public void resetGame() {
		allGrids = new Grids[4][4];
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				allGrids[i][j] = new Grids();
			}
		}
		score = 0;
		lose = false;
		win = false;
		//start with two numbers generated at random positions
		addRandomGrid();
		addRandomGrid();
	}
	
	/*
	 * generate a random number at a random empty grid
	 */
	public void addRandomGrid() {
		List<Grids> emptyGrids = this.getEmptySpace();
		
		//set the value of the random grid selected
		if (!emptyGrids.isEmpty()) {
			int index = (int) (Math.random() * emptyGrids.size());
			int val = Math.random() < 0.7 ? 2 : 4;
			emptyGrids.get(index).value = val;
		}
	}
	
	/*
	 * move all grids left; adding same numbers up and updating the all the 
	 * grids on the panel
	 */
	public void moveLeft() {
		boolean needNewGrids = false;
		//for each line, update the values
		for (int i = 0; i < 4; i++) {
			Grids[] newLine = new Grids[4];
			Grids[] oldLine = new Grids[4];
			//j represents column here
			for (int j = 0; j < 4; j++) {
				newLine[j] = allGrids[i][j];
				oldLine[j] = allGrids[i][j];
			}
			
			//let grids with value greater than 0 to occupy empty space
			LinkedList<Grids> l = new LinkedList<Grids>();
			for (int k = 0; k < 4; k++) {
				if (newLine[k].isEmpty()) continue;
				else l.add(newLine[k]);
			}
			setLineEmpty(newLine);
			for (int m = 0; m < 4 && !l.isEmpty(); m++) {
				newLine[m].value = l.poll().value;
			}
			
			//add up same numbers
			LinkedList<Grids> li = new LinkedList<Grids>();
			for (int a = 0; a < 4 && !newLine[a].isEmpty(); a++) {
				int num = newLine[a].value;
				if (a < 3 && newLine[a].value == newLine[a + 1].value) {
					num = num * 2;
					this.score += num;
					if (num == 2048) {
						win = true;
					}
					a++;
				}
				li.add(new Grids(num));
			}
			
			//ensure the size of li to be 4
			while(li.size() < 4) {
				li.add(new Grids());
			}
			
			if (!li.isEmpty()) {
				int g;
				for (g = 0; g < 4; g++) {
					newLine[g] = li.poll();
				}
				for (g = 0; g < 4; g++) {
					if (newLine[g].value != oldLine[g].value) { 
						needNewGrids = true;
					}
				}
				for (g = 0; g < 4; g++) {
					this.allGrids[i][g].value = newLine[g].value;
				}
			}
		}
		if (needNewGrids) addRandomGrid();
	}
	
	/*
	 * move all grids right; adding same numbers up and updating the all the 
	 * grids on the panel
	 */
	public void moveRight() {
		this.allGrids = rotate(180);
		this.moveLeft();
		this.allGrids = rotate(180);
	}
	
	/*
	 * move all grids up; adding same numbers up and updating the all the 
	 * grids on the panel
	 */
	public void moveUp() {
		this.allGrids = rotate(270);
		this.moveLeft();
		this.allGrids = rotate(90);
	}
	
	/*
	 * move all grids up; adding same numbers up and updating the all the 
	 * grids on the panel
	 */
	public void moveDown() {
		this.allGrids = rotate(90);
		this.moveLeft();
		this.allGrids = rotate(270);
	}
	
	/*
	 * rotate clockwise
	 * return a new two-dimensional array to store the grids
	 * the function is only set for angle = 90 or angle = 180
	 * or angle = 270
	 */
	public Grids[][] rotate(int angle) {
		Grids[][] res = new Grids[4][4];
		if (angle == 180) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					res[i][j] = this.allGrids[3 - i][3 - j];
				}
			}
		}
		else if (angle == 90) {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					res[i][j] = this.allGrids[j][3 - i];
				}
			}
		}
		//angle = 270
		else {
			for (int i = 0; i < 4; i++) {
				for (int j = 0; j < 4; j++) {
					res[i][j] = this.allGrids[3 - j][i];
				}
			}
		}
		return res;
	}
	
	/*
	 * set the line to contain new empty grids
	 */
	public static void setLineEmpty(Grids[] line) {
		for (int i = 0; i < 4; i++) {
			line[i] = new Grids(); 
		}
	}
	
	/*
	 * return a list that contains all the grids with value 0 (isEmpty)
	 */
	public List<Grids> getEmptySpace() {
		List<Grids> list = new ArrayList<Grids>();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (allGrids[i][j].isEmpty()) {
					list.add(allGrids[i][j]);
				}
			}
		}
		return list;
	}
	
	/*
	 * check if all grids are still movable
	 */
	public boolean canMove() {
		if (this.getEmptySpace().size() != 0) return true;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				if (i < 3 && allGrids[i][j].value == allGrids[i+1][j].value ||
					((j < 3) && allGrids[i][j].value == allGrids[i][j+1].value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/*
	 * initialize the game
	 */
	public Game2048() {
		setFocusable(true);
		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				int keycode = e.getKeyCode();
				if (!canMove()) {
					lose = true;
				}
				if (!lose) {
					switch(keycode) {
					case KeyEvent.VK_ESCAPE:
						resetGame();
					case KeyEvent.VK_LEFT:
						moveLeft();
						break;
					case KeyEvent.VK_RIGHT:
						moveRight();
						break;
					case KeyEvent.VK_DOWN:
						moveDown();
						break;
					case KeyEvent.VK_UP:
						moveUp();
						break;
					}
				}
				if (keycode == KeyEvent.VK_ESCAPE) resetGame();
				repaint();
			}
		});
		resetGame();
	}
	
	
	/*
	 * draw the game panel
	 */
	@Override
	public void paint (Graphics g) {
		super.paint(g);
		g.setColor(new Color(0xbbada0));
		g.fillRect(0, 0, this.getSize().width, this.getSize().height);
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				drawGrids(g, allGrids[3 - i][j], j, i);
			}
		}
	}
	
	public void drawGrids(Graphics g2, Grids grid, int x, int y) {
		Graphics2D g = ((Graphics2D) g2);
		int number = grid.value;
		int xStart = x * (64 + 16) + 16;
		int yStart = y * (64 + 16) + 16;
		//draw the grid without numbers
		g.setColor(grid.getGridBackground());
		g.fillRoundRect(xStart, yStart, 64, 64, 14, 14);
		//draw the number
		g.setColor(grid.getNumColor());
		int fontsize = number < 100 ? 36 : number < 1000 ? 32 : 24;
		Font font = new Font("Arial", Font.BOLD, fontsize);
		g.setFont(font);
		
		String text = String.valueOf(number);
		FontMetrics f = getFontMetrics(font);
		int width = f.stringWidth(text);
		int height = (int) f.getLineMetrics(text, g).getHeight();
		
		if (number != 0) {
			g.drawString(text, xStart + (64 - width) / 2, yStart + 64 - (64 - height) / 2 - 5);
		}
		
		if (win || lose) {
			g.setColor(new Color(255, 255, 255, 30));
			g.fillRect(0, 0, getWidth(), getHeight());
			g.setColor(new Color(78, 139, 202));
			g.setFont(new Font("Arial", Font.BOLD, 48));
			if (win) {
				g.drawString("You won!", 68, 150);
			}
			if (lose) {
				g.drawString("Game over!", 50, 130);
				g.drawString("You lose!", 64, 200);
			}
			if (win || lose) {
				g.setFont(new Font("Arial", Font.PLAIN, 16));
				g.setColor(new Color(128, 128, 128, 128));
				g.drawString("Press ESC to play again", 80, getHeight() - 40);
			}
		}
		g.setFont(new Font ("Arial", Font.PLAIN, 18));
		g.drawString("Score: " + score, 200, 365);
	}
	
	public static void main(String[] args) {
		JFrame game = new JFrame();
		game.setTitle("2048 Game");
		game.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		game.setSize(340, 400);
		game.setResizable(false);
		game.add(new Game2048());
		game.setLocationRelativeTo(null);
		game.setVisible(true);
	}

}
