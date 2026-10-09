# Baozi Store — API REST

Trabalho da disciplina **Desenvolvimento Web Back-End** (UNINTER).
Aluno: **Vitor** — RU 5570847

API REST para o controle básico de clientes, produtos e pedidos da Baozi Store,
uma pequena loja de pãozinho chinês.

## Tecnologias

- Java 21
- Spring Boot 4 (Spring Web MVC + Spring Data JPA)
- MariaDB
- Maven
- Postman (testes)

## Estrutura

```
src/main/java/com/baozi/store/
├── model/        # Entidades JPA (AbstractModel, Cliente, Produto, Pedido)
├── repository/   # Interfaces JpaRepository
└── controller/   # Endpoints REST
```

## Entidades

| Entidade | Campos |
|---|---|
| Cliente | `id` (Long), `nome` (String), `clienteDesde` (LocalDate) |
| Produto | `id` (Long), `nome` (String), `preco` (BigDecimal), `estoque` (Boolean) |
| Pedido  | `id` (Long), `cliente` (FK → Cliente), `produto` (FK → Produto), `quantidade` (Integer) |

Todas as entidades herdam o `id` de `AbstractModel` (`@MappedSuperclass`).

## Endpoints

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| POST | `/clientes` | Cria um cliente | 201 |
| GET | `/clientes` | Lista todos os clientes | 200 |
| GET | `/clientes/{id}` | Consulta um cliente | 200 / 404 |
| PUT | `/clientes/{id}` | Atualiza um cliente | 200 / 404 |
| DELETE | `/clientes/{id}` | Apaga um cliente | 204 / 404 / 409 |
| POST | `/produtos` | Cria um produto | 201 |
| GET | `/produtos` | Lista todos os produtos | 200 |
| GET | `/produtos/{id}` | Consulta um produto | 200 / 404 |
| PUT | `/produtos/{id}` | Atualiza um produto | 200 / 404 |
| DELETE | `/produtos/{id}` | Apaga um produto | 204 / 404 / 409 |
| POST | `/pedidos` | Cria um pedido | 201 / 400 |
| GET | `/pedidos` | Lista todos os pedidos | 200 |
| GET | `/pedidos/{id}` | Consulta um pedido | 200 / 404 |
| PUT | `/pedidos/{id}` | Atualiza um pedido | 200 / 400 / 404 |
| DELETE | `/pedidos/{id}` | Apaga um pedido | 204 / 404 |

O DELETE de um cliente ou produto que possui pedidos retorna **409 Conflict**,
pois a chave estrangeira impede a exclusão.

### Exemplos de corpo (JSON)

```json
// POST /clientes
{ "nome": "Vitor5570847", "clienteDesde": "2026-10-09" }

// POST /produtos
{ "nome": "Doushabao", "preco": 8.90, "estoque": true }

// POST /pedidos
{ "cliente": { "id": 1 }, "produto": { "id": 1 }, "quantidade": 3 }
```

## Como executar

1. Crie o banco no MariaDB/MySQL:
   ```sql
   CREATE DATABASE baozi_store;
   ```
2. Informe usuário e senha do banco: edite `src/main/resources/application.properties`
   ou crie `src/main/resources/application-local.properties` com:
   ```properties
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   ```
3. Rode a aplicação (requer Java 21):
   ```bash
   ./mvnw spring-boot:run
   ```
4. A API fica disponível em `http://localhost:8080`. As tabelas são criadas
   automaticamente pelo Hibernate.

## Testes no Postman

Importe a collection `postman/BaoziStore.postman_collection.json` e execute as
requisições na ordem das pastas.
