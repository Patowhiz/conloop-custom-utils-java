package conloop;

/**
 *
 * @author Patowhiz on 30/12/2020 04:12 PM in Machakos Office
 * @param <TResult>
 */
public class CustomTaskResult<TResult> extends CustomTask<TResult> {

    private TResult res;
    private boolean complete = false;
    private boolean cancelled = false;
    private CustomTaskException mEntityException = null;
    private OnCompleteListener<TResult> mOnCompleteListener;
    //private OnSuccessListener<? super TResult> mOnSuccessListener;
    private OnFailureListener mOnFailureListener;

    public void setResult(TResult res) {
        this.res = res;
        this.complete = true;
        if (this.mOnCompleteListener != null) {
            this.mOnCompleteListener.onComplete(this);
        }

        //this.mOnSuccessListener.onSuccess(this.res);
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setException(CustomTaskException exception) {
        this.mEntityException = exception;
        this.complete = true;
        if (this.mOnCompleteListener != null) {
            this.mOnCompleteListener.onComplete(this);
        }
        if (this.mOnFailureListener != null) {
            this.mOnFailureListener.onFailure(this.mEntityException);
        }
    }

    public void setExceptionError(Exception exception) {
        setException(new CustomTaskException(CustomTaskException.EXCEPTION_ERROR, exception.getMessage()));
    }

    public void setNetworkError(Exception exception) {
        setException(new CustomTaskException(CustomTaskException.NETWORK_ERROR, exception.getMessage()));
    }

    public void setNetworkResponseError(Exception exception) {
        setException(new CustomTaskException(CustomTaskException.RESPONSE_ERROR, exception.getMessage()));
    }

    @Override
    public final boolean isComplete() {
        return complete;
    }

    @Override
    public final boolean isSuccessful() {
        //successful when no exception, not cancelled and result is not null
        return mEntityException == null && !cancelled && res != null;
    }

    @Override
    public final boolean isCanceled() {
        return cancelled;
    }

    @Override
    public final TResult getResult() {
        return this.res;
    }

    @Override
    public final <X extends Throwable> TResult getResult(Class<X> var1) throws X {
        throw new UnsupportedOperationException("this is not implemented");
    }

    @Override
    public final CustomTaskException getException() {
        return this.mEntityException;
    }

    @Override
    public final CustomTaskResult<TResult> addOnCompleteListener(OnCompleteListener<TResult> var1) {
        this.mOnCompleteListener = var1;
        return this;
    }

    @Override
    public final CustomTaskResult<TResult> addOnSuccessListener(OnSuccessListener<? super TResult> var1) {
        //this.  mOnSuccessListener =  var1;
        return this;
    }

    @Override
    public final CustomTaskResult<TResult> addOnFailureListener(OnFailureListener var1) {
        this.mOnFailureListener = var1;
        return this;
    }
}
