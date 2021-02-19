import javazoom.jlgui.basicplayer.BasicPlayer;
//import javazoom.jlgui.basicplayer.BasicPlayerException;
import javazoom.jlgui.basicplayer.BasicController;
import java.io.File;

/**
 * Handles everything relating to music playing and selecting songs.
 */
public class MusicPlayer {

    private int m_currentSong;
    private String m_currentSongPath;
    private BasicPlayer m_player;
    private BasicController m_controller;
    private double m_volume;

    /**
     * Default constructor, creates the basic player object to control song playing.
     */
    public MusicPlayer() {
        m_player = new BasicPlayer();
        m_controller = (BasicController) m_player;
        m_volume = -1.0;    // indicates that gain has yet to be initialized
    }// end default constructor

    /**
     * Gets the current volume level of the player/system converted to Volume Slider value
     *
     * @return the current volume level as an int value in range [0, 100]
     */
    public int getSliderVolume() {
        return (int)(this.m_volume * 100);
    }// end getSliderVolume()

    /**
     * Returns the current volume.
     * @return The current volume value.
     */
    public double getVolume() { 
    	return m_volume; 
    }// end getVolume()

    public BasicPlayer getPlayer() {
        return m_player;
    }// end getPlayer()

    /**
     * Adjusts the volume to the given value
     * Note: the volume value must be in range [0.0, 1.0] as per
     * BasicPlayer setGain() method requirement
     *
     * @param volume The volume to change to (double value in range [0.0, 1.0]
     */
    public void adjustVolume(double volume) {
        try {
            m_controller.setGain(volume);
            this.m_volume = volume;
        }// end try 
        catch (Exception e) {
        	 System.out.println("From MusicPlayer.java: Error occured adjusting the volume!");
        }// end catch
    }// end adjustVolume()

    /**
     * Takes in a filePath of a song and opens it to play the song.
     * @param filePath The location of the file.
     * @return True if song plays successfully, false otherwise.
     */
    public boolean play(String filePath) {
        try {
            m_controller.open(new File(filePath));
            // play song right after opening the file
            m_controller.play();
            m_currentSongPath = filePath;
            // Gain will be set to default value 0.5
            // Volume is set to 0.5
            if(this.m_volume == -1.0) {
                m_controller.setGain(0.5);
                this.m_volume = 0.3;
            }// end if
            return true;
        }// end try 
        catch (Exception e) {
        	 System.out.println("From MusicPlayer.java: Error occured when loading in the song!");
        }// end try
        return false;
    }// end play()

    /**
     * Resumes the current song.
     * @return True if song is resumed successfully, false otherwise.
     */
    public boolean resume() {
        try {
            m_controller.resume();
            return true;
        }// end try 
        catch (Exception e) {
        	 System.out.println("From MusicPlayer.java: Song could not be resumed!");
        }// end catch
        return false;
    }// end resume()

    /**
     * Pauses the current song.
     * @return True if the song is paused successfully, false otherwise.
     */
    public boolean pause() {
        try {
            m_controller.pause();
            return true;
        }// end try 
        catch (Exception e) {
        	 System.out.println("From MusicPlayer.java: Song could not be paused!");
        }// end catch
        return false;
    }// end pause()

    /**
     * Stops the current song.
     * @return True if song stopped successfully, false otherwise.
     */
    public boolean stop() {
        try {
            m_controller.stop();
            return true;
        }// end try 
        catch (Exception e) {
            System.out.println("From MusicPlayer.java: Song could not be stopped!");
        }// end catch
        return false;
    }// end stop()

    /**
     * Gets the currently played song. 
     * @return The current song int.
     */
    public int getCurrentSong() {
        return m_currentSong;
    }// end getCurrentSong()
    
    /**Returns the filePath of the currently playing song.
     * @return A String representing the current song's filePath.*/
    public String getCurrentSongPath() {
    	return m_currentSongPath;
    }// end getCurrentSongPath()

    /**
     * Allows the user to select a song to be played.
     * @param choice The song that is selected to play.
     * @param filePath The selected song's filePath.
     */
    public void setCurrentSong(int choice, String filePath) {
        this.m_currentSong = choice;
        this.m_currentSongPath = filePath;
    }// end setCurrentSong()
   
}// end musicPlayer class