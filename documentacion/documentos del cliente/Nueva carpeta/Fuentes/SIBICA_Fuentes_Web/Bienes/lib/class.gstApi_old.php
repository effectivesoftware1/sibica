<?php

/**
 * gstBienes
 *
 * @package Bienes
 * @subpackage Bienes
 * @version 2016/12/15 17:30
 */
use Tribunet\configBlade as View;
use Firebase\JWT\JWT;

/**
 * Clase de gestion encargada de los WebServices.
 *
 * Esta clase contiene los métodos que mostrará la información de los predios, 
 * los detalles cartográficos de cada predio en la ciudad de Cali, referenciar
 * un predio por medio de un marker de google maps, el logueo al sistema LDAP
 * y el reporte del predio al sistema Orfeo
 *
 * @package Bienes
 * @subpackage Api
 * @copyright Copyright {@link http://www.nexura.com nexura} 2009
 * @version Bienes v1.0
 *
 * @author Guillermo Alfonso Morales <gmorales@nexura.com>
 */
class gstApi {

    /**
     * Entrega de polígonos
     * 
     * Se cargan los polígonos extraidos de la vista externa o interna, 
     * dependiendo del tipo de usuario que esté cargan el mapa
     * 
     * @param string $vista Extraer datos de la vista externa o interna
     * 
     * @return array En caso de regresar datos, la clave success será 1
     */
    public function mostrarPredios($vista, $coordenadas) {
        $coords = explode(',', trim($coordenadas, ''));

        $img = 'mod/Bienes/img/';

        $objCx = bd::getCx('bdPg1');

        $sql = "SELECT oid AS gid, 
                       ST_X(ST_CENTROID((ST_DumpPoints(the_geom)).geom)) AS lng,
                       ST_Y(ST_CENTROID((ST_DumpPoints(the_geom)).geom)) AS lat,
                       COALESCE(direccion_p, '') AS direccion, 
                       COALESCE(nombre_tb, '') AS nombre_tb, 
                       COALESCE(identifica_p, '') AS predial, 
                       COALESCE(nombrecomun_p, '') AS nombre_comun, 
                       COALESCE(predial_edificacion_const, '') AS predial2, 
                       COALESCE(mat_inmob_p, '') AS matricula,
                       CASE id_capa
                           WHEN 1 THEN '#FF0000'
                           WHEN 2 THEN '#FF8000'
                           WHEN 3 THEN '#800080'
                           WHEN 4 THEN '#FFFF00'
                           WHEN 5 THEN '#6A6A6A'
                           WHEN 6 THEN '#00FF00'
                           WHEN 7 THEN '#FA045D'
                       END AS id_capa
                FROM vw_app_$vista
                WHERE ST_ASTEXT(the_geom) ILIKE '%" . substr($coords[0], 0, 5) . "%' AND 
                      ST_ASTEXT(the_geom) ILIKE '%" . substr($coords[1], 0, 6) . "%'
                ORDER BY 1";
        $rs = $objCx->execute($sql);

        if (!$rs) {
            return array('success' => 0, 'msg' => BIENES_NOTOK);
        }

        $poligono = $rs->fields['gid'];
        $predial = $rs->fields['predial'];
        /*
         * Si el predio tiene un reporte por fraude, se pintará un marker 
         * diferente en éste
         */
        $sql = "SELECT COALESCE(tipo_reporte, '') AS tipo_rpt, 
                       COALESCE(estado_reporte, '') AS estado_rpt
                FROM reporte_predio
                WHERE predial ILIKE '" . $rs->fields['predial'] . "'";
        $rsRpt = $objCx->execute($sql);

        if (!$rsRpt->EOF) {
            $fraude = array('tipo' => $rsRpt->fields['tipo_rpt'],
                'estado' => $rsRpt->fields['estado_rpt'],
                'img' => ($rsRpt->fields['estado_rpt'] == 'resuelto' ? $img . 'alcaldia.png' : $img . 'ladron.png'));
        } else {
            $fraude = array('tipo' => '', 'img' => '');
        }

        $contenido = array(
            'nombre' => $rs->fields['nombre_comun'],
            'direccion' => $rs->fields['direccion'],
            'tipo' => $rs->fields['nombre_tb'],
            'predial' => (empty($rs->fields['predial']) ? $rs->fields['predial2'] : $rs->fields['predial']),
            'matricula' => $rs->fields['matricula'],
            'coordenadas' => $rs->fields['lat'] . ', ' . $rs->fields['lng'],
        );

        $color = $rs->fields['id_capa'];

        $LatLng = array();
        $arrayPredios = array();
        while (!$rs->EOF) {
            /*
             * Se toma el gid del primer registro, se va validando con 
             * los siguientes para cargar un nuevo indice en caso de ser
             * diferente
             */
            if ($poligono != $rs->fields['gid']) {
                $arrayPredios[$poligono] = array(
                    'LatLng' => $LatLng,
                    'color' => $color,
                    'contenido' => $contenido,
                    'fraude' => $fraude
                );

                $contenido = array(
                    'nombre' => $rs->fields['nombre_comun'],
                    'direccion' => $rs->fields['direccion'],
                    'tipo' => $rs->fields['nombre_tb'],
                    'predial' => (empty($rs->fields['predial']) ? $rs->fields['predial2'] : $rs->fields['predial']),
                    'matricula' => $rs->fields['matricula'],
                    'coordenadas' => $rs->fields['lat'] . ', ' . $rs->fields['lng'],
                );

                $poligono = $rs->fields['gid'];
                $predial = $rs->fields['predial'];

                /*
                 * Si el predio tiene un reporte por fraude, se pintará un marker 
                 * diferente en éste
                 */
                $sql = "SELECT COALESCE(tipo_reporte, '') AS tipo_rpt, 
                               COALESCE(estado_reporte, '') AS estado_rpt
                        FROM reporte_predio
                        WHERE predial ILIKE '" . $rs->fields['predial'] . "'";
                $rsRpt = $objCx->execute($sql);

                if (!$rsRpt->EOF) {
                    $fraude = array('tipo' => $rsRpt->fields['tipo_rpt'],
                        'estado' => $rsRpt->fields['estado_rpt'],
                        'img' => ($rsRpt->fields['estado_rpt'] == 'resuelto' ? $img . 'alcaldia.png' : $img . 'ladron.png'));
                } else {
                    $fraude = array('tipo' => '', 'img' => '');
                }

                // Se inicializa para evitar guardar las coordenadas sobre los nuevos predios
                $LatLng = array();
            }

            // QUITAR EL floatval, PUES LA LATITUD EN UN VALOR TIPO
            // 3.47931452563901, TERMINA CONVIRTIÉNDOSE EN 
            // 3.479314525639 Y EL POLÍGONO NO SE PINTA BIEN
            $LatLng[] = array('lng' => floatval($rs->fields['lng']), 'lat' => floatval($rs->fields['lat']));
            $color = $rs->fields['id_capa'];

            $rs->MoveNext();
        }

        $arrayPredios[$poligono] = array('LatLng' => $LatLng,
            'color' => $color,
            'contenido' => $contenido,
            'fraude' => $fraude);

        return array('success' => 1, 'msg' => BIENES_OK, 'data' => $arrayPredios);
    }

