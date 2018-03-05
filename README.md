# Sample-User-Microservices

## What is this?
This is a fully running client/server implementation for a user management tool, with goals:
1. Scalable
2. Easy to deploy
3. Same environment for Dev|Prod
4. Concise

## Technology:
- **Sever**: Kotlin, Java, Jetty, Redis, Apache Shiro, AES encryption, Vert.X messaging (with ssl).
- **Client**: Aurelia.io, Typescript.

## Features:
- Login (with remember me)
- Captchas
- Google Analytics
- Registration
- Confirm email
- Forgot password
- Two factor authentication (enable | disable | recovery codes)
- Guest account

## To Run #
1. add to `etc/hosts`
    - `127.0.0.1 usermicroservice.com`
2. `cd ./server` 
3. `./gradlew build jar`
4. `cd ..`
4. `docker-compose up --build`
5. open: `https://usermicroservice.com:8443` 
6. in chrome accept private certificate.


### when adding new microservices
update self.keystore in `resources` 

    keytool -keystore self.keystore -alias localhost -validity 3650 -genkey -keyalg RSA -sigalg SHA256withRSA -ext san=dns:localhost,dns:usermicroservice.com,dns:apigateway,dns:members,dns:webserver,dns:contact