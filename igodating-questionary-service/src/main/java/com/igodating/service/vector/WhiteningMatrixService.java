package com.igodating.service.vector;

public interface WhiteningMatrixService {

    void refreshWhiteningMatrix();

    double[] applyWhitening(double[] vector);
}
