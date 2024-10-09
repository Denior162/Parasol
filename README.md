# Parasol App

Parasol is an Android application that provides users with real-time UV index information based on their selected city. The app utilizes Jetpack Compose for the UI and Hilt for dependency injection, ensuring a clean architecture and responsive design.

## Features

- **City Search**: Users can search for cities using an intuitive search interface.
- **UV Index Information**: Displays current UV index and forecasts for selected cities.
- **User Preferences**: Saves the user's selected city for quick access.

## Tech Stack

- **Kotlin**: The primary programming language used in the project.
- **Jetpack Compose**: For building the UI in a declarative manner.
- **Hilt**: For dependency injection, simplifying the management of dependencies.
- **Retrofit**: For making network requests to fetch UV index data and city information.
- **Gson**: For JSON serialization and deserialization.

## Getting Started

### Prerequisites

- Android Studio (latest version)
- Kotlin 1.5 or higher
- Gradle 7.0 or higher

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/parasol.git
   cd parasol
   ```

2. Open the project in Android Studio.

3. Sync the Gradle files.

4. Run the app on an emulator or physical device.

## Architecture

The app follows a clean architecture pattern:

- **ViewModel**: Manages UI-related data and handles user interactions.
- **Repository**: Acts as a mediator between data sources (API, database).
- **API Services**: Interfaces for network requests using Retrofit.
  
## Usage

1. Launch the app.
2. Use the search bar to find a city.
3. Select a city to view its UV index information.
4. The app will save your selected city for future access.

## Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a new branch (`git checkout -b feature/YourFeature`).
3. Make your changes and commit them (`git commit -m 'Add some feature'`).
4. Push to the branch (`git push origin feature/YourFeature`).
5. Open a pull request.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Thanks to the contributors of Jetpack Compose, Hilt, Retrofit, and Gson for their excellent libraries that made this project possible.

---

Feel free to customize this README further based on your project's specifics, such as adding screenshots, detailed setup instructions, or additional features you plan to implement!

Citations:
[1] problemy-s-ViewModel-posle-Hilt.txt https://ppl-ai-file-upload.s3.amazonaws.com/web/direct-files/1064/9c8c1592-adb6-4ef2-b9ae-ba5f47d11585/problemy-s-ViewModel-posle-Hilt.txt
