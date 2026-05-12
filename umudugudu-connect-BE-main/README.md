# Umudugudu Connect — Backend

## Structure
```
src/main/java/com/umudugudu/
├── UmuduguduApplication.java   Entry point
├── config/                     SecurityConfig, CorsConfig, RedisConfig
├── controller/                 One controller per domain (Auth, Activity, ...)
├── dto/request/ + response/    POJOs with @Valid constraints
├── entity/                     JPA entities
├── exception/                  GlobalExceptionHandler + custom exceptions
├── repository/                 Spring Data JPA interfaces
├── security/                   JwtFilter, JwtUtils, UserDetailsServiceImpl
├── service/ + impl/            Business logic
└── util/                       OtpService, SmsService, PushNotifService, MoMoService
```

## Commands
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev   # run dev
mvn test                                              # run tests
mvn package -DskipTests                              # build JAR
```

## Adding an endpoint
1. entity/ → repository/ → dto/ → service/ → controller/ → tests
