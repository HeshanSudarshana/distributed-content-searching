package response;

/**
 * Created by Oshada on 2020-02-05.
 */
public class JoinOK extends Response {
    int result;

    public JoinOK(int result) {
        this.result = result;
        this.type = "JOINOK";
    }

    public String getResponse() {
        if (result == 0) {
            return "0014 JOINOK 0";
        } else {
            return "0017 JOINOK 9999";
        }

    }

}
