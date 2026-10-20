# Electronic Invoice

A Spring Boot and PostgreSQL invoice workspace with JWT login, role-based access, two-level approval, and a database-enforced immutable audit trail.

The backend uses interface-driven layers: controllers depend on service interfaces, service implementations depend on Spring Data repository interfaces, and implementations are kept in `service/impl`.

## Run locally

1. Start PostgreSQL with `docker compose up -d db` from this directory. The default local database port is `5433`.
2. Set `DB_PASSWORD` and a strong, Base64-encoded `JWT_SECRET` in your environment (or in the ignored local `.env`). The JWT secret must decode to at least 32 bytes.
3. Set `BOOTSTRAP_ADMIN_PASSWORD` to a unique password of at least eight characters. A fresh database creates `BOOTSTRAP_ADMIN_USERNAME` (default `admin`) as its first administrator. The application creates this account only while the users table is empty.
4. Run `mvn spring-boot:run` here and `npm run dev` from `../electronic-invoice-fe`.
5. Sign in at `http://localhost:5173`. Use **Team & access** to create an `APPROVER_LV1` and `APPROVER_LV2` account before submitting invoices.

For a full container setup, configure the same required secrets in `.env` and run `docker compose up --build`. The UI is available on port `5173`; PostgreSQL remains on `5433`.

## Roles and workflow

- `ADMIN` manages users, creates and edits drafts or returned invoices, submits invoices, archives drafts, and views invoice audit histories.
- `APPROVER_LV1` sees invoices assigned to the first approval step.
- `APPROVER_LV2` sees invoices after level 1 has approved them.
- Approvers can approve, reject, or return an invoice for revision. Reject and return require remarks. Returned invoices can be edited and resubmitted.
- The invoice total is calculated by the backend from each item's unit price and quantity. The client cannot set an independent total.
- Invoice deletion is a soft archive, so the record and its audit history remain available. Only drafts can be archived.
- Audit records include actor, role, timestamp, action, before/after state, and remarks. A PostgreSQL trigger rejects audit row updates or deletes.

## API overview

All routes use `/api`. Send `Authorization: Bearer <token>` except for login.

## Database migrations

Flyway applies `V1` for the original schema and `V2` for archive support, unassigned approval compatibility, and the audit immutability trigger. JPA validates the migrated schema at startup.
