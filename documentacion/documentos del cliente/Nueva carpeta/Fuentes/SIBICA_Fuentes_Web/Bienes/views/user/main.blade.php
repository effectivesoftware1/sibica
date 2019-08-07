@extends(PATH_VIEW_LAYOUT.'userLayout')
@section('content')

    <span id="coordenadas" data-lat="{{BIENES_LATITUD}}" data-lng="{{BIENES_LONGITUD}}" data-zoom="{{BIENES_ZOOM}}" data-estado="{{BIENES_RESUELTO}}"></span>
    <div id="busquedaPpal">@include('mod.Bienes.views.user.capabusqueda')</div>
    <div id="reporteIrregularidad">@include('mod.Bienes.views.user.reporte')</div>
    <div id="map"></div>
    <div id="informacion">{{BIENES_ADVERTENCIA}}</div>

    <!--<script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyDlOfLKqC490PII4a1_T8xHG6aDPH-nlnc"></script>-->    
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyD2yekkW--4OZeozoeQhvuK78ARyq33SDc"></script>
    
    <script type="text/javascript">
        $( document ).ready(function() {
            // Capas que no se deben mostrar al inicio de la carga de la página
            $("#busquedaPpal").hide();
            $(".hide-button").hide(); 
            $("#infoPredio").hide();
            $("#predios").hide();
            $("#loginForm").hide();
            $("#infoPredioExtra").hide();
            $("#reporteIrregularidad").hide();
            
            // Se agrega un atributo al modPath del manual PDF
            $("#modPDF").attr('target','_blank');
            
            initMap(); // Carga de mapa
        });
    </script>

    <script type="text/javascript" src="/chat/js/compiled/chat_popup.js"></script>
    <script type="text/javascript">
        Mibew.ChatPopup.init({
            "id":"58864fa22b855697",
            "url":"/chat/chat?locale=en",
            "preferIFrame":true,
            "modSecurity":false,
            "width":640,
            "height":480,
            "resizable":true,
            "styleLoader":"/chat/chat/style/popup"
        });
    </script>
    <div id="mibew-invitation"></div>
    <script type="text/javascript" src="/chat/js/compiled/widget.js"></script>
    <script type="text/javascript">
        Mibew.Widget.init({
            "inviteStyle":"/chat/styles/invitations/default/invite.css",
            "requestTimeout":10000,
            "requestURL":"/chat/widget",
            "locale":"en",
            "visitorCookieName":"MIBEW_VisitorID"
        })
    </script>
    <!-- / mibew button -->

    
@stop