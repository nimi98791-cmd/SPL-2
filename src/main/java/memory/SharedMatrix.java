package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {}

    public SharedMatrix(double[][] matrix) {
        for (int i = 0; i < matrix.length; i++) {
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadRowMajor(double[][] matrix) {
        SharedVector[] currentVectors = vectors;
        acquireAllVectorWriteLocks(currentVectors);
        try {
            SharedVector[] newVectors = new SharedVector[matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                newVectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
            }
            vectors = newVectors;
        } finally {
            releaseAllVectorWriteLocks(currentVectors);
        }
    }

    public void loadColumnMajor(double[][] matrix) {
        SharedVector[] currentVectors = vectors;
        acquireAllVectorWriteLocks(currentVectors);
        if (matrix.length == 0) return;
        try {
            SharedVector[] newVectors = new SharedVector[matrix[0].length];
            for (int j = 0; j < matrix[0].length; j++) {
                double[] column = new double[matrix.length];
                for (int i = 0; i < matrix.length; i++) {
                    column[i] = matrix[i][j];
                }
                newVectors[j] = new SharedVector(column, VectorOrientation.COLUMN_MAJOR);
            }
            vectors = newVectors;
        } finally {
            releaseAllVectorWriteLocks(currentVectors);
        }
    }

    public double[][] readRowMajor() {
        SharedVector[] currentVectors = vectors;
        acquireAllVectorReadLocks(currentVectors);
        try {
            if (currentVectors.length == 0) return new double[0][0];
            if (getOrientation() == VectorOrientation.ROW_MAJOR) {
                double[][] ans = new double[currentVectors.length][currentVectors[0].length()];
                for (int i = 0; i < currentVectors.length; i++) {
                    for (int j = 0; j < currentVectors[i].length(); j++) {
                        ans[i][j] = currentVectors[i].get(j);
                    }
                }
                return ans;
            } else {
                double[][] ans = new double[currentVectors[0].length()][currentVectors.length];
                for (int i = 0; i < currentVectors.length; i++) {
                    for (int j = 0; j < currentVectors[i].length(); j++) {
                        ans[j][i] = currentVectors[i].get(j);
                    }
                }
                return ans;
            }
        } finally {
            releaseAllVectorReadLocks(currentVectors);
        }
    }

    public SharedVector get(int index) {
        SharedVector[] current = this.vectors;
        if (current != null && index >= 0 && index < current.length) {
            return current[index];
        }
        return null;
    }

    public int length() {
        SharedVector[] current = this.vectors;
        return (current != null) ? current.length : 0;
    }

    public VectorOrientation getOrientation() {
        if (vectors.length == 0) return null;
        return vectors[0].getOrientation();
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            if (vec != null) {
                vec.readLock();
            }
        }
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            if (vec != null) {
                vec.readUnlock();
            }
        }
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            if (vec != null) {
                vec.writeLock();
            }
        }
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        for (SharedVector vec : vecs) {
            if (vec != null) {
                vec.writeUnlock();
            }
        }
    }
}
