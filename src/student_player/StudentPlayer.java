package student_player;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielMove.MoveType;
import bohnenspiel.BohnenspielPlayer;
import student_player.mytools.AlphaBetaMinimax;
import student_player.mytools.Minimax;
import student_player.mytools.MinimaxResponse;
import student_player.mytools.OptiMinimax;

/** A Hus player submitted by a student. */
public class StudentPlayer extends BohnenspielPlayer {
	
	// the maximum amount of time in milliseconds that we have to make a move
	private static final int MAX_TIME = 700;
	// the maximum amount of time in milliseconds that we have to make the first move
	private static final int BUFFER_TIME = 100;
	// cap on the maximum number of moves allowed
	private static final int MAX_MOVES = 10;
	// 11 moves is sometimes too many early in the game
	private static final int EARLY_MAX_MOVES = 10;
	// we make EARLY_MAX_MOVES the maximum number of allowable moves up to
	// NUM_EARLY_MOVES moves into the game
	private static final int NUM_EARLY_MOVES = 8;
	// the number of moves to take in the initial move
	private static final int INITIAL_MOVES = 10;

	private int numMovesMade = 0;
	// number of moves to begin with (a good number determined experimentally)
	private int numMovesToSimulate = 9;

	// store the OptiMinimax object
	private final OptiMinimax omm = new OptiMinimax();

	/**
	 * You must modify this constructor to return your student number. This is
	 * important, because this is what the code that runs the competition uses
	 * to associate you with your agent. The constructor should do nothing else.
	 */
	public StudentPlayer() {
		super("260674699");
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
		AlphaBetaMinimax abmm = new AlphaBetaMinimax(boardState.getTurnPlayer(), 4);
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
		AlphaBetaMinimax abmm = new AlphaBetaMinimax(boardState.getTurnPlayer(), 4, this.numMovesMade);
		long start = System.currentTimeMillis();
		MinimaxResponse mresp = abmm.minimaxDecision(boardState, this.numMovesToSimulate);
		long end = System.currentTimeMillis();
		System.out.println("simulated " + this.numMovesToSimulate + " moves in " + (end - start) + " milliseconds");

		// update the number of moves to simulate
		/*
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

		this.numMovesMade++;
		return mresp.getMove();
	}
	
	// =================================================================================
	// Minimax with no pruning
	// =================================================================================

	/**
	 * Returns the first move to play, determined by minimax
	 * 
	 * @param boardState
	 * @return
	 */
	private BohnenspielMove getFirstMove(BohnenspielBoardState boardState) {
		// TODO improve
		Minimax mm = new Minimax(boardState.getTurnPlayer());
		MinimaxResponse mresp = mm.minimaxDecision(boardState, INITIAL_MOVES);

		// don't waste a skip on the first move
		/*
		if (mresp.getMove().getMoveType() == MoveType.SKIP) {
			return (BohnenspielMove) boardState.getRandomMove();
		}
		*/

		return mresp.getMove();
	}

	/**
	 * Returns a move to play, determined by minimax
	 * 
	 * @param boardState
	 * @return
	 */
	private BohnenspielMove getMove(BohnenspielBoardState boardState) {
		Minimax mm = new Minimax(boardState.getTurnPlayer());
		long start = System.currentTimeMillis();
		MinimaxResponse mresp = mm.minimaxDecision(boardState, this.numMovesToSimulate);
		long end = System.currentTimeMillis();

		// update the number of moves to simulate
		if ((end - start) >= MAX_TIME) {
			// back-off
			this.numMovesToSimulate = Math.max(1, this.numMovesToSimulate - 2);
		} else if (this.numMovesToSimulate < MAX_MOVES && mresp.getFullSimulation()) {
			// potentially increase the number of moves to simulate
			if ((end - start + BUFFER_TIME) < MAX_TIME) {
				this.numMovesToSimulate++;
			}
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

		return mresp.getMove();
	}

	// =================================================================================
	// Minimax using memory to store the game tree
	// =================================================================================

	/**
	 * Returns the first move to play, determined by minimax. Saves the game
	 * tree to memory.
	 * 
	 * @param boardState
	 * @return
	 */
	private BohnenspielMove getFirstMoveOpti(BohnenspielBoardState boardState) {
		// finish initialization of OptiMinimax
		this.omm.setRootState(boardState);
		this.omm.setPlayer(boardState.getTurnPlayer());

		MinimaxResponse mresp = this.omm.optiMinimaxDecision(boardState, INITIAL_MOVES);

		// don't waste a skip on the first move
		if (mresp.getMove().getMoveType() == MoveType.SKIP) {
			return (BohnenspielMove) boardState.getRandomMove();
		}

		return mresp.getMove();
	}

	/**
	 * Returns a move to play, determined by minimax. Traverses through the game
	 * tree in memory and adds to it as needed.
	 * 
	 * @param boardState
	 * @return
	 */
	private BohnenspielMove getMoveOpti(BohnenspielBoardState boardState) {
		long start = System.currentTimeMillis();
		MinimaxResponse mresp = this.omm.optiMinimaxDecision(boardState, this.numMovesToSimulate);
		long end = System.currentTimeMillis();

		// update the number of moves to simulate
		if ((end - start) >= MAX_TIME) {
			// exponential back-off
			this.numMovesToSimulate = Math.max(1, this.numMovesToSimulate / 2);
		} else if (this.numMovesToSimulate < MAX_MOVES && (end - start + BUFFER_TIME) < MAX_TIME) {
			this.numMovesToSimulate++;
		}

		// if Minimax says we should skip, then try to skip
		if (mresp.getShouldSkip()) {
			if (boardState.getCredit(boardState.getTurnPlayer()) > 0) {
				return new BohnenspielMove("skip", boardState.getTurnPlayer());
			} else {
				// try a random move and hope for the best
				System.out.println("says should skip");
				return (BohnenspielMove) boardState.getRandomMove();
			}
		}

		return mresp.getMove();
	}
}