var navegador = navigator.userAgent;
var caliPolygon = new Array();
var constPolygon = new Array();
var lineaAmobPolygon = new Array()
var poliAmobPolygon = new Array()
var puntoAmobMarkers = [];
var markerLogos = [];
var markerLogo = new Array();
var markerCluster;
var map;
var marker;
var geocoder;
var dataIni = {
    zoom: 17,
    center: {
        lat: 3.42319368365869,
        lng: -76.5148200529943
//    zoom: $("#coordenadas', 'data-zoom'),
//    center: {
//        lat: $("#coordenadas', 'data-lat'), // TOMARLO DE UN <span id="coordenadas" data-lat="" data-lng="" data-zoom=""></span>
//        lng: $("#coordenadas', 'data-lng')
    }
};
var infoWindow = new google.maps.InfoWindow({
    content: ''
});

/**
 * Inicializa el objeto de Google Maps, cargando las coordenadas de los poligonos;
 * se muestra un infoWindow para la información general del predio
 */
function initMap() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(function(objPosition) {
            dataIni = {
                zoom: 17,
                center: {
                    lat: objPosition.coords.latitude,
                    lng: objPosition.coords.longitude
                }
            };        
        });
    }
    
    map = new google.maps.Map(document.getElementById('map'), dataIni);

    // Cuando el zoom cambie, se debe reiniciar el marker clusterer, sino las imágenes aparecerán
    google.maps.event.addListener(map, 'zoom_changed', function(){
        markerCluster = new MarkerClusterer(map, markerLogos, {imagePath: 'mod/Bienes/img/images/m'});
    });

    // Sólo cuando el mapa se mueva, se mostrarán los predios
    //google.maps.event.addListener(map, 'dragend', mostrarPredios);

    // Los primeros predios a mostrar cuando se cargue el mapa
    google.maps.event.addListenerOnce(map, 'tilesloaded', mostrarPredios);
}

