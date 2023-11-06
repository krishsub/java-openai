param openAiName string
param webappName string
param principalId string

resource roleAssignment 'Microsoft.Authorization/roleAssignments@2022-04-01' = {
  name: guid(subscription().id, principalId, openAiContributor.id)
  scope: oai // apply to the OpenAI resource only
  properties: {
    principalId: webapp.identity.principalId
    roleDefinitionId: openAiContributor.id
  }
}

resource oai 'Microsoft.CognitiveServices/accounts@2023-10-01-preview' existing = {
  name: openAiName
}

resource webapp 'Microsoft.Web/sites@2022-09-01' existing = {
  name: webappName
}

// https://www.azadvertizer.net/azrolesadvertizer/a001fd3d-188f-4b5d-821b-7da978bf7442.html
resource openAiContributor 'Microsoft.Authorization/roleDefinitions@2022-05-01-preview' existing = {
  name: 'a001fd3d-188f-4b5d-821b-7da978bf7442'
}
