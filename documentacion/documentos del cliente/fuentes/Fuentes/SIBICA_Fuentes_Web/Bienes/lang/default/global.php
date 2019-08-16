<?php
define('BIENES_MOD','Sistema de Información de Bienes Inmuebles de Cali (SIBICA)');
define('BIENES_MODULO','Sistema de Información de Bienes Inmuebles de Cali (SIBICA)');

// Coordenadas y zoom para cargar el mapa en la ciudad de Cali
define('BIENES_LATITUD', 3.44840579768201);
define('BIENES_LONGITUD', -76.5269385953114);
define('BIENES_ZOOM', 13);
define('BIENES_RESUELTO', 'resuelto');

// Tabla Información del Predio
define('BIENES_MSG_USUARIO', 'Mensaje ciudadano');
define('BIENES_PREDIAL_TERRENO', 'Predial terreno');
define('BIENES_PREDIAL_CONSTRUCCION', 'Predial edificación');
define('BIENES_DIRECCION', 'Dirección');
define('BIENES_TIPO_BIEN', 'Tipo bien');
define('BIENES_NOMBRE_PROYECTO', 'Nombre proyecto');
define('BIENES_NOMBRE_AREA_CEDIDA', 'Nombre área cedida');
define('BIENES_NOMBRE_CONSTRUCCION', 'Nombre edificación');
define('BIENES_AREA_EDIFICADA', 'Área edificación');
define('BIENES_DIRECCION_CONSTRUCCION', 'Dirección edificación');
define('BIENES_MATRICULA', 'Matricula');
define('BIENES_AREA_CEDIDA', 'Área cesión');
define('BIENES_NOMBRE_COMUN', 'Nombre común');
// Extras
define('BIENES_CODIGONAL', 'Código nacional');
define('BIENES_NUM_ACTIVOFIJO', ' Número activo fijo');
define('BIENES_ESTA_MODA', 'Estrato');
define('BIENES_CEDENTE', 'Cedente');
define('BIENES_NOMBRE_CB', 'Nombre CB');
define('BIENES_ORFEO_CB', 'Orfeo CB');
define('BIENES_NOMBRE_TB', 'Tipo bien');
define('BIENES_NOMBRE_TU', 'Nombre tipo u');
define('BIENES_NOMBRE_MADQ', 'Nombre madq');
define('BIENES_LIND_NORTE', 'Lind norte');
define('BIENES_LIND_SUR', 'Lind sur');
define('BIENES_LIND_ESTE', 'Lind este');
define('BIENES_LIND_OESTE', 'Lind oeste');
define('BIENES_LIND_ADIC', 'Lind adición');
define('BIENES_ESTADO_PREDIO', 'Estado predio');
define('BIENES_NOMBRE_TIPO', 'Nombre tipo documento');
define('BIENES_NUMDOC', 'Número documento');
define('BIENES_FECHA_DOC', 'Fecha documento');
define('BIENES_CONTRATO_NOMBRE', 'Nombre contrato');
define('BIENES_CONTRATO_NUMERO', 'Número contrato');
define('BIENES_CONTRATO_AREA_ENTREGADA', 'Área entregada contrato');
define('BIENES_CONTRATO_FEC_INI', 'Fecha inicio contrato');
define('BIENES_CONTRATO_FEC_FIN', 'Fecha finaliza contrato');
define('BIENES_CONTRATO_ESTADO', 'Estado contrato');
define('BIENES_CONTRATO_LIND_NORTE', 'Lind contrato norte');
define('BIENES_CONTRATO_LIND_SUR', 'Lind contrato sur');
define('BIENES_CONTRATO_LIND_ESTE', 'Lind contrato este');
define('BIENES_CONTRATO_LIND_OESTE', 'Lind contrato oeste');
define('BIENES_CONTRATO_LIND_ADIC', 'Lind contrato adición');
define('BIENES_TERCERO', 'Tercero');
define('BIENES_NUMPISOS', 'Número de pisos construcción');
define('BIENES_ACTFIJO_CONSTRUCCION', 'Activo fijo construcción');
define('BIENES_NOMBRE_DEPENDENCIA', 'Nombre dependencia');
define('BIENES_PATH_FOTOS', 'Path fotos');
define('BIENES_PATH_ARCHIVOS', 'Path archivos');
define('BIENES_OBSERVACION_FECHA', 'Fecha Observación');
define('BIENES_OBSERVACION', 'Observación');
define('BIENES_USUARIO_NOMBRE', 'Nombre usuario');
define('BIENES_USUARIO_APELLIDO', 'Apellido usuario');
define('BIENES_URL1', 'URL 1');
define('BIENES_URL2', 'URL 2');
define('BIENES_URL3', 'URL 3');

