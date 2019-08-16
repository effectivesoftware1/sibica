<div id="busqueda">
    <div align="right"><a href="#" class="links" onclick="javascript: $('#busquedaPpal').hide(1000);">X</a></div>
    <table id="predios" border="1" cellpadding="1" cellspacing="1" width="100%">
        <thead>
            <tr style="font-size: 12px">
                <th>{{BIENES_DIRECCION}}</th>
                <th>{{BIENES_COMUNA}}</th>
                <th>{{BIENES_BARRIO}}</th>
                <th>{{BIENES_MATRICULA}}</th>
                <th>{{BIENES_PREDIAL}}</th>
                <th>{{BIENES_TIPO}}</th>
                <th>{{BIENES_GSV}}</th>
            </tr>
        </thead>
        <tbody></tbody>
    </table>

    <span id="tablaPredioGral" data-msg="{{BIENES_MSG_USUARIO}}" data-predialterreno="{{BIENES_PREDIAL_TERRENO}}" 
          data-predialconst="{{BIENES_PREDIAL_CONSTRUCCION}}" data-dir="{{BIENES_DIRECCION}}" 
          data-tipobien="{{BIENES_TIPO_BIEN}}" data-nombrepro="{{BIENES_NOMBRE_PROYECTO}}" 
          data-nombrearea="{{BIENES_NOMBRE_AREA_CEDIDA}}" data-nombreconst="{{BIENES_NOMBRE_CONSTRUCCION}}" 
          data-areaedi="{{BIENES_AREA_EDIFICADA}}" data-dirconst="{{BIENES_DIRECCION_CONSTRUCCION}}" 
          data-matricula="{{BIENES_MATRICULA}}" data-areaced="{{BIENES_AREA_CEDIDA}}" 
          data-nombrecomun="{{BIENES_NOMBRE_COMUN}}"></span>
    <span id="tablaPredioExtra" data-codigonal="{{BIENES_CODIGONAL}}" data-numactivo="{{BIENES_NUM_ACTIVOFIJO}}"
          data-estrato="{{BIENES_ESTA_MODA}}" data-cedente="{{BIENES_CEDENTE}}" 
          data-nombrecb="{{BIENES_NOMBRE_CB}}" data-orfeocb="{{BIENES_ORFEO_CB}}" 
          data-tipobien="{{BIENES_NOMBRE_TB}}" data-tipou="{{BIENES_NOMBRE_TU}}" 
          data-nombremadq="{{BIENES_NOMBRE_MADQ}}" data-lindnorte="{{BIENES_LIND_NORTE}}" 
          data-lindsur="{{BIENES_LIND_SUR}}" data-lindeste="{{BIENES_LIND_ESTE}}" 
          data-lindoeste="{{BIENES_LIND_OESTE}}" data-lindadic="{{BIENES_LIND_ADIC}}" 
          data-estadopredio="{{BIENES_ESTADO_PREDIO}}" data-nombretipo="{{BIENES_NOMBRE_TIPO}}" 
          data-numdoc="{{BIENES_NUMDOC}}" data-fechadoc="{{BIENES_FECHA_DOC}}" 
          data-contnombre="{{BIENES_CONTRATO_NOMBRE}}" data-connumero="{{BIENES_CONTRATO_NUMERO}}" 
          data-contarea="{{BIENES_CONTRATO_AREA_ENTREGADA}}" data-contfecini="{{BIENES_CONTRATO_FEC_INI}}" 
          data-contfecfin="{{BIENES_CONTRATO_FEC_FIN}}" data-contestado="{{BIENES_CONTRATO_ESTADO}}" 
          data-contlindnorte="{{BIENES_CONTRATO_LIND_NORTE}}" data-contlindsur="{{BIENES_CONTRATO_LIND_SUR}}" 
          data-contlideste="{{BIENES_CONTRATO_LIND_ESTE}}" data-contlindoeste="{{BIENES_CONTRATO_LIND_OESTE}}" 
          data-contlindadic="{{BIENES_CONTRATO_LIND_ADIC}}" data-conttercero="{{BIENES_TERCERO}}" 
          data-numpisos="{{BIENES_NUMPISOS}}" data-actfijo="{{BIENES_ACTFIJO_CONSTRUCCION}}" 
          data-nombredependencia="{{BIENES_NOMBRE_DEPENDENCIA}}" data-pathfotos="{{BIENES_PATH_FOTOS}}" 
          data-patharchivos="{{BIENES_PATH_ARCHIVOS}}" data-obsfecha="{{BIENES_OBSERVACION_FECHA}}" 
          data-observacion="{{BIENES_OBSERVACION}}" data-usunombre="{{BIENES_USUARIO_NOMBRE}}" 
          data-usuapellido="{{BIENES_USUARIO_APELLIDO}}" data-url1="{{BIENES_URL1}}" 
          data-url2="{{BIENES_URL2}}" data-url3="{{BIENES_URL3}}"></span>              

    <table id="infoPredio" border="1" cellpadding="1" cellspacing="1" width="100%">
        <tr>
            <td>
                <table id="infoPredioGral" border="1" cellpadding="1" cellspacing="1" width="100%">
                    <tbody></tbody>
                </table>
            </td>
        </tr>
        <tr>
            <td>&nbsp;</td>
        </tr>
        <tr>
            <td>
            <table id="infoPredioExtra" border="1" cellpadding="1" cellspacing="1" width="100%">
                <tbody></tbody>
            </table>            
            </td>
        </tr>
    </table>
    <br />
</div>