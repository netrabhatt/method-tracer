package org.analyzer;

import java.util.*;

public class Lineage {
    private static final Map<String, Set<String>> childToParentMap = new HashMap<>();

    public static void main(String[] args) {
        // Example input data
        String data = "";
        parseData(data);

        // Example: Get lineage of a specific method
        String childMethod = "com.task.domains.GridObject.isProtectedTree()";
        System.out.println("Lineage of " + childMethod + ":");
        printLineage(childMethod, "");
    }

    private static void parseData(String data) {
        Scanner scanner = new Scanner(data);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (!line.isEmpty()) {
                String[] parts = line.split("#");
                childToParentMap.computeIfAbsent(parts[0], k -> new HashSet<>()).add(parts[1]);
            }
        }
        scanner.close();
    }

    private static void printLineage(String method, String path) {
        String newPath = path.isEmpty() ? method : path + " -> " + method;

        Set<String> parents = childToParentMap.get(method);
        if (parents == null || parents.isEmpty()) {
            System.out.println(newPath);  // Print the complete path when no further ancestors are found
            return;
        }

        for (String parent : parents) {
            printLineage(parent, newPath);
        }
    }
}
