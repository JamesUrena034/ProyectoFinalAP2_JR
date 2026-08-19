# AlquilaFest 🎉🪑

## ¿Qué es AlquilaFest?

AlquilaFest es una solución móvil integral diseñada para la gestión y control de alquiler de equipos para eventos. La aplicación permite administrar de manera eficiente el inventario de productos como sillas, mesas, iluminación, sonido y carpas, con control de stock, disponibilidad por fechas y registro de pedidos, optimizando la operatividad de empresas de alquiler de equipos para eventos.

## ¿Cómo funciona?

La aplicación utiliza Firebase como backend en tiempo real:

1. **Autenticación:** Los usuarios inician sesión con su cuenta de Google de forma segura mediante Firebase Authentication.
2. **Roles:** El sistema detecta automáticamente si el usuario es Administrador o Usuario regular, mostrando las opciones correspondientes a cada rol.
3. **Gestión de Productos:** El administrador puede crear, editar y eliminar productos organizados por categorías, con imágenes, precios y stock.
4. **Reservas por Fechas:** El usuario selecciona las fechas de inicio y fin de la renta, y el sistema valida automáticamente que el producto esté disponible en esas fechas.
5. **Métodos de Pago:** La app incluye pago con Tarjeta de Crédito/Débito o Efectivo al momento de confirmar el pedido.
6. **Control de Stock:** Al confirmar un pedido, el stock de cada producto se actualiza automáticamente en tiempo real.

## Tecnologías Utilizadas

* **Lenguaje:** [Kotlin](https://kotlinlang.org/)
* **Interfaz de Usuario:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material Design 3)
* **Autenticación:** [Firebase Authentication](https://firebase.google.com/docs/auth) (Google Sign-In)
* **Navegación:** Navigation 3
* **Inyección de Dependencias:** Hilt / Dagger
* **Arquitectura:** Clean Architecture + MVI (Model-View-Intent)

## Funcionalidades

### Administrador
-  Crear, editar y eliminar categorías
-  Crear, editar y eliminar productos con imagen, precio y stock
-  Ver todos los pedidos de todos los usuarios
-  Cambiar el estado de los pedidos (Pendiente, Completado, Cancelado)
  
## Usuario
-  Ver productos por categoría
-  Buscar y filtrar productos desde el Home
-  Agregar productos al carrito con cantidad
-  Seleccionar fechas de renta con validación de disponibilidad
-  Pagar con Tarjeta o Efectivo
-  Ver historial de sus pedidos

## Video de Presentación en Youtube

Puedes ver el funcionamiento detallado de la aplicación en el siguiente enlace:
https://youtu.be/RUdv7Gp_avg


## Imágenes de la App
<img width="377" height="827" alt="WhatsApp Image 2026-08-18 at 6 27 17 PM" src="https://github.com/user-attachments/assets/03a0a624-5020-436b-b288-10bbedfaebb7" />
<img width="372" height="713" alt="WhatsApp Image 2026-08-19 at 1 14 35 AM" src="https://github.com/user-attachments/assets/90dba5ba-42af-4ed1-a43f-3900edd9a936" />
<img width="370" height="766" alt="WhatsApp Image 2026-08-19 at 1 14 35 AM (1)" src="https://github.com/user-attachments/assets/02785aab-dfc5-42a2-ba12-409aeef63d40" />
<img width="376" height="755" alt="WhatsApp Image 2026-08-19 at 1 14 36 AM" src="https://github.com/user-attachments/assets/9e160d8a-d2cb-4141-9789-34377d40396a" />
<img width="371" height="819" alt="WhatsApp Image 2026-08-19 at 1 14 36 AM (4)" src="https://github.com/user-attachments/assets/0e3b03b4-305d-403e-a951-704507da8dc8" />
<img width="381" height="772" alt="WhatsApp Image 2026-08-19 at 1 14 36 AM (3)" src="https://github.com/user-attachments/assets/6270dd85-ecb8-43f5-92c4-10ad1590d2c9" />
<img width="374" height="829" alt="WhatsApp Image 2026-08-19 at 1 14 36 AM (2)" src="https://github.com/user-attachments/assets/6940a24b-5a59-416c-b965-ba948e2c49f2" />
<img width="371" height="828" alt="WhatsApp Image 2026-08-19 at 1 14 36 AM (1)" src="https://github.com/user-attachments/assets/98a12bba-83aa-4828-a21e-534b7e4f549b" />



## Desarrollado por

1-**Ronnel De La Cruz** 
2-**James Ureña**

Estudiantes de Ingeniería de Sistemas — UCNE 🇩🇴
