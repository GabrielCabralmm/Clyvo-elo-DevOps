RESOURCE_GROUP_NAME="rg-clyvo-devops"
LOCATION="chilecentral"
WEBAPP_NAME="clyvo-elo-devops"
APP_SERVICE_PLAN="clyvo-plan"
RUNTIME="JAVA:21-java21"
APP_INSIGHTS_NAME="ai-clyvo-elo-devops"
PG_SERVER_NAME="pgserver-clyvo-elo"
PG_DB_NAME="clyvoelodb"
PG_ADMIN_USER="clyvoadmin"
PG_ADMIN_PASSWORD="<sua-senha>"

# Grupo de recursos
az group create --name $RESOURCE_GROUP_NAME --location "$LOCATION"

# Banco de dados PostgreSQL
az postgres flexible-server create \
  --name $PG_SERVER_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --location "$LOCATION" \
  --admin-user $PG_ADMIN_USER \
  --admin-password $PG_ADMIN_PASSWORD \
  --tier Burstable \
  --sku-name Standard_B1ms \
  --storage-size 32 \
  --version 16 \
  --public-access Enabled \
  --yes

az postgres flexible-server db create \
  --resource-group $RESOURCE_GROUP_NAME \
  --server-name $PG_SERVER_NAME \
  --database-name $PG_DB_NAME

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
  --sku F1 \
  --is-linux

# Serviço de Aplicativo
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

# Configurações do Web App
CONNECTION_STRING=$(az monitor app-insights component show \
  --app $APP_INSIGHTS_NAME \
  --resource-group $RESOURCE_GROUP_NAME \
  --query connectionString \
  --output tsv)

az webapp config appsettings set \
  --name "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --settings \
    APPLICATIONINSIGHTS_CONNECTION_STRING="$CONNECTION_STRING" \
    ApplicationInsightsAgent_EXTENSION_VERSION="~3" \
    XDT_MicrosoftApplicationInsights_Mode="Recommended" \
    XDT_MicrosoftApplicationInsights_PreemptSdk="1" \
    SERVER_PORT="80" \
    SPRING_DATASOURCE_USERNAME="$PG_ADMIN_USER" \
    SPRING_DATASOURCE_PASSWORD="$PG_ADMIN_PASSWORD" \
    SPRING_DATASOURCE_URL="jdbc:postgresql://$PG_SERVER_NAME.postgres.database.azure.com:5432/$PG_DB_NAME?sslmode=require"

az webapp restart --name $WEBAPP_NAME --resource-group $RESOURCE_GROUP_NAME

az monitor app-insights component connect-webapp \
  --app $APP_INSIGHTS_NAME \
  --web-app $WEBAPP_NAME \
  --resource-group $RESOURCE_GROUP_NAME
