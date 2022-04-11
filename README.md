# Crazy Eights

Crazy Eights is a simple and popular card game with many variations being played all over the world. In this assignment, we will implement this game in Java. 

## Rules

In our variation of Crazy Eights, the game is played with the 52 standard cards of a playing deck. That is, all the Aces, Jacks, Queens, and Kings, as well as all the numbered cards for each of the suits Clubs, Diamonds, Hearts, and Spades are included. In our variation, the game is played without Jokers. 

The game is played with at least 2 and at most 7 players. At the start of the game, the deck is shuffled and each player is handed five cards. One card from the deck is then added to the discard pile. Players then take turns discarding cards or drawing a card from the deck. Cards may only be discarded if it shares the suit or the rank with the top card of the discard pile. That is, if the top card of the discard pile is a 5 of Clubs, the player may only play cards that are Clubs (e.g. the 4 of Clubs) or cards that have rank 5 (e.g. the 5 of Diamonds). The card that has been played then becomes the new top card of the discard pile. If they cannot play any card, they must draw a card from the deck. The first player to discard all cards in their hand wins the game. 

In addition to the basic rules above, there are a few special cards:
- **Ace Reverse**: Whenever a player plays an Ace, the direction of play reverses.
- **Draw Two**: Whenever a player plays a 2, the next player must draw 2 cards before their turn starts. Note that in this variation of the game, it is not allowed to stack 2s.
- **Queen Skip**: Whenever a player plays a Queen, the next player skips their turn.
- **Crazy Eights**: Players may always play an 8, even if it does not match the suit or rank of the top card of the discard pile. If a player plays an 8, they choose a suit. The 8 that is now on top of the discard pile is now treated as if it were of the chosen suit. For example, if a player plays the 8 of Hearts and chooses Diamonds, the next player can only play another 8, or any Diamonds card. They cannot play any Hearts card (unless it is the 8 of Hearts).

## Implementation

The assignment is to implement this game in Java, using Object Oriented Programming concepts and practices. In your implementation, artificial players (i.e. algorithms) should be able to compete in a game of Crazy Eights, as described in the previous section. For this purpose, your implementation should include a player capable of playing Crazy Eights. We recommend implementing a random player that randomly chooses a (legal) action to perform.


In your implementation, consider the following issues.

- Playing cards and players should be objects that are separate of the game controller class.
- Different classes should be as independent as possible. For example, it should be possible to define a new player class or a new type of card without changing the game controller class.
- Players should not be able to cheat. That is, players should not be able to:
    - Play a card that is not in their hand,
	- Change the cards in their hand,
	- Change the cards in other players' hands,
	- Change the top card of the discard pile, or
	- Announce themselves the winner.
- Players should be able to observe relevant information, including:
    - The cards in their hand, and
	- The top card of the discard pile.
