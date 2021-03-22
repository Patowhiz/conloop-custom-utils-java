package conloop;



import javafx.application.Platform;

/**
 * used to return results on the JavaFX main application UI thread
 * @author PatoWhiz 21/01/2021 09:35 AM at machakos house
 * @param <TResult>
 */
public class CustomJavaFXTaskResult<TResult> extends CustomTaskResult<TResult> {

    @Override
    public void setResult(TResult res) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //UI related things can go ahead and be done
                CustomJavaFXTaskResult.super.setResult(res);
            }
        });

    }

    @Override
    public void setException(CustomTaskException exception) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //UI related things can go ahead and be done
                CustomJavaFXTaskResult.super.setException(exception);
            }
        });
    }

}
