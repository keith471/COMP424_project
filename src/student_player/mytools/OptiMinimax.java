package student_player.mytools;

import java.util.HashMap;

import bohnenspiel.BohnenspielBoardState;
import bohnenspiel.BohnenspielMove;
import student_player.exceptions.InvalidDepthException;

/**
 * Minimax with memory
 * 
 * @author kstricks
 *
 */
public class OptiMinimax {

	// need to keep a reference to the root of the game tree
	private GameTreeNode root;

	// need to maintain a hashmap of <board configurations, player turn> pairs
	// GameTreeNodes in the tree
	private HashMap<String, GameTreeNode> configs;

	private int player;

	public OptiMinimax() {
		this.configs = new HashMap<String, GameTreeNode>();
	}

	public MinimaxResponse optiMinimaxDecision(BohnenspielBoardState boardState, int movesToGo) {
		if (movesToGo <= 0) {
			throw new InvalidDepthException();
		}
		
		BohnenspielMove bestMove = null;
		int bestScore = Integer.MIN_VALUE;

		boolean fullSimulation = true;

		// first, we check the tree for the node
		GameTreeNode currNode = this.getNodeCorrespondingToState(boardState);
		// NOTE: this should never be null, however it very well could be in
		// alpha beta pruning!

		// move through the tree from currNode to the leaves, keeping track
		// of depth. once at the leaves, expand for movesToGo - depth
		// additional moves
		int projectedMoveScore;
		// TODO this assumes the children are not null - what if they are?
		for (GameTreeNode n : currNode.getChildren()) {
			projectedMoveScore = optiMinValue(n, movesToGo - 1);
			if (projectedMoveScore == Integer.MAX_VALUE) {
				// this move results in us winning --> take it
				return new MinimaxResponse(n.getMove(), false, false);
			} else if (projectedMoveScore == Integer.MIN_VALUE) {
				// this move results in us losing --> move onto the next
				// move
				fullSimulation = false;
				continue;
			}
			if (projectedMoveScore > bestScore) {
				bestScore = projectedMoveScore;
				bestMove = n.getMove();
			}
		}

		return new MinimaxResponse(bestMove, fullSimulation, bestMove == null ? true : false);
	}

	private int optiMinValue(GameTreeNode node, int movesToGo) {
		// TODO implement
		return 0;
	}

	private GameTreeNode getNodeCorrespondingToState(BohnenspielBoardState boardState) {
		return this.configs.get(this.getKey(boardState));
	}

	/**
	 * Add a node directly to configs
	 * 
	 * @param node
	 */
	private void addNodeToConfigs(GameTreeNode node) {
		this.configs.put(this.getKey(node.getBoardState()), node);
	}

	/**
	 * The key to configs is a string of the following form: the number of seeds
	 * in each pit, followed by the player id
	 * 
	 * @param state
	 * @return
	 */
	private String getKey(BohnenspielBoardState state) {
		StringBuffer buff = new StringBuffer();
		int[][] pits = state.getPits();
		for (int i = 0; i < 6; i++) {
			buff.append(pits[0][i]);
			buff.append(pits[1][i]);
		}
		// to handle the fact that the same board configuration could be
		// possible on either player's turn, we add the turn to the key
		buff.append(state.getTurnPlayer());
		return buff.toString();
	}

	// GETTERS AND SETTERS
	public void setRootState(BohnenspielBoardState state) {
		this.root = new GameTreeNode(state, null, state.getTurnPlayer());
		// add the root to configs
		addNodeToConfigs(this.root);
	}

	public void setPlayer(int player) {
		this.player = player;
	}

}
