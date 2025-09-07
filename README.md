My Journal App: Your AI-Powered Journal
My Journal App is a modern, feature-rich Android journaling application designed to transform daily reflection into a seamless and insightful experience. It leverages Generative AI to help you create rich, narrative-style journal entries from simple, guided questions, making it the perfect space to capture your thoughts, track your growth, and uncover meaningful insights about your life.
<br>

✨ Key Features

📝 AI-Powered Story Generation: Overcome writer's block. Simply answer a few customizable prompts, and our AI will weave your thoughts into a beautiful, cohesive journal entry.

🧠 AI-Generated Weekly Insights: Receive a compassionate summary of your week's entries. The AI analyzes your writing to identify recurring themes and emotions, helping you understand yourself better.

☁️ Secure Cloud Sync: Your journal is your private space. All entries are stored securely in your own private cloud database using Firebase and are accessible only to you.

🖼️ Rich Media Entries: Add context and color to your memories by uploading two photos with each journal entry, hosted securely on Cloudinary.

📅 Journal of the Day: The home screen dynamically features your entry for the current day or provides a beautiful prompt to encourage daily writing.

❤️ "On This Day" Memories: The Insights screen helps you rediscover entries from the same date in previous years, fostering long-term reflection.

🎨 Modern, Beautiful UI: A stunning, edge-to-edge interface built with Material 3, featuring delightful Lottie animations, custom dialogs, and a full-featured profile screen with a Light/Dark theme switcher.

🔐 Secure Authentication: Easy and secure sign-up and login with both Email/Password (with email verification) and Google Sign-In.

✏️ Full CRUD Functionality: Create, Read, Update, and Delete your journal entries with ease through an intuitive and interactive user interface.

<br>

🛠️ Tech Stack & Architecture
This project is a full-stack mobile application built with a modern, industry-standard technology stack.

Language: Kotlin

Core Architecture:

Single-Activity Architecture: Uses a single MainActivity to host all fragment destinations.

Android Jetpack: A suite of libraries to help developers follow best practices.

UI Layer (XML):

View Binding: Safely access views without findViewById.

Material 3 Components: Modern UI elements for a consistent and beautiful design.

Lottie: For high-quality, engaging animations.

Glide: For efficient loading and caching of images.

Navigation:

Jetpack Navigation Component: Manages all fragment transactions and deep linking.

Backend & Database:

Firebase Authentication: Handles secure user login and registration (Email/Password & Google Sign-In).

Firebase Firestore: A real-time, NoSQL cloud database for storing all journal data.

Firestore Security Rules: Implemented to ensure each user can only access their own data.

AI & Machine Learning:

Google Gemini AI: Integrated for generating journal stories and weekly insights.

Image Hosting:

Cloudinary: For robust cloud-based image storage and delivery.

<br>

🚀 Setup & Installation
To build and run this project yourself, follow these steps:

Clone the repository:

Bash

git clone [PASTE YOUR GITHUB REPO LINK HERE]
Firebase Setup:

Create a new project on the Firebase Console.

Enable Authentication (Email/Password and Google providers).

Enable Firestore Database.

From your project settings, download the google-services.json file and place it in the app/ directory of the project.

Google Sign-In SHA-1 Key:

Follow the instructions in the Firebase documentation to add your debug SHA-1 key to your Firebase project settings. This is required for Google Sign-In to work.

Cloudinary Setup:

Create a free account on Cloudinary.

After signing up, navigate to your account Dashboard.

You will find your Cloud Name, API Key, and API Secret in the "Account Details" section.

Copy these three values.

In the Android Studio project, open the MyJournalApp.kt file.

Replace the placeholder strings ("YOUR_CLOUD_NAME", "YOUR_API_KEY", "YOUR_API_SECRET") with the credentials you copied from your dashboard.

Google Cloud (Gemini API):

In the Google Cloud Console, ensure the same project is selected.

Navigate to the API Library and enable the "Generative Language API".

Ensure your project is linked to a billing account (a free tier is available).

Build and Run:

Open the project in Android Studio, let Gradle sync, and run the app on an emulator or a physical device.

<br>

🌟 Future Enhancements

Mood Tracking & Charting: Implement mood selection for each entry and visualize trends in the Insights screen.

Advanced Search: Add full-text search capabilities to find specific entries.

Custom Reminders: Allow users to set daily/weekly notifications to encourage consistent journaling.

Export to PDF: Provide an option for users to back up or share their journals as a PDF file.
