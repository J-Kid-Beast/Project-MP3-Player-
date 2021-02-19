import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import net.miginfocom.swing.MigLayout;
 

public class MusicPlayerGUI {
	// container that will hold info about added songs
	private Database m_library;
	// controls music playing 
	private MusicPlayer m_player;
	// stores how the GUI will look
	private JFrame m_frame;
	private JTable m_table;
	private JScrollPane m_pane;
	private Vector<DefaultTableModel> m_models;
	// stores the tree playlist implementation
	private JTree m_tree;
	private DefaultTreeModel m_treeModel;
	private DefaultMutableTreeNode m_playlist;
	private DefaultMutableTreeNode m_visibleRoot;
	private JPopupMenu m_treePopup;
	// stores the popup and menu bar implementation
	private JPopupMenu m_popup;
	private JMenuBar m_menuBar;
	private JMenu m_playlists;
	// stores all button implementations
	private JButton m_play;
	private JButton m_pause;
	private JButton m_resume;
	private JButton m_stop;
	private JButton m_skipNext;
	private JButton m_skipPrevious;
	
	/**Used to locate the songID of a selected song in the table row.
	 * songID of any song is always in the 5th column.*/
	private final int songIDColumn = 5;
	/**Used to locate the playlist name of a table model.
	 * playlist name of all table models is always in the 6th column.*/
	private final int playlistColumn = 6;
	
	
	public MusicPlayerGUI() throws IOException {
		 //XXXXXXXXXXXXXX Initializing library, musicPlayer, JFrame and setting JFrame layout XXXXXXXXXXXXXX
		
		 m_library = new Database();
		 m_player = new MusicPlayer();
		 m_frame = new JFrame("MP3 Player by Jerry and Aron :)");
		 m_frame.setBounds(100, 100, 591, 378);
		 m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		 m_frame.getContentPane().setLayout(new MigLayout("", "[grow][][][grow][][][][][][]", "[][][][][][grow][][grow][][][][][][]"));
		 
		 //XXXXXXXXXXXXXX Initializing and formatting table contents XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		 
		 m_models = new Vector<>();
		 // the first table model will be the main library table 
		 m_models.add(new DefaultTableModel(new Object[]{"Title","Artist","Album","Release Year","Genre", "songID", "Library"},0));
		 m_table = new JTable(m_models.elementAt(0));
		 // for every song in the library, grab their info and create a new row with their info
		 for (int i = 0; i < m_library.getLibrarySize(); i++) {
			 Object[] songInfo = m_library.getSong(i+1);
			 DefaultTableModel model = (DefaultTableModel) m_table.getModel();
             model.addRow(songInfo);
		 }// end for loop
		 // add table attributes
		 TableColumnModel tcm = m_table.getColumnModel();
	     tcm.removeColumn(tcm.getColumn(5));
	     tcm.removeColumn(tcm.getColumn(5));
		 m_table.setDropTarget(new MyDropTarget());
		 m_table.addMouseListener(new MouseAction());
		 m_table.addMouseListener(new PopupMouseAction());
		 m_pane = new JScrollPane();
		 m_pane.setDropTarget(new MyDropTarget());
		 m_pane.addMouseListener(new PopupMouseAction());
		 m_frame.getContentPane().add(m_pane, "cell 5 0 5 13,grow");
		 m_pane.setViewportView(m_table);
		 
		 //XXXXXXXXXXXXXX Creating and Implementing buttons XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		 
		 m_play = new JButton("Play");
		 m_play.addActionListener(new PlayAction());
		 m_pause = new JButton("Pause");
		 m_pause.addActionListener(new PauseAction());
		 m_resume = new JButton("Resume");
		 m_resume.addActionListener(new ResumeAction());
		 m_stop = new JButton("Stop");
		 m_stop.addActionListener(new StopAction());
		 m_skipNext = new JButton("FWD");
		 m_skipNext.addActionListener(new SkipForwardAction());
		 m_skipPrevious = new JButton("Prev");
		 m_skipPrevious.addActionListener(new SkipPreviousAction());
		 // adding buttons to JFrame 
		 m_frame.getContentPane().add(m_play, "cell 4 13");
		 m_frame.getContentPane().add(m_pause, "cell 5 13");
		 m_frame.getContentPane().add(m_stop, "cell 6 13");
		 m_frame.getContentPane().add(m_resume, "cell 7 13");
		 m_frame.getContentPane().add(m_skipNext, "cell 8 13");
		 m_frame.getContentPane().add(m_skipPrevious, "cell 9 13");
		 
		 //XXXXXXXXXXXXXX Creating and Implementing Library Popup Menu XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		 
		 m_popup = new JPopupMenu();
		 // creating add option
		 JMenuItem addMenu = new JMenuItem("Add Song to Library");
		 addMenu.setMnemonic(KeyEvent.VK_P);
		 addMenu.getAccessibleContext().setAccessibleDescription("Add a new Song");
		 addMenu.addActionListener(new AddAction());
		 m_popup.add(addMenu);
		 
		 // creating delete option
		 JMenuItem removeMenu = new JMenuItem("Delete Selected Song");
		 removeMenu.setMnemonic(KeyEvent.VK_P);
		 removeMenu.getAccessibleContext().setAccessibleDescription("Remove Selected Song");
		 removeMenu.addActionListener(new DeleteAction());
		 m_popup.add(removeMenu);
		 
		 // create Jmenu to display all playlists
		 m_playlists = new JMenu("Add to Playlist");
		 m_popup.add(m_playlists);
		 
		//XXXXXXXXXXXXXX Creating and Implementing Menu Bar XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		 
		 m_menuBar = new JMenuBar();
		 // create menu
		 JMenu menu = new JMenu("File");
	     menu.setMnemonic(KeyEvent.VK_F);
	     menu.getAccessibleContext().setAccessibleDescription("File Operations");
	     
	     // creating add song option
	     JMenuItem add = new JMenuItem("Add Song to Library");
	     add.setMnemonic(KeyEvent.VK_F);
	     add.addActionListener(new AddAction());
	     menu.add(add);
	     
	     // creating delete song option
	     JMenuItem remove = new JMenuItem("Remove Selected Song");
	     remove.setMnemonic(KeyEvent.VK_F);
	     remove.addActionListener(new DeleteAction());
	     menu.add(remove);
	     
	     // create quit application option
	     JMenuItem quit = new JMenuItem("Quit Application");
	     quit.setMnemonic(KeyEvent.VK_F);
	     quit.addActionListener(new QuitAction());
	     menu.add(quit);
	     
	     //create add playlist option
	     JMenuItem addPlaylist = new JMenuItem("Add Playlist...");
	     addPlaylist.setMnemonic(KeyEvent.VK_F);
	     addPlaylist.addActionListener(new AddPlaylistAction());
	     menu.add(addPlaylist);
	     
		 // add menu to menuBar. add menuBar to JFrame
	     m_menuBar.add(menu);
	     m_frame.setJMenuBar(m_menuBar);
		 
		//XXXXXXXXXXXXXX Creating and Implementing Tree Playlist XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		 
		 // invisible root is the root that will be hidden
		 DefaultMutableTreeNode invisibleRoot = new DefaultMutableTreeNode("invisibleRoot");
		 // visible root will be a node but act as the root of the tree
		 m_visibleRoot = new DefaultMutableTreeNode((String) m_table.getModel().getColumnName(6));
		 // playlist node will hold all created playlists   
		 m_playlist = new DefaultMutableTreeNode("Playlist");
		 
		 // configure tree attributes
		 m_treeModel = new DefaultTreeModel(invisibleRoot);
		 m_tree = new JTree(m_treeModel);
		 m_tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		 m_tree.addTreeSelectionListener(new DisplayPlaylistTable());
		 
		 // insert 'Root' and 'Playlist' nodes into tree
		 m_treeModel.insertNodeInto(m_visibleRoot, invisibleRoot, invisibleRoot.getChildCount());
		 m_tree.expandPath(new TreePath(m_treeModel.getPathToRoot(m_visibleRoot.getParent())));
		 
		 m_treeModel.insertNodeInto(m_playlist, invisibleRoot, invisibleRoot.getChildCount());
		 m_tree.expandPath(new TreePath(m_treeModel.getPathToRoot(m_playlist.getParent())));
		 // insert tree into frame
		 m_tree.setRootVisible(false);
		 m_frame.getContentPane().add(m_tree, "cell 0 0 5 13,grow");
		 m_tree.setVisible(true);
		 // enable tree popup menu
		 m_tree.addMouseListener(new PopupPlaylistMouseAction());
		 
        //XXXXXXXXXXXXXX Creating and Implementing Tree Playlist Popup XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
		 
		 m_treePopup = new JPopupMenu();
		// creating new window option
		JMenuItem window = new JMenuItem("Open in new Window");
		window.setMnemonic(KeyEvent.VK_P);
		window.getAccessibleContext().setAccessibleDescription("Places selected Playlist in a new window.");
		window.addActionListener(new GenerateWindowAction());
		m_treePopup.add(window);
				 
		// creating delete playlist option
		JMenuItem deletePlaylist = new JMenuItem("Delete Selected Playlist");
		deletePlaylist.setMnemonic(KeyEvent.VK_P);
		deletePlaylist.getAccessibleContext().setAccessibleDescription("Deletes selected playlist from the tree.");
		deletePlaylist.addActionListener(new DeletePlaylistAction());
		m_treePopup.add(deletePlaylist);
		 
		 // set frame attributes
		 m_frame.setSize(1000, 1000);
		 m_frame.pack();
		 m_frame.setVisible(true);
		 
	}// end default constructor
	
