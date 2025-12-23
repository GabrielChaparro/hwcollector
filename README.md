
# HWCollectors Backend

Backend en **Java + Spring Boot** para gestionar colecciones de Hot Wheels y un **marketplace** con ventas fijas y subastas entre usuarios autenticados vía **Keycloak**.

## Stack

- Java 17+
- Spring Boot 3
    - Spring Web
    - Spring Security (OAuth2 Resource Server)
    - Spring Data JPA
    - WebSocket (STOMP)
    - Scheduling
- PostgreSQL
- Keycloak (Auth / Roles)
- Maven

## Modelo de Dominio

### Entidades principales

- **User**
    - `id : Long`
    - `keycloakId : String` (sub del token)
    - `email : String`
    - `balance : Double`
    - `collectionItems : List<CollectionItem>`
    - `listings : List<Listing>`

- **HotWheel**
    - `id : Long`
    - `code : String` (único, p.e. `HWF12`)
    - `name, year, color, series, imageUrl`

- **CollectionItem**
    - `id : Long`
    - `user : User` (`ManyToOne`)
    - `hotwheel : HotWheel` (`ManyToOne`)
    - `condition : String`
    - `acquiredDate : LocalDate`
    - `notes : String`

- **Listing**
    - `id : Long`
    - `seller : User`
    - `hotwheel : HotWheel`
    - `type : ListingType` (`FIXED`, `AUCTION`)
    - `price : Double`
    - `currentBid : Double`
    - `highestBidder : User`
    - `endDate : LocalDateTime`
    - `status : ListingStatus` (`ACTIVE`, `SOLD`, `CANCELLED`)
    - `bids : List<Bid>`
    - `createdAt : LocalDateTime`

- **Bid**
    - `id : Long`
    - `listing : Listing`
    - `bidder : User`
    - `amount : Double`
    - `timestamp : LocalDateTime`

## DTOs y Mappers

Se usan DTOs para no exponer directamente las entidades JPA y evitar bucles de serialización.

### DTOs

- `CollectionItemDto`
    - `id`
    - `hotwheelCode`
    - `hotwheelName`
    - `condition`
    - `acquiredDate`

- `ListingDto`
    - `id`
    - `hotwheelCode`
    - `hotwheelName`
    - `sellerEmail`
    - `type`
    - `price`
    - `currentBid`
    - `status`
    - `endDate`

- `BidDto`
    - `id`
    - `amount`
    - `bidderEmail`
    - `timestamp`

### Mappers

- `CollectionItemMapper`
    - `toDto(CollectionItem)`
    - `toDtoList(List<CollectionItem>)`

- `ListingMapper`
    - `toDto(Listing)`
    - `toDtoList(List<Listing>)`

- `BidMapper`
    - `toDto(Bid)`
    - `toDtoList(List<Bid>)`

## Seguridad (Keycloak + JWT)

- Keycloak actúa como **Authorization Server**.
- El backend Spring Boot es **Resource Server** con JWT.
- `keycloakId` se obtiene desde `Authentication.getName()` y se usa como clave en la tabla `users`.
- Si el usuario no existe en BD, se **autocrean** registros en `User` con saldo inicial.

Configuración básica (application.yml):

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/hotwheels
    username: hotwheels_user
    password: hotwheels123
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

keycloak:
  realm: hotwheels-realm
  auth-server-url: http://localhost:8080
  resource: hotwheels-api
  credentials:
    secret: TU_CLIENT_SECRET
Endpoints principales
Colecciones
GET /api/collections/my
Devuelve la colección del usuario autenticado como List<CollectionItemDto>.

POST /api/collections/add

Body:

json
{
  "hotwheelCode": "HWF12",
  "condition": "MINT"
}
Si el usuario no existe en users, se crea.

Busca el HotWheel por code.

Crea un CollectionItem y devuelve CollectionItemDto.

Listings (Marketplace)
POST /api/listings

Body:

json
{
  "hotwheelCode": "HWF12",
  "type": "AUCTION",
  "price": 10.0,
  "durationHours": 2
}
Valida que el usuario tenga ese HotWheel en su colección.

Crea un Listing (AUCTION o FIXED).

Devuelve ListingDto.

GET /api/listings/active?type=AUCTION|FIXED
Devuelve List<ListingDto> con las listings activas, filtrando por tipo opcionalmente.

POST /api/listings/{id}/bid

Body:

json
{ "amount": 15.0 }
Solo para listings AUCTION y ACTIVE.

Valida que la puja sea mayor que currentBid.

Crea Bid, actualiza currentBid y highestBidder.

Envía evento WebSocket a /topic/listings/{id}.

Devuelve ListingDto actualizado.

GET /api/listings/{id}/bids
Devuelve List<BidDto> con el historial de pujas de la listing.

POST /api/listings/{id}/buy
Para listings FIXED:

Valida estado y balance del comprador.

Transfiere saldo entre usuarios.

Marca listing como SOLD.

WebSockets (Pujas en tiempo real)
Endpoint STOMP: /ws

Prefijos:

Cliente envía a: /app/...

Servidor envía a: /topic/...

Canales
Cliente se suscribe a:
/topic/listings/{listingId}

Al hacer POST /api/listings/{id}/bid, se emite un BidEvent a ese canal con:

listingId

amount

bidderName

timestamp

Cierre automático de subastas
Servicio AuctionService:

@Scheduled(fixedRate = 30000):

Busca listings ACTIVE con endDate < now.

Para cada una:

Si hay highestBidder y currentBid:

Transfiere saldo buyer → seller.

Marca listing SOLD.

Transfiere el CollectionItem al comprador.

Envía AuctionClosedEvent a /topic/auctions/closed.

Si no hay pujas:

Marca listing CANCELLED.

Seed de datos (Hot Wheels 2025)
Ejemplo de carga en tabla hotwheels:

sql
INSERT INTO hotwheels (code, name, year, color, series, image_url) VALUES
('HWF01', 'Ferrari F40', '2025', 'Red', 'HW Race', 'https://example.com/hw_f40.jpg'),
('HWF12', 'Ferrari F40 Competizione', '2025', 'Red/White', 'HW Race HW5', 'https://hotwheels.fandom.com/...'),
('HWM50', 'Honda Civic Type R', '2025', 'Red', 'HW Mainstreet HW1', 'https://hotwheels.fandom.com/...');
Flujo típico
Usuario se autentica en Keycloak y obtiene access_token.

Llama a POST /api/collections/add con un hotwheelCode para añadir a su colección.

Crea una subasta con POST /api/listings.

Otros usuarios ven subastas con GET /api/listings/active.

Pujan con POST /api/listings/{id}/bid y ven actualizaciones en tiempo real vía WebSocket.

AuctionService cierra subastas expiradas y realiza la transferencia de saldo y propiedad.