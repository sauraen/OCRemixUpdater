package ocremixupdater;

/**
 * Represents a single OC ReMix, including its number, file name, whether
 * the user has it on disk or not, and whether the user wants to download it
 * or not.
 * <p>
 * Also contains all the static methods for converting ReMix names.
 *
 * @author Copyright (C) 2015 Sauraen, sauraen@gmail.com; GPL licensed
 */
public class OCReMix implements Comparable {
    public String name;
    public int number;
    public boolean have;
    public boolean get;

    /**
     * Compare this ReMix to another on the basis of its number.
     * @param other Another object, which we hope is an OCReMix.
     * @return <code>this.number - other.number</code>
     */
    public int compareTo(Object other){
        OCReMix m = null;
        try{
            m = (OCReMix)other;
        }catch (ClassCastException e){
            throw new IllegalArgumentException("Wrong class!");
        }
        return number - m.number;
    }

    /**
     * Make an array of four objects that represents this ReMix.
     * @return The elements are: (Integer)number, (Boolean)have, (Boolean)get,
     * and name.
     */
    public Object[] toRow(){
        Object[] stuff = new Object[4];
        stuff[0] = (Integer)number;
        stuff[1] = (Boolean)have;
        stuff[2] = (Boolean)get;
        stuff[3] = name;
        return stuff;
    }

    /**
     * The table for converting game and mix names into file names.
     * <p>
     * Note: The rules have changed since OCR01700! Hence the commented-out entries.
     */
    public static final String[][] REPLACEMENTS = {
        //HTML
        {"&amp;",     "&"},
        {"&quot;",    "\""},
        {"&lt;",      "<"},
        {"&gt;",      ">"},
        {"&tilde;",   "~"},
        {"&ndash;",   "–"},
        {"&mdash;",   "—"},
        {"&lsquo;",   "‘"},
        {"&rsquo;",   "’"},
        {"&lsquo;",   "“"},
        {"&rsquo;",   "”"},
        //Final Fantasy
        {" XVIII",    " 18"}, //I'm being a little optimistic :)
        {" XVII",     " 17"},
        {" XVI",      " 16"},
        {" XV",       " 15"},
        {" XIV",      " 14"},
        {" XIII",     " 13"},
        {" XII",      " 12"},
        {" XI",       " 11"},
        {" Fantasy X"," Fantasy 10"},
        {" IX",       " 9"},
        {" VIII",     " 8"},
        {" VII",      " 7"},
        {" VI",       " 6"},
        {" Fantasy V"," Fantasy 5"},
        {" IV",       " 4"},
        {" III",      " 3"},
        {" II",       " 2"},
        //Pokémon
        {"é",         "e"},
        //Mario
        {": Legend of the Seven Stars", ""},
        //{"Super Mario World 2: ",  ""},
        //Zelda: The rules seem to have changed!
        {" The Adventure of Link", ""},
        //{"A Link to the Past",     "3"},
        //{"Link's Awakening",       "LinksAwakening"},
        //{"Ocarina of Time",        "64"},
        //{"Legend of Zelda",        "Zelda"},
        //Sonic
        //{" the Hedgehog",          ""},
        //{" 3D Blast",              " 3D"},
        //Donkey Kong
        {": Diddy's Kong Quest",    ""},
        {": Dixie Kong's Double Trouble", ""},
        //Metroid
        {": Return of Samus",       ""},
        {": Echoes",                ""},
        //Radical Dreamers
        {": Nusumenai Houseki",     ""},
        //Castlevania
        {": Simon's Quest",         ""},
        //Other
        {": Rise of the Sinistrals",""},
        {": The World Warrior",     ""},
        {": Snake Eater",           ""},
        {": Turtles in Time",       ""},
        //Additional characters
        {"/",         ""},
        {"\\",        ""},
        {"*",         ""},
        {"~",         ""},
        {"!",         ""},
        {"?",         ""},
        {":",         ""},
        {";",         ""},
        {",",         ""},
        {".",         ""},
        {"  ",        "_"},
        {" ",         "_"}
    };

    /**
     * The table for checking guessed ReMix names against file names.
     */
    public static final String[][] REDUCTIONS = {
        {"_", ""},
        {"'", ""},
        {"-", ""},
        {"&", "and"}
    };

    /**
     * The table for converting URL syntax into ordinary syntax.
     */
    public static final String[][] URL_REPLACEMENTS = {
        {"%21", "!"},
        {"%22", "\""},
        {"%23", "#"},
        {"%24", "$"},
        {"%25", "%"},
        {"%26", "&"},
        {"%27", "'"},
        {"%28", "("},
        {"%29", ")"},
        {"%2A", "*"},
        {"%2B", "+"},
        {"%2C", ","},
        {"%2D", "-"},
        {"%2E", "."}
    };

    /**
     * Guess the ReMix filename based on the game name and the ReMix title.
     * @param game The game's name. E.g.: "Donkey Kong Country 2: Diddy's Kong Quest" [sic]
     * @param title The ReMix title in plain text. E.g.: "Intense Glory (victory mix)"
     * @return The ReMix filename. E.g.: "Portal_Not_Alive_Anymore_OC_ReMix.mp3"
     */
    public static String createFullName(String game, String title){
        //Zelda
        if(game.startsWith("The ")){
            game = game.substring(4);
        }
        for(int i=0; i<REPLACEMENTS.length; i++){
            game = replaceAllLiteral(game, REPLACEMENTS[i][0], REPLACEMENTS[i][1]);
            title = replaceAllLiteral(title, REPLACEMENTS[i][0], REPLACEMENTS[i][1]);
        }
        return game + "_" + title + "_OC_ReMix.mp3";
    }

    /**
     * Perform literal replacement of one String for another in a given String.
     * @param searchIn The String to replace things in.
     * @param searchFor The String to find.
     * @param replaceWith The String to replace searchFor with.
     * @return The resultant String.
     */
    public static String replaceAllLiteral(String searchIn, String searchFor, String replaceWith){
        for(int i=0; i<searchIn.length() - searchFor.length() + 1; i++){
            if(searchIn.substring(i, i + searchFor.length()).equals(searchFor)){
                searchIn = searchIn.substring(0, i) + replaceWith +
                        searchIn.substring(i+searchFor.length());
                i += replaceWith.length() - 1;
            }
        }
        return searchIn;
    }

    /**
     * Squash up the given filename as much as possible, to see if a file with a
     * name that also squashes to this already exists.
     * @param name The file name.
     * @return The "reduced" file name.
     */
    public static String reduceName(String name){
        name = name.toLowerCase();
        for(int i=0; i<REDUCTIONS.length; i++){
            name = replaceAllLiteral(name, REDUCTIONS[i][0], REDUCTIONS[i][1]);
        }
        return name;
    }

    /**
     * Convert a URL to a file name, taking out those annoying % codes.
     * @param name The URL
     * @return The filename
     */
    public static String convertURLNameToFSName(String name){
        for(int i=0; i<URL_REPLACEMENTS.length; i++){
            name = replaceAllLiteral(name, URL_REPLACEMENTS[i][0], URL_REPLACEMENTS[i][1]);
        }
        return name;
    }

    /**
     * Convert an OCR number to its five-digit-number String.
     * @param number The ReMix number.
     * @return A String padded on the left with zeros to five digits.
     */
    public static String fiveDigitNumber(int number){
        String ret = Integer.toString(number);
        while(ret.length() < 5){
            ret = "0" + ret;
        }
        return ret;
    }
}
