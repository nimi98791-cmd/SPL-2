package memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SharedVectorTest {

    private SharedVector vector1;
    private SharedVector vector2;

    @BeforeEach
    public void setUp() {
        double[] data1 = {1.0, 2.0, 3.0};
        double[] data2 = {4.0, 5.0, 6.0};

        vector1 = new SharedVector(data1, VectorOrientation.ROW_MAJOR);
        vector2 = new SharedVector(data2, VectorOrientation.ROW_MAJOR);
    }

    @Test
    public void testGetAndLength() {
        assertEquals(3, vector1.length());
        assertEquals(1.0, vector1.get(0));
        assertEquals(3.0, vector1.get(2));
    }

    @Test
    public void testAdd() {
        vector1.add(vector2);
        // Expected result
        assertEquals(5.0, vector1.get(0));
        assertEquals(7.0, vector1.get(1));
        assertEquals(9.0, vector1.get(2));
        // Ensure vector2 remained unchanged
        assertEquals(4.0, vector2.get(0));
    }

    @Test
    public void testNegate() {
        vector1.negate();
        assertEquals(-1.0, vector1.get(0));
        assertEquals(-2.0, vector1.get(1));
        assertEquals(-3.0, vector1.get(2));
    }

    @Test
    public void testTranspose() {
        assertEquals(VectorOrientation.ROW_MAJOR, vector1.getOrientation());
        vector1.transpose();
        assertEquals(VectorOrientation.COLUMN_MAJOR, vector1.getOrientation());
        vector1.transpose();
        assertEquals(VectorOrientation.ROW_MAJOR, vector1.getOrientation());
    }

    @Test
    public void testDotProduct() {
        double result = vector1.dot(vector2);
        assertEquals(32.0, result);
    }

    @Test
    void testVecMatMul() {
        double[][] matrixData = {
                {2.0, 0.0},
                {0.0, 2.0},
                {1.0, 1.0}
        };
        SharedMatrix matrix = new SharedMatrix();
        matrix.loadColumnMajor(matrixData);
        vector1.vecMatMul(matrix);
        assertEquals(2, vector1.length());
        assertEquals(5.0, vector1.get(0));
        assertEquals(7.0, vector1.get(1));
    }
}
