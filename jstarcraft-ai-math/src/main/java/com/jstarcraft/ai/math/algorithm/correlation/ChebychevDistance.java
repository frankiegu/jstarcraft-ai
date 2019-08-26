package com.jstarcraft.ai.math.algorithm.correlation;

import java.util.List;

import org.apache.commons.math3.util.FastMath;

import com.jstarcraft.ai.math.structure.vector.MathVector;
import com.jstarcraft.core.utility.Float2FloatKeyValue;

/**
 * 切比雪夫距离
 * 
 * @author Birdy
 *
 */
public class ChebychevDistance extends AbstractDistance {

    private float getCoefficient(int count, List<Float2FloatKeyValue> scoreList) {
        float coefficient = 0F;
        for (Float2FloatKeyValue term : scoreList) {
            float distance = term.getKey() - term.getValue();
            coefficient = Math.max(coefficient, FastMath.abs(distance));
        }
        return coefficient;
    }

    @Override
    public float getCoefficient(MathVector leftVector, MathVector rightVector, float scale) {
        // compute similarity
        List<Float2FloatKeyValue> scoreList = getScoreList(leftVector, rightVector);
        int count = scoreList.size();
        float similarity = getCoefficient(count, scoreList);
        // shrink to account for vector size
        if (!Double.isNaN(similarity)) {
            if (scale > 0) {
                similarity *= count / (count + scale);
            }
        }
        return similarity;
    }

}
