import java.awt.EventQueue;

import javax.swing.JFrame;
import net.miginfocom.swing.MigLayout;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.JLayeredPane;

public class GUI {

	private JFrame frame;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUI window = new GUI();
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
	public GUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 536, 348);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[grow][][][grow][][][][][][]", "[][][][][][grow][][grow][][][][][][]"));
		
		JTree tree = new JTree();
		tree.setRootVisible(false);
		frame.getContentPane().add(tree, "cell 0 0 5 13,growy");
		
		JScrollPane scrollPane = new JScrollPane();
		frame.getContentPane().add(scrollPane, "cell 5 0 5 13,grow");
		
		JPopupMenu popup = new JPopupMenu();
		JMenuItem add = new JMenuItem("Add Song...");
		JMenuItem delete = new JMenuItem("Delete Song...");
		popup.add(add);
		popup.add(delete);
		
		table = new JTable();
		table.add(popup);
		scrollPane.setViewportView(table);
		
		JLayeredPane layeredPane = new JLayeredPane();
		scrollPane.setColumnHeaderView(layeredPane);
		
		JButton btnNewButton = new JButton("Play  ");
		frame.getContentPane().add(btnNewButton, "cell 4 13");
		
		JButton btnNewButton_1 = new JButton("Pause");
		frame.getContentPane().add(btnNewButton_1, "cell 5 13");
		
		JButton btnNewButton_2 = new JButton("Stop");
		frame.getContentPane().add(btnNewButton_2, "cell 6 13");
		
		JButton btnNewButton_3 = new JButton("Resume");
		frame.getContentPane().add(btnNewButton_3, "cell 7 13");
		
		JButton btnNewButton_4 = new JButton("FWD");
		frame.getContentPane().add(btnNewButton_4, "cell 8 13");
		
		JButton btnNewButton_5 = new JButton("Prev");
		frame.getContentPane().add(btnNewButton_5, "cell 9 13");
		
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		
		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);
		
		JMenuItem mntmNewMenuItem = new JMenuItem("Add Song...");
		mnNewMenu.add(mntmNewMenuItem);
		
		JMenuItem mntmNewMenuItem_1 = new JMenuItem("Delete Song...");
		mnNewMenu.add(mntmNewMenuItem_1);
		
		JMenuItem mntmNewMenuItem_2 = new JMenuItem("Quit App");
		mnNewMenu.add(mntmNewMenuItem_2);
	}

}
