# ![Hospital Management App](Screen_Recording_20250304_012048_hospitals-ezgif.com-optimize.gif)

# Hospital Management App

## Overview

The Hospital Management App is an Android application designed to streamline medical appointments, provide health guidance, and offer emergency assistance. Users can book and manage appointments, receive medical advice from a chatbot, and access quick emergency actions.

## Features

- **User Authentication:** Secure sign-up and login using Firebase Authentication.
- **Book Appointments:** Users can schedule appointments based on available dates and hospital departments.
- **Chatbot Assistance:** AI-powered chatbot for medical guidance.
- **Emergency Hotlines:** Quick dial for emergency medical services.
- **Medical Records:** Users can store and manage medical history.
- **Hospital Listings:** View hospitals, departments, and available doctors.
- **Firebase Integration:** Stores user data, appointments, and chat history.

## User Roles & Permissions

- **Patient:** Can book appointments, access medical records, and chat with the AI chatbot.
- **Doctor:** Can view scheduled appointments, update availability, and access patient medical history.
- **Admin:** Manages hospital listings, doctors, and system-wide settings.

## Security Features

- **Two-Factor Authentication (2FA):** Extra security for user logins.
- **Data Encryption:** Secure storage of medical records.
- **Role-Based Access Control (RBAC):** Ensures limited access based on user roles.

## Installation Guide

### Prerequisites

- Android Studio (latest version)
- Java Development Kit (JDK)
- Firebase account
- Google Play Services

### Steps to Install

#### Clone the Repository
```sh
git clone https://github.com/yourusername/hospital-management-app.git
```

#### Open in Android Studio
1. Open Android Studio.
2. Click on **Open an existing project**.
3. Select the cloned repository folder.

#### Set Up Firebase
1. Create a Firebase project.
2. Download `google-services.json` and place it inside `app/` directory.
3. Enable Authentication, Realtime Database, and Firestore in Firebase Console.

#### Build and Run
1. Sync Gradle files.
2. Run the project on an emulator or real device.

## Usage

### User Registration & Login
- Sign up using ID, email, and password.
- Login securely to access features.

### Booking Appointments
- Select hospital and department.
- Choose available dates and times.
- Confirm and manage your appointments.

### Chatbot Assistance
- Ask health-related questions.
- Get AI-powered responses for medical guidance.

### Emergency Hotline
- Quick access to emergency numbers.
- Tap **Call Now** to dial emergency services.

## Future Enhancements

- **Telemedicine Integration:** Video consultations with doctors.
- **E-Prescriptions:** Digital prescription generation.
- **Insurance & Billing:** Integration with medical insurance providers.

## Technologies Used

- **Android SDK:** Core framework.
- **Java & XML:** App development.
- **Firebase:** Authentication & Database.
- **Google APIs:** Location & Maps.
- **ChatGPT API:** AI chatbot integration.

## Contributing

1. Fork the repository.
2. Create a feature branch:
   ```sh
   git checkout -b feature-branch
   ```
3. Commit changes:
   ```sh
   git commit -m "Added new feature"
   ```
4. Push to your branch:
   ```sh
   git push origin feature-branch
   ```
5. Submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Collaborations

This project was developed with contributions from:
- **Yazan Dawud**
- **Maria Elheeb**

## Contact

For any inquiries or contributions, contact us at:
📧 **Email:** dahood.yazan8@@gmail.com
