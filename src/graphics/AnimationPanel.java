package graphics;

import app.ProducerConsumerApp;
import models.Buffer;
import models.Consumer;
import models.Producer;
import models.Queue;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AnimationPanel
{
    private final ProducerConsumerApp context;

    private JPanel panel;
    private final ArrayList<Producer> producers;
    private final ArrayList<Consumer> consumers;
    private final ArrayList<JLabel> producerLabels;
    private final ArrayList<JLabel> consumerLabels;
    private final ArrayList<JLabel> itemLabels;
    private final ArrayList<Timer> itemTimers;
    private final Queue producersQueue;
    private final Queue consumersQueue;
    private final ArrayList<Timer> producerTimers;
    private final ArrayList<Timer> consumerTimers;
    private final ArrayList<Thread> producerThreads;
    private final ArrayList<Thread> consumerThreads;

    private int nProducers;
    private int nConsumers;
    private final Buffer buffer;
    private int maxBuffer;

    private static final String[] PRODUCER_LABEL_ICONS = {
            "./res/producer0.jpeg",
            "./res/producer1.jpeg",
            "./res/producer2.jpeg"
    };
    private static final String[] CONSUMER_LABEL_ICONS = {
            "./res/consumer0.jpeg",
            "./res/consumer1.jpeg",
            "./res/consumer2.jpeg"
    };
    private static final String ITEM_LABEL = "./res/item.jpeg";
    private static final String BUFFER_START_LABEL = "./res/start.jpeg";
    private static final String BUFFER_END_LABEL = "./res/end.jpeg";
    private static final int PRODUCER_LABEL_WIDTH = 75;
    private static final int PRODUCER_LABEL_HEIGHT = 90;
    private static final int CONSUMER_LABEL_WIDTH = 75;
    private static final int CONSUMER_LABEL_HEIGHT = 90;
    private static final int ITEM_LABEL_WIDTH = 75;
    private static final int ITEM_LABEL_HEIGHT = 75;

    private static final int MOV_ANIMATION_TIME = 475;
    private static final int MOV_ANIMATION_DELAY = 5;
    private static final int MOV_ANIMATION_STEPS = MOV_ANIMATION_TIME / MOV_ANIMATION_DELAY;
    private static final int PRODUCTION_AREA_X = 10;
    private static final int PRODUCTION_AREA_Y = 10;
    private static final int PRODUCERS_QUEUE_X = 150;
    private static final int PRODUCERS_QUEUE_Y = 265;
    private static final int PRODUCERS_BUFFER_X = 300;
    private static final int PRODUCERS_BUFFER_Y = 265;
    private static final int BUFFER_X = 450;
    private static final int BUFFER_Y = 265;
    private static final int PRODUCERS_SLEEP_AREA_X = 10;
    private static final int PRODUCERS_SLEEP_AREA_Y = 470;
    private static final int CONSUMING_AREA_X = 900;
    private static final int CONSUMING_AREA_Y = 10;
    private static final int CONSUMERS_QUEUE_X = 750;
    private static final int CONSUMERS_QUEUE_Y = 265;
    private static final int CONSUMERS_BUFFER_X = 600;
    private static final int CONSUMERS_BUFFER_Y = 265;
    private static final int CONSUMERS_SLEEP_AREA_X = 900;
    private static final int CONSUMERS_SLEEP_AREA_Y = 470;

    private final JLabel bufferStart;
    private final JLabel bufferEnd;
    private Timer bufferEndTimer;

    public AnimationPanel(int nProducers, int nConsumers, int maxBuffer, ProducerConsumerApp context)
    {
        this.context = context;
        this.nProducers = nProducers;
        this.nConsumers = nConsumers;
        this.maxBuffer = maxBuffer;

        producers = new ArrayList<>();
        consumers = new ArrayList<>();
        producerLabels = new ArrayList<>();
        consumerLabels = new ArrayList<>();
        itemLabels = new ArrayList<>();
        itemTimers = new ArrayList<>();
        producersQueue = new Queue();
        consumersQueue = new Queue();
        producerTimers = new ArrayList<>();
        consumerTimers = new ArrayList<>();
        producerThreads = new ArrayList<>();
        consumerThreads = new ArrayList<>();
        bufferStart = new JLabel();
        bufferEnd = new JLabel();

        buffer = new Buffer(maxBuffer);
        initPanel();
    }

    private void initPanel()
    {
        panel = new JPanel()
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                super.paintComponent(g);
                g.drawRect(0, 5, 95, 460);          // Production Area
                g.drawRect(0, 465, 95, 460);        // Producers Sleep Area
                g.drawRect(895, 5, 95, 460);        // Consuming Area
                g.drawRect(895, 465, 95, 460);      // Consumers Sleep Area
                g.drawRect(95, 5, 165, 920);        // Producers Queue Area
                g.drawRect(730, 5, 165, 920);       // Consumers Queue Area
            }
        };
        // ^^^ Draw layout

        panel.setLayout(null);
        panel.setPreferredSize(new Dimension(1200, 1000));
        fillArrays();

        bufferStart.setIcon(new ImageIcon(BUFFER_START_LABEL));
        bufferStart.setLocation(BUFFER_X, BUFFER_Y - (ITEM_LABEL_HEIGHT+25));
        bufferStart.setSize(new Dimension(ITEM_LABEL_WIDTH, ITEM_LABEL_HEIGHT));
        panel.add(bufferStart);

        bufferEnd.setIcon(new ImageIcon(BUFFER_END_LABEL));
        bufferEnd.setLocation(BUFFER_X, BUFFER_Y + maxBuffer*(ITEM_LABEL_HEIGHT+25));
        bufferEnd.setSize(new Dimension(ITEM_LABEL_WIDTH, ITEM_LABEL_HEIGHT));
        panel.add(bufferEnd);

        panel.repaint();
    }

    private void fillArrays()
    {
        for (int i = 0; i < nProducers; ++i)
        {
            producers.add(new Producer(2*i, buffer, producersQueue, this));
            producerLabels.add(new JLabel());
            producerLabels.get(i).setText("P" + i);
            producerLabels.get(i).setHorizontalTextPosition(JLabel.CENTER);
            producerLabels.get(i).setVerticalTextPosition(JLabel.NORTH);
            producerLabels.get(i).setSize(PRODUCER_LABEL_WIDTH, PRODUCER_LABEL_HEIGHT);
            producerLabels.get(i).setIcon(new ImageIcon(PRODUCER_LABEL_ICONS[0])); // Default producer state
            producerLabels.get(i).setLocation(PRODUCTION_AREA_X, PRODUCTION_AREA_Y + i*PRODUCER_LABEL_HEIGHT);
            panel.add(producerLabels.get(producerLabels.size()-1));
            producerTimers.add(new Timer(1, actionEvent -> {})); // Default timer (replaced later)
        }

        for (int i = 0; i < nConsumers; ++i)
        {
            consumers.add(new Consumer(2*i+1, buffer, consumersQueue, this));
            consumerLabels.add(new JLabel());
            consumerLabels.get(i).setText("C" + i);
            consumerLabels.get(i).setHorizontalTextPosition(JLabel.CENTER);
            consumerLabels.get(i).setVerticalTextPosition(JLabel.NORTH);
            consumerLabels.get(i).setSize(CONSUMER_LABEL_WIDTH, CONSUMER_LABEL_HEIGHT);
            consumerLabels.get(i).setIcon(new ImageIcon(CONSUMER_LABEL_ICONS[1])); // Default consumer state
            consumerLabels.get(i).setLocation(CONSUMERS_QUEUE_X, CONSUMERS_QUEUE_Y + i*(CONSUMER_LABEL_HEIGHT + 10));
            panel.add(consumerLabels.get(consumerLabels.size()-1));
            consumerTimers.add(new Timer(1, actionEvent -> {})); // Default timer (replaced later)
        }
    }

    // Model animation calls
    public void updateProducerLabel(Producer p)
    {
        producerLabels.get(p.getId()/2)
                .setIcon(new ImageIcon(PRODUCER_LABEL_ICONS[p.getState()]));
    }

    public void moveToProduceArea(Producer p)
    {
        int index = p.getId()/2;
        // producerLabels.get(index).setLocation(PRODUCTION_AREA_X, PRODUCTION_AREA_Y + index*PRODUCER_LABEL_HEIGHT);
        // panel.repaint();

        int x0 = producerLabels.get(index).getX();
        int y0 = producerLabels.get(index).getY();
        double dx = (double) (PRODUCTION_AREA_X - x0) / MOV_ANIMATION_STEPS;
        double dy = (double) (PRODUCTION_AREA_Y + index*PRODUCER_LABEL_HEIGHT - y0) / MOV_ANIMATION_STEPS;

        producerTimers.set(index, new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int xn = (int) (x0 + currentStep * dx);
                    int yn = (int) (y0 + currentStep * dy);
                    producerLabels.get(index).setLocation(xn, yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    panel.repaint();
                    ((Timer) actionEvent.getSource()).stop();
                }
            }
        }));

        producerTimers.get(index).start();
    }

    public void moveProducerToQueue(Producer p)
    {
        int index = p.getId()/2;
        // producerLabels.get(index).setLocation(PRODUCERS_QUEUE_X, PRODUCERS_QUEUE_Y + index*(PRODUCER_LABEL_HEIGHT+10));
        // panel.repaint();

        int x0 = producerLabels.get(index).getX();
        int y0 = producerLabels.get(index).getY();

        double dx = (double) (PRODUCERS_QUEUE_X - x0) / MOV_ANIMATION_STEPS;
        double dy = (double) (PRODUCERS_QUEUE_Y + index*(PRODUCER_LABEL_HEIGHT+10) - y0) / MOV_ANIMATION_STEPS;

        producerTimers.set(index, new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int xn = (int) (x0 + currentStep * dx);
                    int yn = (int) (y0 + currentStep * dy);
                    producerLabels.get(index).setLocation(xn, yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    panel.repaint();
                    ((Timer) actionEvent.getSource()).stop();
                }
            }
        }));

        producerTimers.get(index).start();
    }

    public synchronized void moveProducerIntoBuffer(Producer p)
    {
        int index = p.getId()/2;
        // producerLabels.get(index).setLocation(PRODUCERS_BUFFER_X, PRODUCERS_BUFFER_Y + index*(PRODUCER_LABEL_HEIGHT+10));
        // panel.repaint();

        int x0 = producerLabels.get(index).getX();
        int y0 = producerLabels.get(index).getY();

        double dx = (double) (PRODUCERS_BUFFER_X - x0) / MOV_ANIMATION_STEPS;
        double dy = (double) (PRODUCERS_BUFFER_Y + index*(PRODUCER_LABEL_HEIGHT+10) - y0) / MOV_ANIMATION_STEPS;

        producerTimers.set(index, new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int xn = (int) (x0 + currentStep * dx);
                    int yn = (int) (y0 + currentStep * dy);
                    producerLabels.get(index).setLocation(xn, yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    panel.repaint();
                    ((Timer) actionEvent.getSource()).stop();
                }
            }
        }));

        producerTimers.get(index).start();
    }

    public synchronized void animateAddBuffer()
    {
        for (int i = 0; i < itemLabels.size(); ++i) shiftUpItem(itemLabels.get(i), i);

        // Create new item
        JLabel item = new JLabel();
        item.setIcon(new ImageIcon(ITEM_LABEL));
        item.setLocation(BUFFER_X, BUFFER_Y);
        item.setSize(new Dimension(ITEM_LABEL_WIDTH, ITEM_LABEL_HEIGHT));
        panel.add(item);
        itemLabels.add(item);
        itemTimers.add(new Timer(1, actionEvent -> {})); // Default timer (replaced later)

        panel.repaint();
    }

    private void shiftUpItem(JLabel item, int i)
    {
        int y0 = item.getY();
        // item.setLocation(item.getX(), y0+ITEM_LABEL_HEIGHT+25);
        // panel.repaint();

        // Add some margin between items in buffer.
        double dy = (double) (ITEM_LABEL_HEIGHT+25) / MOV_ANIMATION_STEPS;

        itemTimers.set(i, new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int yn = (int) (y0 + currentStep * dy);
                    item.setLocation(item.getX(), yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    ((Timer) actionEvent.getSource()).stop();
                    panel.repaint();
                }
            }
        }));

        itemTimers.get(i).start();
    }

    public void moveProducerToSleep(Producer p)
    {
        int index = p.getId()/2;
        // producerLabels.get(index).setLocation(PRODUCERS_SLEEP_AREA_X, PRODUCERS_SLEEP_AREA_Y + index*PRODUCER_LABEL_HEIGHT);
        // panel.repaint();

        int x0 = producerLabels.get(index).getX();
        int y0 = producerLabels.get(index).getY();
        double dx = (double) (PRODUCERS_SLEEP_AREA_X - x0) / MOV_ANIMATION_STEPS;
        double dy = (double) (PRODUCERS_SLEEP_AREA_Y + index*PRODUCER_LABEL_HEIGHT - y0) / MOV_ANIMATION_STEPS;

        producerTimers.set(index, new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int xn = (int) (x0 + currentStep * dx);
                    int yn = (int) (y0 + currentStep * dy);
                    producerLabels.get(index).setLocation(xn, yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    panel.repaint();
                    ((Timer) actionEvent.getSource()).stop();
                }
            }
        }));

        producerTimers.get(index).start();
    }


    public void updateConsumerLabel(Consumer c)
    {
        consumerLabels.get((c.getId()-1)/2)
                .setIcon(new ImageIcon(CONSUMER_LABEL_ICONS[c.getState()]));
    }

    public void moveToConsumeArea(Consumer c)
    {
        int index = (c.getId()-1)/2;
        // consumerLabels.get(index).setLocation(CONSUMING_AREA_X, CONSUMING_AREA_Y + index*CONSUMER_LABEL_HEIGHT);
        // panel.repaint();

        int x0 = consumerLabels.get(index).getX();
        int y0 = consumerLabels.get(index).getY();
        double dx = (double) (CONSUMING_AREA_X - x0) / MOV_ANIMATION_STEPS;
        double dy = (double) (CONSUMING_AREA_Y + index*CONSUMER_LABEL_HEIGHT - y0) / MOV_ANIMATION_STEPS;

        consumerTimers.set(index, new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int xn = (int) (x0 + currentStep * dx);
                    int yn = (int) (y0 + currentStep * dy);
                    consumerLabels.get(index).setLocation(xn, yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    panel.repaint();
                    ((Timer) actionEvent.getSource()).stop();
                }
            }
        }));

        consumerTimers.get(index).start();
    }

    public void moveConsumerToQueue(Consumer c)
    {
        int index = (c.getId()-1)/2;
        // consumerLabels.get(index).setLocation(CONSUMERS_QUEUE_X, CONSUMERS_QUEUE_Y + index*(CONSUMER_LABEL_HEIGHT + 10));
        // panel.repaint();

        int x0 = consumerLabels.get(index).getX();
        int y0 = consumerLabels.get(index).getY();

        double dx = (double) (CONSUMERS_QUEUE_X - x0) / MOV_ANIMATION_STEPS;
        double dy = (double) (CONSUMERS_QUEUE_Y + index*(CONSUMER_LABEL_HEIGHT + 10) - y0) / MOV_ANIMATION_STEPS;

        consumerTimers.set(index, new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int xn = (int) (x0 + currentStep * dx);
                    int yn = (int) (y0 + currentStep * dy);
                    consumerLabels.get(index).setLocation(xn, yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    panel.repaint();
                    ((Timer) actionEvent.getSource()).stop();
                }
            }
        }));

        consumerTimers.get(index).start();
    }

    public synchronized void moveConsumerIntoBuffer(Consumer c)
    {
        int index = (c.getId()-1)/2;
        // consumerLabels.get(index).setLocation(CONSUMERS_BUFFER_X, CONSUMERS_BUFFER_Y + index*(CONSUMER_LABEL_HEIGHT + 10));
        // panel.repaint();

        int x0 = consumerLabels.get(index).getX();
        int y0 = consumerLabels.get(index).getY();

        double dx = (double) (CONSUMERS_BUFFER_X - x0) / MOV_ANIMATION_STEPS;
        double dy = (double) (CONSUMERS_BUFFER_Y + index*(CONSUMER_LABEL_HEIGHT + 10) - y0) / MOV_ANIMATION_STEPS;

        consumerTimers.set(index, new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int xn = (int) (x0 + currentStep * dx);
                    int yn = (int) (y0 + currentStep * dy);
                    consumerLabels.get(index).setLocation(xn, yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    panel.repaint();
                    ((Timer) actionEvent.getSource()).stop();
                }
            }
        }));

        consumerTimers.get(index).start();
    }

    public synchronized void animateRemBuffer()
    {
        for (int i = 0; i < itemLabels.size(); ++i) shiftDownItem(itemLabels.get(i), i);

        // Remove last item inserted
        JLabel item = itemLabels.get(itemLabels.size()-1);
        panel.remove(item);
        if (itemTimers.get(itemLabels.size()-1) != null)
            itemTimers.get(itemLabels.size()-1).stop();
        itemTimers.remove(itemLabels.size()-1);
        itemLabels.remove(item);
        panel.repaint();
    }

    private void shiftDownItem(JLabel item, int i)
    {
        int y0 = item.getY();
        // item.setLocation(item.getX(), y0-ITEM_LABEL_HEIGHT-25);
        // panel.repaint();

        // Add some margin between items in buffer.
        double dy = (double) -(ITEM_LABEL_HEIGHT+25) / MOV_ANIMATION_STEPS;

        itemTimers.set(i, new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int yn = (int) (y0 + currentStep * dy);
                    item.setLocation(item.getX(), yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    panel.repaint();
                    ((Timer) actionEvent.getSource()).stop();
                }
            }
        }));

        itemTimers.get(i).start();
    }

    public void moveConsumerToSleep(Consumer c)
    {
        int index = (c.getId()-1)/2;
        // consumerLabels.get(index).setLocation(CONSUMERS_SLEEP_AREA_X, CONSUMERS_SLEEP_AREA_Y + index*(CONSUMER_LABEL_HEIGHT));
        // panel.repaint();

        int x0 = consumerLabels.get(index).getX();
        int y0 = consumerLabels.get(index).getY();
        double dx = (double) (CONSUMERS_SLEEP_AREA_X - x0) / MOV_ANIMATION_STEPS;
        double dy = (double) (CONSUMERS_SLEEP_AREA_Y + index*(CONSUMER_LABEL_HEIGHT) - y0) / MOV_ANIMATION_STEPS;

        consumerTimers.set(index, new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int xn = (int) (x0 + currentStep * dx);
                    int yn = (int) (y0 + currentStep * dy);
                    consumerLabels.get(index).setLocation(xn, yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    panel.repaint();
                    ((Timer) actionEvent.getSource()).stop();
                }
            }
        }));

        consumerTimers.get(index).start();
    }

    public JPanel getPanel() { return panel; }

    public void startAnimation()
    {
        // Add threads
        for (Producer producer : producers) producerThreads.add(new Thread(producer));
        for (Consumer consumer : consumers) consumerThreads.add(new Thread(consumer));

        // Start threads
        for (Thread producerThread : producerThreads) producerThread.start();
        for (Thread consumerThread : consumerThreads) consumerThread.start();
    }

    public synchronized void updateQueueLabels()
    {
        for (int i = 0; i < producersQueue.size(); ++i)
        {
            int id = producersQueue.getQueueIds().get(i);
            producerLabels.get(id/2).setText("P" + (id/2) + " (" + i + ")");
        }

        for (int i = 0; i < consumersQueue.size(); ++i)
        {
            int id = consumersQueue.getQueueIds().get(i);
            consumerLabels.get((id-1)/2).setText("C" + (id-1)/2 + " (" + i + ")");
        }

        for (int i = 0; i < producers.size(); ++i)
        {
            boolean flag = true;
            for (int j = 0; j < producersQueue.size(); ++j)
            {
                if (producers.get(i).getId() == producersQueue.getQueueIds().get(j))
                {
                    flag = false;
                    break;
                }
            }

            if (flag) producerLabels.get(i).setText("P" + i);
        }

        for (int i = 0; i < consumers.size(); ++i)
        {
            boolean flag = true;
            for (int j = 0; j < consumersQueue.size(); ++j)
            {
                if (consumers.get(i).getId() == consumersQueue.getQueueIds().get(j))
                {
                    flag = false;
                    break;
                }
            }

            if (flag) consumerLabels.get(i).setText("C" + i);
        }

        panel.repaint();
    }

    public void logMonitor(String msg)
    {
        context.logMonitor(msg);
    }

    public void addProducer()
    {
        // Create required items
        producers.add(new Producer(2*nProducers, buffer, producersQueue, this));
        producerLabels.add(new JLabel());
        producerLabels.get(nProducers).setText("P" + nProducers);
        producerLabels.get(nProducers).setHorizontalTextPosition(JLabel.CENTER);
        producerLabels.get(nProducers).setVerticalTextPosition(JLabel.NORTH);
        producerLabels.get(nProducers).setSize(PRODUCER_LABEL_WIDTH, PRODUCER_LABEL_HEIGHT);
        producerLabels.get(nProducers).setIcon(new ImageIcon(PRODUCER_LABEL_ICONS[0])); // Default producer state
        producerLabels.get(nProducers).setLocation(PRODUCTION_AREA_X, PRODUCTION_AREA_Y + nProducers*PRODUCER_LABEL_HEIGHT);
        panel.add(producerLabels.get(producerLabels.size()-1));
        producerTimers.add(new Timer(1, actionEvent -> {})); // Default timer (replaced later)
        producerThreads.add(new Thread(producers.get(nProducers)));
        producerThreads.get(nProducers).start(); // Start

        panel.repaint();

        // Increment index value
        nProducers++;
    }

    public void remProducer()
    {
        // Stop thread, this also frees the buffer
        producerThreads.get(nProducers-1).interrupt();

        // Stop animation, if running
        producerTimers.get(nProducers-1).stop();

        // Removes from queue if is there
        for (int _id : producersQueue.getQueueIds())
            if (producers.get(nProducers-1).getId() == _id)
            {
                producersQueue.getQueueIds().remove((Integer) producers.get(nProducers-1).getId());
                updateQueueLabels();
                break;
            }

        producers.remove(nProducers-1);
        panel.remove(producerLabels.get(nProducers-1));
        producerLabels.remove(nProducers-1);
        producerThreads.remove(nProducers-1);
        producerTimers.remove(nProducers-1);

        panel.repaint();

        // Decrement value
        nProducers--;
    }

    public void addConsumer()
    {
        // Create required items
        consumers.add(new Consumer(2*(nConsumers)+1, buffer, consumersQueue, this));
        consumerLabels.add(new JLabel());
        consumerLabels.get(nConsumers).setText("C" + nConsumers);
        consumerLabels.get(nConsumers).setHorizontalTextPosition(JLabel.CENTER);
        consumerLabels.get(nConsumers).setVerticalTextPosition(JLabel.NORTH);
        consumerLabels.get(nConsumers).setSize(CONSUMER_LABEL_WIDTH, CONSUMER_LABEL_HEIGHT);
        consumerLabels.get(nConsumers).setIcon(new ImageIcon(CONSUMER_LABEL_ICONS[1])); // Default consumer state
        consumerLabels.get(nConsumers).setLocation(CONSUMERS_QUEUE_X, CONSUMERS_QUEUE_Y + nConsumers*(CONSUMER_LABEL_HEIGHT + 10));
        panel.add(consumerLabels.get(consumerLabels.size()-1));
        consumerTimers.add(new Timer(1, actionEvent -> {})); // Default timer (replaced later)

        consumerThreads.add(new Thread(consumers.get(nConsumers)));
        consumerThreads.get(nConsumers).start(); // Start

        panel.repaint();

        // Increment index value
        nConsumers++;
    }

    public void remConsumer()
    {
        // Stop thread, this also frees the buffer
        consumerThreads.get(nConsumers-1).interrupt();

        // Stop animation, if running
        consumerTimers.get(nConsumers-1).stop();

        // Removes from queue if is there
        for (int _id : consumersQueue.getQueueIds())
            if (consumers.get(nConsumers-1).getId() == _id)
            {
                consumersQueue.getQueueIds().remove((Integer) consumers.get(nConsumers-1).getId());
                updateQueueLabels();
                break;
            }

        consumers.remove(nConsumers-1);
        panel.remove(consumerLabels.get(nConsumers-1));
        consumerLabels.remove(nConsumers-1);
        consumerThreads.remove(nConsumers-1);
        consumerTimers.remove(nConsumers-1);

        panel.repaint();

        // Decrement value
        nConsumers--;
    }

    public synchronized void addBufferSlot()
    {
        maxBuffer++;
        buffer.setMax(maxBuffer);

        int y0 = bufferEnd.getY();

        // Add some margin between items in buffer.
        double dy = (double) (ITEM_LABEL_HEIGHT+25) / MOV_ANIMATION_STEPS;

        if (bufferEndTimer != null) bufferEndTimer.stop();
        bufferEndTimer = new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int yn = (int) (y0 + currentStep * dy);
                    bufferEnd.setLocation(bufferEnd.getX(), yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    panel.repaint();
                    ((Timer) actionEvent.getSource()).stop();
                }
            }
        });

        bufferEndTimer.start();
    }

    public synchronized void remBufferSlot()
    {
        maxBuffer--;
        buffer.setMax(maxBuffer);

        if (itemLabels.size() == maxBuffer+1)
        {
            buffer.remFirstItem();
            panel.remove(itemLabels.get(0));
            if (itemTimers.get(0) != null)
                itemTimers.get(0).stop();
            itemTimers.remove(0);
            itemLabels.remove(0);
        }

        int y0 = bufferEnd.getY();

        // Add some margin between items in buffer.
        double dy = (double) -(ITEM_LABEL_HEIGHT+25) / MOV_ANIMATION_STEPS;

        if (bufferEndTimer != null) bufferEndTimer.stop();
        bufferEndTimer = new Timer(MOV_ANIMATION_DELAY, new ActionListener()
        {
            int currentStep = 0;
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if (currentStep <= MOV_ANIMATION_STEPS)
                {
                    int yn = (int) (y0 + currentStep * dy);
                    bufferEnd.setLocation(bufferEnd.getX(), yn);
                    currentStep++;
                    panel.repaint();
                }
                else
                {
                    panel.repaint();
                    ((Timer) actionEvent.getSource()).stop();
                }
            }
        });

        bufferEndTimer.start();
    }
}