// Hará la consulta ajax al "WS" que trae los predios, dependiendo de las coordenadas como filtro
function mostrarPredios() {
    $.ajax({
        type: 'POST',
        url: '/loader.php?lServicio=Bienes&lFuncion=mostrarPredios',
        dataType: 'json',
        data: {
            coords: map.getCenter().toUrlValue().toString()
        },
        success: function (data) {
            if (data.success == 1) {
                var contenido = '';
                var reporte = '';
                for (var clave in data.data) {

                    /*
                     * Se coloca un marker con el logo de la alcaldía o un 
                     * ladrón, si el predio ha sido reportado
                     */
                    reporte = '';
                    if (data.data[clave]['fraude']['tipo'] != '') {
                        var latlng = data.data[clave]['contenido']['coordenadas'].split(', ');

                        // Se evita pintar el mismo punto 2 veces
                        if (typeof markerLogo[clave]==='undefined') {
                            markerLogo[clave] = 1;
                            var markerLog = new google.maps.Marker({
                                position: new google.maps.LatLng(latlng[0], latlng[1]),
                                icon: data.data[clave]['fraude']['img'],
                                map: map,
                                title: data.data[clave]['fraude']['tipo'],
                                strokeColor: data.data[clave]['color']
                            });

                            markerLogos.push(markerLog);
                            /*
                             * Si un predio ya ha sido reportado y no está resuelto, 
                             * no se puede volver a reportar
                             */
                            if (data.data[clave]['fraude']['estado'].toLowerCase() == $("#coordenadas").attr('data-estado')) {
                                reporte = '<a class="linkInfoWindow" href="#" onclick="reportePredio(\'' + data.data[clave]['contenido']['predial'] + '\', \'' + data.data[clave]['contenido']['direccion'] + '\', \'' + data.data[clave]['contenido']['coordenadas'] + '\')" title="Reportar irregularidad">' +
                                        '  <span><span class="fa fa-inbox fa-2x"></span><span class="text hidden">Reportar irregularidad</span></span>' +
                                        '</a>';
                            }
                        }
                    } else {
                        reporte = '<a class="linkInfoWindow" href="#" onclick="reportePredio(\'' + data.data[clave]['contenido']['predial'] + '\', \'' + data.data[clave]['contenido']['direccion'] + '\', \'' + data.data[clave]['contenido']['coordenadas'] + '\')" title="Reportar irregularidad">' +
                                '  <span><span class="fa fa-inbox fa-2x"></span><span class="text hidden">Reportar irregularidad</span></span>' +
                                '</a>';
                    }



                    contenido = '<table border="0">' +
                            '    <tbody>' +
                            '        <tr>' +
                            '            <th colspan="2">' + data.data[clave]['contenido']['nombre'] + '</th></tr>' +
                            '            <tr><th align="right">Dirección:</th>' +
                            '            <td>' + data.data[clave]['contenido']['direccion'] + '</td>' +
                            '        </tr>' +
                            '        <tr>' +
                            '            <th align="right">Tipo:</th>' +
                            '            <td>' + data.data[clave]['contenido']['tipo'] + '</td>' +
                            '        </tr>' +
                            '        <tr>' +
                            '            <th align="right">Predial:</th>' +
                            '            <td>' + data.data[clave]['contenido']['predial'] + '</td>' +
                            '        </tr>' +
                            '        <tr>' +
                            '            <th align="right">Matricula:</th>' +
                            '            <td>' + data.data[clave]['contenido']['matricula'] + '</td>' +
                            '        </tr>' +
                            '        <tr>' +
                            '            <td align="center">' +
                            '                <a class="linkInfoWindow" href="#" onclick="informacionPredio(\'' + data.data[clave]['contenido']['predial'] + '\')" title="Ver más">' +
                            '                    <span><span class="fa fa-search-plus fa-2x"></span><span class="text hidden">Ver más</span></span>' +
                            '                </a>' +
                            '            </td>' +
                            '            <td valign="top">' +
                            '                <a class="linkInfoWindow" target="_blank" href="http://maps.google.com/maps?q=&amp;layer=c&amp;cbll=' + data.data[clave]['contenido']['coordenadas'] + '&amp;cbp=12,0,0,0,0" title="Google Street View">' +
                            '                    <span><span class="fa fa-street-view fa-2x"></span><span class="text hidden">Google Street View</span></span>' +
                            '                </a>&nbsp;' + reporte +
                            '            </td>' +
                            '        </tr>' +
                            '    </tbody>' +
                            '</table>';

                    // Para evitar que se cargue de nuevo el polígono
                    if (typeof caliPolygon[clave] === 'undefined') {
                        // Se construye el poligono a mostrar en el mapa
                        caliPolygon[clave] = new google.maps.Polygon({
                            paths: data.data[clave]['LatLng'], //eval(polygonCoords),
                            strokeColor: data.data[clave]['color'],
                            strokeOpacity: 0.8,
                            strokeWeight: 2,
                            fillColor: data.data[clave]['color'],
                            fillOpacity: 0.35,
                            //content: data.data[clave]['contenido']
                            content: contenido
                        });
                        caliPolygon[clave].setMap(map);

                        /*
                         * Se añade un evento para mostrar un infoWindow al darle 
                         * click al predio
                         */
                        google.maps.event.addListener(caliPolygon[clave], 'click', function (evt) {
                            infoWindow.setContent(this.content);
                            infoWindow.open(map, this);
                            infoWindow.setPosition(evt.latLng);
                        });
                    }
                }
                markerCluster = new MarkerClusterer(map, markerLogos, {imagePath: 'mod/Bienes/img/images/m'});
            } else {
                alert(data.msg);
            }
        }
    });    
}

/**
 * Evento click del botón 'Buscar'
 */
