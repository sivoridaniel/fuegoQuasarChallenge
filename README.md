# Fuego Quasar Challenge

## Algoritmos utilizados

* Algoritmo calculo distancia

Para el calculo de coordenadas del emisor del mensaje se utilizo el método de **Trilateración**. Esta técnica nos permite calcular las coordenadas de un punto,
conociendo 3 puntos de referencia y sus distancias respecto al punto por el cual queremos obtener sus coordenadas.  
Estos puntos de referencia son los satelites Kenobi, Skywalker y Sato los cuales se encuentran en las coordenadas P1=(500,-200), P2=(100,-100) y P3=(500,100) respectivamente.
Estas coodenadas se encuentran en el archivo de configuración application.properties.

Para el algoritmo se toman 3 esferas con centros en P1, P2 y P3. Luego armamos el sistema de ecuaciones y hacemos los calculos pertinentes para obtener las coordenadas
del punto emisor que queremos obtener.

**_Sistema de ecuaciones_**

1) r1^2 = x^2+y^2+z^2
2) r2^2 = (x-d)^2+y^2+z^2
3) r3^2 = (x-i)^2+(y-j)^2+z^2

2) - 1) x = (r2^2 - r1^2 + d^2) / 2d

Reemplazo x en ecuación (1):

y^2 + z^2 = r1^2 - ( (r2^2 - r1^2 + d^2)^2 ) / 4d^2

Reemplazo y^2 + z^2 en ecuación (3) llegando a la ecucación:

y = ((r1^2 - r3^2 + i^2 + j^2) / 2j) - (i/j)x

De esta manera las ecuaciones para el calculo de coordenadas del punto del emisor son:

x = (r2^2 - r1^2 + d^2) / 2d
y = ((r1^2 - r3^2 + i^2 + j^2) / 2j) - (i/j)((r2^2 - r1^2 + d^2) / 2d)

r1, r2 y r3: Distancias del satelite al emisor.
d: Distancia entre el punto P1 y P2 (sqrt((x2-x1)^2 + (y2-y1)^2))


* Algoritmo interpretación de mensaje

Para la interpretación del mensaje se deberá contar con la información recibida por los 3 satelites. En caso de que no se reciba la info de al menos un satelite, no se
podrás obtener ni la distancia al emisor ni el mensaje interpretado, devolviendo en el body **"codigo":"QUAS-005", "message":"Interpretation failure" (Http Status 404)**.
Para la interpretación del mensaje que llega del emisor a los satelites, se itera los mensajes recibidos en orden inverso y primeramente se valida que se pueda interpretar
el mensaje viendo si la ultima palabra recibida en cada satelite es la misma. Puede pasar que al menos un satelite reciba la ultima palabra y en ese caso se podrá interpretar
dicho mensaje. En cambio si los tres satelites no pueden interpretar la ultima palabra del mensaje, se devolverá el error 404.

Barriendo los mensajes en orden inverso, se iran comparando los mensajes recibidos en cada satelite construyendo el mensaje secreto. Esta solución garantiza
que los mensajes recibidos en el bufer de cada satelite se recorreran una sola vez para su interpretación.

## Almacenación de datos recibidos por los satelites

Para la parte 3 del problema QUASAR se armo un QasCache el cual permite almacenar la información recibida por cada satelite. Esta info se almacena en un HashMap cuya clave
es el nombre del satelite y al momento de recibir los siguientes post se consulta en este mapa cuales son las claves (satelites) cuya info expiro. El tiempo de expiración
es de 80 segundos, por lo cual si los post de los 3 satelites no se realizan dentro de dicho intervalo de tiempo, el mensaje obtenido por el servicio POST topsecret_split/{nombre_satelite}
será **"codigo":"QUAS-005", "message":"Interpretation failure" (Http Status 404)*.

## Prueba de Aplicación

Para probar la aplicación se puede importar el siguiente link de postman:

https://www.getpostman.com/collections/6a5042c1f3f9b2e5e950

Aquí se encuentran los servicios api rest del challenge.

Esos servicios se encuentran desplegados en el PAAS Heroku. Host: quasar-app-2020.herokuapp.com.


