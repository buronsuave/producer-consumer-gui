package models;

import graphics.AnimationPanel;

public class Producer implements Runnable
{
    private static final int TIME_FOR_ANIMATION = 500;
    private static final int TIME_IN_BUFFER = 500;

    // States:
    // 0: Producing
    // 1: In Queue
    // 2: Sleeping
    private int state;

    // Even numbers (2i)
    private final int id;

    // Buffer reference
    private final Buffer buffer;
    private final Queue producerQueue;

    // Animation Panel reference
    private final AnimationPanel ap;

    public Producer(int id, Buffer buffer, Queue producerQueue, AnimationPanel ap)
    {
        this.id = id;
        this.buffer = buffer;
        this.producerQueue = producerQueue;
        this.ap = ap;
        state = 0; // Default state
    }

    @Override
    public void run()
    {
        try {
            Thread.sleep(50);
            ap.logMonitor("Producer " + id + " just joined.");
            ap.moveToProduceArea(this); // <- Call the animation to move to production area
            Thread.sleep(TIME_FOR_ANIMATION); // Time for animation

            while (true) {
                // About 3~5 seconds here
                produce();

                do
                {
                    joinQueue();
                    // Since process is at queue, wait until it can allocate the buffer
                    // Notice that all animation transactions will be managed by the top panel
                    // (such as moving along the queue)
                    while (true)
                    {
                        if (producerQueue.getNext() == id && buffer.allocBuffer(id)) break;
                        Thread.sleep((int) (Math.random() * 50));
                    }

                } while (!addItem());
                // If add item doesn't go well, rejoins to queue and try again after sleep
                buffer.releaseBuffer();
                ap.logMonitor("Producer " + id + " released buffer.");
            }
        }
        catch (InterruptedException e)
        {
            // If interrupted, assume it's the exit signal.
            // Release buffer if it's in use
            ap.logMonitor("Producer " + id + " just left.");
            if (buffer.getOwner() == id) buffer.releaseBuffer();
        }
    }

    private void produce() throws InterruptedException
    {
        state = 0; // Change state to producing

        ap.updateProducerLabel(this); // <- Call the update label method
        ap.logMonitor("Producer " + id + " started producing."); // <- Replace with monitor call
        Thread.sleep(3000 + (int)(Math.random() * 2000));
    }

    private void joinQueue() throws InterruptedException {
        state = 1; // Change state to joined queue
        ap.updateProducerLabel(this); // <- Call the update label method

        producerQueue.push(id);
        ap.updateQueueLabels();
        ap.moveProducerToQueue(this); // <- Call the animation transition to move into actual queue position
        Thread.sleep(TIME_FOR_ANIMATION); // Time for animation
        ap.logMonitor("Producer " + id + " joined to producer queue."); // <- Replace with monitor call
    }

    private boolean addItem() throws InterruptedException {
        producerQueue.pop(); // Leave queue first
        ap.updateQueueLabels();

        ap.logMonitor("Producer " + id + " entered buffer."); // <- Replace with monitor call
        ap.moveProducerIntoBuffer(this);
        Thread.sleep(TIME_FOR_ANIMATION); // Time for animation
        // ^^ Call the animation transition to move into actual buffer position.
        //    This also calls the methods to move producers queue.

        Thread.sleep(TIME_IN_BUFFER);

        //Try to put item
        if (buffer.pushItem())
        {
            ap.logMonitor("Producer " + id + " successfully increased buffer."); // <- Replace with monitor call
            ap.animateAddBuffer(); // <- Call the animation transition to update buffer
            Thread.sleep(TIME_FOR_ANIMATION); // Time for animation

            // Lets check...
            ap.moveToProduceArea(this);
            Thread.sleep(TIME_FOR_ANIMATION); // Time for animation
            return true;
        }
        else // Buffer is full
        {
            ap.logMonitor("Producer " + id + " realized buffer is full. Going to sleep"); // <- Replace with monitor call
            ap.moveProducerToSleep(this); // <- Call the animation transition to going to sleep
            Thread.sleep(TIME_FOR_ANIMATION); // Time for animation
            buffer.releaseBuffer();
            ap.logMonitor("Producer " + id + " released buffer"); // <- Replace with monitor call
            sleep();
            return false;
        }
    }

    private void sleep() throws InterruptedException {
        state = 2; // Sleeping
        ap.updateProducerLabel(this); // <- Call the update label method
        ap.logMonitor("Producer " + id + " is sleeping."); // <- Replace with monitor call
        Thread.sleep(3000 + (int)(Math.random() * 2000));


    }

    public int getId()
    {
        return id;
    }

    public int getState()
    {
        return state;
    }
}
