package ocremixupdater;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.SwingWorker;

/**
 * A SwingWorker thread that downloads ReMixes.
 * <p>
 * It attempts to download the remix with the name it already has, but if the
 * mirror returns an error page, it checks the ReMix's own page at ocremix.org
 * to find the correct file name, and then downloads that.
 * <p>
 * It repeats this process for each ReMix in the database marked as "get",
 * beginning with the lowest numbered one.
 *
 * @author Copyright (C) 2015 Sauraen, sauraen@gmail.com; GPL licensed
 */
public class MP3DownloaderThread extends SwingWorker<Object, String> {

    /**
     * If the mp3 download was successful.
     */
    public static final int MP3_RESULT_SUCCESS = 0;
    /**
     * If attempting to download the mp3 resulted in a 404 page (i.e. wrong name).
     */
    public static final int MP3_RESULT_404 = 1;
    /**
     * If the mp3 download failed but there's no reason the next one would.
     */
    public static final int MP3_RESULT_FAILED = 2;
    /**
     * If the mp3 download failed and every future one will too.
     */
    public static final int MP3_RESULT_FATAL = 3;

    public static final String MIXPAGE_PREFIX = "http://ocremix.org/remix/OCR";
    public static final String MIXPAGE_SUFFIX = "/";
    public static final String DL_LINK_PREFIX = "<li><a href=\"http://ocrmirror.org/files/music/remixes/";
    public static final String ERROR_404_HEADER = "<!DOCTYPE html";
    public static final int INC_PAGE = 30;
    /**
     * The number of bytes after which to update the amount downloaded.
     */
    private static final int DL_REFRESH_RATE = 100000;


    private ReMixDatabase data;
    private Downloader dialog;
    private boolean waitNow;
    private int minTime, maxTime;

    private String outputText;
    
    public MP3DownloaderThread(ReMixDatabase data, Downloader dialog, boolean waitFirst, int minTime, int maxTime){
        this.data = data;
        this.dialog = dialog;
        this.waitNow = waitFirst;
        this.minTime = minTime;
        this.maxTime = maxTime;
        if(this.maxTime - this.minTime < 1){
            this.maxTime = this.minTime + 10;
        }
    }

    @Override protected Object doInBackground() throws Exception {
        if(data.mirrors.size() < 1){
            writeOutput("No mirrors!");
            return null;
        }
        outputText = dialog.getOutputText();
        int i;
        OCReMix mix;
        long msecToWait;
        int succeeded;
        File testFile;
        try{
            for(i=data.size() - 1; i>=0; i--){ //Reverse order
                mix = data.get(i);
                if(!mix.get) continue;
                //Wait
                if(waitNow){
                    msecToWait = (long)(((Math.random() * (maxTime - minTime)) + minTime) * 1000);
                    waitWithDisplay(msecToWait);
                }
                //Attempt MP3 download
                succeeded = downloadMP3(mix);
                switch(succeeded){
                    case MP3_RESULT_SUCCESS:
                    case MP3_RESULT_FAILED:
                        break;
                    case MP3_RESULT_404:
                        waitWithDisplay(1000);
                        if(fixName(mix)){
                            //Check if you already have the file with the correct name
                            testFile = new File(data.getFolder(), mix.name);
                            if(testFile.exists()){
                                writeOutput("Already have " + mix.name);
                                mix.have = true;
                                mix.get = false;
                            }else{
                                //Attempt download again, ignore all results but fatal
                                waitWithDisplay(5000);
                                if(downloadMP3(mix) == MP3_RESULT_FATAL){
                                    throw new InterruptedException();
                                }
                            }
                        }
                        break;
                    case MP3_RESULT_FATAL:
                        throw new InterruptedException();
                }
                waitNow = true;
            }
            writeOutput("Downloader finished!");
        }catch (InterruptedException e){
            writeOutput("Stopping");
        }
        return null;
    }

    @Override protected void done(){
        dialog.receiveStop();
    }

    /**
     * To be called from this thread: set up output to be later written to the
     * text box. Works the same way as Downloader.writeOutput(), but is
     * asynchronous.
     * @param opt The text to append.
     */
    private void writeOutput(String opt){
        if(opt == null){
            opt = "";
        }
        if(!outputText.equals("")){
            outputText += "\n" + opt;
        }else{
            outputText = opt;
        }
        publish(outputText);
    }
    /**
     * To be called from this thread: remove the last line of text from the
     * output text box. Works the same way as Downloader.unOutput(), but is
     * asynchronous.
     */
    private void unOutput(){
        int endpos = outputText.lastIndexOf("\n");
        if(endpos > 0){
            outputText = outputText.substring(0, endpos);
            //DO NOT publish(outputText);
        }
    }

    @Override protected void process(List<String> chunks){
        dialog.setOutputText(chunks.get(chunks.size() - 1));
    }

