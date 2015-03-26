package ocremixupdater;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application. Generated by NetBeans. Basically just
 * shows a OCRemixUpdaterView form.
 *
 * @author Copyright (C) 2012 Sauraen, sauraen@gmail.com; GPL licensed
 */
public class OCRemixUpdaterApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new OCRemixUpdaterView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of OCRemixUpdaterApp
     */
    public static OCRemixUpdaterApp getApplication() {
        return Application.getInstance(OCRemixUpdaterApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(OCRemixUpdaterApp.class, args);
    }
}