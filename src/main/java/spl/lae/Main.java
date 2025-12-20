package spl.lae;
import java.io.IOException;
import java.text.ParseException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
        InputParser parser = new InputParser();
        try {
            ComputationNode root = parser.parse(args[1]);
            LinearAlgebraEngine engine = new LinearAlgebraEngine(Integer.parseInt(args[0]));
            engine.run(root);
            for (double[] row : root.getMatrix()) {
                for (double value : row) {
                    System.out.print(value + " ");
                }
                System.out.println();
            }

        } catch (ParseException e) {
            System.out.println();
        }
        System.out.println();
    }
}