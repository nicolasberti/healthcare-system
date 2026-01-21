# Document Service

Microservicio para la gestión de documentos en el sistema de healthcare. Proporciona funcionalidades de carga, descarga y administración de documentos almacenados en AWS S3 con metadatos persistentes en PostgreSQL.

## Features

- Upload de documentos a AWS S3 con URLs presignadas
- Descarga de documentos mediante URLs temporales seguras
- CRUD completo de documentos
- Almacenamiento de metadatos en PostgreSQL
- Integración con AWS S3 usando SDK v2
- Documentación OpenAPI/Swagger
- Health checks con Spring Boot Actuator
- Distributed tracing con Zipkin
- Service Discovery con Eureka
- Configuración externa con Spring Cloud Config

## Architecture

Este servicio implementa una arquitectura hexagonal (Ports & Adapters), separando claramente las responsabilidades:

- **Interfaces/Controller Layer**: Maneja las solicitudes HTTP y mapeo DTO
- **Service Layer**: Orquesta los casos de uso
- **Domain Layer**: Contiene modelos de dominio y lógica de negocio
- **Infrastructure Layer**: Implementaciones de AWS S3, JPA y sistemas externos

## Tech Stack

| Technology | Version |
|------------|---------|
| Java | 21 |
| Spring Boot | 3.5.7 |
| Spring Cloud | 2025.0.0 |
| AWS SDK | 2.37.4 |
| PostgreSQL | - |
| Lombok | - |
| SpringDoc OpenAPI | 2.5.0 |

## Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 14+
- AWS Account (con credenciales o IAM Role)
- LocalStack (opcional, para desarrollo local)

## Installation

### Clonar el repositorio

```bash
git clone <repository-url>
cd document-service
```

### Configurar variables de entorno

Crear un archivo `application-local.yaml` o configurar las siguientes variables:

```yaml
aws:
  s3:
    endpoint: http://localhost:4566  # LocalStack (opcional)
    region: us-east-1
    access-key: test              # Solo para LocalStack/dev
    secret-key: test              # Solo para LocalStack/dev
    bucket: healthcare-documents

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/documents_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: update
```

### Ejecutar el servicio

```bash
mvn clean install
mvn spring-boot:run
```

## API Endpoints

### Obtener URL de Upload Presignada

Genera una URL temporaria para subir un archivo directamente a S3.

```http
POST /api/documents/upload-url
Content-Type: application/json

{
  "fileName": "informe_medico.pdf",
  "contentType": "application/pdf"
}
```

**Response:**

```json
{
  "url": "https://s3.amazonaws.com/..."
}
```

### Obtener URL de Download Presignada

Genera una URL temporaria para descargar un archivo de S3.

```http
GET /api/documents/{id}/download-url
```

**Response:**

```json
{
  "url": "https://s3.amazonaws.com/..."
}
```

### Listar Documentos

```http
GET /api/documents
```

**Response:**

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440000",
    "name": "informe_medico.pdf",
    "extension": "pdf",
    "contentType": "application/pdf",
    "path": "documents/550e8400-e29b-41d4-a716-446655440000_informe_medico.pdf",
    "url": "https://..."
  }
]
```

### Eliminar Documento

```http
DELETE /api/documents/{id}
```

**Response:** `204 No Content`

### Endpoints Legacy (Deprecated)

Los siguientes endpoints están disponibles pero marcados como deprecated:

```http
POST /api/documents/upload          # Upload directo (no recomendado en prod)
GET  /api/documents/{id}/download  # Descarga directa por el servicio
GET  /api/documents/{id}/url-firmada # Obtener URL presignada
```

## Project Structure

```
src/main/java/com/healthcare/document_service/
├── config/
│   ├── AwsS3Config.java       # Configuración de AWS S3 Client
│   └── AwsS3Properties.java   # Propiedades de configuración
├── controller/
│   └── DocumentController.java # Endpoints REST
├── entity/
│   ├── Document.java          # Entidad JPA
│   ├── UploadUrlRequest.java  # DTO para request
│   └── URLDto.java            # DTO para response
├── repository/
│   └── DocumentRepository.java # Repository JPA
└── service/
    └── DocumentService.java    # Lógica de negocio
