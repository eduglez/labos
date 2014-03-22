package laboratorios.jsfcontroller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import jsf.util.JsfUtil;
import jsf.util.ServicioCorreo;
import laboratorios.facade.DocumentoResultadosFacade;
import laboratorios.facade.OrdenTrabajoLaboratorioFacade;
import laboratorios.facade.OrdenTrabajoMantenimientoFacade;
import laboratorios.facade.PermisoEmpleadoFacade;
import laboratorios.facade.TecnicaFacade;
import laboratorios.facade.TrabajoFacade;
import laboratorios.facade.TrabajoOrdenFacade;
import laboratorios.modelo.OrdenTrabajoLaboratorio;
import laboratorios.modelo.OrdenTrabajoMantenimiento;
import personal.facade.EmpleadoFacade;
import proyectos.facade.ProyectoFacade;

@ManagedBean(name="manveh")
@SessionScoped
public class MANVEHController extends LaboratorioController {
    @Resource(name = "mail/iactavisos")
    private Session mailSession;
    
    @Override
    protected Session getMailSession() {
        return mailSession;
    }
    
    @EJB
    private ProyectoFacade proyectoFacade;

    @EJB
    private EmpleadoFacade empleadoFacade;

    @EJB
    private TecnicaFacade tecnicaFacade;

    @EJB
    private OrdenTrabajoMantenimientoFacade ordenTrabajoMantenimientoFacade;
    
    @EJB
    private PermisoEmpleadoFacade permisoEmpleadoFacade;
    
    @Override
    protected PermisoEmpleadoFacade getPermisoEmpleadoFacade() {
        return permisoEmpleadoFacade;
    }
    
    @PostConstruct
    public void init(){
        setTecnica(tecnicaFacade.findByNombre("Mantenimiento vehículos"));
    }

    @Override
    protected OrdenTrabajoLaboratorioFacade getOrdenTrabajoLaboratorioFacade() {
        return null;
    }

    @Override
    protected ProyectoFacade getProyectoFacade() {
        return proyectoFacade;
    }

    @Override
    protected EmpleadoFacade getEmpleadoFacade() {
        return empleadoFacade;
    }

    @Override
    protected TecnicaFacade getTecnicaFacade() {
        return tecnicaFacade;
    }

    @Override
    protected TrabajoFacade getTrabajoFacade() {
        return null;
    }

    @Override
    protected TrabajoOrdenFacade getTrabajoOrdenFacade() {
        return null;
    }

    @Override
    protected DocumentoResultadosFacade getDocumentoResultadosFacade() {
        return null;
    }



    // <editor-fold defaultstate="collapsed" desc="ordenTrabajoMantenimiento">
    private OrdenTrabajoMantenimiento ordenTrabajoMantenimiento;

    public OrdenTrabajoMantenimiento getOrdenTrabajoMantenimiento() {
        return ordenTrabajoMantenimiento;
    }

    public void setOrdenTrabajoMantenimiento(OrdenTrabajoMantenimiento ordenTrabajoMantenimiento) {
        this.ordenTrabajoMantenimiento = ordenTrabajoMantenimiento;
    }

    // </editor-fold>


    public List<OrdenTrabajoMantenimiento> getOrdenTrabajoLaboratorioMantenimientoItems() {
        List<OrdenTrabajoMantenimiento> ordenTrabajoMantenimiento;
        if (getColumnaFiltro() == null) {
            if (getLogin().isInvestigador()) {
                ordenTrabajoMantenimiento = ordenTrabajoMantenimientoFacade.findByTecnicaSolicitante(getTecnica(), getLogin().getDni());
            } else {
                ordenTrabajoMantenimiento = ordenTrabajoMantenimientoFacade.findByTecnica(getTecnica());
            }

        } else {
            if (getLogin().isInvestigador()) {
                ordenTrabajoMantenimiento = ordenTrabajoMantenimientoFacade.findByTecnicaSolicitante(getTecnica(), getLogin().getDni(), getColumnaFiltro(), isAscendente());
            } else {
                ordenTrabajoMantenimiento = ordenTrabajoMantenimientoFacade.findByTecnica(getTecnica(), getColumnaFiltro(), isAscendente());
            }
        }

        return ordenTrabajoMantenimiento;
    }

