# Stage 1: Build the Angular app
FROM node:24-alpine AS angular-build

WORKDIR /angular-app

COPY ./booklore-ui/package.json ./booklore-ui/package-lock.json ./
RUN npm ci
COPY ./booklore-ui /angular-app/

RUN npm run build --configuration=production

# Stage 2: Build the Spring Boot app with Gradle
FROM gradle:9.3-jdk25-alpine AS springboot-build

WORKDIR /springboot-app

COPY ./booklore-api/gradlew ./booklore-api/gradle/ /springboot-app/
COPY ./booklore-api/build.gradle ./booklore-api/settings.gradle /springboot-app/
COPY ./booklore-api/gradle /springboot-app/gradle
COPY ./booklore-api/src /springboot-app/src

RUN ./gradlew clean build -x test

# Stage 3: Final image combining everything
FROM eclipse-temurin:25-jre-alpine

RUN apk update && apk add nginx

COPY ./nginx.conf /etc/nginx/nginx.conf
COPY --from=angular-build /angular-app/dist/booklore/browser /usr/share/nginx/html
COPY --from=springboot-build /springboot-app/build/libs/booklore-api-0.0.1-SNAPSHOT.jar /app/app.jar

EXPOSE 8080 80

CMD /usr/sbin/nginx -g "daemon off;" & \
    java -jar /app/app.jar