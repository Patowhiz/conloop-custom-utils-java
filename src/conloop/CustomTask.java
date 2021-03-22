package conloop;



/**
 *
 * @author Patowhiz on 30/12/2020 04:04 PM in Machakos Office
 * @param <TResult>
 */
public abstract class CustomTask<TResult> {
     public CustomTask() {
    }

    public abstract boolean isComplete();

    public abstract boolean isSuccessful();

    public abstract boolean isCanceled();

   
    public abstract TResult getResult();

  
    public abstract <X extends Throwable> TResult getResult( Class<X> var1) throws X;

 
    public abstract CustomTaskException getException();

 
    public abstract CustomTask<TResult> addOnSuccessListener( OnSuccessListener<? super TResult> var1);

    
    public abstract CustomTask<TResult> addOnFailureListener( OnFailureListener var1);

    
    public CustomTask<TResult> addOnCompleteListener( OnCompleteListener<TResult> var1) {
        throw new UnsupportedOperationException("addOnCompleteListener is not implemented");
    }

    
    public CustomTask<TResult> addOnCanceledListener( OnCanceledListener var1) {
        throw new UnsupportedOperationException("addOnCanceledListener is not implemented.");
    }
}
