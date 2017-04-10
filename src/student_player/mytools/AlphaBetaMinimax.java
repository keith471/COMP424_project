package student_player.mytools;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;

/**
 * Minimax with alpha-beta pruning
 * 
 * @author kstricks
 *
 */
public class AlphaBetaMinimax {

	private static final int MAX_BEANS = 72;

	// the id of the max player
	private int player;
	// an integer indicating the utility function to use
	private int utilityFunction;

	public AlphaBetaMinimax(int player, int utilityFunction) {
		this.player = player;
		this.utilityFunction = utilityFunction;
	}

	/**
	 * Chooses a move based on the minimax algorithm and given a copy of the
	 * current board state. Uses alpha-beta pruning to speed up the computation.
	 * 
	 * @param boardState
	 *            - a copy of the current board state
	 * @param movesToGo
	 *            - the depth at which to stop simulating moves (i.e. the number
	 *            of moves to simulate)
	 * @return The best move or null if no moves are possible or if all moves
	 *         result in us losing
	 */
	public MinimaxResponse minimaxDecision(BohnenspielBoardState boardState, int movesToGo) {
		if (movesToGo <= 0) {
			throw new InvalidDepthException();
		}

		BohnenspielMove bestMove = null;
		int bestScore = Integer.MIN_VALUE;

		int alpha = Integer.MIN_VALUE;
		int beta = Integer.MAX_VALUE;

		int projectedMoveScore;
		for (BohnenspielMove move : boardState.getLegalMoves()) {
			// clone the current board state
			BohnenspielBoardState clonedBoardState = (BohnenspielBoardState) boardState.clone();
			// make the move on the cloned board state
			clonedBoardState.move(move);
			// get score expected if we make this move
			projectedMoveScore = minValue(clonedBoardState, movesToGo - 1, alpha, beta);
			if (projectedMoveScore == Integer.MAX_VALUE) {
				// this move results in us winning --> take it
				return new MinimaxResponse(move, false, false);
			} else if (projectedMoveScore == Integer.MIN_VALUE) {
				// this move results in us losing --> move onto the next move
				continue;
			}
			// update the best move/score if applicable
			if (projectedMoveScore > bestScore) {
				bestScore = projectedMoveScore;
				alpha = projectedMoveScore;
				bestMove = move;
			}
			// is it worth considering any other moves?
			if (alpha > beta) {
				// this path will not be allowed by the minimizer --> prune
				break;
			}
		}

		return new MinimaxResponse(bestMove, false, bestMove == null ? true : false);
	}

	/**
	 * Min player's move.
	 * 
	 * @param boardState
	 * @return
	 */
	private int minValue(BohnenspielBoardState boardState, int movesToGo, int alpha, int beta) {
		if (boardState.gameOver()) {
			if (boardState.getWinner() == this.player) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		} else if (movesToGo == 0) {
			return getUtility(boardState);
		}

		int bestScore = Integer.MAX_VALUE;
		int projectedMoveScore;
		for (BohnenspielMove move : boardState.getLegalMoves()) {
			BohnenspielBoardState clonedBoardState = (BohnenspielBoardState) boardState.clone();
			clonedBoardState.move(move);
			projectedMoveScore = maxValue(clonedBoardState, movesToGo - 1, alpha, beta);
			if (projectedMoveScore == Integer.MAX_VALUE) {
				// this move results in the min player losing --> try the next
				// one
				continue;
			} else if (projectedMoveScore == Integer.MIN_VALUE) {
				// this move results in the min player winning
				return Integer.MIN_VALUE;
			}
			// update the best score (and beta) if applicable
			if (projectedMoveScore < bestScore) {
				bestScore = projectedMoveScore;
				beta = projectedMoveScore;
			}
			// is it worth considering any other moves?
			if (beta < alpha) {
				// this path will not be allowed by the maximizer --> prune
				break;
			}
		}

		return bestScore;
	}

