# Laboratorio 7 — MongoDB (Java 8 / NetBeans)

Proyecto basado en `Lab-DespliegueCloud`, adaptado a `mongoPU` y `ArquiSW`.

## Funcionalidades implementadas
- `Competitor`: entidad NoSQL con `Vehicle` embebido y `Producto` asociado mediante OneToOne obligatorio.
- `Producto`: entidad independiente persistente con nombre, marca y fechaCompra.
- `POST /competitors`: registro de competidor junto a producto obligatorio.
- `GET /competitors`: lista de competidores.
- `GET /productos`: lista de productos persistidos.
- `GET /competitors/names/a`: nombres de competidores que empiezan por A.
- `GET /competitors/{name}`: búsqueda por nombre exacto.

## Compilar
Abrir esta carpeta en NetBeans, configurar Java Platform JDK 8 y ejecutar Clean and Build.

## Ejecutar
Desde NetBeans, ejecutar `com.example.Main` con directorio de trabajo en la raíz del proyecto.
O usar PowerShell desde la raíz después de compilar:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Zulu\zulu-8'
& "$env:JAVA_HOME\bin\java.exe" -cp "target/classes;target/dependency/*" com.example.Main
```

Servidor esperado: http://localhost:8080/ .

## Pruebas REST (Postman)

POST http://localhost:8080/competitors
Content-Type: application/json

```json
{
  "name":"Andres",
  "surname":"Melo",
  "age":29,
  "telephone":"6011234567",
  "cellphone":"3001234567",
  "address":"Bogota",
  "city":"Bogota",
  "country":"Colombia",
  "vehicle":{"plate":"ABC123","brand":"Mazda","modelYear":2022},
  "producto":{"nombre":"Portatil","marca":"Lenovo","fechaCompra":"2026-11-01"}
}
```

GET http://localhost:8080/productos
GET http://localhost:8080/competitors/names/a
GET http://localhost:8080/competitors

Desde MongoDB Compass: base `ArquiSW`, colecciones normalmente `COMPETITOR` y `PRODUCTO`.

## Advertencia de compatibilidad
El proyecto conserva el driver MongoDB Java **2.12.3** pedido en la guía de 2019. Este driver puede fallar frente a servidores MongoDB actuales que retiraron protocolos antiguos. Si al ejecutar aparece un error de protocolo o de conexión, no significa que Maven haya fallado: es una incompatibilidad en tiempo de ejecución. Debe resolverse con una versión compatible del servidor o una modernización específica de la capa de persistencia.

## Estado de validación
Se revisó el código y se implementaron los requisitos. **No se han ejecutado pruebas de integración con el MongoDB local del estudiante ni una compilación Maven en este entorno**. Se requiere verificar `BUILD SUCCESS` y probar las peticiones antes de la entrega.
