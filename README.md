# Spencer's Cardtopia | Sistema de Gestão

Projeto acadêmico completo, com CRUD de **produtos, clientes e vendas**, dashboard, controle de estoque e autenticação, desenvolvido em **Java 25 LTS, Spring Boot 4.2.0, Thymeleaf, HTML/CSS/JS e banco local XAMPP (MariaDB/MySQL)**.

## 1. Pré-requisitos no Windows

- JDK 25 LTS instalado e configurado em `JAVA_HOME`/`PATH` (`java -version`)
- Apache Maven 3.9 ou superior (`mvn -version`) e extensão Java/Spring Boot para VS Code
- XAMPP (inicie **MySQL** no painel; Apache é opcional, mas necessário para abrir o phpMyAdmin em `http://localhost/phpmyadmin`)
- Porta `3306` livre para o banco e `8080` livre para o Spring Boot

> O XAMPP moderno instala **MariaDB** sob o botão "MySQL". Por isso o projeto usa o driver JDBC MariaDB. O banco e os comandos SQL são compatíveis com a proposta acadêmica de MySQL/XAMPP. Não é preciso instalar MySQL Server separadamente.

## 2. Criar o banco pelo XAMPP

1. Abra o **XAMPP Control Panel** e inicie `Apache` e `MySQL`.
2. Acesse `http://localhost/phpmyadmin`.
3. Clique na aba **SQL**, abra `database/01_criar_banco.sql`, copie o conteúdo e execute.
4. Confira se `cardtopia_db` aparece na lateral.
5. As tabelas `produtos`, `clientes` e `vendas` serão criadas pelo Hibernate na **primeira execução do Java**. Antes disso é normal que o banco esteja vazio. Depois de executar o sistema pela primeira vez, você pode rodar o script opcional `database/02_dados_exemplo.sql` no phpMyAdmin para inserir produtos e clientes fictícios.

O SQL cria o usuário local `cardtopia_app`, senha de desenvolvimento `TroqueEstaSenhaDev2026` e concede permissões apenas ao banco do projeto. **Altere essa senha antes de usar dados reais**. Se o usuário já existia, `CREATE USER IF NOT EXISTS` não redefine a senha; ajuste manualmente a conta ou configure `DB_PASSWORD` com a senha existente.

## 3. Executar no Visual Studio Code

1. Extraia o ZIP, escolha **File > Open Folder**, abra a pasta que contém `pom.xml`.
2. Aguarde o VS Code importar o Maven. Se solicitadas, instale `Extension Pack for Java` e `Spring Boot Extension Pack`.
3. Abra um terminal na pasta raiz e execute:

```powershell
$env:JAVA_HOME = "C:\caminho\para\jdk-25"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -version
mvn -version
mvn clean spring-boot:run
```

4. No navegador, abra **http://localhost:8080/login**.
5. Credenciais de demonstração (somente no computador local):
   - Usuário: `admin`
   - Senha: `AlterarAntesDaEntrega2026!`

A senha é codificada com BCrypt na inicialização. Para trocar credenciais no PowerShell, antes de rodar o Maven:

```powershell
$env:APP_ADMIN_USER="admin"
$env:APP_ADMIN_PASSWORD="SuaSenhaForteAqui"
$env:DB_PASSWORD="SuaSenhaDoBancoAqui"
mvn spring-boot:run
```

O arquivo `.env.example` serve apenas como referência: **Spring Boot não carrega automaticamente arquivos `.env`** sem biblioteca adicional. Defina variáveis no terminal ou na sua IDE quando alterar as credenciais.

## 4. Ordem para demonstrar em sala

1. Fazer login.
2. Cadastrar um produto "Secret Lair x Dune: Spice", SKU `DUNE-SPICE-F`, edição "My Arrakis, My Superdrop", acabamento Foil, preço `450.00`, estoque `5`.
3. Cadastrar um cliente fictício.
4. Registrar venda PENDENTE de 2 unidades: estoque passa de 5 para 3; valor calculado R$ 900,00.
5. Confirmar a venda: estoque permanece em 3; dashboard exibe receita confirmada.
6. Cancelar a venda: estoque retorna a 5.
7. Demonstrar bloqueio ao tentar vender 10 unidades sem estoque.
8. Demonstrar validação de campos e proteção de exclusão de registros com histórico de vendas.

## 5. Mapa dos arquivos

```text
src/main/java/br/com/scardtopia/gestao/
  CardtopiaApplication.java
  config/SecurityConfig.java
  model/{Produto,Cliente,Venda,StatusVenda}.java
  form/VendaForm.java
  repository/{ProdutoRepository,ClienteRepository,VendaRepository}.java
  service/{VendaService,RegraNegocioException}.java
  controller/{HomeController,ProdutoController,ClienteController,VendaController}.java
src/main/resources/
  application.properties
  templates/{dashboard,login}.html
  templates/fragments/base.html
  templates/{produtos,clientes,vendas}/{lista,form}.html
  static/{css/app.css,js/app.js}
database/01_criar_banco.sql
src/test/java/.../service/VendaServiceTest.java
```

