package memory;

import java.util.concurrent.locks.ReadWriteLock;

public class SharedVector {

    private double[] vector;
    private VectorOrientation orientation;
    private ReadWriteLock lock = new java.util.concurrent.locks.ReentrantReadWriteLock();

    public SharedVector(double[] vector, VectorOrientation orientation) {
        this.vector = vector;
        this.orientation = orientation;
    }

    public double get(int index) {
        {
            readLock();
            try {
                return vector[index];
            } finally {
                readUnlock();
            }
        }
    }

    public int length() {
        readLock();
        try {
            return vector.length;
        } finally {
            readUnlock();
        }
    }

    public VectorOrientation getOrientation() {
        {
            readLock();
            try {
                return orientation;
            } finally {
                readUnlock();
            }
        }
    }

    public void writeLock() {
        lock.writeLock().lock();
    }

    public void writeUnlock() {
        lock.writeLock().unlock();
    }

    public void readLock() {
        lock.readLock().lock();
    }

    public void readUnlock() {
        lock.readLock().unlock();
    }

    public void transpose() {
        writeLock();
        try {
            orientation = getOrientation() == VectorOrientation.ROW_MAJOR ?
                    VectorOrientation.COLUMN_MAJOR : VectorOrientation.ROW_MAJOR;
        }
        finally {
            writeUnlock();
        }
    }

    public void add(SharedVector other) {
        writeLock();
        other.readLock();
        try {
            for (int i = 0; i < vector.length; i++) {
                vector[i] += other.get(i);
            }
        } finally {
            other.readUnlock();
            writeUnlock();
        }
    }

    public void negate() {
        writeLock();
        try {
            for (int i = 0; i < vector.length; i++) {
                vector[i] = -vector[i];
            }
        } finally {
            writeUnlock();
        }
    }

    public double dot(SharedVector other) {
        readLock();
        other.readLock();
        try {
            double ans = 0;
            for (int i = 0; i < vector.length; i++) {
                ans += get(i) * other.get(i);
            }
            return ans;
        }
        finally {
            readUnlock();
            other.readUnlock();
        }
    }

    public void vecMatMul(SharedMatrix matrix) {
        writeLock();
        try {
            double[] vec = new double[matrix.length()];
            for (int i = 0; i < matrix.length(); i++) {
                vec[i] = dot(matrix.get(i));
            }
            vector = vec;
        }
        finally {
            writeUnlock();
        }
    }
}
