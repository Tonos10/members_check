# 🎯 Guía rápida: Ejecutan tu aplicación con diseño moderno

## ✅ Paso 1: Reconstruir el proyecto en IntelliJ

1. Abre **IntelliJ IDEA**
2. Haz clic en: **Build → Rebuild Project**
3. Espera a que termine (verás la barra de progreso abajo)
4. Verifica que no haya errores en la pestaña "Build" (pueden haber algunos warnings, es normal)

---

## ✅ Paso 2: Ejecutar la aplicación

1. Haz clic en: **Run → Run 'App'** (o presiona **Shift + F10**)
2. Debería abrirse una ventana JavaFX de 800x600 con tu aplicación

---

## 🎨 Qué verás en pantalla

### Lado izquierdo (Formulario):
```
┌─────────────────────────┐
│  Nuevo Alumno           │
├─────────────────────────┤
│ Nombre:  [_____________] │
│ Cinta:   [_____________] │
│ Fecha:   [___/___/_____] │
│                          │
│ [ Guardar ]              │
│ [Actualizar] [ Eliminar] │
│ [ Limpiar  ]             │
└─────────────────────────┘
```

### Lado derecho (Tabla):
```
┌────────────────────────────┐
│ Listado de Alumnos         │
├─────┬─────────┬─────┬──────┤
│ ID  │ Nombre  │Cinta│Fecha │
├─────┼─────────┼─────┼──────┤
│ 1   │ Carlos  │Azul │2024… │
│ 2   │ María   │Roja │2024… │
└─────┴─────────┴─────┴──────┘
```

---

## 🖱️ Funcionalidades a probar

### 1️⃣ **Crear un alumno nuevo**
- Completa los tres campos (Nombre, Cinta, Fecha)
- Haz clic en **"Guardar"** (botón azul)
- Debería aparecer una alerta verde de "Éxito"
- El alumno se agrega a la tabla

### 2️⃣ **Seleccionar un alumno de la tabla**
- Haz clic en cualquier fila de la tabla
- ✨ **Magia**: Los datos se cargan automáticamente en el formulario
- La fila se resalta en azul claro
- El botón "Actualizar" y "Eliminar" se activan

### 3️⃣ **Limpiar el formulario**
- Con un alumno seleccionado, haz clic en **"Limpiar"** (botón gris)
- Los campos se vacían
- La selección de la tabla se deselecciona
- Los botones vuelven a su estado inicial

### 4️⃣ **Actualizar un alumno**
- Selecciona un alumno (haz clic en la tabla)
- Modifica los datos en el formulario
- Haz clic en **"Actualizar"** (botón verde)
- Por ahora, ves un mensaje informativo
- *Nota: La conexión al DAO se hará en la próxima fase*

### 5️⃣ **Eliminar un alumno**
- Selecciona un alumno
- Haz clic en **"Eliminar"** (botón rojo)
- Aparece un cuadro de confirmación
- Si confirmas ("OK"), ves un mensaje informativo
- *Nota: La eliminación real se hará en la próxima fase*

---

## 🎨 Estilos que verás

### Botones:
- ✅ **Guardar** (azul) → Guardar nuevos registros
- 🟢 **Actualizar** (verde) → Editar seleccionado
- 🔴 **Eliminar** (rojo) → Borrar seleccionado  
- ⚪ **Limpiar** (gris) → Limpiar formulario

### Efectos visuales:
- 📌 Sombra bajo los botones
- 🎯 Color más oscuro al pasar el ratón (hover)
- 💫 Borde azul en campos al enfocar
- 🟦 Fila seleccionada en azul claro

### Tabla:
- 🔲 Sin líneas grises feas (diseño limpio)
- ⚪ Fondo blanco
- 🔵 Fila seleccionada destacada

---

## ❓ Preguntas frecuentes

### P: ¿Por qué veo warnings de "Cannot resolve symbol"?
**R**: Son falsos positivos del IDE. Desaparecerán al hacer "Rebuild Project".

### P: ¿Los botones Actualizar y Eliminar funcionan?
**R**: Sí, la UI funciona (validan, muestran mensajes). La lógica de persistencia en BD se conectará en la próxima fase.

### P: ¿Puedo editar el CSS?
**R**: Sí, está en `src/main/resources/css/style.css`. Cualquier cambio se verá al reiniciar la app.

### P: ¿El fichero CSS está bien enlazado?
**R**: Sí, está declarado en `Dashboard.fxml` con `<URL value="@../css/style.css"/>`.

---

## 🔧 Solución de problemas

| Problema | Solución |
|----------|----------|
| La app no inicia | Compila con `Build → Rebuild Project` |
| No se ven los estilos CSS | Reinicia la app y verifica que el CSS esté en `src/main/resources/css/` |
| Los botones nuevos no funcionan | Reconstruye y espera a que se recompile el controlador |
| La tabla está vacía | Crea al menos un alumno con "Guardar" |
| Los campos no se rellenan al seleccionar | Asegúrate de hacer clic directamente en una fila de la tabla |

---

## 📊 Estado del desarrollo

```
✅ Diseño moderno implementado (Material Design)
✅ UI responsive y coloreada
✅ Selección automática de filas
✅ Botones Actualizar y Eliminar lógica UI
✅ Validaciones en formularios
✅ Mensajes de confirmación

⏳ Próximo paso: Conectar DAO para Actualizar/Eliminar en BD
```

---

## 📝 Estructura de archivos

```
src/main/resources/
├── css/
│   └── style.css ← Nuevo archivo de estilos
├── view/
│   └── Dashboard.fxml ← Modificado con botones y CSS
└── ...

src/main/java/
├── controller/
│   └── DashboardController.java ← Modificado con lógica de selección
└── ...
```

---

¡Tu app ahora tiene un **diseño profesional y moderno**! 🎉
Próximo objetivo: **Conectar la lógica de actualizar/eliminar al DAO**. ¿Quieres que lo haga? 🚀


