#Artificial Intelligence Programming Exercise
The aim of the programming exercise is to implement an algorithm that plays the reverse game.
#Participants
    Pablo Panero
    Satish Kumar Rajagopal
    Raúl Jímenez Redondo
#Algorithm
    Our algorithm (Jarvis) implements iterative deepening search with no depth limit. Therefore, the only constraint is the given time, meaning that it will keep looking for a better solution according to the heuristics.
    Alpha-Beta pruning is used to cut off branches of the tree according to Wikipedia pseudocode . However due to the usage of different parameters, values, and making it in a single function instead of two (one for max, and another for min) it has been slightly changed.
#Heuristics
-Coin placement
    Concerning the heuristics, a two dimensional matrix where each position corresponds to its place in the games’ board and contains its weight. Weights are assigned according to a master thesis . The main idea is that borders weight more than the rest of the board, and even more if it is a corner. For the same reason squares that are in the second line (from the limits) of the board are considered worse positions, because the opponent could take borders or corners, and therefore have lower weights.

    10000	-3000	1000	800	800	1000	-3000	10000
    -3000	-5000	-450	-500	-500	-450	-5000	-3000
    1000	-450	30	10	10	30	-450	1000
    8000	-500	10	50	50	10	-500	800
    8000	-500	10	50	50	10	-500	800
    1000	-450	30	10	10	30	-450	1000
    -3000	-5000	-450	-500	-500	-450	-5000	-3000
    10000	-3000	1000	800	800	1000	-3000	10000

    However, if is a corner is available as a movement, it is taken without further checking because of its high value.
-Coin parity
    In addition the amount of coins of each player is taken into account . The difference between our amount of coins and the opponents’ one is divided by the total amount of coins in the board.

    1000 *  (Player coins-Opponent coins)/(Player coins+Opponent coins)

    This value is multiplied by 1000 in order for it to have effect along with the matrix values.
-Mobility
    In order to avoid running out of moves and therefore losing its amount is taken into account. If a movement leads to 0 moves for us, it is discarded. If not, both ours and the opponents’ amount of moves are taken into account.
    200*  (Player moves-Opponent moves)/(Player moves+Opponent moves)
    As before, this values is multiplied by 200 1000 in order for it to have effect along with the matrix values.

