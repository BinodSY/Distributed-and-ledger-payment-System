# Ledger-Based Wallet & Payment System

A **production-grade, ledger-driven wallet system** inspired by real-world payment platforms like Paytm / PhonePe.  
The system supports **atomic, idempotent wallet-to-wallet money transfers** with **strong consistency guarantees** and **concurrency safety**.

---

## 🚀 Key Highlights

- Ledger-first accounting model (immutable financial records)
- Atomic wallet-to-wallet transfers
- Idempotent APIs to safely handle retries
- Strong consistency using database transactions
- Concurrency-safe money movement (`SELECT FOR UPDATE`)
- JWT-based authentication with custom security principal
- Modular Monolith architecture (clean separation of concerns)

---

## 🧠 System Design Overview

### Core Principles

- **Ledger is the source of truth**
- Wallet balance is a **cached optimization**
- Every successful transfer produces **exactly two ledger entries**
- Money movement is **atomic or nothing**
- No direct balance updates without ledger entries

---

## 🧱 Architecture

```
┌────────────┐
│   Client   │
└─────┬──────┘
      │
      ▼
┌────────────┐
│ API Layer  │ (Controllers, DTOs)
└─────┬──────┘
      │
      ▼
┌────────────┐
│  Service   │ (WalletTransferService)
│   Layer    │
└─────┬──────┘
      │
      ▼
┌────────────┐
│Persistence │ (JPA + PostgreSQL)
│   Layer    │
└────────────┘
```

**Modules:**
- `auth-module` → JWT authentication & authorization
- `wallet-module` → Wallet, Ledger, Transfer logic
- `transaction-module` → Transaction intent & history
- `api-module` → REST controllers & DTOs

---

## 🗃️ Database Schema

### 1️⃣ Users

```sql
CREATE TABLE user (
  user_id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  name           VARCHAR(100),
  phone          VARCHAR(20) UNIQUE NOT NULL,
  email          VARCHAR(255) NOT NULL,
  password_hash  VARCHAR(255) NOT NULL,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

### 2️⃣ Wallets

```sql
CREATE TABLE wallet (
  wallet_id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id        UUID NOT NULL,
  balance_cache  BIGINT NOT NULL CHECK (balance_cache >= 0),
  version        BIGINT NOT NULL,
  created_at     TIMESTAMPTZ NOT NULL DEFAULT now(),
  FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

⚠️ `balance_cache` is a derived cache, not the source of truth.

### 3️⃣ Transactions (Intent)

```sql
CREATE TABLE transaction (
  txn_id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  type              VARCHAR(20) NOT NULL,
  status            VARCHAR(20) NOT NULL,
  source_type       VARCHAR(20) NOT NULL,
  source_ref        VARCHAR(50) NOT NULL,
  destination_type  VARCHAR(20) NOT NULL,
  destination_ref   VARCHAR(50) NOT NULL,
  txn_amount        BIGINT NOT NULL,
  failure_reason    VARCHAR(255),
  idempotency_key   VARCHAR(100) UNIQUE,
  created_at        TIMESTAMPTZ NOT NULL DEFAULT now()
);
```

### 4️⃣ Ledger Entries (Source of Truth)

```sql
CREATE TABLE ledger_entry_ (
  ledger_id       UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  txn_id          UUID NOT NULL,
  wallet_id       UUID NOT NULL,
  entry_type      VARCHAR(10) CHECK (entry_type IN ('DEBIT','CREDIT')),
  amount          BIGINT NOT NULL CHECK (amount > 0),
  balance_after   BIGINT NOT NULL,
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  FOREIGN KEY (txn_id) REFERENCES transactions(txn_id),
  FOREIGN KEY (wallet_id) REFERENCES wallets(wallet_id),
  UNIQUE (txn_id, wallet_id, entry_type)
);
```

**Ledger immutability enforced via DB trigger:**
- ❌ UPDATE not allowed
- ❌ DELETE not allowed

---

## 🔄 Wallet-to-Wallet Transfer Flow

```
Client
  ↓
POST /wallets/transfer
  ↓
Validate request + Idempotency
  ↓
BEGIN TRANSACTION
  ↓
Lock wallets (FOR UPDATE)
  ↓
Create transaction (INITIATED)
  ↓
Debit source wallet
  ↓
Credit destination wallet
  ↓
Insert 2 ledger entries
  ↓
Update wallet balance cache
  ↓
Mark transaction SUCCESS
  ↓
COMMIT

If any step fails → ROLLBACK
```

---

## 🔐 Authentication & Authorization

- **JWT-based authentication**
- JWT subject = userId (UUID)
- Email stored as claim (metadata)
- Custom immutable principal object used:
  - `userId`
  - `email`
  - `role`

```java
@AuthenticationPrincipal CustomUserPrincipal principal
```

No DB lookup required per request.

---

## 🌐 API Endpoints

### 1️⃣ Transfer Money

**POST** `/api/wallets/transfer`

**Request:**
```json
{
  "toWalletId": "uuid",
  "amount": 5000,
  "idempotencyKey": "client-generated-uuid"
}
```

**Response:**
```json
{
  "txnId": "uuid",
  "status": "SUCCESS",
  "fromWalletBalance": 95000,
  "toWalletBalance": 205000
}
```

### 2️⃣ Get Wallet Summary

**GET** `/api/wallets/me`

**Response:**
```json
{
  "walletId": "uuid",
  "balance": 95000,
  "currency": "INR"
}
```

### 3️⃣ Transaction History

**GET** `/api/transactions?limit=20&cursor=...`

**Response:**
```json
{
  "items": [
    {
      "txnId": "uuid",
      "type": "TRANSFER",
      "status": "SUCCESS",
      "amount": 5000,
      "direction": "DEBIT",
      "createdAt": "2024-01-01T10:00:00Z"
    }
  ],
  "nextCursor": "..."
}
```

---

## 🧪 Correctness Guarantees (Invariants)

For every **SUCCESS** transaction:

- ✅ Exactly 2 ledger entries
- ✅ One DEBIT, one CREDIT
- ✅ Same amount
- ✅ Wallet balance cache matches ledger `balance_after`

If any invariant breaks → system is incorrect.

---

## ⚠️ Why This Is Production-Grade

- ✅ Prevents double spending via DB locking
- ✅ Safe retries via idempotency keys
- ✅ Ledger-based auditability
- ✅ Clear separation of concerns
- ✅ No business logic in controllers
- ✅ No balance mutation without ledger

---

## 🛠️ Tech Stack

- **Java 17**
- **Spring Boot**
- **Spring Security** (JWT)
- **PostgreSQL**
- **JPA / Hibernate**
- **Modular Monolith Architecture**

---

## 📌 Future Enhancements

- Rate limiting for transfers
- Async reconciliation jobs
- Ledger audit APIs
- Metrics & monitoring
- Fraud detection rules
- Multi-currency support

---

## 🧠 Learning Outcome

This project demonstrates real-world backend engineering concepts:

- Payments domain modeling
- Distributed-system safety within a monolith
- Data consistency under concurrency
- Secure API design

---

## 👤 Author

Built by **Binod (Piku)**  
Focused on backend systems, payments, and cloud-native design.
