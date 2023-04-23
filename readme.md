# Huawei In-App Purchases Demo

This demo App demonstrates Huawei In-App Purchases (IAP) client APIs and usages. 

Documentation can be found at this 
[link](https://developer.huawei.com/consumer/cn/codelab/HMSInAppPurchase/index.html#0).

## Table of Content

- [Huawei In-App Purchases Demo](#huawei-in-app-purchases-demo)
  - [Table of Content](#table-of-content)
  - [Introduction](#introduction)
  - [Getting Started](#Getting-Started)
    - [Environment requirement](#environment-requirement)
    - [Hardware Requirements](#hardware-Requirements)
  - [Software Requirements](#software-Requirements)
  - [Installation](#installation)
  - [Configuration](#configuration)
  - [Tutorial](#tutorial)
  - [Licensing](#licensing)

## Introduction 

Huawei In-App Purchases provides 3 types of products: consumable, non-consumable and 
auto-renewable subscription. 

* Consumable : Consumables are used once, are depleted, and can be purchased again after being consumed.

* Non-consumable : Non-consumables can be only purchased once and do not expire. 

* Auto-renewable subscription : Once purchased, Users can access to value-added functions or content in a specified period of time. The subscriptions will automatically renew on a recurring basis until users decide to cancel.

This project implements the purchase process of consumable products, helping developers learn more about Huawei In-App Purchases.

## Getting Started

   1. Finish the configuration in AppGallery Connect. For details, please refer to [the related document](https://developer.huawei.com/consumer/en/codelabsPortal/carddetails/HMSInAppPurchase).
	  - Finish integration preparations.
	  - Enable IAP.
	  - Configuring Your Products.
   2. Check whether the Android studio development environment is ready.
   3. Build the demo.
      - Download the file "agconnect-services.json" of the app on AGC, and add the file to the app root directory(\app) of the demo. 
      - Add the certificate file to the app root directory(\app) of the demo and add your configuration to in the app-level `build.gradle` file. 
      - Open the app-level `build.gradle` file, and change the value of applicationId to your app package name.
      - Replace the PUBLIC_KEY in the Key class with the public key of your app. For details about how to obtain the public key, please refer to [Querying IAP Information](https://developer.huawei.com/consumer/en/doc/development/HMSCore-Guides/query-payment-info-0000001050166299).
      - Replace products in the demo with your products.
   4. Run the sample on your Android device or emulator.

## Environment requirement

To be able to develop, build and debug this demo, you will need at least the following environment:

### Hardware Requirements
* A computer (desktop or laptop)
* A phone used for running the app with HUAWEI IAP integrated.
* A data cable used for conneting the computer to the phone.

### Software Requirements
* JDK version: 1.8 or later
* Android Studio version: 3.6.1 or later
  - minSdkVersion: 19 or later
  - targetSdkVersion: 30 (recommended)
  - compileSdkVersion: 30 (recommended)
  - Gradle version: 5.4.1 or later (recommended)

## Installation

* Download the project and open the downloaded folder in Android Studio or compatible IDE.
* Use IDE's functionality to install configured project on to your device.

## Configuration

To use the project package, you can configure a consumable in-app product in the AGC page referring to [configuring In-App Product Information](https://developer.huawei.com/consumer/en/codelab/HMSInAppPurchase/index.html#4), and then apply the product in the project to finish the payment process.  

## Tutorial

1. The demo provides *gem* as an example of consumable product. 
Once you start the demo, you should be able to see the following page.

    <img src="images/homepage.jpg" alt="demo home page" height="500"/>

1. Tap **Ten gems**, the demo will call the `createPurchaseIntent` API,
    and jump to the checkout page which is provided by IAP Service.

    <img src="images/checkout-page.jpg" alt="checkout page" height="500"/>

3. When you finish payment, HUAWEI IAP will return the payment result to your app through onActivityResult. If success, the demo will call `consumeOwnedPurchase` API to notify Huawei IAP Service that user has consumed the purchase, and then the page will show you a tip "Pay success, and the product has been delivered".

    <img src="images/result.jpg" alt="checkout page" height="500"/>

## Licensing

This demo is licensed under the [Apache License, version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
