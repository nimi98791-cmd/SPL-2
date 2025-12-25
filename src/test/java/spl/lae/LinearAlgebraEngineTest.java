package spl.lae;

import memory.SharedMatrix;
import memory.VectorOrientation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import parser.ComputationNode;
import parser.InputParser;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LinearAlgebraEngineTest {

    private LinearAlgebraEngine engine;
    private SharedMatrix matrix1;
    private SharedMatrix matrix2;

    @BeforeEach
    void setUp() {
        engine = new LinearAlgebraEngine(0);
        double[][] data1 = {{1.0, 2.0}, {3.0, 4.0}};
        double[][] data2 = {{5.0, 6.0}, {7.0, 8.0}};
        matrix1 = new SharedMatrix(data1);
        matrix2 = new SharedMatrix(data2);
    }

    @Test
    void testCreateAddTasks() throws Exception {
        injectPrivateField(engine, "leftMatrix", matrix1);
        injectPrivateField(engine, "rightMatrix", matrix2);

        List<Runnable> tasks = engine.createAddTasks();
        assertEquals(2, tasks.size(), "Should generate one task per row");
        for (Runnable task : tasks) {
            task.run();
        }

        assertEquals(6.0, matrix1.get(0).get(0));
        assertEquals(8.0, matrix1.get(0).get(1));
        assertEquals(10.0, matrix1.get(1).get(0));
        assertEquals(12.0, matrix1.get(1).get(1));
    }

    @Test
    void testCreateAddTasksThrowsOnRowMismatch() throws Exception {
        double[][] dataLeft = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        double[][] dataRight = {
                {5.0, 6.0}
        };
        SharedMatrix left = new SharedMatrix(dataLeft);
        SharedMatrix right = new SharedMatrix(dataRight);
        injectPrivateField(engine, "leftMatrix", left);
        injectPrivateField(engine, "rightMatrix", right);
        assertThrows(IllegalArgumentException.class, () -> {
            engine.createAddTasks();
        });
    }

    @Test
    void testCreateAddTasksThrowsOnColumnMismatch() throws Exception {
        double[][] dataLeft = {
                {1.0, 2.0},
                {3.0, 4.0}
        };
        double[][] dataRight = {
                {5.0, 6.0, 7.0},
                {8.0, 9.0, 10.0}
        };
        SharedMatrix left = new SharedMatrix(dataLeft);
        SharedMatrix right = new SharedMatrix(dataRight);
        injectPrivateField(engine, "leftMatrix", left);
        injectPrivateField(engine, "rightMatrix", right);
        assertThrows(IllegalArgumentException.class, () -> {
            engine.createAddTasks();
        });
    }

    @Test
    void testCreateNegateTasks() throws Exception {
        injectPrivateField(engine, "leftMatrix", matrix1);
        List<Runnable> tasks = engine.createNegateTasks();
        assertEquals(2, tasks.size());
        for (Runnable task : tasks) {
            task.run();
        }
        assertEquals(-1.0, matrix1.get(0).get(0));
        assertEquals(-2.0, matrix1.get(0).get(1));
        assertEquals(-3.0, matrix1.get(1).get(0));
        assertEquals(-4.0, matrix1.get(1).get(1));
    }

    @Test
    void testCreateTransposeTasks() throws Exception {
        injectPrivateField(engine, "leftMatrix", matrix1);
        List<Runnable> tasks = engine.createTransposeTasks();
        assertEquals(2, tasks.size());
        for (Runnable task : tasks) {
            task.run();
        }
        assertEquals(VectorOrientation.COLUMN_MAJOR, matrix1.get(0).getOrientation());
        assertEquals(VectorOrientation.COLUMN_MAJOR, matrix1.get(1).getOrientation());
    }

    @Test
    void testCreateMultiplyTasks() throws Exception {
        double[][] data2 = {{5.0, 6.0}, {7.0, 8.0}};
        matrix2.loadColumnMajor(data2);
        injectPrivateField(engine, "leftMatrix", matrix1);
        injectPrivateField(engine, "rightMatrix", matrix2);

        List<Runnable> tasks = engine.createMultiplyTasks();
        assertEquals(2, tasks.size());
        for (Runnable task : tasks) {
            task.run();
        }
        assertEquals(19.0, matrix1.get(0).get(0));
        assertEquals(22.0, matrix1.get(0).get(1));
        assertEquals(43.0, matrix1.get(1).get(0));
        assertEquals(50.0, matrix1.get(1).get(1));
    }

    @Test
    void testCreateMultiplyTasksErrors() throws Exception {
        double[][] dataLeft = {{1.0, 2.0}};
        double[][] dataRightMismatch = {{1.0}, {2.0}, {3.0}};
        SharedMatrix left = new SharedMatrix(dataLeft);
        SharedMatrix rightMismatch = new SharedMatrix();
        rightMismatch.loadColumnMajor(dataRightMismatch);
        injectPrivateField(engine, "leftMatrix", left);
        injectPrivateField(engine, "rightMatrix", rightMismatch);
        assertThrows(IllegalArgumentException.class, () -> {
            engine.createMultiplyTasks();
        });
        SharedMatrix rightEmpty = new SharedMatrix(); // get(0) will return null
        injectPrivateField(engine, "rightMatrix", rightEmpty);
        assertThrows(IllegalArgumentException.class, () -> {
            engine.createMultiplyTasks();
        });
    }

    @Test
    void testRunWithProvidedPath() throws Exception {
        String filePath = "src/test/java/spl/lae/input.json"; // Update this path

        InputParser parser = new InputParser();
        ComputationNode root = parser.parse(filePath);

        LinearAlgebraEngine engine = new LinearAlgebraEngine(3);
        ComputationNode resultNode = engine.run(root);
        System.out.println(engine.getWorkerReport());

//        double[][] resultMatrix = resultNode.getMatrix();
//        assertNotNull(resultMatrix);
//        assertEquals(2, resultMatrix.length);
//        assertEquals(2, resultMatrix[0].length);
//        assertEquals(58.0, resultMatrix[0][0]);
//        assertEquals(64.0, resultMatrix[0][1]);
//        assertEquals(139.0, resultMatrix[1][0]);
//        assertEquals(154.0, resultMatrix[1][1]);
    }

    private void injectPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}