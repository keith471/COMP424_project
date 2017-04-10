package student_player;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielPlayer;
import student_player.mytools.AlphaBetaMinimax;
import student_player.mytools.MinimaxResponse;
import student_player.mytools.OptiMinimax;

/** A Hus player submitted by a student. */
public class StudentPlayer2 extends BohnenspielPlayer {

	// the maximum amount of time in milliseconds that we have to make a move
	private static final int MAX_TIME = 700;
	// the maximum amount of time in milliseconds that we have to make the first
	// move
	private static final int MAX_TIME_FIRST_MOVE = 30000;
	private static final int BUFFER_TIME = 100;
	// cap on the maximum number of moves allowed
	private static final int MAX_MOVES = 15;

	// number of moves to begin with (a good number determined experimentally)
	private int numMovesToSimulate = 9;
	// whether or not it is the first move
	private boolean isFirstMove = true;

	// store the OptiMinimax object
	private final OptiMinimax omm = new OptiMinimax();

	/**
	 * You must modify this constructor to return your student number. This is
	 * important, because this is what the code that runs the competition uses
	 * to associate you with your agent. The constructor should do nothing else.
	 */
	public StudentPlayer2() {
		super("260674699_2");
	}

	/**
	 * This is the primary method that you need to implement. The
	 * ``board_state`` object contains the current state of the game, which your
	 * agent can use to make decisions. See the class bohnenspiel.RandomPlayer
	 * for another example agent.
	 */
	@Override
	public BohnenspielMove chooseMove(BohnenspielBoardState boardState) {
		// minimax
		if (this.isFirstMove) {
			this.isFirstMove = false;
			return getFirstMoveAB(boardState);
		}

		return getMoveAB(boardState);
	}

	// =================================================================================
	// Minimax with alpha-beta pruning
	// =================================================================================

	private BohnenspielMove getFirstMoveAB(BohnenspielBoardState boardState) {
		// TODO improve
		AlphaBetaMinimax abmm = new AlphaBetaMinimax(boardState.getTurnPlayer(), "scoreAndBeanDifference");
		MinimaxResponse mresp = abmm.minimaxDecision(boardState, 10);

		// don't waste a skip on the first move
		/*
		if (mresp.getMove().getMoveType() == MoveType.SKIP) {
			return (BohnenspielMove) boardState.getRandomMove();
		}
		*/

		return mresp.getMove();
	}

	private BohnenspielMove getMoveAB(BohnenspielBoardState boardState) {
		AlphaBetaMinimax abmm = new AlphaBetaMinimax(boardState.getTurnPlayer(), "scoreAndBeanDifference");
		long start = System.currentTimeMillis();
		MinimaxResponse mresp = abmm.minimaxDecision(boardState, this.numMovesToSimulate);
		long end = System.currentTimeMillis();
		// System.out.println("simulated " + this.numMovesToSimulate + " moves
		// in " + (end - start) + " milliseconds");

		// update the number of moves to simulate
		/*
		 * if ((end - start) >= MAX_TIME) { // exponential back-off
		 * this.numMovesToSimulate = Math.max(1, this.numMovesToSimulate / 2); }
		 * else if (this.numMovesToSimulate < MAX_MOVES && (end - start +
		 * BUFFER_TIME) < MAX_TIME) { this.numMovesToSimulate++; }
		 */

		// if Minimax says we should skip, then try to skip
		if (mresp.getShouldSkip()) {
			if (boardState.getCredit(boardState.getTurnPlayer()) > 0) {
				return new BohnenspielMove("skip", boardState.getTurnPlayer());
			} else {
				// try a random move and hope for the best
				return (BohnenspielMove) boardState.getRandomMove();
			}
		}

		return mresp.getMove();
	}
}