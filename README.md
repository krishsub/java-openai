# Azure OpenAI Tester

This project can be used as a starting point to validate how to deploy
applications on Azure Web Apps (e.g. App Service or any another service) 
and have them communicate with an Azure OpenAI endpoint via standard 
authentication and authorization mechanisms provided by Azure's OpenAI 
service.

The project consists of two parts:
1. The first part is Infrastructure as Code (IaC) that deploys an App Service
with managed identity and an Azure OpenAI service with a GPT-35-Turbo model.
It also adds the App Service's managed identity to the 
`Cognitive Services OpenAI Contributor` role. This enables the application 
running on App Service to invoke the OpenAI service's APIs without an API key.
2. A Java web application that can leverage either API keys or managed
identity to invoke the OpenAI service. If running the code locally from your
developer workstation (or VM in Azure), you can use your Azure credentials 
instead of an API key. You will need to ensure that your Azure credentials
are given the same `Cognitive Services OpenAI Contributor` role and you are 
logged in via the `az login` command.

## Running the application

The `main.bicep` file under the `automation` folder contains the top-level
functionality to deploy and configure all services in Azure. You will need
owner permissions on the subscription and/or resource group in order to
deploy the resources and assign users to roles (in this case, the App Service's
managed identity to the `Cognitive Services OpenAI Contributor` role)

The project is a standard Maven project. To run it from the command line,
type `mvnw` (Windows), or `./mvnw` (Mac & Linux), then open
http://localhost:8080 in your browser. The same project / jar file can be
deployed to Azure App Service (see notes below as it requires the 
`production` flag to Maven's `package` command).

## Deploying to Production or Azure App Service

To create a production build, call `mvnw clean package -Pproduction` (Windows),
or `./mvnw clean package -Pproduction` (Mac & Linux).
This will build a JAR file with all the dependencies and front-end resources,
ready to be deployed. The file can be found in the `target` folder after the 
build completes.

Once the JAR file is built, you can run it using
`java -jar target/azure-openai-example-1.0-SNAPSHOT.jar`

You can also deploy this jar file to Azure App Service. It does require
that you set a `PORT` application setting to `80` under the App Service's
`Configuration` tab. Note that the `PORT` application setting value in Azure's
App Service is `80` and not `8080`. 

## Project structure

- `main.bicep` in `automation` contains the top-level Infrastructure As Code
deployment.
- `MainLayout.java` in `src/main/java` contains the code to use the Azure 
OpenAI service via its APIs.
- The code is meant for testing - as it instantiates a new client connection
on each call to the Open AI endpoint which is quite expensive. 
- The code doesn't maintain any chat conversation history or context.

## Useful links

- Read [Quickstart: Get started generating text using Azure OpenAI Service](https://learn.microsoft.com/en-us/azure/ai-services/openai/quickstart?tabs=command-line&pivots=programming-language-java).
- Read [Role-based access control for Azure OpenAI Service](https://learn.microsoft.com/en-us/azure/ai-services/openai/how-to/role-based-access-control).
- Automation [Microsoft.CognitiveServices accounts](https://learn.microsoft.com/en-us/azure/templates/microsoft.cognitiveservices/accounts?pivots=deployment-language-bicep).
- Read [Authenticate Azure-hosted Java applications](https://learn.microsoft.com/en-us/azure/developer/java/sdk/identity-azure-hosted-auth)
