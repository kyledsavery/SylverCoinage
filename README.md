# Sylver Coinage
Sylver Coinage is a two-player mathematical game played on the positive number line and the purpose of this program calculates which player is in a winning position for manageable game sizes given initial conditions.. Sylver Coinage was invented by John H. Conway and is named after James Joseph Sylvester who proved that any number greater than (_a_-1)(_b_-1)-1 for some integers _a_, _b_ > 0 can be made as a linear combination of _a_ and _b_. The game is described in _Winning Ways for Your Mathematical Plays_ (1982).

## How to Play
The game alternates between two players with each selecting an available integer on their turn. To begin, **Player One** chooses a positive integer say C<sub>1</sub>. This eliminates every non-negative multiple of that integer. **Player Two** can now select an integer, C<sub>2</sub>, such that C<sub>2</sub> =/= _m_ C<sub>1</sub>, for integer _m_ > 0. This effect compounds the longer the game is played, any choice must not be a linear combination of _any_ of the previous choices. So, when **Player One** prepares to make his second choice, it can not be of the form _m_ C<sub>1</sub> + _n_ C<sub>2</sub> for positive integers _m_, _n_. 

Clearly, since every number is a multiple of 1, the player who is forced to name 1 eliminates all other choices and loses the game. In the context of Sylver Coinage, the positive number line is now referred to as the **board** which starts out as {1, 2, 3, 4, 5, 6, 7, ...}.

### Example
Suppose the following choices are made by both players:

* **Player One** begins by selecting 5
  * Resulting board = {1, 2, 3, 4, 6, 7, 8, 9, 11, 12, ...}
* **Player Two** selects 7
  * Resulting board = {1, 2, 3, 4, 6, 8, 9, 11, 13, 16, 18, 23}, note the board is now finite.
* **Player One** selects 8
  * Resulting board = {1, 2, 3, 4, 6, 9, 11}
* **Player Two** selects 9
  * Resulting board = {1, 2, 3, 4, 6, 11}
* **Player One** selects 11
  * Resulting board = {1, 2, 3, 4, 6}
* **Player Two** selects 4
  * Resulting board = {1, 2, 3, 6}
* **Player One** selects 6
  * Resulting board = {1, 2, 3}
* **Player Two** selects 2
  * Resulting board = {1, 3}
* **Player One** selects 3
  * Resulting board = {1}
* **Player Two** is forced to choose 1, losing the game.

In fact, if at any point a player selects 2 the other player can select 3 and vice versa. This results in a board with only 1. Thus 1, 2, and 3 are regarded as trivial choices since selecting any of them puts the player in an obvious losing position. As such, this program ignores 1, 2, and 3 for simplicity.

## Usage
