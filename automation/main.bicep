targetScope = 'subscription'

param location string = 'westeurope'
param resourceGroupName string = 'Scrap-Resources'

// create or use an existing resource group
module rg 'resourceGroup.bicep' = {
  name: resourceGroupName
  scope: subscription()
  params: {
    location: location
    resourceGroupName: resourceGroupName
  }
}

// create a web app
module web 'webapp.bicep' = {
  name: 'web'
  scope: resourceGroup(rg.name)
  params: {
    location: location
  }
}

// create OpenAI resource and model deployment
module oai 'openai.bicep' = {
  name: 'openai'
  scope: resourceGroup(rg.name)
  params: {
    location: location
  }
}

// assign the web app identity to the OpenAI resource
module roleAssignment 'roleassignment.bicep' = {
  name: 'openaiRoleAssignment'
  scope: resourceGroup(rg.name)
  params: {
    webappName: web.outputs.siteName
    principalId: web.outputs.principalId
    openAiName: oai.outputs.openaiEndpoint
  }
}