var direccion;
$("#consulta").click(function () {
    $.ajax({
        type: 'POST',
        url: '/loader.php?lServicio=Bienes&lFuncion=buscarPoligonos',
        dataType: 'json',
        data: {
            opcion: $("#tipo").val(),
            valor: $("#buscar").val()
        },
        success: function (data) {
            if (data.success == 1) {
                if (navigator.userAgent.indexOf('Firefox') != -1) {
                    $("#predios td").remove();
                } else if (navigator.userAgent.indexOf('Chrome') != -1) {
                    $("#predios td").remove();
                }
                
                var lat;
                var lng;
                for (var x = 0; x < data.data.length; x++) {
                    // Se agregan a la tabla predios, todos los nodos encontrados
                    $('#predios').append('<tr style = "font-size: 12px">' +
                            '  <td>' +
                            '    <a href="#" onclick="irPosicion(' + data.data[x].latitud + ', ' + data.data[x].longitud + ', 18);">'
                            + data.data[x].direccion +
                            '    </a>' +
                            '  </td>' +
                            '  <td align="center">' + data.data[x].comuna + '</td>' +
                            '  <td>' + data.data[x].barrio + '</td>' +
                            '  <td align="center">' + data.data[x].matricula + '</td>' +
                            '  <td align="center">' + data.data[x].predial + '</td>' +
                            '  <td>' + data.data[x].tipo + '</td>' +
                            '  <td>' +
                            '    <a target="_blank" href="http://maps.google.com/maps?q=&layer=c&cbll=' + data.data[x].longitud + ', ' + data.data[x].latitud + '&cbp=12,0,0,0,0" title="Ver m&aacute;s">'
                            + '<span><span class="fa fa-street-view fa-2x"></span></span>' +
                            '    </a>' +
                            '  </td>' +
                            '</tr>');
                    
                    lat = data.data[x].latitud;
                    lng = data.data[x].longitud;
                }

                /*
                 * Si se busca por barrio o comuna, se reposiciona el mapa en
                 * una coordenada a zoom 18
                 */
                if ($("#tipo").val()>=4) {
                    map.setCenter(new google.maps.LatLng(lng, lat));
                    map.setZoom(16);
                }


                /*
                 * Se modifica el tamaño de la capa de búsqueda, porque se 
                 * comparte con la información del predio
                 */
                $('#busqueda').css({'height': '505px'});
                $("#predios").show();
                $("#infoPredio").hide();
                $("#busquedaPpal").show();
                $("#reporteIrregularidad").hide();
            } else {
                /*
                 * En caso de hacer la búsqueda por dirección y no se 
                 * encuentra nada, se hará búsqueda por google maps
                 */
                if ($("#tipo").val() == '1') {
                    var geocoder = new google.maps.Geocoder();
                    // Obtenemos la dirección y la asignamos a una variable
                    direccion = $("#buscar").val();
                    // Creamos el Objeto Geocoder
                    var geocoder = new google.maps.Geocoder();
                    // Hacemos la petición indicando la dirección e invocamos la función
                    // geocodeResult enviando todo el resultado obtenido
                    geocoder.geocode({'address': direccion + ", Santiago de Cali, Valle del Cauca, Colombia"}, geocodeResult);
                } else {
                    alert(data.msg);
                }
            }

            $("#buscar").focus();
        }
    })
});

/**
 * Busca en la DB la información general del predio, para mostrar en la tabla infoPredio
 * 
 * @param {string} predio
 */
