# URL Sender App

![Kotlin](https://img.shields.io/badge/language-Kotlin-orange?logo=kotlin)
![Android](https://img.shields.io/badge/platform-Android-green?logo=android)
![License](https://img.shields.io/badge/license-MIT-blue)

---

## Overview

**URL Sender App** is an Android application that runs in the background and sends TCP/IP messages continuously at a fixed interval of 30 seconds. Each message contains a URL, which is loaded from a local asset file (`urls.txt`). The app sends these URLs in a cyclic manner to a TCP server running on a specified host and port.

---

## Features

- Background operation using Kotlin Coroutines
- Reads URLs from `assets/urls.txt`
- Sends URLs via TCP socket connection to specified host and port
- Cycles through the list of URLs every 30 seconds
- Handles connection errors gracefully with logging

---

## Getting Started

### Prerequisites

- Android Studio (latest stable recommended)
- Android device or emulator (API 21+)
- Internet permission in `AndroidManifest.xml`

