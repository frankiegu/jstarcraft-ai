/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.jstarcraft.ai.jsat.clustering.kmeans;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.jstarcraft.ai.jsat.FixedProblems;
import com.jstarcraft.ai.jsat.classifiers.ClassificationDataSet;
import com.jstarcraft.ai.jsat.distributions.Uniform;
import com.jstarcraft.ai.jsat.distributions.kernels.LinearKernel;
import com.jstarcraft.ai.jsat.distributions.kernels.RBFKernel;
import com.jstarcraft.ai.jsat.utils.GridDataGenerator;
import com.jstarcraft.ai.jsat.utils.random.RandomUtil;
import com.jstarcraft.ai.jsat.utils.random.XORWOW;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;

/**
 *
 * @author Edward Raff
 */
public class ElkanKernelKMeansTest {
    public ElkanKernelKMeansTest() {
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
     * Test of cluster method, of class ElkanKernelKMeans.
     */
    @Test
    public void testCluster_4args() {
        System.out.println("cluster");
        ElkanKernelKMeans kmeans = new ElkanKernelKMeans(new RBFKernel(0.1));
        ClassificationDataSet toCluster = FixedProblems.getCircles(1000, RandomUtil.getRandom(), 1e-3, 1.0);
        int[] result = kmeans.cluster(toCluster, 2, true, (int[]) null);
        // make sure each cluster has points from only 1 class. If true then everyone is
        // good
        Map<Integer, IntOpenHashSet> tmp = new HashMap<>();
        for (int c = 0; c < toCluster.getClassSize(); c++)
            tmp.put(c, new IntOpenHashSet());
        for (int i = 0; i < result.length; i++)
            tmp.get(toCluster.getDataPointCategory(i)).add(result[i]);
        for (Set<Integer> set : tmp.values())
            assertEquals(1, set.size());
    }

    /**
     * Test of cluster method, of class ElkanKernelKMeans.
     */
    @Test
    public void testCluster_3args() {
        System.out.println("cluster");
        ElkanKernelKMeans kmeans = new ElkanKernelKMeans(new RBFKernel(0.1));
        ClassificationDataSet toCluster = FixedProblems.getCircles(1000, RandomUtil.getRandom(), 1e-3, 1.0);
        int[] result = kmeans.cluster(toCluster, 2, (int[]) null);
        // make sure each cluster has points from only 1 class. If true then everyone is
        // good
        Map<Integer, IntOpenHashSet> tmp = new HashMap<>();
        for (int c = 0; c < toCluster.getClassSize(); c++)
            tmp.put(c, new IntOpenHashSet());
        for (int i = 0; i < result.length; i++)
            tmp.get(toCluster.getDataPointCategory(i)).add(result[i]);
        for (Set<Integer> set : tmp.values())
            assertEquals(1, set.size());
    }

    @Test
    public void testCluster_Weighted() {
        System.out.println("cluster(dataset, int, threadpool)");
        LloydKernelKMeans kmeans = new LloydKernelKMeans(new LinearKernel());
        GridDataGenerator gdg = new GridDataGenerator(new Uniform(-0.15, 0.15), new XORWOW(1238962356), 2);
        ClassificationDataSet toCluster = gdg.generateData(200).asClassificationDataSet(0);
        // make the LAST data point so far out it will screw everything up, UNLCESS you
        // understand that it has a tiny weight
        toCluster.getDataPoint(toCluster.size() - 1).getNumericalValues().set(0, 1.9e100);
        Random rand = new XORWOW(897654);
        for (int i = 0; i < toCluster.size(); i++)
            toCluster.setWeight(i, 0.5 + 5 * rand.nextDouble());
        toCluster.setWeight(toCluster.size() - 1, 1e-200);

        int[] result = kmeans.cluster(toCluster, 2, (int[]) null);
        // make sure each cluster has points from only 1 class. If true then everyone is
        // good
        Map<Integer, IntOpenHashSet> tmp = new HashMap<>();
        IntOpenHashSet allSeen = new IntOpenHashSet();
        for (int c = 0; c < toCluster.getClassSize(); c++)
            tmp.put(c, new IntOpenHashSet());
        for (int i = 0; i < result.length - 1; i++) {
            tmp.get(toCluster.getDataPointCategory(i)).add(result[i]);
            allSeen.add(result[i]);
        }
        for (IntOpenHashSet set : tmp.values())
            assertEquals(1, set.size());
        assertEquals(2, allSeen.size());// make sure we saw both clusters!

        result = kmeans.cluster(toCluster, 2, true, (int[]) null);
        // make sure each cluster has points from only 1 class. If true then everyone is
        // good
        tmp = new HashMap<>();
        allSeen = new IntOpenHashSet();
        for (int c = 0; c < toCluster.getClassSize(); c++)
            tmp.put(c, new IntOpenHashSet());
        for (int i = 0; i < result.length - 1; i++) {
            tmp.get(toCluster.getDataPointCategory(i)).add(result[i]);
            allSeen.add(result[i]);
        }
        for (IntOpenHashSet set : tmp.values())
            assertEquals(1, set.size());
        assertEquals(2, allSeen.size());// make sure we saw both clusters!
    }
}
