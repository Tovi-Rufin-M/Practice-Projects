import google.generativeai as genai

genai.configure(api_key="AIzaSyCAs34hbVfGZ8SOicqBpxt2yNaMqUmwrjQ")
model = genai.GenerativeModel('gemini-2.0-flash')

# Start a chat session with history memory
chat = model.start_chat(history=[])

while True:
    user_input = input("You: ")
    if user_input.lower() in ['exit', 'quit']:
        print("Exiting chat. Goodbye!")
        break
    response = chat.send_message(user_input)
    print(f"Bot: {response.text}")