function informacionPredio(predio) {
    $.ajax({
        type: 'POST',
        url: '/loader.php?lServicio=Bienes&lFuncion=informacionPredio',
        dataType: 'json',
        data: {
            predio: predio
        },
        success: function (data) {
            if (data.success == 1) {
                if (navigator.userAgent.indexOf('Firefox') != -1) {
                    $("#infoPredioGral tr").remove();
                    $("#infoPredioExtra tr").remove();
                } else if (navigator.userAgent.indexOf('Chrome') != -1) {
                    $("#infoPredioGral tr").each(function () {
                        $(this).remove();
                    });
                    
                    $("#infoPredioExtra tr").each(function () {
                        $(this).remove();
                    });
                }
                $('#infoPredioGral').append('<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'msg') + '</th>' +
                        '  <td>' + data.data.general.msg_usuario + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'predialterreno') + '</th>' +
                        '  <td>' + data.data.general.predial_terreno + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'predialconst') + '</th>' +
                        '  <td>' + data.data.general.predial_construccion + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'dir') + '</th>' +
                        '  <td>' + data.data.general.direccion_oficial + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'tipobien') + '</th>' +
                        '  <td>' + data.data.general.tipo_bien + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'nombrepro') + '</th>' +
                        '  <td>' + data.data.general.nombre_proyecto + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'nombrearea') + '</th>' +
                        '  <td>' + data.data.general.nombre_area_cedida + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'nombreconst') + '</th>' +
                        '  <td>' + data.data.general.nombre_construccion + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'areaedi') + '</th>' +
                        '  <td align="center">' + data.data.general.area_edificada + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'dirconst') + '</th>' +
                        '  <td>' + data.data.general.direccion_construccion + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'matricula') + '</th>' +
                        '  <td align="center">' + data.data.general.matricula + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'areaced') + '</th>' +
                        '  <td align="center">' + data.data.general.area_cedida + '</td>' +
                        '</tr>' +
                        '<tr style="font-size: 12px">' +
                        '  <th>' + printSpan('tablaPredioGral', 'nombrecomun') + '</th>' +
                        '  <td>' + data.data.general.nombre_comun + '</td>' +
                        '</tr>');

                if (typeof data.data.extras !== 'undefined') {
                    $("#infoPredioExtra").show();
                    $("#infoPredioExtra").append('<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'codigonal') + '</th>' +
                            '  <td>' + data.data.extras.codigo_nacional + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'numactivo') + '</th>' +
                            '  <td>' + data.data.extras.num_activo_fijo + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'estrato') + '</th>' +
                            '  <td>' + data.data.extras.estrato + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'cedente') + '</th>' +
                            '  <td>' + data.data.extras.cedente + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'nombrecb') + '</th>' +
                            '  <td>' + data.data.extras.nombre_cb + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'orfeocb') + '</th>' +
                            '  <td>' + data.data.extras.orfeo + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'tipobien') + '</th>' +
                            '  <td>' + data.data.extras.nombre_tb + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'tipou') + '</th>' +
                            '  <td>' + data.data.extras.nombre_tu + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'nombremadq') + '</th>' +
                            '  <td>' + data.data.extras.nombre_madq + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'lindnorte') + '</th>' +
                            '  <td>' + data.data.extras.lind_norte_predio + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'lindsur') + '</th>' +
                            '  <td>' + data.data.extras.lind_sur_predio + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'lindeste') + '</th>' +
                            '  <td>' + data.data.extras.lind_este_predio + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'lindoeste') + '</th>' +
                            '  <td>' + data.data.extras.lind_oeste_predio + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'lindadic') + '</th>' +
                            '  <td>' + data.data.extras.lind_adic_predio + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'estadopredio') + '</th>' +
                            '  <td>' + data.data.extras.estado_predio + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'nombretipo') + '</th>' +
                            '  <td>' + data.data.extras.nombre_tipo + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'numdoc') + '</th>' +
                            '  <td>' + data.data.extras.numero_doc + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'fechadoc') + '</th>' +
                            '  <td>' + data.data.extras.fecha_documento + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'contnombre') + '</th>' +
                            '  <td>' + data.data.extras.nombre_not + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'connumero') + '</th>' +
                            '  <td>' + data.data.extras.numero_cont + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'contarea') + '</th>' +
                            '  <td>' + data.data.extras.area_entregada + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'contfecini') + '</th>' +
                            '  <td>' + data.data.extras.fecha_inicio_contrato + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'contfecfin') + '</th>' +
                            '  <td>' + data.data.extras.fecha_fin_contrato + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'contestado') + '</th>' +
                            '  <td>' + data.data.extras.estado_contrato + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'contlindnorte') + '</th>' +
                            '  <td>' + data.data.extras.lind_norte_contrato + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'contlindsur') + '</th>' +
                            '  <td>' + data.data.extras.lind_sur_contrato + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'contlideste') + '</th>' +
                            '  <td>' + data.data.extras.lind_este_contrato + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'contlindoeste') + '</th>' +
                            '  <td>' + data.data.extras.lind_oeste_contrato + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'contlindadic') + '</th>' +
                            '  <td>' + data.data.extras.lind_adic_contrato + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'conttercero') + '</th>' +
                            '  <td>' + data.data.extras.tercero + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'numpisos') + '</th>' +
                            '  <td>' + data.data.extras.numero_pisos_construccion + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'actfijo') + '</th>' +
                            '  <td>' + data.data.extras.activo_fijo_construccion + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'nombredependencia') + '</th>' +
                            '  <td>' + data.data.extras.nombre_dependencia + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'pathfotos') + '</th>' +
                            '  <td>' + data.data.extras.path_archivo_digital + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'patharchivos') + '</th>' +
                            '  <td>' + data.data.extras.path_archivo_fotos + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'obsfecha') + '</th>' +
                            '  <td>' + data.data.extras.fecha_observacion + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'observacion') + '</th>' +
                            '  <td>' + data.data.extras.observacion + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'usunombre') + '</th>' +
                            '  <td>' + data.data.extras.nombre_usuario + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'usuapellido') + '</th>' +
                            '  <td>' + data.data.extras.apellido_usuario + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'url1') + '</th>' +
                            '  <td>' + data.data.extras.url1 + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'url2') + '</th>' +
                            '  <td>' + data.data.extras.url2 + '</td>' +
                            '</tr>' +
                            '<tr style="font-size: 12px">' +
                            '  <th>' + printSpan('tablaPredioExtra', 'url3') + '</th>' +
                            '  <td>' + data.data.extras.url3 + '</td>' +
                            '</tr>');
                } else {
                    console.log('No es Excelente');
                }

                /*
                 * Se modifica el tamaño de la capa de búsqueda, porque se 
                 * comparte con los predios encontrados
                 */
                $('#busqueda').css({'height': '505px'});
                $("#predios").hide();
                $("#infoPredio").show();
                $("#busquedaPpal").show();
                $("#reporteIrregularidad").hide();
            } else {
                alert(data.msg);
            }
        }
    });
}

