package request;

/**
 * Created by Oshada on 2020-02-05.
 */
public abstract class Request {
    String type;

    public Request() {
    }

    public String getType() {
        return type;
    }

    public abstract String getRequest();
}
