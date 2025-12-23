package spl.lae;

import parser.*;
import memory.*;
import scheduling.*;

import java.util.ArrayList;
import java.util.List;

public class LinearAlgebraEngine {

    private SharedMatrix leftMatrix = new SharedMatrix();
    private SharedMatrix rightMatrix = new SharedMatrix();
    private TiredExecutor executor;

    public LinearAlgebraEngine(int numThreads) {
        executor = new TiredExecutor(numThreads);
    }

    public ComputationNode run(ComputationNode computationRoot) {
        try {
            while (computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
                ComputationNode temp = computationRoot.findResolvable();
                temp.associativeNesting();
                temp = temp.findResolvable();
                loadAndCompute(temp);
                temp.resolve(leftMatrix.readRowMajor());
            }
        } finally {
            try {
                executor.shutdown();
            } catch (InterruptedException e) {
                //todo
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        leftMatrix.loadRowMajor(node.getChildren().get(0).getMatrix());
        ComputationNodeType nodeType = node.getNodeType();
        if (nodeType == ComputationNodeType.ADD) {
            rightMatrix.loadRowMajor(node.getChildren().get(1).getMatrix());
        } else if (nodeType == ComputationNodeType.MULTIPLY) {
            rightMatrix.loadColumnMajor(node.getChildren().get(1).getMatrix());
        }
        List<Runnable> tasks = getTasks(nodeType);
        executor.submitAll(tasks);
    }

    public List<Runnable> createAddTasks() {
        if (leftMatrix.length() != rightMatrix.length() ||
                leftMatrix.get(0).length() != rightMatrix.get(0).length())
            throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            int index = i;
            tasks.add(() -> {
                System.out.println("task add start");
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    System.out.println();
                }
                leftMatrix.get(index).add(rightMatrix.get(index));
                System.out.println("task add end");
            });
        }
        return tasks;
    }

    public List<Runnable> createMultiplyTasks() {
        if (leftMatrix.get(0).length() != rightMatrix.get(0).length())
            throw new IllegalArgumentException("Illegal operation: dimensions mismatch");
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            int index = i;
            tasks.add(() -> {
                System.out.println("task multi start");
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    System.out.println();
                }
                leftMatrix.get(index).vecMatMul(rightMatrix);
                System.out.println("task multi end");
            });
        }
        return tasks;
    }


    public List<Runnable> createNegateTasks() {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            int index = i;
            tasks.add(() -> {
                System.out.println("task neg start");
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    System.out.println();
                }
                leftMatrix.get(index).negate();
                System.out.println("task neg end");
            });
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            int index = i;
            tasks.add(() -> {
                System.out.println("task Tran start");
                try {
                    Thread.sleep(0);
                } catch (InterruptedException e) {
                    System.out.println();
                }
                leftMatrix.get(index).transpose();
                System.out.println("task Tran end");
            });
        }
        return tasks;
    }

    public String getWorkerReport() {
        return executor.getWorkerReport();
    }

    private List<Runnable> getTasks(ComputationNodeType computationNodeType) {
        switch (computationNodeType) {
            case ADD -> {
                return createAddTasks();
            }
            case NEGATE -> {
                return createNegateTasks();
            }
            case TRANSPOSE -> {
                return createTransposeTasks();
            }
            case MULTIPLY -> {
                return createMultiplyTasks();
            }
            default -> {
                return new ArrayList<>();
            }
        }
    }
}
