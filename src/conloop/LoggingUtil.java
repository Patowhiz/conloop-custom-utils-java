package conloop;

//import org.jfree.util.Log;

import com.sun.media.jfxmedia.logging.Logger;

//import java.util.logging.

/**
 *
 * @author Patowhiz on 30/12/2020 03:49 PM in Machakos Office
 */
public class LoggingUtil {

    //Functions here are meant for logging to help with debugging the code.
    // Their statements should eventually be commented before publishing or made to store the logs to a file
    private static final String TAG_PREFIX = "CONLOOP: "; //for debug logging

    public static void d(Class cls, String msg) {
        d(cls.getSimpleName(), msg);
    }

    public static void e(Class cls, String msg) {
        e(cls.getSimpleName(), msg);
    }

    public static void w(Class cls, String msg, Throwable tr) {
        w(cls.getSimpleName(), msg, tr);
    }

    public static void d(String tag, String msg) {
          Logger.logMsg(Logger.DEBUG, TAG_PREFIX + tag + " : " + msg);
       // Log.debug(TAG_PREFIX + tag + " : " + msg);
        //System.out.println(TAG_PREFIX + tag + " : " + msg);
        // Log.d(TAG_PREFIX + tag, msg);//todo. comment out before publishing
    }

    public static void e(String tag, String msg) {
        Logger.logMsg(Logger.ERROR, TAG_PREFIX + tag + " : " + msg);
       // Log.error(TAG_PREFIX + tag + " : " + msg);
       // System.err.println(TAG_PREFIX + tag + " : " + msg);
        //Log.e(TAG_PREFIX + tag, msg);//todo. comment out before publishing
    }

    public static void w(String tag, String msg, Throwable tr) {
        Logger.logMsg(Logger.WARNING, TAG_PREFIX + tag + " : " + msg);
        //Log.warn(TAG_PREFIX + tag + " : " + msg);
        //Log.w(TAG_PREFIX + tag, msg,tr);//todo. comment out before publishing
    }

}
