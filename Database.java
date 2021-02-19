import java.io.*;
import java.sql.*;
import java.util.Scanner;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class Database{
	// allows access to the SQL server
	private String m_url;
	private String m_user;
	private String m_password;
	
	/**Allows connection to the SQL server*/
	private Connection m_connection;
	/**Used to execute SQL commands.  */
	private Statement m_statement;
	/**Prepares SQL statements to be executed*/
	private PreparedStatement m_prep;
	/**Array co*/
	private final String[] m_songTitles = {"filePath", "title", "artist", "album", "releaseYear", "genre", "songID"};
	
	/**Establishes a connection to the SQL server, sets up the database, and creates a table to store songs.*/
	Database() {
		m_url = "jdbc:mysql://localhost:3306/";
		m_user = "root";
		m_password = "";
		
		try {
			//XXXXXXXXXXXXX Establish connection to SQL server XXXXXXXXXXXXXXXXXXXXXXXX
			
			Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
			m_connection = DriverManager.getConnection(m_url,m_user,m_password);
			m_statement = m_connection.createStatement();
			
			//XXXXXXXXXXXXX Create and Select database XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
			
			m_statement.execute("CREATE DATABASE IF NOT EXISTS library");
			m_statement.execute("USE library");
			
			//XXXXXXXXXXXXX Create Songs Table XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX 
			
			m_statement.execute("DROP TABLE IF EXISTS songs");
			m_statement.execute("CREATE TABLE songs (" +
								"songID BIGINT NOT NULL AUTO_INCREMENT," + 
					            "filePath VARCHAR(200) UNIQUE NOT NULL," + 
								"title VARCHAR(150)," + 
					            "artist VARCHAR(100)," +
								"album VARCHAR(150)," +
								"releaseYear VARCHAR(4)," +
								"genre VARCHAR(20)," +
								"PRIMARY KEY (songID))");
			
			m_statement.close();
			populateDatabase();
		}// end try  
		catch (Exception e) {
			e.printStackTrace();
		}// end catch
	}// end default constructor
	
	/**Used to add songs to the database when the database is first initialized.
	 * If the file that the user chooses does not contain any valid mp3 filePaths
	 * or no file is chosen, then the database will be empty. */
	private void populateDatabase() {
		File file = null;
		Reader reader = null;
		Song s1 = null;
		try {
     	JFileChooser choose = new JFileChooser();
     	choose.setDialogTitle("Select a file with intial library song locations.");
     	if (choose.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
  	       file = choose.getSelectedFile();
     	} // end if
     	reader = new BufferedReader(new InputStreamReader(new FileInputStream (file)));
		Scanner scan = new Scanner(reader);
     	while (scan.hasNextLine()) {
     		s1 = new Song(scan.nextLine().replace('\\', '/')); // replace escape char for string path
     		add(s1);
     	}//end while
     	scan.close();
		}// end try
		catch (NullPointerException | IOException e){
			JOptionPane.showMessageDialog(null, "Error reading file! Library will be empty at initialization!");
			return;
		}// end catch
		// file contained no valid mp3 filePaths
		if (getLibrarySize() == 0)
			JOptionPane.showMessageDialog(null, "File is invalid! Library will be empty at initialization!");
	}// end populateDatabase()
	
	/**Determines if the filePath of a particular song is present in the database.
     * @param filePath The filePath to look for in the database.
     * @return True/False depending if song is in database.
     */
    public boolean songPresent(String filePath) {
    	// if song is present in database, location will be set to its rowcount
        int rowCount = 0;
        try {
            String statement = "SELECT count(*) AS rowcount FROM songs WHERE filePath=?";
            m_prep = m_connection.prepareStatement(statement);
            m_prep.setString(1, filePath);
            ResultSet resultSet = m_prep.executeQuery();
            resultSet.next();
            rowCount = resultSet.getInt("rowcount");
            m_prep.close();
            if(rowCount == 0) {
            	// filePath is not present, song isn't in database
                return false;
            }// end if 
        }// end try 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }// end catch
        return true;
    }// end songPresent()
    
    /**Determines if the song the songID belongs to is present in the database.
     * @param songID The unique int ID of a song.
     * @return True/False depending if song is in database.
     */
    public boolean songPresent(int songID) {
    	boolean present = false;
        try {
            String statement = "SELECT * FROM songs WHERE songID=?";
            m_prep = m_connection.prepareStatement(statement);
            m_prep.setInt(1, songID);
            ResultSet resultSet = m_prep.executeQuery();
            if(resultSet.next())
            	present = true;
        }// end try 
        catch (SQLException e) {
            e.printStackTrace();
        }// end catch
        return present;
    }// end songPresent()
	
    /**Allows songs to be added to the database.
	 * Doesn't allow songs that are already present to be added again.
	 * @param s1 A song object to be stored to the database.
	 * @return A bool that states whether the song was added or not.
	 */
    public boolean add(Song song) {
    	// make sure song isn't in database and song object contains a valid filePath 
        if(!songPresent(song.getFilePath()) && song.getFilePath() != null) {
            try {
                String query = "INSERT INTO songs (filePath, title, artist, album, releaseYear, genre)" +
                               " VALUES (?, ?, ?, ?, ?, ?)";
                m_prep = m_connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
                m_prep.setString(1, song.getFilePath());
                m_prep.setString(2, song.getTitle());
                m_prep.setString(3, song.getArtist());
                m_prep.setString(4, song.getAlbum());
                m_prep.setString(5, song.getYear());
                m_prep.setString(6, song.getGenre());
                m_prep.execute();
                m_prep.close();
                return true;
            }// end try 
            catch (SQLException e) {
                e.printStackTrace();
                return false;
            }// end catch
        }// end if 
        else // song is already in database 
        	return false;
    }// end add()
    
    /**
     * Deletes a given song from the database based on its filePath.
     * @param filePath the unique filePath of the song to delete.
     * @return True/False depending if the removal was successful.
     */
    public boolean remove(String filePath) {
    	int removed;
        try {
            String statement = "DELETE FROM songs WHERE filePath=?";
            m_prep = m_connection.prepareStatement(statement);
            m_prep.setString(1, filePath);
            removed = m_prep.executeUpdate();
            m_prep.close();
        }// end try 
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }//end catch
        // if executeUpdate() deleted the song it will return 1, else 0
        return (removed == 1) ? true : false;
    }// end remove()
    
