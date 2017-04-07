package student_player.mytools;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;

public class Minimax {

	// the id of the max player
	private int player;

	public Minimax(int player) {
		this.player = player;
	}

	/**
	 * Chooses a move based on the minimax algorithm and given a copy of the
	 * current board state
	 * 
	 * @param boardState
	 *            - a copy of the current board state
	 * @return The best move or null if no moves are possible or if all moves
	 *         result in us losing
	 */
	public BohnenspielMove minimaxDecision(BohnenspielBoardState boardState) {
		BohnenspielMove bestMove = null;
		int bestScore = Integer.MIN_VALUE;

		int projectedMoveScore;
		for (BohnenspielMove move : boardState.getLegalMoves()) {
			// clone the current board state
			BohnenspielBoardState clonedBoardState = (BohnenspielBoardState) boardState.clone();
			// make the move on the cloned board state
			clonedBoardState.move(move);
			// get net score expected if we make this move (not including any
			// gain from the move itself)
			projectedMoveScore = minValue(clonedBoardState);
			if (projectedMoveScore == Integer.MAX_VALUE) {
				// this move results in us winning --> take it
				return move;
			} else if (projectedMoveScore == Integer.MIN_VALUE) {
				// this move results in us losing --> move onto the next move
				continue;
			}
			if (projectedMoveScore > bestScore) {
				bestScore = projectedMoveScore;
				bestMove = move;
			}
		}

		return bestMove;
	}

	/**
	 * Min player's move. The min player SUBTRACTS any score they gain from
	 * making a move.
	 * 
	 * @param boardState
	 * @return
	 */
	private int minValue(BohnenspielBoardState boardState) {
		if (boardState.gameOver()) {
			if (boardState.getWinner() == this.player) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		}

		int bestScore = Integer.MAX_VALUE;
		int projectedMoveScore;
		for (BohnenspielMove move : boardState.getLegalMoves()) {
			BohnenspielBoardState clonedBoardState = (BohnenspielBoardState) boardState.clone();
			clonedBoardState.move(move);
			projectedMoveScore = maxValue(clonedBoardState);
			if (projectedMoveScore == Integer.MAX_VALUE) {
				// this move results in the min player losing --> try the next one
				continue;
			} else if (projectedMoveScore == Integer.MIN_VALUE) {
				// this move results in the min player winning
				return Integer.MIN_VALUE;
			}
			if (projectedMoveScore < bestScore) {
				bestScore = projectedMoveScore;
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
	private int maxValue(BohnenspielBoardState boardState) {
		if (boardState.gameOver()) {
			if (boardState.getWinner() == this.player) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		}

		int bestScore = Integer.MIN_VALUE;
		int projectedMoveScore;
		for (BohnenspielMove move : boardState.getLegalMoves()) {
			BohnenspielBoardState clonedBoardState = (BohnenspielBoardState) boardState.clone();
			clonedBoardState.move(move);
			projectedMoveScore = minValue(clonedBoardState);
			if (projectedMoveScore == Integer.MAX_VALUE) {
				// this move results in the max player winning --> take it
				return Integer.MAX_VALUE;
			} else if (projectedMoveScore == Integer.MIN_VALUE) {
				// this move results in the min player winning --> try a
				// different move
				continue;
			}
			if (projectedMoveScore > bestScore) {
				bestScore = projectedMoveScore;
			}
		}

		return bestScore;
	}

	// =========================================================================
	// Utility functions
	// =========================================================================

	/**
	 * The utility of a state might be the number of seeds the best move from
	 * the state can get you
	 * 
	 * @param boardState
	 * @return
	 */
	private int bestMove(BohnenspielBoardState boardState) {
		// TODO implement
		return 0;
	}
}
