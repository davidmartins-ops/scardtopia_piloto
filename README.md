# Spencer's Cardtopia | Sistema de Gestão

Projeto acadêmico completo, com CRUD de **produtos, clientes e vendas**,
dashboard, controle de estoque e autenticação, desenvolvido em **Java 25 LTS,
Spring Boot 4.2.0, Thymeleaf, HTML/CSS/JS e banco local XAMPP (MariaDB/MySQL)**.

## 1. Pré-requisitos no Windows

- JDK 25 LTS instalado e configurado em `JAVA_HOME`/`PATH` (`java -version`)
- Apache Maven 3.9 ou superior (`mvn -version`) e extensão Java/Spring Boot
  para VS Code
- XAMPP (inicie **MySQL** no painel; Apache é opcional, mas necessário para
  abrir o phpMyAdmin em `http://localhost/phpmyadmin`)
- Porta `3306` livre para o banco e `8080` livre para o Spring Boot

## 2. Criar o banco pelo XAMPP

1. Abra o **XAMPP Control Panel** e inicie `Apache` e `MySQL`.
2. Acesse `http://localhost/phpmyadmin`.
3. Clique na aba **SQL**, abra `database/banco.sql`, copie o conteúdo e execute.
4. Confira se `cardtopia_db` aparece na lateral.
5. As tabelas `produtos`, `clientes` e `vendas` serão criadas pelo Hibernate
   na **primeira execução do Java**.

O SQL cria o usuário local `cardtopia_app` e concede permissões apenas ao banco
do projeto. Antes de executar o script, substitua `CHANGE_ME_LOCALLY` por uma
senha local e configure o mesmo valor em `DB_PASSWORD`. Reexecutar o script
redefine essa senha; use-o somente no ambiente local.

## 3. Executar no Visual Studio Code

1. Extraia o ZIP, escolha **File > Open Folder**, abra a pasta que contém `pom.xml`.
2. Aguarde o VS Code importar o Maven. Se solicitadas, instale
   `Extension Pack for Java` e `Spring Boot Extension Pack`.
3. Abra um terminal na pasta raiz e execute:

```powershell
$env:JAVA_HOME = "C:\caminho\para\jdk-25"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
java -version
mvn -version
mvn clean spring-boot:run
```

1. No navegador, abra <http://localhost:8080/login>.
2. Entre com os valores locais definidos em `APP_ADMIN_USER` e
   `APP_ADMIN_PASSWORD`; o repositório não contém uma senha padrão.

A senha é codificada com BCrypt na inicialização. Para trocar credenciais no
PowerShell, antes de rodar o Maven:

```powershell
$env:APP_ADMIN_USER="admin"
$env:APP_ADMIN_PASSWORD="SuaSenhaForteAqui"
$env:DB_PASSWORD="SuaSenhaDoBancoAqui"
mvn spring-boot:run
```

## 4. Painel principal

O dashboard exibe o total de produtos, clientes e vendas cadastrados, além das
cinco vendas mais recentes.

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

**Arquitetura MVC:** Browser -> Controller -> Service (regras de vendas) ->
Repository (Spring Data JPA) -> MySQL/MariaDB. Thymeleaf renderiza o HTML no
servidor.

## 6. Segurança aplicada

- Autenticação e autorização: `Spring Security`, todas as rotas de gestão
  exigem perfil ADMIN.
- Senha: `BCryptPasswordEncoder`, não registrada em texto puro no
  armazenamento da autenticação (usuário em memória de demonstração).
- CSRF: tokens em formulários POST, inclusive exclusão e logout; não foi
  desabilitado globalmente.
- Validação no servidor: `jakarta.validation` (`@NotBlank`, `@Email`, `@Min`,
  `@DecimalMin`).
- JPA: consultas tipadas/parametrizadas, reduzindo exposição a SQL Injection;
  template usa `th:text`, que escapa HTML.
- Integridade: transações `@Transactional`; validação de estoque com bloqueio
  pessimista; exclusão de produtos/clientes referenciados bloqueada.

**Limites desta versão didática:** contas administrativas são configuradas
em memória; não existe cadastro de usuários, 2FA, HTTPS, logs de auditoria,
perfis adicionais, pagamentos reais ou rotina fiscal.

## 7. Regras de negócio e escopo

- **Produto**: nome, SKU único, categoria, edição, Foil, preço e saldo de estoque.
- **Cliente**: nome, e-mail único, telefone.
- **Venda**: associa **um único produto** a um cliente e quantidade. `PENDENTE`
  ou `CONFIRMADA` reserva estoque. `CANCELADA` libera reserva. O preço de cada
  venda é gravado como retrato do preço do produto no momento de salvar/editar.
- **Dashboard**: somente `CONFIRMADA` entra na receita confirmada; produtos
  com até três unidades aparecem como estoque baixo.
- **Exclusão de venda**: reverte reserva se a venda estava pendente/confirmada.
  Produtos/clientes vinculados a vendas não podem ser excluídos.
- **Escopo acadêmico**: não contempla múltiplos itens por pedido, frete,
  imposto, cupom, parcelamento, NF-e, pagamento ou integração com o site de
  vendas da loja. Pode ser ampliado posteriormente.

Fontes técnicas: [Spring Boot](https://docs.spring.io/spring-boot/3.5/),
[Spring Security](https://docs.spring.io/spring-security/reference/),
[Thymeleaf](https://www.thymeleaf.org/documentation.html) e
[Apache Friends](https://www.apachefriends.org/faq_windows.html).
