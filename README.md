Se modifica proyetco de sqlite y se agrega Content Providers con los requisitos solicitados.Codigo comentado y tambien se muestra el archivo UserProvider.kt y todos los archivos relacionados con el ContentProvider, tambien se puede ver el archivo 
AndroidManifest.xml para ver la configuracion del Provider:
<provider
android:name=".UserProvider"
android:authorities="es.ua.eps.sqlite.provider"
android:exported="true"
android:grantUriPermissions="true"/>
