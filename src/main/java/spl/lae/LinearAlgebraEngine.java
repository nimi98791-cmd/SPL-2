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
        while (computationRoot.getNodeType() != ComputationNodeType.MATRIX) {
            ComputationNode temp = computationRoot.findResolvable();
            //
            temp.associativeNesting();
            temp = temp.findResolvable();
            loadAndCompute(temp);
            temp.resolve(leftMatrix.readRowMajor());
        }
        try {
            executor.shutdown();
        } catch (InterruptedException e) {
            // TODO - check if needed.
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        return computationRoot;
    }

    public void loadAndCompute(ComputationNode node) {
        leftMatrix.loadRowMajor(node.getChildren().get(0).getMatrix());
        ComputationNodeType nodeType = node.getNodeType();
        if (nodeType == ComputationNodeType.ADD || nodeType == ComputationNodeType.NEGATE) {
            rightMatrix.loadRowMajor(node.getChildren().get(1).getMatrix());
        } else if (nodeType == ComputationNodeType.MULTIPLY) {
            rightMatrix.loadColumnMajor(node.getChildren().get(1).getMatrix());
        }
        List<Runnable> tasks = getTasks(nodeType);
        executor.submitAll(tasks);
        // TODO: load operand matrices
        // TODO: create compute tasks & submit tasks to executor
    }

    public List<Runnable> createAddTasks() {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            int index = i;
            tasks.add(()-> {
                System.out.println("task add start");
                try {
                    Thread.sleep(1000);
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
        // TODO: return tasks that perform row × matrix multiplication
        return null;
    }

    public List<Runnable> createNegateTasks() {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < rightMatrix.length(); i++) {
            int index = i;
            tasks.add(()-> {
                System.out.println("task neg start");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println();
                }
                rightMatrix.get(index).negate();
                leftMatrix.get(index).add(rightMatrix.get(index));
                System.out.println("task neg end");
            });
        }
        return tasks;
    }

    public List<Runnable> createTransposeTasks() {
        List<Runnable> tasks = new ArrayList<>();
        for (int i = 0; i < leftMatrix.length(); i++) {
            int index = i;
            tasks.add(()-> {
                System.out.println("task Tran start");
                try {
                    Thread.sleep(1000);
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
        // TODO: return summary of worker activity
        return null;
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
            default -> {
                return null;
            }
        }
    }
}