/**
 * Evento click del botón 'Login'
 */
$("#loginSubmit").click(function () {
    $.ajax({
        type: 'POST',
        url: '/loader.php?lServicio=Bienes&lFuncion=login',
        dataType: 'json',
        data: {
            usuario: $("#usuLDAP").val(),
            password: $("#passLDAP").val()
        },
        success: function (data) {
            if (data.success == 1) {
                // En caso de conectarse bien, se redirecciona al main
                alert(data.msg+"\n"+data.data.nombre);
                location.href = $("#redireccion").data('url');
            } else {
                alert(data.msg);
            }
        }
    });
});

/**
 * Evento click para enviar un reporte
 */
$("#reporteForm").on('submit',(function(e) {
    e.preventDefault();
//$("#loginSubmitReporte").click(function () {
    $.ajax({
        type: 'POST',
        url: '/loader.php?lServicio=Bienes&lFuncion=reporte',
        dataType: 'json',
        data: new FormData(this)/*{
            tipo: $("#reporte_tipo").val(),
            fotografia: $("#reporte_fotografia").val(),
            direccion: $("#reporte_direccion").val(),
            predial: $("#reporte_num_predial").val(),
            nombre: $("#reporte_nombre").val(),
            correo: $("#reporte_correo").val(),
            telefono: $("#reporte_telefono").val(),
            coordenadas: $("#coordenadas").val(),
            equipo: $("#equipo").val()
        }*/,
        contentType: false,       		// The content type used when sending data to the server. Default is: "application/x-www-form-urlencoded"
        cache: false,					// To unable request pages to be cached
        processData:false,  			// To send DOMDocument or non processed data file it is set to false (i.e. data should not be in the form of string)
        success: function (data) {
            if (data.success == 1) {
                var r = confirm("Se generó el reporte # " + data.data);
                if (r == true) {
                    location.href = $("#redireccion").data('url');
                } else {
                    $("#reporteIrregularidad").hide();
                }
            } else {
                $("#errorMsg").html(data.msg);
            }
        }
    });
}));

