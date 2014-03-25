package laboratorios.jsfcontroller;

import java.util.ArrayList;
import java.util.Arrays;
import javax.faces.context.FacesContext;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.*;
import javax.faces.model.SelectItem;
import jsf.controllers.LoginController;
import jsf.util.JsfUtil;
import laboratorios.facade.PermisoEmpleadoFacade;
import laboratorios.modelo.PermisoEmpleado;
import personal.facade.EmpleadoFacade;
import personal.modelo.Empleado;
import proyectos.facade.ProyectoFacade;
import proyectos.modelo.Proyecto;

@ManagedBean(name="permisos")
@SessionScoped
public class PermisoController{
    @EJB
    private PermisoEmpleadoFacade permisoEmpleadoFacade;
    
    @EJB
    private EmpleadoFacade empleadoFacade;
    
    @EJB
    private ProyectoFacade proyectoFacade;
    
    
    // <editor-fold defaultstate="collapsed" desc="login">
    private LoginController login;

    public LoginController getLogin() {
        return login;
    }

    public void setLogin(LoginController login) {
        this.login = login;
    }
    // </editor-fold>

    public PermisoController() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        login=(LoginController)facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "login");
        tipoPermisoSeleccionado=-1;

        //servicioLaboratorio=jpaController.findServicioLaboratorio("Fluorescencia");
    }
    
    
    public String borrarPermiso(){
        if(permisoSeleccionado!=null){
            permisoSeleccionado.getPermisor().getPermisosOtorgados().remove(permisoSeleccionado);
            permisoSeleccionado.getPermitido().getPermisosOtorgados().remove(permisoSeleccionado);
            
            empleadoFacade.edit(permisoSeleccionado.getPermisor());
            empleadoFacade.edit(permisoSeleccionado.getPermitido());
            
            permisoEmpleadoFacade.remove(permisoSeleccionado);
        }
        
        return null;
    }
    
    
    public void insertarPermiso(){
        if(getProyectoSeleccionado()!=null
                &&getEmpleadoSeleccionado()!=null&&getTipoPermisoSeleccionado()!=-1){
            
            PermisoEmpleado pe = new PermisoEmpleado();
            pe.setProyecto(proyectoSeleccionado);
            pe.setPermisor(login.getEmpleado());
            pe.setPermitido(getEmpleadoSeleccionado());
            pe.setPermiso(PermisoEmpleado.PERMISOS[getTipoPermisoSeleccionado()]);
            
            login.getEmpleado().getPermisosOtorgados().add(pe);

            getEmpleadoSeleccionado().getPermisosRecibidos().add(pe);
            permisoEmpleadoFacade.create(pe);
            empleadoFacade.edit(login.getEmpleado()); 
            empleadoFacade.edit(getEmpleadoSeleccionado());
            
        }
    }
    
    public SelectItem[] getEmpleadosInvestigacionSelectOne() {
        List<Empleado> empleadosInvestigacion=empleadoFacade.findEmpleados();
        return JsfUtil.getSelectItems(empleadosInvestigacion, true);
    }
    
    
    public SelectItem[] getPermisosSelectOne() {
        SelectItem [] permisos = new SelectItem[3];
        
        permisos[0]=new SelectItem(0, "Servicios cientifico-técnicos");
        permisos[1]=new SelectItem(1, "Reserva automóviles");
        permisos[2]=new SelectItem(2, "Compras");
        
        return permisos;
    }
    
    public SelectItem[] getProyectoItemsAvailableSelectOne() {
        List<Proyecto> proyectos=proyectoFacade.findVigentes(login.getDni());

        return JsfUtil.getSelectItems(proyectos, true);
    }
    
    
    private Proyecto proyectoSeleccionado;

    public Proyecto getProyectoSeleccionado() {
        return proyectoSeleccionado;
    }

    public void setProyectoSeleccionado(Proyecto proyectoSeleccionado) {
        this.proyectoSeleccionado = proyectoSeleccionado;
    }
    
    
    
    private Empleado empleadoSeleccionado;

    public Empleado getEmpleadoSeleccionado() {
        return empleadoSeleccionado;
    }

    public void setEmpleadoSeleccionado(Empleado empleadoSeleccionado) {
        this.empleadoSeleccionado = empleadoSeleccionado;
    }
    
    

    private PermisoEmpleado permisoSeleccionado;

    public PermisoEmpleado getPermisoSeleccionado() {
        return permisoSeleccionado;
    }

    public void setPermisoSeleccionado(PermisoEmpleado permisoSeleccionado) {
        this.permisoSeleccionado = permisoSeleccionado;
    }
    
    private int tipoPermisoSeleccionado;

    public int getTipoPermisoSeleccionado() {
        return tipoPermisoSeleccionado;
    }

    public void setTipoPermisoSeleccionado(int tipoPermisoSeleccionado) {
        this.tipoPermisoSeleccionado = tipoPermisoSeleccionado;
    }
    
    
 
    
    public List<PermisoEmpleado> getPermisosOtorgados(){
        return permisoEmpleadoFacade.getPermisosOtorgados(login.getEmpleado());
    }
    
    public List<PermisoEmpleado> getPermisosRecibidos(){
        return permisoEmpleadoFacade.getPermisosRecibidos(login.getEmpleado());
    }
    
}
