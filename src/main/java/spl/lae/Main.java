package spl.lae;
import java.io.IOException;
import java.text.ParseException;

import parser.*;

public class Main {
    public static void main(String[] args) throws IOException {
        InputParser parser = new InputParser();
        try {
            ComputationNode root = parser.parse("C:\\Users\\Nimrod\\OneDrive\\Desktop\\programs\\Skeleton\\example2.json");
            LinearAlgebraEngine engine = new LinearAlgebraEngine(5);
            engine.run(root);
            System.out.println(root);

        } catch (ParseException e) {
            System.out.println();
        }
        System.out.println();
    }
}