// Se mostrarán las capas de acuerdo al check seleccionado
$("#filtrarSubmit").click(function () {
    for (var clave in caliPolygon) {
        caliPolygon[clave].setVisible(false);
    }
    if (markerLogos.length > 0) {
        for (var punto in markerLogos) {
            //markerLogos[punto].setMap(null);
            markerLogos[punto].setVisible(false);
        }
    }
    for (var clave in caliPolygon) {
        $(':checkbox:checked').each(function (i) {
            switch ($(this).attr("value")) {
                // 1;"Predio propiedad del Mcpio para la Venta"
                case '1':
                    if (caliPolygon[clave].strokeColor == "#FF0000") {
                        caliPolygon[clave].setVisible(true);
                        if (typeof markerLogos[clave] !== 'undefined') {
                            markerLogos[clave].setVisible(true);
                        }
                    }
                    break;
                    // 2;"Predio Parcialmente del Mcpio para la Venta"
                case '2':
                    if (caliPolygon[clave].strokeColor == "#FF8000") {
                        caliPolygon[clave].setVisible(true);
                        if (typeof markerLogos[clave] !== 'undefined') {
                            markerLogos[clave].setVisible(true);
                        }
                    }
                    break;
                    // 3;"Predio Propiedad del Mcpio"
                case '3':
                    if (caliPolygon[clave].strokeColor == "#800080") {
                        caliPolygon[clave].setVisible(true);
                        if (typeof markerLogos[clave] !== 'undefined') {
                            markerLogos[clave].setVisible(true);
                        }
                    }
                    break;
                    // 4;"Predio Parcialmente Propiedad del Mcpio"
                case '4':
                    if (caliPolygon[clave].strokeColor == "#FFFF00") {
                        caliPolygon[clave].setVisible(true);
                        if (typeof markerLogos[clave] !== 'undefined') {
                            markerLogos[clave].setVisible(true);
                        }
                    }
                    break;
                    // 5;"Construccion Propiedad del Mpio"
                case '5':
                    if (caliPolygon[clave].strokeColor == "#6A6A6A") {
                        caliPolygon[clave].setVisible(true);
                        if (typeof markerLogos[clave] !== 'undefined') {
                            markerLogos[clave].setVisible(true);
                        }
                    }
                    break;
                    // 6;"Zona Verde"
                case '6':
                    if (caliPolygon[clave].strokeColor == "#00FF00") {
                        caliPolygon[clave].setVisible(true);
                        if (typeof markerLogos[clave] !== 'undefined') {
                            markerLogos[clave].setVisible(true);
                        }
                    }
                    break;
                    // 7;"Via Publica"
                case '7':
                    if (caliPolygon[clave].strokeColor == "#FA045D") {
                        caliPolygon[clave].setVisible(true);
                        if (typeof markerLogos[clave] !== 'undefined') {
                            markerLogos[clave].setVisible(true);
                        }
                    }
                    break;
            }
        });
    }

    // En cada click se limpia el mapa de los amoblamientos y las construcciones
    if (lineaAmobPolygon.length > 0) {
        for (var clave in lineaAmobPolygon) {
            lineaAmobPolygon[clave].setVisible(false);
        }
    }
    if (poliAmobPolygon.length > 0) {
        for (var clave in poliAmobPolygon) {
            poliAmobPolygon[clave].setVisible(false);
        }
    }
    if (puntoAmobMarkers.length > 0) {
        for (var punto in puntoAmobMarkers) {
            puntoAmobMarkers[punto].setVisible(false);
        }
    }
    if (constPolygon.length > 0) {
        for (var clave in constPolygon) {
            constPolygon[clave].setVisible(false);
        }
    }
    $(':checkbox:checked').each(function (i) {
        switch ($(this).attr("value")) {
            case 'const':
                // Sólo se hará la consulta a construcciones una vez
                if (constPolygon.length == 0) {
                    $.ajax({
                        type: 'POST',
                        url: '/loader.php?lServicio=Bienes&lFuncion=mostrarConstrucciones',
                        dataType: 'json',
                        success: function (data) {
                            if (data.success == 1) {
                                var contenido = '';
                                for (var clave in data.data) {
                                    contenido = '<table border="0">' +
                                            '    <tbody>' +
                                            '        <tr>' +
                                            '            <th>' + data.data[clave]['capa'] + '</th></tr>' +
                                            '        </tr>' +
                                            '        <tr>' +
                                            '            <td valign="top">' +
                                            '                <a class="linkInfoWindow" target="_blank" href="http://maps.google.com/maps?q=&amp;layer=c&amp;cbll=' + data.data[clave]['coordenadas'] + '&amp;cbp=12,0,0,0,0" title="Google Street View">' +
                                            '                    <span><span class="fa fa-street-view fa-2x"></span><span class="text hidden">Google Street View</span></span>' +
                                            '                </a>&nbsp;' +
                                            '            </td>' +
                                            '        </tr>' +
                                            '    </tbody>' +
                                            '</table>';

                                    // Se construye el poligono a mostrar en el mapa
                                    constPolygon[clave] = new google.maps.Polygon({
                                        paths: data.data[clave]['LatLng'], //eval(polygonCoords),
                                        strokeColor: data.data[clave]['color'],
                                        strokeOpacity: 0.8,
                                        strokeWeight: 2,
                                        fillColor: data.data[clave]['color'],
                                        fillOpacity: 0.35,
                                        //content: data.data[clave]['contenido']
                                        content: contenido
                                    });
                                    constPolygon[clave].setMap(map);

                                    /*
                                     * Se añade un evento para mostrar un infoWindow al darle 
                                     * click al predio
                                     */
                                    google.maps.event.addListener(constPolygon[clave], 'click', function (evt) {
                                        infoWindow.setContent(this.content);
                                        infoWindow.open(map, this);
                                        infoWindow.setPosition(evt.latLng);
                                    });
                                }
                            } else {
                                alert(data.msg);
                            }
                        }
                    });
                } else {
                    for (var clave in constPolygon) {
                        constPolygon[clave].setVisible(true);
                    }
                }
                break;
            case 'amob1':
                // Sólo se hará la consulta si el array no está lleno
                if (lineaAmobPolygon.length == 0) {
                    amoblamientos('linea');
                } else {
                    for (var clave in lineaAmobPolygon) {
                        lineaAmobPolygon[clave].setVisible(true);
                    }
                }
                break;
            case 'amob2':
                // Sólo se hará la consulta si el array no está lleno
                if (poliAmobPolygon.length == 0) {
                    amoblamientos('poligono');
                } else {
                    for (var clave in poliAmobPolygon) {
                        poliAmobPolygon[clave].setVisible(true);
                    }
                }
                break;
            case 'amob3':
                // Sólo se hará la consulta si el array no está lleno
                if (puntoAmobMarkers.length == 0) {
                    amoblamientos('punto');
                } else {
                    for (var punto in puntoAmobMarkers) {
                        //puntoAmobMarker[punto].setMap(map);
                        puntoAmobMarkers[punto].setVisible(true);
                    }
                }
                break;
        }
    });
});

