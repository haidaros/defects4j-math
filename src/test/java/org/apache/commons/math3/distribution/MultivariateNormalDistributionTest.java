/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.commons.math3.distribution;

import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.linear.RealMatrix;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases for {@link MultivariateNormalDistribution}.
 */
public class MultivariateNormalDistributionTest {
    /**
     * Test the ability of the distribution to report its mean value parameter.
     */
    @Test
    public void testGetMean() {
        final double[] mu = { -1.5, 2 };
        final double[][] sigma = { { 2, -1.1 },
                                   { -1.1, 2 } };
        final MultivariateNormalDistribution d = new MultivariateNormalDistribution(mu, sigma);

        final double[] m = d.getMeans();
        for (int i = 0; i < m.length; i++) {
            Assert.assertEquals(mu[i], m[i], 0);
        }
    }

    /**
     * Test the ability of the distribution to report its covariance matrix parameter.
     */
    @Test
    public void testGetCovarianceMatrix() {
        final double[] mu = { -1.5, 2 };
        final double[][] sigma = { { 2, -1.1 },
                                   { -1.1, 2 } };
        final MultivariateNormalDistribution d = new MultivariateNormalDistribution(mu, sigma);

        final RealMatrix s = d.getCovariances();
        final int dim = d.getDimensions();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                Assert.assertEquals(sigma[i][j], s.getEntry(i, j), 0);
            }
        }
    }

    /**
     * Test the accuracy of sampling from the distribution.
     */
    @Test
    public void testSampling() {
        final double[] mu = { -1.5, 2 };
        final double[][] sigma = { { 2, -1.1 },
                                   { -1.1, 2 } };
        final MultivariateNormalDistribution d = new MultivariateNormalDistribution(mu, sigma);
        d.reseedRandomGenerator(50);

        final int n = 30;

        final double[][] samples = d.sample(n);
        final int dim = d.getDimensions();
        final double[] sampleMeans = new double[dim];

        for (int i = 0; i < samples.length; i++) {
            for (int j = 0; j < dim; j++) {
                sampleMeans[j] += samples[i][j];
            }
        }

        final double sampledMeanTolerance = 1e-1;
        for (int j = 0; j < dim; j++) {
            sampleMeans[j] /= samples.length;
            Assert.assertEquals(mu[j], sampleMeans[j], sampledMeanTolerance);
        }

        final double sampledCovarianceTolerance = 2;
        final double[][] sampleSigma = new Covariance(samples).getCovarianceMatrix().getData();
        for (int i = 0; i < dim; i++) {
            for (int j = 0; j < dim; j++) {
                Assert.assertEquals(sigma[i][j], sampleSigma[i][j], sampledCovarianceTolerance);
            }
        }
    }

    /**
     * Test the accuracy of the distribution when calculating densities.
     */
    @Test
    public void testDensities() {
        final double[] mu = { -1.5, 2 };
        final double[][] sigma = { { 2, -1.1 },
                                   { -1.1, 2 } };
        final MultivariateNormalDistribution d = new MultivariateNormalDistribution(mu, sigma);

        final double[][] testValues = { { -1.5, 2 },
                                        { 4, 4 },
                                        { 1.5, -2 },
                                        { 0, 0 } };
        final double[] densities = new double[testValues.length];
        for (int i = 0; i < densities.length; i++) {
            densities[i] = d.density(testValues[i]);
        }

        // From dmvnorm function in R 2.15 CRAN package Mixtools v0.4.5
        final double[] correctDensities = { 0.09528357207691344,
                                            5.80932710124009e-09,
                                            0.001387448895173267,
                                            0.03309922090210541 };

        for (int i = 0; i < testValues.length; i++) {
            Assert.assertEquals(correctDensities[i], densities[i], 1e-16);
        }
    }
}
