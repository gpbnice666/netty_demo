package com.bo.netty.common;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class TaskManager {

    private static final TaskManager INSTANCE = new TaskManager();

    private ConcurrentHashMap<Integer, LinkedBlockingQueue<Runnable>> taskMap = new ConcurrentHashMap<>();

    private TaskManager() {
    }

    public static TaskManager getInstance() {
        return INSTANCE;
    }

    public ConcurrentHashMap<Integer, LinkedBlockingQueue<Runnable>> getTaskMap() {
        return taskMap;
    }
}
