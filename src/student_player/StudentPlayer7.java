package student_player;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielPlayer;
import student_player.mytools.AlphaBetaMinimax;
import student_player.mytools.MinimaxResponse;

/** A Hus player submitted by a student. */
public class StudentPlayer7 extends BohnenspielPlayer {

	// the maximum amount of time in milliseconds that we have to make a move
	private static final int MAX_TIME = 700;
	// the maximum amount of time in milliseconds that we have to make the first
	// move
	private static final int BUFFER_TIME = 100;
	// cap on the maximum number of moves allowed
	private static final int MAX_MOVES = 11;
	// 11 moves is sometimes too many early in the game
	private static final int EARLY_MAX_MOVES = 10;
	// we make EARLY_MAX_MOVES the maximum number of allowable moves up to
	// NUM_EARLY_MOVES moves into the game
	private static final int NUM_EARLY_MOVES = 10;
	// the number of moves to take in the initial move
	private static final int INITIAL_MOVES = 10;

	private int numMovesMade = 0;
	// number of moves to begin with (a good number determined experimentally)
	private int numMovesToSimulate = 9;

	/**
	 * You must modify this constructor to return your student number. This is
	 * important, because this is what the code that runs the competition uses
	 * to associate you with your agent. The constructor should do nothing else.
	 */
	public StudentPlayer7() {
		super("260674699_7");
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
		if (this.numMovesMade == 0) {
			return getFirstMoveAB(boardState);
		}

		return getMoveAB(boardState);
	}

	// =================================================================================
	// Minimax with alpha-beta pruning
	// =================================================================================

	private BohnenspielMove getFirstMoveAB(BohnenspielBoardState boardState) {
		AlphaBetaMinimax abmm = new AlphaBetaMinimax(boardState.getTurnPlayer(), 6);
		long start = System.currentTimeMillis();
		MinimaxResponse mresp = abmm.minimaxDecision(boardState, INITIAL_MOVES);
		long end = System.currentTimeMillis();
		System.out.println("First move computed in " + (end - start) + " milliseconds");
		this.numMovesMade++;
		return mresp.getMove();
	}

	private BohnenspielMove getMoveAB(BohnenspielBoardState boardState) {
		AlphaBetaMinimax abmm = new AlphaBetaMinimax(boardState.getTurnPlayer(), 6);
		long start = System.currentTimeMillis();
		MinimaxResponse mresp = abmm.minimaxDecision(boardState, this.numMovesToSimulate);
		long end = System.currentTimeMillis();
		System.out.println("simulated " + this.numMovesToSimulate + " moves in " + (end - start) + " milliseconds");

		if ((end - start) >= MAX_TIME) {
			// exponential back-off
			this.numMovesToSimulate = Math.max(1, this.numMovesToSimulate / 2);
		} else if (this.numMovesMade < NUM_EARLY_MOVES) {
			if (this.numMovesToSimulate < EARLY_MAX_MOVES && (end - start + BUFFER_TIME) < MAX_TIME) {
				this.numMovesToSimulate++;
			}
		} else if (this.numMovesToSimulate < MAX_MOVES && (end - start + BUFFER_TIME) < MAX_TIME) {
			this.numMovesToSimulate++;
		}

		// if Minimax says we should skip, then try to skip
		if (mresp.getShouldSkip()) {
			if (boardState.getCredit(boardState.getTurnPlayer()) > 0) {
				return new BohnenspielMove("skip", boardState.getTurnPlayer());
			} else {
				// try a random move and hope for the best
				return (BohnenspielMove) boardState.getRandomMove();
			}
		}

		this.numMovesMade++;
		return mresp.getMove();
	}
}