	/** Allows the JTable or JScrollPanel to accept mp3 files dropped into it by the cursor.
	 *  Creates a song object from the mp3 file, stores it into the library, and displays its info on the table.*/
	private class MyDropTarget extends DropTarget {
		private static final long serialVersionUID = 1L; // ignore this thing it gets rid of a warning
		
		public  void drop(DropTargetDropEvent evt) {
	            try {
	                evt.acceptDrop(DnDConstants.ACTION_COPY);
	                List result;
	                result = (List) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
	                // for loop will handle if 2 or more files are dropped at the same time
	                for (Object it : result) {
	                	String path = it.toString().replace('\\', '/'); // replace escape char for string path
	                    Song song = new Song(path);
	                    // determine action by determining which playlist table songs are dropped into
	                    if (m_table.getModel() == m_models.get(0))
	                    	dropToLibrary(song);
	                    else
	                    	dropToPlaylist(song);
	                }// end for loop
	             }// end try
	            catch (Exception ex){
	                ex.printStackTrace();
	            }// end catch
	    }// end drop()
		
		/**Drops the song into the main library and adds the song to the database.
		 * If the song is already present in the database, an error message is shown to the user
		 * and the method does nothing.
		 * @param song The song object being dropped to the main library.
		 */
		private void dropToLibrary(Song song) {
			 if (m_library.add(song)) {
             	// get the unique songID assigned to the song from the database
             	int songID = m_library.getSongID(song.getFilePath());
             	// push new song info to the main library if song was added to library
             	DefaultTableModel model = m_models.get(0);
             	model.addRow(new Object[]{song.getTitle(), song.getArtist(), song.getAlbum(), 
             			                  song.getYear(), song.getGenre(), songID});
             }// end if 
             else 
             	JOptionPane.showMessageDialog(null, "Song: " + song.getTitle() + " already exists in the main Library!");
		}// end dropToLibrary()
		