**Arquitetura MVC:** Browser -> Controller -> Service (regras de vendas) -> Repository (Spring Data JPA) -> MySQL/MariaDB. Thymeleaf renderiza o HTML no servidor.

## 6. Segurança aplicada

- Autenticação e autorização: `Spring Security`, todas as rotas de gestão exigem perfil ADMIN.
- Senha: `BCryptPasswordEncoder`, não registrada em texto puro no armazenamento da autenticação (usuário em memória de demonstração).
- CSRF: tokens em formulários POST, inclusive exclusão e logout; não foi desabilitado globalmente.
- Validação no servidor: `jakarta.validation` (`@NotBlank`, `@Email`, `@Min`, `@DecimalMin`).
- JPA: consultas tipadas/parametrizadas, reduzindo exposição a SQL Injection; template usa `th:text`, que escapa HTML.
- Integridade: transações `@Transactional`; validação de estoque com bloqueio pessimista; exclusão de produtos/clientes referenciados bloqueada.

**Limites desta versão didática:** contas administrativas são configuradas em memória; não existe cadastro de usuários, 2FA, HTTPS, logs de auditoria, perfis adicionais, pagamentos reais ou rotina fiscal. Para produção, exigir armazenamento seguro de segredos, HTTPS, migrações versionadas, backup, testes integrados e controle de permissões mais granular.

## 7. Regras de negócio e escopo

- **Produto**: nome, SKU único, categoria, edição, Foil, preço e saldo de estoque.
- **Cliente**: nome, e-mail único, telefone.
- **Venda**: associa **um único produto** a um cliente e quantidade. `PENDENTE` ou `CONFIRMADA` reserva estoque. `CANCELADA` libera reserva. O preço de cada venda é gravado como retrato do preço do produto no momento de salvar/editar.
- **Dashboard**: somente `CONFIRMADA` entra na receita confirmada; produtos com até três unidades aparecem como estoque baixo.
- **Exclusão de venda**: reverte reserva se a venda estava pendente/confirmada. Produtos/clientes vinculados a vendas não podem ser excluídos.
- **Escopo acadêmico**: não contempla múltiplos itens por pedido, frete, imposto, cupom, parcelamento, NF-e, pagamento ou integração com o site de vendas da loja. Pode ser ampliado posteriormente.

## 8. Testes

```powershell
mvn test
```

`VendaServiceTest` contempla reserva de estoque, bloqueio por saldo insuficiente, cancelamento e reposição após exclusão. Consulte `documentacao/PLANO_DE_TESTES.md` para validação manual no XAMPP. **O projeto deve ser testado no seu computador com XAMPP antes de apresentar prints como evidências de execução.**

## 9. Solução de problemas comuns

- **`MySQL shutdown unexpectedly` no XAMPP:** consulte `Logs > mysql_error.log`, verifique conflito na porta 3306 no Windows (`netstat -ano | findstr :3306`) e se já existe outro serviço MySQL/MariaDB. Não apague a pasta `mysql/data` nem substitua arquivos de banco sem backup.
- **`Access denied for user cardtopia_app`**: reexecute o SQL com conta administrativa, confira as credenciais e o host do usuário.
- **`Unknown database 'cardtopia_db'`**: rode o SQL antes de iniciar o Java.
- **`Port 8080 was already in use`**: encerre a outra aplicação ou altere `server.port`.
- **`mvn não é reconhecido`**: instale Maven e adicione sua pasta `bin` ao `PATH`.
- **Erro ao salvar produto em vendas**: não é permitido excluir produto/cliente com histórico de venda; edite ou mantenha o registro.

## 10. Informações para entrega

- Tema: gestão interna da Spencer's Cardtopia, comércio de cards colecionáveis e Secret Lair.
- Documentação acadêmica: `documentacao/Documentacao_Spencers_Cardtopia.pdf`.
- Apresentação: `documentacao/Apresentacao_Spencers_Cardtopia.pptx`.
- Edite os campos entre colchetes `[INSTITUIÇÃO]`, `[INTEGRANTES]`, `[RGM]`, `[PROFESSOR(A)]`, `[CIDADE]` nas entregas.

Fontes técnicas: https://docs.spring.io/spring-boot/3.5/ ; https://docs.spring.io/spring-security/reference/ ; https://www.thymeleaf.org/documentation.html ; https://www.apachefriends.org/faq_windows.html .
