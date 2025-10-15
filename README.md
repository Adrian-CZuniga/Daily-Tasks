# 🗓️ Daily Task

**Daily Task** es una aplicación Android desarrollada en **Kotlin** utilizando **Jetpack Compose**.  
Su objetivo principal es demostrar el manejo de **persistencia local en el dispositivo** mediante el uso de **archivos JSON** en lugar de bases de datos tradicionales como Room o SQLite.  
El proyecto implementa un modelo **MVVM (Model–View–ViewModel)** y emplea **Paging 3** para la carga eficiente de datos en pantalla.

---

## ✨ Descripción General

Daily Task es una aplicación enfocada en la gestión diaria de tareas.  
El usuario puede visualizar sus tareas organizadas por día de la semana, dentro de un **Tab Layout** con desplazamiento horizontal (swipe o botones laterales).

Cada día muestra las tareas programadas, clasificadas en dos tipos principales:

- **Tareas Secuenciales:**  
  Se repiten de forma diaria o semanal, según la configuración del usuario.
- **Tareas Únicas:**  
  Son tareas asignadas a una fecha específica. Se puede establecer su importancia y filtrarlas de manera independiente.

---

## 🧩 Atributos de las Tareas

Cada tarea posee los siguientes campos:

| Atributo | Descripción |
|-----------|-------------|
| **Identificador** | Texto único y automático para uso interno. |
| **Nombre de la tarea** | Nombre descriptivo visible para el usuario. |
| **Día/s y hora** | Fecha y hora programada. Se notifica 15 minutos antes (valor configurable). |
| **Tipo de tarea** | Permite etiquetar tareas y crear etiquetas personalizadas. |
| **Fecha final** | Indica hasta cuándo se repetirá la tarea (puede ser indefinida). |

---

## 🗃️ Persistencia Local

El proyecto se centra en la **gestión de archivos JSON** como medio de almacenamiento.  
Cada tarea o “ticket” se serializa a JSON y se guarda directamente en el **storage del dispositivo**, sin usar bases de datos tradicionales.

- Al iniciar la aplicación, se verifica si existen los archivos correspondientes a la semana.
- Si la fecha actual está dentro del rango válido, se generan los tickets de tareas semanales.
- Cuando una tarea supera su fecha final, se mueve automáticamente a una carpeta de “tareas completadas o fuera de plazo”.

Este enfoque demuestra un **control total del manejo de archivos y su transformación a modelos de datos persistentes**, junto con su presentación a través de Paging 3.

---

## 🧱 Arquitectura

El proyecto sigue el patrón **MVVM**, con las siguientes capas principales:

- **Model:** Manejo de entidades, conversión JSON ↔ modelo de datos.
- **ViewModel:** Lógica de negocio y comunicación entre vista y modelo.
- **View (Compose):** Interfaz de usuario declarativa creada con Jetpack Compose.

---

## ⚙️ Tecnologías Utilizadas

- **Kotlin**
- **Jetpack Compose**
- **Paging 3**
- **Android ViewModel**
- **Gson / kotlinx.serialization** (para manejo de JSON)
- **Local Storage API de Android**

---

## 🚀 Estado del Proyecto

Actualmente el proyecto se encuentra en desarrollo.  
Las siguientes funcionalidades están planificadas:

- [ ] Sistema de notificaciones para recordatorios de tareas
- [ ] Personalización avanzada de etiquetas
- [ ] Estadisticas de feedback para el usuario.
- [ ] Test unitarios de persistencia local

---

## 📸 Capturas de Pantalla (Próximamente)

> Aquí se incluirán imágenes o GIFs demostrativos de la interfaz principal y el flujo de tareas.

---

## 👨‍💻 Autor

**Desarrollado por:** [Angel Adrian Cortes Zuñiga]  
**Contacto:** [aadriancz45@gmail.com]  
**GitHub:** [Adrian-CZuniga](https://github.com/Adrian-CZuniga)

---

## 🧠 Objetivo del Proyecto

Este proyecto fue creado con fines de aprendizaje y demostración técnica.  
Busca reflejar habilidades en:

- Programación en Kotlin para Android
- Uso de Jetpack Compose como herramienta de UI moderna
- Implementación de Paging 3 sin base de datos
- Manejo manual del almacenamiento local (archivos + JSON)
- Arquitectura MVVM aplicada a datos persistentes locales

---

## 🏁 Cómo Ejecutar el Proyecto

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Adrian-CZuniga/Daily-Tasks.git