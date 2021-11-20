/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robotics;

/**
 *
 * @author USER
 */

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.concurrent.*;
import javax.swing.border.*;
import javax.swing.text.*;

interface Content {
	public Color getColor();
	public void processInput();
}

interface KeyValue {
	public int getKey();
	public void setKey(int k);
	public Node getNode();
	public void setNode(Node n);
}


interface Handler {
	public void processInput(Content c);
	public void updateDisplay();
}


class Utilities {
	
	public static void sleep(int ms) {
		try {
			Thread.sleep(ms);
		} catch(InterruptedException e) {
			System.out.print("Error sleeping");
		}
	}

	
	public static Color makeColor(double r, double g, double b) {
		return new Color((float) (r/255.0),(float) (g/255.0),(float) (b/255.0));
	}

	public static Color contrastingColor(Color c) {
		if(c == Color.BLACK) {
			return Color.WHITE;
		} else {
			return Color.BLACK;
		}
	}

	
	
	public static Color valueToColor(int v) {
		switch(v) {
			
			case 0:
				
				return Color.BLACK;
			case 1:
				
				return Color.WHITE;
			case 2:
				
				return Color.RED;
			case 3:
				
				return Color.BLUE;
			
			case 4:
				
				return Color.ORANGE;
			case 5:
				
				return Color.GRAY;
			case 6:
				
				return Color.YELLOW;
			case 7:
				
				return Color.GREEN;
			
			case 8:
				
				return Color.YELLOW;
			case 9:
				
				return Color.DARK_GRAY;
			case 10:
				
				return Color.ORANGE;
			default:
				
				return Color.GRAY;
		}
	}


	public static void addStyledText(JTextPane pane, String s, int id, String font, int fontSize, Color color, boolean bold, boolean italic, boolean center) {
		StyledDocument doc = pane.getStyledDocument();
		SimpleAttributeSet attributeSet = new SimpleAttributeSet();
		String uniqueID = Integer.toString(id);
		Style style = pane.addStyle(uniqueID, null);
		if(center) {
			StyleConstants.setAlignment(attributeSet, StyleConstants.ALIGN_CENTER);
		}
		if(bold) {
			StyleConstants.setBold(style, true);
		}
		if(italic) {
			StyleConstants.setItalic(style, true);
		}
		StyleConstants.setFontFamily(style, font);
		StyleConstants.setFontSize(style, fontSize);
		StyleConstants.setForeground(style, color);

		try {doc.insertString(doc.getLength(), s, style); }
    catch (BadLocationException e){}

		doc.setParagraphAttributes(0, doc.getLength() - 1, attributeSet, false);
	}


	public static String displayTime(long timeUsed) {
		long secondsUsed = timeUsed / 1000;
		long minutesUsed = secondsUsed / 60;
		secondsUsed = secondsUsed % 60;
		long millisecondsUsed = timeUsed % 1000;
		return timeString(minutesUsed, secondsUsed, millisecondsUsed);
	}

	
	public static String timeString(long m, long s, long ms) {
		if(m > 0) {
			 return new String(Long.toString(m) + "m "+ Long.toString(s) + "s " + Long.toString(ms) + "ms");
		}
		return new String(Long.toString(s) + "s " + Long.toString(ms) + "ms");
	}

}


class CustomFrame extends JFrame {

