package com.otr.plugins.qualityGate.model.mail;

import com.google.gson.annotations.SerializedName;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MailSettings implements Serializable {

    /**
     * Отправлять отчет на электронную почту
     */
    @SerializedName(value = "useNotification")
    boolean useNotification;

    /**
     * Подпись
     */
    @SerializedName(value = "signature", alternate = "Development team")
    String signature;

    /**
     * Тема письма:
     * По умолчанию: Announcement Notification
     */
    @SerializedName(value = "emailSubject", alternate = "version quality control")
    String subject;


    /**
     * Пользователь эл. почты
     */
    @SerializedName(value = "username")
    String username;

    /**
     * Пароль пользователя эл. почты
     */
    @SerializedName(value = "password")
    String password;

    /**
     * Адрес отправителя письма
     * Указывается валидный адрес эл. почты, например: user@user_mail.com
     */
    @SerializedName(value = "sender")
    String sender;

    /**
     * Адреса получателей письма
     * Адреса эл. почты через запятую, например: user@user_mail.com
     */
    @SerializedName(value = "recipients")
    List<String> recipients;


    /**
     * Хост SMTP
     * Пример: smtp.user_mail.com
     */
    @SerializedName(value = "smtpHost")
    String smtpHost;

    /**
     * Порт SMTP (*)
     * Пример: 25
     */
    @SerializedName(value = "smtpPort")
    String smtpPort;

    /**
     * Аутентификация SMTP
     * Указывается значение true или false
     */
    @SerializedName(value = "smtpAuth")
    boolean smtpAuth;

    /**
     * Использовать расширение STARTTLS
     * Указывается значение true или false
     */
    @SerializedName(value = "smtpStartTls")
    boolean smtpStartTls;

    public MailSettings(boolean enable) {
        this.useNotification = enable;
    }
}
