package laboratorios.jsfcontroller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.*;
import javax.faces.model.SelectItem;
import jsf.controllers.LoginController;
import laboratorios.modelo.Reserva;
import jsf.util.Calendario;
import jsf.util.JsfUtil;
import laboratorios.facade.OrdenTrabajoLaboratorioFacade;
import laboratorios.facade.RecursoFacade;
import laboratorios.facade.ReservaFacade;
import laboratorios.facade.TecnicaFacade;
import laboratorios.modelo.Tecnica;
import personal.facade.EmpleadoFacade;
import proyectos.facade.ProyectoFacade;
import proyectos.modelo.Proyecto;

@ManagedBean(name="micspc")
@SessionScoped
public class MICSPCController {

    @EJB
    private ReservaFacade reservaFacade;

    @EJB
    private TecnicaFacade tecnicaFacade;

    @EJB
    private OrdenTrabajoLaboratorioFacade ordenTrabajoLaboratorioFacade;

    @EJB
    private ProyectoFacade proyectoFacade;

    @EJB
    private EmpleadoFacade empleadoFacade;
    
    @EJB
    private RecursoFacade recursoFacade;

    @PostConstruct
    public void init(){
        tecnica=tecnicaFacade.findByNombre("Espectroscopía micro-Raman de alta resolución espectral");
        calendario=new Calendario(reservaFacade);
        calendario.actualizar();
    }

    @ManagedProperty(value="#{login}")
    private LoginController login;

    public void setLogin(LoginController login){
        this.login=login;
    }
    
    // <editor-fold defaultstate="collapsed" desc="calendario">
    private Calendario calendario;

    public Calendario getCalendario() {
        return calendario;
    }

    public void setCalendario(Calendario calendario) {
        this.calendario = calendario;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="reserva">
    private Reserva reserva;

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }
    // </editor-fold>
    
    public String realizarPeticionReservaSetup(){
        reserva = new Reserva();
        reserva.setFechaInicio(new Date());
        reserva.setHoraInicio(new Date());
        reserva.setHoraFin(new Date());
        reserva.setFechaFin(new Date());
        reserva.setEstado(Reserva.RELLENANDO);
        reserva.setSolicitante(empleadoFacade.find(login.getDni()));
        reserva.setRecurso(recursoFacade.findByNombre("Microscopio confocal"));
        return "rellenar-reserva";
    }

    public String realizarPeticionReserva(){
        reservaFacade.create(reserva);
        return "ver-reserva";
    }

    public void aceptarPeticion(){
        reserva.setEstado(Reserva.ACEPTADA);
        reserva.setFechaaceptacion(new Date());
        reservaFacade.edit(reserva);
    }

    public void denegarPeticion(){
        reserva.setEstado(Reserva.DENEGADA);
        reserva.setFechadenegacion(new Date());
        reservaFacade.edit(reserva);
    }

    public String revisarPeticionSetup(){
        return "revisar-reserva";
    }

    public String revisarPeticion(){
        reservaFacade.edit(reserva);
        return "ver-reserva";
    }

    public String editarPeticion(){
        reservaFacade.edit(reserva);
        return "ver-reserva";
    }

    public String editarPeticionSetup(){
        return "rellenar-reserva";
    }

    public void firmarPeticion(){
//        if(!isPosibleNuevaPeticion()){
//            JsfUtil.addErrorMessage("Tiene ordenes de pedido sin FINALIZAR. No puede solicitar otra.");
//        }

        if(reserva!=null){
            if(reserva.getEstado().equals(Reserva.RELLENANDO)){
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date fecha =new Date();
                reserva.setRegistroFirmaPeticion("Firmado por "+login.getName()+" a las "+df.format(fecha));
                reserva.setFechasolicitud(fecha);
                reserva.setEstado(Reserva.SOLICITADA);
                reserva.setNumordentrabajo(ordenTrabajoLaboratorioFacade.getSiguienteNumOrdenTrabajo(tecnica));
                reservaFacade.edit(reserva);
            }
        }
    }

    public List<Reserva> getReservas(){
        return reservaFacade.findAll();
    }

    // <editor-fold defaultstate="collapsed" desc="tecnica">
    private Tecnica tecnica;

    public Tecnica getTecnica() {
        return tecnica;
    }

    public void setTecnica(Tecnica tecnica) {
        this.tecnica = tecnica;
    }
    // </editor-fold>

    public boolean isTecnico(){
        return login.isInRole(tecnica.getRoleTecnico());
    }

    public SelectItem[] getProyectoItemsAvailableSelectOne() {
        List<Proyecto> proyectos=proyectoFacade.findVigentes(login.getDni());

//        proyectos.add(0, getProyectoFacade().getSinProyecto());

        return JsfUtil.getSelectItems(proyectos, true);
    }

    public String listSetup() {
        return "listar-reservas";
    }
}
