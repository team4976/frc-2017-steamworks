package ca._4976.library;

class Evaluator {

    final Evaluable evaluable;
    final long delayedTime;

    Evaluator(Evaluable evaluable, long delayedTime) {

        this.evaluable = evaluable;
        this.delayedTime = delayedTime;
    }
}