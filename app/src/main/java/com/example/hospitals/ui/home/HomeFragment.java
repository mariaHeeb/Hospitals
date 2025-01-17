package com.example.hospitals.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.hospitals.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;

public class HomeFragment extends Fragment {

    private Button sendButton;
    private EditText userInput;
    private LinearLayout chatContainer;
    private ScrollView chatScrollView;

    // Correct API endpoint (replace with the actual endpoint from Groq documentation)
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions"; // Groq's OpenAI-compatible chat endpoint
    // API Key
    private static final String API_KEY = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI components
        sendButton = view.findViewById(R.id.sendButton);
        userInput = view.findViewById(R.id.userInput);
        chatContainer = view.findViewById(R.id.chatContainer);
        chatScrollView = view.findViewById(R.id.chatScrollView);

        sendButton.setOnClickListener(v -> {
            String userMessage = userInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                addUserMessage(userMessage);
                fetchBotResponse(userMessage);
                userInput.setText("");
            }
        });

        return view;
    }

    private void addUserMessage(String message) {
        View userMessageView = getLayoutInflater().inflate(R.layout.user_message, chatContainer, false);
        TextView userMessageText = userMessageView.findViewById(R.id.messageText);
        userMessageText.setText(message);

        chatContainer.addView(userMessageView);
        scrollToBottom();
    }

    private void addBotMessage(String message) {
        View botMessageView = getLayoutInflater().inflate(R.layout.bot_message, chatContainer, false);
        TextView botMessageText = botMessageView.findViewById(R.id.messageText);
        botMessageText.setText(message);
        chatContainer.addView(botMessageView);
        scrollToBottom();
    }

    private void scrollToBottom() {
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    private void fetchBotResponse(String userMessage) {
        OkHttpClient client = new OkHttpClient();

        // Build the JSON payload
        JsonObject userMessageObject = new JsonObject();
        userMessageObject.addProperty("role", "user");
        userMessageObject.addProperty("content", userMessage+" Here’s the updated sentence:  \n" +
                "\n" +
                "Always respond like a professional doctor, ensuring a more polished and precise approach, and make sure that your response directs the patient to the needed action. Additionally, ensure your response is summarized and divided into points that a patient in an emergency situation could use to save their life.");

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(userMessageObject);

        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("model", "llama-3.1-70b-versatile"); // Replace with your preferred model
        requestBodyJson.add("messages", messagesArray); // Add the array to the payload
        requestBodyJson.addProperty("temperature", 0.7); // Optional, controls randomness

        RequestBody body = RequestBody.create(
                requestBodyJson.toString(),
                MediaType.parse("application/json")
        );

        // Build the request
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                requireActivity().runOnUiThread(() -> addBotMessage("Failed to fetch response. Please try again."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    requireActivity().runOnUiThread(() -> addBotMessage(parseGroqResponse(responseBody)));
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No error body";

                    // Move Toast to the main thread
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), errorBody, Toast.LENGTH_LONG).show();
                        addBotMessage("Error: " + response.code() + " - " + errorBody);
                    });
                }
            }
        });
    }

    private String parseGroqResponse(String responseBody) {
        try {
            JsonObject jsonObject = com.google.gson.JsonParser.parseString(responseBody).getAsJsonObject();

            // Navigate to "choices" -> first element -> "message" -> "content"
            JsonArray choicesArray = jsonObject.getAsJsonArray("choices");
            if (choicesArray != null && choicesArray.size() > 0) {
                JsonObject firstChoice = choicesArray.get(0).getAsJsonObject();
                JsonObject messageObject = firstChoice.getAsJsonObject("message");
                return messageObject.get("content").getAsString();
            } else {
                return "No response content available.";
            }
        } catch (Exception e) {
            Log.e("GroqAPI", "Failed to parse response: " + responseBody, e);
            return "Error parsing response.";
        }
    }

}
