package student_player.mytools;

import java.util.ArrayList;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;

/**
 * A node in the game tree
 * 
 * @author kstricks
 *
 */
public class GameTreeNode {

	// has the state
	private final BohnenspielBoardState boardState;

	// has the move used to get to this state (only null for the root of the
	// tree)
	private final BohnenspielMove move;

	// has the player whose turn it is
	private final int player;

	// has the child board states
	private ArrayList<GameTreeNode> children;

	public GameTreeNode(BohnenspielBoardState boardState, BohnenspielMove move, int player) {
		this.boardState = boardState;
		this.move = move;
		this.player = player;
		this.children = new ArrayList<GameTreeNode>();
	}

	public BohnenspielBoardState getBoardState() {
		return this.boardState;
	}

	public BohnenspielMove getMove() {
		return this.move;
	}

	public int getPlayer() {
		return this.player;
	}

	public ArrayList<GameTreeNode> getChildren() {
		return this.children;
	}

}