	private int width;
	private int height;
	private String title;
	private CustomPanel contentPanel;
	private int panelID;

	
	public CustomFrame(int x, int y, String t) {
		width = x+50;
		height = y+50;
		title = t;
		panelID = 0;
		setTitle(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(x, y);

		this.contentPanel = new CustomPanel(x, y, this, panelID, null);
		add(contentPanel);
	}

	
	public CustomFrame(int x, int y, String t, boolean mainWindow) {
		this(x, y, t);
		if(mainWindow) {
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		} else {
			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		}
	}

	public CustomPanel getContentPanel() {
		return this.contentPanel;
	}

	
	public void display() {
		pack();
		setVisible(true);
		contentPanel.display();
	}

	
	public void close() {
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getTitle() {
		return title;
	}

	public void setFrameBackground(Color c) {
		contentPanel.setBackground(c);
	}


	public CustomPanel getPanel(int i) {
		return contentPanel.getPanel(i);
	}

	
	public int addPanel(int x, int y, int gridwidth, int gridheight, int w, int h, Content c) {
		panelID++;
		contentPanel.addPanel(x, y, gridwidth, gridheight, w, h, this, panelID, c);
		return panelID;
	}

	public int addPanel(int x, int y, int w, int h, Content c) {
		panelID++;
		contentPanel.addPanel(x, y, w, h, this, panelID, c);
		return panelID;
	}

	public void addButton(int id, String s) {
		getPanel(id).addButton(s);
	}

	public void processInput(int id) {
		getPanel(id).getContent().processInput();
	}

}

class CustomPanel extends JPanel {
	private int width;
	private int height;
	private GridBagConstraints constraints; 
	private ArrayList<CustomPanel> panels;
	private JButton button;	
	private CustomFrame root;
	private CustomPanel parent;
	private int myID;
	private Content content; 
	private Border border;


	public CustomPanel(int x, int y, CustomFrame f, int id, Content c) {
		width = x;
		height = y;
		root = f;
		myID = id;
		content = c;
		setLayout(new GridBagLayout());
		constraints = new GridBagConstraints();
		if(myID == 0) {
			panels = new ArrayList<CustomPanel>();
		} else {
			border = BorderFactory.createLineBorder(Color.WHITE);
		}
	}

	public GridBagConstraints getConstraints() {
		return constraints;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getID() {
		return myID;
	}

	public Content getContent() {
		return content;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	
	public void display() {
		setSize(getPreferredSize());
		setVisible(true);
		if(myID == 0) {
			
			for(CustomPanel p : panels) {
				p.display();
			}
		} else {
			Color color = content.getColor();
			Color buttonColor = Utilities.contrastingColor(color);
			setBackground(color);

			if(button != null) {
				if(content instanceof MenuButton) {
					
					button.setOpaque(true);
					button.setContentAreaFilled(true);
					button.setBorderPainted(true);
					button.setForeground(buttonColor);
					button.setVisible(true);
				} else {
					setBorder(border);
					button.setOpaque(false);
					button.setContentAreaFilled(false);
					button.setBorderPainted(false);
					button.setForeground(buttonColor);
					button.setVisible(true);
				}
			}
		}
	}

	// Add and attach a sub-panel. x,y are relative positions not pixels
	public void addPanel(int x, int y, int gridwidth, int gridheight, int w, int h, CustomFrame f, int id, Content c) {
		CustomPanel newPanel = new CustomPanel(w, h, f, id, c);
		constraints.gridx = x;
		constraints.gridy = y;
		constraints.gridwidth = gridwidth;
		constraints.gridheight = gridheight;
		add(newPanel, constraints);
		panels.add(newPanel);
	}

	public void addPanel(int x, int y, int w, int h, CustomFrame f, int id, Content c) {
		CustomPanel newPanel = new CustomPanel(w, h, f, id, c);
		constraints.gridx = x;
		constraints.gridy = y;
		add(newPanel, constraints);
		panels.add(newPanel);
	}

	public ArrayList<CustomPanel> getPanels() {
		return panels;
	}

	public CustomPanel getPanel(int id) {
		assert ((id >= 1) && (id <= panels.size()) && (myID == 0));
		return panels.get(id - 1); 
	}

	
	public void addButton(String text) {
		assert (myID != 0);
		if(content instanceof MenuButton) {
			button = new JButton(text);
		} else {
			button = new JButton();
		}
		button.setFocusable(false);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				root.processInput(myID);
      }
    });
		button.setPreferredSize(getPreferredSize());
		add(button);
	}

}


class MenuButton implements Content {

	private Handler h;
	private String title;
	private String action;
	private Grid g;

	public MenuButton(String text, Handler handler, String action, Grid g) {
		title = text;
		h = handler;
		this.action = action;
		this.g = g;
	}

	public void processInput() {
		h.processInput(this);
	}

	public Color getColor() {
		return Color.WHITE;
	}

	public String getTitle() {
		return title;
	}

	public String getAction() {
		return action;
	}

	public Grid getGrid() {
		return g;
	}
}

class Cell implements Content {
	private int value;
	private Grid parent; 

	public Cell(int v, Grid g) {
		value = v;
		parent = g;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int v) {
		value = v;
	}

	public void processInput() {
		parent.processInput(this);
	}

	public Color getColor() {
		return Utilities.valueToColor(getValue());
	}

	public Grid getGrid() {
		return parent;
	}

}

class Grid implements Iterable<Cell> {
	private Cell[][] cells;
	private int width; 
	private int height;
	private Handler caller; 
	private boolean lock; 
	private int valueToMove; 
	private boolean finalised; 

	public Grid(int x, int y) {
		width = x;
		height = y;
		cells = new Cell[width][height];
		init(0);
		caller = null; 
		lock = false;
		valueToMove = 0;
		finalised = false;
	}

