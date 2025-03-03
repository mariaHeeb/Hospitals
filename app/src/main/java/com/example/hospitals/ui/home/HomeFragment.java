package com.example.hospitals.ui.home;

import android.content.Intent;
import android.net.Uri;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.hospitals.R;
import com.example.hospitals.db.UserDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import java.util.HashMap;

/**
 * HomeFragment provides a simple chat interface using ChatGPT's API.
 * It also saves chat messages to Firebase under "Users/{userId}/chats"
 * and supports clearing the chat history.
 */
public class HomeFragment extends Fragment {

    // UI components
    private Button sendButton, clearButton;
    private EditText userInput;
    private LinearLayout chatContainer;
    private ScrollView chatScrollView;

    // ChatGPT API endpoint and key (replace the API key with your actual one)
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final String API_KEY = "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";

    // User ID from local SQLite database (dummy value here; update with actual logic)
    private String userId;
    private Button hotlineButton,quickActionHeartAttack,quickActionBurn,quickActionChoking;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI elements from the layout
        sendButton = view.findViewById(R.id.sendButton);
        clearButton = view.findViewById(R.id.clearButton);
        userInput = view.findViewById(R.id.userInput);
        chatContainer = view.findViewById(R.id.chatContainer);
        chatScrollView = view.findViewById(R.id.chatScrollView);
        hotlineButton=view.findViewById(R.id.hotlineButton);

        quickActionHeartAttack=view.findViewById(R.id.quickActionHeartAttack);
        quickActionBurn=view.findViewById(R.id.quickActionBurn);
        quickActionChoking=view.findViewById(R.id.quickActionChoking);


        // Initialize and open the local SQLite database to fetch the user ID
        UserDatabase userDatabase = new UserDatabase(getContext());
        userDatabase.open();
        String[] lastLogin = userDatabase.getLastLogin();
        if (lastLogin != null && lastLogin.length > 0) {
            userId = lastLogin[0];
        } else {
            // Handle error or set a default userId if needed
            userId = "defaultUser";
        }
        hotlineButton.setOnClickListener(v -> {
            // The emergency number (e.g., "911" in some regions, or your local equivalent).
            String emergencyNumber = "911";

            // Create a dial intent with the emergency number.
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + emergencyNumber));

            // Start the dialer app. The user can confirm the call from there.
            startActivity(callIntent);
        });


        // Quick Action for Heart Attack
        quickActionHeartAttack.setOnClickListener(v -> {
            showEmergencyInstructions("Heart Attack Emergency",
                    "If you suspect a heart attack:\n\n" +
                            "1. Call emergency services immediately (e.g., 911).\n" +
                            "2. Have the person sit down and rest.\n" +
                            "3. If the person is conscious and not allergic, have them chew an aspirin.\n" +
                            "4. Monitor their condition until help arrives.");
        });

// Quick Action for Burn
        quickActionBurn.setOnClickListener(v -> {
            showEmergencyInstructions("Burn Emergency",
                    "For burns:\n\n" +
                            "1. Cool the burn under running cool water for at least 10 minutes.\n" +
                            "2. Remove any tight clothing or jewelry near the area.\n" +
                            "3. Cover the burn with a clean, non-fluffy cloth.\n" +
                            "4. Seek medical help if the burn is severe.");
        });