		/**This method will check if the song being dropped into a playlist is present in the main library first.
		 * If the song is not in the library, it is added their first, then added to the playlist. If the song is 
		 * present in both, then an error message will be displayed to the user and the method does nothing.
		 * @param song The song object that will be dropped into the playlist.
		 */
		private void dropToPlaylist(Song song) {
			dropToLibrary(song);
			DefaultTableModel model = (DefaultTableModel) m_table.getModel();
			for (int i = 0; i < model.getRowCount(); i++) {
				// check if a songID in the playlist matches a songID in the database
				if (m_library.getSongID(song.getFilePath()) == (int) model.getValueAt(i, songIDColumn)) {
					JOptionPane.showMessageDialog(null, "Song: " + song.getTitle() + " already exists in the playlist!");
					return;
				}// end if
			}// end for loop
			// get the unique songID assigned to the song from the database
         	int songID = m_library.getSongID(song.getFilePath());
         	// add song to the database since song is not in the playlist
			model.addRow(new Object[]{song.getTitle(), song.getArtist(), song.getAlbum(), 
	                  song.getYear(), song.getGenre(), songID});
		}// end dropToPlaylist()
		
	}// end myDropTarget class
	
	/**Enables the play button to play the current song with the MusicPlayer class.*/
	private class PlayAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if (m_library.getLibrarySize() == 0) {
				JOptionPane.showMessageDialog(null, "Library is empty!");
			    return;
			}// end if 
			// playlist table is empty
			if (m_table.getRowCount() == 0) {
				JOptionPane.showMessageDialog(null, "Playlist is empty!");
			    return;
			}// end if
			int row = m_table.getSelectedRow(); // get the row the user selected to play  
			if (row == -1) { row = 0; } // if no row was selected, play 1st song in table
			int currentSongID = (int) m_table.getModel().getValueAt(row, songIDColumn);
			m_player.play(m_library.getFilePath(currentSongID));
			m_table.addRowSelectionInterval(row, row);
			//m_player.setCurrentSong(1);
		}// end actionPerformed()
	}// end PlayAction class
	
	/**Enables the pause button to pause the current song with the MusicPlayer class.*/
	private class PauseAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			m_player.pause();
		}// end actionPerformed()
	}// end PauseAction class
	
	/**Enables the resume button to resume the current song with the MusicPlayer class.*/
	private class ResumeAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			m_player.resume();
		}// end actionPerformed()
	}// end ResumeAction class
	
	/**Enables the stop button to stop the current song with the MusicPlayer class.*/
	private class StopAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			m_player.stop();
		}// end actionPerformed()
	}// end StopAction class
	
	/**Enables the skip forward button to skip to the next song in the library.*/
	private class SkipForwardAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			m_player.stop();
			if (m_library.getLibrarySize() == 0) {
				JOptionPane.showMessageDialog(null, "Library is empty!");
			    return;
			}// end if 
			// playlist table is empty
			if (m_table.getRowCount() == 0) {
				JOptionPane.showMessageDialog(null, "Playlist is empty!");
			    return;
			}// end if
			int row = m_table.getSelectedRow(); // get the current selected song row  
			if (row == -1) { row = 0; } // if no row was selected, play 1st song in table
			m_table.removeRowSelectionInterval(row, row); // remove highlight from current song row 
			// if current song is at the end of table, start at the 1st song
			if (row == m_table.getRowCount()-1)  
				row = 0; 
			else // shift row down to the next song row 
				row++; 
			
			int currentSongID = (int) m_table.getModel().getValueAt(row, songIDColumn);
			m_player.play(m_library.getFilePath(currentSongID));
			m_table.addRowSelectionInterval(row, row); // highlight the new song's row being played
		}// end actionPerformed()
	}// end SkipForwardAction class
	
	/**Enables the skip previous button to skip to the previous song in the library.*/
	private class SkipPreviousAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			m_player.stop();
			if (m_library.getLibrarySize() == 0) {
				JOptionPane.showMessageDialog(null, "Library is empty!");
			    return;
			}// end if 
			// playlist table is empty
			if (m_table.getRowCount() == 0) {
				JOptionPane.showMessageDialog(null, "Playlist is empty!");
			    return;
			}// end if
			int row = m_table.getSelectedRow(); // get the current selected song row  
			if (row == -1) { row = 0; } // if no row was selected, play 1st song in table
			m_table.removeRowSelectionInterval(row, row); // remove highlight from current song row 
			// if current song is at the start of table, start at the end song
			if (row == 0)  
				row = m_table.getRowCount()-1; 
			else // shift row up to the previous song row 
				row--; 
			
			int currentSongID = (int) m_table.getModel().getValueAt(row, songIDColumn);
			m_player.play(m_library.getFilePath(currentSongID));
			m_table.addRowSelectionInterval(row, row); // highlight the new song's row being played
		}// end actionPerformed()
	}// end SkipPreviousAction class
	
	/**Allows the user to select which song to play from the table by clicking on its row on the table.*/
	private class MouseAction implements MouseListener{
		// user must left click on mouse to select song
		@Override
		public void mousePressed(MouseEvent e) {
			 if (SwingUtilities.isLeftMouseButton(e)) {
				 int row = m_table.getSelectedRow();
				 int songID = (int) m_table.getModel().getValueAt(row, songIDColumn);
				 String path = m_library.getFilePath(songID);
				 m_player.play(path);
			 }// end if 
		}// end mousePressed()
		
		// All other methods must be implemented but will not be used
		@Override
		public void mouseClicked(MouseEvent e){}
		@Override
		public void mouseReleased(MouseEvent e){}
		@Override
		public void mouseEntered(MouseEvent e){}
		@Override
		public void mouseExited(MouseEvent e){}
	}// end MouseAction class 
	
	/**Allows the user to make the library popup appear by right clicking on the mouse.*/
	private class PopupMouseAction implements MouseListener{
		// user must right click on mouse to show popup
		 @Override
         public void mousePressed(MouseEvent e) {
			 if (SwingUtilities.isRightMouseButton(e))
                 showPopup(e);
         }// end mousePressed()

         @Override
         public void mouseReleased(MouseEvent e) {
        	 if (SwingUtilities.isRightMouseButton(e))
                showPopup(e);
         }// end mouseReleased()

         private void showPopup(MouseEvent e) {
        	 if (SwingUtilities.isRightMouseButton(e))
                if (e.isPopupTrigger()) {
                   m_popup.show(e.getComponent(),e.getX(), e.getY());
                 }// end if 
	     }// end showPopup()
    
      // All other methods must be implemented but will not be used
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
	}// end popupMouseAction class 
	
	/**Enables the user to add a song to the library when clicking on the "Add Song" tab.*/
	private class AddAction implements ActionListener{
		 public void actionPerformed(ActionEvent e) {
			try {
	         	File file = null;
	         	JFileChooser choose = new JFileChooser();
	         	choose.setDialogTitle("Select an mp3 file.");
	         	if (choose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
	      	       file = choose.getSelectedFile();
	         	} // end if
	         	Song song = new Song(file.getAbsolutePath().replace('\\', '/')); // replace escape char for string path
	         	if (m_library.add(song)) {
	         	// get the unique songID assigned to the song in the database
	         	 int songID = m_library.getSongID(song.getFilePath());
	         	// push new song info to the JTable if song was added to the library
	             DefaultTableModel model = m_models.get(0);
	             model.addRow(new Object[]{song.getTitle(), song.getArtist(), song.getAlbum(), 
	            		                   song.getYear(), song.getGenre(), songID});
	         	}// end if 
	         	else 
	         		JOptionPane.showMessageDialog(null, "Song Already Exists in the library!");
				}// end try
			catch (NullPointerException ex) { // user selected no file when asked 
				 JOptionPane.showMessageDialog(null, "Error: No file selected!");
			}// end catch
         }// end actionPerformed()
	}// end addAction class
	
	/**Enables the user to delete the selected song from the library when clicking on the "Delete Song" tab.*/
	private class DeleteAction implements ActionListener{
		public void actionPerformed(ActionEvent e) {
        	int row = m_table.getSelectedRow();
        	DefaultTableModel model = (DefaultTableModel) m_table.getModel();
        	// check if user selected a song on the table
        	if (row == -1) { 
        		JOptionPane.showMessageDialog(null, "No song has been selected!");
        		return;
        	}// end if
        	int songID = (int) model.getValueAt(row, songIDColumn);
        	// determine which action to take depending if song is deleted from main library or playlist
			if (model == m_models.get(0))
				mainLibraryAction(model,row,songID);
			else
				removeSong(model, songID);
        }// end actionPerformed()
		
		/**Deletes the selected song from the main library, and then notifies each playlist
		 * to delete the same song from their libraries, if they have the song.
		 * @param main The DefaultTableModel for the main library.
		 */
		private void mainLibraryAction(DefaultTableModel main, int row, int songID) {
			// check if database is empty
        	if (m_library.getLibrarySize() == 0) { 
        		JOptionPane.showMessageDialog(null, "Library is empty!");
        	    return;
        	}// end if 
        	// confirm with user to delete selected song 
			int result = JOptionPane.showConfirmDialog 
		     (null, "Would you like to delete the selected song from the main library? "
		      + "This will remove the song from all other playlists.", "Warning",JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
	        	m_player.stop();
	        	if (m_library.remove(m_library.getFilePath(songID))) {
	        		main.removeRow(row);
	        		// pseudo observer pattern, alerts all playlist table models to delete selected song
	            	for (int i = 0; i < m_models.size(); i++)
	            		removeSong(m_models.get(i), songID);
	        	}// end if 
			}// end if 
		}// end mainLibraryAction()
		
		/**Removes the song from the chosen playlist's DefaultTableModel.
		 * Method does nothing if the song is not present in the table.
		 * @param model The chosen playlist's DefaultTableModel.
		 * @param songID The songId of the song to be deleted. 
		 */
		private void removeSong(DefaultTableModel model, int songID) {
			for (int i = 0; i < model.getRowCount(); i++)
				if (songID == (int) model.getValueAt(i, songIDColumn)) {
					m_player.stop();
					model.removeRow(i);
					return;
				}// end if 
		}// end removeSong()
		
	}// end deleteAction class
	
	/**Allows the user to quit the Application and close the window by clicking on the "Quit Application" tab in the popup menu.*/
	private class QuitAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			m_frame.dispatchEvent(new WindowEvent(m_frame, WindowEvent.WINDOW_CLOSING));
			System.exit(1);
			}// end actionPerformed()
	}// end QuitApplication class
	
	/**Allows the user to create new playlists and add them to the 'playlist' tree node.*/
	private class AddPlaylistAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			 // ask user for playlist's name using JOptionPane
			 String name = JOptionPane.showInputDialog("Enter the name of the Playlist.");
			 if (name.equals("Library") || name.equals("Playlist")) {
				 JOptionPane.showMessageDialog(null, "Cannot name a Playlist after 'Playlist' or 'Library'!");
				 return;
			 }// end if
			 
			 for (int i = 0; i < m_models.size(); i++)
				 if (m_models.get(i).getColumnName(playlistColumn).equals(name)) {
					 JOptionPane.showMessageDialog(null, "A playlist with the name: " + name + " already exists.");
			 		 return;
				 }// end if 
			 
			 // create new table model for the new playlist and configure it to display on the GUI.
			 DefaultTableModel newModel = new DefaultTableModel(new Object[]{"Title","Artist","Album","Release Year","Genre", "songID", name},0);
			 m_models.add(newModel);
			 m_table.setModel(newModel);
			 TableColumnModel tcm = m_table.getColumnModel();
		     tcm.removeColumn(tcm.getColumn(5));
		     tcm.removeColumn(tcm.getColumn(5));
		     // add a new tree node to the playlist folder for the new playlist  
			 DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(name);
			 m_treeModel.insertNodeInto(newChild, m_playlist , m_playlist.getChildCount());
			 m_tree.expandPath(new TreePath(m_treeModel.getPathToRoot(newChild.getParent())));
			 addToPopupList(name);
			 m_tree.setSelectionPath(new TreePath(m_treeModel.getPathToRoot(newChild)));
	    }// end actionPerformed()
		
		/**Adds a tab on the Jmenu m_playlists data member to allow the user to 
		 * add songs to the playlist through the popup menu in the main library.
		 * @param playlistName The name of the playlist.
		 */
		private void addToPopupList(String playlistName) {
			 JMenuItem newPlaylist = new JMenuItem(playlistName);
			 newPlaylist.setMnemonic(KeyEvent.VK_P);
			 newPlaylist.getAccessibleContext().setAccessibleDescription("Option to add song to " + playlistName);
			 newPlaylist.addActionListener(new AddActionPlaylist());
			 m_playlists.add(newPlaylist);
		}// end addToPopupList()
	}// end AddPlaylistAction class
	
	/**Allows the user to display a playlist on the main window by clicking on the playlist.*/
	private class DisplayPlaylistTable implements TreeSelectionListener{
		@Override
		public void valueChanged(TreeSelectionEvent e) {
			 DefaultMutableTreeNode node = (DefaultMutableTreeNode) m_tree.getLastSelectedPathComponent();
			 // nothing was selected, end method
			 if (node == null) return;
			 // load the table model the playlist belongs to to the GUI and display it
			 String playlistName = node.toString();
			 boolean stop = false;
			 try {
				 for (int i = 0; i < m_models.size() && !stop; i++) {
					 if (m_models.get(i).getColumnName(playlistColumn) == playlistName) {
						 m_table.setModel(m_models.get(i));
						 TableColumnModel columnModel = m_table.getColumnModel();
						 columnModel.removeColumn(columnModel.getColumn(5));
						 columnModel.removeColumn(columnModel.getColumn(5));
						 stop = true;
					 }// end if 
				 }// end for loop
			 }// end try
			 catch(ArrayIndexOutOfBoundsException ex) {
				 // this catch is only when the user selects a playlist twice 
				 // and the removeColumn() methods throw an exeption
			 }// end catch()
		}// end valueChanged()
	}// end DisplayPlaylistTable class
	
	/**Enables playlists to add songs to their own table models from the main library.*/
	private class AddActionPlaylist implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			int row = m_table.getSelectedRow();
			if (row == -1) {
				JOptionPane.showMessageDialog(null, "No song has been selected!");
				return;
			}// end if 
			int songID = (int) m_table.getModel().getValueAt(row, songIDColumn);
			// get the specific JMenuItem that represents the playlist the user selected  
			JMenuItem obj = (JMenuItem) e.getSource();
			DefaultTableModel model = findModel(obj);
			if (songPresent(model, songID)) // prevent duplicate songs
				return;
			else
				model.addRow(m_library.getSong(songID));
		}// end actionPerformed()
		
		/**Returns the DefaultTableModel that has the same playlist name as 
		 * the JMenuItem that represents the playlist in the add song sub menu.
		 * @param obj The JMenuItem that was selected on the GUI 'Add To Playlist' sub menu.
		 * @return The DefaultTableModel that belongs to the selected playlist.
		 */
		private DefaultTableModel findModel(JMenuItem obj) {
			DefaultTableModel target = null;
			String playlistName = obj.getText();
			boolean stop = false;
			for (int i = 0; i < m_models.size() && !stop; i++)
				if (m_models.get(i).getColumnName(playlistColumn).equals(playlistName)) {
					target = m_models.get(i);
					stop = true;
				}// end if 
			return target;
		}// end findModel()
		
		/**
		 * Determines if the song being added to a playlist is already present in the playlist.
		 * @param model The chosen playlist's DefaultTableModel.
		 * @param songID The songID of the song being added to the playlist.
		 * @return True/False depending if the song is present in the playlist.
		 */
		private boolean songPresent(DefaultTableModel model, int songID) {
			for (int i = 0; i < model.getRowCount(); i++) {
				if (songID == (int) model.getValueAt(i, songIDColumn)) {
					JOptionPane.showMessageDialog(null, "Song is already in the Playlist!");
					return true;
				}// end if 
			}// end for loop
			return false;
		}// end songPresent()
	}// end AddActionPlaylist class
	
	/**Allows the user to make the tree popup appear by right clicking on the mouse.*/
	private class PopupPlaylistMouseAction implements MouseListener{
		// user must right click on mouse to show popup on tree
		 @Override
         public void mousePressed(MouseEvent e) {
			 if (SwingUtilities.isRightMouseButton(e))
                 showPopup(e);
         }// end mousePressed()

         @Override
         public void mouseReleased(MouseEvent e) {
        	 if (SwingUtilities.isRightMouseButton(e))
                showPopup(e);
         }// end mouseReleased()

         private void showPopup(MouseEvent e) {
        	 if (SwingUtilities.isRightMouseButton(e))
                if (e.isPopupTrigger()) {
                   m_treePopup.show(e.getComponent(),e.getX(), e.getY());
                 }// end if 
	     }// end showPopup()
    
      // All other methods must be implemented but will not be used
		@Override
		public void mouseClicked(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {}
		@Override
		public void mouseExited(MouseEvent e) {}
		
	}// end popupMouseAction class 
	
	/**Allows the user to delete created playlists from the tree.*/
	private class DeletePlaylistAction implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			DefaultTreeModel model = (DefaultTreeModel)m_tree.getModel();
			MutableTreeNode selectedNode = (MutableTreeNode) m_tree.getLastSelectedPathComponent();
			if (selectedNode == null)
				JOptionPane.showMessageDialog(null, "No playlist has been selected to delete!");
			// prevent main library or playlist folder node from being deleted
			else if (selectedNode != m_playlist && selectedNode != m_visibleRoot) {
				 // confirm with user to delete selected playlist
				  int result = JOptionPane.showConfirmDialog 
			      (null, "Would you like to delete " +  selectedNode.toString() + 
			       "?", "Warning",JOptionPane.YES_NO_OPTION);
				  if (result == JOptionPane.YES_OPTION) {
					// remove the playlist from the tree and its corresponding table model from the vector of models
					boolean stop = false;
					for (int i = 0; i < m_models.size() && !stop; i++)
						if (m_models.get(i).getColumnName(playlistColumn) == selectedNode.toString()) {
							removeFromPopupList(selectedNode.toString());
							m_models.remove(i);
							model.removeNodeFromParent(selectedNode);
							m_table.setModel(m_models.get(0)); // display main library table as default
							TableColumnModel columnModel = m_table.getColumnModel();
							columnModel.removeColumn(columnModel.getColumn(5));
							columnModel.removeColumn(columnModel.getColumn(5));
							stop = true;
						}// end if 
				  }// end if 
			}// end else if 
			else 
				JOptionPane.showMessageDialog(null, "Cannot delete main Library or Playlist folder!");
		}// end actionPerformed()
		
		/**Removes the JmenuItem from m_playlists that corresponds to the playlist being deleted.
		 * @param playlistName The name of the playlist.
		 */
		private void removeFromPopupList(String playlistName) {
			for (int i = 0; i < m_playlists.getItemCount(); i++)
				if (m_playlists.getItem(i).getText().equals(playlistName)) {
					m_playlists.remove(i);
					return;
				}// end if 
		}// end removeFromPopupList()
	}// end DeletePlaylist class
	
	private class GenerateWindowAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			JOptionPane.showMessageDialog(null, "Implementation for window split does not exist!");
		}
	}
	
}// end MusicPlayerGUI class 






