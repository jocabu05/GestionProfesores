# Gestión de Sustituciones de Profesores

Este proyecto es un programa en **Java** que permite gestionar las sustituciones de profesores en un instituto en caso de ausencia.  

El sistema permite detectar profesores libres, asignar sustitutos y llevar un control acumulativo de las sustituciones realizadas por cada profesor.

---

## 📂 Estructura del proyecto

Ejercicio-AD/
│
├── src/
│ ├── Main.java
│ ├── Profesor.java
│ └── GestorSustitucionesAlt.java
│
├── data/
│ ├── horarios.csv # Horarios de cada profesor
│ └── sustituciones.csv # Registro de sustituciones realizadas


---

## 📝 Funcionalidades

1. **Detectar y asignar sustituto**
   - Introduces el profesor ausente, día y hora.
   - El sistema busca profesores libres en esa franja.
   - Los posibles sustitutos se muestran ordenados de menor a mayor número de sustituciones acumuladas.
   - Puedes confirmar la asignación del sustituto elegido.

2. **Consultar sustituciones de un profesor**
   - Permite conocer cuántas sustituciones ha realizado un profesor en total.

3. **Mostrar ranking de sustituciones**
   - Muestra los profesores ordenados de mayor a menor número de sustituciones acumuladas.

4. **Persistencia de datos**
   - Cada sustitución confirmada se guarda en `sustituciones.csv`.
   - Se mantiene un registro acumulativo de las sustituciones por profesor.

---

## ⚙️ Cómo ejecutar el programa

1. Asegúrate de tener **Java** instalado.
2. Abre la terminal o Git Bash y sitúate en la carpeta raíz del proyecto:
   ```bash
   cd "C:/Ejercicio AD"
