package com.jstarcraft.ai.jsat.classifiers.svm;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jstarcraft.ai.jsat.FixedProblems;
import com.jstarcraft.ai.jsat.classifiers.ClassificationDataSet;
import com.jstarcraft.ai.jsat.classifiers.svm.LSSVM;
import com.jstarcraft.ai.jsat.classifiers.svm.SupportVectorLearner;
import com.jstarcraft.ai.jsat.distributions.kernels.RBFKernel;
import com.jstarcraft.ai.jsat.regression.RegressionDataSet;
import com.jstarcraft.ai.jsat.utils.random.RandomUtil;

/**
 *
 * @author Edward Raff
 */
public class LSSVMTest {
    public LSSVMTest() {
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

    /**
     * Test of train method, of class LSSVM.
     */
    @Test
    public void testTrainC_ClassificationDataSet_ExecutorService() {
        System.out.println("trainC");
        ClassificationDataSet trainSet = FixedProblems.getInnerOuterCircle(150, new Random(2));
        ClassificationDataSet testSet = FixedProblems.getInnerOuterCircle(50, new Random(3));

        for (SupportVectorLearner.CacheMode cacheMode : SupportVectorLearner.CacheMode.values()) {
            LSSVM classifier = new LSSVM(new RBFKernel(0.5), cacheMode);
            classifier.setCacheMode(cacheMode);
            classifier.setC(1);
            classifier.train(trainSet, true);

            for (int i = 0; i < testSet.size(); i++)
                assertEquals(testSet.getDataPointCategory(i), classifier.classify(testSet.getDataPoint(i)).mostLikely());
        }
    }

    /**
     * Test of train method, of class LSSVM.
     */
    @Test
    public void testTrainC_ClassificationDataSet() {
        System.out.println("trainC");
        ClassificationDataSet trainSet = FixedProblems.getInnerOuterCircle(150, new Random(2));
        ClassificationDataSet testSet = FixedProblems.getInnerOuterCircle(50, new Random(3));

        for (SupportVectorLearner.CacheMode cacheMode : SupportVectorLearner.CacheMode.values()) {
            LSSVM classifier = new LSSVM(new RBFKernel(0.5), cacheMode);
            classifier.setCacheMode(cacheMode);
            classifier.setC(1);
            classifier.train(trainSet);

            for (int i = 0; i < testSet.size(); i++)
                assertEquals(testSet.getDataPointCategory(i), classifier.classify(testSet.getDataPoint(i)).mostLikely());
        }
    }

    /**
     * Test of train method, of class LSSVM.
     */
    @Test
    public void testTrain_RegressionDataSet_ExecutorService() {
        System.out.println("train");
        RegressionDataSet trainSet = FixedProblems.getSimpleRegression1(150, new Random(2));
        RegressionDataSet testSet = FixedProblems.getSimpleRegression1(50, new Random(3));

        for (SupportVectorLearner.CacheMode cacheMode : SupportVectorLearner.CacheMode.values()) {
            LSSVM lssvm = new LSSVM(new RBFKernel(0.5), cacheMode);
            lssvm.setCacheMode(cacheMode);
            lssvm.setC(1);
            lssvm.train(trainSet, true);

            double errors = 0;
            for (int i = 0; i < testSet.size(); i++)
                errors += Math.pow(testSet.getTargetValue(i) - lssvm.regress(testSet.getDataPoint(i)), 2);
            assertTrue(errors / testSet.size() < 1);
        }
    }

    /**
     * Test of train method, of class LSSVM.
     */
    @Test
    public void testTrain_RegressionDataSet() {
        System.out.println("train");
        RegressionDataSet trainSet = FixedProblems.getSimpleRegression1(150, new Random(2));
        RegressionDataSet testSet = FixedProblems.getSimpleRegression1(50, new Random(3));

        for (SupportVectorLearner.CacheMode cacheMode : SupportVectorLearner.CacheMode.values()) {
            LSSVM lssvm = new LSSVM(new RBFKernel(0.5), cacheMode);
            lssvm.setCacheMode(cacheMode);
            lssvm.setC(1);
            lssvm.train(trainSet);

            double errors = 0;
            for (int i = 0; i < testSet.size(); i++)
                errors += Math.pow(testSet.getTargetValue(i) - lssvm.regress(testSet.getDataPoint(i)), 2);
            assertTrue(errors / testSet.size() < 1);
        }
    }

    @Test()
    public void testTrainWarmC() {
        ClassificationDataSet train = FixedProblems.getHalfCircles(100, RandomUtil.getRandom(), 0.1, 0.2);

        LSSVM warmModel = new LSSVM();
        warmModel.setC(1);
        warmModel.setCacheMode(SupportVectorLearner.CacheMode.FULL);
        warmModel.train(train);

        LSSVM warm = new LSSVM();
        warm.setC(2e1);
        warm.setCacheMode(SupportVectorLearner.CacheMode.FULL);

        long start, end;

        start = System.currentTimeMillis();
        warm.train(train, warmModel);
        end = System.currentTimeMillis();
        long warmTime = (end - start);

        LSSVM notWarm = new LSSVM();
        notWarm.setC(2e1);
        notWarm.setCacheMode(SupportVectorLearner.CacheMode.FULL);

        start = System.currentTimeMillis();
        notWarm.train(train);
        end = System.currentTimeMillis();
        long normTime = (end - start);

        assertTrue("Warm start was slower? " + warmTime + " vs " + normTime, warmTime < normTime * 1.35);

    }

    @Test()
    public void testTrainWarmR() {
        RegressionDataSet train = FixedProblems.getSimpleRegression1(75, RandomUtil.getRandom());

        LSSVM warmModel = new LSSVM();
        warmModel.setC(1);
        warmModel.setCacheMode(SupportVectorLearner.CacheMode.FULL);
        warmModel.train(train);

        LSSVM warm = new LSSVM();
        warm.setC(1e1);
        warm.setCacheMode(SupportVectorLearner.CacheMode.FULL);

        long start, end;

        start = System.currentTimeMillis();
        warm.train(train, warmModel);
        end = System.currentTimeMillis();
        long warmTime = (end - start);

        LSSVM notWarm = new LSSVM();
        notWarm.setC(1e1);
        notWarm.setCacheMode(SupportVectorLearner.CacheMode.FULL);

        start = System.currentTimeMillis();
        notWarm.train(train);
        end = System.currentTimeMillis();
        long normTime = (end - start);

        assertTrue("Warm start was slower? " + warmTime + " vs " + normTime, warmTime < normTime * 1.35);

    }

}
