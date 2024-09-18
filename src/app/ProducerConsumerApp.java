package app;

import graphics.AnimationPanel;
import graphics.MonitorPanel;

import javax.swing.*;
import java.awt.*;

public class ProducerConsumerApp extends JFrame
{
    private final AnimationPanel ap;
    private final MonitorPanel mp;

    private static final int INITIAL_PRODUCERS = 5;
    private static final int INITIAL_CONSUMERS = 1;
    private static final int INITIAL_MAX_BUFFER = 1;

    private int nProducers;
    private int nConsumers;
    private int maxBuffer;
    private static final int FRAME_WIDTH = 1385;
    private static final int FRAME_HEIGHT = 970;

    public ProducerConsumerApp(int nProducers, int nConsumers, int maxBuffer)
    {
        this.nProducers = nProducers;
        this.nConsumers = nConsumers;
        this.maxBuffer = maxBuffer;

        setTitle("Producer Consumer App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLayout(new BorderLayout());

        ap = new AnimationPanel(nProducers, nConsumers, maxBuffer, this);
        add(ap.getPanel(), BorderLayout.CENTER);

        mp = new MonitorPanel(nProducers, nConsumers, maxBuffer, this);
        add(mp.getPanel(), BorderLayout.EAST);

        ap.startAnimation();
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> {
            new ProducerConsumerApp(INITIAL_PRODUCERS, INITIAL_CONSUMERS, INITIAL_MAX_BUFFER).setVisible(true);
        });
    }

    public void logMonitor(String msg)
    {
        mp.logMonitor(msg);
    }

    public synchronized void addProducer()
    {
        nProducers++;
        mp.setNProducers(nProducers);
        ap.addProducer();
    }

    public synchronized void remProducer()
    {
        nProducers--;
        mp.setNProducers(nProducers);
        ap.remProducer();
    }

    public synchronized void addConsumer()
    {
        nConsumers++;
        mp.setNConsumers(nConsumers);
        ap.addConsumer();
    }

    public synchronized void remConsumer()
    {
        nConsumers--;
        mp.setNConsumers(nConsumers);
        ap.remConsumer();
    }

    public synchronized void addBufferSlot()
    {
        maxBuffer++;
        logMonitor("Buffer size changed up to " + maxBuffer + " items");
        mp.setMaxBuffer(maxBuffer);
        ap.addBufferSlot();
    }

    public synchronized void remBufferSlot()
    {
        maxBuffer--;
        logMonitor("Buffer size changed up to " + maxBuffer + " items");
        mp.setMaxBuffer(maxBuffer);
        ap.remBufferSlot();

    }
}
