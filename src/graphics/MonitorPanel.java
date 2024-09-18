package graphics;

import app.ProducerConsumerApp;

import javax.swing.*;
import java.awt.*;

public class MonitorPanel
{
    private int nProducers;
    private int nConsumers;
    private int maxBuffer;
    private static final int FRAME_WIDTH = 1385;
    private static final int FRAME_HEIGHT = 970;
    private final ProducerConsumerApp context;

    private final JPanel controlPanel;
    private final JButton addProducerButton;
    private final JButton remProducerButton;
    private final JButton addConsumerButton;
    private final JButton remConsumerButton;
    private final JButton addBufferSlotButton;
    private final JButton remBufferSlotButton;
    private final JTextArea logArea;

    public MonitorPanel(int nProducers, int nConsumers, int maxBuffer, ProducerConsumerApp context)
    {
        this.context = context;
        this.nProducers = nProducers;
        this.nConsumers = nConsumers;
        this.maxBuffer = maxBuffer;

        // Control panel
        controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        controlPanel.setPreferredSize(new Dimension(FRAME_WIDTH-985, FRAME_HEIGHT));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Control buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(3, 2, 15, 15));

        // Producer Buttons
        addProducerButton = new JButton("Add Producer");
        if (nProducers == 5) addProducerButton.setEnabled(false);
        addProducerButton.addActionListener(actionEvent -> handleAddProducer());
        remProducerButton = new JButton("Remove Producer");
        if (nProducers == 1) remProducerButton.setEnabled(false);
        remProducerButton.addActionListener(actionEvent -> handleRemProducer());
        buttonsPanel.add(addProducerButton);
        buttonsPanel.add(remProducerButton);

        // Consumer Buttons
        addConsumerButton = new JButton("Add Consumer");
        if (nConsumers == 5) addConsumerButton.setEnabled(false);
        addConsumerButton.addActionListener(actionEvent -> handleAddConsumer());
        remConsumerButton = new JButton("Remove Consumer");
        if (nConsumers == 1) remConsumerButton.setEnabled(false);
        remConsumerButton.addActionListener(actionEvent -> handleRemConsumer());
        buttonsPanel.add(addConsumerButton);
        buttonsPanel.add(remConsumerButton);

        // Buffer Buttons
        addBufferSlotButton = new JButton("Add Buffer Slot");
        if (maxBuffer == 5) addBufferSlotButton.setEnabled(false);
        addBufferSlotButton.addActionListener(actionEvent -> handleAddBufferSlot());
        remBufferSlotButton = new JButton("Remove Buffer Slot");
        if (maxBuffer == 1) remBufferSlotButton.setEnabled(false);
        remBufferSlotButton.addActionListener(actionEvent -> handleRemBufferSlot());
        buttonsPanel.add(addBufferSlotButton);
        buttonsPanel.add(remBufferSlotButton);

        // Large Text Area for Logs
        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setFont(logArea.getFont().deriveFont(12f));
        JScrollPane scrollPane = new JScrollPane(logArea);
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BorderLayout());
        logPanel.add(scrollPane);

        controlPanel.add(buttonsPanel, BorderLayout.NORTH);
        controlPanel.add(logPanel, BorderLayout.CENTER);
    }

    public synchronized void logMonitor(String msg) { logArea.append(msg + "\n"); controlPanel.repaint(); }

    public JPanel getPanel() { return controlPanel; }

    private void handleAddProducer()
    {
        context.addProducer();
    }

    private void handleRemProducer()
    {
        context.remProducer();
    }

    private void handleAddConsumer()
    {
        context.addConsumer();
    }

    private void handleRemConsumer()
    {
        context.remConsumer();
    }

    private void handleAddBufferSlot()
    {
        context.addBufferSlot();
    }

    private void handleRemBufferSlot()
    {
        context.remBufferSlot();
    }

    public void setNProducers(int nProducers)
    {
        this.nProducers = nProducers;
        remProducerButton.setEnabled(nProducers != 1);
        addProducerButton.setEnabled(nProducers != 5);
    }

    public void setNConsumers(int nConsumers)
    {
        this.nConsumers = nConsumers;
        remConsumerButton.setEnabled(nConsumers != 1);
        addConsumerButton.setEnabled(nConsumers != 5);
    }

    public void setMaxBuffer(int maxBuffer)
    {
        this.maxBuffer = maxBuffer;
        remBufferSlotButton.setEnabled(maxBuffer != 1);
        addBufferSlotButton.setEnabled(maxBuffer != 5);
    }
}
