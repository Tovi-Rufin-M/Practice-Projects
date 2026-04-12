from google import genai
import os

# Replace with your actual key or set it as an environment variable
client = genai.Client(api_key="AIzaSyDkKwr3oxg4GcYCftJkTgrmkYAZHJ_XVVU")

def chat():
    # 'gemini-2.0-flash' is fast and free within the rate limits
    chat = client.chats.create(model="gemini-2.0-flash")
    
    print("--- Free Gemini Bot (Type 'exit' to stop) ---")
    while True:
        user_msg = input("You: ")
        if user_msg.lower() in ['exit', 'quit']: break
        
        response = chat.send_message(user_msg)
        print(f"Bot: {response.text}\n")

if __name__ == "__main__":
    chat()