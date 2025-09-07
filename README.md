________________________________________
📔✨ My Journal App: Your AI-Powered Journal
________________________________________
Transform your daily thoughts into beautiful, insightful narratives.
Struggle with writer's block or finding the time to journal? My Journal App is a modern Android application that redefines daily reflection. It uses the power of Generative AI to turn your simple, guided answers into rich, narrative-style entries. It's your personal, secure space to capture thoughts, track growth, and uncover meaningful insights about your life, effortlessly.
<br>
✨ Key Features
•	📝 AI-Powered Story Generation: Overcome writer's block. Simply answer a few customizable prompts, and our AI weaves your thoughts into a beautiful, cohesive journal entry.
•	🧠 AI-Generated Weekly Insights: Receive a compassionate summary of your week. The AI analyzes your entries to identify recurring themes and emotions, helping you understand yourself better.
•	☁️ Secure Cloud Sync: Your journal, your privacy. All entries are stored in your own private Firebase cloud database and are accessible only to you.
•	🖼️ Rich Media Entries: Add context and color to your memories by uploading two photos with each entry, hosted securely on Cloudinary.
•	📅 Journal of the Day: The home screen dynamically features your entry for the current day or provides a beautiful prompt to inspire daily writing.
•	❤️ "On This Day" Memories: The Insights screen helps you rediscover entries from the same date in previous years, fostering long-term reflection and gratitude.
•	🎨 Modern & Beautiful UI: A stunning, edge-to-edge interface built with Material 3, featuring delightful Lottie animations, custom dialogs, and a full-featured profile screen with a Light/Dark theme switcher.
•	🔐 Secure Authentication: Easy and secure sign-up and login with both Email/Password (with email verification) and Google Sign-In.
•	✏️ Full CRUD Functionality: Create, Read, Update, and Delete your journal entries with ease through an intuitive and interactive user interface.
<br>
🛠️ Tech Stack & Architecture
This project is built with a modern, scalable, and industry-standard technology stack.
•	💻 Language: Kotlin
•	🏗️ Core Architecture:
o	Single-Activity Architecture: Manages the UI within a single, focused MainActivity.
o	Android Jetpack: A suite of libraries for robust, high-quality app development.
•	🎨 UI Layer (XML):
o	Material 3: For modern, beautiful, and consistent UI components.
o	View Binding: For null-safe and type-safe interaction with views.
o	Lottie: For high-quality, engaging vector animations.
o	Glide: For efficient and smooth image loading and caching.
•	🧭 Navigation:
o	Jetpack Navigation Component: To manage all fragment transactions and deep linking seamlessly.
•	☁️ Backend, Database & Storage:
o	Firebase Authentication: For secure user login (Email/Password & Google Sign-In).
o	Firebase Firestore: A real-time, NoSQL cloud database for all journal data.
o	Firestore Security Rules: To ensure each user can only access their own data.
o	Cloudinary: For robust, cloud-based image hosting and delivery.
•	🤖 AI & Machine Learning:
o	Google Gemini AI: The engine behind the AI-powered story generation and weekly insights.
<br>
🚀 Setup & Installation
To get this project up and running on your local machine, follow these steps:
1.	Clone the Repository
Bash
git clone [PASTE YOUR GITHUB REPO LINK HERE]
2.	Firebase Setup
o	Create a new project on the Firebase Console.
o	In the dashboard, enable Authentication (with Email/Password and Google providers).
o	Enable the Firestore Database.
o	From your project settings, download the google-services.json file and place it in the app/ directory of the project.
3.	Google Sign-In SHA-1 Key
o	Follow the official Firebase documentation to add your debug SHA-1 key to your Firebase project settings. This is required for Google Sign-In to work.
4.	Cloudinary Setup
o	Create a free account on Cloudinary.
o	Navigate to your account Dashboard to find your Cloud Name, API Key, and API Secret.
o	In the Android Studio project, open the MyJournalApp.kt file.
o	Replace the placeholder strings ("YOUR_CLOUD_NAME", etc.) with your actual credentials.
5.	Google Cloud (Gemini API)
o	In the Google Cloud Console, select the same project you used for Firebase.
o	Navigate to the API Library and enable the "Generative Language API".
o	Ensure your project is linked to a billing account (a free tier is available).
6.	Build and Run
o	Open the project in Android Studio, allow Gradle to sync, and run the app on an emulator or a physical device. Enjoy!
<br>
🌟 Roadmap: Future Enhancements
This project has a bright future! Here are some features planned for upcoming releases:
•	📊 Mood Tracking & Charting: Implement mood selection for each entry and visualize trends in the Insights screen.
•	🔍 Advanced Search: Add full-text search capabilities to find specific entries or memories instantly.
•	🔔 Custom Reminders: Allow users to set daily or weekly notifications to encourage a consistent journaling habit.
•	📄 Export to PDF: Provide an option for users to back up or share their journals as a beautifully formatted PDF file.

