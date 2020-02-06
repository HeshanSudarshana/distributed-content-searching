package utils;

import java.util.StringTokenizer;

/**
 * Created by Oshada on 2020-02-06.
 */
public class DFile {
    String fileName;

    public DFile(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }

    //This will check whether the file matches the given query
    public boolean isMatch(String query) {
        StringTokenizer tokens = new StringTokenizer(this.fileName, " ");
        StringTokenizer queryTokens = new StringTokenizer(query, " ");
        while (queryTokens.hasMoreTokens()) {
            String nextTerm = queryTokens.nextToken();
            while (tokens.hasMoreTokens()) {
                if (tokens.nextToken().equalsIgnoreCase(nextTerm)) {
                    return true;
                }
            }
        }
        return false;
    }
}