	public Grid(int x, int y, Handler h) {
		width = x;
		height = y;
		cells = new Cell[width][height];
		init(0);
		caller = h;
		lock = false;
		valueToMove = 0;
		finalised = false;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public void toggleLock() {
		lock = !lock;
	}

	public boolean getLock() {
		return lock;
	}

	public void setValueToMove(int v) {
		valueToMove = v;
	}

	public int getValueToMove() {
		return valueToMove;
	}

	public void finaliseGrid() {
		finalised = true;
	}

	public void setFinalise(boolean b) {
		finalised = b;
	}

	public boolean getFinalised() {
		return finalised;
	}

	@Override
	public Iterator<Cell> iterator() {
		Iterator<Cell> iter = new Iterator<Cell>() {
			private int x = 0;
			private int y = 0;

			@Override
			public boolean hasNext() {
				return ((x < width) && (y < height));
			}

			@Override
			public Cell next() {
				Cell c = cells[x++][y];
				y = (x == width) ? (y+1) : y;
				x = (x == width) ?	 0 	 : x;
				return c;
			}

			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
		return iter;
	}

	public void init(int v) {
		for(int j=0; j<width; j++) {
			for(int i=0; i<height; i++) {
				cells[j][i] = new Cell(v, this);
			}
		}
	}

	public int sumValues() {
		int t = 0;
		for(Cell c : this) {
			t += c.getValue();
		}
		return t;
	}

	public int getCellValue(int x, int y) {
		return cells[x][y].getValue();
	}

	public void setCellValue(int x, int y, int v) {
		cells[x][y].setValue(v);
	}

	public void markSelected(int x, int y) {
		setCellValue(x, y, getCellValue(x, y) + 4);
	}

	public void markUnselected(int x, int y) {
		assert(getCellValue(x, y) >= 4); 
		setCellValue(x, y, getCellValue(x, y) - 4);
	}

	public void markOpen(int x, int y) {
		if(getCellValue(x, y) == 0) {
			setCellValue(x, y, 8);
		}
	}

	public void markUnopen(int x, int y) {
		if(getCellValue(x, y) == 8) {
			setCellValue(x, y, 0);
		}
	}

	public void markVisited(int x, int y) {
		if(getCellValue(x, y) == 0) {
			setCellValue(x, y, 9);
		}
	}

	public void markSolution(int x, int y) {
		int val = getCellValue(x, y);
		if((val == 0) || (val == 4) || (val == 8) || (val == 9)) {
			setCellValue(x, y, 10);
		}
	}

	public void removeMarkings() {
		int v;
		for(Cell c : this) {
			v = c.getValue();
			if(v > 7) {
				c.setValue(0);
			} else {
				c.setValue(v % 4); 
			}
		}
	}

	public Cell getCellAt(int x, int y) {
		return cells[x][y];
	}

	public int[][] asMatrix() {
		int[][] matrix = new int[width][height];
		for(int j=0; j<width; j++) {
			for(int i=0; i<height; i++) {
				matrix[j][i] = cells[j][i].getValue();
			}
		}
		return matrix;
	}

	public void processInput(Cell c) {
		caller.processInput(c);
	}
}

class GridGUI {
	Grid grid;
	int x;
	int y;
	CustomFrame frame;
	int delayMilliseconds;

	public GridGUI(Grid g, int delay) {
		grid = g;
		x = g.getWidth();
		y = g.getHeight();
		delayMilliseconds = delay;
	}

	public GridGUI(Grid g) {
		this(g, 0);
	}

	public void createGUI(int cellWidth, int cellHeight, String title) {

		int width = x * cellWidth;
		int height = y * cellHeight;

		frame = new CustomFrame(width, height, title, true);

		Cell c;
		int id;
		for(int j=0; j<y; j++) {
			for(int i=0; i<x; i++) {
				c = grid.getCellAt(i, j);
				id = frame.addPanel(i, j, cellWidth, cellHeight, c);
				frame.addButton(id, Integer.toString(id));
			}
		}

		frame.display();

	}

	public void createGUI(int cellWidth, int cellHeight, String title, MenuButton[] buttons) {

		int width = x * cellWidth;
		int height = y * cellHeight;
		if(buttons.length > 0) {
			height += cellHeight;
		}
		frame = new CustomFrame(width, height, title, true);

		Cell c;
		int id;
		for(int j=0; j<y; j++) {
			for(int i=0; i<x; i++) {
				c = grid.getCellAt(i, j);
				id = frame.addPanel(i, j, cellWidth, cellHeight, c);
				frame.addButton(id, Integer.toString(id));
			}
		}

		int k = 0;
		for(MenuButton b : buttons) {
			id = frame.addPanel(k, y, cellWidth, cellHeight, b);
			frame.addButton(id, b.getTitle());
			k++;
		}

		frame.display();

	}

	public void instantUpdate() {
		frame.display();
	}

	public void updateDisplay() {
		frame.display();
		Utilities.sleep(delayMilliseconds);

	}
}

class Demo implements Handler {

	Grid g;
	GridGUI gui;

	public void runMenu() {
		Color pastelBlue = Utilities.makeColor(175.0, 218.0, 240.0);

		CustomFrame frame = new CustomFrame(500, 350, "Choose grid size", false);
		CustomPanel panel = frame.getContentPanel();
		GridBagConstraints constraints = panel.getConstraints();

		JPanel sliderPanel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500,100);
			};
		};
		constraints.gridx = 0;
		constraints.gridy = 0;
		panel.add(sliderPanel, constraints);

