package com.example.FlowSenseMPC.model;

public class RLSModel {

    private double[] theta = new double[3]; // [a1, a2, b1]
    private double[][] P = new double[3][3];

    public RLSModel() {
        for (int i = 0; i < 3; i++) {
            P[i][i] = 1000; // large initial uncertainty
        }
    }

    public void update(double[] phi, double y) {
        // phi = [y(k-1), y(k-2), u(k-1)]
        double[] Pphi = new double[3];
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                Pphi[i] += P[i][j] * phi[j];

        double denom = 1;
        for (int i = 0; i < 3; i++)
            denom += phi[i] * Pphi[i];

        double[] K = new double[3];
        for (int i = 0; i < 3; i++)
            K[i] = Pphi[i] / denom;

        double yHat = 0;
        for (int i = 0; i < 3; i++)
            yHat += phi[i] * theta[i];

        double error = y - yHat;

        for (int i = 0; i < 3; i++)
            theta[i] += K[i] * error;
    }

    public double[] getTheta() {
        return theta;
    }
}

