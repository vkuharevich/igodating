package com.igodating.service.vector.impl;

import com.igodating.repository.QuestionaryRepository;
import com.igodating.service.vector.WhiteningMatrixService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.ejml.data.DMatrixRMaj;
import org.ejml.dense.row.CommonOps_DDRM;
import org.ejml.dense.row.factory.DecompositionFactory_DDRM;
import org.ejml.interfaces.decomposition.EigenDecomposition_F64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
public class WhiteningMatrixServiceImpl implements WhiteningMatrixService {

    @Value("${whitening-matrix.selection-limit}")
    private int covarianceMatrixSelectionLimit;

    @Value("${whitening-matrix.shrinkage-lambda}")
    private double shrinkageLambda;

    private boolean thresholdReached = false;

    private DMatrixRMaj whiteningMatrix;

    private final QuestionaryRepository questionaryRepository;

    @Override
    @Transactional(readOnly = true)
    public void refreshWhiteningMatrix() {
        if (!thresholdReached) {
            // для cold-start
            long actualCount = questionaryRepository.count();
            if (actualCount < covarianceMatrixSelectionLimit) {
                log.info("Обновление whitening не осуществимо по причине малого объема данных анкет");
                return;
            }
            thresholdReached = true;
        }
        List<float[]> selection = questionaryRepository.getFactorVectorsAsMatrix(PageRequest.of(0, covarianceMatrixSelectionLimit, Sort.unsorted()));
        int rows = selection.size();
        int columns = selection.getFirst().length;

        DMatrixRMaj matrix = toDMatrixWithCentering(selection, rows, columns);
        DMatrixRMaj covariance = getCovarianceMatrix(matrix, rows, columns);
        shrinkage(covariance, columns);
        EigenDecomposition_F64<DMatrixRMaj> eigen = eigenDecomposition(covariance, columns);

        whiteningMatrix = createWhiteningMatrix(eigen, columns);
    }

    @Override
    public double[] applyWhitening(double[] vector) {
        if (!thresholdReached) {
            log.debug("Whitening не применим по причине малого объема данных анкет");
            return vector;
        }
        int K = vector.length;

        DMatrixRMaj v = new DMatrixRMaj(K, 1);
        for (int i = 0; i < K; i++) {
            v.set(i, 0, vector[i]);
        }

        DMatrixRMaj result = new DMatrixRMaj(K, 1);
        CommonOps_DDRM.mult(whiteningMatrix, v, result);

        double[] out = new double[K];
        for (int i = 0; i < K; i++) {
            out[i] =  result.get(i, 0);
        }

        return out;
    }

    private DMatrixRMaj createWhiteningMatrix(EigenDecomposition_F64<DMatrixRMaj> eigen, int columns) {
        DMatrixRMaj V = new DMatrixRMaj(columns, columns);
        DMatrixRMaj D = new DMatrixRMaj(columns, columns);

        for (int i = 0; i < columns; i++) {

            double eigenvalue = eigen.getEigenvalue(i).getReal();

            // защита от нуля
            if (eigenvalue < 1e-8) {
                eigenvalue = 1e-8;
            }

            D.set(i, i, 1.0 / Math.sqrt(eigenvalue));

            DMatrixRMaj eigenvector = eigen.getEigenVector(i);

            for (int j = 0; j < columns; j++) {
                V.set(j, i, eigenvector.get(j, 0));
            }
        }

        DMatrixRMaj temp = new DMatrixRMaj(columns, columns);
        DMatrixRMaj W = new DMatrixRMaj(columns, columns);

        CommonOps_DDRM.mult(V, D, temp);
        CommonOps_DDRM.multTransB(temp, V, W);

        return W;
    }

    private EigenDecomposition_F64<DMatrixRMaj> eigenDecomposition(DMatrixRMaj covariance, int columns) {
        EigenDecomposition_F64<DMatrixRMaj> eig = DecompositionFactory_DDRM.eig(columns, true);

        eig.decompose(covariance);
        return eig;
    }

    private void shrinkage(DMatrixRMaj covariance, int columns) {
        DMatrixRMaj diag = new DMatrixRMaj(columns, columns);

        for (int i = 0; i < columns; i++) {
            diag.set(i, i, covariance.get(i, i));
        }

        CommonOps_DDRM.scale(1 - shrinkageLambda, covariance);
        CommonOps_DDRM.addEquals(covariance, shrinkageLambda, diag);
    }

    private DMatrixRMaj getCovarianceMatrix(DMatrixRMaj matrix, int rows, int columns) {
        DMatrixRMaj Xt = new DMatrixRMaj(columns, rows);
        CommonOps_DDRM.transpose(matrix, Xt);

        DMatrixRMaj covariance = new DMatrixRMaj(rows, rows);
        CommonOps_DDRM.mult(Xt, matrix, covariance);

        CommonOps_DDRM.scale(1.0 / rows, covariance);

        return covariance;
    }

    private DMatrixRMaj toDMatrixWithCentering(List<float[]> selection, int rows, int columns) {
        DMatrixRMaj matrix = new DMatrixRMaj(rows, columns);

        for (int i = 0; i < rows; i++) {
            float[] v = selection.get(i);
            for (int j = 0; j < columns; j++) {
                matrix.set(i, j, v[j]);
            }
        }

        double[] mean = new double[columns];

        for (int j = 0; j < columns; j++) {
            for (int i = 0; i < rows; i++) {
                mean[j] += matrix.get(i, j);
            }
            mean[j] /= rows;
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                matrix.add(i, j, -mean[j]);
            }
        }

        return matrix;
    }
}