    /**
     * Información general del predio
     * 
     * @param string $predio Código del predio en la tabla
     * @param string $vista Extraer datos de la vista externa o interna 
     * @return array En caso de regresar datos, la clave success será 1
     */
    public function informacionPredio($predio, $vista) {
        $objCx = bd::getCx('bdPg1');

        $predio = blindSqlInjection(removeTags(cleanCRLF(cleanXSS($predio))));
        $predio = multipleHtmlEntityDecode($predio);

        // La vista externa tiene 2 campos que se agregarán a la consulta
        $camposExtra = '';
        if ($vista == 'externo') {
            $camposExtra = "COALESCE(geo.mensaje_p, '') AS msg_usuario, 
                            COALESCE(geo.direccion_const, '') AS direccion_construccion,";
        } else {
            $camposExtra = "COALESCE(geo.codigonal_p,'') AS codigo_nacional,
                            COALESCE(geo.num_activofijo_p,'') AS num_activo_fijo,
                            COALESCE(geo.barrio,'') AS barrio,
                            COALESCE(geo.comuna,'') AS comuna,
                            COALESCE(geo.estra_moda,0) AS estrato,
                            COALESCE(geo.cedente,'') AS cedente,
                            COALESCE(geo.nombre_cb,'') AS nombre_cb,
                            COALESCE(geo.orfeo_cb_p,'') AS orfeo,
                            COALESCE(geo.nombre_tb,'') AS nombre_tb,
                            COALESCE(geo.nombre_tu,'') AS nombre_tu,
                            COALESCE(geo.nombre_madq,'') AS nombre_madq,
                            COALESCE(geo.lind_norte_p,'') AS lind_norte_predio,
                            COALESCE(geo.lind_sur_p,'') AS lind_sur_predio,
                            COALESCE(geo.lind_este_p,'') AS lind_este_predio,
                            COALESCE(geo.lind_oeste_p,'') AS lind_oeste_predio,
                            COALESCE(geo.lind_adic_p,'') AS lind_adic_predio,
                            COALESCE(geo.estado_predio,'') AS estado_predio,
                            COALESCE(geo.nombre_tipod,'') AS nombre_tipo,
                            COALESCE(geo.numero_doc,'') AS numero_doc,
                            COALESCE(geo.fecha_doc,current_timestamp) AS fecha_documento,
                            COALESCE(geo.nombre_not,'') AS nombre_not,
                            COALESCE(geo.numero_cont,'') AS numero_cont,
                            COALESCE(geo.area_entregada_cont,0) AS area_entregada,
                            COALESCE(geo.fecha_ini_cont,current_timestamp) AS fecha_inicio_contrato,
                            COALESCE(geo.fecha_fin_cont,current_timestamp) AS fecha_fin_contrato,
                            COALESCE(geo.estado_cto,'') AS estado_contrato,
                            COALESCE(geo.lind_norte_cont,'') AS lind_norte_contrato,
                            COALESCE(geo.lind_sur_cont,'') AS lind_sur_contrato,
                            COALESCE(geo.lind_este_cont,'') AS lind_este_contrato,
                            COALESCE(geo.lind_oeste_cont,'') AS lind_oeste_contrato,
                            COALESCE(geo.lind_adic_cont,'') AS lind_adic_contrato,
                            COALESCE(geo.tercero,'') AS tercero,
                            COALESCE(geo.numpisos_const,0) AS numero_pisos_construccion,
                            COALESCE(geo.activofijo_const,'') AS activo_fijo_construccion,
                            COALESCE(geo.nombre_depen,'') AS nombre_dependencia,
                            COALESCE(geo.path_archivo_digi,'') AS path_archivo_digital,
                            COALESCE(geo.path_foto_digi,'') AS path_archivo_fotos,
                            COALESCE(geo.fecha_obs,current_timestamp) AS fecha_observacion,
                            COALESCE(geo.observacion_obs,'') AS observacion,
                            COALESCE(geo.nombre_usu,'') AS nombre_usuario,
                            COALESCE(geo.apellido_usu,'') AS apellido_usuario,
                            COALESCE(geo.url1,'') AS url1,
                            COALESCE(geo.url2,'') AS url2,
                            COALESCE(geo.url3,'') AS url3,";
        }

        $sql = "SELECT $camposExtra
                       COALESCE(geo.identifica_p, '') AS predial_terreno, 
                       COALESCE(geo.predial_edificacion_const, '') AS predial_construccion, 
                       COALESCE(geo.direccion_p, '') AS dir_oficial, 
                       --COALESCE(capa.nombre_capa, '') AS tipo_bien, 
                       COALESCE(geo.nombre_tb, '') AS tipo_bien, 
                       COALESCE(geo.proyecto_p, '') AS nombre_proyecto, 
                       COALESCE(geo.nombre_areacedida_p, '') AS nombre_area_cedida, 
                       COALESCE(geo.nombre_const, '') AS nombre_construccion,
                       COALESCE(geo.area_edifica_const, 0) AS area_edificada,
                       COALESCE(geo.mat_inmob_p, '') AS matricula, 
                       COALESCE(geo.area_cesion_p, 0) AS area_cedida, 
                       COALESCE(geo.nombrecomun_p, '') AS nombre_comun
                FROM vw_app_$vista AS geo
                     INNER JOIN capa ON (geo.id_capa=capa.id_capa)
                WHERE geo.identifica_p ILIKE '$predio'
                ORDER BY 1
                LIMIT 1";
        $rs = $objCx->execute($sql);

        if (!$rs) {
            return array('success' => 0, 'msg' => BIENES_NOTOK);
        } else {
            $predioArray = array();
            if (!$rs->EOF) {
                $predioArray['general'] = array(
                    'msg_usuario' => $vista == 'externo' ? $rs->fields['msg_usuario'] : '',
                    'predial_terreno' => $rs->fields['predial_terreno'],
                    'predial_construccion' => $rs->fields['predial_construccion'],
                    'direccion_oficial' => $rs->fields['dir_oficial'],
                    'tipo_bien' => $rs->fields['tipo_bien'],
                    'nombre_proyecto' => $rs->fields['nombre_proyecto'],
                    'nombre_area_cedida' => $rs->fields['nombre_area_cedida'],
                    'nombre_construccion' => $rs->fields['nombre_construccion'],
                    'area_edificada' => $rs->fields['area_edificada'],
                    'direccion_construccion' => $vista == 'externo' ? $rs->fields['direccion_construccion'] : '',
                    'matricula' => $rs->fields['matricula'],
                    'area_cedida' => $rs->fields['area_cedida'],
                    'nombre_comun' => $rs->fields['nombre_comun']
                );

                // En caso de que esté conectado al LDAP se crea un array con los campos extras
                if ($_COOKIE['tokenSID'] == true) {
                    $predioArray['extras'] = array(
                        'codigo_nacional' => $rs->fields['codigo_nacional'],
                        'num_activo_fijo' => $rs->fields['num_activo_fijo'],
                        'barrio' => $rs->fields['barrio'],
                        'comuna' => $rs->fields['comuna'],
                        'estrato' => $rs->fields['estrato'],
                        'cedente' => $rs->fields['cedente'],
                        'nombre_cb' => $rs->fields['nombre_cb'],
                        'orfeo' => $rs->fields['orfeo'],
                        'nombre_tb' => $rs->fields['nombre_tb'],
                        'nombre_tu' => $rs->fields['nombre_tu'],
                        'nombre_madq' => $rs->fields['nombre_madq'],
                        'lind_norte_predio' => $rs->fields['lind_norte_predio'],
                        'lind_sur_predio' => $rs->fields['lind_sur_predio'],
                        'lind_este_predio' => $rs->fields['lind_este_predio'],
                        'lind_oeste_predio' => $rs->fields['lind_oeste_predio'],
                        'lind_adic_predio' => $rs->fields['lind_adic_predio'],
                        'estado_predio' => $rs->fields['estado_predio'],
                        'nombre_tipo' => $rs->fields['nombre_tipo'],
                        'numero_doc' => $rs->fields['numero_doc'],
                        'fecha_documento' => substr($rs->fields['fecha_documento'], 0, 10),
                        'nombre_not' => $rs->fields['nombre_not'],
                        'numero_cont' => $rs->fields['numero_cont'],
                        'area_entregada' => $rs->fields['area_entregada'],
                        'fecha_inicio_contrato' => substr($rs->fields['fecha_inicio_contrato'], 0, 10),
                        'fecha_fin_contrato' => substr($rs->fields['fecha_fin_contrato'], 0, 10),
                        'estado_contrato' => $rs->fields['estado_contrato'],
                        'lind_norte_contrato' => $rs->fields['lind_norte_contrato'],
                        'lind_sur_contrato' => $rs->fields['lind_sur_contrato'],
                        'lind_este_contrato' => $rs->fields['lind_este_contrato'],
                        'lind_oeste_contrato' => $rs->fields['lind_oeste_contrato'],
                        'lind_adic_contrato' => $rs->fields['lind_adic_contrato'],
                        'tercero' => $rs->fields['tercero'],
                        'numero_pisos_construccion' => $rs->fields['numero_pisos_construccion'],
                        'activo_fijo_construccion' => $rs->fields['activo_fijo_construccion'],
                        'nombre_dependencia' => $rs->fields['nombre_dependencia'],
                        'path_archivo_digital' => $rs->fields['path_archivo_digital'],
                        'path_archivo_fotos' => $rs->fields['path_archivo_fotos'],
                        'fecha_observacion' => substr($rs->fields['fecha_observacion'], 0, 10),
                        'observacion' => $rs->fields['observacion'],
                        'nombre_usuario' => $rs->fields['nombre_usuario'],
                        'apellido_usuario' => $rs->fields['apellido_usuario'],
                        'url1' => $rs->fields['url1'],
                        'url2' => $rs->fields['url2'],
                        'url3' => $rs->fields['url3'],
                    );
                }
            }
        }

        if (count($predioArray) != 0) {
            return array('success' => 1, 'msg' => BIENES_OK, 'data' => $predioArray);
        } else {
            return array('success' => 0, 'msg' => BIENES_NOTOK, 'sql' => $sql);
        }
    }

