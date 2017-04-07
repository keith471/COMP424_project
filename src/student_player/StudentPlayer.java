package student_player;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import bohnenspiel.BohnenspielPlayer;
import student_player.mytools.Minimax;
import student_player.mytools.MinimaxResponse;
import student_player.mytools.OptiMinimax;

/** A Hus player submitted by a student. */
public class StudentPlayer extends BohnenspielPlayer {
	
	// the maximum amount of time in milliseconds that we have to make a move
	private static final int MAX_TIME = 700;
	// the maximum amount of time in milliseconds that we have to make the first move
	private static final int MAX_TIME_FIRST_MOVE = 30000;
	private static final int BUFFER_TIME = 100;
	// cap on the maximum number of moves allowed
	private static final int MAX_MOVES = 7;

	// number of moves to begin with (a good number determined experimentally)
	private int numMovesToSimulate = 3;
	// whether or not it is the first move
	private boolean isFirstMove = true;

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
			return getFirstMove(boardState);
		}

		return getMove(boardState);
	}

	private BohnenspielMove getFirstMove(BohnenspielBoardState boardState) {
		// TODO improve: note - never a good idea to waste a skip on the first
		// move!
		Minimax mm = new Minimax(boardState.getTurnPlayer());
		MinimaxResponse mresp = mm.minimaxDecision(boardState, 8);

		return mresp.getMove();
	}

	private BohnenspielMove getMove(BohnenspielBoardState boardState) {
		Minimax mm = new Minimax(boardState.getTurnPlayer());
		long start = System.currentTimeMillis();
		MinimaxResponse mresp = mm.minimaxDecision(boardState, this.numMovesToSimulate);
		long end = System.currentTimeMillis();

		// update the number of moves to simulate
		if ((end - start) >= MAX_TIME) {
			// back-off
			this.numMovesToSimulate = Math.min(1, this.numMovesToSimulate - 2);
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
	
	private BohnenspielMove getFirstMoveOpti(BohnenspielBoardState boardState) {
		// TODO
		// finish initialization of OptiMinimax
		this.omm.setRootState(boardState);
		this.omm.setPlayer(boardState.getTurnPlayer());

		return null;
	}

	private BohnenspielMove getMoveOpti(BohnenspielBoardState boardState) {
		// TODO
		return null;
	}

	/*
	// Get the contents of the pits so we can use it to make decisions.
	int[][] pits = board_state.getPits();

	// Use ``player_id`` and ``opponent_id`` to get my pits and opponent
	// pits.
	int[] my_pits = pits[player_id];
	int[] op_pits = pits[opponent_id];

	// Use code stored in ``mytools`` package.
	MyTools.getSomething();

	// Get the legal moves for the current board state.
	ArrayList<BohnenspielMove> moves = board_state.getLegalMoves();
	BohnenspielMove move = moves.get(0);

	// We can see the effects of a move like this...
	BohnenspielBoardState cloned_board_state = (BohnenspielBoardState) board_state.clone();
	cloned_board_state.move(move);

	// But since this is a placeholder algorithm, we won't act on that
	// information.
	return move;
	*/
}