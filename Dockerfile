# ====== ЭТАП 1: Сборка ======
FROM gradle:8.12-jdk21 AS builder

WORKDIR /home/gradle/project

# Копируем файлы зависимостей
COPY --chown=gradle:gradle build.gradle.kts settings.gradle.kts ./
COPY --chown=gradle:gradle gradle ./gradle
COPY --chown=gradle:gradle gradle.properties ./

# Скачиваем зависимости
RUN gradle dependencies --no-daemon

# Копируем исходный код
COPY --chown=gradle:gradle src ./src

# Собираем дистрибутив
RUN gradle installDist --no-daemon


# ====== ЭТАП 2: Финальный образ ======
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Копируем собранный дистрибутив из builder
COPY --from=builder /home/gradle/project/build/install/nodewatch ./

EXPOSE 8080

# Запускаем приложение
CMD ["bin/nodewatch"]