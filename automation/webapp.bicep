param location string = resourceGroup().location
param webappName string = uniqueString(resourceGroup().id)
param planName string = '${webappName}-plan'
param skuName string = 'S1'
param osType string = 'linux'
param runtimeStack string = 'JAVA|17-java17'

resource plan 'Microsoft.Web/serverfarms@2022-09-01' = {
  name: planName
  location: location
  sku: {
    name: skuName
  }
  kind: osType
  properties: {
    reserved: true
  }
}

resource app 'Microsoft.Web/sites@2022-09-01' = {
  name: webappName
  location: location
  properties: {
    serverFarmId: plan.id
    siteConfig: {
      linuxFxVersion: runtimeStack
      alwaysOn: true
    }
  }
  identity: {
    type: 'SystemAssigned'
  }
}

output siteName string = app.name
output principalId string = app.identity.principalId
