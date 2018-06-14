# Connect4AI
Implemented a connect 4 ai that competes on http://theaigames.com/competitions. It's currently 40th among over 500 submissions.

I implemented the following algorithms:
- Minimax
  - Alpha Beta Pruning
  - Iterative Deepening
- Monte Carlo Tree search

The best bot is a 6 ply minimax with alpha beta pruning and iterative deepening.
The evaluation function is the total number of incomplete 3 in a rows for the player minus the same for the opponent.
This is meant to represents the net number of chances the player has.
