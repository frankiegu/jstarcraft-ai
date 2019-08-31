package jsat.classifiers.boosting;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import jsat.FixedProblems;
import jsat.classifiers.ClassificationDataSet;
import jsat.classifiers.DataPointPair;
import jsat.classifiers.UpdateableClassifier;
import jsat.classifiers.linear.LinearSGD;
import jsat.classifiers.linear.PassiveAggressive;
import jsat.classifiers.linear.SPA;
import jsat.linear.DenseVector;
import jsat.linear.Vec;
import jsat.lossfunctions.SoftmaxLoss;
import jsat.lossfunctions.SquaredLoss;
import jsat.regression.RegressionDataSet;
import jsat.regression.UpdateableRegressor;
import jsat.utils.random.RandomUtil;

/**
 *
 * @author Edward Raff
 */
public class UpdatableStackingTest {

    public UpdatableStackingTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testClassifyBinary() {
        System.out.println("binary classifiation");

        UpdatableStacking stacking = new UpdatableStacking((UpdateableClassifier) new PassiveAggressive(), new LinearSGD(new SoftmaxLoss(), 1e-15, 0), new LinearSGD(new SoftmaxLoss(), 100, 0), new LinearSGD(new SoftmaxLoss(), 0, 100));

        ClassificationDataSet train = FixedProblems.get2ClassLinear(500, RandomUtil.getRandom());

        stacking = stacking.clone();
        stacking.train(train);
        stacking = stacking.clone();

        ClassificationDataSet test = FixedProblems.get2ClassLinear(200, RandomUtil.getRandom());

        for (DataPointPair<Integer> dpp : test.getAsDPPList())
            assertEquals(dpp.getPair().longValue(), stacking.classify(dpp.getDataPoint()).mostLikely());
    }

    @Test
    public void testClassifyMulti() {
        UpdatableStacking stacking = new UpdatableStacking(new SPA(), new LinearSGD(new SoftmaxLoss(), 1e-15, 0), new LinearSGD(new SoftmaxLoss(), 100, 0), new LinearSGD(new SoftmaxLoss(), 0, 100));

        ClassificationDataSet train = FixedProblems.getSimpleKClassLinear(500, 6, RandomUtil.getRandom());

        stacking = stacking.clone();
        stacking.train(train);
        stacking = stacking.clone();

        ClassificationDataSet test = FixedProblems.getSimpleKClassLinear(200, 6, RandomUtil.getRandom());

        for (DataPointPair<Integer> dpp : test.getAsDPPList())
            assertEquals(dpp.getPair().longValue(), stacking.classify(dpp.getDataPoint()).mostLikely());
    }

    @Test
    public void testRegression() {
        System.out.println("regression");

        Vec coef = DenseVector.toDenseVec(2.5, 1.5, 1, 0.2);

        List<UpdateableRegressor> models = new ArrayList<UpdateableRegressor>();
        LinearSGD tmp = new LinearSGD(new SquaredLoss(), 1e-15, 0);
        tmp.setUseBias(false);
        models.add(tmp.clone());
        tmp.setLambda1(0.9);
        models.add(tmp.clone());
        tmp.setLambda1(0);
        tmp.setLambda0(0.9);
        models.add(tmp.clone());

        UpdatableStacking stacking = new UpdatableStacking(new PassiveAggressive(), models);
        RegressionDataSet train = FixedProblems.getLinearRegression(15000, RandomUtil.getRandom(), coef);

        stacking = stacking.clone();
        stacking.train(train);
        stacking = stacking.clone();

        RegressionDataSet test = FixedProblems.getLinearRegression(500, RandomUtil.getRandom(), coef);

        for (DataPointPair<Double> dpp : test.getAsDPPList()) {
            double truth = dpp.getPair();
            double pred = stacking.regress(dpp.getDataPoint());
            assertEquals(0, (truth - pred), 0.3);
        }
    }

}
