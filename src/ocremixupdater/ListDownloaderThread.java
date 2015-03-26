package ocremixupdater;

import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.SwingWorker;

/**
 * A SwingWorker thread that downloads ReMix list pages.
 * <p>
 * These are the HTML files displayed to a visitor of ocremix.org looking at
 * the "View All ReMixes in descending chronological order" page.
 *
 * @author Copyright (C) 2015 Sauraen, sauraen@gmail.com; GPL licensed
 */
public class ListDownloaderThread extends SwingWorker<Object, String> {

    public static final String LIST_PREFIX = "http://ocremix.org/remixes/?&offset=";
    public static final String LIST_SUFFIX = "&sort=datedesc";
    public static final int INC_PAGE = 30;


    private ReMixDatabase data;
    private int listDLToNumber, listDLFromNumber;
    private Downloader dialog;
    private int curNumber;
    private boolean waitNow;
    private int minTime, maxTime;
    
    private String outputText;
    
    public ListDownloaderThread(ReMixDatabase data, int minIndex, int startFrom, Downloader dialog, boolean waitFirst, int minTime, int maxTime){
        this.data = data;
        this.listDLToNumber = minIndex;
        this.listDLFromNumber = startFrom;
        this.dialog = dialog;
        this.waitNow = waitFirst;
        this.minTime = minTime;
        this.maxTime = maxTime;
        if(this.maxTime - this.minTime < 1){
            this.maxTime = this.minTime + 10;
        }
    }

    @Override protected Object doInBackground() throws Exception {
        outputText = dialog.getOutputText();
        curNumber = Integer.MAX_VALUE;
        int pageNum = listDLFromNumber;
        boolean succeeded;
        long msecToWait;
        String pageName;
        try{
            while(curNumber > listDLToNumber){
                if(waitNow){
                    msecToWait = (long)(((Math.random() * (maxTime - minTime)) + minTime) * 1000);
                    waitWithDisplay(msecToWait);
                }
                pageName = LIST_PREFIX + Integer.toString(pageNum) + LIST_SUFFIX;
                writeOutput("Starting page: " + pageName);
                succeeded = readListPage(pageName);
                if(!succeeded){
                    writeOutput("Failed to read page " + pageName + ", stopping");
                    break;
                }
                pageNum += INC_PAGE;
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
     * Read an HTML page listing some ReMixes, and guess the file names based
     * on the game name and ReMix title.
     * @param strAddress A String holding the URL to the page to download.
     * @return Whether everything was successful or not.
     * @throws InterruptedException If the user cancelled the download.
     */
    private boolean readListPage(String strAddress) throws InterruptedException {
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
        String line = null, fullName = null, gameName = null, remixTitle = null, temp;
        int lineNum = 1;
        int state = 0, startChar, endChar, mixNumber;
        boolean done = false, foundAny = false;
        try{
            line = page.readLine().trim();
            while(line != null && !done){
                switch(state){
                    case 0:
                        if(line.startsWith("<table")){
                            state = 1;
                        }
                        break;
                    case 1:
                        if(line.equals("<tbody>")){
                            state = 2;
                        }
                        break;
                    case 2:
                        if(line.equals("<tr>")){
                            state = 3; //Within one ReMix record
                        }
                        break;
                    case 3:
                        if(line.startsWith("<td valign=\"top\" class=\"no-highlight\" colspan=\"5\"><a href=\"/game/")){
                            startChar = line.indexOf(">", 60);
                            endChar = line.indexOf("<", startChar);
                            if(startChar < 0 || endChar < 0){
                                throw new Exception();
                            }
                            gameName = line.substring(startChar + 1, endChar).trim();
                        }else if(line.startsWith("<td style=\"padding-left:15px;\"><a href=\"/remix/OCR")){
                            temp = line.substring(50, 55);
                            try{
                                mixNumber = Integer.parseInt(temp);
                            }catch (NumberFormatException e){
                                throw new Exception();
                            }
                            curNumber = mixNumber;
                            startChar = line.indexOf(" '", 70) + 2;
                            endChar = line.length() - 11;
                            remixTitle = line.substring(startChar, endChar);
                            //Parse name
                            fullName = OCReMix.createFullName(gameName, remixTitle);
                            System.out.println("OCR " + mixNumber + ": " + gameName + ": " + remixTitle);
                            writeOutput("Found OCR" + mixNumber + ": " + fullName);
                            foundAny = true;
                            //Add to database
                            data.addRecord(mixNumber, fullName);
                        }else if(line.equals("</tbody>")){
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
        if(!foundAny){
            writeOutput("No ReMixes found on this page! Possibly the format changed? Check source code.");
            return false;
        }
        try{
            page.close();
        }catch(Exception e){
            writeOutput("Unable to close stream...?");
            return false;
        }
        writeOutput("Done!");
        return true;
        
        
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
