package spl.lae;
import java.io.IOException;
import java.text.ParseException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
        try {
            InputParser parser = new InputParser();
            ComputationNode root = parser.parse(args[1]);
            LinearAlgebraEngine engine = new LinearAlgebraEngine(Integer.parseInt(args[0]));
            ComputationNode result = engine.run(root);
            OutputWriter.write(result.getMatrix(), args[2]);
        } catch (Throwable e) {
            OutputWriter.write(e.getMessage(),args[2]);
        }
    }
}