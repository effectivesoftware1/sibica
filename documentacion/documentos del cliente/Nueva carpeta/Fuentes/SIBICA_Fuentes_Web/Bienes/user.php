<?php

/**
 * Bienes_user
 *
 * @package Bienes
 * @subpackage Interfaz
 * @version 2016/12/15 17:30
 */
include_once('lib/class.gstApi.php');

/**
 * 
 *
 * @package Bienes
 * @subpackage Interfaz
 * @copyright Copyright {@link http://www.nexura.com nexura} 2009
 * @version 1.0
 *
 * @author Guillermo Alfonso Morales <gmorales@nexura.com>
 */
class Bienes_user extends configBienes {

    function main() {
        $objBienes = new gstApi();
        $capas = $objBienes->capas((isset($_COOKIE['tokenSID']) ? 'interno' : 'externo'));
        
        $this->getOptions();
        // Se verifica si hay un token para evitar que el usuario se loguee cada que entra al aplicativo
        if (isset($_COOKIE['tokenSID'])) {
            $this->optionsByFunction = [
                'user' => [
                    'main' => [
                        'modPath' => [],
                        'modMenu' => ['logout', 'buscar', 'filtro', 'nomenclatura', 'pdf'],
                    ],
                ],
            ];
        } else {
            $this->optionsByFunction = [
                'user' => [
                    'main' => [
                        'modPath' => [],
                        'modMenu' => ['login', 'buscar', 'filtro', 'nomenclatura', 'pdf'],
                    ],
                ],
            ];
        }

        $this->vData = array('datos' => $_SESSION['Bienes'], 'capas' => $capas['data']);
        $this->title = BIENES_MODULO;
        $this->view = 'user.main';
        $this->template = 'dInternoAmplio';
        $this->plugins = ['markerClusterer'];
        $this->render();
    }

    /**
     * Devuelve los poligonos de los predios al AJAX initMap de la página main
     * 
     * @return JSON Coordenadas y contenido para el infoWindow
     */
    function mostrarPredios() {
        $objBienes = new gstApi();
        //(isset($_POST['vista']) ? $_POST['vista'] == 1 ? 'interno' : 'externo' : isset($_COOKIE['tokenSID']) ? 'interno' : 'externo')
        echo json_encode($objBienes->mostrarPredios($objBienes->validaToken(isset($_POST['token'])?$_POST['token']:$_COOKIE['tokenSID']), $_POST['coords']));
    }

    /**
     * Devuelve los poligonos de los predios al AJAX initMap de la página main
     * 
     * @return JSON Coordenadas y contenido para el infoWindow
     */
    function buscarPoligonos() {
        $objBienes = new gstApi();
        echo json_encode($objBienes->buscarPoligonos($_POST['opcion'], $_POST['valor'], $objBienes->validaToken(isset($_POST['token'])?$_POST['token']:$_COOKIE['tokenSID'])));
    }

    function informacionPredio() {
        $objBienes = new gstApi();
        echo json_encode($objBienes->informacionPredio($_POST['predio'], $objBienes->validaToken(isset($_POST['token'])?$_POST['token']:$_COOKIE['tokenSID'])));
    }

    function login() {
        $objBienes = new gstApi();
        echo json_encode($objBienes->login($_POST['usuario'],$_POST['password']));
    }

    function logout() {
        setcookie('tokenSID', '', time()-100);

        setMsg(BIENES_LDAP_LOGOUT);
        redirect(genUrl('Bienes', 'user', 'main'));
    }

