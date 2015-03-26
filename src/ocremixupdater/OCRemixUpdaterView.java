package ocremixupdater;

import javax.swing.event.TableModelEvent;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import javax.swing.filechooser.*;

/**
 * Main window of OCRemixUpdater.
 * 
 * @author Copyright (C) 2015 Sauraen, sauraen@gmail.com; GPL licensed
 */
public class OCRemixUpdaterView extends FrameView {

    public static final int NUM_AVAILABLE_BY_TORRENT = 3000;
    
    public ReMixDatabase data;
    public DefaultTableModel table;

    public Downloader frmDL;
    public MirrorWindow frmMirrors;

    public OCRemixUpdaterView(SingleFrameApplication app) {
        super(app);

        initComponents();

        getFrame().setTitle("OCReMixUpdater");

        //License agreement
        try{
            String licenseAgreed = (String)getContext().getLocalStorage().load("licenseagreed");
            if(licenseAgreed == null) throw new Exception();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,
                    "OCRemixUpdater: Update your OverClocked ReMix collection from ocremix.org and its mirrors\n" +
                    "Copyright (C) 2012 Sauraen, sauraen@gmail.com\n\n" +

                    "This program is free software: you can redistribute it and/or modify\n" +
                    "it under the terms of the GNU General Public License as published by\n" +
                    "the Free Software Foundation, either version 3 of the License, or\n" +
                    "(at your option) any later version.\n\n" +

                    "This program is distributed in the hope that it will be useful,\n" +
                    "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" +
                    "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n" +
                    "GNU General Public License for more details.\n\n" +

                    "You should have received a copy of the GNU General Public License\n" + 
                    "along with this program.  If not, see <http://www.gnu.org/licenses/>.\n\n" +

                    "You are solely responsible for your use of this program. It is designed to\n" +
                    "generate less web traffic than you could (if you never got bored), but\n" +
                    "you are still responsible for the content and volume of downloads."
                    , "OCRemixUpdater", JOptionPane.PLAIN_MESSAGE);
            try{
                getContext().getLocalStorage().save("yes", "licenseagreed");
            }catch (Exception e2){
                System.out.println("Could not save that you agreed to the license," +
                        " you will have to agree next time.");
            }
        }

        //Set up database

        data = new ReMixDatabase(this);
        try{
            String remixpath = (String)getContext().getLocalStorage().load("remixpath");
            if(remixpath == null){
                JOptionPane.showMessageDialog(null, "The first thing you probably want to do is go " + 
                        "to File > Folder\nand select the folder where all your numbered OC ReMixes are.\n" +
                        "Also put the file remixes.lst that came with this program into that folder,\n" +
                        "if you haven't done so already.");
            }
            File pathfile = new File(remixpath);
            if(!pathfile.exists()){
                JOptionPane.showMessageDialog(getComponent(), "ReMix path " +
                        pathfile.getPath() + " does not exist.\n" +
                        "Maybe you have your ReMixes on an external drive that\'s not connected?\n" +
                        "In any case, select a ReMix folder with File > Folder.",
                        "OCRemixUpdater", JOptionPane.WARNING_MESSAGE);
            }
            data.setFolder(pathfile);
        }catch (Exception e){
            System.out.println("Could not find saved remix path");
        }

        data.mirrors = loadMirrorsList();
        if(data.mirrors == null){
            data.mirrors = new Vector<Mirror>();
            data.mirrors.add(new Mirror("http://ocr.blueblue.fr/files/music/remixes/", true));
            data.mirrors.add(new Mirror("http://iterations.org/files/music/remixes/", true));
            data.mirrors.add(new Mirror("http://ocremix.dreamhosters.com/files/music/remixes/", true));
            data.mirrors.add(new Mirror("http://ocrmirror.org/files/music/remixes/", true));
            saveMirrorsList();
        }

        //Set up table data

        table = (DefaultTableModel)tblReMixes.getModel();

        table.addTableModelListener(new TableModelListener() {

            public void tableChanged(TableModelEvent e) {
                if(e.getType() != TableModelEvent.UPDATE) return;
                if(e.getColumn() != 2) return;
                int r = e.getFirstRow();
                Object val = table.getValueAt(r, 2);
                if(val == null) return;
                if(!(val instanceof Boolean)) return;
                boolean b = (Boolean)val;
                OCReMix mix = data.get(r);
                if(mix == null) return;
                if(mix.get != b){
                    mix.get = b;
                }
            }
        });
        
