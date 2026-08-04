package com.thesis.electronic_invoice.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@MappedSuperclass
@Data
public class BaseModel implements Serializable {
    private static final long serialVersionUID = -1637867492272859249L;

    @Column(name = "CREATED_DT")
    protected LocalDateTime createdDt;

    @Column(name = "CREATED_BY")
    protected String createdBy;

    @Column(name = "UPDATED_DT")
    protected LocalDateTime updatedDt;

    @Column(name = "UPDATED_BY")
    protected String updatedBy;
}