    function reporte() {
        /*
         * Se valida que los campos se env&iacute;n con datos y que los especiales
         * como correo y teléfono tengan la información correcta
         */
        if (isset($_POST['reporte_tipo']) && $_POST['reporte_tipo']=="") {
            echo json_encode(array('success' => 0, 'msg' => str_replace('xxx', 'tipo', BIENES_ORFEO_REQUERIDO)));
            return false;
        }        
        if (isset($_POST['reporte_direccion']) && $_POST['reporte_direccion']=="") {
            echo json_encode(array('success' => 0, 'msg' => str_replace('xxx', 'direcci&oacute;n', BIENES_ORFEO_REQUERIDO))); 
            return false;
        }
        if (strlen($_POST['reporte_direccion'])>BIENES_ORFEO_MAXFIELD) {
            echo json_encode(array('success' => 0, 'msg' => str_replace('xxx', 'direcci&oacute;n', BIENES_ORFEO_MAYOR)));
            return false;
        }        
        /*if (isset($_POST['predial']) && $_POST['predial']=="") {
            echo json_encode(array('success' => 0, 'msg' => str_replace('xxx', 'predial', BIENES_ORFEO_REQUERIDO)));
            return false;
        }
        if (isset($_POST['nombre']) && $_POST['nombre']=="") {
            echo json_encode(array('success' => 0, 'msg' => str_replace('xxx', 'nombre', BIENES_ORFEO_REQUERIDO))); 
            return false;
        }
        if (strlen($_POST['nombre'])>BIENES_ORFEO_MAXFIELD) {
            echo json_encode(array('success' => 0, 'msg' => str_replace('xxx', 'nombre', BIENES_ORFEO_MAYOR)));
            return false;
        }
        if (isset($_POST['correo']) && $_POST['correo']=="") {
            echo json_encode(array('success' => 0, 'msg' => str_replace('xxx', 'correo', BIENES_ORFEO_REQUERIDO)));
            return false;
        }*/
        if (isset($_POST['reporte_correo']) && $_POST['reporte_correo']!=="") {
            if(!preg_match("/^[_a-z0-9-]+(\.[_a-z0-9-]+)*@[a-z0-9-]+(\.[a-z0-9-]+)*(\.[a-z]{2,3})$/", $_POST['reporte_correo'])) {
                echo json_encode(array('success' => 0, 'msg' => BIENES_ORFEO_EMAIL));
                return false;
            }
        }
        /*if (isset($_POST['telefono']) && $_POST['telefono']=="") {
            echo json_encode(array('success' => 0, 'msg' => str_replace('xxx', 'tel&eacute;fono', BIENES_ORFEO_REQUERIDO)));
            return false;
        }*/
        if (isset($_POST['reporte_telefono']) && $_POST['reporte_telefono']!=="") {
            if(!preg_match("/^\d+$/", $_POST['reporte_telefono'])) {
                echo json_encode(array('success' => 0, 'msg' => str_replace('xxx', 'tel&eacute;fono', BIENES_ORFEO_NUMTEL)));
                return false;
            }
        }
        
        /*$archivo_name = $_FILES['reporte_fotografia']['name'];
        $tipo = $_FILES['reporte_fotografia']['type'];
        $archivo_size = $_FILES['reporte_fotografia']['size'];
        $archivo = $_FILES['reporte_fotografia']['tmp_name'];
        
        if ($tipo) {
            if (!validar_foto_directorio($tipo, $archivo_name)) {
                echo json_encode(array('success' => 0, 'msg' => BIENES_ORFEO_IMAGEN));
                return false;
            }//if
        } // if
        
        if (!$archivo_name || $archivo_size <= 0) {
            echo json_encode(array('success' => 0, 'msg' => BIENES_ORFEO_IMAGEN2));
            return false;
        }*/
        $objBienes = new gstApi();
        if ($_FILES['reporte_fotografia']['name']!="") {
            $target_path = "mod/Bienes/img/uploads/";
            $img = explode('.', basename( $_FILES['reporte_fotografia']['name']));
            $target_path = $target_path.$img[0]."_".strtotime(date("Y-m-d G:i:s")).".".$img[1]; 
            if(move_uploaded_file($_FILES['reporte_fotografia']['tmp_name'], $target_path)) { 
                echo json_encode($objBienes->reportarPredio($_POST['reporte_tipo'], $target_path, 
                                                            $_POST['reporte_direccion'], $_POST['reporte_num_predial'], 
                                                            $_POST['reporte_nombre'], $_POST['reporte_correo'], 
                                                            $_POST['reporte_telefono'], $_POST['reporte_coordenadas'], 
                                                            $_POST['reporte_equipo'], $_POST['reporte_cedula']));
            } else {
                echo json_encode(array('success' => 0, 'msg' => BIENES_ORFEO_IMAGEN4));
            }
        } else {
            echo json_encode($objBienes->reportarPredio($_POST['reporte_tipo'], "", 
                                                        $_POST['reporte_direccion'], $_POST['reporte_num_predial'], 
                                                        $_POST['reporte_nombre'], $_POST['reporte_correo'], 
                                                        $_POST['reporte_telefono'], $_POST['reporte_coordenadas'], 
                                                        $_POST['reporte_equipo'], $_POST['reporte_cedula']));
        }
    }
    
    function mostrarConstrucciones() {
        $objBienes = new gstApi();
        echo json_encode($objBienes->mostrarConstrucciones());
    }
    
    function mostrarAmoblamientos() {
        $objBienes = new gstApi();
        echo json_encode($objBienes->mostrarAmoblamientos($_POST['tipo']));
    }

} // class
?>