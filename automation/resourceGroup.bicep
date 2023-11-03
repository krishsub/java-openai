targetScope = 'subscription'

param resourceGroupName string
param location string

resource newRG 'Microsoft.Resources/resourceGroups@2023-07-01' = {
  name: resourceGroupName
  location: location
}
