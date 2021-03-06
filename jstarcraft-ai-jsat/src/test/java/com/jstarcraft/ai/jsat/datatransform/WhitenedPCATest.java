/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jstarcraft.ai.jsat.datatransform;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jstarcraft.ai.jsat.SimpleDataSet;
import com.jstarcraft.ai.jsat.classifiers.CategoricalData;
import com.jstarcraft.ai.jsat.classifiers.DataPoint;
import com.jstarcraft.ai.jsat.datatransform.DataTransform;
import com.jstarcraft.ai.jsat.datatransform.WhitenedPCA;
import com.jstarcraft.ai.jsat.distributions.multivariate.NormalM;
import com.jstarcraft.ai.jsat.linear.DenseMatrix;
import com.jstarcraft.ai.jsat.linear.DenseVector;
import com.jstarcraft.ai.jsat.linear.Matrix;
import com.jstarcraft.ai.jsat.linear.MatrixStatistics;
import com.jstarcraft.ai.jsat.linear.Vec;

/**
 *
 * @author Edward Raff
 */
public class WhitenedPCATest {

    public WhitenedPCATest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of setUpTransform method, of class WhitenedPCA.
     */
    @Test
    public void testTransform() {
        System.out.println("testTransform");
        NormalM normal = new NormalM(new DenseVector(3), new DenseMatrix(new double[][] { { 133.138, -57.278, 40.250 }, { -57.278, 25.056, -17.500 }, { 40.250, -17.500, 12.250 }, }));

        List<Vec> sample = normal.sample(500, new Random(17));
        List<DataPoint> dataPoints = new ArrayList<DataPoint>(sample.size());
        for (Vec v : sample)
            dataPoints.add(new DataPoint(v, new int[0], new CategoricalData[0]));

        SimpleDataSet data = new SimpleDataSet(dataPoints);

        DataTransform transform = new WhitenedPCA(data, 0.0);

        data.applyTransform(transform);

        Matrix whiteCov = MatrixStatistics.covarianceMatrix(MatrixStatistics.meanVector(data), data);

        assertTrue(Matrix.eye(3).equals(whiteCov, 1e-8));
    }

}
