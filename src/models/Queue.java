package models;

import java.util.ArrayList;

public class Queue
{
    // List of IDs queued
    private ArrayList<Integer> queueIds;

    // Notice that this MAX is consistent with the number of
    // prod/cons to be animated
    private static final int MAX_SIZE = 5;

    public Queue()
    {
        queueIds = new ArrayList<>();
    }

    public synchronized void push(int id)
    {
        // Always add to the end of the queue
        queueIds.add(id);
    }

    public synchronized void pop()
    {
        // Always remove the very first element
        queueIds.remove(0);
    }

    public synchronized int size()
    {
        return queueIds.size();
    }

    public synchronized ArrayList<Integer> getQueueIds()
    {
        return queueIds;
    }

    public synchronized int getNext()
    {
        return queueIds.get(0);
    }
}
