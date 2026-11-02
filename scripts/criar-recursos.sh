#!/bin/bash
set -e

# Variáveis
RESOURCE_GROUP_NAME="rg-clyvo-devops"
LOCATION="${LOCATION:-chilecentral}"
WEBAPP_NAME="clyvo-elo-devops-rm563230"
APP_SERVICE_PLAN="clyvo-plan"
RUNTIME="JAVA:21-java21"
APP_INSIGHTS_NAME="ai-clyvo-elo-devops"
PG_SERVER_NAME="pg-clyvo-elo-rm563230"
PG_DB_NAME="clyvoelo"
PG_ADMIN_USER="clyvoadmin"

if [ -z "$PG_ADMIN_PASSWORD" ]; then
  read -s -p "Senha do administrador do PostgreSQL: " PG_ADMIN_PASSWORD
  echo
fi

# Provedores usados
az provider register --namespace Microsoft.Web
az provider register --namespace Microsoft.Insights
az provider register --namespace Microsoft.OperationalInsights
az provider register --namespace Microsoft.DBforPostgreSQL
az extension add --name application-insights --upgrade --only-show-errors

# Grupo de recursos
az group create \
  --name $RESOURCE_GROUP_NAME \
  --location "$LOCATION"

# Banco de dados PostgreSQL (PaaS)
az postgres flexible-server create \
  --resource-group $RESOURCE_GROUP_NAME \
  --name $PG_SERVER_NAME \
  --location "$LOCATION" \
  --tier Burstable \
  --sku-name Standard_B1ms \
  --storage-size 32 \
  --version 16 \
  --admin-user $PG_ADMIN_USER \
  --admin-password "$PG_ADMIN_PASSWORD" \
  --public-access Enabled \
  --yes

az postgres flexible-server db create \
  --resource-group $RESOURCE_GROUP_NAME \
  --server-name $PG_SERVER_NAME \
  --database-name $PG_DB_NAME

# Acesso público ao banco
az postgres flexible-server firewall-rule create \
  --resource-group $RESOURCE_GROUP_NAME \
  --name $PG_SERVER_NAME \
  --rule-name AllowAll \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 255.255.255.255

# Application Insights
az monitor app-insights component create \
  --app $APP_INSIGHTS_NAME \
  --location "$LOCATION" \
  --resource-group $RESOURCE_GROUP_NAME \
  --application-type web

# Plano do Serviço de Aplicativo
az appservice plan create \
  --name $APP_SERVICE_PLAN \
  --resource-group $RESOURCE_GROUP_NAME \
  --location "$LOCATION" \
  --sku B1 \
  --is-linux

# Serviço de Aplicativo (Web App)
az webapp create \
  --name $WEBAPP_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --plan $APP_SERVICE_PLAN \
  --runtime "$RUNTIME"

az resource update \
  --resource-group $RESOURCE_GROUP_NAME \
  --namespace Microsoft.Web \
  --resource-type basicPublishingCredentialsPolicies \
  --name scm \
  --parent sites/$WEBAPP_NAME \
  --set properties.allow=true

az webapp config set \
  --name $WEBAPP_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --always-on true \
  --startup-file "java -jar /home/site/wwwroot/app.jar"

az webapp update \
  --name $WEBAPP_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --https-only true

# Configurações do Web App
CONNECTION_STRING=$(az monitor app-insights component show \
  --app $APP_INSIGHTS_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --query connectionString \
  --output tsv)

az webapp config appsettings set \
  --name "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --output none \
  --settings \
    APPLICATIONINSIGHTS_CONNECTION_STRING="$CONNECTION_STRING" \
    ApplicationInsightsAgent_EXTENSION_VERSION="~3" \
    XDT_MicrosoftApplicationInsights_Mode="Recommended" \
    XDT_MicrosoftApplicationInsights_PreemptSdk="1" \
    SERVER_PORT="80" \
    SPRING_DATASOURCE_USERNAME="$PG_ADMIN_USER" \
    SPRING_DATASOURCE_PASSWORD="$PG_ADMIN_PASSWORD" \
    SPRING_DATASOURCE_URL="jdbc:postgresql://$PG_SERVER_NAME.postgres.database.azure.com:5432/$PG_DB_NAME?sslmode=require"

az webapp restart \
  --name $WEBAPP_NAME \
  --resource-group $RESOURCE_GROUP_NAME

az monitor app-insights component connect-webapp \
  --app $APP_INSIGHTS_NAME \
  --web-app $WEBAPP_NAME \
  --resource-group $RESOURCE_GROUP_NAME

# Resumo
echo
echo "Web App:         https://$WEBAPP_NAME.azurewebsites.net"
echo "Servidor do BD:  $PG_SERVER_NAME.postgres.database.azure.com"
echo "JDBC URL:        jdbc:postgresql://$PG_SERVER_NAME.postgres.database.azure.com:5432/$PG_DB_NAME?sslmode=require"
echo "Usuário do BD:   $PG_ADMIN_USER"
