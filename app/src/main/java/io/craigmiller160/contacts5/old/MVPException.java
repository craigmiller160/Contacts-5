package io.craigmiller160.contacts5.old;

/**
 * Created by Craig on 2/14/2016.
 */
public class MVPException extends Exception {

    public MVPException() {
    }

    public MVPException(String detailMessage) {
        super(detailMessage);
    }

    public MVPException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }

    public MVPException(Throwable throwable) {
        super(throwable);
    }
}
