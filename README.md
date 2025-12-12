<div align="center">
  <img src="assets/logo-readme.png" alt="logo" height="80">
</div>

![Java](https://img.shields.io/badge/java-21-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white)
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)
![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white)
![CSS](https://img.shields.io/badge/css-%23663399.svg?style=for-the-badge&logo=css&logoColor=white)
![Bootstrap](https://img.shields.io/badge/bootstrap-%238511FA.svg?style=for-the-badge&logo=bootstrap&logoColor=white)
![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)

<h2>📋 Descrição do Projeto</h2>

O Enquete é uma aplicação web que permite aos usuários criar e participar de enquetes online. O sistema oferece autenticação segura, controle de acesso e uma interface intuitiva para gerenciamento de enquetes públicas e privadas.

<h2>Funcionalidades</h2>

<h3>🔐 Autenticação e Segurança</h2>

- Login tradicional com email e senha
- Login de teste, com usuário de demonstração temporário
- Login social com Google OAuth2
- Sistema Authorization Server próprio com fluxo Authorization Code
- Tokens JWT para autenticação segura
- Tokens armazenados via HttpOnly Cookies

<h3>📊Gestão de Enquetes</h3>

- Criação de enquetes públicas ou privadas
- Página inicial com enquetes públicas
- Dashboard pessoal com enquetes criadas pelo usuário
- Dashboard pessoal com enquetes votadas pelo usuário
- Sistema de votação em enquetes ativas
- Compartilhamento via URL
- Paginação com Spring Pageable (8 enquetes por página)

<h3>👤 Gestão de Conta</h3>

- Edição de username
- Alteração de senha (apenas para contas tradicionais)
- Visualização de email cadastrado
- Diferenciação automática entre contas tradicionais e sociais

<h2>🛠 Tecnologias Utilizadas</h2>

<h3>Backend</h3>

- Spring Boot 3.4.4
- Spring Security com JWT
- Spring Data JPA
- Spring OAuth2 Authorization Server
- PostgreSQL - Banco de dados
- Maven

<h3>Frontend</h3>

- Thymeleaf
- HTML5, CSS3, JavaScript
- Bootstrap

<h2>Como Executar o Projeto</h2>

<h3>Pré-requisito</h3>

- Docker e Docker Compose instalados

<h2>Configuração</h2>
<h3>1. Clone o repositório</h3>

```shell
git clone https://github.com/Leoo098/enquete-platform
cd enquete-platform
```

<h3>2. Configure as variáveis de ambiente</h3>

### Configure as variáveis de ambiente criando um arquivo `.env` na raíz do projeto

```shell
# Database
POSTGRES_DB=enquete_db_teste
POSTGRES_USER=postgres
POSTGRES_PASSWORD=teste

# Google OAuth2 (opcional, para utilizar o login social com google)
# Se não configurar, o login social com Google não funcionará
GOOGLE_CLIENT_ID=seu_client_id_google
GOOGLE_CLIENT_SECRET=seu_client_secret_google
```

### 3. Crie o arquivo `docker-compose.yml`

```shell
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://db:5432/${POSTGRES_DB}
      - SPRING_DATASOURCE_USERNAME=${POSTGRES_USER}
      - SPRING_DATASOURCE_PASSWORD=${POSTGRES_PASSWORD}
      
#      Adicionar caso queira utilizar o Google OAuth2
#      - SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_ID=${GOOGLE_CLIENT_ID}
#      - SPRING_SECURITY_OAUTH2_CLIENT_REGISTRATION_GOOGLE_CLIENT_SECRET=${GOOGLE_CLIENT_SECRET}
    depends_on:
      - db
    env_file:
      - .env

  db:
    image: postgres:16.9-alpine
    environment:
      - POSTGRES_DB=${POSTGRES_DB}
      - POSTGRES_USER=${POSTGRES_USER}
      - POSTGRES_PASSWORD=${POSTGRES_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data
      - ./sql/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    env_file:
      - .env

volumes:
  postgres_data:


```

<h6>* O banco é gerado automaticamente ao executar o docker-compose, com base no arquivo schema.sql no diretório sql da raíz do projeto.</h4>

<h3>4. Execute o projeto</h3>

```shell
# Suba os containers em background
docker-compose up -d

# Ou para ver os logs em tempo real
docker-compose up
```

<h3>5. Acesse a aplicação</h3>
<h4>Abra seu navegador e acesse:</h4>

```shell
http://localhost:8080
```

## ♦ Diagrama do Banco de Dados

<img src="assets/db_diagram.jpeg" style="display: block; margin: 0 auto; margin-block: 50px" alt="db">


<h3>📃 License</h3>

[MIT](LICENSE)