function amoblamientos(tipo) {
    $.ajax({
        type: 'POST',
        url: '/loader.php?lServicio=Bienes&lFuncion=mostrarAmoblamientos',
        dataType: 'json',
        data: {
            tipo: tipo
        },
        success: function (data) {
            if (data.success == 1) {
                var contenido = '';
//                for (var clave in data.data) {
                    /*
                     * Si es línea se crea un Polyline,
                     * si es polígono se crea un Polygon, 
                     * si es punto se crea un Marker
                     */
                    switch (tipo) {
                        case 'linea':
                            for (var lineas in data.data) {
                                lineaAmobPolygon[lineas] = new google.maps.Polyline({
                                    path: data.data[lineas]['LatLng'],
                                    geodesic: true,
                                    strokeColor: data.data[lineas]['impresion'],
                                    strokeOpacity: 1.0,
                                    strokeWeight: 2
                                });
                                lineaAmobPolygon[lineas].setMap(map);
                            }
                            break;
                        case 'poligono':
                            for (var poligonos in data.data) {
                                var colores = data.data[poligonos]['impresion'].split(';');

                                poliAmobPolygon[poligonos] = new google.maps.Polygon({
                                    path: data.data[poligonos]['LatLng'],
                                    geodesic: true,
                                    strokeColor: colores[0],
                                    strokeOpacity: 1.0,
                                    strokeWeight: 2,
                                    fillColor: colores[1],
                                    fillOpacity: 0.35
                                });
                                poliAmobPolygon[poligonos].setMap(map);
                            }
                            break;
                        case 'punto':
                                for (var puntos in data.data) {
                                    var puntoAmobMarker = new google.maps.Marker({
                                        position: new google.maps.LatLng(data.data[puntos]['LatLng'][0]),
                                        icon: data.data[puntos]['impresion'],
                                        map: map
                                    });

                                    puntoAmobMarkers.push(puntoAmobMarker);
                                }
                                var mcOptions = {gridSize: 40, maxZoom: 16, minimumClusterSize: 2, imagePath: 'mod/Bienes/img/images/m'};
                                markerCluster = new MarkerClusterer(map, puntoAmobMarkers, mcOptions); //'media/plugins/markerClusterer/1.0/img/m'
                            break;
                    }
//                }
            } else {
                alert(data.msg);
            }
        }
    });
    
}