// Tabla Búsqueda de predios e InfoWindow
define('BIENES_COMUNA', 'Comuna');
define('BIENES_BARRIO', 'Barrio');
define('BIENES_PREDIAL', 'Predial');
define('BIENES_TIPO', 'Tipo');
define('BIENES_GSV', 'GSV');
define('BIENES_BUSCAR', 'Buscar Predios');
define('BIENES_BUSCAR_AYUDA', "- Lugares: Pance, San Fernando, Río Cali.\n
- Direcciones: A:Avenida, C:Calle, K:Carrera, P:Pasaje, T:Transversal, D:Diagonal, N:Norte, O:Oeste\n
* No utilizar Norte (N) en las avenidas, Guía nomenclatura\n
A 2 BIS # 24A N - 25   A 4 O # 6 O - 170
C 56A # 42C2 - 35      K 2 N # 22 - 103
P 7F # 66 - 24         T 2A # 1C - 14
D 28C # 43A - 41       A 2A con C 12 N (Cruces ejes viales)\n
Las direcciones ubicadas son aproximadas.");

// Opciones combobox en formulario de búsqueda
define('BIENES_PREDIO_DIRECCION', 'Dirección Predio');
define('BIENES_PREDIO_MATRICULA', 'Matricula inmobiliaria');
define('BIENES_PREDIO_NUMPREDIAL', 'Número predial');
define('BIENES_PREDIO_BARRIO', 'Barrio');
define('BIENES_PREDIO_COMUNA', 'Comuna');

// Titles, alts y placeholder de la vista
define('BIENES_DATO', 'Dato a buscar');
define('BIENES_TITLE_OCULTAR', 'Ocultar búsqueda');
define('BIENES_TITLE_MOSTRAR', 'Mostrar búsqueda');
define('BIENES_OCULTAR', '>>');
define('BIENES_MOSTRAR', '<<');
define('BIENES_LABEL_BUSQUEDA', 'Búsqueda por:');
define('BIENES_ALT_MAS', 'Ver más');
define('BIENES_ALT_GSV', 'Google Street View');
define('BIENES_ALT_ORFEO', 'Reportar irregularidad');

// Mensajes de return
define('BIENES_OK', '');
define('BIENES_NOTOK', 'Sin información');
define('BIENES_NOCONSTRUCCION', 'No hay construcciones para mostrar');
define('BIENES_NOAMOBLAMIENTO', 'No hay amoblamientos para mostrar');

// Advertencias que se muestran al usuario al entrar a la APP
define('BIENES_ADVERTENCIA', "Apreciado usuario, la información aquí consignada NO corresponde
                              a la totalidad de predios de propiedad del municipio de Santiago de Cali.
                              Se irá actualizando periódicamente en el Geovisor SIBICA, de
                              acuerdo a la verificación del inventario de Bienes Inmuebles
                              de propiedad del municipio de Santiago de Cali.");

// Formulario login y credenciales LDAP
define('BIENES_LOGIN', 'INICIAR SESIÓN');
define('BIENES_LOGOUT', 'LOGOUT SISTEMA');
define('BIENES_USUARIO', 'Usuario: ');
define('BIENES_PASSWORD', 'Password: ');
define('BIENES_CONECTAR', 'Login');
define('BIENES_LOGINOK', 'Bienvenido al sistema');
define('BIENES_LOGINNOTOK', 'Las credenciales de conexión están erradas');
define('BIENES_LOGOUTOK', 'Se ha desconectado del sistema LDAP');
define('BIENES_TOKENKEY', 'aM*gd/lF2P/xd*');
define('BIENES_TOKENTIME', (60*60*24*365));
define('BIENES_LDAP_HOST', '172.18.1.51');
define('BIENES_LDAP_PORT', 389);
define('BIENES_LDAP_DOMINIO', 'alcaldiacali.local');
define('BIENES_LDAP_USUARIO', 'NexCaract');
define('BIENES_LDAP_PASSWORD', '+NexCar2016+');
define('BIENES_LDAP_ERROR', 'No ha sido posible conectarse al servidor para realizar la validación del usuario');
define('BIENES_LDAP_LOGINNOTOK', 'Conexión errada al sistema LDAP');
define('BIENES_LDAP_LOGINOK', 'Se ha conectado al sistema satisfactoriamente');
define('BIENES_LDAP_LOGOUT', 'Ha salido del sistema');

// Formulario ORFEO
define('BIENES_ORFEO', 'Reportar irregularidad');
define('BIENES_ORFEO_TIPO', 'Tipo de reporte');
define('BIENES_ORFEO_FOTO', 'Adjuntar fotografía');
define('BIENES_ORFEO_DIRECCION', 'Dirección');
define('BIENES_ORFEO_NUMPREDIAL', 'Número predial');
define('BIENES_ORFEO_OPCIONALES', 'DATOS OPCIONALES');
define('BIENES_ORFEO_NOMBRE', 'Nombre');
define('BIENES_ORFEO_CEDULA', 'C&eacute;dula');
define('BIENES_ORFEO_CORREO', 'Correo');
define('BIENES_ORFEO_TELEFONO', 'Teléfono');
define('BIENES_ORFEO_ENVIAR', 'Enviar');
define('BIENES_ORFEO_REQUERIDO', 'El campo xxx es requerido');
define('BIENES_ORFEO_IMAGEN', 'El tipo de archivo que intenta subir no es una imagen png o jpg');
define('BIENES_ORFEO_IMAGEN2', 'No se pudo anexar la fotograf&iacute;a. Por favor especifique el archivo de la imagen.');
define('BIENES_ORFEO_IMAGEN3', 'No se pudo anexar la fotograf&iacute;a');
define('BIENES_ORFEO_IMAGEN4', "No se pudo subir la imagen intente de nuevo m&aacute;s tarde,\n o env&iacute;e el reporte sin imagen");
define('BIENES_ORFEO_MAYOR', 'Por favor no introduzca más de 255 caracteres en el campo xxx');
define('BIENES_ORFEO_EMAIL', 'Debe ingresar un formato de correo valido');
define('BIENES_ORFEO_NUMTEL', 'Debe ingresar un número telefónico valido');
define('BIENES_ORFEO_MAXFIELD', 255);
define('BIENES_ORFEO_ERROR', 'No se guardó la información correctamente ');
define('BIENES_ORFEO_OK', 'El reporte se ha generado satisfactoriamente');
define('BIENES_ORFEO_REPORTADO', 'El predio ya fue reportado previamente');
define('BIENES_ORFEO_WSDL', 'http://172.18.26.33/webServ/wsRadicado2p.php?wsdl');
define('BIENES_ORFEO_CAMPO_DOCUMENTO', '0'); // Tipo de Documento.   Asociado a la tabla tipo_doc_identificacion con datos (0-Cedula de Ciudadania, 
                                             // 1-Tarjeta de Identidad, 2-Cedula de Extranjería, 3-Pasaporte, 4-Nit  ,5-NUIR)
define('BIENES_ORFEO_CAMPO_TIPOEMP', '1'); // Si es Ciudadano 1, OEM 2, Entidad 3
define('BIENES_ORFEO_CAMPO_IDCONT', '1'); 
define('BIENES_ORFEO_CAMPO_IDPAIS', '170'); // Codigo del Pais donde se encuentra el remitente y/o Destino
define('BIENES_ORFEO_CAMPO_CODEP', '76'); // Codigo del Departamento
define('BIENES_ORFEO_CAMPO_MUNI', '1'); // Codigo del municipio
define('BIENES_ORFEO_CAMPO_OBSERVACION', 'FRAUDE EN PREDIO, ');
define('BIENES_ORFEO_CAMPO_MEDIOREC', '3'); // Medio de recepcion /envio.  Este codigoes según la tabla existente en orfeo “MEDIO_RECEPCION” (1-correo, 2-fax, 3-Internet, 4-mail... )
define('BIENES_ORFEO_CAMPO_ANEXOS', '7'); // Descripcion Anexos
define('BIENES_ORFEO_CAMPO_CODDEPEN', '4137010'); // Codigo Dependencia Radicadora. De este codigo extraera la secuencia del radicado.
define('BIENES_ORFEO_CAMPO_TIPORAD', '2'); // Tipo de Radicado 2 Entrada, 3 Salida
define('BIENES_ORFEO_CAMPO_CUENTA', '4'); // Cuenta Interna/Oficion o algun codigo identificador del documento.
define('BIENES_ORFEO_CAMPO_DEPENDENCIA', '4137010'); // Dependencia Destino
define('BIENES_ORFEO_CAMPO_TIPOREM', '3'); // Tipo de remitente. Asociado con la tabla tipo_remitente (en la tabla de uds 0-Entidades,1-Otras empresas,
                                           // 2-Persona natural,3-Predio,5-,O-Oros,6-Funcionario)
define('BIENES_ORFEO_CAMPO_TIPOREMITENTE', '671'); // Tipo de remitente
define('BIENES_ORFEO_CAMPO_CODDIRRAD', '0'); // Codigo de carpeta a la cual se quiere enviar el radicado.  0 – La bandeja de entrada. (Depende tambien de si es personal o general)
define('BIENES_ORFEO_CAMPO_CODDIR', '0'); //  Codigo tipo de carpeta.  0- Carpetas generales (Entrada, salida, internas)   1- Codigo de carpetas personales.
define('BIENES_ORFEO_CAMPO_DOCRAD', '66838856'); // Documento de identificacion de la persona radicadora.  Esta debe existir en OrfeoGPL.
define('BIENES_ORFEO_CAMPO_OTRO', 'Informativa'); // Tipo de solicitud
define('BIENES_ORFEO_CAMPO_DESCRIMG', 'Guardado por Bienes inmuebles'); // Tipo de solicitud
define('BIENES_ORFEO_CAMPO_CEDULADEFECTO', '93939393');

// Formulario filtro
define('BIENES_LABEL_FILTRAR', 'Mostrar predios tipo');
define('BIENES_FILTRAR', 'Filtrar');
define('BIENES_FILTRO_CONST', 'Construcciones');
define('BIENES_FILTRO_AMOBL1', 'Amoblamientos I');
define('BIENES_FILTRO_AMOBL2', 'Amoblamientos II');
define('BIENES_FILTRO_AMOBL3', 'Amoblamientos III');
?>