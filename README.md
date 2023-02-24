# Majika - IF3210-2023-Android-MSN

![Majika](app/src/main/res/drawable/logo.png)
## About
Aplikasi Multiverse kegunaan yang terdapat foto twibbon yang tidak berhubungan dengan fitur lainnya, menu dari suatu restoran dengan fitur tambahan mendeteksi suhu yang sangat fungsional, restoran berdasarkan lokasi terdekat, dan pembayaran dengan scan QR

## Library 

Berikut merupakan library yang digunakan

- Retrofit
- Moshi
- Google ML kit (Barcode Scanning)
- Room
- CameraX

## cara penggunaan
- jalankan executable backend yang dapat didownload pada [link berikut](https://drive.google.com/drive/u/1/folders/1AVrZUfIrDluISTemOAmy_jj4LQNTxuCb) dengan perintah pada command prompt

```./nama_executable_file```

- ubah server dan port pada file **BackendService.kt** yang terdapat pada directory **app/src/main/java/com/example/majika/network**

```private const val BASE_URL = "http://192.168.1.3:8000"```

- jalankan aplikasi dengan menggunakan Android Studio

## Contributor

project ini dikerjakan oleh orang-orang berikut ini
- Mahesa Lizardy  |  13520116
- Bryan Amirul Husna | 13520146
- Mohamad Hilmi Rinaldi | 13520149


dengan pembagian tugas sebagai berikut

| Nama          | Tugas                            |
| ------------- | -------------------------------- |
| Hilmi         | - daftar makanan minuman         |
|               | - halaman cabang restoran (gmaps)| 
| Bryan         | - halaman keranjang              |
|               | - halaman pembayaran (scan qr)   | 
| Mahesa        | - header dan navbar              |
|               | - halaman twibbon (kamera)       |

## Screenshots

![twibbon](/screenshot/twibbon.png)
![cabang restoran](/screenshot/cabang_restoran.png)
![daftar menu](/screenshot/daftar_menu.png)
![keranjang](/screenshot/keranjang.png)
![scan qr](/screenshot/scan_qr.png)
## Time Spent

| Tugas                            | Spent      |
| -------------------------------- | ---------- |
| - daftar makanan minuman         | 12h        |
| - halaman cabang restoran (gmaps)| 10h        |
| - halaman keranjang              | 12h        |
| - halaman pembayaran (scan qr)   | 12h        |
| - header dan navbar              | 2h         |
| - halaman twibbon (kamera)       | 24h        |