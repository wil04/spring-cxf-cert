## Instalación y configuración de Wildfly v10.1.0.Final

Descargar la versión 10.1.0.Final de la siguiente dirección: http://wildfly.org/downloads/

Descomprimir el archivo descargado en un directorio de instalación, llamaremos a este directorio de descompresión:
```sh
WILDFLY_HOME
```

Iniciar el servidor de aplicaciones ejecutando el siguiente comando:
```sh
WILDFLY_HOME\bin\standalone.bat
```

Comprobar que el servidor de aplicaciones se ha iniciado correctamente, ingresar a:
```sh
http://127.0.0.1:8080
```

Adicionar un usuario administrador ejecutando:
```sh
WILDFLY_HOME\bin\add-user.bat
```
proporcionar los parámetros solicitados.

Ingresar a la consola de administración:
```sh
http://127.0.0.1:9090
```
ingresar los parámetros anteriormente creados.

## Despliegue del proyecto de servicio web de ciclos

Importar el proyecto con eclipse y posteriormente realizar la instalación del proyecto:
```sh
mvn install
```

Generar el archivo .war con eclipse y posteriormente realizar el despliegue en Wildfly.

## Creación de los certificados con java Keytool

Generar el almacen de llaves:

```sh
keytool -genkey -alias almacen -keyalg RSA -keystore almacen.jks -keysize 2048
```

Exportar el certificado con la llave pública:
```sh
keytool -export -alias almacen -file publica.crt -keystore almacen.jks
```
