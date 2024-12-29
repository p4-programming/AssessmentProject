# My Android Project Assessment

This project is an Android application that implements a user authentication system with login, signup, and forgot password functionality. It also includes offline data storage using Room Database, and follows the MVVM architecture pattern and Dagger Hilt for dependency injection.

## Features

- **Login:** Secure login functionality with user credentials.
- **Signup:** User registration with validation.
- **Forgot Password:** Allows users to reset their password.
- **Offline Storage:** Integrated Room Database for local data storage.
- **MVVM Architecture:** Ensures separation of concerns and a clean code structure.
- **Dagger Hilt:** Dependency Injection framework for efficient and clean management of app dependencies.

## Tech Stack

- **Programming Language:** Java
- **Architecture:** MVVM (Model-View-ViewModel)
- **Dependency Injection:** Dagger Hilt
- **Database:** Room Database
- **UI:** XML Layouts, RecyclerView, and various UI components

## Installation

1. Clone the repository:
    ```bash
    git clone https://github.com/p4-programming/AssessmentAD.git
    ```

2. Open the project in Android Studio.

3. Make sure to have the necessary dependencies in your `build.gradle` files, including:

    - Room Database
    - Dagger Hilt
    - LiveData and ViewModel
    - Retrofit (if needed for networking)

4. Sync the project with Gradle.

5. Run the app on an emulator or physical device.

## How to Use

1. **Login:**
    - Enter your credentials (username/email and password).
    - On successful login, you’ll be directed to the home screen.
    - If the credentials are incorrect, an error message will be shown.

2. **Signup:**
    - Enter the required details (username/email, password).
    - After successful registration, you can log in using your new credentials.

3. **Forgot Password:**
    - Enter your email address to receive a password reset link.

## Offline Functionality

- The app stores user data locally in Room Database, allowing the app to function even when offline.
- User data (credentials) will be cached, and the app will use local storage when a network is not available.

## Contributing

1. Fork the repository.
2. Create a new branch (`git checkout -b feature-branch`).
3. Make changes and commit them (`git commit -am 'Add feature'`).
4. Push to the branch (`git push origin feature-branch`).
5. Create a new Pull Request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgements

- [Dagger Hilt](https://developer.android.com/training/dependency-injection/hilt-setup) for dependency injection
- [Room Database](https://developer.android.com/training/data-storage/room) for offline data storage
- [MVVM Architecture](https://developer.android.com/jetpack/guide) for clean and maintainable code
