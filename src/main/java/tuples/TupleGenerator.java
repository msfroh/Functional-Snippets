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
        if (maxTuple < 1) {
            return;
        }
        File basePath = new File(sourceRoot, targetPackage.replace(".", File.separator));
        if (!basePath.exists() && !basePath.mkdirs()) {
            throw new IOException("Unable to create directory " + basePath);
        }

        for (int i = 1; i <= maxTuple; i++) {
            generateTupleClass(basePath, targetPackage, i);
        }
        generateTupleUtils(basePath, targetPackage, maxTuple);
    }

    private static void generateTupleClass(final File basePath, final String targetPackage,
                                           final int dimension) throws IOException {
        final File targetFile = new File(basePath, "Tuple" + dimension + ".java");
        final BufferedWriter writer = new BufferedWriter(new FileWriter(targetFile));
        try {
            writer.write("// This class is auto-generated! Modify at your own risk.\n");
            writer.write("package " + targetPackage + ";\n\n");
            writer.write("public final class Tuple" + dimension + "<");
            writer.write(buildTypeArgs(dimension) + "> {\n");

            // Generate fields
            for (int i = 1; i <= dimension; i++) {
                writer.write("    public final T" + i + " _" + i + ";\n");
            }

            // Generate constructor
            writer.write("\n    public Tuple" + dimension + "(");
            writer.write(buildConstructorArgs(dimension));
            writer.write(") {\n");
            for (int i = 1; i <= dimension; i++) {
                writer.write("        _" + i + " = v" + i + ";\n");
            }
            writer.write("    }\n\n");

            // Generate equals()
            writer.write("    @Override\n" +
                    "    public boolean equals(final Object o) {\n" +
                    "        if (this == o) return true;\n" +
                    "        if (o == null || getClass() != o.getClass()) return false;\n" +
                    "\n" +
                    "        Tuple" + dimension + " tuple = (Tuple" + dimension + ") o;\n" +
                    "\n");
            for (int i = 1; i <= dimension; i++) {

                writer.write(
                        "        if (_" + i + " != null ? !_" + i + ".equals(tuple._" + i + ") : tuple._" + i + " != null)\n" +
                                "            return false;\n");
            }
            writer.write("\n" +
                    "        return true;\n" +
                    "    }\n\n");

            // Generate hashCode()
            writer.write("    @Override\n" +
                    "    public int hashCode() {\n" +
                    "        int result = _1 != null ? _1.hashCode() : 0;\n");
            for (int i = 2; i <= dimension; i++) {
                writer.write("        result = 31 * result + (_" + i + " != null ? _" + i + ".hashCode() : 0);\n");
            }
            writer.write("        return result;\n" +
                    "    }\n\n");

            // Generate toString()
            writer.write("    @Override\n" +
                    "    public String toString() {\n" +
                    "        return \"(\" +\n");
            for (int i = 1; i <= dimension; i++) {
                writer.write("            _" + i);
                if (i < dimension) {
                    writer.write(" + ','");
                }
                writer.write(" +\n");
            }
            writer.write("        ')';\n");
            writer.write("    }\n\n");


            writer.write("}\n");
        } finally {
            writer.close();
        }
    }

    private static void generateTupleUtils(final File basePath, final String targetPackage,
                                           final int maxTuple) throws IOException {
        final File utilsFile = new File(basePath, "TupleUtils.java");
        BufferedWriter writer = new BufferedWriter(new FileWriter(utilsFile));
        try {
            writer.write("package " + targetPackage + ";\n\n");
            for (int i = 1; i <= maxTuple; i++) {
                writer.write("import functions.Function" + i + ";\n");
            }
            writer.write("\npublic final class TupleUtils {\n");
            for (int i = 1; i <= maxTuple; i++) {
                // Generate tuple() function
                String typeArgs = buildTypeArgs(i);
                writer.write("    public static <" + typeArgs + "> Tuple" + i + "<" + typeArgs + "> tuple(");
                writer.write(buildConstructorArgs(i) + ") {\n");
                writer.write("        return new Tuple" + i + "<" + typeArgs + ">(");
                for (int j = 1; j <= i; j++) {
                    if (j > 1) {
                        writer.write(", ");
                    }
                    writer.write("v" + j);
                }
                writer.write(");\n");
                writer.write("    }\n\n");

                // Generate tupled() function
                writer.write("    public static <R, " + typeArgs + "> Function1<R, Tuple" + i + "<" + typeArgs + ">> " +
                        "tupled(final Function" + i + "<R," + typeArgs + "> f) {\n");
                writer.write("        return new Function1<R, Tuple" + i +"<" + typeArgs + ">>() {\n");
                writer.write("            @Override\n" +
                        "            public R evaluate(Function1<R, Tuple" + i + "<" + typeArgs + ">> self, " +
                        "Tuple" + i + "<" + typeArgs + "> i) {\n");
                writer.write(
                        "                return f.evaluate(f");
                for (int j = 1; j <=i; j++) {
                    writer.write(String.format(", i._%d", j));
                }
                writer.write(");\n");
                writer.write(
                        "            }\n" +
                        "        };\n" +
                        "    }\n\n");

                // Generate untupled() function
                writer.write("    public static <R, " + typeArgs + "> Function" + i + "<R, " + typeArgs + "> " +
                        "untupled(final Function1<R, Tuple" + i + "<" + typeArgs + ">> f) {\n");
                writer.write("        return new Function" + i + "<R, " + typeArgs + ">() {\n");
                writer.write("            @Override\n" +
                        "            public R evaluate(Function" + i + "<R, " + typeArgs + "> self");
                for (int j = 1; j <= i; j++) {
                    writer.write(String.format(", final T%d i%d", j, j));
                }
                writer.write(") {\n");
                writer.write(
                        "                return f.evaluate(f, tuple(");
                for (int j = 1; j <=i; j++) {
                    if (j > 1) {
                        writer.write(", ");
                    }
                    writer.write(String.format("i%d", j));
                }
                writer.write("));\n");
                writer.write(
                        "            }\n" +
                        "        };\n" +
                        "    }\n\n");
            }
            writer.write("}\n");
        } finally {
            writer.close();
        }

    }

    private static String buildTypeArgs(final int dimension) {
        final StringBuilder typeArgumentBuilder = new StringBuilder();
        for (int i = 1; i <= dimension; i++) {
            if (i > 1) {
                typeArgumentBuilder.append(", ");
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
