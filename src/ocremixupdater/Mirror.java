package ocremixupdater;

import java.io.Serializable;

/**
 * A mirror to download OC ReMixes from.
 *
 * @author Copyright (C) 2012 Sauraen, sauraen@gmail.com; GPL licensed
 */
public class Mirror implements Serializable {
    /**
     * A String representing the URL path to remixes at this mirror, minus
     * the actual mix file name.
     */
    public String path;
    /**
     * Whether to download from this mirror or not.
     */
    public boolean use;
    /**
     * Construct a new Mirror with the given path and use.
     * @param path The URL of the mirror.
     * @param use Whether to use this mirror.
     */
    public Mirror(String path, boolean use){
        this.path = path;
        this.use = use;
    }
}
