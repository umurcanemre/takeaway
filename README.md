#About

This is an assesment game for Takeaway

#Running

##Tests
>cd ${project_directory}

>./mvnw test

##Server
>cd ${project_directory}

>./mvnw spring-boot:run

#Endpoints
After running the server go to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

* /players         -> Post method to create a new player account
* /players/{id}    -> Get method to get a player by id if exists


* /lobby/new       -> Post method to create a new game for a player, which waits in "lobby" for other play players to join
* /lobby/join      -> Post method for a player to join a pre-existing game that's waiting for a player


* /play/makemove   -> Post method that allows a player to make a move on a game
* /play            -> poorly named get endpoint to see the trace of a given game

#How-to play
* all players need to be created using /players method

* create a game in lobby. In here there is a decision to make the game either automatic or manual. 

* Rest of tjhe players should join using /lobby/join endpoint

⋅⋅⋅If game is initiated in automatic mode, once game has all the players, it will "play itself" until a player has won.

* In manual games, each player, in their turn, need to use /play/makemove endpoint to play the game until one has won.