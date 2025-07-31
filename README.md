# Mi Proyecto Java

Este proyecto es una aplicación Java que permite a los usuarios iniciar sesión, registrarse y gestionar su información personal y académica. La aplicación está estructurada en varias vistas y utiliza una conexión a base de datos a través de JDBC.

## Estructura del Proyecto

```
mi-proyecto-java
├── src
│   ├── Main.java                # Punto de entrada de la aplicación.
│   ├── views                    # Contiene las vistas de la aplicación.
│   │   ├── LoginView.java       # Interfaz de inicio de sesión.
│   │   ├── SignUpView.java      # Registro de nuevos usuarios.
│   │   ├── NavBarPanel.java      # Barra de navegación reutilizable.
│   │   ├── InfoPersonalView.java # Vista de información personal.
│   │   ├── InfoAcademicaView.java# Vista de información académica.
│   │   └── RadicarView.java     # Vista para radicar solicitudes.
│   ├── utils                    # Contiene utilidades del proyecto.
│   │   └── DatabaseConnection.java# Clase para conexión a la base de datos.
│   └── models                   # Contiene los modelos de datos.
│       ├── Usuario.java         # Modelo de usuario.
│       └── Estudiante.java      # Modelo de estudiante.
├── # Sistema Académico UDC - Migración Web



```
mi-proyecto-java/
├── pom.xml                              # ✅ Configuración Maven Web
├── README.md                            # ✅ Documentación actualizada
│
├── src/main/
│   ├── java/                           # Código Java
│   │   ├── models/                     # ✅ PRESERVADO
│   │   │   ├── Usuario.java           # Con constructor por defecto para web
│   │   │   └── Estudiante.java        # Herencia preservada
│   │   ├── utils/                      # ✅ PRESERVADO + MEJORADO  
│   │   │   └── DatabaseConnection.java # + método obtenerUsuarioPorUsername()
│   │   ├── servlets/                   # ✅ NUEVO - Lógica web
│   │   │   ├── LoginServlet.java      # GET/POST para autenticación
│   │   │   ├── MainServlet.java       # Dashboard principal
│   │   │   └── LogoutServlet.java     # Cierre de sesión
│   │   └── filters/                    # ✅ Configurado
│   │       └── CharacterEncodingFilter.java # UTF-8
│   │
│   └── webapp/                         # Contenido web
│       ├── index.jsp                   # ✅ Página de bienvenida
│       └── WEB-INF/
│           ├── web.xml                 # ✅ Servlets configurados
│           └── views/
│               ├── login.jsp           # ✅ NUEVO - Réplica exacta de LoginView
│               ├── main.jsp            # ✅ NUEVO - Dashboard post-login
│               └── error/              # ✅ Páginas de error
│                   ├── 404.jsp
│                   └── 500.jsp
│
└── src/ (original)                     # 🔄 Mantener durante transición
    ├── Main.java                       # ⚠️  Será reemplazado en Fase 3
    ├── models/                         # ✅ Migrado
    ├── utils/                          # ✅ Migrado y mejorado
    └── views/
        ├── LoginView.java              # ✅ MIGRADO a login.jsp + LoginServlet
        ├── MainView.java               # ⏳ Pendiente Fase 3
        └── [otros views]               # ⏳ Pendientes Fases 3-5
```

### 🔄 Funcionalidades Web Implementadas

#### ✅ **LoginServlet.java - Métodos GET y POST**
```java
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    
    // GET: Mostrar formulario (equivale a mostrar LoginView)
    protected void doGet(...) {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(...);
    }
    
    // POST: Procesar login (equivale al ActionListener del botón)
    protected void doPost(...) {
        // PRESERVADA: Misma lógica de validación que LoginView
        DatabaseConnection db = new DatabaseConnection();
        boolean valido = db.validarUsuario(email, password);
        // Sesiones HTTP en lugar de variables estáticas
    }
}
```


## Requisitos

- JDK 11 o superior
- Dependencias de JDBC para la conexión a la base de datos

## Instrucciones de Configuración

1. Clona el repositorio en tu máquina local.
2. Navega a la carpeta del proyecto.
3. Asegúrate de tener el JDK instalado y configurado en tu sistema.
4. Compila el proyecto utilizando el comando `javac src/*.java src/views/*.java src/utils/*.java src/models/*.java`.
5. Ejecuta la aplicación con el comando `java src/Main`.

## Uso

- Al iniciar la aplicación, se presentará la vista de inicio de sesión.
- Los usuarios pueden registrarse utilizando la vista de registro.
- Después de iniciar sesión, los usuarios pueden acceder a su información personal y académica, así como radicar solicitudes.

## Contribuciones

Las contribuciones son bienvenidas. Si deseas contribuir, por favor abre un issue o envía un pull request.

## Licencia

Este proyecto está bajo la Licencia MIT.
