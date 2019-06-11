![Spring Logo](https://2.bp.blogspot.com/-DvI0i_YgvtY/WxT8sljW-1I/AAAAAAAACNQ/O9PFQhwkPGY5ChFlWzEZa96V56PdhFtwQCLcBGAs/s1600/spring-boot-logo.png)


# REST COM SPRING BOOT #

## Introdução

Esse repositório foi criado com o objetivo de desenvolver um CRUD REST com SPRING BOOT de Clientes. Foram consumidos API's de Geolocalização e Clima na efetivação do cadastro dos clientes.

## Ferramentas Utilizadas:

- Spring Data JPA

   - O Spring Data tem por objetivo facilitar o trabalho com persistência de dados. O que economiza tempo com a configuração do JPA e como consequência aumenta a produtividade nas camadas de persistência, motivo este pelo qual foi escolhido.
  
- Spring Boot Devtools

   - Foi escolhido, pois, DevTools configura algumas propriedades com valores que são convenientes em tempo de desenvolvimento, monitora os classpath que serão reinicializados automaticamente a qualquer alteração neles. Contem também um servidor embarcado que envia um aviso para o navegador dizendo que os arquivos estáticos ou os templates foram alterados.
   
- Gson

   - É uma Biblioteca Java que tem como principal objetivo prover uma interface simples para ler e exportar no formato JSON. 
   - Foi escolhido o Gson porque com apenas poucas linhas você consegue converter Objetos Java em uma representação JSON e vice-versa.
   
- H2 Database
   - É um banco de dados open Source que funciona em memória com um console acessível pelo browser dentro do contexto da aplicação.
   - Foi escolhido por ser de configuração rápida e fácil, o que favorece a produtividade.


## Infraestrutura adcional:

Para efetuar as requisições Json foi utilizado o [Postman](https://www.getpostman.com/apps).


## Como Executar o projeto:

- Abra a classe **UolApplication**, localizada no caminho: _src/main/java/projeto_.
- Execute o código através do menu: run > run as > Java Application



## Como usar os serviços:

Com o Postman aberto execute os seguintes JSON's abaixo:

- *GET* 
   - `http://localhost:8080/clientes` - Lista todos os clientes com id, nome e idade de cada um.
   - `http://localhost:8080/clientes/id` - Retorna o id, nome e idade do cliente informado.
   - `http://localhost:8080/clientes/historico-cliente/id` - Retorna todas as informações do cliente informado, como nome, idade, localidade, temperatura mínima e máxima.
   - `http://localhost:8080/historicos` - Lista todos os históricos cadastrados.
 
- *DELETE* 
   - `http://localhost:8080/clientes/id` - Remove todos os registros do cliente informado nas tabelas histórico, localidade e cliente.
   
 - *PUT* 
   - Na aba Headers do Postman preencha os campos _KEY:_ _Content-Type_ e _VALUE:_ _application/json_.
   - Na aba Body do Postman selecione _raw_ e insira o JSON desejado no campo abaixo.
   
   Insira no campo solicitado a _URL_a seguir:
   
   - `http://localhost:8080/clientes/id` - Atualiza o nome e idade do cliente no registro informado.
   
 - *POST* 
   - Na aba Headers do Postman preencha os campos _KEY:_ _Content-Type_ e _VALUE:_ _application/json_.
   - Na aba Body do Postman selecione _raw_ e insira o JSON desejado no campo abaixo.
   
   Insira no campo solicitado a _URL_a seguir:
   
   - `http://localhost:8080/clientes` - Cadastra um novo cliente.
