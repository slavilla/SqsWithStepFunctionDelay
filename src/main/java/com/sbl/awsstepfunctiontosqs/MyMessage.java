package com.sbl.awsstepfunctiontosqs;

import java.time.LocalDateTime;

public class MyMessage {
    private String receiptHandle;
    private String body;
    private LocalDateTime sent;
    private LocalDateTime processOn;

    public String getReceiptHandle() {
        return receiptHandle;
    }

    public void setReceiptHandle(String receiptHandle) {
        this.receiptHandle = receiptHandle;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public LocalDateTime getSent() {
        return sent;
    }

    public void setSent(LocalDateTime sent) {
        this.sent = sent;
    }

    public LocalDateTime getProcessOn() {
        return processOn;
    }

    public void setProcessOn(LocalDateTime processOn) {
        this.processOn = processOn;
    }

    @Override
    public String toString() {
        return "MyMessage{" +
                "receiptHandle='" + receiptHandle + '\'' +
                ", body='" + body + '\'' +
                ", sent=" + sent +
                ", processOn=" + processOn +
                '}';
    }
}
