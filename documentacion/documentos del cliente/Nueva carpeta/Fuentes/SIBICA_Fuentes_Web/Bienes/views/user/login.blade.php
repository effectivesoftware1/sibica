<div class="panel panel-default">
  <form class="form-horizontal panel-body">
    <div class="form-group">
      <label for="usuLDAP" class="col-sm-2 control-label">{{BIENES_USUARIO}}</label>
      <div class="col-sm-10">
          <input type="text" id="usuLDAP" class="form-control" />
      </div>
    </div>
    <div class="form-group">
      <label for="buscar" class="col-sm-2 control-label">{{BIENES_PASSWORD}}</label>
      <div class="col-sm-10">
        <input type="password" id="passLDAP" class="form-control" />
      </div>
    </div>
    <div class="form-group">
      <div class="col-sm-offset-2 col-sm-10">
        <a href="#" id="loginSubmit" class="btn btn-default">{{BIENES_CONECTAR}}</a>
      </div>
    </div>
  </form>
</div>

<span id="redireccion" data-url="{{genUrl('Bienes', 'user', 'main')}}"></span>