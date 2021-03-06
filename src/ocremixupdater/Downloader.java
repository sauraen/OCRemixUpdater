package ocremixupdater;

import java.util.*;

/**
 * The window that shows the download progress, either for list pages or mp3s.
 * 
 * @author Copyright (C) 2012 Sauraen, sauraen@gmail.com; GPL licensed
 */
public class Downloader extends javax.swing.JDialog {

    /**
     * 0 is download list, 1 is download files
     */
    private int task = -1;
    private int listDLToNumber;
    private int curNumber;
    private ReMixDatabase data;
    private ArrayList<String> mirrors;
    private boolean running;
    private boolean justStopped;

    private OCRemixUpdaterView view;
    
    private Thread dlThread = null;

    /** Creates new form Downloader */
    public Downloader(java.awt.Frame parent, OCRemixUpdaterView view, boolean modal) {
        super(parent, modal);
        initComponents();
        setTitle("Downloader");
        this.view = view;
        running = false;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        spnLow = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        spnHigh = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtOutput = new javax.swing.JTextArea();
        comStart = new javax.swing.JButton();
        comStop = new javax.swing.JButton();

        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ocremixupdater.OCRemixUpdaterApp.class).getContext().getResourceMap(Downloader.class);
        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        spnLow.setModel(new javax.swing.SpinnerNumberModel(5, 5, 1000, 1));
        spnLow.setName("spnLow"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        spnHigh.setModel(new javax.swing.SpinnerNumberModel(30, 6, 1000, 1));
        spnHigh.setName("spnHigh"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        txtOutput.setColumns(20);
        txtOutput.setEditable(false);
        txtOutput.setRows(5);
        txtOutput.setName("txtOutput"); // NOI18N
        jScrollPane1.setViewportView(txtOutput);

        comStart.setText(resourceMap.getString("comStart.text")); // NOI18N
        comStart.setName("comStart"); // NOI18N
        comStart.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comStartActionPerformed(evt);
            }
        });

        comStop.setText(resourceMap.getString("comStop.text")); // NOI18N
        comStop.setName("comStop"); // NOI18N
        comStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comStopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 546, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnLow, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spnHigh, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(comStart, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comStop, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(spnLow, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(spnHigh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(comStop)
                    .addComponent(comStart))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Set up the window to download the list of ReMixes.
     * @param data The database.
     * @param minIndex The lowest OCR number to download; that is, when the
     * program sees a ReMix with this number, it doesn't download any more
     * list files.
     */
    public void setUpToDownloadList(ReMixDatabase data, int minIndex){
        this.data = data;
        task = 0;
        listDLToNumber = minIndex;
        justStopped = false;
        clearOutput();
        writeOutput("Download list files for ReMixes down to OCR" + minIndex);
    }

    /**
     * Set up the window to download the mp3s in the database that are marked as
     * "get".
     * @param data The database.
     */
    public void setUpToDownloadMP3s(ReMixDatabase data){
        this.data = data;
        task = 1;
        justStopped = false;
        clearOutput();
        writeOutput("Download " + data.countGet() + " selected remixes from random mirrors:");
        for(int i=0; i<data.mirrors.size(); i++){
            if(data.mirrors.get(i).use){
                writeOutput(data.mirrors.get(i).path);
            }
        }
        writeOutput("");
    }

    private void comStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comStartActionPerformed
        if(task == 0){
            dlThread = new Thread(new ListDownloaderThread(data, listDLToNumber, 0,
                    this, justStopped, (Integer)spnLow.getValue(), (Integer)spnHigh.getValue()));
            running = true;
            justStopped = false;
            dlThread.start();
        }else if(task == 1){
            dlThread = new Thread(new MP3DownloaderThread(data,
                    this, justStopped, (Integer)spnLow.getValue(), (Integer)spnHigh.getValue()));
            running = true;
            justStopped = false;
            dlThread.start();
        }
    }//GEN-LAST:event_comStartActionPerformed

    private void comStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comStopActionPerformed
        stopThread();
    }//GEN-LAST:event_comStopActionPerformed

    /**
     * Stop the currently running thread, if there is one.
     */
    private void stopThread(){
        if(dlThread != null){
            if(dlThread.isAlive()){
                dlThread.interrupt();
                running = false;
                justStopped = true;
            }
        }
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        stopThread();
    }//GEN-LAST:event_formWindowClosing

    /**
     * Get the contents of the output text box.
     * @return <code>txtOutput.getText()</code>
     */
    public String getOutputText(){
        return txtOutput.getText();
    }

    /**
     * Set (not append) the text in the output text box to the given text, and
     * move the cursor to the end.
     * @param t The text to display.
     */
    public void setOutputText(String t){
        txtOutput.setText(t);
        txtOutput.setCaretPosition(t.length());
    }

    /**
     * Write the given text to the end of the output text box as a new line, and
     * set the cursor to the end.
     * @param opt The text to append. Newline automatically added.
     */
    public void writeOutput(String opt){
        if(opt == null){
            opt = "";
        }
        String t = txtOutput.getText();
        if(!t.equals("")){
            txtOutput.setText(t + "\n" + opt);
        }else{
            txtOutput.setText(opt);
        }
        txtOutput.setCaretPosition(txtOutput.getText().length());
    }

    /**
     * Sets the text in the output text box to "".
     */
    public void clearOutput(){
        txtOutput.setText("");
    }
    /**
     * Deletes the last line of output.
     */
    public void unOutput(){
        String t = txtOutput.getText();
        int endpos = t.lastIndexOf("\n");
        if(endpos > 0){
            t = t.substring(0, endpos);
            txtOutput.setText(t);
        }
    }

    /**
     * Call this function after the downloading thread has finished.
     */
    public void receiveStop(){
        running = false;
        data.checkMP3sAgainstList(true);
        view.refreshTable();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton comStart;
    private javax.swing.JButton comStop;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSpinner spnHigh;
    private javax.swing.JSpinner spnLow;
    private javax.swing.JTextArea txtOutput;
    // End of variables declaration//GEN-END:variables

}
