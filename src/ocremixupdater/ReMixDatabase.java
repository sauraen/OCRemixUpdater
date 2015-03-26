package ocremixupdater;

import java.util.*;
import java.io.*;

/**
 * A database holding records for OC ReMixes.
 * <p>
 * Also holds a list of mirrors.
 * <p>
 * Can read from and write to a list file, and check the list against the mp3s
 * in a folder. Maintains the ReMixes sorted in descending order.
 *
 * @author Copyright (C) 2012 Sauraen, sauraen@gmail.com; GPL licensed
 */
public class ReMixDatabase {
    private Vector<OCReMix> mixes;
    public Vector<Mirror> mirrors;
    private File folder, list;
    private int maxMixNum;

    private OCRemixUpdaterView view;
    public OCRemixUpdaterView getView(){return view;}

    /**
     * Create a new ReMixDatabase.
     * @param view The OCRemixUpdaterView to show updates on.
     */
    public ReMixDatabase(OCRemixUpdaterView view){
         mixes = new Vector<OCReMix>(3000, 101);
         mirrors = new Vector<Mirror>(5, 3);
         folder = null;
         list = null;
         maxMixNum = -1;
         this.view = view;
    }

    /**
     * Get the size of the ReMix list.
     * @return <code>mixes.size()</code>
     */
    public int size(){
        return mixes.size();
    }

    /**
     * @return The greatest ReMix number we have a record for.
     */
    public int getMaxMixNumber(){
        return maxMixNum;
    }

    /**
     * Get the ReMix at the given index.
     * @param i An index in the list, NOT the ReMix number.
     * @return The OCReMix at that index; if it's an invalid index, returns null.
     */
    public OCReMix get(int i){
        if(i >= 0 || i < mixes.size()){
            return mixes.get(i);
        }else{
            return null;
        }
    }

    /**
     * Binary search for a ReMix with the given number.
     * @param number The ReMix number to look for a ReMix with.
     * @return The OCReMix if it was found; else null.
     */
    public OCReMix getByNumber(int number){
        int bottom = 0, top = mixes.size() - 1, middle, num;
        while(top >= bottom){
            middle = (top + bottom) / 2;
            num = mixes.get(middle).number;
            if(num == number){
                return mixes.get(middle);
            }else if(num > number){
                bottom = middle + 1;
            }else{
                top = middle - 1;
            }
        }
        return null;
    }

    /**
     * Add a record to the database.
     * <p>
     * If a ReMix with the given number already exists:
     * <ul>
     * <li>If that ReMix is marked as "have", do nothing.</li>
     * <li>Otherwise, update that ReMix's name to the given name.</li>
     * </ul>
     * Otherwise, add a new OCReMix with the given name and number in the
     * proper place.
     * @param number The ReMix's OCR number.
     * @param name The ReMix's filename.
     */
    public void addRecord(int number, String name){
        OCReMix newMix = getByNumber(number);
        if(newMix != null){
            if(newMix.have){
                return; //Don't update the name of a mix you already have
            }
            newMix.name = name;
            newMix.have = false;
            newMix.get = false;
            return;
        }
        newMix = new OCReMix();
        newMix.number = number;
        newMix.name = name;
        newMix.get = false;
        newMix.have = false;
        for(int i=0; i<mixes.size(); i++){
            if(mixes.get(i).number < number){
                mixes.add(i, newMix);
                return;
            }
        }
        //If it's at the end
        mixes.add(newMix);
    }

    /**
     * Count the number of ReMixes marked to be downloaded.
     * @return The number of ReMixes to get.
     */
    public int countGet(){
        int count = 0;
        for(int i=0; i<mixes.size(); i++){
            if(mixes.get(i).get) count++;
        }
        return count;
    }


    /**
     * Set the folder to look for ReMixes and remixes.lst in.
     * @param folder A File representing the folder.
     * @return False if no existing database file was found. This is not a
     * problem, it makes one there.
     * @throws IOException If folder is invalid, etc.
     */
    public boolean setFolder(File folder) throws IOException{
        if(!(folder.canRead() && folder.isDirectory())){
            throw new IllegalArgumentException("Bad folder!");
        }
        this.folder = folder;
        list = new File(folder, "remixes.lst");
        if(!list.canRead()){
            System.out.println("Creating remixes.lst...");
            list.createNewFile();
        }
        return true;
    }

    /**
     * @return The ReMix folder.
     */
    public File getFolder(){ return folder; }

    /**
     * @return Whether the ReMix folder has been set yet.
     */
    public boolean isFolderSet(){
        return !(folder == null);
    }

