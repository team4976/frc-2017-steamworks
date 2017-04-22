package ca._4976.library;

public class Evaluator {

	public final Evaluable evaluable;
	public final long delay;

	public Evaluator(Evaluable evaluable, long delay) {

		this.evaluable = evaluable;
		this.delay = delay;
	}
}
