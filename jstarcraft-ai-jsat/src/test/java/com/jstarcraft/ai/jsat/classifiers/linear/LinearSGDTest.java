/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jstarcraft.ai.jsat.classifiers.linear;

import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jstarcraft.ai.jsat.FixedProblems;
import com.jstarcraft.ai.jsat.TestTools;
import com.jstarcraft.ai.jsat.classifiers.ClassificationDataSet;
import com.jstarcraft.ai.jsat.classifiers.DataPointPair;
import com.jstarcraft.ai.jsat.classifiers.linear.LinearSGD;
import com.jstarcraft.ai.jsat.lossfunctions.HingeLoss;
import com.jstarcraft.ai.jsat.lossfunctions.SquaredLoss;
import com.jstarcraft.ai.jsat.math.optimization.stochastic.AdaGrad;
import com.jstarcraft.ai.jsat.math.optimization.stochastic.GradientUpdater;
import com.jstarcraft.ai.jsat.math.optimization.stochastic.RMSProp;
import com.jstarcraft.ai.jsat.math.optimization.stochastic.SimpleSGD;
import com.jstarcraft.ai.jsat.regression.RegressionDataSet;
import com.jstarcraft.ai.jsat.utils.random.RandomUtil;

import static org.junit.Assert.*;

/**
 *
 * @author Edward Raff
 */
public class LinearSGDTest {

    public LinearSGDTest() {
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

    static boolean[] useBiasOptions = new boolean[] { true, false };
    static GradientUpdater[] updaters = new GradientUpdater[] { new SimpleSGD(), new AdaGrad(), new RMSProp() };

    @Test
    public void testClassifyBinary() {
        System.out.println("binary classifiation");

        for (boolean useBias : useBiasOptions) {
            for (GradientUpdater gu : updaters) {
                LinearSGD linearsgd = new LinearSGD(new HingeLoss(), 1e-4, 1e-5);
                linearsgd.setUseBias(useBias);
                linearsgd.setGradientUpdater(gu);

                ClassificationDataSet train = FixedProblems.get2ClassLinear(500, RandomUtil.getRandom());

                linearsgd.train(train);

                ClassificationDataSet test = FixedProblems.get2ClassLinear(200, RandomUtil.getRandom());

                for (DataPointPair<Integer> dpp : test.getAsDPPList())
                    assertEquals(dpp.getPair().longValue(), linearsgd.classify(dpp.getDataPoint()).mostLikely());
            }
        }
    }

    @Test
    public void testClassifyMulti() {
        System.out.println("multi class classification");
        for (boolean useBias : useBiasOptions) {
            for (GradientUpdater gu : updaters) {
                LinearSGD linearsgd = new LinearSGD(new HingeLoss(), 1e-4, 1e-5);
                linearsgd.setUseBias(useBias);
                linearsgd.setGradientUpdater(gu);

                ClassificationDataSet train = FixedProblems.getSimpleKClassLinear(500, 6, RandomUtil.getRandom());

                linearsgd.train(train);

                ClassificationDataSet test = FixedProblems.getSimpleKClassLinear(200, 6, RandomUtil.getRandom());

                for (DataPointPair<Integer> dpp : test.getAsDPPList())
                    assertEquals(dpp.getPair().longValue(), linearsgd.classify(dpp.getDataPoint()).mostLikely());
            }
        }
    }

    @Test
    public void testRegression() {
        System.out.println("regression");
        for (boolean useBias : useBiasOptions) {
            for (GradientUpdater gu : updaters) {
                LinearSGD linearsgd = new LinearSGD(new SquaredLoss(), 0.0, 0.0);
                linearsgd.setUseBias(useBias);
                linearsgd.setGradientUpdater(gu);

                // SGD needs more iterations/data to learn a really close fit
                linearsgd.setEpochs(50);
                if (!(gu instanceof SimpleSGD))// the others need a higher learning rate than the default
                {
                    linearsgd.setEta(0.5);
                    linearsgd.setEpochs(100);// more iters b/c RMSProp probably isn't the best for this overly simple problem
                }

                int tries = 4;
                do {
                    if (TestTools.regressEvalLinear(linearsgd, 10000, 200))
                        break;
                } while (tries-- > 0);
                assertTrue(tries > 0);

            }
        }
    }
}