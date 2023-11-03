param location string = resourceGroup().location
param openaiName string = uniqueString(resourceGroup().id)
param skuName string = 'S0'
param modelName string = 'gpt-35-turbo'
param modelDeploymentName string = '${modelName}-modelDeployment'
param modelCapacityUnits int = 30

var openAiString = 'OpenAI'

resource oai 'Microsoft.CognitiveServices/accounts@2023-10-01-preview' = {
  name: openaiName
  location: location
  sku: {
    name: skuName
  }
  kind: openAiString
  properties: {
    customSubDomainName: openaiName
    networkAcls: {
      defaultAction: 'Allow'
    }
    publicNetworkAccess: 'Enabled'
  }
  resource modelDeployment 'deployments' = {
    name: modelDeploymentName
    properties: {
      model: {
        name: modelName
        format: openAiString
        version: '0301'
      }
      versionUpgradeOption: 'OnceNewDefaultVersionAvailable'
    }
    sku: {
      name: 'Standard'
      capacity: modelCapacityUnits
    }
  }
}

output openaiEndpoint string = oai.name
