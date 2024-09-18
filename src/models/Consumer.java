package models;

import graphics.AnimationPanel;

public class Consumer implements Runnable
{
    private static final int TIME_FOR_ANIMATION = 500;
    private static final int TIME_IN_BUFFER = 500;

    // States:
    // 0: Consuming
    // 1: In Queue
    // 2: Sleeping
    private int state;

    // Odd numbers (2i+1)
    private final int id;

    // Buffer reference
    private final Buffer buffer;
    private final Queue consumerQueue;

    // Animation Panel reference
    private final AnimationPanel ap;

    public Consumer(int id, Buffer buffer, Queue consumerQueue, AnimationPanel ap)
    {
        this.id = id;
        this.buffer = buffer;
        this.consumerQueue = consumerQueue;
        this.ap = ap;
        state = 1; // Default state
    }

    @Override
    public void run()
    {
        try {
            Thread.sleep(50);
            ap.logMonitor("Consumer " + id + " just joined.");
            ap.moveConsumerToQueue(this); // <- Call the animation to move to production area
            Thread.sleep(TIME_FOR_ANIMATION); // Time for animation

            while (true) {
                do
                {
                    joinQueue();
                    // Since process is at queue, wait until it can allocate the buffer
                    // Notice that all animation transactions will be managed by the top panel
                    // (such as moving along the queue)
                    while (true)
                    {
                        if (consumerQueue.getNext() == id && buffer.allocBuffer(id)) break;
                        Thread.sleep((int) (Math.random() * 50));
                    }
                } while (!getItem());
                // When get item doesn't go well, rejoins to queue and try again after sleep
                buffer.releaseBuffer();
                ap.logMonitor("Consumer " + id + " released buffer.");

                // Finally, consume the product
                // About 3~5 seconds here
                consume();
            }
        }
        catch (InterruptedException e) {
            // If interrupted, assume it's the exit signal.
            // Release buffer if it's in use
            ap.logMonitor("Consumer " + id + " just left.");
            if (buffer.getOwner() == id) buffer.releaseBuffer();
        }
    }

    private void consume() throws InterruptedException
    {
        state = 0; // Change state to producing

        ap.updateConsumerLabel(this); // <- Call the update label method
        ap.logMonitor("Consumer " + id + " started consuming."); // <- Replace with monitor call
        Thread.sleep(3000 + (int)(Math.random() * 2000));
    }

    private void joinQueue() throws InterruptedException {
        state = 1; // Change state to joined queue
        ap.updateConsumerLabel(this); // <- Call the update label method

        consumerQueue.push(id);
        ap.updateQueueLabels();
        ap.moveConsumerToQueue(this); // <- Call the animation transition to move into actual queue position
        Thread.sleep(TIME_FOR_ANIMATION); // Time for animation
        ap.logMonitor("Consumer " + id + " joined to consumer queue."); // <- Replace with monitor call
    }

    private boolean getItem() throws InterruptedException {
        consumerQueue.pop(); // Leave queue first
        ap.updateQueueLabels();

        ap.logMonitor("Consumer " + id + " entered buffer."); // <- Replace with monitor call
        ap.moveConsumerIntoBuffer(this);
        // ^^^ Call the animation transition to move into actual buffer position.
        //    This also calls the methods to move consumers queue.
        Thread.sleep(TIME_FOR_ANIMATION); // Time for animation

        Thread.sleep(TIME_IN_BUFFER);
        //Try to put item
        if (buffer.popItem())
        {
            ap.logMonitor("Consumer " + id + " successfully decreased buffer."); // <- Replace with monitor call
            ap.animateRemBuffer(); // <- Call the animation transition to update buffer
            Thread.sleep(TIME_FOR_ANIMATION); // Time for animation

            // Lets check...
            ap.moveToConsumeArea(this); // <- Call the animation to move to consuming area
            Thread.sleep(TIME_FOR_ANIMATION); // Time for animation
            return true;
        }
        else // Buffer is empty
        {
            ap.logMonitor("Consumer " + id + " realized buffer is empty. Going to sleep"); // <- Replace with monitor call
            ap.moveConsumerToSleep(this); // <- Call the animation transition to going to sleep
            Thread.sleep(TIME_FOR_ANIMATION); // Time for animation
            buffer.releaseBuffer();
            ap.logMonitor("Producer " + id + " released buffer"); // <- Replace with monitor call
            sleep();
            return false;
        }
    }

    private void sleep() throws InterruptedException {
        state = 2; // Sleeping
        ap.updateConsumerLabel(this); // <- Call the update label method
        ap.logMonitor("Consumer " + id + " is sleeping."); // <- Replace with monitor call
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
