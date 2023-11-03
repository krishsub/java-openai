package com.example.application.views.main;

import java.util.ArrayList;
import java.util.List;

import com.azure.ai.openai.OpenAIClient;
import com.azure.ai.openai.OpenAIClientBuilder;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatMessage;
import com.azure.ai.openai.models.ChatRole;
import com.azure.core.credential.KeyCredential;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Azure OpenAI Tester")
@Route(value = "")
public class MainView extends VerticalLayout {
    // UI components
    private TextField openaiEndpointTextField;
    private TextField modeldeploymentNameTextField;
    private PasswordField apiKeyTextField;
    private Checkbox usedefaultCredentialCheckbox;
    private TextArea chatSystemMessageTextArea;
    private TextArea chatPromptTextArea;
    private Button chatButton;
    private TextArea chatResponseTextArea;

    public MainView() {
        openaiEndpointTextField = new TextField("Azure OpenAI endpoint");
        modeldeploymentNameTextField = new TextField("Model deployment name");
        apiKeyTextField = new PasswordField("API key");
        usedefaultCredentialCheckbox = new Checkbox(
                "Use default credential (e.g. managed identity) instead of API key");
        chatPromptTextArea = new TextArea("Chat prompt");
        chatSystemMessageTextArea = new TextArea("Chat system message");
        chatButton = new Button("Send");
        chatResponseTextArea = new TextArea("Chat response");

        // if using managed identity, disable the API key text field
        usedefaultCredentialCheckbox.addClickListener(e -> {
            if (usedefaultCredentialCheckbox.getValue()) {
                apiKeyTextField.setEnabled(false);
            } else {
                apiKeyTextField.setEnabled(true);
            }
        });

        openaiEndpointTextField.setWidth(50, Unit.PERCENTAGE);
        modeldeploymentNameTextField.setWidth(25, Unit.PERCENTAGE);
        apiKeyTextField.setWidth(50, Unit.PERCENTAGE);

        HorizontalLayout endpointsLayout = new HorizontalLayout();
        endpointsLayout.setWidth(100, Unit.PERCENTAGE);
        endpointsLayout.add(openaiEndpointTextField, modeldeploymentNameTextField);

        this.add(endpointsLayout,
                apiKeyTextField,
                usedefaultCredentialCheckbox);

        chatSystemMessageTextArea.setMinHeight("75px");
        chatSystemMessageTextArea.setMaxHeight("75px");
        chatSystemMessageTextArea.setValue("You are an AI assistant that helps people find information.");

        chatPromptTextArea.setMinHeight("150px");
        chatPromptTextArea.setMaxHeight("150px");
        Icon buttonIcon = VaadinIcon.PAPERPLANE.create();
        chatButton.setIcon(buttonIcon);
        chatButton.addClickListener(e -> {
            chat(
                    chatPromptTextArea.getValue(),
                    chatSystemMessageTextArea.getValue(),
                    usedefaultCredentialCheckbox.getValue(),
                    openaiEndpointTextField.getValue(),
                    modeldeploymentNameTextField.getValue(),
                    apiKeyTextField.getValue());
        });

        chatResponseTextArea.setMinHeight("200px");
        chatResponseTextArea.setMaxHeight("200px");
        chatResponseTextArea.setReadOnly(true);

        chatSystemMessageTextArea.setWidth(75, Unit.PERCENTAGE);
        chatPromptTextArea.setWidth(75, Unit.PERCENTAGE);
        chatResponseTextArea.setWidth(75, Unit.PERCENTAGE);

        this.add(chatSystemMessageTextArea, chatPromptTextArea, chatButton, chatResponseTextArea);
    }

    private void chat(
            String prompt,
            String instruction,
            boolean usedefaultCredential,
            String endpoint,
            String modeldeploymentName,
            String apiKey) {
        OpenAIClient openAIClient = null;
        try {
            if (usedefaultCredential) { // if using managed identity, use DefaultAzureCredential

                openAIClient = new OpenAIClientBuilder()
                        .endpoint(endpoint)
                        .credential(new DefaultAzureCredentialBuilder().build())
                        .buildClient();
            } else { // else use API key
                openAIClient = new OpenAIClientBuilder()
                        .endpoint(endpoint)
                        .credential(new KeyCredential(apiKey))
                        .buildClient();
            }

            List<ChatMessage> chatMessages = new ArrayList<>();
            chatMessages.add(new ChatMessage(ChatRole.SYSTEM, instruction));
            chatMessages.add(new ChatMessage(ChatRole.USER, prompt));

            ChatCompletions chatCompletions = openAIClient.getChatCompletions(
                    modeldeploymentName,
                    new ChatCompletionsOptions(chatMessages));

            String response = "";
            for (ChatChoice choice : chatCompletions.getChoices()) {
                ChatMessage message = choice.getMessage();
                response += message.getContent() + System.lineSeparator();
            }

            chatResponseTextArea.setValue(response);
        } catch (Exception e) {
            Notification.show("Error: " + e.getMessage());
            return;
        }
    }
}
