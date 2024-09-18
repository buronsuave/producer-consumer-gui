package models;

public class Buffer
{
    // Owner is either the prod/cons id or -1 if free
    private int owner;
    private int items;
    private int max;

    public Buffer(int max)
    {
        owner = -1;
        items = 0;
        this.max = max;
    }

    public synchronized boolean allocBuffer(int id)
    {
        // Check if already in use. If no, change the owner
        if (owner != -1) return false;
        owner = id; return true;
    }

    public synchronized void releaseBuffer()
    {
        // Restates owner to free value
        owner = -1;
    }

    public synchronized boolean pushItem()
    {
        // Action performed by Producer. If buffer is full, returns false.
        // Otherwise, increase the number of items.
        if (items == max) return false;
        items++; return true;
    }

    public synchronized boolean popItem()
    {
        // Action performed by Consumer. If buffer is empty, returns false.
        // Otherwise, decrease the number of items.
        if (items == 0) return false;
        items--; return true;
    }

    public synchronized int getOwner()
    {
        return owner;
    }

    public synchronized void setMax(int max)
    {
        this.max = max;
    }

    public synchronized void remFirstItem()
    {
            items--;
    }
}
