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

## Public user testing deployment (Render)

The frontend and backend are separate repositories, so create the services separately in Render. Put the PostgreSQL database, backend, and frontend in the same region.

### 1. Create PostgreSQL

Create a managed PostgreSQL database. Use its **internal** connection details for the backend. The backend expects a JDBC URL, for example:

```text
jdbc:postgresql://HOST:5432/DATABASE
```

Do not make the database publicly reachable for this setup.

### 2. Deploy the backend

Create a Render Web Service from this repository and use its Dockerfile. Set the health check path to `/actuator/health`. Add these environment variables in the Render dashboard (never commit secret values):

| Variable | Value |
| --- | --- |
| `DB_URL` | JDBC URL built from the database's internal host and database name |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | Base64-encoded random secret of at least 32 bytes; generate with `openssl rand -base64 32` |
| `BOOTSTRAP_ADMIN_USERNAME` | A private admin username for testing |
| `BOOTSTRAP_ADMIN_PASSWORD` | A unique password of at least 8 characters |
| `BOOTSTRAP_ADMIN_FIRST_NAME` | Admin's displayed first name |
| `CORS_ALLOWED_ORIGINS` | The exact deployed frontend origin, e.g. `https://your-frontend.onrender.com` |
| `JPA_SHOW_SQL` | `false` |

Render supplies `PORT`; the backend now uses it automatically. The bootstrap admin is created only if the database has no users. Save its credentials securely and change them after first sign-in if needed.

### 3. Deploy the frontend

Create a Render Static Site from the frontend repository with:

- Build command: `npm ci && npm run build`
- Publish directory: `dist`
- Build environment variable `VITE_API_URL`: `https://YOUR-BACKEND.onrender.com/api`

Add a rewrite rule from `/*` to `/index.html` with status `200` so React Router routes work on refresh. After Render gives the frontend its URL, put that exact origin in the backend's `CORS_ALLOWED_ORIGINS` and redeploy the backend.

`VITE_API_URL` is included in public browser code, so it must contain only the API URL, never credentials or secrets. Use test accounts and dummy invoice data for public testing.

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
