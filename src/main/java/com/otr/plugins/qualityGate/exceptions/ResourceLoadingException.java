package com.otr.plugins.qualityGate.exceptions;

public class ResourceLoadingException extends Exception {
    public ResourceLoadingException(String message) {
        super(message);
    }


    public ResourceLoadingException(Throwable throwable) {
        super(throwable);
    }

    public ResourceLoadingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
