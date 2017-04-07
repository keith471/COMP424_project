package student_player.exceptions;

public class InvalidDepthException extends RuntimeException {
	public InvalidDepthException() {
		super("The depth cannot be negative");
	}
}
