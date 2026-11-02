#!/bin/bash
set -e

# Variáveis
GRUPO="rg-clyvo-elo"
LOCAL="${LOCAL:-brazilsouth}"
PLANO="plan-clyvo-elo"
WEBAPP="clyvo-elo-rm563230"
SERVIDOR_BD="pg-clyvo-elo-rm563230"
BANCO="clyvoelo"
USUARIO_BD="clyvoadmin"

if [ -z "$SENHA_BD" ]; then
  read -s -p "Senha do administrador do PostgreSQL: " SENHA_BD
  echo
fi

# Grupo de recursos
az group create \
  --name "$GRUPO" \
  --location "$LOCAL"

# Banco de dados PostgreSQL (PaaS)
az postgres flexible-server create \
  --resource-group "$GRUPO" \
  --name "$SERVIDOR_BD" \
  --location "$LOCAL" \
  --tier Burstable \
  --sku-name Standard_B1ms \
  --storage-size 32 \
  --version 16 \
  --admin-user "$USUARIO_BD" \
  --admin-password "$SENHA_BD" \
  --public-access Enabled \
  --yes

az postgres flexible-server db create \
  --resource-group "$GRUPO" \
  --server-name "$SERVIDOR_BD" \
  --database-name "$BANCO"

# Acesso público ao banco
az postgres flexible-server firewall-rule create \
  --resource-group "$GRUPO" \
  --name "$SERVIDOR_BD" \
  --rule-name acesso-publico \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 255.255.255.255

# Plano do Serviço de Aplicativo
az appservice plan create \
  --resource-group "$GRUPO" \
  --name "$PLANO" \
  --location "$LOCAL" \
  --is-linux \
  --sku B1

# Serviço de Aplicativo (Web App Java 21)
az webapp create \
  --resource-group "$GRUPO" \
  --plan "$PLANO" \
  --name "$WEBAPP" \
  --runtime "JAVA:21-java21"

# Configurações adicionais
az webapp config set \
  --resource-group "$GRUPO" \
  --name "$WEBAPP" \
  --always-on true \
  --startup-file "java -jar /home/site/wwwroot/app.jar"

az webapp update \
  --resource-group "$GRUPO" \
  --name "$WEBAPP" \
  --https-only true

az webapp log config \
  --resource-group "$GRUPO" \
  --name "$WEBAPP" \
  --application-logging filesystem \
  --docker-container-logging filesystem \
  --level information

az webapp config appsettings set \
  --resource-group "$GRUPO" \
  --name "$WEBAPP" \
  --settings SERVER_PORT=80 \
  --output none

# Resumo
echo
echo "Web App:     https://$WEBAPP.azurewebsites.net"
echo "Servidor BD: $SERVIDOR_BD.postgres.database.azure.com"
echo "JDBC URL:    jdbc:postgresql://$SERVIDOR_BD.postgres.database.azure.com:5432/$BANCO?sslmode=require"
echo "Usuário BD:  $USUARIO_BD"
