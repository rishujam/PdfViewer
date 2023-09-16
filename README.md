# Local PDF Viewer Library for Android

## Overview

This Android library provides a lightweight and easy-to-use PDF viewer for local PDF files within your Android applications. It allows users to view PDF documents stored locally on their devices. Whether you want to include a PDF viewer in your app for reading documents or displaying user manuals, this library simplifies the integration process.

Key features:
- View local PDF files.
- Smooth and intuitive user interface.
- Pinch to zoom

## Screenshot

<img src="https://github.com/rishujam/PdfViewer/assets/74773876/be378d46-6a7f-4e87-bea8-61afa1fdcd75" width="260" height="480">

## Installation

settings.gradle:
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

To get started, add the library to your Android project by including it as a dependency. You can do this by adding the following lines to your app's `build.gradle` file:

gradle
```
dependencies {
    implementation 'com.github.rishujam:PdfViewer:1.3'
}
```
## Demo

XML File
```
    <com.example.docwatcher.DocView
		android:id="@+id/docView"
		android:layout_width="match_parent"
		android:layout_height="match_parent"/>
```
Fragment/Activity
```
    companion object {
        private const val PATH = "/data/data/com.example.pdfviewer/files/sample_pdf.pdf"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.docView.setData(PATH)
    }
```
