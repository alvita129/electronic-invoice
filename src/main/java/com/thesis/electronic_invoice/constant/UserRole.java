package com.thesis.electronic_invoice.constant;

public enum UserRole {
    ADMIN(0),
    APPROVER_LV1(1),
    APPROVER_LV2(2);

    private final Integer approvalLevel;

    UserRole(Integer approvalLevel) {
        this.approvalLevel = approvalLevel;
    }

    public Integer getApprovalLevel() {
        return approvalLevel;
    }
}
