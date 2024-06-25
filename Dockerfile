# Etapa de construcción con Maven
FROM maven:3.8.4-openjdk-17-slim AS build

# Establece el directorio de trabajo dentro del contenedor
WORKDIR /app

# Copia los archivos de configuración y la definición del proyecto
COPY pom.xml .
COPY src ./src

# Compila el proyecto y genera el archivo JAR
RUN mvn clean package

# Etapa de construcción de JRE mínimo
FROM eclipse-temurin:17-jdk-alpine AS packager

# Construir distribución mínima de módulos
RUN jlink \
    --verbose \
    --add-modules java.base,java.sql,java.naming,java.management,java.instrument,java.desktop \
    --compress 2 \
    --strip-debug \
    --no-header-files \
    --no-man-pages \
    --output /opt/jre

# Segunda etapa, configurar entorno mínimo
FROM alpine:latest

# Instalar dependencias necesarias para ejecutar Java
RUN apk add --no-cache libc6-compat

# Copiar JRE personalizado desde la etapa de construcción
COPY --from=packager /opt/jre /opt/jre

# Configuración del entorno y del PATH
ENV JAVA_HOME=/opt/jre
ENV PATH=$PATH:$JAVA_HOME/bin

# Directorio de trabajo para el JAR
WORKDIR /app

# Copiar el JAR generado desde la etapa de construcción
COPY --from=build /app/target/ms-restaurantmenu.jar .

# Exponer el puerto 8086 (ajusta si es necesario)
EXPOSE 8086

# Comando de inicio
CMD ["java", "-jar", "ms-restaurantmenu.jar"]
