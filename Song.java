import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.ID3v1;
import java.io.IOException;

/**Song objects contain the information of the mp3 file they have been 
 * constructed around and contain the location of the mp3 file in the directory.*/
public class Song {
	private String genre;
	private String comment;
	private String filePath;
	private String album;
    private String year;
    private String artist;
    private String title;
    private int ID;
    
    /**Takes the song file path and stores it in an MP3File obj to get its info. 
     * Also takes in an ID and stores it. This constructor is used when a song's info
     * is being taken from the SQL database and its assigned ID in the database is needed. 
     * @param Location of of the song.
     * @param ID
     */
    public Song(String filePath, int ID) {
    	this(filePath);
        this.ID = ID;
    }// end song constructor
    
    /**Takes the song file path and stores it in an MP3File obj to get its info.  
     * @param Location of of the song.
     */
    public Song(String filePath) {
        try {
            Mp3File mp3 = new Mp3File(filePath);
            this.filePath = filePath;
            if (mp3.hasId3v1Tag()) {
                ID3v1 id3v1Tag = mp3.getId3v1Tag();
                this.artist = id3v1Tag.getArtist();
                this.title = id3v1Tag.getTitle();
                this.album = id3v1Tag.getAlbum();
                this.year = id3v1Tag.getYear();
                this.genre = id3v1Tag.getGenreDescription();
                this.comment = id3v1Tag.getComment();
                this.ID = 0; 
            }// end if  
            else if (mp3.hasId3v2Tag()) {
                ID3v2 id3v2Tag = mp3.getId3v2Tag();
                this.title = id3v2Tag.getTitle();
                this.artist = id3v2Tag.getArtist();
                this.genre = id3v2Tag.getGenreDescription();
                this.comment = id3v2Tag.getComment();
                this.album = id3v2Tag.getAlbum();
                this.year = id3v2Tag.getYear();
                this.ID = 0;
            }// end else 
        }// end try 
        catch (IOException ioe) {
            System.out.println("From song.java: \"" + this.filePath + "\" is an invalid location!");
        }// end catch 
        catch (Exception e) {
            System.out.println("From song.java: Error occured while getting song info!");
        }// end catch 
    }// end Song constructor

    /**
     * Returns the Song file path.
     * @return the Song file path.
     */
    public String getFilePath() {
        return filePath;
    }// end getFilePath()

    /**
     * Returns the Song artist.
     * @return the Song artist.
     */
    public String getArtist() {
        return artist;
    }// end getArtist()

    /**
     * Returns the Song title.
     * @return the Song title.
     */
    public String getTitle() {
        return title;
    }// end getTitle()

    /**
     * Returns the Song album.
     * @return the Song album.
     */
    public String getAlbum() {
        return album;
    }// end getAlbum()

    /**
     * Returns the Song year.
     * @return the Song year.
     */
    public String getYear() {
        return year;
    }// end getYear()

    /**
     * Returns the Song genre.
     * @return the Song genre.
     */
    public String getGenre() {
        return genre;
    }// end getGenre()

    /**
     * Returns the Song comment.
     * @return the Song comment.
     */
    public String getComment() { 
    	return comment; 
    }// end getComment()
    
    /**
     * Returns the unique ID of the song obj.
     * @return An int representing the Song obj's unique ID.
     */
    public int getID() {
    	return ID;
    }// end getID()
    
    /**
     * Returns a string containing all the info of the song.
     * @return A string obj with song's info.
     */
    public String toString() {
    	String song = "Title: "   + this.getTitle()    + "\n" +
    			      "Artist: "  + this.getArtist()   + "\n" +
    			      "Album: "   + this.getAlbum()    + "\n" +
    			      "Year: "    + this.getYear()     + "\n" +
    			      "Genre: "   + this.getGenre()     + "\n" +
    			      "Comment: " + this.getComment()  + "\n";
		return song;
    	
    }// end toString()

}