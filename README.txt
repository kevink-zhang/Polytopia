=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=
CIS 120 Game Project README
PennKey: kz2025
=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=:=

===================
=: Core Concepts :=
===================

- List the four core concepts, the features they implement, and why each feature
  is an appropriate use of the concept. Incorporate the feedback you got after
  submitting your proposal.

  1. Advanced Game Logic

     Terrain is generated using Perlin noise. In addition, each tribe has requirements on the probability of resources
     generating in a 1 and 2 tile radius around cities, and required resource generation as well for balanced gameplay.

  2. Collections

     GameTile classes are stored in a Set that represents the board. Each GameTile stores its adjacency GameTiles, so
     when a BFS is called on a tile, it can easily traverse a given distance/cost through these adjacencies.

  3. Advanced mechanic

     Server-client interaction. This game can be played as multiplayer on separate computers, where you can join a host
     game from the host's ip. The game allows for user input to find these games.

  4. Inheritence and subtyping

    Each class in GameAction implements the GameAction interface. Although each GameAction must include a tick function,
    an end function, and

=========================
=: Your Implementation :=
=========================

- Provide an overview of each of the classes in your code, and what their
  function is in the overall game.

  Button, CircleButton, InfoBox, PopUp: visual classes for user interaction within the main JPanel,
  with functionalities for adding labels and icons

  FontLoader, ImageLoader: loads in fonts and images as pointers for other draw functions to use

  Main, Game: same purpose as in the example projects
  Game2: a duplicate client for localhost server testing

  GameBody: controls and draws the main part of the game. Also processes server requests. The board logic is stored in
  GameBoard: stores board logic, sends client requests to server

  Unit folder:
  Stat: stores the stats for each Unit game
  Unit: class for the troops of the game

  Tile folder:
  Building: stores the information and update functions for game buildings
  City: stores the information, draws, and updates for cities
  Edge: a simple datastructure I used for finding city borders. Used to have a draw function, but was
  replaced by a generated polygon in GameBoard
  GameMap: a map of tiles. This is where terrain generation occurs, including Perlin Noise, tribe specific spawn random
  and where server information about game spawns are processed
  GameTile: stores the information, game logic, and draw functions for each tile in the game map.
  Tech: stores the information and draw function for a technology of a tribe. These techs are stored in a set in
  Tribe: essentially the player class. The static variable USER is the client's tribe.

  Server folder:
  Client: framework for sending things to the server. Modified from an online example.
  Server: framework for server using sockets. Code was heavily modified from an online example, as the online example
  threads were not working at all in conjunction with game.
  Server packet: a simple class for sending information to and from the server, which converts objects to and from JSON
  using Gson

  GameAction:
  Each action is run by the ACTION queue in GameBoard for easy client-server information decoding and consistency.
  More documentation for each class implenting the GameAction interface can be found within the GameAction interface.


- Were there any significant stumbling blocks while you were implementing your
  game (related to your design, or otherwise)?

  Everything with serversockets, perlin noise, string equality, using mutable objects as map keys, resizing images,
  converting private variables into JSON strings using Gson, creating a draggable and zoomable perspective, using and
  loading in custom fonts.


- Evaluate your design. Is there a good separation of functionality? How well is
  private state encapsulated? What would you refactor, if given the chance?

  I did not encapsulate many variables inside classes since these variables found constant. In addition, Gson breaks
  when I try giving it custom classes with private variables, so I was forced to make many variables public. I did try
  to encapsulate the few private variables that did exist. I think there was a pretty good separation of functionality,
  as I did have to separate GameBoard and GameBody at one point due to the class being too cluttered.

  If I were to have more time to refine my project, I would improve the UI by figuring out how italicized and bold fonts
  work, as well as adding translucent rectangles behind certain texts to make them more clear. Finally, I would add
  costs to Techs and display costs on the action toolbar.



========================
=: External Resources :=
========================

- Cite any external resources (images, tutorials, etc.) that you may have used 
  while implementing your game.

  Polytopia game images, Polytopia game main menu screen, Gson examples, ServerSocket Oracle docs example and the JPanel
  popup tutorial.
