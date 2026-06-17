# Screen Share Server

<p>

  <img src="https://counter.kimhwan.kr/?key=github-akon47-screen-share-server" />
  <img alt="GitHub" src="https://img.shields.io/github/license/akon47/screen-share-server">
  <img alt="GitHub stars" src="https://img.shields.io/github/stars/akon47/screen-share-server">
</p>

The signaling & backend server for the [Screen Share](https://github.com/akon47/screen-share) service, built with the Spring Framework. It connects WebRTC peers and relays all in-room messaging — the actual media stream stays peer-to-peer and never passes through this server.

## ✨ Features
- **WebRTC signaling** over WebSocket (STOMP / SockJS) — relays SDP offers/answers and ICE candidates between host and viewers.
- **Channel management** — create rooms (public or private, with an optional password), join, and join-by-password.
- **Public channel listing** — exposes currently active public rooms with their live viewer counts.
- **JWT-based access** — hosts and guests receive scoped tokens (host / guest roles) when creating or joining a channel.
- **Real-time messaging relay** — text chat, nickname updates, join/leave events, emoji reactions, host kick, and host drawing annotations (draw / clear).
- **Chat history** with cursor-based paging.
- **Short-lived STUN/TURN credentials** issued per session for connections on restrictive networks.
- **Automatic channel cleanup** of stale / empty rooms.
- **Redis** for channel/session state and **Swagger** API documentation.

## 🛠️ Tech Stack
- Java + Spring Boot (Spring Security, Spring WebSocket, Spring Data JPA)
- Redis, JWT, Swagger (springfox)

## 📄 API Document

[https://api.screenshare.kimhwan.kr/swagger-ui/index.html](https://api.screenshare.kimhwan.kr/swagger-ui/index.html)

## 🚀 Service
https://screenshare.kimhwan.kr/

## 🎆 Contributing
- This project is an open source project. Anyone can contribute in any way.

## 🐞 Bug Report
- If you find a bug, please report to us posting [issues](https://github.com/akon47/screen-share/issues) on GitHub.
