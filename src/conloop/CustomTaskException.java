package conloop;




/**
 *
 * @author Patowhiz on 30/12/2020 04:10 PM in Machakos Office
 */
public class CustomTaskException  extends Exception {
    public static final String NETWORK_ERROR = "network_error";//error due to lack of internet
    public static final String EXCEPTION_ERROR = "exception_error"; //error cause when parsing data
    public static final String RESPONSE_ERROR = "response_error"; //errors sent by server
    private final String errorMessage;
    private final String errorType;

    public CustomTaskException(String errorMessage) {
        this.errorType = RESPONSE_ERROR;//by default it's a response error
        this.errorMessage = errorMessage;
    }

    public CustomTaskException(String errorType, String errorMessage) {
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
    public String getErrorType() {
        return errorType;
    }
}
