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
            engine.run(root);
            OutputWriter.write(root.getMatrix(), args[2]);
        } catch (IllegalArgumentException | ParseException e) {
            OutputWriter.write(e.getMessage(),args[2]);
        }
    }
}