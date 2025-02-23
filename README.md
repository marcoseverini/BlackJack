<br>

<img src="src/BlackJack/resources/images/logo.png" alt="Image 1" width="50%" style="display:inline-block; vertical-align:middle;"/>
<br>

### Documentation

Javadoc documentation is available in the [doc](doc) folder. To consult it, open [index.html](doc/index.html) in a browser.  

### Class Diagram

[UML.png](UML.png)

## Design Decisions

### User profile management

At the start of the game, the player is given the option to choose an avatar from 4 available options and enter a nickname.  

<div>
   <img src="src/BlackJack/resources/images/avatars/avatar1.png" alt="Image 1" width="5%" style="display:inline-block; vertical-align:middle;"/>
   <img src="src/BlackJack/resources/images/avatars/avatar2.png" alt="Image 2" width="5%" style="display:inline-block; vertical-align:middle;"/>
   <img src="src/BlackJack/resources/images/avatars/avatar3.png" alt="Image 2" width="5%" style="display:inline-block; vertical-align:middle;"/>
   <img src="src/BlackJack/resources/images/avatars/avatar4.png" alt="Image 2" width="5%" style="display:inline-block; vertical-align:middle;"/>
</div>  
<br>
If the field is left empty or a text longer than 10 characters is entered, a pop-up appears signaling the invalidity of the nickname.
Subsequently, in the top left of the Home screen, the following player information will be visible: selected avatar, nickname, number of games won, lost, and drawn.
<br><br>
<img width="14.6%" alt="immagine" src="https://github.com/user-attachments/assets/fba9a1cc-aef0-4ba2-8562-6afba3a686fd" />
<img width="28%" alt="immagine" src="https://github.com/user-attachments/assets/bb70ebc1-2566-4153-830f-1ee36c689682" />

### Management of a complete game against 1/2/3 artificial players.

On the Home screen, the player has the option to choose whether to start a game with one, two, or three players by clicking on the corresponding card image.  

<img width="30%" alt="immagine" src="https://github.com/user-attachments/assets/f6be8e1f-421a-424a-b730-96832e137120" />
<br><br>

The artificial players follow the same rules as the dealer: they continue to draw cards until they reach a total of 17 points. 

Player 2 and Player 3 are shown on the Home screen, each with a randomly selected avatar, different from the one chosen by the player.
Their number of won, lost, and drawn games is also shown.

<img width="30%" alt="immagine" src="https://github.com/user-attachments/assets/59fffc17-7fa9-4c5e-890b-2c3bf10abf62" />


During the game, the player has 2 available buttons, to draw a new card or to pass the turn.
`hit` and `stay` buttons, respectively.
By selecting `stay`, the dealer flips the hidden card, and along with the other artificial players, shows the other drawn cards.
The `hit` and `stay` buttons become unselectable, and simultaneously, a message is shown at the center of the screen indicating the outcome of the game.
This is accompanied by a `home` button that allows the player to return to the Home screen.

<img width="24.6%" alt="immagine" src="https://github.com/user-attachments/assets/35f6bfda-d247-4235-91e9-2351927dbe5f" />
<br>
<img width="249" alt="immagine" src="https://github.com/user-attachments/assets/3f25c49b-2b69-4eea-96a6-bdc185e6f0f0" />


### Betting System

There is an initial amount of `$5000`, visible on the Home screen.

The betting system automatically starts before the game begins.
The selected chips are placed on the board’s center through animation, stacked one on top of the other.
In the top left, the current balance and the bet amount are shown.
If a chip is selected that exceeds the available balance, a pop-up warning appears indicating the impossibility of making that bet.
<br><br>
<img width="26%" alt="immagine" src="https://github.com/user-attachments/assets/2ef6e7c7-1e0f-4633-8020-06ada36c8f9d" />


## Design Patterns

### MVC
The code is organized in:
- [Model](src/BlackJack/Model.java), responsible for the game logic and data. It manages the state of the game, including the deck of cards, the dealer's hand, the players' hands, and the results.
- **View**, one for each game mode:
    * [OnePlayer.java](src/BlackJack/views/OnePlayer.java): game with no AI players
    * [TwoPlayers.java](src/BlackJack/views/TwoPlayers.java): game with one AI player
    * [ThreePlayers.java](src/BlackJack/views/ThreePlayers.java): game with two AI players
- [Controller](src/BlackJack/Controller.java) that acts as an intermediary between the model and the view. It manages user actions and updates the model accordingly.

The three game views extend the [GameView](src/BlackJack/GameView.java) interface, which defines abstract methods that are implemented by all the views so that the Controller class can communicate with them.

### Observer/Observable

