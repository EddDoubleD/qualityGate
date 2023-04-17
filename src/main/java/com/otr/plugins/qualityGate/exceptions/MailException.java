package com.otr.plugins.qualityGate.exceptions;

public class MailException extends Exception {


    public MailException(String message) {

    }

    public MailException(String message, Throwable e) {
        super(message, e);
    }
}