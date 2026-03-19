# 🏭 Factory Production Optimizer

Sistema para gerenciamento de insumos e otimização de produção industrial.  
O sistema analisa o estoque atual e sugere quais produtos fabricar para obter o **maior valor total de venda**.

---

## 🛠️ Tecnologias

| Camada | Tecnologia |
|--------|-----------|
| Back-end | Java 17 + Spring Boot |
| Front-end | Vue.js 3 + Vite |
| Banco de Dados | H2 (in-memory) |
| Testes Back | JUnit 5 + Mockito |
| Testes Front | Vitest + Vue Test Utils |

---

## 📋 Pré-requisitos

- **Java 17+**
- **Maven 3.8+**
- **Node.js 18+**
- **npm 9+**

---

## 🚀 Como rodar localmente

### 1. Clone o repositório

```bash
git clone https://github.com/PamellaBelo/Teste-Full-Stack.git
```

---

### 2. Back-end

```bash
cd backend
mvn spring-boot:run
```

A API estará disponível em: **http://localhost:8080**

O console do banco H2 estará disponível em: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: *(vazio)*

---

### 3. Front-end

Em outro terminal:

```bash
cd frontend
npm install
npm run dev
```

A aplicação estará disponível em: **http://localhost:5173**

---

## ✅ Testes

### Back-end

```bash
cd backend
mvn test
```

### Front-end

```bash
cd frontend
npm test
```

---

## 📡 Endpoints da API

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| GET | `/api/raw-materials` | Listar matérias-primas |
| POST | `/api/raw-materials` | Criar matéria-prima |
| PUT | `/api/raw-materials/{id}` | Atualizar matéria-prima |
| DELETE | `/api/raw-materials/{id}` | Excluir matéria-prima |
| GET | `/api/products` | Listar produtos |
| POST | `/api/products` | Criar produto |
| PUT | `/api/products/{id}` | Atualizar produto |
| DELETE | `/api/products/{id}` | Excluir produto |
| GET | `/api/production/optimize` | Sugestão de produção otimizada |

---

## 🧠 Como funciona o algoritmo de otimização

1. Carrega o estoque atual de todas as matérias-primas
2. Calcula quantas unidades de cada produto podem ser fabricadas com o estoque disponível
3. Ordena os produtos por **maior valor unitário** (greedy)
4. Simula a produção debita o estoque a cada produto alocado
5. Retorna a lista de produtos sugeridos com quantidade e faturamento esperado

---

## 🌐 Internacionalização

O front-end suporta **Português (PT-BR)** e **Inglês (EN)**.  
O idioma pode ser alternado pelo botão na parte inferior da sidebar.

<img width="1799" height="988" alt="image" src="https://github.com/user-attachments/assets/05c613e1-1ac5-4996-9cbe-712c0ec8b73e" />
<img width="1803" height="999" alt="image" src="https://github.com/user-attachments/assets/07886f07-4c2a-4a48-be98-71391f38a9ec" />

<img width="1801" height="998" alt="image" src="https://github.com/user-attachments/assets/61dfc8e9-52ce-4f38-aa5f-a3ed7803486e" />
<img width="1803" height="997" alt="image" src="https://github.com/user-attachments/assets/92e1b583-0df5-4319-9ab7-b725b644f823" />

<img width="1806" height="997" alt="image" src="https://github.com/user-attachments/assets/a41e446d-ba5e-4296-84a4-b9a405e797f2" />





