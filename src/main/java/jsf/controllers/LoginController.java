package jsf.controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.TimeZone;
import javax.ejb.EJB;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import jsf.util.MysqlConnector;
import laboratorios.facade.PermisoEmpleadoFacade;
import laboratorios.modelo.PermisoEmpleado;
import personal.facade.EmpleadoFacade;
import personal.modelo.Empleado;

@ManagedBean(name = "login")
@SessionScoped
public class LoginController {
    @EJB
    private EmpleadoFacade empleadoFacade;
    
    @EJB
    private PermisoEmpleadoFacade permisoEmpleadoFacade;
    
    
    public boolean isInvestigador() {
        return roles.contains("investigador");
    }
    
    public boolean isDirector(){
        return roles.contains("director");
    }
    
    public boolean isAutorizadoServiciosCT(){
        if(isInvestigador())
            return true;
        
        System.out.println("Comprobando Autorizacion a Servicio CT para "+getEmpleado().getNombre());
        
        for(PermisoEmpleado pe: permisoEmpleadoFacade.getPermisosRecibidos(getEmpleado())){
            System.out.println("Tiene permiso para: "+pe.getProyecto().getReferencia());
            if(pe.getPermiso().equals(PermisoEmpleado.PERMISOS[0]))
                return true;
        }
        
        return false;
    }
    
    public boolean isAdminServ() {
        return roles.contains("admin-serv");
    }

    public boolean isInRole(String role) {
        return roles.contains(role);
    }

    public String getDni() {
        return getName().toUpperCase();
    }

    public String getTimeZone() {
        return TimeZone.getDefault().getID();
    }

    public String getName() {
        //DEBUG
        return FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();


//        if(getCurrentContext().getExternalContext().getRemoteUser().equalsIgnoreCase("52565191w"))
//            return "X7284246P";
//        else
//            return "10898374P";

//        Investigador Oscar Romero
//        return "X7284246P";
        
//          return "23582812S";

//        Tecnico Fluorescencia Carmen Niembro
//        return "24224445V";

//        Tecnico DRX, MICRAM Juan Santamarina
//        return "24151785Z";

//        Tecnico Informática Eduardo González
//        return "71658368M";

//        Tecnico Informática Manuel Carmona
//        return "52565191W";

//        Tecnico Molienda y Separacion mineral Sonia Sanchez
//        return "24261757T";

//        Tecnico Miguel Martin
//        return "24094236B";

//        Responsable Isótopos/ Geotech Carlos Garrido
//        return "25995826F";

//        Tecnico Dibujo Angel Caballero
//        return "30189931Q";

//        Tecnico Serigraph Luisa García Simón
//        return "26233716P";

//        Técnico Esteso
//          return "51696916T";

    }


    private HashSet<String> roles;

    public LoginController() {
        roles = new HashSet<String>();

        Connection conn;
        Statement stat;
        ResultSet rs;
        String grupo;

        try {
            conn = MysqlConnector.getInstance().getConnection();
            stat = conn.createStatement();
            rs = stat.executeQuery("SELECT GROUPNAME FROM GROUPSINTRANET WHERE LOGIN='" + getDni() + "'");
            
            System.out.println("Usuario: "+getDni());
            
            while (rs.next()) {
                grupo = rs.getString(1);
                roles.add(grupo);
                System.out.println("ROLE: "+grupo);

            }

            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            jsf.util.JsfUtil.addErrorMessage("No se ha podido conectar con la base de datos.");
            

        }
        
    }

    public String logout() {
        HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
        session.invalidate();
        return "logout";
    }
    
    public Empleado getEmpleado(){
        return empleadoFacade.find(getDni());
    }
    
    
    

    
}
