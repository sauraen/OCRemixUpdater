package ocremixupdater;

/**
 * Stores two Strings: a file name and a "reduced" file name, with special
 * characters removed and all run together.
 *
 * @author Copyright (C) 2012 Sauraen, sauraen@gmail.com; GPL licensed
 */
public class DoubleFileName implements Comparable<DoubleFileName>{
    public String fullname;
    public String reducedname;

    /**
     * Create a new DoubleFileName with the given full name, and automatically
     * set the reduced name to <code>OCReMix.reduceName(fullname)</code>.
     * @param fullname The file name.
     */
    public DoubleFileName(String fullname){
        this.fullname = fullname;
        this.reducedname = OCReMix.reduceName(fullname);
    }

    /**
     * Compare on the basis of the reduced name, for sorting.
     * @param o Another DoubleFileName to compare this to.
     * @return The value of <code>this.reducedname.compareTo(o.reducedname)</code>
     * @see #String.compareTo()
     */
    public int compareTo(DoubleFileName o) {
        return this.reducedname.compareTo(o.reducedname);
    }

    /**
     * NetBeans made this for me...?
     * @return A hash of this DoubleFileName based on its reducedname only.
     */
    @Override public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (this.reducedname != null ? this.reducedname.hashCode() : 0);
        return hash;
    }

    /**
     * Compare on the basis of the reduced name.
     * @param o An object.
     * @return False if o is not a DoubleFileName; otherwise, the value of the
     * String.equals() method applied to this reduced name and o's reduced name.
     */
    @Override public boolean equals(Object o){
        if(o == null) return false;
        if(!(o instanceof DoubleFileName)) return false;
        return this.reducedname.equals(((DoubleFileName)o).reducedname);
    }
}