		JSlider slider = new JSlider(5, 50) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500,50);
			};
		};
		slider.setFont(new Font("Helvetica", Font.BOLD, 16));
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(5);
		slider.setSnapToTicks(true);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setValue(10);

		slider.setFocusable(false);

		sliderPanel.add(slider);

		JTextPane sliderHelp = new JTextPane() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500,50);
			};
		};
		sliderHelp.setEditable(false);

		String text = "Number of columns";

		String font = "Helvetica";
		int fontSize = 16;
		Utilities.addStyledText(sliderHelp, text, 0, font, fontSize, Color.BLACK, true, false, true);

		sliderPanel.add(sliderHelp);

		JPanel sliderPanel2 = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500,100);
			};
		};
		constraints.gridx = 0;
		constraints.gridy = 1;
		panel.add(sliderPanel2, constraints);

		JSlider slider2 = new JSlider(5, 50) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500,50);
			};
		};
		slider2.setFont(new Font("Helvetica", Font.BOLD, 16));
		slider2.setMinorTickSpacing(1);
		slider2.setMajorTickSpacing(5);
		slider2.setSnapToTicks(true);
		slider2.setPaintTicks(true);
		slider2.setPaintLabels(true);
		slider2.setValue(10);

		slider2.setFocusable(false);

		sliderPanel2.add(slider2);

		JTextPane sliderHelp2 = new JTextPane() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500,50);
			};
		};
		sliderHelp2.setEditable(false);

		String text2 = "Number of rows";

		Utilities.addStyledText(sliderHelp2, text2, 0, font, fontSize, Color.BLACK, true, false, true);

		sliderPanel2.add(sliderHelp2);

		JPanel sliderPanel3 = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500,100);
			};
		};
		constraints.gridx = 0;
		constraints.gridy = 2;
		panel.add(sliderPanel3, constraints);

		JSlider slider3 = new JSlider(0, 1000) {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500,50);
			};
		};
		slider3.setFont(new Font("Helvetica", Font.BOLD, 16));
		slider3.setMinorTickSpacing(20);
		slider3.setMajorTickSpacing(100);
		slider3.setSnapToTicks(false);
		slider3.setPaintTicks(true);
		slider3.setPaintLabels(true);
		slider3.setValue(100);

		slider3.setFocusable(false);

		sliderPanel3.add(slider3);

		JTextPane sliderHelp3 = new JTextPane() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500,50);
			};
		};
		sliderHelp3.setEditable(false);

		String text3 = "Delay (milliseconds)";

		Utilities.addStyledText(sliderHelp3, text3, 0, font, fontSize, Color.BLACK, true, false, true);

		sliderPanel3.add(sliderHelp3);

		JPanel confirmPanel = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(500,50);
			};
		};
		constraints.gridx = 0;
		constraints.gridy = 3;
		panel.add(confirmPanel, constraints);

		JButton button = new JButton("Start") {
			@Override
			public Dimension getPreferredSize() {
				return new Dimension(200,40);
			};
		};
		button.setFont(new Font("Helvetica", Font.BOLD, 24));
		button.setFocusable(false);

		Demo ptr = this;

		button.setActionCommand("Start");
		
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				(new Thread(new Runnable(){
   				public void run(){
       			ptr.run(slider.getValue(), slider2.getValue(), slider3.getValue());
   				}
				})).start();

				frame.close();

      }
		});

		confirmPanel.add(button);

		sliderPanel.setBackground(pastelBlue);
		sliderPanel2.setBackground(pastelBlue);
		sliderHelp.setBackground(pastelBlue);
		sliderHelp2.setBackground(pastelBlue);
		sliderPanel3.setBackground(pastelBlue);
		sliderHelp3.setBackground(pastelBlue);
		confirmPanel.setBackground(pastelBlue);

		frame.display();

	}

	private void run(int x, int y, int delay) {

		g = new Grid(x, y, this); 

		g.setCellValue(0, 0, 2);
		g.setCellValue(x-1, y-1, 3); 
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int windowDimension = (int) ((float) screenSize.getHeight() * 0.8);

		int longestDimension = (g.getWidth() > g.getHeight()) ? g.getWidth() : g.getHeight();
		int cellWidth = windowDimension / (longestDimension + 1);
		int cellHeight = cellWidth;

		gui = new GridGUI(g, delay);

		MenuButton[] buttons = new MenuButton[3];
		buttons[0] = new MenuButton("A*", this, "A*", g);
		buttons[1] = new MenuButton("Dijkstra", this, "Dijkstra", g);
		buttons[2] = new MenuButton("Clear", this, "Clear", g);

		gui.createGUI(cellWidth, cellHeight, "Pathfinding", buttons);

	}

	public void instantUpdate() {
		gui.instantUpdate();
	}

	public void updateDisplay() {
		gui.updateDisplay();
	}

	
	public void processInput(Content cont) {
		if(cont instanceof Cell) {
			Cell c = (Cell) cont;
			Grid g = c.getGrid();
			if(!g.getFinalised()) {
				int value = c.getValue();
				int valueToMove = g.getValueToMove();
				boolean lock = g.getLock();
				switch(value) {
					case 0:
						if(lock) {
							c.setValue(valueToMove);
							g.toggleLock();
							instantUpdate();
						} else {
							c.setValue(1);
							instantUpdate();
						}
						break;
					case 1:
						if(lock) {
							c.setValue(valueToMove);
							g.toggleLock();
							instantUpdate();
						} else {
							c.setValue(0);
							instantUpdate();
						}
						break;
					case 2:
						// Move start square
						if(!lock) {
							g.toggleLock(); // Prevent algorithm from running
							g.setValueToMove(2); // Make next click set new start square
							c.setValue(0);
							instantUpdate();
						}
						break;
					case 3:
						if(!lock) {
							g.toggleLock();
							g.setValueToMove(3);
							c.setValue(0);
							instantUpdate();
						}
						break;
					default:
						return;
				}
			}
		} else if(cont instanceof MenuButton) {
			MenuButton b = (MenuButton) cont;
			Grid g = b.getGrid();
			if(!(g.getLock() || g.getFinalised())) {
				String action = b.getAction();
				switch(action) {
					case "A*":
						runAStar();
						break;
					case "Dijkstra":
						runDijkstra();
						break;
					case "Clear":
						g.removeMarkings();
						gui.instantUpdate();
						break;
				}
			}
		}
	}

	
	public void runAStar() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				System.out.println("Running A*");
				AlgorithmStats stats = new AlgorithmStats("A* Search");
				Algorithms.AStar(g, gui, stats);
				System.out.println(stats.prettyPrint());
			}
		});
		t.start();
	}

	
	public void runDijkstra() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				System.out.println("Running Dijkstra");
				AlgorithmStats stats = new AlgorithmStats("Dijkstra's Algorithm");
				Algorithms.Dijkstra(g, gui, stats);
				System.out.println(stats.prettyPrint());
			}
		});
		t.start();
	}
}