    /**
     * Búsqueda de predios
     * 
     * Mostrará los predios que se relacionen con el valor a buscar
     * 
     * @param string $tipo  Búsqueda por dirección, matricula, predial barrio o comuna
     * @param string $valor Valor de búsqueda
     * @return array En caso de regresar datos, la clave success será 1
     */
    public function buscarPoligonos($tipo, $valor, $vista) {
        $objCx = bd::getCx('bdPg1');

        $valor = blindSqlInjection(removeTags(cleanCRLF(cleanXSS($valor))));
        $valor = multipleHtmlEntityDecode($valor);

        /*
         * Se valida el tipo por el que se hará la búsqueda, ya que el filtro 
         * y el order se harán con éste
         */
        switch ($tipo) {
            case 1: // Por dirección
                $where = "LOWER(direccion_p) ILIKE '%" . strtolower($valor) . "%'";
                $order = "x.direccion_p";
                break;
            case 2: // Por matricula inmobiliaria
                $where = "LOWER(mat_inmob_p) ILIKE '%" . strtolower($valor) . "%'";
                $order = "x.mat_inmob_p";
                break;
            case 3: // Por predial
                $where = "(LOWER(x.identifica_p) ILIKE '%" . strtolower($valor) . "%' OR 
                           LOWER(x.predial_edificacion_const) ILIKE '%" . strtolower($valor) . "%')";
                $order = "x.predial_edificacion_const";
                break;
            case 4: // Por predial
                $where = "LOWER(x.barrio) ILIKE '%" . strtolower($valor) . "%'";
                $order = "x.barrio";
                break;
            case 5: // Por predial
                $where = "x.comuna ILIKE '%$valor%'";
                $order = "x.comuna";
                break;
        }

