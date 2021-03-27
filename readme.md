wit-challenge
=========
# Como correr
## 1 - Inicie o rabbitmq
Aceda ao directorio .rabbitmq e execute o comando `docker-compose up`

## 2 - Crie as queues no rabbitmq
Aceda a http://localhost:15672/ faça login com username: guest e password: guest.
Depois crie as queues `InCalcQueue` e `OutCalcQueue`

## 3 - Inicie a aplicação
Se tiver aberto o projecto com o IDE execute o método main da classe mz.co.witchallenge.app.WitChallengeApplication

# Operações suportadas
## Soma
```shell script
curl --request GET \
  --url 'http://localhost:8080/sum?a=2.1&b=9'
```
## Subtração
```shell script
curl --request GET \
  --url 'http://localhost:8080/subtract?a=2.1&b=9'
```
## Multiplicação
```shell script
curl --request GET \
  --url 'http://localhost:8080/multiply?a=2.1&b=9'
```
## Divisão
```shell script
curl --request GET \
  --url 'http://localhost:8080/divide?a=2.1&b=9'
```

# Problemas conhecidos
* O logback-access não está a logar o corpo da resposta do request