        //Set up table looks

        TableColumnModel cols = tblReMixes.getColumnModel();
        cols.getColumn(0).setMinWidth(60);
        cols.getColumn(0).setMaxWidth(60);
        cols.getColumn(1).setMinWidth(45);
        cols.getColumn(1).setMaxWidth(45);
        cols.getColumn(2).setMinWidth(45);
        cols.getColumn(2).setMaxWidth(45);

        //Set up forms

        frmDL = new Downloader(getFrame(), this, false);
        frmMirrors = new MirrorWindow(getFrame(), true, data);

        //Read list

        if(data.isFolderSet()){
            DBAccessThread.accessDatabase(data, DBAccessThread.DBMode.READLIST, this);
            DBAccessThread.accessDatabase(data, DBAccessThread.DBMode.CHECKMP3S, this);
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblReMixes = new javax.swing.JTable();
        barStatus = new javax.swing.JToolBar();
        comSelect = new javax.swing.JButton();
        comDeselect = new javax.swing.JButton();
        barProgress = new javax.swing.JProgressBar();
        lblStatus = new javax.swing.JLabel();
        menuBar = new javax.swing.JMenuBar();
        mnuFile = new javax.swing.JMenu();
        mnuFolder = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        mnuExit = new javax.swing.JMenuItem();
        mnuList = new javax.swing.JMenu();
        mnuRefreshCurrent = new javax.swing.JMenuItem();
        mnuRefreshAll = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        mnuRereadList = new javax.swing.JMenuItem();
        mnuRescanFS = new javax.swing.JMenuItem();
        mnuReMix = new javax.swing.JMenu();
        mnuDownload = new javax.swing.JMenuItem();
        mnuMirrors = new javax.swing.JMenuItem();

        mainPanel.setName("mainPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        tblReMixes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "OCR", "Have", "Get", "ReMix Title"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblReMixes.setName("tblReMixes"); // NOI18N
        tblReMixes.setSelectionMode(javax.swing.ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(tblReMixes);

        barStatus.setFloatable(false);
        barStatus.setRollover(true);
        barStatus.setName("barStatus"); // NOI18N
        barStatus.setPreferredSize(new java.awt.Dimension(500, 31));

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(ocremixupdater.OCRemixUpdaterApp.class).getContext().getResourceMap(OCRemixUpdaterView.class);
        comSelect.setText(resourceMap.getString("comSelect.text")); // NOI18N
        comSelect.setName("comSelect"); // NOI18N
        comSelect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comSelectActionPerformed(evt);
            }
        });
        barStatus.add(comSelect);