    /**
     * Download an mp3 from a random mirror, saving it to file.
     * @param mix The file name of the ReMix to attempt download of.
     * @return One of the <code>MP3DownloaderThread.MP3_RESULT_</code>
     * values, showing whether the download succeeded or failed in various ways.
     * @throws InterruptedException If the user cancelled the download.
     */
    public int downloadMP3(OCReMix mix) throws InterruptedException {
        String remoteAddr = data.getRandomMirrorPath();
        if(remoteAddr == null){
            writeOutput("No mirrors!");
            return MP3_RESULT_FATAL;
        }
        remoteAddr += mix.name;
        URL address = null;
        try{
            address = new URL(remoteAddr);
        }catch (MalformedURLException e){
            writeOutput("Malformed address: " + remoteAddr + "!");
            return MP3_RESULT_FAILED;
        }
        InputStream remoteFile;
        try{
            HttpURLConnection remoteConn = (HttpURLConnection)address.openConnection();
            if(remoteConn.getResponseCode() / 100 != 2){
                writeOutput("HTTP " + remoteConn.getResponseCode() + " from " + remoteAddr);
                return MP3_RESULT_FAILED;
            }
            remoteFile = remoteConn.getInputStream();
        }catch (Exception e){
            writeOutput("Could not open connection to: " + remoteAddr + "!");
            return MP3_RESULT_FATAL;
        }
        writeOutput("Opened connection to " + remoteAddr);
        //
        File saveAs = new File(data.getFolder(), mix.name);
        boolean fileAlreadyExists = saveAs.exists();
        if(fileAlreadyExists){
            saveAs.renameTo(new File(saveAs.getPath() + ".old"));
        }
        FileOutputStream localFile;
        try{
            localFile = new FileOutputStream(saveAs);
        }catch (Exception e){
            writeOutput("Could not write to file " + saveAs.getPath() + "!");
            return MP3_RESULT_FATAL;
        }
        //Actually download
        try{
            byte[] bytes = new byte[4096];
            int numBytes;
            int totalBytes = 0;
            int bytesCtr = 0;
            boolean isErrorPage;
            writeOutput("0K downloaded");
            while((numBytes = remoteFile.read(bytes)) >= 0){
                if(totalBytes == 0){
                    isErrorPage = true;
                    for(int i=0; i<ERROR_404_HEADER.length(); i++){
                        if(bytes[i] != ERROR_404_HEADER.charAt(i)){
                            isErrorPage = false;
                            break;
                        }
                    }
                    if(isErrorPage){
                        unOutput(); //Remove the "0K downloaded"
                        writeOutput("Error page returned!");
                        if(!fileAlreadyExists){
                            saveAs.delete();
                        }
                        return MP3_RESULT_404;
                    }
                }
                localFile.write(bytes, 0, numBytes);
                totalBytes += numBytes;
                bytesCtr += numBytes;
                if(bytesCtr >= DL_REFRESH_RATE){
                    bytesCtr %= DL_REFRESH_RATE;
                    unOutput();
                    writeOutput((totalBytes / 1024) + "K downloaded");
                }
            }
            localFile.close();
            remoteFile.close();
        }catch (Exception e){
            writeOutput("Error downloading remix " + remoteAddr + " !");
            return MP3_RESULT_FAILED;
        }
        //Done
        writeOutput("Downloaded " + mix.name);
        return MP3_RESULT_SUCCESS;
    }

    /**
     * If the mp3 download failed in that the mirror returned an error page,
     * check ocremix.org for the correct ReMix name.
     * @param mix The ReMix to update the name of.
     * @return Whether this succeeded.
     */
    public boolean fixName(OCReMix mix){
        String fiveDigit = OCReMix.fiveDigitNumber(mix.number);
        writeOutput("Checking for correct name of OCR" + fiveDigit + ": " + mix.name);
        String strAddress = MIXPAGE_PREFIX + fiveDigit + MIXPAGE_SUFFIX;
        URL address = null;
        try{
            address = new URL(strAddress);
        }catch (MalformedURLException e){
            writeOutput("Malformed address: " + strAddress + "!");
            return false;
        }
        BufferedReader page = null;
        try{
            page = new BufferedReader(new InputStreamReader(address.openStream()));
        }catch (Exception e){
            writeOutput("Could not open connection to: " + strAddress + "!");
            return false;
        }
        writeOutput("Opened connection to " + strAddress);
        String line = null, newTitle = null;
        int lineNum = 1;
        int state = 0, startChar, endChar;
        boolean done = false;
        try{
            line = page.readLine().trim();
            while(line != null && !done){
                switch(state){
                    case 0:
                        if(line.startsWith("<div id=\"panel-download\"")){
                            state = 1;
                        }
                        break;
                    case 1:
                        if(line.startsWith(DL_LINK_PREFIX)){
                            startChar = DL_LINK_PREFIX.length();
                            endChar = line.indexOf("\"", startChar);
                            if(startChar < 0 || endChar < 0){
                                throw new Exception();
                            }
                            newTitle = line.substring(startChar, endChar).trim();
                            newTitle = OCReMix.convertURLNameToFSName(newTitle);
                            mix.name = newTitle;
                            done = true;
                        }
                        break;
                }
                line = page.readLine().trim();
                lineNum++;
            }
        }catch(Exception e){
            writeOutput("Reading from HTML failed on line " + lineNum + ":");
            writeOutput(line);
            return false;
        }
        try{
            page.close();
        }catch(Exception e){
            writeOutput("Unable to close stream...?");
            return false;
        }
        if(!done){
            writeOutput("Could not find mp3 link in page!");
            return false;
        }else{
            writeOutput("Fixed name: " + mix.name + "!");
            return true;
        }
    }

    /**
     * Make this thread wait a number of milliseconds, showing the remaining
     * time in the output text box.
     * @param msecToWait The number of milliseconds to wait.
     * @throws InterruptedException If the user cancelled the thread.
     */
    public void waitWithDisplay(long msecToWait) throws InterruptedException{
        writeOutput("Waiting " + (msecToWait / 1000.0) + " seconds...");
        Thread.sleep(msecToWait % 1000);
        msecToWait -= msecToWait % 1000;
        writeOutput("--");
        while(msecToWait > 0){
            unOutput();
            writeOutput(Long.toString(msecToWait / 1000));
            Thread.sleep(1000);
            msecToWait -= 1000;
        }
        unOutput();
    }


    
}