	/**
	 * Max player's move. The max player ADDS any score they gain from making a
	 * move.
	 * 
	 * @param boardState
	 * @return
	 */
	private int maxValue(BohnenspielBoardState boardState, int movesToGo, int alpha, int beta) {
		if (boardState.gameOver()) {
			if (boardState.getWinner() == this.player) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		} else if (movesToGo == 0) {
			return getUtility(boardState);
		}

		int bestScore = Integer.MIN_VALUE;
		int projectedMoveScore;
		for (BohnenspielMove move : boardState.getLegalMoves()) {
			BohnenspielBoardState clonedBoardState = (BohnenspielBoardState) boardState.clone();
			clonedBoardState.move(move);
			projectedMoveScore = minValue(clonedBoardState, movesToGo - 1, alpha, beta);
			if (projectedMoveScore == Integer.MAX_VALUE) {
				// this move results in the max player winning --> take it
				return Integer.MAX_VALUE;
			} else if (projectedMoveScore == Integer.MIN_VALUE) {
				// this move results in the min player winning --> try a
				// different move
				continue;
			}
			// update the best score and alpha if applicable
			if (projectedMoveScore > bestScore) {
				bestScore = projectedMoveScore;
				alpha = projectedMoveScore;
			}
			// is it worth considering any other moves?
			if (alpha > beta) {
				// this path will not be allowed by the minimizer --> prune
				break;
			}
		}

		return bestScore;
	}

	/**
	 * Computes and returns the value of a utility function, based on the
	 * utility function specified when this AlphaBetaMinimax object was created
	 * 
	 * @param boardState
	 * @return
	 * @throws UndefinedUtilityFunctionException
	 */
	private int getUtility(BohnenspielBoardState boardState) throws UndefinedUtilityFunctionException {
		if (this.utilityFunction == 0) {
			return scoreDifference(boardState);
		} else if (this.utilityFunction == 1) {
			return scoreAndBeanDifference(boardState);
		} else if (this.utilityFunction == 2) {
			return scoreAndBeanDifference2(boardState);
		} else if (this.utilityFunction == 3) {
			return scoreAndBeanDifferenceWithBeansLeft(boardState);
		} else if (this.utilityFunction == 4) {
			return scoreAndBeanDifferenceWithBeansLeft2(boardState);
		} else if (this.utilityFunction == 5) {
			return scoreAndBeanDifferenceWithBeansLeft3(boardState);
		} else if (this.utilityFunction == 6) {
			return scoreAndBeanDifferenceWithBeansLeft4(boardState);
		} else if (this.utilityFunction == 7) {
			return scoreDifferenceWithBeansLeft(boardState);
		}
		throw new UndefinedUtilityFunctionException();
	}

	// =========================================================================
	// Utility functions
	// =========================================================================

	/**
	 * Returns the difference in score between the two players in this board
	 * state
	 * 
	 * @param boardState
	 * @return
	 */
	private int scoreDifference(BohnenspielBoardState boardState) {
		return boardState.getScore(this.player) - boardState.getScore(1 - this.player);
	}

	/**
	 * The more beans we have on our side, the more control we have, and the
	 * more likely it is that our opponent can't make a move. Thus, we might
	 * want to factor in the number of beans on either side.
	 */
	private int scoreAndBeanDifference(BohnenspielBoardState boardState) {
		int[][] pits = boardState.getPits();
		int myBeans = 0;
		int yourBeans = 0;
		for (Integer beans : pits[this.player]) {
			myBeans += beans;
		}
		for (Integer beans : pits[1 - this.player]) {
			yourBeans += beans;
		}
		return (boardState.getScore(this.player) - boardState.getScore(1 - this.player)) + (myBeans - yourBeans);
	}

	/**
	 * Same as the above except we don't value bean difference as highly.
	 */
	private int scoreAndBeanDifference2(BohnenspielBoardState boardState) {
		int[][] pits = boardState.getPits();
		int myBeans = 0;
		int yourBeans = 0;
		for (Integer beans : pits[this.player]) {
			myBeans += beans;
		}
		for (Integer beans : pits[1 - this.player]) {
			yourBeans += beans;
		}
		return (boardState.getScore(this.player) - boardState.getScore(1 - this.player))
				+ (int) (0.5 * (myBeans - yourBeans));
	}

