<div align="center">

<img src="https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
<img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
<img src="https://img.shields.io/badge/Android%20Studio-3DDC84?style=for-the-badge&logo=android-studio&logoColor=white" />
<img src="https://img.shields.io/badge/Material%20Design-757575?style=for-the-badge&logo=material-design&logoColor=white" />

<br /><br />

```
 ███████╗████████╗███████╗ ██████╗  ██████╗ ██╗   ██╗ █████╗ ██╗   ██╗██╗  ████████╗
 ██╔════╝╚══██╔══╝██╔════╝██╔════╝ ██╔═══██╗██║   ██║██╔══██╗██║   ██║██║  ╚══██╔══╝
 ███████╗   ██║   █████╗  ██║  ███╗██║   ██║██║   ██║███████║██║   ██║██║     ██║   
 ╚════██║   ██║   ██╔══╝  ██║   ██║██║   ██║╚██╗ ██╔╝██╔══██║██║   ██║██║     ██║   
 ███████║   ██║   ███████╗╚██████╔╝╚██████╔╝ ╚████╔╝ ██║  ██║╚██████╔╝███████╗██║   
 ╚══════╝   ╚═╝   ╚══════╝ ╚═════╝  ╚═════╝   ╚═══╝  ╚═╝  ╚═╝ ╚═════╝ ╚══════╝╚═╝   
```

### 🔐 Hide secrets in plain sight — Image Steganography for Android

<br />

