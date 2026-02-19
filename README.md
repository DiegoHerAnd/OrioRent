# ⚠️ ADVERTENCIA

> **Este es un proyecto en desarrollo y no representa el producto final.**

---

## Estructura del Proyecto

El proyecto **OrioRent** está organizado en dos carpetas principales, cada una con un propósito específico:

# Video Demostración:
[![Ver Demostración](https://img.youtube.com/vi/6OUtWggupp4/maxresdefault.jpg)](https://youtu.be/M8AZCJxDW4E)

### 1. OrioRentSQL

Esta carpeta contiene todos los scripts y archivos relacionados con la base de datos. Aquí se encuentra:

- La estructura de la base de datos (tablas, relaciones, índices, etc.).
- Scripts de ejemplo para poblar o modificar la base de datos.
- Documentación interna sobre la lógica de los datos.

**Propósito:**  
Esta parte está pensada principalmente para los desarrolladores. Nos permite entender, mantener y actualizar la base de datos de forma eficiente sin afectar directamente la experiencia del usuario final. Es una herramienta interna de desarrollo y documentación.

### 2. OrioRent (Kotlin + SQLite)

Esta carpeta contiene la aplicación desarrollada en **Kotlin**, que interactúa con la base de datos mediante **SQLite**. Aquí se encuentra:

- El código de la aplicación que gestiona la lógica de negocio.
- La interfaz y funcionalidades que interactúan directamente con el usuario.
- La integración con la base de datos para realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar).

**Propósito:**  
Esta parte es la que utiliza el público. Mientras que la base de datos contiene la información y la lógica interna, la aplicación Kotlin + SQLite ofrece la experiencia completa al usuario, manejando datos y mostrando información de manera accesible y funcional.

---

## Resumen

- **OrioRentSQL**: Para desarrolladores, manejo y mantenimiento de la base de datos.  
- **OrioRent (Kotlin + SQLite)**: Para usuarios finales, interfaz y funcionalidades de la aplicación.

Esta separación permite trabajar de manera organizada, facilitando tanto el desarrollo como el mantenimiento de la base de datos y la aplicación.
