<div class="panel panel-default">
  <form class="form-horizontal panel-body">
    <div class="form-group">
      <label for="tipo" class="col-sm-2 control-label">{{BIENES_LABEL_BUSQUEDA}}</label>
      <div class="col-sm-10">
        <select id="tipo" class="form-control">
            <option value="1">{{BIENES_PREDIO_DIRECCION}}</option>
            <option value="2">{{BIENES_PREDIO_MATRICULA}}</option>
            <option value="3">{{BIENES_PREDIO_NUMPREDIAL}}</option>
            <option value="4">{{BIENES_PREDIO_BARRIO}}</option>
            <option value="5">{{BIENES_PREDIO_COMUNA}}</option>
        </select>
      </div>
    </div>
    <div class="form-group">
      <label for="buscar" class="col-sm-2 control-label">Valor buscar</label>
      <div class="col-sm-10">
        <input type="text" id="buscar" class="form-control" autocomplete="off" placeholder="{{BIENES_DATO}}" />{{dspMsgAyudaTooltip(BIENES_BUSCAR_AYUDA)}}
      </div>
    </div>
    <div class="form-group">
      <div class="col-sm-offset-2 col-sm-10">
        <a href="#" id="consulta" class="btn btn-default">{{BIENES_BUSCAR}}</a>
      </div>
    </div>
  </form>
</div>