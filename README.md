# Project-MP3-Player-
Design a fully functional MP3 player.

This repository contains all the files nessesary to run the MP3 player. An SQL database is needed in order to run this project. 

Playlist panel with trees.

![](Images/image1.png)

Playlist "Bruch" selected and displayed in the main frame.

![](Images/image2.png)
 
Playlist "Bruch" moved to a separate window and main frame window filled with the song library. Multiple playlist may be open simultaneously.

![](Images/image3.png)

Popup menu for adding a selected song to a playlist.

![](Images/image4.png)

Open playlist in a new window popup menu - playlist must be selected.

 ![](Images/image5.png)
 
Create a new playlist from menu "File".

![](Images/image6.png)

Dialog to name the playlist.

![](Images/image7.png) 

MyTunes after playlist Radiohead is created. 

![](Images/image8.png)

Radiohead playlist to separate window.

![](Images/image9.png) 

Radiohead playlist after "High and Dry" dragged from MyTunes library to playlist.

![](Images/image10.png)
 
A song may appear in multiple playlists. It should only in the DB once. A very simple playlist song tactic is to create a table with playlist IDs and song IDs. Do not duplicate the song tag data.
