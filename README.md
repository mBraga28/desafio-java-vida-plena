
# Medical Appointment API - Vida Plena

API REST para gerenciamento de atendimentos médicos, desenvolvida com **Java + Spring Boot**.

O projeto inclui autenticação JWT, controle de acesso por roles, documentação Swagger e execução via Docker.

---

## 🚀 Tecnologias utilizadas

- Java 17
- Spring Boot 3.3.9
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- H2 Database (testes)
- Swagger / OpenAPI
- Docker + Docker Compose
- Maven

---

## 📥 Como baixar o projeto

Para clonar o repositório, utilize o comando abaixo no seu terminal:

```bash
git clone https://github.com/mBraga28/desafio-java-vida-plena.git
```

Após clonar, entre na pasta do projeto:

```bash
cd desafio-java-vida-plena
```

---

## ▶️ Como rodar o projeto

### ✅ Pré-requisitos

- Docker instalado
- Docker Compose instalado

---

### 🐳 Rodar com Docker (recomendado)

Na raiz do projeto via Terminal(cmd, bash, etc):

```bash
docker compose -f docker/docker-compose.yml up --build
```

A aplicação ficará disponível em:

```
http://localhost:8080
```

Swagger:

```
http://localhost:8080/swagger-ui/index.html
```

---

## 🔐 Autenticação

A API utiliza **JWT Bearer Token** com expiração de **15 minutos**.

### 👤 Usuários de seed

| Username     | Password | Role         |
| ------------ | -------- | ------------ |
| admin        | 123456   | ADMIN        |
| doctor       | 123456   | DOCTOR       |
| receptionist | 123456   | RECEPTIONIST |

---

### 🔑 Como autenticar

1. Acesse o Swagger
2. Use endpoint:

```
POST /token/login
```

Exemplo:

```json
{
  "username": "admin",
  "password": "123456"
}
```

3. Copie o token retornado
4. Clique em **Authorize** no Swagger
5. Cole:

```
SEU_TOKEN
```

Agora pode testar endpoints protegidos.

**⚠️ Atenção:** O token expira em 15 minutos. Caso receba erro 401, gere um novo token.

---

## 📌 Endpoints principais

### 🔐 Autenticação

* `POST /token/login` → gerar JWT

### 📅 Atendimentos

* `POST /appointments` → criar atendimento
* `GET /appointments` → listar todos
* `GET /appointments/{id}` → buscar por id
* `PUT /appointments/{id}` → atualizar atendimento
* `PUT /appointments/{id}/status` → atualizar status
* `DELETE /appointments/{id}` → remover

Todos documentados no Swagger.

---

## 🧪 Como testar a API (passo a passo)

Após subir o projeto com Docker, siga os passos abaixo para testar os principais endpoints usando o Swagger e considerando que você já gerou um token JWT conforme explicado na seção de autenticação:


---

## 📅 1️⃣ Criar um atendimento

Acesse:

```
POST /appointments
```

Exemplo de body:

```json
{
  "patientName": "João Silva",
  "doctorName": "Dr. Silva",
  "specialty": "Cardiologia",
  "appointmentDateTime": "2026-03-01T10:00:00"
}
```

Clique em **Execute**.

A resposta retornará o objeto criado com `id` e status `SCHEDULED`.

---

## 📖 2️⃣ Listar atendimentos

```
GET /appointments
```

Verifique se o atendimento criado aparece na lista.

---

## ✏️ 3️⃣ Atualizar atendimento

```
PUT /appointments/{id}
```

Informe o ID retornado anteriormente e altere:

```json
{
  "doctorName": "Dr. João",
  "specialty": "Dermatologia",
  "appointmentDateTime": "2026-03-02T14:00:00"
}
```

Execute e confirme a atualização.

---

## 🔄 4️⃣ Atualizar status

```
PUT /appointments/{id}/status
```

**Status válidos:**
- `SCHEDULED` (agendado)
- `IN_PROGRESS` (em andamento)
- `COMPLETED` (concluído)
- `CANCELED` (cancelado)

Exemplo:

```json
{
  "status": "IN_PROGRESS"
}
```

---

## ❌ 5️⃣ Remover atendimento

```
DELETE /appointments/{id}
```

Confirme que foi removido.

---

## 🧠 Decisões técnicas

### 🔐 Segurança

* JWT stateless para autenticação
* Controle de acesso baseado em roles (ENUM)
* Spring Security configurado com filtro JWT

### 🧩 DTOs separados

* DTOs de criação e atualização não recebem `id`
* `id` é sempre definido pelo banco
* Evita inconsistência no contrato REST

### 🐳 Docker

* Multi-stage build para reduzir tamanho da imagem
* PostgreSQL em container separado (porta 5433 no host)
* Configuração via docker-compose

### 📚 Swagger

* Documentação automática dos endpoints
* Suporte a autenticação JWT direto na interface

---

## 📁 Estrutura do Projeto

```
src/main/java/com/mv/appointment/
├── config/          # Configurações (Security, Swagger, etc)
├── controllers/     # Endpoints REST
├── domain/          # Entidades e Enums
├── dtos/            # Data Transfer Objects
├── exceptions/      # Tratamento de erros
├── repositories/    # Acesso ao banco de dados
├── security/        # Lógica de JWT
└── services/        # Regras de negócio
```

---

## 🧪 Testes

Para executar os testes automatizados:

```bash
./mvnw test
```

Ou com Docker:

```bash
docker compose -f docker/docker-compose.yml exec app ./mvnw test
```

---

## 🗄️ Acesso ao banco de dados

O PostgreSQL está exposto na porta **5433** (não 5432) para evitar conflitos.

Para conectar diretamente:

```
Host: localhost
Port: 5433
Database: vida-plena
User: postgres
Password: 123456
```

---

## 📎 Observações finais

Este projeto foi desenvolvido como desafio técnico com foco em:

* boas práticas REST
* organização em camadas
* segurança com JWT
* documentação clara
* execução simples via Docker

Algumas decisões foram simplificadas para manter o escopo adequado ao desafio.

---

## 👨‍💻 Autor

Marco Braga