![GitHub repo size](https://img.shields.io/github/repo-size/krrrish11/StegoVault?color=00E5FF&style=flat-square)
![GitHub last commit](https://img.shields.io/github/last-commit/krrrish11/StegoVault?color=9C6FFF&style=flat-square)
![GitHub stars](https://img.shields.io/github/stars/krrrish11/StegoVault?color=00E5FF&style=flat-square)
![License](https://img.shields.io/badge/license-MIT-00E676?style=flat-square)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-9C6FFF?style=flat-square)
![Target SDK](https://img.shields.io/badge/Target%20SDK-34-00E5FF?style=flat-square)

</div>

---

## 📖 What is StegoVault?

**StegoVault** is an Android microproject that implements **Image Steganography** — the technique of hiding secret text messages *inside* digital images without any visible change to the image. The image looks completely normal to the naked eye, but carries a hidden message embedded at the pixel level.

> *"The best place to hide something is in plain sight."*

Unlike encryption (which scrambles data and makes it obviously hidden), steganography makes the very existence of the secret message invisible. StegoVault combines both concepts — your message is hidden AND undetectable.

---

## ✨ Features

| Feature | Description |
|---|---|
| 🔒 **Encode Message** | Select any image → type a secret message → generate a stego image |
| 🔓 **Decode Message** | Load any stego image → instantly extract the hidden text |
| 📊 **Capacity Indicator** | Real-time display of how many characters the image can hold |
| 🔢 **Live Character Counter** | See exactly how much space your message uses as you type |
| 💾 **Auto PNG Save** | Stego images auto-saved in lossless PNG format to your gallery |
| 📋 **Copy to Clipboard** | One-tap copy of the extracted message |
| 🌙 **Dark Neon UI** | Modern dark theme with cyan & purple neon accents |
| ⚡ **Background Processing** | Encoding/decoding runs on a background thread — no UI freezing |
| 🎬 **Smooth Animations** | Animated splash, slide transitions, and staggered entry effects |

---

## 🧠 How It Works — The LSB Algorithm

StegoVault uses the **LSB (Least Significant Bit)** steganography technique.

Every pixel in a digital image has three color channels: **Red**, **Green**, and **Blue** — each stored as an 8-bit number (0–255). The LSB algorithm replaces the **last bit** of each channel with one bit of the secret message.

```
Original pixel channel:   1 0 1 1 0 1 1 0  (182)
Message bit to hide:                      1
Modified pixel channel:   1 0 1 1 0 1 1 1  (183)  ← change of just 1/255
```

Since the change in value is at most **±1 out of 255**, the human eye cannot distinguish the modified image from the original. With 3 bits per pixel (one per channel), a 1-megapixel image can hide approximately **375,000 characters** of text.

### Capacity Formula

```
Max characters = (image_width × image_height × 3) / 8
```

### Encoding Flow

```
Secret Message + "##END##" delimiter
         ↓
Convert to UTF-8 byte array
         ↓
For each pixel → modify LSB of R, G, B channels
         ↓
Save as lossless PNG
         ↓
🖼️  Stego Image (visually identical to original)
```

### Decoding Flow

```
🖼️  Stego Image (PNG)
         ↓
Read LSB of R, G, B from each pixel sequentially
         ↓
Reconstruct bits → bytes → characters
         ↓
Stop at "##END##" delimiter
         ↓
🔓  Extracted Secret Message
```

> ⚠️ **Important:** Always use **PNG format** for stego images. JPEG uses lossy compression that destroys the LSBs and corrupts the hidden data.

---

## 📱 Screenshots

| Splash | Home | Encode |
|:---:|:---:|:---:|
| *Animated Logo Entry* | *Choose Encode or Decode* | *Step-by-step Encoding* |

| Decode | Result (Encoded) | Result (Decoded) |
|:---:|:---:|:---:|
| *Load Stego Image* | *Success + Filename* | *Extracted Message* |

---

## 🏗️ Project Structure

```
StegoVault/
├── app/src/main/
│   ├── java/com/steganography/app/
│   │   ├── SteganographyEngine.java    ← Core LSB algorithm (encode + decode)
│   │   ├── SplashActivity.java         ← Animated splash screen
│   │   ├── MainActivity.java           ← Home screen with card navigation
│   │   ├── EncodeActivity.java         ← Full encoding workflow (3 steps)
│   │   ├── DecodeActivity.java         ← Full decoding workflow
│   │   └── ResultActivity.java         ← Output screen with copy/done actions
│   │
│   ├── res/
│   │   ├── layout/
│   │   │   ├── activity_splash.xml
│   │   │   ├── activity_main.xml
│   │   │   ├── activity_encode.xml
│   │   │   ├── activity_decode.xml
│   │   │   └── activity_result.xml
│   │   ├── drawable/                   ← Custom shapes, icons, glow effects
│   │   ├── values/
│   │   │   ├── colors.xml              ← Dark neon color palette
│   │   │   ├── themes.xml              ← MaterialComponents dark theme
│   │   │   └── strings.xml
│   │   └── anim/                       ← Slide and fade transitions
│   │
│   └── AndroidManifest.xml
│
└── app/build.gradle
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| **Java** | Primary programming language |
| **Android Studio** | Development IDE |
| **Material Design 3** | UI components and theming |
| **Android Bitmap API** | Pixel-level image manipulation |
| **MediaStore API** | Saving stego images to gallery |
| **ActivityResultContracts** | Modern image picker (no deprecated startActivityForResult) |
| **Java Threads** | Background encoding/decoding to prevent UI freezing |
| **Glide** | Image loading and display |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio **Hedgehog** or newer
- Android device or emulator running **API 24+** (Android 7.0+)
- Gradle **8.2.0**

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/krrrish11/StegoVault.git
   ```

2. **Open in Android Studio**
   ```
   File → Open → Select the StegoVault folder
   ```

3. **Sync Gradle**
   ```
   Android Studio will auto-prompt → Click "Sync Now"
   ```

4. **Run on device or emulator**
   ```
   Click ▶️ Run or press Shift + F10
   ```

### Permissions Required

```xml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />   <!-- API 33+ -->
```

---

## 📋 How to Use

### 🔒 Hiding a Message

1. Launch **StegoVault** and tap **Encode Message**
2. Tap **Choose Image** and select any photo from your gallery
3. View the **maximum capacity** displayed (e.g., "Max capacity: 45,230 characters")
4. Type your secret message in the text field
5. Watch the **live character counter** to stay within limits
6. Tap **Encode & Hide** — a progress bar shows while processing
7. The stego image is **automatically saved** to your gallery as a PNG
8. The result screen shows the image and confirms success ✅

### 🔓 Extracting a Message

1. Launch **StegoVault** and tap **Decode Message**
2. Tap **Choose Stego Image** and select the PNG image with the hidden message
3. Tap **Extract Message** — the hidden text is decoded in seconds
4. View the extracted message on the result screen
5. Tap **Copy Message** to copy it to your clipboard 📋

---

## ⚙️ Core Algorithm — Code Snippet

```java
// Embed 1 bit into a color channel
int modified_channel = (original_channel & 0xFE) | bit_to_embed;
//  0xFE = 11111110  → clears the LSB
//  bit_to_embed     → sets the LSB to the message bit

// Extract 1 bit from a color channel  
int extracted_bit = channel_value & 1;
//  & 1 isolates the LSB
```

---

## 📊 Capacity Examples

| Image Size | Resolution | Max Characters |
|---|---|---|
| Low-res photo | 480 × 640 | ~115,200 |
| HD photo | 1280 × 720 | ~345,600 |
| Full HD photo | 1920 × 1080 | ~777,600 |
| 4K photo | 3840 × 2160 | ~3,110,400 |

---

## ⚠️ Limitations

- **PNG only** — JPEG/WebP compression destroys hidden data
- **No password protection** — anyone with this app can decode a stego image
- **Text only** — this version supports text messages only (not files or images)
- **Single-layer** — one message per image; re-encoding overwrites previous data

---

## 🔮 Future Improvements

- [ ] Password-protected encoding with AES encryption layer
- [ ] Support for hiding files (PDF, audio) inside images
- [ ] Multi-bit LSB (2 or 4 bits per channel) for higher capacity
- [ ] Steganalysis detection resistance techniques
- [ ] Dark/light theme toggle
- [ ] Share stego image directly from result screen

---

## 📚 References

- [Android Bitmap API](https://developer.android.com/reference/android/graphics/Bitmap)
- [Material Design 3](https://m3.material.io)
- [MediaStore API](https://developer.android.com/reference/android/provider/MediaStore)
- [Steganography — Wikipedia](https://en.wikipedia.org/wiki/Steganography)
- [Gary Kessler — Steganography Reference](https://www.garykessler.net/library/steganography.html)

---

## 👤 Author

**Krrish**  
📧 GitHub: [@krrrish11](https://github.com/krrrish11)  
🏫 GTMC Vishnupuri, Nanded — MAD Microproject + NIS Microproject

---

## 📄 License

```
MIT License

Copyright (c) 2025 Krrish

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software to use, copy, modify, merge, publish, distribute, and/or
sell copies of the Software, subject to the above copyright notice appearing
in all copies.
```

---

<div align="center">

**⭐ If you found this project useful, please consider giving it a star!**

*Made with ❤️ and a lot of bitwise operations*

`0xFE & pixel = steganography`

</div>