        comDeselect.setText(resourceMap.getString("comDeselect.text")); // NOI18N
        comDeselect.setName("comDeselect"); // NOI18N
        comDeselect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comDeselectActionPerformed(evt);
            }
        });
        barStatus.add(comDeselect);

        barProgress.setName("barProgress"); // NOI18N
        barStatus.add(barProgress);

        lblStatus.setText(resourceMap.getString("lblStatus.text")); // NOI18N
        lblStatus.setName("lblStatus"); // NOI18N
        barStatus.add(lblStatus);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(barStatus, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 769, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 572, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(barStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        menuBar.setName("menuBar"); // NOI18N

        mnuFile.setText(resourceMap.getString("mnuFile.text")); // NOI18N
        mnuFile.setName("mnuFile"); // NOI18N

        mnuFolder.setText(resourceMap.getString("mnuFolder.text")); // NOI18N
        mnuFolder.setName("mnuFolder"); // NOI18N
        mnuFolder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuFolderActionPerformed(evt);
            }
        });
        mnuFile.add(mnuFolder);

        jSeparator1.setName("jSeparator1"); // NOI18N
        mnuFile.add(jSeparator1);

        mnuExit.setText(resourceMap.getString("mnuExit.text")); // NOI18N
        mnuExit.setName("mnuExit"); // NOI18N
        mnuExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuExitActionPerformed(evt);
            }
        });
        mnuFile.add(mnuExit);

        menuBar.add(mnuFile);

        mnuList.setText(resourceMap.getString("mnuList.text")); // NOI18N
        mnuList.setName("mnuList"); // NOI18N

        mnuRefreshCurrent.setText(resourceMap.getString("mnuRefreshCurrent.text")); // NOI18N
        mnuRefreshCurrent.setName("mnuRefreshCurrent"); // NOI18N
        mnuRefreshCurrent.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRefreshCurrentActionPerformed(evt);
            }
        });
        mnuList.add(mnuRefreshCurrent);

        mnuRefreshAll.setText(resourceMap.getString("mnuRefreshAll.text")); // NOI18N
        mnuRefreshAll.setName("mnuRefreshAll"); // NOI18N
        mnuRefreshAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRefreshAllActionPerformed(evt);
            }
        });
        mnuList.add(mnuRefreshAll);

        jSeparator2.setName("jSeparator2"); // NOI18N
        mnuList.add(jSeparator2);

        mnuRereadList.setText(resourceMap.getString("mnuRereadList.text")); // NOI18N
        mnuRereadList.setName("mnuRereadList"); // NOI18N
        mnuRereadList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRereadListActionPerformed(evt);
            }
        });
        mnuList.add(mnuRereadList);

        mnuRescanFS.setText(resourceMap.getString("mnuRescanFS.text")); // NOI18N
        mnuRescanFS.setName("mnuRescanFS"); // NOI18N
        mnuRescanFS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuRescanFSActionPerformed(evt);
            }
        });
        mnuList.add(mnuRescanFS);

        menuBar.add(mnuList);

        mnuReMix.setText(resourceMap.getString("mnuReMix.text")); // NOI18N
        mnuReMix.setName("mnuReMix"); // NOI18N

        mnuDownload.setText(resourceMap.getString("mnuDownload.text")); // NOI18N
        mnuDownload.setName("mnuDownload"); // NOI18N
        mnuDownload.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuDownloadActionPerformed(evt);
            }
        });
        mnuReMix.add(mnuDownload);

        mnuMirrors.setText(resourceMap.getString("mnuMirrors.text")); // NOI18N
        mnuMirrors.setName("mnuMirrors"); // NOI18N
        mnuMirrors.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mnuMirrorsActionPerformed(evt);
            }
        });
        mnuReMix.add(mnuMirrors);

        menuBar.add(mnuReMix);

        setComponent(mainPanel);
        setMenuBar(menuBar);
    }// </editor-fold>//GEN-END:initComponents

    private void mnuExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuExitActionPerformed
        OCRemixUpdaterApp.getApplication().exit();
    }//GEN-LAST:event_mnuExitActionPerformed

    private void mnuFolderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuFolderActionPerformed
        JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setFileFilter(new FileNameExtensionFilter("All Folders", "*"));
        int result = fc.showOpenDialog(getComponent());
        if(result == JFileChooser.APPROVE_OPTION){
            File sel = fc.getSelectedFile();
            if(sel == null){
                sel = fc.getCurrentDirectory();
            }
            if(!sel.canRead() || !sel.isDirectory()){
                JOptionPane.showMessageDialog(getComponent(), "Folder cannot be read!",
                        "Ut-oh!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if(!sel.isDirectory()){
                JOptionPane.showMessageDialog(getComponent(), "That is not a folder!",
                        "You thought that by selecting \"All Files\" you would break it, right?",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try{
                data.setFolder(sel);
            }catch (IOException e){
                JOptionPane.showMessageDialog(getComponent(), "IOException!", "Ut-oh!",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            try{
                getContext().getLocalStorage().save(sel.getPath(), "remixpath");
            }catch (IOException e){
                JOptionPane.showMessageDialog(getComponent(), "Unable to save application "
                        + "setting that this is the path to your ReMixes.\n" 
                        + "The program will work fine now, but you will have to "
                        + "reselect the path next time.", "OCRemixUpdater",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            data.readListFile();
            data.checkMP3sAgainstList(false);
            refreshTable();
        }
    }//GEN-LAST:event_mnuFolderActionPerformed

    private void mnuRescanFSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRescanFSActionPerformed
        if(!data.isFolderSet()){
            JOptionPane.showMessageDialog(getComponent(), "ReMix folder not set up!\n" +
                    "Use File > Folder to select the folder where the ReMixes are.",
                    "OCReMixUpdater", JOptionPane.ERROR_MESSAGE);
        }
        DBAccessThread.accessDatabase(data, DBAccessThread.DBMode.CHECKMP3S, this);
    }//GEN-LAST:event_mnuRescanFSActionPerformed

    private void mnuRereadListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRereadListActionPerformed
        if(!data.isFolderSet()){
            JOptionPane.showMessageDialog(getComponent(), "ReMix folder not set up!\n" +
                    "Use File > Folder to select the folder where the ReMixes are.",
                    "OCReMixUpdater", JOptionPane.ERROR_MESSAGE);
        }
        DBAccessThread.accessDatabase(data, DBAccessThread.DBMode.READLIST, this);
        DBAccessThread.accessDatabase(data, DBAccessThread.DBMode.CHECKMP3S, this);
    }//GEN-LAST:event_mnuRereadListActionPerformed

    private void mnuRefreshCurrentActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRefreshCurrentActionPerformed
        frmDL.setUpToDownloadList(data, data.getMaxMixNumber());
        frmDL.setVisible(true);
    }//GEN-LAST:event_mnuRefreshCurrentActionPerformed

    private void mnuRefreshAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuRefreshAllActionPerformed
        frmDL.setUpToDownloadList(data, 1);
        frmDL.setVisible(true);
    }//GEN-LAST:event_mnuRefreshAllActionPerformed

    private void comSelectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comSelectActionPerformed
        int[] selRows = tblReMixes.getSelectedRows();
        for(int i=0; i<selRows.length; i++){
            if(!data.get(selRows[i]).have){
                table.setValueAt(Boolean.TRUE, selRows[i], 2);
            }
        }
    }//GEN-LAST:event_comSelectActionPerformed

    private void comDeselectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comDeselectActionPerformed
        int[] selRows = tblReMixes.getSelectedRows();
        for(int i=0; i<selRows.length; i++){
            table.setValueAt(Boolean.FALSE, selRows[i], 2);
        }
    }//GEN-LAST:event_comDeselectActionPerformed

    private void mnuMirrorsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuMirrorsActionPerformed
        frmMirrors.setVisible(true);
    }//GEN-LAST:event_mnuMirrorsActionPerformed

    private void mnuDownloadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mnuDownloadActionPerformed
        if(data.countGet() == 0){
            JOptionPane.showMessageDialog(getComponent(), "You haven't selected any ReMixes to download!\n" + 
                    "To select ReMixes to download, either check the \"Get\" box next to their names,\n" + 
                    "or highlight a group of them and click the Select button.\n" +
                    "By default, this will not select ReMixes you already have.",
                    "OCRemixUpdater", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        //See if you're trying to download one below 1700
        if(downloadingAnyTorrentable()){
            //Yell at user
            String shutUp; int result;
            try{
                shutUp = (String)getContext().getLocalStorage().load("shutup");
                if(shutUp == null) throw new Exception();
                if(shutUp.equals("yes")){
                    result = JOptionPane.showOptionDialog(getComponent(),
                        "Please use the torrents (available at http://ocremix.org/torrents/)\n" +
                        "to download ReMixes " + NUM_AVAILABLE_BY_TORRENT + " and below!",
                        "OCRemixUpdater", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null,
                        new String[] {"Okay, fine...", "SHUT UP"}, "Okay, fine...");
                    if(result == 1){ //The second one
                        try{
                            getContext().getLocalStorage().save("YES REALLY SHUT UP", "shutup");
                        }catch (Exception e2){
                            System.out.println("Could not save that you said to shut up! " +
                                    "Oh, what a pity!");
                        }
                    }else{
                        uncheckAllTorrentable();
                        refreshTable();
                        if(data.countGet() == 0){
                            return; //Don't download
                        }
                    }
                }
            }catch (Exception e){
                result = JOptionPane.showOptionDialog(getComponent(),
                        "Please use the torrents (available at http://ocremix.org/torrents/)\n" +
                        "to download ReMixes " + NUM_AVAILABLE_BY_TORRENT + " and below!",
                        "OCRemixUpdater", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, null,
                        new String[] {"Okay", "No, I know what I'm doing"}, "Okay");
                System.out.println(result);
                if(result == 1){ //The second one
                    try{
                        getContext().getLocalStorage().save("yes", "shutup");
                    }catch (Exception e2){
                        System.out.println("Could not save that you said to shut up! " +
                                "Oh, what a pity!");
                    }
                }else{
                    uncheckAllTorrentable();
                    refreshTable();
                    if(data.countGet() == 0){
                        return; //Don't download
                    }
                }
            }
        }
        frmDL.setUpToDownloadMP3s(data);
        frmDL.setVisible(true);
    }//GEN-LAST:event_mnuDownloadActionPerformed

    public boolean downloadingAnyTorrentable(){
        for(int i=0; i<data.size(); i++){
            if(data.get(i).get && data.get(i).number <= NUM_AVAILABLE_BY_TORRENT){
                return true;
            }
        }
        return false;
    }

    public void uncheckAllTorrentable(){
        for(int i=0; i<data.size(); i++){
            if(data.get(i).number <= NUM_AVAILABLE_BY_TORRENT){
                data.get(i).get = false;
            }
        }
    }

    /**
     * Refresh the contents of the table, reading from the database.
     * <p>
     * Note: This method should only be called from the Event Dispatch Thread.
     */
    public void refreshTable(){
        int i;
        for(i=table.getRowCount()-1; i>=0; i--){
            table.removeRow(i);
        }
        OCReMix mix;
        for(i=0; i<data.size(); i++){
            mix = data.get(i);
            table.addRow(new Object[4]);
            table.setValueAt(mix.number, i, 0);
            table.setValueAt(mix.have, i, 1);
            table.setValueAt(mix.get, i, 2);
            table.setValueAt(mix.name, i, 3);
        }
    }

    /**
     * Show text in the status bar.
     * @param status The text to display.
     */
    public void setStatus(String status){
        lblStatus.setText(status);
        System.out.println(status);
    }

    /**
     * Set the range of the progres bar in the status bar.
     * @param low The smallest value to display.
     * @param high The largest value to display.
     */
    public void setBarRange(int low, int high){
        barProgress.setMinimum(low);
        barProgress.setMaximum(Math.max(low + 1, high));
        barProgress.setValue(low);
    }

    /**
     * Set the value to display in the progress bar.
     * @param value Should be between the low and high bounds of the progress
     * bar as set by setBarRange(). If this value is greater than the maximum,
     * this method sets the value to the maximum; if this value is less than
     * the minimum, this method sets the value to the minimum.
     */
    public void setBarValue(int value){
        barProgress.setValue(Math.min(Math.max(value, barProgress.getMinimum()), barProgress.getMaximum()));
    }

    /**
     * Clear the progress bar, setting its range 0-100 and value to 0.
     */
    public void clearBar(){
        barProgress.setMinimum(0);
        barProgress.setMaximum(100);
        barProgress.setValue(0);
    }

    /**
     * Load the list of mirrors from a serialized file in the local storage
     * named "mirrors".
     * @return A Vector of Mirrors if the operation was successful; else null.
     */
    public Vector<Mirror> loadMirrorsList(){
        try{
            System.out.println("Loading mirrors list...");
            File loadFrom = new File(getContext().getLocalStorage().getDirectory(), "mirrors");
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(loadFrom));
            Vector<Mirror> ret = (Vector<Mirror>)ois.readObject();
            System.out.println("Loaded mirrors list with " + ret.size() + " mirrors");
            ois.close();
            return ret;
        }catch (Exception e){
            System.out.println("Could not load mirrors list: " + e.getMessage());
            return null;
        }
    }

    /**
     * Save the list of mirrors in data to a serialized file in local storage
     * called "mirrors".
     */
    public void saveMirrorsList(){
        try{
            System.out.println("Saving mirrors list...");
            File saveto = new File(getContext().getLocalStorage().getDirectory(), "mirrors");
            if(!saveto.exists()){
                saveto.createNewFile();
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(saveto));
            oos.writeObject(data.mirrors);
            oos.close();
            System.out.println("Saved mirrors list");
        }catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(getComponent(),
                      "Unable to save list of mirrors.\n"
                    + "The program will work fine now, but you will have to "
                    + "reselect the mirrors next time.\n"
                    + "(Error: " + e.getMessage() + ")", "OCRemixUpdater",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar barProgress;
    private javax.swing.JToolBar barStatus;
    private javax.swing.JButton comDeselect;
    private javax.swing.JButton comSelect;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JLabel lblStatus;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mnuDownload;
    private javax.swing.JMenuItem mnuExit;
    private javax.swing.JMenu mnuFile;
    private javax.swing.JMenuItem mnuFolder;
    private javax.swing.JMenu mnuList;
    private javax.swing.JMenuItem mnuMirrors;
    private javax.swing.JMenu mnuReMix;
    private javax.swing.JMenuItem mnuRefreshAll;
    private javax.swing.JMenuItem mnuRefreshCurrent;
    private javax.swing.JMenuItem mnuRereadList;
    private javax.swing.JMenuItem mnuRescanFS;
    private javax.swing.JTable tblReMixes;
    // End of variables declaration//GEN-END:variables

}
