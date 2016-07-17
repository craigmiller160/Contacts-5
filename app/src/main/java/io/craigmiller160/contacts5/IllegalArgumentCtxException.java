/*
 * Copyright 2016 ShadowAngler <craigmiller160@gmail.com> - All Rights Reserved
 * Proprietary / Confidential
 * Unauthorized copying, use, or redistribution of this file is prohibited
 */

package io.craigmiller160.contacts5;

import org.apache.commons.lang3.exception.ContextedRuntimeException;
import org.apache.commons.lang3.exception.ExceptionContext;

/**
 * Created by craig on 6/18/16.
 */
public class IllegalArgumentCtxException extends ContextedRuntimeException {

    public IllegalArgumentCtxException() {
    }

    public IllegalArgumentCtxException(Throwable cause) {
        super(cause);
    }

    public IllegalArgumentCtxException(String message) {
        super(message);
    }

    public IllegalArgumentCtxException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalArgumentCtxException(String message, Throwable cause, ExceptionContext context) {
        super(message, cause, context);
    }
}
