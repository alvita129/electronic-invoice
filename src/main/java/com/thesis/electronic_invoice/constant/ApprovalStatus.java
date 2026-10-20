package com.thesis.electronic_invoice.constant;

public enum ApprovalStatus {
    PENDING ("Pending Invoice"),
    APPROVAL_LV1("Approved Level 1"),
    APPROVAL_LV2 ("Approved Level 2"),
    REJECTED ("Invoice Rejected"),
    RETURN_TO_ADMIN ("Invoice Return to Admin");

    private final String dscp;

    ApprovalStatus(String dscp) {
        this.dscp = dscp;
    }
}
