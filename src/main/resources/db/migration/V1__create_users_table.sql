-- =====================================================
-- MST_USERS
-- =====================================================

CREATE TABLE MST_USERS (
                           USER_ID UUID PRIMARY KEY,

                           USERNAME VARCHAR(100) NOT NULL UNIQUE,
                           PASSWORD VARCHAR(255) NOT NULL,
                           USER_ROLE VARCHAR(30) NOT NULL,

                           FIRST_NAME VARCHAR(100) NOT NULL,
                           LAST_NAME VARCHAR(100),
                           EMAIL VARCHAR(150),

                           CREATED_DT TIMESTAMP,
                           CREATED_BY VARCHAR(100),
                           UPDATED_DT TIMESTAMP,
                           UPDATED_BY VARCHAR(100)
);

-- =====================================================
-- MST_INVOICES
-- =====================================================

CREATE TABLE MST_INVOICES (
                              INVOICE_ID UUID PRIMARY KEY,

                              USER_ID UUID NOT NULL,

                              ISSUED_DT DATE NOT NULL,
                              INVOICE_NUMBER VARCHAR(100) NOT NULL UNIQUE,
                              INVOICE_STATUS VARCHAR(50),
                              DUE_DT DATE NOT NULL,

                              TOTAL_AMOUNT NUMERIC(18,2) NOT NULL,

                              CREATED_DT TIMESTAMP,
                              CREATED_BY VARCHAR(100),
                              UPDATED_DT TIMESTAMP,
                              UPDATED_BY VARCHAR(100),

                              CONSTRAINT FK_INVOICE_USER
                                  FOREIGN KEY (USER_ID)
                                      REFERENCES MST_USERS(USER_ID)
);

-- =====================================================
-- INVOICE_ITEMS
-- =====================================================

CREATE TABLE INVOICE_ITEMS (
                                   INVOICE_ITEMS_ID UUID PRIMARY KEY,

                                   INVOICE_ID UUID NOT NULL,

                                   ITEM_NAME VARCHAR(255),
                                   UNIT_PRICE NUMERIC(18,2) NOT NULL,
                                   TOTAL_PRICE NUMERIC(18,2) NOT NULL,
                                   QUANTITY INTEGER NOT NULL,

                                   REMARKS TEXT,

                                   CREATED_DT TIMESTAMP,
                                   CREATED_BY VARCHAR(100),
                                   UPDATED_DT TIMESTAMP,
                                   UPDATED_BY VARCHAR(100),

                                   CONSTRAINT FK_INVOICE_ITEMS
                                       FOREIGN KEY (INVOICE_ID)
                                           REFERENCES MST_INVOICES(INVOICE_ID)
                                           ON DELETE CASCADE
);

-- =====================================================
-- APPROVAL_WORKFLOWS
-- =====================================================

CREATE TABLE APPROVAL_WORKFLOWS (
                                        APPROVAL_WORKFLOW_ID UUID PRIMARY KEY,

                                        INVOICE_ID UUID NOT NULL,
                                        USER_ID UUID NOT NULL,

                                        LEVEL INTEGER NOT NULL,

                                        REMARKS TEXT,

                                        APPROVAL_STATUS VARCHAR(30) NOT NULL,

                                        CREATED_DT TIMESTAMP,
                                        CREATED_BY VARCHAR(100),
                                        UPDATED_DT TIMESTAMP,
                                        UPDATED_BY VARCHAR(100),

                                        CONSTRAINT FK_APPROVAL_INVOICE
                                            FOREIGN KEY (INVOICE_ID)
                                                REFERENCES MST_INVOICES(INVOICE_ID)
                                                ON DELETE CASCADE,

                                        CONSTRAINT FK_APPROVAL_USER
                                            FOREIGN KEY (USER_ID)
                                                REFERENCES MST_USERS(USER_ID)
);

-- =====================================================
-- AUDIT_TRAILS
-- =====================================================

CREATE TABLE AUDIT_TRAILS (
                                  AUDIT_TRAILS_ID UUID PRIMARY KEY,

                                  INVOICE_ID UUID NOT NULL,
                                  USER_ID UUID NOT NULL,

                                  ACTION VARCHAR(50) NOT NULL,

                                  AUDIT_DETAILS JSONB,

                                  LOG_DT TIMESTAMP NOT NULL,

                                  CONSTRAINT FK_AUDIT_INVOICE
                                      FOREIGN KEY (INVOICE_ID)
                                          REFERENCES MST_INVOICES(INVOICE_ID)
                                          ON DELETE CASCADE,

                                  CONSTRAINT FK_AUDIT_USER
                                      FOREIGN KEY (USER_ID)
                                          REFERENCES MST_USERS(USER_ID)
);