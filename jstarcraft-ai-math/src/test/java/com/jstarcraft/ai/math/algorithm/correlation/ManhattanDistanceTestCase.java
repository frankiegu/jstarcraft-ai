package com.jstarcraft.ai.math.algorithm.correlation;

public class ManhattanDistanceTestCase extends AbstractDistanceTestCase {

    @Override
    protected Correlation getCorrelation() {
        return new ManhattanDistance();
    }

}
