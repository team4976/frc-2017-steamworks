package ca._4976.library;

class Evaluator {

	final Evaluable evaluable;
	final long delay;

	Evaluator(Evaluable evaluable, long delay) {

		this.evaluable = evaluable;
		this.delay = delay;
	}
}
