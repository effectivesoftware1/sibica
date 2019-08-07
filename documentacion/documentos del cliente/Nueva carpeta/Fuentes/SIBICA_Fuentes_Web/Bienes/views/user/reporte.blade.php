<div align="right"><a href="#" class="linkForm" onclick="javascript: $('#reporteIrregularidad').fadeOut(1000);">X</a></div>
<caption><span id="tituloLogin">{{BIENES_ORFEO}}</span></caption>
<div class="panel panel-default">
  <div id="errorMsg"></div><br />
  <form class="form-horizontal panel-body" id="reporteForm" method="POST" autocomplete="off" enctype="multipart/form-data">
    <fieldset>
        <div class="form-group">
          <label class="col-sm-2 control-label"for="reporte_tipo">{{BIENES_ORFEO_TIPO}}</label> 
          <div class="col-sm-10">
            <select id="reporte_tipo" name="reporte_tipo" class="form-control">
                <option value="Invasion">Invasi&oacute;n</option>
                <option value="Fraude">Fraude</option>
                <option value="Otros">Otros</option>
            </select>
          </div>
        </div>
        <div class="form-group">
          <label class="col-sm-2 control-label"for="reporte_fotografia">{{BIENES_ORFEO_FOTO}}</label> 
          <div class="col-sm-10">
             <input type="file" id="reporte_fotografia" name="reporte_fotografia" class="form-control" /> 
          </div>
        </div>
        <div class="form-group">
          <label class="col-sm-2 control-label"for="reporte_direccion">{{BIENES_ORFEO_DIRECCION}}</label> 
          <div class="col-sm-10">
            <input type="text" id="reporte_direccion"  name="reporte_direccion" class="form-control" required /> 
          </div>
        </div>
        <div class="form-group">
          <label class="col-sm-2 control-label"for="reporte_num_predial">{{BIENES_ORFEO_NUMPREDIAL}}</label> 
          <div class="col-sm-10">
             <input type="text" id="reporte_num_predial"  name="reporte_num_predial" class="form-control" required /> 
          </div>
        </div>

    <!--     <th colspan="5">&nbsp;  
         <th colspan="5"><center>{{BIENES_ORFEO_OPCIONALES}}</center>  -->
        <div class="form-group">
          <label class="col-sm-2 control-label"for="reporte_nombre">{{BIENES_ORFEO_NOMBRE}}</label> 
          <div class="col-sm-10">
             <input type="text" id="reporte_nombre" name="reporte_nombre" class="form-control" /> 
          </div>
        </div>
        <div class="form-group">
          <label class="col-sm-2 control-label"for="reporte_cedula">{{BIENES_ORFEO_CEDULA}}</label> 
          <div class="col-sm-10">
             <input type="text" id="reporte_cedula" name="reporte_cedula" class="form-control" /> 
          </div>
        </div>
        <div class="form-group">
          <label class="col-sm-2 control-label"for="reporte_correo">{{BIENES_ORFEO_CORREO}}</label> 
          <div class="col-sm-10">
              <input type="email" id="reporte_correo" name="reporte_correo" class="form-control" /> 
          </div>
        </div>
        <div class="form-group">
          <label class="col-sm-2 control-label"for="reporte_telefono">{{BIENES_ORFEO_TELEFONO}}</label> 
          <div class="col-sm-10">
              <input type="number" id="reporte_telefono" name="reporte_telefono" class="form-control" /> 
          </div>
        </div>
        <div class="form-group">
          <div class="col-sm-offset-2 col-sm-3">
            <!--<input type="button" id="loginSubmitReporte" value="{{BIENES_ORFEO_ENVIAR}}" />-->
            <input type="submit" id="loginSubmitReporte" value="{{BIENES_ORFEO_ENVIAR}}" />
            <input type="hidden" id="reporte_coordenadas" name="reporte_coordenadas" />
            <input type="hidden" id="reporte_equipo" name="reporte_equipo" value="{{$_SERVER['REMOTE_ADDR']}}" />
          </div>
        </div>
    </fieldset>
  </form>
</div>
<span id="redireccion" data-url="{{genUrl('Bienes', 'user', 'main')}}"></span>