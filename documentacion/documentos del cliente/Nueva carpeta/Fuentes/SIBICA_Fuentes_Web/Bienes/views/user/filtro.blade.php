<div class="panel panel-default">
  <div class="panel-body">
    <h2>{{BIENES_LABEL_FILTRAR}}</h2>
    {{--*/
    $objBienes = new gstApi();
    $capas = $objBienes->capas((isset($_SESSION['Bienes']['conectado'])?'interno':'externo'));

    foreach ($capas['data'] as $id=>$capa) {
        echo "<span style='border-color: $capa[1];'>&nbsp;<input type='checkbox' value='$id' name='capa[]' id='capa' checked /> </span>&nbsp;$capa[0]<br />";
    }
    /*--}}
    <span style='border-color: #000000'>&nbsp;<input type='checkbox' value='const' name='capa[]' id='capa' /> </span>&nbsp;{{BIENES_FILTRO_CONST}}<br />
    <span style='border-color: #00FF00'>&nbsp;<input type='checkbox' value='amob1' name='capa[]' id='capa' /> </span>&nbsp;{{BIENES_FILTRO_AMOBL1}}<br />
    <span style='border-color: #00FF00'>&nbsp;<input type='checkbox' value='amob2' name='capa[]' id='capa' /> </span>&nbsp;{{BIENES_FILTRO_AMOBL2}}<br />
    <span style='border-color: #00FF00'>&nbsp;<input type='checkbox' value='amob3' name='capa[]' id='capa' /> </span>&nbsp;{{BIENES_FILTRO_AMOBL3}}<br />
    <br />    
    <input type="button" id="filtrarSubmit" class="btn btn-default" value="{{BIENES_FILTRAR}}" />
  </div>
</div>