        $sql = "SELECT (ARRAY_AGG(TRIM(TO_CHAR(x.X, '9999999999999999D9999999999999999'))))[1] AS longitud,
                       (ARRAY_AGG(TRIM(TO_CHAR(x.Y, '9999999999999999D9999999999999999'))))[1] AS latitud,
                       COALESCE(x.direccion_p, '') AS direccion, 
                       COALESCE(x.nombre_tb, '') AS nombre_tb, 
                       COALESCE(x.identifica_p, '') AS predio, 
                       COALESCE(x.comuna, '') AS comuna, 
                       COALESCE(x.barrio, '') AS barrio, 
                       COALESCE(x.mat_inmob_p, '') AS matricula
                FROM (
                        SELECT direccion_p, nombre_tb, comuna, identifica_p, 
                               barrio, predial_edificacion_const, mat_inmob_p, 
                               ST_X(ST_CENTROID((ST_DumpPoints(the_geom)).geom)) AS X,
                               ST_Y(ST_CENTROID((ST_DumpPoints(the_geom)).geom)) AS Y
                        FROM vw_app_$vista
                        ORDER BY 1
                ) AS x
                WHERE $where
                GROUP BY x.direccion_p, x.nombre_tb, x.comuna, x.identifica_p, 
                         x.barrio, x.predial_edificacion_const, x.mat_inmob_p
                ORDER BY $order";
        $rs = $objCx->execute($sql);

