<?xml version='1.0' encoding='UTF-8' ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:ice="http://www.icesoft.com/icefaces/component"
      xmlns:f="http://java.sun.com/jsf/core">
    <head>
        <title>Servicios Cientifico-Técnicos IACT-CSIC (Pasarela externa)</title>
        <style type="text/css">
            h1 {
                font-family: Arial, Helvetica, sans-serif;
                font-weight: lighter;
                color: #CCC;
                text-transform: none;
                padding: 1em;
            }
            #header {
                width: 100%;
                background-color: #333;
            }
            h2 {
                font-family: Arial, Helvetica, sans-serif;
                color: #FFF;
                background-color: #000;
                padding-bottom: 1em;
                padding-left: 2em;
            }
            h3 {
                font-family: Arial, Helvetica, sans-serif;
            }
        </style>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    </head>

    <body>
        <div id="header">
            <h1>Servicios Cientifico-Técnicos IACT-CSIC</h1>
            <h2>Acceso externo</h2>
        </div>
        <div style="margin-left:20px;">
            <form method="POST" action="/iact-adm/service/index.jsf">
                <h3>Login:</h3>
                <input type="text" name="j_username"/><br/>
                <h3>Contraseña:</h3>
                <input type="password" name="j_password"/>
                <br/>
                <input type="submit" value="Login" tabindex="0"/>
            </form>
        </div>
    </body>

</html>