// Quick Action for Choking
        quickActionChoking.setOnClickListener(v -> {
            showEmergencyInstructions("Choking Emergency",
                    "If someone is choking:\n\n" +
                            "1. Call emergency services immediately (e.g., 911).\n" +
                            "2. Perform the Heimlich maneuver if trained.\n" +
                            "3. Encourage the person to cough if they can.\n" +
                            "4. Continue assistance until the obstruction is removed or help arrives.");
        });


        // Set up the send button click listener
        sendButton.setOnClickListener(v -> {
            String userMessage = userInput.getText().toString().trim();
            if (!userMessage.isEmpty()) {
                // Display and save user's message
                addUserMessage(userMessage);
                // Query ChatGPT for a response and display it
                fetchBotResponse(userMessage);
                // Clear input field after sending
                userInput.setText("");
            } else {
                Toast.makeText(getContext(), "Please enter a message", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up the clear button click listener
        clearButton.setOnClickListener(v -> {
            // Remove all messages from the UI
            chatContainer.removeAllViews();
            // Remove all saved chats from Firebase
            clearChatsFromFirebase();
        });

        // Load any previously saved chats from Firebase
        loadChatsFromFirebase();

        return view;
    }

    /**
     * Adds a user message: displays it in the chat and saves it to Firebase.
     */
    private void addUserMessage(String message) {
        displayUserMessage(message);
        saveChatMessage("User", message);
    }

    /**
     * Adds a bot message: displays it in the chat and saves it to Firebase.
     */
    private void addBotMessage(String message) {
        displayBotMessage(message);
        saveChatMessage("Bot", message);
    }
    private void showEmergencyInstructions(String title, String instructions) {
        // Inflate the custom dialog layout.
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_emergency_instructions, null);

        // Retrieve UI components from the inflated layout.
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogTitle);
        TextView tvInstructions = dialogView.findViewById(R.id.tvDialogInstructions);
        Button btnCallNow = dialogView.findViewById(R.id.btnCallNow);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);

        // Set the title and instructions text.
        tvTitle.setText(title);
        tvInstructions.setText(instructions);

        // Build and display the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        // Optionally set a transparent background if desired.
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        // Set up the "Call Now" button to launch the phone dialer.
        btnCallNow.setOnClickListener(v -> {
            String emergencyNumber = "911"; // Update with your local emergency number if needed.
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + emergencyNumber));
            startActivity(callIntent);
            dialog.dismiss();
        });

        // Set up the "Cancel" button to dismiss the dialog.
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }
    /**
     * Displays a user message in the chat container.
     */
    private void displayUserMessage(String message) {
        View userMessageView = getLayoutInflater().inflate(R.layout.user_message, chatContainer, false);
        TextView userMessageText = userMessageView.findViewById(R.id.messageText);
        userMessageText.setText(message);
        chatContainer.addView(userMessageView);
        scrollToBottom();
    }

    /**
     * Displays a bot message in the chat container.
     */
    private void displayBotMessage(String message) {
        View botMessageView = getLayoutInflater().inflate(R.layout.bot_message, chatContainer, false);
        TextView botMessageText = botMessageView.findViewById(R.id.messageText);
        botMessageText.setText(message);
        chatContainer.addView(botMessageView);
        scrollToBottom();
    }

    /**
     * Scrolls the chat container to the bottom.
     */
    private void scrollToBottom() {
        chatScrollView.post(() -> chatScrollView.fullScroll(View.FOCUS_DOWN));
    }

    /**
     * Sends the user's message to ChatGPT and handles the response.
     */
    private void fetchBotResponse(String userMessage) {
        OkHttpClient client = new OkHttpClient();

        // Construct JSON payload for ChatGPT
        JsonObject userMessageObject = new JsonObject();
        userMessageObject.addProperty("role", "user");
        // Append instructions for ChatGPT to format its response as a professional doctor's step-by-step answer
        userMessageObject.addProperty("content", userMessage +
                "(don't write it but make the response like a professional doctor and the answer step by step) , answer the same language of the question");

        JsonArray messagesArray = new JsonArray();
        messagesArray.add(userMessageObject);

        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("model", "gpt-3.5-turbo");
        requestBodyJson.add("messages", messagesArray);
        requestBodyJson.addProperty("temperature", 0.7);

        RequestBody body = RequestBody.create(requestBodyJson.toString(), MediaType.parse("application/json"));

        // Build the HTTP request
        Request request = new Request.Builder()
                .url(API_URL)
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        // Execute the request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // On failure, update the UI on the main thread
                requireActivity().runOnUiThread(() -> addBotMessage("Failed to fetch response. Please try again."));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    requireActivity().runOnUiThread(() -> addBotMessage(parseChatGPTResponse(responseBody)));
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "No error body";
                    requireActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), errorBody, Toast.LENGTH_LONG).show();
                        addBotMessage("Error: " + response.code() + " - " + errorBody);
                    });
                }
            }
        });
    }

    /**
     * Parses the JSON response from ChatGPT.
     *
     * @param responseBody The raw JSON response as a string.
     * @return The extracted message content.
     */
    private String parseChatGPTResponse(String responseBody) {
        try {
            JsonObject jsonObject = com.google.gson.JsonParser.parseString(responseBody).getAsJsonObject();
            JsonArray choicesArray = jsonObject.getAsJsonArray("choices");
            if (choicesArray != null && choicesArray.size() > 0) {
                JsonObject firstChoice = choicesArray.get(0).getAsJsonObject();
                JsonObject messageObject = firstChoice.getAsJsonObject("message");
                return messageObject.get("content").getAsString();
            } else {
                return "No response content available.";
            }
        } catch (Exception e) {
            Log.e("ChatGPTAPI", "Failed to parse response: " + responseBody, e);
            return "Error parsing response.";
        }
    }

    /**
     * Saves a chat message to Firebase under Users/{userId}/chats.
     *
     * @param sender  The sender ("User" or "Bot").
     * @param message The message content.
     */
    private void saveChatMessage(String sender, String message) {
        HashMap<String, Object> chatData = new HashMap<>();
        chatData.put("sender", sender);
        chatData.put("message", message);
        chatData.put("timestamp", System.currentTimeMillis());

        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("chats")
                .push()
                .setValue(chatData);
    }

    /**
     * Clears all chat messages from Firebase under Users/{userId}/chats.
     */
    private void clearChatsFromFirebase() {
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("chats")
                .removeValue();
    }

    /**
     * Loads previously saved chats from Firebase and displays them in the chat container.
     */
    private void loadChatsFromFirebase() {
        FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(userId)
                .child("chats")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Clear UI before loading new data.
                        chatContainer.removeAllViews();
                        for (DataSnapshot chatSnapshot : snapshot.getChildren()) {
                            String sender = chatSnapshot.child("sender").getValue(String.class);
                            String message = chatSnapshot.child("message").getValue(String.class);
                            if (sender != null && message != null) {
                                // Display messages based on sender type.
                                if (sender.equals("User")) {
                                    displayUserMessage(message);
                                } else {
                                    displayBotMessage(message);
                                }
                            }
                        }
                        scrollToBottom();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Error loading chats: " + error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