	/**
	 * Same as the above but now we also factor in the number of beans left. The
	 * fewer beans left, the closer we are to winning (assuming we are ahead).
	 */
	private int scoreAndBeanDifferenceWithBeansLeft(BohnenspielBoardState boardState) {
		int[][] pits = boardState.getPits();
		int myBeans = 0;
		int yourBeans = 0;
		for (Integer beans : pits[this.player]) {
			myBeans += beans;
		}
		for (Integer beans : pits[1 - this.player]) {
			yourBeans += beans;
		}
		return (MAX_BEANS - (myBeans + yourBeans))
				* (boardState.getScore(this.player) - boardState.getScore(1 - this.player))
				+ (int) (0.5 * (myBeans - yourBeans));
	}

	/**
	 * Similar to the above, but we group the bean difference with the score
	 * difference
	 * 
	 * @param boardState
	 * @return
	 */
	private int scoreAndBeanDifferenceWithBeansLeft2(BohnenspielBoardState boardState) {
		int[][] pits = boardState.getPits();
		int myBeans = 0;
		int yourBeans = 0;
		for (Integer beans : pits[this.player]) {
			myBeans += beans;
		}
		for (Integer beans : pits[1 - this.player]) {
			yourBeans += beans;
		}
		return (MAX_BEANS - (myBeans + yourBeans))
				* (3 * (boardState.getScore(this.player) - boardState.getScore(1 - this.player))
						+ (myBeans - yourBeans));
	}

	/**
	 * Similar to the above, but we multiply by the ratio of total beans to
	 * beans left rather than total beans used up
	 * 
	 * @param boardState
	 * @return
	 */
	private int scoreAndBeanDifferenceWithBeansLeft3(BohnenspielBoardState boardState) {
		int[][] pits = boardState.getPits();
		int myBeans = 0;
		int yourBeans = 0;
		for (Integer beans : pits[this.player]) {
			myBeans += beans;
		}
		for (Integer beans : pits[1 - this.player]) {
			yourBeans += beans;
		}
		return (MAX_BEANS / Math.max(myBeans + yourBeans, 1))
				* (3 * (boardState.getScore(this.player) - boardState.getScore(1 - this.player))
						+ (myBeans - yourBeans));
	}

	/**
	 * Similar to scoreAndBeanDifferenceWithBeansLeft2 except we now multiply
	 * the score difference by 2
	 * 
	 * @param boardState
	 * @return
	 */
	private int scoreAndBeanDifferenceWithBeansLeft4(BohnenspielBoardState boardState) {
		int[][] pits = boardState.getPits();
		int myBeans = 0;
		int yourBeans = 0;
		for (Integer beans : pits[this.player]) {
			myBeans += beans;
		}
		for (Integer beans : pits[1 - this.player]) {
			yourBeans += beans;
		}
		return (MAX_BEANS - (myBeans + yourBeans))
				* (2 * (boardState.getScore(this.player) - boardState.getScore(1 - this.player))
						+ (int) (0.5 * (myBeans - yourBeans)));
	}

	/**
	 * Same as scoreAndBeanDifferenceWithBeansLeft, but we don't consider the
	 * bean difference
	 * 
	 * @param boardState
	 * @return
	 */
	private int scoreDifferenceWithBeansLeft(BohnenspielBoardState boardState) {
		int[][] pits = boardState.getPits();
		int myBeans = 0;
		int yourBeans = 0;
		for (Integer beans : pits[this.player]) {
			myBeans += beans;
		}
		for (Integer beans : pits[1 - this.player]) {
			yourBeans += beans;
		}
		return (MAX_BEANS - (myBeans + yourBeans))
				* (boardState.getScore(this.player) - boardState.getScore(1 - this.player));
	}

}
