package org.analyzer;

import java.util.*;

public class Lineage {
    private static final Map<String, Set<String>> childToParentMap = new HashMap<>();

    public static void main(String[] args) {
        // Example input data
        String data = "com.task.domains.BullDozer.advance(int)#com.task.commands.AdvanceCommand.execute()\n" +
                "com.task.commands.Command.execute()#com.task.commands.CommandRunner.runCommand(com.task.commands.Command)\n" +
                "com.task.commands.CommandRunner.close()#com.task.commands.QuitCommand.execute()\n" +
                "com.task.domains.BullDozer.left()#com.task.commands.TurnLeftCommand.execute()\n" +
                "com.task.domains.BullDozer.right()#com.task.commands.TurnRightCommand.execute()\n" +
                "com.task.domains.Position.getRow()#com.task.domains.BullDozer.requireBullDozerWithinBoundary()\n" +
                "com.task.domains.BullDozer.clearCurrentCell()#com.task.domains.BullDozer.advance(int)\n" +
                "com.task.domains.Position.getCol()#com.task.domains.BullDozer.requireBullDozerWithinBoundary()\n" +
                "com.task.domains.GridObject.fuelUsage()#com.task.domains.BullDozer.updateDozerStats(boolean, com.task.domains.GridObject)\n" +
                "com.task.domains.GridObject.symbol()#com.task.domains.BullDozer.initDozerStats()\n" +
                "com.task.domains.BullDozerStats.incrementProtectedTreesDestroyed()#com.task.domains.BullDozer.requireNoProtectedTreeVisit(com.task.domains.GridObject)\n" +
                "com.task.domains.GridObject.symbol()#com.task.domains.BullDozer.clearCurrentCell()\n" +
                "com.task.domains.BullDozer.requireBullDozerWithinBoundary()#com.task.domains.BullDozer.getCurrentObject()\n" +
                "com.task.domains.BullDozer.updateDozerStats(boolean, com.task.domains.GridObject)#com.task.domains.BullDozer.advance(int)\n" +
                "com.task.domains.Position.getRow()#com.task.domains.BullDozer.clearCurrentCell()\n" +
                "com.task.domains.GridObject.resolveObject(char)#com.task.domains.BullDozer.getCurrentObject()\n" +
                "com.task.domains.BullDozer.requireNoProtectedTreeVisit(com.task.domains.GridObject)#com.task.domains.BullDozer.getCurrentObject()\n" +
                "com.task.domains.BullDozerStats.incrementCommOverhead()#com.task.domains.BullDozer.left()\n" +
                "com.task.domains.BullDozerStats.setTotalCells(int)#com.task.domains.BullDozer.initDozerStats()\n" +
                "com.task.domains.BullDozerStats.incrementPaintDamages()#com.task.domains.BullDozer.updateDozerStats(boolean, com.task.domains.GridObject)\n" +
                "com.task.domains.Position.getCol()#com.task.domains.BullDozer.updateDozerStats(boolean, com.task.domains.GridObject)\n" +
                "com.task.domains.Position.getCol()#com.task.domains.BullDozer.getCurrentObject()\n" +
                "com.task.domains.BullDozerStats.incrementVisitedSquares()#com.task.domains.BullDozer.updateDozerStats(boolean, com.task.domains.GridObject)\n" +
                "com.task.domains.BullDozerStats.setProtectedTrees(int)#com.task.domains.BullDozer.initDozerStats()\n" +
                "com.task.domains.Direction.left()#com.task.domains.BullDozer.left()\n" +
                "com.task.domains.GridObject.isTree()#com.task.domains.BullDozer.updateDozerStats(boolean, com.task.domains.GridObject)\n" +
                "com.task.domains.Position.getRow()#com.task.domains.BullDozer.getCurrentObject()\n" +
                "com.task.domains.Direction.right()#com.task.domains.BullDozer.right()\n" +
                "com.task.domains.Position.getCol()#com.task.domains.BullDozer.clearCurrentCell()\n" +
                "com.task.domains.BullDozer.getCurrentObject()#com.task.domains.BullDozer.advance(int)\n" +
                "com.task.domains.Position.moveOneStep(com.task.domains.Direction)#com.task.domains.BullDozer.advance(int)\n" +
                "com.task.domains.BullDozerStats.incrementCommOverhead()#com.task.domains.BullDozer.right()\n" +
                "com.task.domains.BullDozerStats.updateFuelUsage(int)#com.task.domains.BullDozer.updateDozerStats(boolean, com.task.domains.GridObject)\n" +
                "com.task.domains.GridObject.isProtectedTree()#com.task.domains.BullDozer.requireNoProtectedTreeVisit(com.task.domains.GridObject)\n" +
                "com.task.domains.BullDozerStats.incrementCommOverhead()#com.task.domains.BullDozer.advance(int)\n" +
                "com.task.domains.Position.getRow()#com.task.domains.BullDozer.updateDozerStats(boolean, com.task.domains.GridObject)\n" +
                "com.task.domains.BullDozerStats.unclearedSquares()#com.task.domains.BullDozerStats.expenseResponse()\n" +
                "com.task.domains.Grid.requireBullDozerWithinBoundary(int, int)#com.task.domains.Grid.next(com.task.domains.Direction, com.task.domains.Cell)\n" +
                "com.task.domains.Cell.getCol()#com.task.domains.Grid.next(com.task.domains.Direction, com.task.domains.Cell)\n" +
                "com.task.domains.Cell.getRow()#com.task.domains.Grid.next(com.task.domains.Direction, com.task.domains.Cell)\n" +
                "com.task.domains.BullDozer.advance(int)#LAUDA";
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
