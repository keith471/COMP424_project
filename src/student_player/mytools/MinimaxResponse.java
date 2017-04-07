package student_player.mytools;

import bohnenspiel.BohnenspielMove;

public class MinimaxResponse {

	private final BohnenspielMove move;
	private final boolean fullSimulation;
	private final boolean shouldSkip;

	public MinimaxResponse(BohnenspielMove move, boolean fullSimulation, boolean shouldSkip) {
		this.move = move;
		this.fullSimulation = fullSimulation;
		this.shouldSkip = shouldSkip;
	}

	public BohnenspielMove getMove() {
		return this.move;
	}

	public boolean getFullSimulation() {
		return this.fullSimulation;
	}

	public boolean getShouldSkip() {
		return this.shouldSkip;
	}

}