The Model extends the Observable class, and every time its state changes, it sends a notification to the observers via the `notifyObservers()` method.  The observers of the model are [OnePlayer.java](src/BlackJack/views/OnePlayer.java), [TwoPlayers.java](src/BlackJack/views/TwoPlayers.java), and [ThreePlayers.java](src/BlackJack/views/ThreePlayers.java).  These implement the Observer interface and register with the model. When the model changes, the `update()` method is called, allowing the views to reflect the changes.

## GUI

I adopted **Java Swing** for the GUI.
The structure of the interface is centered on a **JFrame**, which represents the main window of the application. I have created several classes that extend **JPanel** to represent the different screens. These are contained in:

* [Start.java](src/BlackJack/views/Start.java): Start screen

* [Home.java](src/BlackJack/views/Home.java): Home screen

* [Bet.java](src/BlackJack/views/Bet.java): Betting phase

* [OnePlayer.java](src/BlackJack/views/OnePlayer.java): Game with no artificial players

* [TwoPlayers.java](src/BlackJack/views/TwoPlayers.java): Game with one artificial player

* [ThreePlayers.java](src/BlackJack/views/ThreePlayers.java): Game with two artificial players

In these, I used Swing elements such as **JButton**, **JLabel**, **JTextField**, etc.
I also used layout managers such as **BorderLayout**, **GridLayout**, and **FlowLayout** for the placement of elements.
User interactions with the interface, such as clicking buttons or entering data in text fields, are handled via listeners that capture the events and trigger the corresponding code to perform the requested operations.


<div style="display: flex; flex-wrap: wrap; ">
    <img width="380" alt="Screenshot 2025-02-23 alle 23 52 37" src="https://github.com/user-attachments/assets/8dfe06c6-e9f7-4147-bb0d-024e662cdc8f" />
    <img width="380" alt="Screenshot 2025-02-23 alle 23 52 25" src="https://github.com/user-attachments/assets/cb9fc9b5-c44e-493e-a55b-9c56a422da4c" />
    <img width="380" alt="Screenshot 2025-02-23 alle 23 52 08" src="https://github.com/user-attachments/assets/8a5b7467-955a-4a99-a17e-b933ac2cec18" />
    <img width="380" alt="Screenshot 2025-02-23 alle 23 51 51" src="https://github.com/user-attachments/assets/cc179368-61f9-4112-9a83-9fc757c04d71" />
    <img width="380" alt="Screenshot 2025-02-23 alle 23 51 23" src="https://github.com/user-attachments/assets/a169a1c6-7502-4c55-8059-18a7a30c3d12" />
   <img width="380" alt="Screenshot 2025-02-24 alle 00 11 16" src="https://github.com/user-attachments/assets/98193353-74e5-4cd8-9b4c-c812fc3f7798" />

</div>

## Stream

In the Model, I used streams to distribute the cards to the player and the bots, and to generate the deck with all possible combinations of suits and values.
In [Bet.java](src/BlackJack/views/Bet.java) (the betting phase), I used streams to speed up the creation of a `Map<Integer, Image>` where the integer is the value of the chip, and the image is the corresponding chip image. From this Map, I used streams to create the **ChipButtons**, i.e., the **ImageButtons** for the chips, which are then drawn in their correct position on the board.
In the views for the 1, 2, and 3 player games, I used streams to update the cards in the hands of the player and the bots.
Streams were also used to update the positions of the cards.
I also used streams in other cases to make the code more compact and efficient.

## AudioManager
 Through the **AudioManager** library, I inserted the playback of 4 .wav files in different situations:
* [card.wav](src/BlackJack/resources/audio/card.wav)
* [click.wav](src/BlackJack/resources/audio/click.wav)
* [click2.wav](src/BlackJack/resources/audio/click2.wav)
* [chip.wav](src/BlackJack/resources/audio/chip.wav)

## Animation 

The main animations implemented relate to the movement of the chips during the bets and the distribution of the cards. For the chips, I created a class **MovingCircle**, which has an initial position, a target position, and a total number of frames to complete the animation.
The `update()` method calculates the new position of the chip at each frame.
The animation is managed by a Timer that periodically calls `update()` and redraws the panel.
For the cards, I created an abstract class [AnimationCard.java](src/BlackJack/views/AnimationCard.java) with various subclasses to handle different types of animations of the cards in the 1, 2, and 3 player games.
These perform two types of movement:
* From outside the board to reach their position when drawn
* From their previous position to their next position to make room for the new drawn cards

The animation of the cards follows a similar mechanism to the chips, with the addition that at each update, the cards move one-tenth of the remaining distance to reach the target.
This means that as the distance decreases, the speed with which they move decreases, creating a smooth movement effect.


<img src="https://github.com/user-attachments/assets/96ab65e6-bbad-430a-8dc4-5600628580e7" width="300" />
<br>
<img src="https://github.com/user-attachments/assets/c0a5dcd0-b197-4b8a-816b-7eca93159b59" width="300" />



