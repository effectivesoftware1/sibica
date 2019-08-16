<?php

use \Firebase\JWT\JWT;

/**
* Clase encargada de controlar la seguridad del consumo de los servicios o Rest API
*/
class gstApiAuth {

	/**
	* Cadena de caracteres cifrada o token
	*/
	public $token;
	
	/**
	* Llave secreta para codificar el token
	*/
	public $key;
	
	/**
	* Datos no cifrados de un token
	*/
	public $data;
	
	/**
	* Tiempo de vigencia de un token desde que es creado
	*/
	public $expFormat;
	/**
	* cliente  url iss
	*/
	public $iss;
        
	public function __construct(){
            $ApiKeyToken = sxMod::getVar(PARTICIPACIONCIUDADANA, 'ApiKeyToken');
            $formato = sxMod::getVar(PARTICIPACIONCIUDADANA, 'formato');
            $expFormat = sxMod::getVar(PARTICIPACIONCIUDADANA, 'expFormat');
            $this->iss = sxMod::getVar(PARTICIPACIONCIUDADANA, 'issCliente');
        
            $this->key = $ApiKeyToken; 
            $this->expFormat = '+'.$expFormat.' '.$formato;
	}

	/**
	* Permite setear los atributos de token y data con los datos decodificados
	* @param $token string Cadena de caracteres con la información codificada
	*/
	public function init($token){
		$this->token = $token;
		$this->data = $this->decode();
	} // init

	/**
	* Permite codificar la información para generar el string del token
	* @param  $data  Array  Array con los datos para generar el token
	* @return        string Cadena de caracteres con la información codificada o token
	*/
	public function encode($data){
		return JWT::encode($data, $this->key);
	} // encode

	/**
	* Permite decodificar la información del token
	* @return $data Array Array con los datos del token
	*/
	public function decode(){
           // return date('Y-m-d H:i:s', $data->exp);
		$data = JWT::decode($this->token, $this->key, array('HS256'));
		$data->create_at = date('Y-m-d H:i:s', $data->iat);
		$data->expire_at = date('Y-m-d H:i:s', $data->exp);
		return $data;
	} // decode

	/**
	* Permite crear la configuración de un nuevo token
        * @param $email string con el email del usuario logueado
	*/
	public function create($datos){
		$email=$datos['email'];
                $id=$datos['id'];
		$token = array(
			"iss" => $this->iss , 
			"iat" => strtotime('now'),
			"exp" => strtotime($this->expFormat),
			"user" => array(
				'email' => $email,
                                'id'=>$id
    			)
		);
		$this->token = $this->encode($token);
	} // create

	/**
	* Verifica si el usuario se encuentra registrado y puede usar el servicio
	* @return Boolean Bandera que indica si el usuario se encuentra en el sistema 
	*/
	public function userAllow(){
            //usuarios registrados aqui
	     $objGstUsuarios = new gstUsuarios();
             $lstUser=$objGstUsuarios->lstUsuariosRegistrados();
             foreach($lstUser as $row):
                 $lst[]=$row['email'];
             endforeach;
             return in_array($this->data->user->email, $lst);
	} // userAllow
/*
 * funcion para expirar un token en el logout
 */
        public function destruir(){
          $fec=strtotime('now');
          $this->expFormat=$fec;
	  $this->token = '';
          $this->data = '';
        }
}