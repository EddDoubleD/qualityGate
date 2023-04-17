package com.otr.plugins.qualityGate.controller;

/**
 * Исключение перехватчик
 */
public class MailException extends Exception {


    public MailException(String message) {

    }

    public MailException(String message, Throwable e) {
        super(message, e);
    }
}