//    /**Returns a song from the database based on their id.
//	 * @param choice An int corresponding to the id of the song.
//	 * @return A song obj based on the choice.
//	 */
//    public Song getSong(int choice) {
//    	choice++;
//    	Song s1 = null;
//    	try {
//    		// grab all rows from database
//    		String statement = "SELECT * FROM songs WHERE songID=?";
//			m_prep = m_connection.prepareStatement(statement);
//			m_prep.setInt(1,choice);
//			ResultSet set = m_prep.executeQuery();
//			// move set cursor to desired row
//		    set.next();
////		    while(choice > 0) {
////		    	set.next();
////		    	choice--;
////		    }//end while 
//		    // create new song object from filePath
//		    s1 = new Song(set.getString("filePath"),set.getInt("songID"));
//		    m_prep.close();
//		}// end try 
//    	catch (SQLException e) {
//    		// print exception trace and return null to stop program
//			e.printStackTrace();
//			return null;
//		}// end catch 
//    	return s1;
//    }// end getSong()
    
    
    /**Returns a song from the database based on their id.
	 * @param choice An int corresponding to the id of the song.
	 * @return A song obj based on the choice.
	 */
    public Object[] getSong(int choice) {
    	Object [] songInfo = new Object[m_songTitles.length-1];
    	try {
    		// 
    		String statement = "SELECT * FROM songs WHERE songID=?";
			m_prep = m_connection.prepareStatement(statement);
			m_prep.setInt(1,choice);
			ResultSet set = m_prep.executeQuery();
			// move set cursor to desired row
		    set.next();
		    for (int i = 1; i < m_songTitles.length-1; i++) // do not add last element (songID)
		    	songInfo[i-1] = set.getString(m_songTitles[i]); // arrays start from 0, but databases start from 1
		    songInfo[m_songTitles.length-2] = set.getInt("songID"); // set last element as an int
		    m_prep.close();
		}// end try 
    	catch (SQLException e) {
    		// print exception trace and return null to stop program
			e.printStackTrace();
			return null;
		}// end catch 
    	return songInfo;
    }// end getSong()
    
    /**Returns the filePath of the song identified by it's songID.
     * @param songID A int corresponding to one unique song in the database. 
     * @return A string containing the filePath of the desired song. 
     */
    public String getFilePath(int songID) {
    	String path = null;
    	try {
    		String statement = "SELECT * FROM songs WHERE songID=?";
    		m_prep = m_connection.prepareStatement(statement);
    		m_prep.setInt(1,songID);
    		ResultSet set = m_prep.executeQuery();
    		set.next();
    		path = set.getString("filePath");
    	}// end try
    	catch(SQLException e){
    		e.printStackTrace();
    	}// end catch
		return path;
    }// end getFilePath()
    
    /**
     * Returns the songID of a song using its unique filePath. 
     * @param filePath A string of the desired song's filePath.
     * @return A unique songID corresponding to a song in the database.
     */
    public int getSongID(String filePath) {
    	int songID = 0;
    	try {
    		String statement = "SELECT * FROM songs WHERE filePath=?";
    		m_prep = m_connection.prepareStatement(statement);
    		m_prep.setString(1,filePath);
    		ResultSet set = m_prep.executeQuery();
    		set.next();
    		songID = set.getInt("songID");
    	}// end try
    	catch(SQLException e){
    		e.printStackTrace();
    	}// end catch
		return songID;
    }// end getSongID()
    
    /**Returns the number of songs in the database.
     * @return An int representing the number of songs.
     */
    public int getLibrarySize() {
    	int size = 0;
		try {
			// grab all rows from database
			String statement = "SELECT * FROM songs";
			m_prep = m_connection.prepareStatement(statement);
			ResultSet set = m_prep.executeQuery();
			while(set.next()) { size++; }// increase size by # of rows in database
			m_prep.close();
		}// end try 
		catch (SQLException e) {
		    e.printStackTrace();
		    return 0;
		}// end catch
		return size;
    }// end getLibrarySize()
}// end Database class