        if (!$rs) {
            return array('success' => 0, 'msg' => BIENES_NOTOK);
        } else {
            $poligono = array();
            while (!$rs->EOF) {
                $poligono[] = array('latitud' => str_replace(',', '.', $rs->fields['longitud']),
                    'longitud' => str_replace(',', '.', $rs->fields['latitud']),
                    'direccion' => $rs->fields['direccion'], 'tipo' => $rs->fields['nombre_tb'],
                    'matricula' => $rs->fields['matricula'], 'comuna' => $rs->fields['comuna'],
                    'barrio' => $rs->fields['barrio'], 'predial' => $rs->fields['predio']);
                $rs->MoveNext();
            }

            if (count($poligono) != 0) {
                return array('success' => 1, 'msg' => BIENES_OK, 'data' => $poligono);
            } else {
                return array('success' => 0, 'msg' => BIENES_NOTOK);
            }
        }
    }

    /**
     * Logueo sistema LDAP
     * 
     * @param string $usuario
     * @param string $password 
     * @return array Se genera un token y una cookie
     */
    public function login($usuario, $password) {
        //sxMod::setVar('Bienes', 'LDAP', array('host'=>'192.168.1.2', 'port'=>389, 'dominio'=>'nexura.nx'));

        $usuario = blindSqlInjection(removeTags(cleanCRLF(cleanXSS(multipleHtmlEntityDecode($usuario)))));
        $password = blindSqlInjection(removeTags(cleanCRLF(cleanXSS(multipleHtmlEntityDecode($password)))));

        // Se genera el JWT
        $nombre = explode(',', $entries[0]['dn']);
        $time = time(); // (60*60*24*365);
        $token = array(
            'iat' => $time, // Tiempo que inició el token
            'exp' => $time + BIENES_TOKENTIME, // Tiempo que expirará el token (1 año)
            'data' => [// información del usuario
                'id' => 1,
                'name' => 'jpoblano@info.info'
            ]
        );
        $jwt = JWT::encode($token, BIENES_TOKENKEY);

        $ldap_connect = ldap_connect(BIENES_LDAP_HOST, BIENES_LDAP_PORT);
        if (!$ldap_connect) {
            return array('success' => 0, 'msg' => BIENES_LDAP_ERROR);
        }

        ldap_set_option($ldap_connect, LDAP_OPT_PROTOCOL_VERSION, 3);
        ldap_set_option($ldap_connect, LDAP_OPT_REFERRALS, 0);

        $loginTmp = trim($usuario) . '@' . BIENES_LDAP_DOMINIO;
        $ldapbind = ldap_bind($ldap_connect, $loginTmp, trim($password));

        /*
         * En caso de conexión correcta, al sistema, se busca en la DB LDAP el 
         * usuario para entregar sus credenciales
         */
        if ($ldapbind) {
            $_COOKIE['tokenSID'] = false;
            $_SESSION['Bienes']['token'] = '';

            $attributes = array('mail');
            // Search AD
            $dc_dominio = explode('.', BIENES_LDAP_DOMINIO);
            $results = ldap_search($ldap_connect, 'DC=' . implode(',DC=', $dc_dominio), "(samaccountname=$usuario)", $attributes);
            $entries = ldap_get_entries($ldap_connect, $results);

            // Se genera el JWT
            $nombre = explode(',', $entries[0]['dn']);
            $time = time(); // (60*60*24*365);
            $token = array(
                'iat' => $time, // Tiempo que inició el token
                'exp' => $time + BIENES_TOKENTIME, // Tiempo que expirará el token (1 año)
                'data' => [// información del usuario
                    'id' => $entries[0]['mail'][0],
                    'name' => str_replace('CN=', '', $nombre[0])
                ]
            );
            $jwt = JWT::encode($token, BIENES_TOKENKEY);

            if ($entries[0]['mail']['count'] > 0) {
                setcookie("tokenSID", $jwt, $time + BIENES_TOKENTIME);
                return array('success' => 1, 'msg' => BIENES_LOGINOK, 'data' => array('nombre' => $token['data']['name'], 'token' => $jwt));
            } else {
                return array('success' => 0, 'msg' => BIENES_LDAP_LOGINNOTOK);
            }
        } else {
            return array('success' => 0, 'msg' => BIENES_LDAP_LOGINNOTOK);
        }
    }

    /**
     * Reportar un predio
     * 
     * Reportará a la alcaldía a través del sistema de gestión documental Orfeo,
     * cualquier irregularidad que se presente con un Bien Fiscal o de uso público,
     * a través del consumo de un WS
     * 
     * @param string $tipo        Tipo de fraude que se puede estar efectuando al predio
     * @param type $fotografia    Imagen JPG, PNG, GIF
     * @param string $direccion 
     * @param string $predial     Número de predial 
     * @param string $nombre      Persona que está haciendo el reporte
     * @param string $correo      Correo electrónico de quien está haciendo el reporte
     * @param number $telefono    Número de teléfono de quien está haciendo el reporte
     * @param string $coordenadas Latitud y longitud
     * @param string $ip          Dirección IP de la máquina donde se hace el reporte
     * @return array
     */
    public function reportarPredio($tipo, $fotografia, $direccion, $predial, $nombre, 
                                   $correo, $telefono, $coordenadas, $ip, $cedula) {

        // Campos de Orfeo
        $nombre = multipleHtmlEntityDecode(blindSqlInjection(removeTags(cleanCRLF(cleanXSS($nombre)))));
        $correo = multipleHtmlEntityDecode(blindSqlInjection(removeTags(cleanCRLF(cleanXSS($correo)))));
        $telefono = multipleHtmlEntityDecode(blindSqlInjection(removeTags(cleanCRLF(cleanXSS($telefono)))));
        // Este campo se debe ver cómo se va a guardar en Orfeo
        $fotografia = multipleHtmlEntityDecode(blindSqlInjection(removeTags(cleanCRLF(cleanXSS($fotografia)))));

        // Campos tabla Bienes
        $tipo = multipleHtmlEntityDecode(blindSqlInjection(removeTags(cleanCRLF(cleanXSS($tipo)))));
        $direccion = multipleHtmlEntityDecode(blindSqlInjection(removeTags(cleanCRLF(cleanXSS($direccion)))));
        $predial = multipleHtmlEntityDecode(blindSqlInjection(removeTags(cleanCRLF(cleanXSS($predial)))));
        $ip = multipleHtmlEntityDecode(blindSqlInjection(removeTags(cleanCRLF(cleanXSS($ip)))));
        $coordenadas = multipleHtmlEntityDecode(blindSqlInjection(removeTags(cleanCRLF(cleanXSS($coordenadas)))));
        $coordenadas = explode(',', str_replace(' ', '', $coordenadas));

        $objCx = bd::getCx('bdPg1');

        // Se consulta si el predio ya tiene un reporte anterior y si es así que no esté resuelto
        $sql = "SELECT COUNT(*) AS reporte
                FROM reporte_predio 
                WHERE predial ILIKE '$predial' AND estado_reporte NOT ILIKE 'resuelto'";
        $rs = $objCx->execute($sql);

        if ($rs->reporte == 0) {
            $sql = "INSERT INTO reporte_predio 
                    (the_geom, 
                     predial, tipo_reporte, fecha_reporte, direccion_predio_reporte, dir_ip_reporte)
                    VALUES (ST_GeomFromEWKT('SRID=4326;POINT(" . $coordenadas[1] . " " . $coordenadas[0] . ")'),
                            '$predial', '$tipo', NOW(), '$direccion', '$ip')
                    RETURNING id_reporte;";

            try {
                $rsInsert = $objCx->execute($sql);

                if (!$rsInsert) {
                    return array('success' => 0, 'msg' => BIENES_ORFEO_ERROR);
                }

                if (!$rsInsert->EOF) {
                    $this->reducirIMG($fotografia);
                    $this->wsOrfeo($fotografia, $tipo, $direccion, $predial, $nombre, 
                                   $correo, $telefono, $coordenadas, $ip, $cedula);

                    return array('success' => 1, 'msg' => '', 'data' => $rsInsert->fields['id_reporte']);
                }
            } catch (Exception $ex) {
                return array('success' => 0, 'msg' => BIENES_ORFEO_ERROR);
            }
        } else {
            return array('success' => 0, 'msg' => BIENES_ORFEO_REPORTADO);
        }
    }

    /**
     * Entrega de capas
     * 
     * Devuelve el tipo de capas que existen en la BD para hacer uso del filtro
     * y poder mostrar los predios de acuerdo a éstas
     * 
     * @param string $vista Extraer datos de la vista externa o interna
     * 
     * @return array En caso de regresar datos, la clave success será 1
     */
    public function capas($vista) {
        $objCx = bd::getCx('bdPg1');

        $sql = "SELECT capa.id_capa, capa.nombre_capa,
                       CASE capa.id_capa
                           WHEN 1 THEN '#FF0000'
                           WHEN 2 THEN '#FF8000'
                           WHEN 3 THEN '#800080'
                           WHEN 4 THEN '#FFFF00'
                           WHEN 5 THEN '#6A6A6A'
                           WHEN 6 THEN '#00FF00'
                           WHEN 7 THEN '#FA045D'
                       END AS color_capa
                FROM vw_app_$vista AS vista
                     INNER JOIN capa ON (capa.id_capa=vista.id_capa)
                GROUP BY capa.id_capa, capa.nombre_capa";
        $rs = $objCx->execute($sql);

        if (!$rs) {
            return array('success' => 0, 'msg' => BIENES_NOTOK);
        } else {
            $capas = array();
            while (!$rs->EOF) {
                $capas[$rs->fields['id_capa']] = array($rs->fields['nombre_capa'], $rs->fields['color_capa']);
                $rs->MoveNext();
            }

            if (count($capas) != 0) {
                return array('success' => 1, 'msg' => BIENES_OK, 'data' => $capas);
            } else {
                return array('success' => 0, 'msg' => BIENES_NOTOK);
            }
        }
    }

    /**
     * Entrega de construcciones
     * 
     * Entrega los polígonos de las construcciones que existan o no en los predios
     * 
     * @return array En caso de regresar datos, la clave success será 1
     */
    public function mostrarConstrucciones() {
        $objCx = bd::getCx('bdPg1');

        $sql = "SELECT const.oid AS gid, 
                       ST_X(ST_CENTROID((ST_DumpPoints(const.the_geom)).geom)) AS lng,
                       ST_Y(ST_CENTROID((ST_DumpPoints(const.the_geom)).geom)) AS lat,
                       nombre_capa
                FROM vw_app_geo_constr AS const";
        $rs = $objCx->execute($sql);

        if (!$rs) {
            return array('success' => 0, 'msg' => BIENES_NOCONSTRUCCION);
        }

        $poligono = $rs->fields['gid'];
        $arrayConstrucciones = array();
        while (!$rs->EOF) {
            if ($poligono != $rs->fields['gid']) {
                $arrayConstrucciones[$poligono] = array(
                    'LatLng' => $LatLng,
                    'color' => $color,
                    'coordenadas' => $coordenadas,
                    'capa' => $capa
                );

                $poligono = $rs->fields['gid'];
                $coordenadas = $rs->fields['lat'] . ', ' . $rs->fields['lng'];
                $capa = $rs->fields['nombre_capa'];
                switch (strtolower($capa)) {
                    case 'zona verde':
                        $color = '#00FF00';
                        break;
                    case 'via publica':
                        $color = '#DCDCDC';
                        break;
                    default:
                        $color = '#000';
                        break;
                }
                // Se inicializa para evitar guardar las coordenadas sobre los nuevos predios
                $LatLng = array();
            }

            $LatLng[] = array('lng' => floatval($rs->fields['lng']), 'lat' => floatval($rs->fields['lat']));

            $rs->moveNext();
        }

        $arrayConstrucciones[$poligono] = array('LatLng' => $LatLng,
            'color' => '#000',
            'coordenadas' => $coordenadas,
            'capa' => $capa);

        return array('success' => 1, 'msg' => BIENES_OK, 'data' => $arrayConstrucciones);
    }

    /**
     * Entrega de amoblamientos
     * 
     * Retorna todos los amoblamientos existentes en la DB con colores para las
     * líneas y los polígonos y como imágenes para los puntos, separados entre:
     * 1) Puntos:    Árboles, macetas, faros, etc...
     * 2) Líneas:    Calles, Vías, separadores, etc...
     * 3) Polígonos: Canchas, graderías, kioskos, etc...
     * 
     * @param string $vista Extraer datos de la vista externa o interna
     * 
     * @return array En caso de regresar datos, la clave success será 1
     */
    public function mostrarAmoblamientos($tipo) {
        $img = 'mod/Bienes/img/';

        // Sólo se extraen los tipos de amoblamientos solicitados
        switch ($tipo) {
            case 'linea':
                $case = "CASE LOWER(nombre_ta)
                           WHEN 'antejardin'         THEN '#444444'
                           WHEN 'baranda'            THEN '#EEBB56'
                           WHEN 'canal'              THEN '#0E95FC'
                           WHEN 'canaleta'           THEN '#72C0FD'
                           WHEN 'cerco'              THEN '#EC43BF'
                           WHEN 'ciclovia'           THEN '#D7A993'
                           WHEN 'jarrillon'          THEN '#6FC552'
                           WHEN 'malla'              THEN '#EF65C9'
                           WHEN 'muro'               THEN '#B1B1B1'
                           WHEN 'paramento'          THEN '#000000'
                           WHEN 'pista de trote'     THEN '#D5D5D5'
                           WHEN 'puente'             THEN '#FFFF00'
                           WHEN 'quebrada'           THEN '#31A4FC'
                           WHEN 'sendero'            THEN '#A69372'
                           WHEN 'swinglea'           THEN '#4bE400'
                           WHEN 'via'                THEN '#FF0202'
                           WHEN 'via sin pavimentar' THEN '#FF3939'
                           WHEN 'zona dura'          THEN '#ACACAC'
                         END AS impresion";
                break;
            case 'poligono':
                $case = "CASE LOWER(nombre_ta)
                           WHEN 'cancha'             THEN '#828282;#FFFFFF'
                           WHEN 'caseta'             THEN '#232323;#E1E1E1'
                           WHEN 'cuarto de maquinas' THEN '#730909;#FFFFFF'
                           WHEN 'ducha'              THEN '#0AAAE6;#FFFFFF'
                           WHEN 'graderia'           THEN '#8C8C8C;#FFFFFF'
                           WHEN 'guadual'            THEN '#6AB940;#FFFFFF'
                           WHEN 'kiosko'             THEN '#734C00;#734C00'
                           WHEN 'matera'             THEN '#83C55F;#FFFFFF'
                           WHEN 'piscina'            THEN '#00A9E6;#00A9E6'
                           WHEN 'shut'               THEN '#B03E2C;#B03E2C'
                         END AS impresion";
                break;
            case 'punto':
                $case = "LOWER(REPLACE(nombre_ta, ' ', '_')) || '.png' AS impresion";
                break;
        }

        $objCx = bd::getCx('bdPg1');
        echo $sql = "SELECT gid, 
                       --ST_AsText(the_geom) AS latlng,
                       REPLACE(REPLACE(REPLACE(REPLACE(REPLACE(ST_AsText(the_geom), 
                                                               ')', ''), 
                                                       '(', ''), 
                                               'POLYGON', ''), 
                                       'POINT', ''), 
                               'LINESTRING', '') AS latlng,
                       nombre_ta,
                       $case
                FROM vw_app_geo_amoblamientos
                WHERE tipo ILIKE '$tipo' AND gid=19
                ORDER BY gid";
        $rs = $objCx->execute($sql);

        if (!$rs) {
            return array('success' => 0, 'msg' => BIENES_NOAMOBLAMIENTO);
        }

        $latlng = explode(',', $rs->fields['latlng']);
        $LatLng = array();
        foreach ($latlng as $valor) {
            $arr = explode(' ', $valor);
            $LatLng[] = array('lng' => floatval($arr[0]), 'lat' => floatval($arr[1]));
        }

        $poligono = $rs->fields['gid'];
        $impresion = $rs->fields['impresion'];
        $ta = $rs->fields['nombre_ta'];
        $arrayAmoblamientos = array();
        while (!$rs->EOF) {
            $arrayAmoblamientos[$poligono] = array(
                //'LatLng' => $LatLng,
                'LatLng' => $LatLng,
                'impresion' => ($tipo == 'punto' ? $img . $impresion : $impresion),
                'id' => $ta
            );

            $poligono = $rs->fields['gid'];
            $impresion = $rs->fields['impresion'];
            // Se inicializa para evitar guardar las coordenadas sobre los nuevos predios
            $LatLng = array();
            $latlng = explode(',', $rs->fields['latlng']);
            foreach ($latlng as $valor) {
                $arr = explode(' ', $valor);
                $LatLng[] = array('lng' => floatval($arr[0]), 'lat' => floatval($arr[1]));
            }
            //$LatLng[] = array('lng' => floatval($rs->fields['lng']), 'lat' => floatval($rs->fields['lat']));
            $ta = $rs->fields['nombre_ta'];

            $rs->moveNext();
        }

        $arrayAmoblamientos[$poligono] = array(
            'LatLng' => $LatLng,
            'impresion' => ($tipo == 'punto' ? $img . $impresion : $impresion),
            'id' => $ta
        );

        return array('success' => 1, 'msg' => BIENES_OK, 'data' => $arrayAmoblamientos);
    }

    /**
     * Validación token JWT
     * 
     * Valida el token generado por JWT para que se haga logout en caso de haber expirado
     * 
     * @param string $token Valor de cadena que contiene el token generado por JWT
     * @return array
     */
    public function validaToken($token = '') {
        // En caso de no enviar nada, se escoge la vista externa
        if ($token == '') {
            return 'externo';
        }

        try {
            $data = JWT::decode($token, BIENES_TOKENKEY, array('HS256'));
        } catch (Exception $ex) {
            return 'externo';
        }


        // En caso de que el token haya expirado se debe trabajar sobre la vista externa
        if (time() >= $data->exp) {
            return 'externo';
        } else {
            return 'interno';
        }
    }

    public function reducirIMG($imgFile, $tipo = 'png') {
        if (isset($imgFile) && $imgFile != '') {

            //Imagen original
            $rtOriginal = $imgFile;

            //Crear variable
            if ($tipo == 'png') {
                $original = imagecreatefrompng($rtOriginal);
            } else {
                $original = imagecreatefromjpeg($rtOriginal);
            }

            //Ancho y alto máximo
            $max_ancho = 600;
            $max_alto = 400;

            //Medir la imagen
            list($ancho, $alto) = getimagesize($rtOriginal);

            //Ratio
            $x_ratio = $max_ancho / $ancho;
            $y_ratio = $max_alto / $alto;

            //Proporciones
            if (($ancho <= $max_ancho) && ($alto <= $max_alto)) {
                $ancho_final = $ancho;
                $alto_final = $alto;
            } else if (($x_ratio * $alto) < $max_alto) {
                $alto_final = ceil($x_ratio * $alto);
                $ancho_final = $max_ancho;
            } else {
                $ancho_final = ceil($y_ratio * $ancho);
                $alto_final = $max_alto;
            }

            //Crear un lienzo
            $lienzo = imagecreatetruecolor($ancho_final, $alto_final);

            //Copiar original en lienzo
            imagecopyresampled($lienzo, $original, 0, 0, 0, 0, $ancho_final, $alto_final, $ancho, $alto);

            //Destruir la original
            imagedestroy($original);

            //Crear la imagen y guardar en directorio upload/
            if ($tipo == 'png') {
                imagepng($lienzo, $imgFile);
            } else {
                imagejpeg($lienzo, $imgFile);
            }
        }
    }

    public function wsOrfeo($imgBase64, $tipo, $direccion, $predial, $nombre, 
                            $correo, $telefono, $coordenadas, $ip, $cedula) {
        $nombre = explode(' ', $nombre);
        $destinatario1 = array(
            'documento' => BIENES_ORFEO_CAMPO_DOCUMENTO,
            'cc_documento' => $cedula,
            'tipo_emp' => BIENES_ORFEO_CAMPO_TIPOEMP,
            'nombre' => $nombre[0],
            'prim_apel' => $nombre[1],
            'seg_apel' => count($nombre)>2?$nombre[2]:'',
            'telefono' => $telefono,
            'direccion' => $direccion,
            'mail' => $correo,
            'otro' => $tipo.'|'.$ip.' fecha: ' . date("Y-m-d"),
            'idcont' => BIENES_ORFEO_CAMPO_IDCONT,
            'idpais' => BIENES_ORFEO_CAMPO_IDPAIS,
            'codep' => BIENES_ORFEO_CAMPO_CODEP,
            'muni' => BIENES_ORFEO_CAMPO_MUNI
        );

        $destinatario2 = "";
        $destinatario3 = "";

        $arregloDatos = array();
        $arregloDatos[0] = $correo;
        $arregloDatos[1] = $destinatario1;
        $arregloDatos[2] = $destinatario2; // Predio = se envia una cadena vacía
        $arregloDatos[3] = $destinatario3; // esp = se envia una cadena vacía
        $arregloDatos[4] = $tipo.BIENES_ORFEO_CAMPO_OBSERVACION;
        $arregloDatos[5] = BIENES_ORFEO_CAMPO_MEDIOREC;
        $arregloDatos[6] = BIENES_ORFEO_CAMPO_ANEXOS;
        $arregloDatos[7] = BIENES_ORFEO_CAMPO_CODDEPEN;
        $arregloDatos[8] = BIENES_ORFEO_CAMPO_TIPORAD;
        $arregloDatos[9] = BIENES_ORFEO_CAMPO_CUENTA;
        $arregloDatos[10] = BIENES_ORFEO_CAMPO_DEPENDENCIA;
        $arregloDatos[11] = BIENES_ORFEO_CAMPO_TIPOREM;
        $arregloDatos[12] = BIENES_ORFEO_CAMPO_TIPOREMITENTE;
        $arregloDatos[13] = BIENES_ORFEO_CAMPO_DOCUMENTO;           
        $arregloDatos[14] = BIENES_ORFEO_CAMPO_CODDIRRAD;
        $arregloDatos[15] = BIENES_ORFEO_CAMPO_CODDIR;
        $arregloDatos[16] = BIENES_ORFEO_CAMPO_DOCRAD;
        $arregloDatos[17] = "";            //  Radicado asociado
        $arregloDatos[18] = "";            //  Número expediente

        $client = new SoapClient(BIENES_ORFEO_WSDL); // http://172.18.26.3/webServ/wsRadicado.php?wsdl
        $result = $client->__soapCall('radicarXDependencia', $arregloDatos);

        $im = file_get_contents($imgBase64);
        $imgFile = base64_encode($im);
        $arregloDatos = array(); // imagen 64
        $arregloDatos[0] = $result->numeroRadicado;
        $arregloDatos[1] = "1404";
        $arregloDatos[2] = array('item' => array('archivo' => $imgFile, 'nombre' => $imgBase64, 'descripcion' => $imgBase64));
        $resultImg = $client->__soapCall('anexoRadicadov2', $arregloDatos);
    }

}

?>