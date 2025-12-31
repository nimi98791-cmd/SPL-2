package memory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SharedMatrixTest {

    private SharedMatrix matrix;
    private final double[][] initialData = {
            {1.0, 2.0},
            {3.0, 4.0}
    };

    @BeforeEach
    void setUp() {
        matrix = new SharedMatrix(initialData);
    }

    @Test
    void testInitialization() {
        assertEquals(2, matrix.length());
        assertEquals(VectorOrientation.ROW_MAJOR, matrix.getOrientation());
        assertEquals(1.0, matrix.get(0).get(0));
        assertEquals(3.0, matrix.get(1).get(0));
    }

    @Test
    void testLoadRowMajor() {
        double[][] newData = {
                {10.0, 20.0, 30.0},
                {40.0, 50.0, 60.0}
        };
        matrix.loadRowMajor(newData);
        assertEquals(2, matrix.length()); // 2 rows
        assertEquals(3, matrix.get(0).length()); // 3 columns
        assertEquals(10.0, matrix.get(0).get(0));
        assertEquals(60.0, matrix.get(1).get(2));
        assertEquals(VectorOrientation.ROW_MAJOR, matrix.getOrientation());
    }

    @Test
    void testLoadColumnMajor() {
        double[][] newData = {
                {1.0, 2.0},
                {3.0, 4.0},
                {5.0, 6.0}
        };
        matrix.loadColumnMajor(newData);
        assertEquals(2, matrix.length()); // Should have 2 vectors (columns)
        assertEquals(3, matrix.get(0).length()); // Each vector has length 3 (rows)
        assertEquals(VectorOrientation.COLUMN_MAJOR, matrix.getOrientation());

        assertEquals(1.0, matrix.get(0).get(0));
        assertEquals(3.0, matrix.get(0).get(1));
        assertEquals(5.0, matrix.get(0).get(2));
        assertEquals(2.0, matrix.get(1).get(0));
        assertEquals(4.0, matrix.get(1).get(1));
        assertEquals(6.0, matrix.get(1).get(2));
    }

    @Test
    void testReadRowMajorWhenLoadedRowMajor() {
        double[][] result = matrix.readRowMajor();

        assertEquals(2, result.length);
        assertEquals(2, result[0].length);
        assertEquals(1.0, result[0][0]);
        assertEquals(4.0, result[1][1]);
    }

    @Test
    void testReadRowMajorWhenLoadedColumnMajor() {
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        // Load as columns:
        // Col 0: [1, 3]
        // Col 1: [2, 4]
        matrix.loadColumnMajor(data);
        // Read as rows
        double[][] result = matrix.readRowMajor();
        assertEquals(1.0, result[0][0]);
        assertEquals(2.0, result[0][1]);
        assertEquals(3.0, result[1][0]);
        assertEquals(4.0, result[1][1]);
    }
}
