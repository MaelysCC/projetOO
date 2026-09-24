# Series Tracker Backend — Architecture

The project includes a Vue.js client that communicates with the Spring REST API. The backend consists of the REST API, a gRPC server, and an H2 database.

## Request flow

```text
Vue.js client :5173
    │ HTTP / JSON
    ▼
REST API :8080
    │ gRPC / Protocol Buffers
    ▼
gRPC server :9090
    │ JPA repositories
    ▼
H2 database
```

The REST controllers call `GrpcRestService`, which uses a blocking gRPC client. The gRPC server implements the contract in `rpcServiceServer/src/main/proto/seriesTracker.proto`. It delegates work to the existing JPA services and repositories, which persist users, works, and tracking entries in H2.

## Modules

- `client/` : Vue.js frontend for browsing works and managing a user's tracking list through the REST API.
- `restService/` : Spring Boot HTTP controllers, REST-to-gRPC client, JPA entities, repositories, and shared application services.
- `rpcServiceServer/` : Spring Boot gRPC server, Protocol Buffers contract, and server startup lifecycle.

The gRPC server depends on `restService` to reuse the existing JPA services, entities, and repositories without maintaining duplicate copies. Both backend modules use Java 25. H2 runs in memory, so its data is cleared when the gRPC server restarts. On startup, five sample works are inserted when the catalogue is empty.

## Run locally

Start the gRPC server and REST API in separate terminals from the repository root:

```powershell
.\gradlew.bat :rpcServiceServer:bootRun
.\gradlew.bat :restService:bootRun
```

Start the Vue.js client in another terminal:

```powershell
cd client
npm install
npm run dev
```

The Vue.js development server listens on `5173`; the REST API listens on `8080`; gRPC listens on `9090`. Run backend tests from the repository root with `.\gradlew.bat test`.
