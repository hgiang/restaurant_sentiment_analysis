package ir.sentiment.weight;

import Jama.Matrix;
import Jama.SingularValueDecomposition;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Do
 */
public class WeightScheme {

    public Matrix tfTransform(Matrix matrix) {
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            double sum = max(matrix.getMatrix(
                    0, matrix.getRowDimension() - 1, j, j));
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                matrix.set(i, j, (matrix.get(i, j) / (sum + 1.0)));
            }
        }
        return matrix;
    }

    public Matrix idfTransform(Matrix matrix) {
        // Phase 1: apply IDF weight to the raw word frequencies
        int n = matrix.getColumnDimension();
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                double matrixElement = matrix.get(i, j);
                if (matrixElement > 0.0D) {
                    double dm = countDocsWithWord(
                            matrix.getMatrix(i, i, 0, matrix.getColumnDimension() - 1));
                    matrix.set(i, j, (Math.log(n) - Math.log(1 + dm)));
                }
            }
        }
        // Phase 2: normalize the word scores for a single document
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            double sum = max(matrix.getMatrix(0, matrix.getRowDimension() - 1, j, j));
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                matrix.set(i, j, (matrix.get(i, j) / sum));
            }
        }
        return matrix;
    }

    public Matrix tfidfTransform(Matrix matrix) {
        Matrix tf = tfTransform(matrix);
        Matrix idf = idfTransform(matrix);
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                matrix.set(i, j, tf.get(i, j) * idf.get(i, j));
            }
        }
        return matrix;
    }

    public Matrix lsiTransform(Matrix matrix) {
        // phase 1: Singular value decomposition
        SingularValueDecomposition svd = new SingularValueDecomposition(matrix);
        Matrix wordVector = svd.getU();
        Matrix sigma = svd.getS();
        Matrix documentVector = svd.getV();
        // compute the value of k (ie where to truncate)
        int k = (int) Math.floor(Math.sqrt(matrix.getColumnDimension()));
        Matrix reducedWordVector = wordVector.getMatrix(
                0, wordVector.getRowDimension() - 1, 0, k - 1);
        Matrix reducedSigma = sigma.getMatrix(0, k - 1, 0, k - 1);
        Matrix reducedDocumentVector = documentVector.getMatrix(
                0, documentVector.getRowDimension() - 1, 0, k - 1);
        Matrix weights = reducedWordVector.times(
                reducedSigma).times(reducedDocumentVector.transpose());
        // Phase 2: normalize the word scrores for a single document
        for (int j = 0; j < weights.getColumnDimension(); j++) {
            double sum = max(weights.getMatrix(
                    0, weights.getRowDimension() - 1, j, j));
            for (int i = 0; i < weights.getRowDimension(); i++) {
                weights.set(i, j, Math.abs((weights.get(i, j)) / sum));
            }
        }
        return weights;
    }

    public Matrix appTransform(Matrix matrix) {
        for (int j = 0; j < matrix.getColumnDimension(); j++) {
            for (int i = 0; i < matrix.getRowDimension(); i++) {
                if (matrix.get(i, j) > 0) {
                    matrix.set(i, j, 1);
                }
            }
        }
        return matrix;
    }

    private double max(Matrix colMatrix) {
        double max = 0.0D;
        for (int i = 0; i < colMatrix.getRowDimension(); i++) {
            max = Math.max(max, colMatrix.get(i, 0));
        }
        return max;
    }

    private double countDocsWithWord(Matrix rowMatrix) {
        double numDocs = 0.0D;
        for (int j = 0; j < rowMatrix.getColumnDimension(); j++) {
            if (rowMatrix.get(0, j) > 0.0D) {
                numDocs++;
            }
        }
        return numDocs;
    }
}
