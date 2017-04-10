package student_player;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielPlayer;
import student_player.mytools.AlphaBetaMinimax;
import student_player.mytools.MinimaxResponse;

/** A Hus player submitted by a student. */
public class StudentPlayer8 extends BohnenspielPlayer {

	// the maximum amount of time in milliseconds that we have to make a move
	private static final int MAX_TIME = 700;
	// the maximum amount of time in milliseconds that we have to make the first
	// move
	private static final int BUFFER_TIME = 100;
	// cap on the maximum number of moves allowed
	private static final int MAX_MOVES = 10;
	// 11 moves is sometimes too many early in the game
	private static final int EARLY_MAX_MOVES = 9;
	// we make EARLY_MAX_MOVES the maximum number of allowable moves up to
	// NUM_EARLY_MOVES moves into the game
	private static final int NUM_EARLY_MOVES = 6;
	// the number of moves to take in the initial move
	private static final int INITIAL_MOVES = 10;

	// the number of moves we've made so far
	private int numMovesMade = 0;
	// number of moves to begin with (a good number determined experimentally)
	private int numMovesToSimulate = 9;

	// the player that we are (0 or 1)
	private int player;

	/**
	 * You must modify this constructor to return your student number. This is
	 * important, because this is what the code that runs the competition uses
	 * to associate you with your agent. The constructor should do nothing else.
	 */
	public StudentPlayer8() {
		super("260674699_8");
	}

	/**
	 * Get a move to play. If this is the first move, then we have a bit more
	 * time and so we use a slightly different approach than if it is a
	 * subsequent move.
	 */
	@Override
	public BohnenspielMove chooseMove(BohnenspielBoardState boardState) {
		if (this.numMovesMade == 0) {
			// get the first move as determined by minimax with alpha-beta
			// pruning
			this.player = boardState.getTurnPlayer();
			return getFirstMoveAB(boardState);
		}

		// get subsequent moves as determined by minimax with alph-beta pruning
		return getMoveAB(boardState);
	}

	// =================================================================================
	// Minimax with alpha-beta pruning
	// =================================================================================

	/**
	 * Returns the first move to play, determined by minimax with alpha-beta
	 * pruning
	 * 
	 * @param boardState
	 * @return
	 */
	private BohnenspielMove getFirstMoveAB(BohnenspielBoardState boardState) {
		AlphaBetaMinimax abmm = new AlphaBetaMinimax(this.player, 7);
		long start = System.currentTimeMillis();
		MinimaxResponse mresp = abmm.minimaxDecision(boardState, INITIAL_MOVES);
		long end = System.currentTimeMillis();
		System.out.println("First move computed in " + (end - start) + " milliseconds");
		this.numMovesMade++;
		return mresp.getMove();
	}

	/**
	 * Returns a move to play, determined by minimax with alpha-beta pruning
	 * 
	 * @param boardState
	 * @return
	 */
	private BohnenspielMove getMoveAB(BohnenspielBoardState boardState) {
		AlphaBetaMinimax abmm = new AlphaBetaMinimax(this.player, 7);
		long start = System.currentTimeMillis();
		MinimaxResponse mresp = abmm.minimaxDecision(boardState, this.numMovesToSimulate);
		long end = System.currentTimeMillis();
		System.out.println("simulated " + this.numMovesToSimulate + " moves in " + (end - start) + " milliseconds");

		updateMovesToSimulate(end - start);

		// if Minimax says we should skip, then try to skip
		if (mresp.getShouldSkip()) {
			if (boardState.getCredit(this.player) > 0) {
				return new BohnenspielMove("skip", this.player);
			} else {
				// try a random move and hope for the best
				return (BohnenspielMove) boardState.getRandomMove();
			}
		}

		this.numMovesMade++;
		return mresp.getMove();
	}

	private void updateMovesToSimulate(long time) {
		if ((time) >= MAX_TIME) {
			// exponential back-off
			this.numMovesToSimulate = Math.max(1, this.numMovesToSimulate / 2);
		} else if (this.numMovesMade < NUM_EARLY_MOVES) {
			if (this.numMovesToSimulate < EARLY_MAX_MOVES && (time + BUFFER_TIME) < MAX_TIME) {
				this.numMovesToSimulate++;
			}
		} else if (this.numMovesToSimulate < MAX_MOVES && (time + BUFFER_TIME) < MAX_TIME) {
			this.numMovesToSimulate++;
		}
	}
}