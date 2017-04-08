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

		int projectedMoveScore;
		boolean fullSimulation = true;

		// first, we check the tree for the node
		// NOTE: this should never be null, however it very well could be in
		// alpha beta pruning!
		GameTreeNode currNode = this.getNodeCorrespondingToState(boardState);

		// it is possible that currNode is null (if we didn't fully expand the
		// tree along all paths in a previous step)
		if (currNode == null) {
			// we don't need to know the move taken to get here, nor the parent
			// node, since we will not visit this node in the tree again
			currNode = new GameTreeNode(boardState, null);
			this.addNodeToConfigs(currNode);
		}

		// if the children of the current node are null, then we perform regular
		// minimax
		if (currNode.getChildren() == null) {
			for (BohnenspielMove move : boardState.getLegalMoves()) {
				BohnenspielBoardState clonedBoardState = (BohnenspielBoardState) boardState.clone();
				clonedBoardState.move(move);
				// create a new GameTreeNode for the state
				GameTreeNode n = new GameTreeNode(clonedBoardState, move);
				// make this node a child of currNode
				currNode.addChild(n);
				// save the node in the map
				this.configs.put(this.getKey(clonedBoardState), n);
				// compute the score for traversing this path
				projectedMoveScore = minValue(n, clonedBoardState, movesToGo - 1);
				if (projectedMoveScore == Integer.MAX_VALUE) {
					// this move results in us winning --> take it
					return new MinimaxResponse(move, false, false);
				} else if (projectedMoveScore == Integer.MIN_VALUE) {
					// this move results in us losing --> move onto the next
					// move
					fullSimulation = false;
					continue;
				}
				if (projectedMoveScore > bestScore) {
					bestScore = projectedMoveScore;
					bestMove = move;
				}
			}
		} else {
			// move through the tree from currNode to the leaves, keeping track
			// of depth. once at the leaves, expand for movesToGo - depth
			// additional moves
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
		}

		return new MinimaxResponse(bestMove, fullSimulation, bestMove == null ? true : false);
	}

	/**
	 * Like minValue in regular minimax except that we create GameTreeNodes for
	 * each new state and save them in the map
	 * 
	 * @param boardState
	 * @param movesToGo
	 * @return
	 */
	private int minValue(GameTreeNode parent, BohnenspielBoardState boardState, int movesToGo) {
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
			GameTreeNode n = new GameTreeNode(clonedBoardState, move);
			parent.addChild(n);
			this.configs.put(this.getKey(clonedBoardState), n);
			projectedMoveScore = maxValue(n, clonedBoardState, movesToGo - 1);
			if (projectedMoveScore == Integer.MAX_VALUE) {
				// this move results in the min player losing --> try the next
				// one
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

	private int maxValue(GameTreeNode parent, BohnenspielBoardState boardState, int movesToGo) {
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
			GameTreeNode n = new GameTreeNode(clonedBoardState, move);
			parent.addChild(n);
			this.configs.put(this.getKey(clonedBoardState), n);
			projectedMoveScore = minValue(n, clonedBoardState, movesToGo - 1);
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

	private int optiMinValue(GameTreeNode node, int movesToGo) {
		if (node.getBoardState().gameOver()) {
			if (node.getBoardState().getWinner() == this.player) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		} else if (movesToGo == 0) {
			return getUtility(node.getBoardState());
		}

		int bestScore = Integer.MAX_VALUE;

		int projectedMoveScore;

		// if the children of the node are null, then we perform regular minimax
		if (node.getChildren() == null) {
			for (BohnenspielMove move : node.getBoardState().getLegalMoves()) {
				BohnenspielBoardState clonedBoardState = (BohnenspielBoardState) node.getBoardState().clone();
				clonedBoardState.move(move);
				// create a new GameTreeNode for the state
				GameTreeNode n = new GameTreeNode(clonedBoardState, move);
				// make this node a child of the node
				node.addChild(n);
				// save the node in the map
				this.configs.put(this.getKey(clonedBoardState), n);
				// compute the score for traversing this path
				projectedMoveScore = maxValue(n, clonedBoardState, movesToGo - 1);
				if (projectedMoveScore == Integer.MAX_VALUE) {
					// this move results in the min player losing
					continue;
				} else if (projectedMoveScore == Integer.MIN_VALUE) {
					// this move results in the min player winning
					return Integer.MIN_VALUE;
				}
				if (projectedMoveScore < bestScore) {
					bestScore = projectedMoveScore;
				}
			}
		} else {
			for (GameTreeNode n : node.getChildren()) {
				projectedMoveScore = optiMaxValue(n, movesToGo - 1);
				if (projectedMoveScore == Integer.MAX_VALUE) {
					// this move results in the min player losing
					continue;
				} else if (projectedMoveScore == Integer.MIN_VALUE) {
					// this move results in the min player winning
					return Integer.MIN_VALUE;
				}
				if (projectedMoveScore < bestScore) {
					bestScore = projectedMoveScore;
				}
			}
		}

		return bestScore;
	}

	/**
	 * We know node is not null, but we don't know whether or not we have
	 * already computed node's children or not
	 * 
	 * @param node
	 * @param movesToGo
	 * @return
	 */
	private int optiMaxValue(GameTreeNode node, int movesToGo) {
		if (node.getBoardState().gameOver()) {
			if (node.getBoardState().getWinner() == this.player) {
				return Integer.MAX_VALUE;
			} else {
				return Integer.MIN_VALUE;
			}
		} else if (movesToGo == 0) {
			return getUtility(node.getBoardState());
		}

		int bestScore = Integer.MIN_VALUE;

		int projectedMoveScore;

		// if the children of the node are null, then we perform regular minimax
		if (node.getChildren() == null) {
			for (BohnenspielMove move : node.getBoardState().getLegalMoves()) {
				BohnenspielBoardState clonedBoardState = (BohnenspielBoardState) node.getBoardState().clone();
				clonedBoardState.move(move);
				// create a new GameTreeNode for the state
				GameTreeNode n = new GameTreeNode(clonedBoardState, move);
				// make this node a child of the node
				node.addChild(n);
				// save the node in the map
				this.configs.put(this.getKey(clonedBoardState), n);
				// compute the score for traversing this path
				projectedMoveScore = minValue(n, clonedBoardState, movesToGo - 1);
				if (projectedMoveScore == Integer.MAX_VALUE) {
					// this move results in us winning --> take it
					return Integer.MAX_VALUE;
				} else if (projectedMoveScore == Integer.MIN_VALUE) {
					// this move results in us losing --> move onto the next
					// move
					continue;
				}
				if (projectedMoveScore > bestScore) {
					bestScore = projectedMoveScore;
				}
			}
		} else {
			for (GameTreeNode n : node.getChildren()) {
				projectedMoveScore = optiMinValue(n, movesToGo - 1);
				if (projectedMoveScore == Integer.MAX_VALUE) {
					// this move results in us winning --> take it
					return Integer.MAX_VALUE;
				} else if (projectedMoveScore == Integer.MIN_VALUE) {
					// this move results in us losing --> move onto the next
					continue;
				}
				if (projectedMoveScore > bestScore) {
					bestScore = projectedMoveScore;
				}
			}
		}

		return bestScore;
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
			if (pits[0][i] < 10) {
				buff.append('0');
			}
			buff.append(pits[0][i]);
			if (pits[1][i] < 10) {
				buff.append('0');
			}
			buff.append(pits[1][i]);
		}
		// to handle the fact that the same board configuration could be
		// possible on either player's turn, we add the turn to the key
		buff.append(state.getTurnPlayer());
		return buff.toString();
	}

	private int getUtility(BohnenspielBoardState boardState) {
		return scoreDifference(boardState);
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

	// GETTERS AND SETTERS

	public void setRootState(BohnenspielBoardState state) {
		this.root = new GameTreeNode(state, null);
		// add the root to configs
		this.addNodeToConfigs(this.root);
	}

	public void setPlayer(int player) {
		this.player = player;
	}

}