class Node {

	private int key;
	private KeyValue value;
	private int numChildren;
	private int degree;
	private LinkedList<Node> children;
	private boolean marked;
	private Node parent;

	public Node(int key, KeyValue value) {
		this.key = key;
		this.value = value;
		this.value.setNode(this);
		numChildren = 0;
		degree = 1;
		children = new LinkedList<Node>();
		marked = false;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int k) {
		key = k;
	}

	public KeyValue getValue() {
		return value;
	}

	public void setValue(KeyValue o) {
		value = o;
	}

	public int getDegree() {
		return degree;
	}

	public int getNumChildren() {
		return numChildren;
	}

	public LinkedList<Node> getChildren() {
		return children;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node n) {
		parent = n;
	}

	public void addChild(Node n) {
		children.add(n);
		n.parent = this;
		int nDegree = n.getDegree();
		numChildren++;
		degree+= nDegree;
		// Update parent degrees
		Node ptr = parent;
		while(ptr != null) {
			ptr.degree += nDegree;
			ptr = ptr.parent;
		}
	}

	public void addChild(int k, KeyValue v) {
		Node n = new Node(k, v);
		addChild(n);
	}

	public void removeChild(Node n) {
		boolean removed = children.remove(n);
		numChildren = (removed) ? (numChildren - 1) : numChildren;
		if(removed) {
			numChildren--;
			degree--;
			Node ptr = parent;
			while(ptr != null) {
				ptr.degree--;
				ptr = ptr.parent;
			}
		}
		n.setParent(null);
	}

	public boolean getMarked() {
		return marked;
	}

	public void setMarked(boolean b) {
		marked = b;
	}

	public String toString(int depth, int childNum) {
		String s;
		if(childNum > 0) {
			s = "\t("+Integer.toString(key)+", "+value.toString()+")";
		} else {
			s = " ("+Integer.toString(key)+", "+value.toString()+")";
		}
		StringBuilder b = new StringBuilder();
		b.append(s);
		int i=0;
		for(Node n : children) {
			b.append(n.toString(depth+1, i));
			b.append("\n");
			i++;
		}
		return b.toString();
	}

	@Override
	public String toString() {
		String s = " ("+Integer.toString(key)+", "+value.toString()+")";
		StringBuilder b = new StringBuilder();
		b.append(s);
		int i=0;
		for(Node n : children) {
			b.append(n.toString(1, i));
			b.append("\n");
			i++;
		}
		return b.toString();
	}
}

class FibonacciHeap {

	Node minRoot; 
	LinkedList<Node> roots; 

	public FibonacciHeap() {
		roots = new LinkedList<Node>();
	}

	@Override
	public String toString() {
		String[] strings = new String[roots.size()];
		int i=0;
		for(Node n : roots) {
			strings[i] = n.toString();
			i++;
		}
		return String.join("\n", strings);
	}

	public LinkedList<Node> getRoots() {
		return roots;
	}

	public boolean isEmpty() {
		return (minRoot == null);
	}

	public Node getMinRoot() {
		return minRoot;
	}

	public int findMinumumKey() {
		return (minRoot == null ) ? null : minRoot.getKey();
	}

	public KeyValue findMinumumValue() {
		return (minRoot == null ) ? null : minRoot.getValue();
	}

	public void add(KeyValue v) {
		int k = v.getKey();
		Node n = new Node(k, v);
		if(roots.size() == 0) {
			roots.add(n);
			minRoot = n;
		} else {
			roots.add(n);
			minRoot = (k < minRoot.getKey()) ? n : minRoot;
		}
	}

	public void join(FibonacciHeap f) {
		roots.addAll(f.getRoots());
		minRoot = (minRoot.getKey() < f.minRoot.getKey()) ? minRoot : f.getMinRoot();
	}

	public void decreaseKey(KeyValue v, int k) {

		Node n = v.getNode();
		assert(k < n.getKey());
		n.setKey(k);
		n.getValue().setKey(k);

		n.setMarked(false);

		Node parent = n.getParent();

		if(parent == null) {
			minRoot = (minRoot.getKey() < n.getKey()) ? minRoot : n;
			return;
		}
		if(n.getKey() >= parent.getKey()) {
			return;
		}

		parent.removeChild(n);
		roots.add(n);
		minRoot = (minRoot.getKey() < n.getKey()) ? minRoot : n;

		do {
			n = parent;
			parent = n.getParent();
			if(!n.getMarked()) {
				if(parent != null) {
					n.setMarked(true);
				}
				return;
			} else {
				n.setMarked(false);
			}
			parent.removeChild(n);
			roots.add(n);
			minRoot = (minRoot.getKey() < n.getKey()) ? minRoot : n;

		} while(true);
	}

