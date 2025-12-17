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
        vectors = new SharedVector[matrix.length];
        for (int i = 0; i < matrix.length; i++) {
            vectors[i] = new SharedVector(matrix[i], VectorOrientation.ROW_MAJOR);
        }
    }

    public void loadColumnMajor(double[][] matrix) {
        // TODO: replace internal data with new column-major matrix
    }

    public double[][] readRowMajor() {
        double[][] ans = new double[vectors.length][vectors[0].length()];
        for (int i = 0; i < vectors.length; i++) {
            for (int j = 0; j < vectors[i].length(); j++) {
                ans[i][j] = vectors[i].get(j);
            }
        }
        return ans;
    }

    public SharedVector get(int index) {
        return vectors[index];
    }

    public int length() {
        return vectors.length;
    }

    public VectorOrientation getOrientation() {
        // TODO: return orientation
        return null;
    }

    private void acquireAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: acquire read lock for each vector
    }

    private void releaseAllVectorReadLocks(SharedVector[] vecs) {
        // TODO: release read locks
    }

    private void acquireAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: acquire write lock for each vector
    }

    private void releaseAllVectorWriteLocks(SharedVector[] vecs) {
        // TODO: release write locks
    }
}
