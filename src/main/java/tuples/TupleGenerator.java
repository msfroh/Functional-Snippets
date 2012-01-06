package tuples;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * User: froh
 * Date: 1/6/12
 * Time: 3:28 PM
 */
public class TupleGenerator {
    public static void main(final String args[]) throws IOException {
        final String sourceRoot = args[0];
        final String targetPackage = args[1];
        final int maxTuple = Integer.valueOf(args[2]);

        for (int i = 1; i <= maxTuple; i++) {
            generateTupleClass(sourceRoot, targetPackage, i);
        }

        File targetFile = new File(sourceRoot + File.separator +
                targetPackage.replace(".", File.separator) +
                File.separator + "TupleUtils.java");
        BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
        writer.write("package " + targetPackage + ";\n\n");
        writer.write("public final class TupleUtils {\n");
        for (int i = 1; i <= maxTuple; i++) {
            String typeArgs = buildTypeArgs(i);
            writer.write("    public static <" + typeArgs + "> Tuple" + i + "<" + typeArgs +"> tuple(");
            writer.write(buildConstructorArgs(i) + ") {\n");
            writer.write("        return new Tuple" + i + "<" + typeArgs +">(");
            for (int j = 1; j <= i; j++) {
                if (j > 1) {
                    writer.write(", ");
                }
                writer.write("v"+j);
            }
            writer.write(");\n");
            writer.write("    }\n\n");
        }
        writer.write("}\n");
        writer.close();
    }

    private static void generateTupleClass(final String sourceRoot, final String targetPackage,
                                           final int dimension) throws IOException {
        File basePath = new File(sourceRoot + File.separator + targetPackage.replace(".", File.separator));
        basePath.mkdirs();
        File targetFile = new File(basePath.getPath() + 
                File.separator + "Tuple" + dimension + ".java");
        BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
        writer.write("// This class is auto-generated! Modify at your own risk.\n");
        writer.write("package " + targetPackage + ";\n\n");
        writer.write("public final class Tuple" + dimension + "<");
        writer.write(buildTypeArgs(dimension) + "> {\n");
        for (int i = 1; i <= dimension; i++) {
            writer.write("    public final T" + i +" _" + i +";\n");
        }

        writer.write("\n    public Tuple" + dimension + "(");
        writer.write(buildConstructorArgs(dimension));
        writer.write(") {\n");
        for (int i = 1; i <= dimension; i++) {
            writer.write("        _" + i + " = v" + i + ";\n");
        }
        writer.write("    }\n");
        // TODO Generate equals and hashCode.
        // TODO How about compare? Only works if underlying elements are comparable.
        writer.write("}\n");
        writer.close();
    }

    private static String buildTypeArgs(final int dimension) {
        final StringBuilder typeArgumentBuilder = new StringBuilder();
        for (int i = 1; i <= dimension; i++) {
            if (i > 1) {
                typeArgumentBuilder.append(',');
            }
            typeArgumentBuilder.append('T').append(i);
        }
        return typeArgumentBuilder.toString();
    }

    private static String buildConstructorArgs(final int dimension) {
        final StringBuilder constructorArgsBuilder = new StringBuilder();
        for (int i = 1; i <= dimension; i++) {
            if (i > 1) {
                constructorArgsBuilder.append(", ");
            }
            constructorArgsBuilder.append("final T").append(i).append(" v").append(i);
        }
        return constructorArgsBuilder.toString();
    }
}