    @Override
    public String verOrdenTrabajoLab() {
        if (ordenTrabajoMantenimiento != null && ordenTrabajoMantenimiento.getId() != null) {
            ordenTrabajoMantenimiento = ordenTrabajoMantenimientoFacade.find(ordenTrabajoMantenimiento.getId());
            return "ver-orden-trabajo";
        } else {
            return listSetup();
        }
    }

    @Override
    public String rellenarOrdenTrabajoSetup() {
        ordenTrabajoMantenimiento = new OrdenTrabajoMantenimiento();
        ordenTrabajoMantenimiento.setEstado(OrdenTrabajoLaboratorio.RELLENANDO);
        ordenTrabajoMantenimiento.setSolicitante(empleadoFacade.find(getLogin().getDni()));
        ordenTrabajoMantenimiento.setTecnica(getTecnica());
        getTecnica().getOrdenesTrabajo().add(ordenTrabajoMantenimiento);
        ordenTrabajoMantenimientoFacade.create(ordenTrabajoMantenimiento);
        tecnicaFacade.edit(getTecnica());
        JsfUtil.addSuccessMessage("La Orden de Trabajo se ha creado correctamente");
        return "rellenar-orden-trabajo";
    }

    @Override
    public String comenzarEstudio() {
        if (ordenTrabajoMantenimiento != null) {
            if (ordenTrabajoMantenimiento.getEstado().equals(OrdenTrabajoLaboratorio.SOLICITADA)) {
//                HttpServletRequest request =
//			(HttpServletRequest)FacesContext
//				.getCurrentInstance()
//					.getExternalContext()
//						.getRequest();
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));

                ordenTrabajoMantenimiento.setFechainicioestudio(new Date());
                ordenTrabajoMantenimiento.setEstado(OrdenTrabajoLaboratorio.EN_ESTUDIO);
                ordenTrabajoMantenimientoFacade.edit(ordenTrabajoMantenimiento);
                
                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String editarSetup() {
        if (ordenTrabajoMantenimiento != null) {
            return "rellenar-orden-trabajo";
        } else {
            return null;
        }
    }

    @Override
    public String entregarMuestras() {
        if (ordenTrabajoMantenimiento != null) {
            if (ordenTrabajoMantenimiento.getEstado().equals(OrdenTrabajoLaboratorio.EN_ESTUDIO)) {
//                HttpServletRequest request =
//			(HttpServletRequest)FacesContext
//				.getCurrentInstance()
//					.getExternalContext()
//						.getRequest();
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));

                ordenTrabajoMantenimiento.setFechaentregaresultados(new Date());
                ordenTrabajoMantenimiento.setEstado(OrdenTrabajoLaboratorio.RESULTADOS_ENTREGADOS);
                enviarMensajeEntregaResultados();
                ordenTrabajoMantenimientoFacade.edit(ordenTrabajoMantenimiento);
                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public String firmarEntrega() {
        if (ordenTrabajoMantenimiento != null) {
            if (ordenTrabajoMantenimiento.getEstado().equals(OrdenTrabajoLaboratorio.RESULTADOS_ENTREGADOS)) {
                try {
                    Date fecha = new Date();
                    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//                    ordenTrabajoLab.setRegistroFirmaEntrega("Firmado por " + login.getName() + " desde " + request.getRemoteAddr() + " a las " + df.format(new Date()));
                    ordenTrabajoMantenimiento.setRegistroFirmaEntrega("Firmado por " + getLogin().getName() + " a las " + df.format(fecha));
                    ordenTrabajoMantenimiento.setEstado(OrdenTrabajoLaboratorio.FINALIZADA);
                    ordenTrabajoMantenimiento.setFechafirmaentregadatos(fecha);
                    ordenTrabajoMantenimientoFacade.edit(ordenTrabajoMantenimiento);
                    return null;
                } catch (Exception ex) {
                    Logger.getLogger(LaboratorioController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;

    }

    @Override
    public String firmarPeticion() {
        if (ordenTrabajoMantenimiento != null) {
            if (ordenTrabajoMantenimiento.getEstado().equals(OrdenTrabajoLaboratorio.RELLENANDO)) {
                HttpServletRequest request =
                        (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date fecha = new Date();
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));

                ordenTrabajoMantenimiento.setRegistroFirmaPeticion("Firmado por " + getLogin().getName() + " a las " + df.format(fecha));

                ordenTrabajoMantenimiento.setFechasolicitud(fecha);
                ordenTrabajoMantenimiento.setEstado(OrdenTrabajoLaboratorio.SOLICITADA);

                ordenTrabajoMantenimiento.setNumordentrabajo(ordenTrabajoMantenimientoFacade.getSiguienteNumOrdenTrabajo(getTecnica()));

                ordenTrabajoMantenimientoFacade.edit(ordenTrabajoMantenimiento);
                
            }
        }

        return null;
    }

    @Override
    public String rellenarOrdenTrabajo() {
        try {
            guardarCambios();
            JsfUtil.addSuccessMessage("La orden de trabajo se ha almacenado correctamente.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "Error al almacenar.");
            return null;
        }
        return "ver-orden-trabajo";
    }

    @Override
    public String revisarOrdenTrabajo() {
        try {
            guardarCambios();
            JsfUtil.addSuccessMessage("La orden de trabajo se ha almacenado correctamente.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "Error al almacenar.");
            return null;
        }
        return "ver-orden-trabajo";
    }

    @Override
    public String revisarOrdenTrabajoSetup() {
        if (ordenTrabajoMantenimiento != null) {
            return "revisar-orden-trabajo";
        } else {
            return null;
        }
    }

    @Override
    public String salirGuardando() {
        guardarCambios();
        return verOrdenTrabajoLab();
    }

    @Override
    public String salirSinGuardar() {
        return verOrdenTrabajoLab();
    }

    @Override
    public void guardarCambios() {
        ordenTrabajoMantenimientoFacade.edit(ordenTrabajoMantenimiento);
    }

    public List<OrdenTrabajoMantenimiento> getOrdenTrabajoLaboratorioMantenimientoPendientesItems() {
        return ordenTrabajoMantenimientoFacade.findPendientes(getTecnica());
    }


    @Override
    public void enviarMensajeEntregaResultados(){
        String nombreOrden=ordenTrabajoMantenimiento.getNumordentrabajo();
        if(ordenTrabajoMantenimiento.getSolicitante()==null)
            return;
        
        if(ordenTrabajoMantenimiento.getSolicitante().getContratoActual()==null)
            return;
        
        String correoSolicitante = ordenTrabajoMantenimiento.getSolicitante().getContratoActual().getEMail();
        
        if(correoSolicitante==null||correoSolicitante.isEmpty())
            return;

        if(ordenTrabajoMantenimiento.getNombre()!=null&&!ordenTrabajoMantenimiento.getNombre().isEmpty()){
            nombreOrden+=" ("+ordenTrabajoMantenimiento.getNombre()+")";
        }

        String titulo = "[I] Entrega de Resultados para "+nombreOrden;
        String mensaje = "El Servicio de Informática ha terminado el Estudio para la Orden de Apoyo Técnico "+nombreOrden+"\r\n\r\n";
        mensaje+="Podrá ver la ficha de la Orden de Apoyo Técnico en: "+"\r\n";
//        mensaje+="https://150.214.192.118:8081/iact-adm/laboratorios/fluorescencia/verordentrabajo.xhtml?id="+ordenTrabajoInformatica.getId()+"\r\n\r\n";
        mensaje+="https://150.214.192.76:8081/iact-adm/laboratorios/informatica/verordentrabajo.xhtml?id="+ordenTrabajoMantenimiento.getId()+"\r\n\r\n";

        mensaje+="Por favor, no olvide FIRMAR LA ENTREGA. Si no se firma, se procederá a hacerse de forma automática pasados 15 días después de haber recibido este mensaje."+"\r\n\r\n";
        mensaje+="Reciba un cordial Saludo"+"\r\n\r\n";

        String receptores[] = {correoSolicitante};
        try {
            postMail(receptores, titulo, mensaje);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }



    }
    
}