    /**
     * Read ReMix records from remixes.lst in the folder that has been already
     * set. The old contents of this database are lost.
     * @return Whether this all succeeded.
     */
    public boolean readListFile(){
        try{
            BufferedReader in = new BufferedReader(new FileReader(list));
            view.setStatus("Reading list file...");
            String line = in.readLine(), name;
            StringTokenizer st;
            int number;
            Vector<OCReMix> mixes2 = new Vector<OCReMix>();
            OCReMix mix;
            while(line != null){
                if(!line.trim().isEmpty()){ //Skip empty lines
                    st = new StringTokenizer(line, " ");
                    if(st.countTokens() != 2){
                        throw new Exception("File badly formatted!");
                    }
                    number = Integer.parseInt(st.nextToken());
                    name = st.nextToken();
                    mix = new OCReMix();
                    mix.number = number;
                    mix.name = name;
                    if(number > maxMixNum){
                        maxMixNum = number;
                    }
                    mixes2.add(mix);
                }
                line = in.readLine();
            }
            in.close();
            //If all that worked, replace the current list
            mixes = mixes2;
            view.setStatus("Read " + mixes.size() + " records from remixes.lst");
            view.clearBar();
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Save the current database to remixes.lst in the selected folder.
     * @return Whether this succeeded.
     */
    public boolean saveListFile(){
        if(list == null) return false;
        if(!list.isFile()) return false;
        if(!list.canWrite()) return false;
        try{
            view.setStatus("Writing list file");
            list.delete();
            list.createNewFile();
            BufferedWriter out = new BufferedWriter(new FileWriter(list));
            OCReMix mix;
            view.setBarRange(1, mixes.size());
            for(int i=0; i<mixes.size(); i++){
                view.setBarValue(i+1);
                mix = mixes.get(i);
                out.write(mix.number + " " + mix.name);
                out.newLine();
            }
            out.close();
            view.setStatus("Wrote " + mixes.size() + " records to remixes.lst");
            view.clearBar();
            return true;
        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    /**
     * Check the database of records against the mp3 files in the folder, and
     * update the names in the database if mp3s are found with names that are
     * really similar to the known ones.
     * @param forceSaveAfter If true, saves the list file after doing all this.
     * This method always saves the list file if it has updated one of the ReMix
     * names, regardless of this parameter.
     */
    public void checkMP3sAgainstList(boolean forceSaveAfter){
        File tmp;
        OCReMix mix;
        int countHave = 0, searchResult;
        int numSaved = 0;
        view.setStatus("Reading files in " + folder.getPath() + " ...");
        DoubleFileName[] files = doubleUp(folder.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".mp3");
            }
        }));
        view.setStatus("Sorting " + files.length + " files...");
        Arrays.sort(files);
        view.setStatus("Comparing to list...");
        DoubleFileName tryrename;
        view.setBarRange(1, mixes.size());
        for(int i=0; i<mixes.size(); i++){
            view.setBarValue(i+1);
            mix = mixes.get(i);
            tmp = new File(folder, mix.name);
            if(tmp == null || !tmp.canRead() || !tmp.isFile()){
                tryrename = new DoubleFileName(mix.name);
                searchResult = Arrays.binarySearch(files, tryrename);
                if(searchResult >= 0){
                    System.out.println("Could not find " + mix.name);
                    System.out.println("but could find " + files[searchResult].fullname);
                    System.out.println("Replacing name in record");
                    mix.name = files[searchResult].fullname;
                    mix.have = true;
                    mix.get = false;
                    countHave++;
                    numSaved++;
                }
            }else{
                mix.have = true;
                mix.get = false;
                countHave++;
            }
        }
        view.setStatus("Found " + countHave + " ReMixes out of " + mixes.size() + " with " + numSaved + " updated");
        view.clearBar();
        if(numSaved > 0 || forceSaveAfter){
            saveListFile();
        }
    }

    /**
     * Convert a list of file names to a list of DoubleFileNames that are
     * reduced.
     * @param files A list of mp3 file names.
     * @return A list of DoubleFileNames, each of which corresponds to one of
     * the given file names.
     */
    public static DoubleFileName[] doubleUp(String[] files){
        DoubleFileName[] ret = new DoubleFileName[files.length];
        for(int i=0; i<files.length; i++){
            ret[i] = new DoubleFileName(files[i]);
        }
        return ret;
    }

    /**
     * Get a random mirror from the list that is marked as "use".
     * @return The mirror's URL; if there are none to choose from, null.
     */
    public String getRandomMirrorPath(){
        if(mirrors.size() < 1) return null;
        int mir, count = 0;
        while(true){
            count++;
            if(count > 1000){
                //You turned off ALL the mirrors...
                return null;
            }
            mir = (int)(Math.random() * mirrors.size());
            if(mirrors.get(mir).use){
                return mirrors.get(mir).path;
            }
        }
    }
}