	private int findSameDegree(Node[] array, Node node) {
		int i = 0;
		for(Node n : array) {
			if(n == null) {
				return -1;
			}
			if((n.getDegree() == node.getDegree()) && (n != node)) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private void updateMinRoot() {
		minRoot = roots.getFirst();
		for(Node r : roots) {
			if(r.getKey() < minRoot.getKey()) {
				minRoot = r;
			}
		}
	}

	private void outputPtrList(Node[] list) {
		System.out.println("\nPTR LIST: ");
		for(Node n : list) {
			if(n != null) {
				System.out.println(Integer.toString(n.getKey())+": "+Integer.toString(n.getDegree()));
			} else {
				System.out.println("NULL");
			}
		}
		System.out.println();
	}

	private void outputDegrees(LinkedList<Node> l) {
		System.out.println("\nALL DEGREES: ");
		for(Node n : l) {
			System.err.println(Integer.toString(n.getKey())+", "+Integer.toString(n.getDegree()));
		}
	}

	private boolean contains(Node[] l, Node n) {
		for(Node n2 : l) {
			if(n2 == n) {
				return true;
			}
		}
		return false;
	}

	private int countRoot() {
		int c = 0;
		for(Node n : roots) {
			if(n == minRoot) {
				c++;
			}
		}
		return c;
	}

	private void outputRoots() {
		for(Node n : roots) {
			System.out.println(n.getValue().toString()+": "+n.getKey());
		}
		System.out.println("Min: "+minRoot.getValue().toString()+": "+minRoot.getKey());
		System.out.println();
	}

	public KeyValue extractMinimum() {
		if(minRoot == null) {
			System.err.println("ERROR: TREE IS EMPTY");
			return null;
		}
		KeyValue returnVal = minRoot.getValue();

		roots.remove(minRoot);
		for(Node c : minRoot.getChildren()) {
			roots.add(c);
		}

		int listSize = roots.size();

		if(listSize == 0) {
			minRoot = null;
			return returnVal;
		}

		Node[] ptrList = new Node[listSize];

		int sameDegreeIndex;
		Node withSameDegree;
		int i = 0;
		int nIndex, otherIndex;
		boolean change;
		boolean breakV;
		LinkedList<Node> removalList = new LinkedList<Node>();

		do {
			change = false;
			nIndex = 0;
			for(Node n : roots) {
				if(!contains(ptrList, n)) {
					breakV = false;
					sameDegreeIndex = findSameDegree(ptrList, n);

					while(sameDegreeIndex != -1) {
						withSameDegree = ptrList[sameDegreeIndex];
						change = true;
						breakV = true;

						minRoot = (withSameDegree.getKey() < n.getKey()) ? withSameDegree : n;
						withSameDegree = (minRoot == n) ? withSameDegree : n;

						removalList.add(withSameDegree);
						ptrList[sameDegreeIndex] = null;
						i--;
						i = (i < 0) ? (i + listSize) : i;
						minRoot.addChild(withSameDegree);

						

						sameDegreeIndex = findSameDegree(ptrList, n);
					}
					if(breakV) {
						ptrList[i] = minRoot;
						break;
					} else {
						while(ptrList[i] != null) {
							i = (i + 1) % listSize;
						}
						ptrList[i++] = n;
					}
					

				}
			}
			for(Node r : removalList) {
				roots.remove(r);
			}
		} while(change);

		updateMinRoot();

		return returnVal;
	}


}
class DijkstraContents implements KeyValue {
	private int key;
	private int xcoord;
	private int ycoord;
	private KeyValue prev;
	private Node node;

	public DijkstraContents(int key, KeyValue prev, int x, int y) {
		this.key = key;
		this.prev = prev;
		xcoord = x;
		ycoord = y;
	}

	public int getX() {
		return xcoord;
	}

	public int getY() {
		return ycoord;
	}

	public KeyValue getPrev() {
		return prev;
	}

	public void setPrev(KeyValue n) {
		prev = n;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int k) {
		key = k;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node n) {
		node = n;
	}

	public String toString() {
		return new String(Integer.toString(xcoord)+", "+Integer.toString(ycoord));
	}

	public boolean coordEquals(DijkstraContents d) {
		return (xcoord == d.xcoord) && (ycoord == d.ycoord);
	}

}

class AlgorithmStats {
	public String name;
	public int iterations;
	public boolean success;
	public int pathLength;
	public long startTime;
	public long endTime;
	public long runTime;

	public AlgorithmStats(String n) {
		name = n;
		runTime = 0;
	}

	public void startClock() {
		startTime = System.currentTimeMillis();
	}

	public void stopClock() {
		runTime += (System.currentTimeMillis() - startTime);
	}

	public String prettyPrint() {
		StringBuilder b = new StringBuilder();
		b.append("Statistics for\t"+name);
		b.append("\nFound path:\t"+Boolean.toString(success));
		b.append("\nPath length:\t"+Integer.toString(pathLength));
		b.append("\nIterations:\t"+Integer.toString(iterations));
		b.append("\nRuntime:\t"+Utilities.displayTime(runTime));
		return b.toString();
	}

}

class Algorithms {

	private static int heuristic(int x1, int y1, int x2, int y2) {
		return (Math.abs(x2 - x1) + Math.abs(y2 - y1));
	}

	private static int[] findX(int[][] matrix, int width, int height, int x) {
		int[] coords = new int[2];
		for(int j=0; j<width; j++) {
			for(int i=0; i<height; i++) {
				if(matrix[j][i] == x) {
					coords[0] = j;
					coords[1] = i;
					return coords;
				}
			}
		}
		return null;
	}

	private static int[][] initMatrix(int x, int y, int v) {
		int[][] m = new int[x][y];
		for(int j=0; j<x; j++) {
			for(int i=0; i<y; i++) {
				m[j][i] = v;
			}
		}
		return m;
	}

	private static int[] findMinumum(int[][] m, ArrayList<int[]> l, int upperBound) {
		int val;
		int minVal = upperBound;
		int[] minCoord = null;
		for(int[] c : l) {
			val = m[c[0]][c[1]];
			if(val < minVal) {
				minVal = val;
				minCoord = c; 
			}
		}
		return minCoord;
	}

	private static void generateNeighbours(ArrayList<int[]> n, int[] c, int[][] m, int x, int y) {
		n.clear();
		int cx = c[0];
		int cy = c[1];
		if((cx > 0) && (m[cx - 1][cy] != 1)) {
			n.add(new int[] {cx - 1, cy}); 
		}
		if((cx < (x - 1)) && (m[cx + 1][cy] != 1)) {
			n.add(new int[] {cx + 1, cy});
		}
		if((cy > 0) && (m[cx][cy - 1] != 1)) {
			n.add(new int[] {cx, cy - 1});
		}
		if((cy < (y - 1)) && (m[cx][cy + 1] != 1)) {
			n.add(new int[] {cx, cy + 1});
		}
	}

	private static int packCoords(int xcoord, int ycoord, int xdim, int ydim) {
		return (ycoord * xdim) + xcoord;
	}

	
	private static int[] unPackCoord(int c, int xdim, int ydim) {
		int[] coords = new int[2];
		coords[0] = c % xdim;
		coords[1] = c / xdim;
		return coords;
	}

	private static boolean coordEquals(int[] a, int[] b) {
		return ((a[0] == b[0]) && (a[1] == b[1]));
	}

	private static boolean listContains(ArrayList<int[]> l, int[] c) {
		for(int[] e : l) {
			if((e[0] == c[0]) && (e[1] == c[1])) {
				return true;
			}
		}
		return false;
	}

	
	private static ArrayList<int[]> reconstructPath(int[][] cameFrom, int[] start, int[] current, int x, int y) {
		ArrayList<int[]> path = new ArrayList<int[]>();
		int[] ptr = current;

		while(!coordEquals(ptr, start)) {
			path.add(ptr);
			ptr = unPackCoord(cameFrom[ptr[0]][ptr[1]], x, y);
		}

		return path;
	}

	public static int AStar(Grid g, GridGUI gui, AlgorithmStats stats) {

		g.finaliseGrid();

		stats.startClock();

		int[][] matrix = g.asMatrix();
		int x = g.getWidth();
		int y = g.getHeight();
		int maxValue = Integer.MAX_VALUE;


		int[] start = findX(matrix, x, y, 2);
		int[] end = findX(matrix, x, y, 3);
		int[] current = null;
		int tentativeScore;
		int iterations = 0;

		ArrayList<int[]> openSet = new ArrayList<int[]>();
		openSet.add(start);
		ArrayList <int[]> neighbours = new ArrayList<int[]>();

	
		int[][] cameFrom = new int[x][y];

		
		int[][] knownCosts = new int[x][y];

		
		int[][] heuristicCosts = initMatrix(x, y, maxValue);

		heuristicCosts[start[0]][start[1]] = heuristic(start[0], start[1], end[0], end[1]);


		stats.stopClock();
		gui.instantUpdate();
		stats.startClock();

		while(!(openSet.size() == 0)) {
			stats.stopClock();
			if(current != null) {
				g.markUnselected(current[0], current[1]);
				g.markVisited(current[0], current[1]);
			}
			stats.startClock();
			
			current = findMinumum(heuristicCosts, openSet, maxValue);

			stats.stopClock();
			g.markUnopen(current[0], current[1]);
			g.markSelected(current[0], current[1]);
			gui.updateDisplay();
			stats.startClock();

			
			if((current[0] == end[0]) && (current[1] == end[1])) {
				stats.stopClock();
				stats.success = true;
				stats.iterations = iterations;

				ArrayList<int[]> path = reconstructPath(cameFrom, start, current, x, y);
				stats.pathLength = path.size();
				for(int[] step : path) {
					g.markSolution(step[0], step[1]);
				}
				gui.instantUpdate();
				g.setFinalise(false);

				return path.size();
				
			}

			openSet.remove(current);
			

			generateNeighbours(neighbours, current, matrix, x, y);

			for(int[] n : neighbours) {

				tentativeScore = heuristicCosts[current[0]][current[1]] + 1; 
				if(tentativeScore < heuristicCosts[n[0]][n[1]]) {
					cameFrom[n[0]][n[1]] = packCoords(current[0], current[1], x, y);
					heuristicCosts[n[0]][n[1]] = tentativeScore;
					knownCosts[n[0]][n[1]] = tentativeScore + heuristic(n[0], n[1], end[0], end[1]);

					
					if(!(listContains(openSet, n))) {
						openSet.add(n);
						stats.stopClock();
						g.markOpen(n[0], n[1]);
						stats.startClock();
					}

				}

			}
			iterations++;
		}
		stats.stopClock();
		stats.success = false;
		stats.pathLength = -1;
		stats.iterations = iterations;

		g.setFinalise(false);

		return -1;
	}

	

	private static void generateDijkstraNeighbours(ArrayList<DijkstraContents> n, DijkstraContents c, int[][] m, DijkstraContents[][] contentList, int x, int y) {
		n.clear();
		int cx = c.getX();
		int cy = c.getY();
		if((cx > 0) && (m[cx - 1][cy] != 1)) {
			n.add(contentList[cx - 1][cy]);
		}
		if((cx < (x - 1)) && (m[cx + 1][cy] != 1)) {
			n.add(contentList[cx + 1][cy]);
		}
		if((cy > 0) && (m[cx][cy - 1] != 1)) {
			n.add(contentList[cx][cy - 1]);
		}
		if((cy < (y - 1)) && (m[cx][cy + 1] != 1)) {
			n.add(contentList[cx][cy + 1]);
		}
	}

	private static ArrayList<DijkstraContents> reconstructDijkstraPath(DijkstraContents start, DijkstraContents current) {
		ArrayList<DijkstraContents> path = new ArrayList<DijkstraContents>();

		while(!current.coordEquals(start)) {
			path.add(current);
			current = (DijkstraContents) current.getPrev();
		}

		return path;
	}

	public static int Dijkstra(Grid g, GridGUI gui, AlgorithmStats stats) {
		g.finaliseGrid();

		stats.startClock();

		int[][] matrix = g.asMatrix();
		int x = g.getWidth();
		int y = g.getHeight();
		int maxValue = Integer.MAX_VALUE;


		int[] start = findX(matrix, x, y, 2);
		int[] end = findX(matrix, x, y, 3);
		int[] current = null;
		int iterations = 0;

		FibonacciHeap q = new FibonacciHeap();
		DijkstraContents c, startC, endC;
		startC = null;
		endC = null;
		DijkstraContents[][] contentList = new DijkstraContents[x][y];
		boolean[][] removedList = new boolean[x][y];
		for(int j=0; j<x; j++) {
			for(int i=0; i<y; i++) {
				removedList[j][i] = false;
			}
		}
		ArrayList<DijkstraContents> neighbours = new ArrayList<DijkstraContents>();
		

		for(int j=0; j<x; j++) {
			for(int i=0; i<y; i++) {

				if((j == start[0]) && (i == start[1])) {
					c = new DijkstraContents(0, null, j, i);
					contentList[j][i] = c;
					startC = c;
					q.add(c);
				} else if((j == end[0]) && (i == end[1])) {
					c = new DijkstraContents(maxValue, null, j, i);
					contentList[j][i] = c;
					endC = c;
					q.add(c);
				} else {
					c = new DijkstraContents(maxValue, null, j, i);
					contentList[j][i] = c;
					q.add(c);
				}
			}
		}

		assert((startC != null) && (endC != null));

		int alt;
		c = null;

		while(!q.isEmpty()) {

			stats.stopClock();
			if(c != null) {
				g.markUnselected(c.getX(), c.getY());
				g.markVisited(c.getX(), c.getY());
			}
			stats.startClock();

			c = (DijkstraContents) q.extractMinimum();
			removedList[c.getX()][c.getY()] = true;

			stats.stopClock();
			g.markSelected(c.getX(), c.getY());
			gui.updateDisplay();
			stats.startClock();

			if(c == endC) {
				stats.stopClock();
				stats.success = true;
				stats.iterations = iterations;
				ArrayList<DijkstraContents> path = reconstructDijkstraPath(startC, c);
				stats.pathLength = path.size();
				for(DijkstraContents step : path) {
					g.markSolution(step.getX(), step.getY());
				}
				gui.instantUpdate();
				g.setFinalise(false);
				return c.getKey();
			}

			generateDijkstraNeighbours(neighbours, c, matrix, contentList, x, y);
			
			for(DijkstraContents d : neighbours) {
				if(!(removedList[d.getX()][d.getY()])) {
					alt = c.getKey() + 1;
					if(alt < d.getKey()) {
						q.decreaseKey(d, alt);
						d.setPrev(c);
					}
				}
			}
			iterations++;
		}

		stats.stopClock();
		stats.success = false;
		stats.iterations = iterations;
		stats.pathLength = -1;

		g.setFinalise(false);
		return -1;
	}

	

}


public class Robotics {

	private static void testGrid() {
		Grid g = new Grid(5,5);
		g.init(1);
		g.setCellValue(2,2,3);
		System.out.println(g.getCellValue(1,1));
		System.out.println(g.getCellValue(2,2));
		System.out.println(g.sumValues());
	}

	public static void testGUI() {
		Grid g = new Grid(5, 5);
		g.setCellValue(1,0,2);
		g.setCellValue(2,4,1);
		g.setCellValue(4,3,3);
		GridGUI gui = new GridGUI(g);
		gui.createGUI(100, 100, "Test");

	}

	public static void testDemo() {
		Demo a = new Demo();
		a.runMenu();
	}

	public static void main(String args[]) {
		testDemo();
	}
}
