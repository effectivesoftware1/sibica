<?php

/**
 * configGlosario
 *
 * @package Glosario
 * @subpackage Configuracion
 * @version  2009/08/23 9:45
 */

/**
 * Clase de configuracion para el mapeo de las tablas del Glosario.
 *
 *
 * @package Glosario
 * @subpackage Configuracion
 * @copyright Copyright {@link http://www.nexura.com nexura} 2009
 * @version Bienes v1.0
 *
 * @author James Romero <jromero@nexura.com>
 */
use Tribunet\configBlade as View;

class configBienes extends Utils\controller {
    protected $listModMenu = []; // Registrar opciones del menú
    protected $listModPath = []; // Configuración rastro de miga
    protected $optionsByFunction = []; // Mapear en qué funciones se van a mostrar

    /**
     * Generación de menús en iconos
     * 
     * Se genera en capa modPath-modMenuContainer, los enlaces tipo icono y su
     * forma de abrir
     */
    protected function getOptions() {
        $htmlLogin = View::make('user.login');
        $htmlBuscar = View::make('user.buscar');
        $htmlFiltro = View::make('user.filtro');
        $htmlNomenclatura = View::make('user.nomenclatura');

        $this->listModMenu = [
            'login' => [
                'collapse'=>['html'=>$htmlLogin],
                'id' => 'modLogin',
                'ico' => 'sign-in',
                'title' => 'Login'
            ],
            'logout' => [
                'id' => 'modLogout',
                'ico' => 'sign-out',
                'title' => 'Logout',
                'href' => genUrl('Bienes', 'user', 'logout')
            ],
            'buscar' => [
                'collapse'=>['html'=>$htmlBuscar],
                'id' => 'modBuscar',
                'ico' => 'search-plus',
                'title' => 'Buscar'
            ],
            'filtro' => [
                'collapse'=>['html'=>$htmlFiltro],
                'id' => 'modFiltro',
                'ico' => 'filter',
                'title' => 'Filtros'
            ],
            'nomenclatura' => [
                'collapse'=>['html'=>$htmlNomenclatura],
                'id' => 'modNomenclatura',
                'ico' => 'cogs',
                'title' => 'Nomenclatura Amoblamientos'
            ],
            'pdf' => [
                'id' => 'modPDF',
                'ico' => 'file-pdf-o',
                'title' => 'Manual SIBICA',
                'href' => '/bienes/manual/'
            ]
        ];

        /**
         * Configuración de cada una de las opciones de modPath
         * obligatoria, inicializar vacia 
         */
        $this->listModPath = [
//            'termino' => [
//                'title' => GLOSARIO_MOD,echo
//            ],
        ];

        /**
         * Mapeo de modMenu y modPath para cada una de las funciones
         */
        $this->optionsByFunction = [
            'user' => [
                'main' => [
                    'modPath' => [],
                    'modMenu' => ['login', 'logout', 'buscar', 'filtro'],
                ],
            ],
        ];
    }
    
    function genUrl($lTipo = 'user', $lFuncion = 'main', $args = array()) {
        if ($lTipo == 'user') {
            if ($lFuncion == 'main') {
                $url = 'bienes';
                return $url; //.'/loader.php?lServicio=Bienes';
            }
        }
        return false;
    }    
}
?>
