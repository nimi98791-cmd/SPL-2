package memory;

public class SharedMatrix {

    private volatile SharedVector[] vectors = {}; // underlying vectors

    public SharedMatrix() {
        // TODO: initialize empty matrix
    }

    public SharedMatrix(double[][] matrix) {
        // TODO: construct matrix as row-major SharedVectors
    }

    public void loadRowMajor(double[][] matrix) {
        acquireAllVectorWriteLocks(vectors);
        try {
            SharedVector[] newVectors = new SharedVector[matrix.length];
            for (int i = 0; i < matrix.length; i++) {
                newVectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
            }
            vectors = newVectors;
        } finally {
            releaseAllVectorWriteLocks(vectors);
        }
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
    }

    public double[][] readRowMajor() {
        SharedVector[] currentVectors = vectors; // In case vectors is changed until release.
        acquireAllVectorReadLocks(currentVectors);
        try {
            if (currentVectors.length == 0) return new double[0][0];
            double[][] ans = new double[currentVectors.length][currentVectors[0].length()];
            for (int i = 0; i < currentVectors.length; i++) {
                for (int j = 0; j < currentVectors[i].length(); j++) {
                    ans[i][j] = currentVectors[i].get(j);
                }
            }
            return ans;
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
        // TODO: return orientation
        return null;
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
