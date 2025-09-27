> **Autor:** Carlos Alvarado | **Fecha:** 27 de Septiembre de 2025
# 🎰 Jackpot Probability Calculator (Spring Boot)


## 🛠️ Tecnologías Utilizadas

| Categoría | Tecnología                     | Propósito                                                    |
| :--- |:-------------------------------|:-------------------------------------------------------------|
| Framework | **Spring Boot** (v3.5.6)       | Desarrollo rápido del servidor REST.                         |
| Lenguaje | **Java 17**                    | Lenguaje de programación utilizado.                          |
| Persistencia | **Spring Data JPA**            | Gestión de la capa de datos.                                 |
| Base de Datos | **H2 Database**                | Base de datos relacional en memoria para desarrollo/pruebas. |
| Testing | **JUnit 4 / Spring Boot Test** | Pruebas unitarias.                                           |
| Gestor de Dependencias | **Maven**                      | Gestión del proyecto y dependencias.                         |

## ⚙️ Configuración y Ejecución (Con Docker)

Si tenéis **Docker** y **Docker Compose** instalados, esta es la forma más rápida y portable de ejecutar la aplicación.

### Prerrequisitos

* Docker y Docker Compose instalados.

### 1. Descargar el Repositorio
```console
git clone https://github.com/csalvarado/jackpotprobabilitycalculator.git
cd jackpotprobabilitycalculator
```
Se ha dejado el proyecto en un fichero zip para poder descargar desde el hilo de correo. Debéis hacer change directory al proyecto para seguir con las instrucciones.

### 2. Compilar, Construir y Ejecutar con Docker Compose
Construye la imagen de Docker y levanta el contenedor en segundo plano (-d)
```console
docker-compose up --build -d
```
Una vez que el contenedor esté corriendo, la aplicación será accesible en http://localhost:8080/swagger-ui.html.

### 3. Detener la Aplicación
Para detener y eliminar el contenedor (manteniendo la imagen construida):
```console
docker-compose down
```

## ⚙️ Configuración Alternativa (Sin Docker)
Si no deseas usar Docker, debes seguir los pasos de ejecución tradicional de Spring Boot:

### Prerrequisitos
* Java 17+ y Maven.

### Compilar y Ejecutar

### 1. Compila y empaqueta el proyecto
```console
mvn clean install
```
### 2.  Ejecuta la aplicación. 

El servidor se iniciará en http://localhost:8080/swagger-ui.html.
```console
mvn spring-boot:run
```
### 3.  Detener el servidor
Para parar el server local, presiona la combinación de teclas: **Ctrl + C**

## 📖 Documentación de la API (Swagger UI)

### 🔗 URL de Acceso

Para explorar el *endpoint* y probar las peticiones directamente desde el navegador, utiliza la siguiente URL:

➡️ **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

### Peticiones

| Método | Ruta | Descripción |
| :--- | :--- | :--- |
| **POST** | `/jackpot-draw` | **Realiza una apuesta.** Requiere el parámetro `betAmount` para calcular la probabilidad. |
| **GET** | `/history` | **Consulta el historial** de todas las apuestas realizadas o de Datos:** Podrás ver la estructura del objeto `Bet` (apuesta) y las anotaciones para los códigos de respuesta.

## 💻 Prueba y Consulta por Consola (cURL)

Una vez que la aplicación esté corriendo, puedes interactuar con el endpoint utilizando comandos cURL en tu terminal bash o símbolo del sistema.

### 1. Realizar una Apuesta (POST /jackpot-draw)

Para iniciar un sorteo, debes enviar una petición **POST** e incluir el monto de la apuesta en el parámetro betAmount.
```console
curl -X POST "http://localhost:8080/jackpot-draw?betAmount=10.50"
```
#### Ejemplo de salida:
```terminaloutput
Sorry, you didn't win this time. Try again.
```

### 2. Consultar el Historial (GET /history)
Para obtener el historial completo de apuestas, simplemente ejecuta petición **GET** /history.
```console
curl "http://localhost:8080/history"
```
#### Ejemplo de salida (Formato JSON):
```json
[
  {
    "id": 1,
    "betAmount": 5.0,
    "won": false,
    "timestamp": "2025-09-27T10:00:00",
    "winningProbability": 0.20
  },
  {
    "id": 2,
    "betAmount": 10.5,
    "won": true,
    "timestamp": "2025-09-27T10:01:30",
    "winningProbability": 0.24
  }
]
```