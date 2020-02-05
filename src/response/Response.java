package response;

/**
 * Created by Oshada on 2020-02-05.
 */
public abstract class Response {
    String type;

    public String getType() {
        return type;
    }

    public abstract String getResponse();
}
