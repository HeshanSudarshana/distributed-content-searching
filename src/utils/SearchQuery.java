package utils;

/**
 * Created by Oshada on 2020-02-06.
 */
//This class is used to store the data of a searchQuery
public class SearchQuery {
    private String searchQuery;
    private String searchersIp;
    private String searchersPort;
    private Long parsedTime;

    public SearchQuery(String searchQuery, String searchersIp, String searchersPort) {
        this.searchQuery = searchQuery;
        this.searchersIp = searchersIp;
        this.searchersPort = searchersPort;
        this.parsedTime = System.currentTimeMillis();
    }

    public boolean isEqual(SearchQuery query) {
        if (this.searchQuery.equals(query.searchQuery) && this.searchersIp.equals(query.searchersIp) &&
                this.searchersPort.equals(query.searchersPort)) {
            return true;
        } else {
            return false;
        }
    }

    public Long getParsedTime() {
        return parsedTime;
    }
}
