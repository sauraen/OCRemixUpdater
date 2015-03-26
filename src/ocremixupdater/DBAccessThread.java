package ocremixupdater;

import javax.swing.SwingWorker;

/**
 * A SwingWorker thread for performing various database operations in the
 * background and displaying the results asynchronously on the main window.
 * <p>
 * Use accessDatabase() to interact with this class, rather than creating
 * DBAccessThreads manually. I mean, it's not that hard, but why not use the
 * convenience function.
 *
 * @author Copyright (C) 2012 Sauraen, sauraen@gmail.com; GPL licensed
 */
public class DBAccessThread extends SwingWorker<Object, Integer> {

    public enum DBMode {
        READLIST,
        WRITELIST,
        CHECKMP3S
    }

    private ReMixDatabase data;
    private DBMode mode;
    private OCRemixUpdaterView view;
    
    public DBAccessThread(ReMixDatabase data, DBMode mode, OCRemixUpdaterView view){
        this.data = data;
        this.mode = mode;
        this.view = view;
    }

    @Override public Object doInBackground() {
        if(mode == DBMode.READLIST){
            data.readListFile();
        }else if(mode == DBMode.WRITELIST){
            data.saveListFile();
        }else if(mode == DBMode.CHECKMP3S){
            data.checkMP3sAgainstList(false);
        }
        return null;
    }
    
    @Override public void done(){
        view.refreshTable();
    }

    /**
     * Create and run a DBAccessThread to do the selected operation.
     * @param data The database.
     * @param mode Whether to read the list file, write the list file, or scan
     * the filesystem for the mp3s.
     * @param view The OCRemixUpdaterView to display the results on.
     */
    public static void accessDatabase(ReMixDatabase data, DBMode mode, OCRemixUpdaterView view){
        DBAccessThread worker = new DBAccessThread(data, mode, view);
        worker.execute();
    }
}