/**
 * Coloca un marker sobre la primera dirección que encuentre
 */
function geocodeResult(results, status) {
    // Verificamos el estatus
    if (status == 'OK') {
        // Se limpia el marcador anterior, para no mostrar varios lugares en el mapa
        if (typeof marker !== 'undefined') {
            marker.setMap(null);
        }

        map.fitBounds(results[0].geometry.viewport);
        // Dibujamos un marcador con la ubicación del primer resultado obtenido
        var markerOptions = {position: results[0].geometry.location}
        marker = new google.maps.Marker(markerOptions);
        marker.setMap(map);
        var latlng = results[0].geometry.location.toString();
        latlng = latlng.replace('(', '').replace(')', '');

        var contenido = '<b><center>' + direccion + '</center></b><br /><a class="linkInfoWindow" href="#" onclick="reportePredio(\'SIN PREDIO\', \'' + direccion + '\'. \'' + latlng + '\')" title="Reportar irregularidad">' +
                '  <span><span class="fa fa-inbox fa-2x"></span><span class="text hidden">Reportar irregularidad</span></span>' +
                '</a>&nbsp;' +
                '<a class="linkInfoWindow" target="_blank" href="http://maps.google.com/maps?q=&amp;layer=c&amp;cbll=' + latlng + '&amp;cbp=12,0,0,0,0" title="Google Street View">' +
                '  <span><span class="fa fa-street-view fa-2x"></span><span class="text hidden">Google Street View</span></span>' +
                '</a>';


        google.maps.event.addListener(marker, 'click', function (evt) {
            infoWindow.setContent(contenido);
            infoWindow.open(map, this);
            infoWindow.setPosition(evt.latLng);
        });
    } else {
        alert("No hay resultados a mostrar: " + status);
    }
}

/**
 * Se mueve en el mapa y coloca un marcador en el predio que encuentre en esas
 * coordenadas
 * 
 * @param {double} latitud
 * @param {double} longitud
 * @param {int} zoom
 */
function irPosicion(latitud, longitud, zoom) {
    // Se limpia el marcador anterior, para no mostrar varios lugares en el mapa
    if (typeof marker !== 'undefined') {
        marker.setMap(null);
    }

    var latlng = new google.maps.LatLng(longitud, latitud);
    marker = new google.maps.Marker({
        position: latlng,
        title: ""
    });

    map.setCenter(marker.getPosition());
    map.setZoom(zoom);
    marker.setAnimation(google.maps.Animation.DROP);
    marker.setPosition(latlng);
    marker.setMap(map);
}

/*
 * Muestra/Esconde la capa de búsqueda para dejar un botón visible en la parte 
 * superior derecha
 *  
 * @param {int} id
 */
function esconder(id) {
    // En caso de ser 0 esconde el botón y muestra la capa de búsqueda
    if (id == 0) {
        $(".hide-button").hide();
        $(".detail-config").show();
    } else { // En caso de ser 1 esconde la capa de búsqueda y muestra el botón
        $(".detail-config").hide(1000);
        $(".hide-button").show();
    }
}

// Recoge el valor data y lo pinta sobre la tabla de informacionPredio
function printSpan(capa, data) {
    return $("#" + capa).attr('data-' + data);
}

/**
 * Muestra y oculta capas en la vista main
 * 
 * @param {string} predio
 * @param {string} direccion
 * @param {string} coordenadas
 */
function reportePredio(predio, direccion, coordenadas) {
    $("#reporte_num_predial").val(predio);
    $("#reporte_direccion").val(direccion);
    $("#reporte_coordenadas").val(coordenadas);
    $("#reporte_nombre").val('');
    $("#reporte_correo").val('');
    $("#reporte_telefono").val('')
    $("#reporteIrregularidad").fadeIn();
    $("#busquedaPpal").hide();
    $("#reporteIrregularidad").show();
}