```

## Configuration

### AWS S3

El servicio soporta dos modos de configuración:

**Development/LocalStack:**
```yaml
aws:
  s3:
    endpoint: http://localhost:4566
    access-key: test
    secret-key: test
```

**Producción (IAM Role):**
```yaml
aws:
  s3:
    endpoint: null  # Usa endpoint de AWS
    access-key: null  # Usa DefaultCredentialsProvider
    secret-key: null
```

### Database

```yaml
spring:
  datasource:
    url: jdbc:postgresql://host:port/database
    username: username
    password: password
  jpa:
    hibernate:
      ddl-auto: update  # usar 'validate' en producción
```

## Development

### Ejecutar tests

```bash
mvn test
```

### Build para producción

```bash
mvn clean package -Pprod
```

### Docker

```bash
docker build -t document-service .
docker run -p 8080:8080 document-service
```

## Monitoring

### Actuator Endpoints

- **Health Check:** `GET /actuator/health`
- **Metrics:** `GET /actuator/metrics`
- **Prometheus:** `GET /actuator/prometheus`

### OpenAPI/Swagger UI

- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

### Distributed Tracing

El servicio está configurado con Zipkin para distributed tracing. Configure el endpoint de Zipkin:

```yaml
management:
  tracing:
    sampling:
      probability: 1.0
  zipkin:
    tracing:
      endpoint: http://localhost:9411/api/v2/spans
```

## Deployment

### Requisitos de Infraestructura

- Bucket S3 creado y configurado
- Base de datos PostgreSQL
- Config Server (Spring Cloud Config)
- Eureka Server (Service Discovery)
- Zipkin Server (opcional, para tracing)

### Variables de Entorno en Producción

```bash
SPRING_PROFILES_ACTIVE=prod
CONFIG_SERVER_URL=http://config-server:8888
AWS_REGION=us-east-1
AWS_S3_BUCKET=healthcare-documents-prod
```

### Kubernetes

```yaml
apiVersion: v1
kind: Service
metadata:
  name: document-service
spec:
  selector:
    app: document-service
  ports:
  - port: 80
    targetPort: 8080
---
apiVersion: apps/v1
kind: Deployment
metadata:
  name: document-service
spec:
  replicas: 3
  selector:
    matchLabels:
      app: document-service
  template:
    metadata:
      labels:
        app: document-service
    spec:
      containers:
      - name: document-service
        image: document-service:latest
        ports:
        - containerPort: 8080
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "prod"
```

## Security Notes

- Las URLs presignadas tienen una expiración de 10 minutos por defecto
- En producción, usar IAM Roles en lugar de credenciales estáticas
- El campo `url` en la entidad `Document` es solo para desarrollo - no exponer en producción
- Validar tipos de archivos en el frontend antes de generar URLs de upload
- Considerar implementar CORS configurado específicamente por dominio

## Troubleshooting

### Error: Unable to load AWS credentials

**Solución:** Verificar que las credenciales de AWS estén configuradas correctamente o que el IAM Role tenga los permisos necesarios.

### Error: Bucket does not exist

**Solución:** Crear el bucket S3 especificado en `aws.s3.bucket` con los permisos correspondientes.

### Error: Connection refused to PostgreSQL

**Solución:** Verificar que la base de datos esté corriendo y las credenciales sean correctas.

## Contributing

1. Fork el repositorio
2. Crear una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir un Pull Request

## Code Standards

Este proyecto sigue las convenciones definidas en `AGENTS.md`:
- Arquitectura hexagonal estricta
- Controllers sin lógica de negocio
- Domain layer sin dependencias de frameworks
- Infrastructure layer encapsulada
- Naming conventions consistentes

## License

[Specified License - e.g., MIT License]

## Contact

Para soporte o preguntas, contactar al equipo de desarrollo.
