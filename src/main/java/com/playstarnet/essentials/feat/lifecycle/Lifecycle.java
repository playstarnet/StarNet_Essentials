package com.playstarnet.essentials.feat.lifecycle;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class Lifecycle {

    private final List<Task> functionStack = Collections.synchronizedList(new ArrayList<>());
    private final Map<String, CompletableFuture<Void>> asyncFunctionsStack = new ConcurrentHashMap<>();

    public void tick() {
        // Execute synchronous tasks
        synchronized (functionStack) {
            for (Task func : functionStack) {
                try {
                    func.run();
                } catch (Exception e) {
                    System.err.println("Error executing task in functionStack: " + e.getMessage());
                }
            }
        }

        // Execute asynchronous tasks
        asyncFunctionsStack.forEach((taskName, asyncFunc) -> {
            asyncFunc.exceptionally(e -> {
                System.err.println("Error in async task '" + taskName + "': " + e.getMessage());
                return null;
            }).thenRun(() -> {
                // Cleanup after task completion
                asyncFunctionsStack.remove(taskName);
            });
        });
    }

    public Lifecycle add(Task func) {
        functionStack.add(func);
        return this;
    }

    public Lifecycle addAsync(String taskName, CompletableFuture<Void> asyncFunc) {
        asyncFunctionsStack.put(taskName, asyncFunc);
        return this;
    }

    public boolean hasAsync(String taskName) {
        return asyncFunctionsStack.containsKey(taskName);
    }
}
