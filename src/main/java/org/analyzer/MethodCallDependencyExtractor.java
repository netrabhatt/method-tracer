package org.analyzer;

import com.github.javaparser.*;
import com.github.javaparser.ast.*;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class MethodCallDependencyExtractor {
    public static void main(String[] args) {
        // Configure the JavaParser with symbol resolution
        ParserConfiguration config = new ParserConfiguration();
        CombinedTypeSolver combinedSolver = new CombinedTypeSolver(
                new ReflectionTypeSolver(),
                new JavaParserTypeSolver(new File("D:\\git\\simulator\\src\\main\\java"))
        );
        config.setSymbolResolver(new JavaSymbolSolver(combinedSolver));
        StaticJavaParser.setConfiguration(config);

        String f = "D:\\git\\simulator\\src\\main\\java";

        findJavaFiles(new File(f)).forEach(x ->
        {
            try {
                callTrace(x);
            } catch (Exception e) {
//                System.out.println("---");
            }
        });


    }

    public static List<File> findJavaFiles(File directory) {
        List<File> javaFiles = new ArrayList<>();
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        javaFiles.addAll(findJavaFiles(file));
                    } else if (file.getName().endsWith(".java")) {
                        javaFiles.add(file);
                    }
                }
            }
        }
        return javaFiles;
    }

    static void callTrace(File file) throws FileNotFoundException {
//        String filename = "D:\\git\\simulator\\src\\main\\java\\com\\task\\domains\\BullDozer.java";
        // Parse the specified Java file
        CompilationUnit cu = StaticJavaParser.parse(file);

        // Map to store method names and the methods they call
        Map<String, Set<String>> methodCallsMap = new HashMap<>();
        Set<String> callTraces = new HashSet<>();

        // Visitor to track method calls and the methods from which they are made
        cu.accept(new VoidVisitorAdapter<Void>() {
            @Override
            public void visit(MethodDeclaration n, Void arg) {
                super.visit(n, arg);
                String currentMethodName = n.resolve().getQualifiedSignature();
                String pkg = n.resolve().getPackageName();
                String className = n.resolve().getPackageName() + "." + n.resolve().getClassName();
                if (pkg.startsWith("java"))
                    return;

                n.accept(new VoidVisitorAdapter<Void>() {
                    @Override
                    public void visit(MethodCallExpr mce, Void arg) {
                        super.visit(mce, arg);
                        try {
                            // Get the qualified signature of the called method
                            String calledMethodName = mce.resolve().getQualifiedSignature();
                            if (!calledMethodName.startsWith("java"))
                                callTraces.add(new StringBuilder()
                                        .append(calledMethodName)
                                        .append('#')
                                        .append(currentMethodName).toString());
                        } catch (Exception e) {
                            System.out.println("Failed to resolve called method: " + mce);
                        }
                    }
                }, null);
            }
        }, null);

        callTraces.forEach(System.out::println);
    }
}

