# Project-MP3-Player-
Design a fully functional MP3 player.

This repository contains all the files nessesary to run the MP3 player. An SQL database is needed in order to run this project. 

Pictures

![](images/image1.png)

Playlist panel with trees.

![](images/image2.png)
 
Playlist "Bruch" selected and displayed in the main frame.

![](images/image3.png)

Playlist "Bruch" moved to a separate window and main frame window filled with the song library. Multiple playlist may be open simultaneously.

![](images/image4.png)

Popup menu for adding a selected song to a playlist.

 ![](images/image5.png)
 
Open playlist in a new window popup menu - playlist must be selected.

![](images/image6.png)

Create a new playlist from menu "File".

![](images/image7.png) 

Dialog to name the playlist.

![](images/image8.png)

MyTunes after playlist Radiohead is created. 

 ![](images/image9.png) 

Radiohead playlist to separate window.

 ![](images/image10.png)
 
Radiohead playlist after "High and Dry" dragged from MyTunes library to playlist.

A song may appear in multiple playlists. It should only in the DB once. A very simple playlist song tactic is to create a table with playlist IDs and song IDs. Do not duplicate the song tag data.
