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
import laboratorios.facade.OrdenTrabajoInformaticaFacade;
import laboratorios.facade.OrdenTrabajoLaboratorioFacade;
import laboratorios.facade.PermisoEmpleadoFacade;
import laboratorios.facade.PrioridadServicioInformaticaFacade;
import laboratorios.facade.TecnicaFacade;
import laboratorios.facade.TrabajoFacade;
import laboratorios.facade.TrabajoOrdenFacade;
import laboratorios.modelo.OrdenTrabajoLaboratorio;
import laboratorios.modelo.OrdenTrabajoInformatica;
import personal.facade.EmpleadoFacade;
import proyectos.facade.ProyectoFacade;

@ManagedBean(name="cominf")
@SessionScoped
public class INFORMATICAController extends LaboratorioController {
    
    @Resource(name = "mail/iactavisos")
    private Session mailSession;
    
    @Override
    protected Session getMailSession() {
        return mailSession;
    }
    
    @EJB
    private OrdenTrabajoLaboratorioFacade ordenTrabajoLaboratorioFacade;

    @EJB
    private OrdenTrabajoInformaticaFacade ordenTrabajoInformaticaFacade;
    
    @EJB
    private ProyectoFacade proyectoFacade;

    @EJB
    private EmpleadoFacade empleadoFacade;

    @EJB
    private TecnicaFacade tecnicaFacade;

    @EJB
    private TrabajoFacade trabajoFacade;

    @EJB
    private TrabajoOrdenFacade trabajoOrdenFacade;

    @EJB
    private DocumentoResultadosFacade documentoResultadoFacade;

    @EJB
    private PrioridadServicioInformaticaFacade prioridadServicioInformaticaFacade;
 
    @EJB
    private PermisoEmpleadoFacade permisoEmpleadoFacade;
    
    @Override
    protected PermisoEmpleadoFacade getPermisoEmpleadoFacade() {
        return permisoEmpleadoFacade;
    }
    
    @PostConstruct
    public void init(){
        setTecnica(tecnicaFacade.findByNombre("Informática"));
    }

    @Override
    protected OrdenTrabajoLaboratorioFacade getOrdenTrabajoLaboratorioFacade() {
        return ordenTrabajoLaboratorioFacade;
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
        return trabajoFacade;
    }

    @Override
    protected TrabajoOrdenFacade getTrabajoOrdenFacade() {
        return trabajoOrdenFacade;
    }

    @Override
    protected DocumentoResultadosFacade getDocumentoResultadosFacade() {
        return documentoResultadoFacade;
    }



    // <editor-fold defaultstate="collapsed" desc="ordenTrabajoInformatica">
    private OrdenTrabajoInformatica ordenTrabajoInformatica;

    public OrdenTrabajoInformatica getOrdenTrabajoInformatica() {

        OrdenTrabajoInformatica solicitadoPorURL = (OrdenTrabajoInformatica) JsfUtil.getObjectFromRequestParameter("id", new OrdenTrabajoInformaticaConverter(), null);
        if (solicitadoPorURL != null) {

            //ARREGLAR ESTO
//            if (getLogin().isInvestigador()&& getOrdenTrabajoLaboratorioInformaticaItems().contains(solicitadoPorURL)) {
//                ordenTrabajoInformatica = solicitadoPorURL;
//            }

            if (solicitadoPorURL.getSolicitante()!=null&&solicitadoPorURL.getSolicitante().getDni().equalsIgnoreCase(getLogin().getName())) {
                ordenTrabajoInformatica = solicitadoPorURL;
            }
        }
        return ordenTrabajoInformatica;

    }

    public void setOrdenTrabajoInformatica(OrdenTrabajoInformatica ordenTrabajoInformatica) {
        this.ordenTrabajoInformatica = ordenTrabajoInformatica;
    }

    // </editor-fold>


    public SelectItem[] getPrioridadesSelectOne() {
        return JsfUtil.getSelectItems(prioridadServicioInformaticaFacade.findAll(),true);
    }

    public List<OrdenTrabajoInformatica> getOrdenTrabajoLaboratorioInformaticaItems() {
        List<OrdenTrabajoInformatica> ordenTrabajoLabEncontrados;
        if (getColumnaFiltro() == null) {
            if (getLogin().isInvestigador()) {
                ordenTrabajoLabEncontrados = ordenTrabajoInformaticaFacade.findOrdenTrabajoLaboratorioInformaticaEntities(getTecnica(), getLogin().getDni());
            } else {
                ordenTrabajoLabEncontrados = ordenTrabajoInformaticaFacade.findOrdenTrabajoLaboratorioInformaticaTecnicoEntities(getTecnica());
            }

        } else {
            if (getLogin().isInvestigador()) {
                ordenTrabajoLabEncontrados = ordenTrabajoInformaticaFacade.findOrdenTrabajoLaboratorioInformaticaEntities(getTecnica(), getLogin().getDni(), getColumnaFiltro(), isAscendente());
            } else {
                ordenTrabajoLabEncontrados = ordenTrabajoInformaticaFacade.findOrdenTrabajoLaboratorioInformaticaTecnicoEntities(getTecnica(), getColumnaFiltro(), isAscendente());
            }
        }

        return ordenTrabajoLabEncontrados;
    }

    @Override
    public String verOrdenTrabajoLab() {
        if (ordenTrabajoInformatica != null && ordenTrabajoInformatica.getId() != null) {
            ordenTrabajoInformatica = ordenTrabajoInformaticaFacade.find(ordenTrabajoInformatica.getId());
            return "ver-orden-trabajo";
        } else {
            return listSetup();
        }
    }

    @Override
    public String rellenarOrdenTrabajoSetup() {
        ordenTrabajoInformatica = new OrdenTrabajoInformatica();
        ordenTrabajoInformatica.setEstado(OrdenTrabajoLaboratorio.RELLENANDO);
        ordenTrabajoInformatica.setSolicitante(empleadoFacade.find(getLogin().getDni()));
        ordenTrabajoInformatica.setTecnica(getTecnica());
        getTecnica().getOrdenesTrabajo().add(ordenTrabajoInformatica);
        ordenTrabajoInformaticaFacade.create(ordenTrabajoInformatica);
        tecnicaFacade.edit(getTecnica());
        JsfUtil.addSuccessMessage("La Orden de Trabajo se ha creado correctamente");
        return "rellenar-orden-trabajo";
    }

    public String rellenarOrdenTrabajoTecnicoSetup() {
        ordenTrabajoInformatica = new OrdenTrabajoInformatica();
     
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date fecha = new Date();
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));

                ordenTrabajoInformatica.setRegistroFirmaPeticion("Firmado por " + getLogin().getName() + " a las " + df.format(fecha));

                ordenTrabajoInformatica.setFechasolicitud(fecha);
                ordenTrabajoInformatica.setEstado(OrdenTrabajoLaboratorio.SOLICITADA);

                ordenTrabajoInformatica.setNumordentrabajo(ordenTrabajoInformaticaFacade.getSiguienteNumOrdenTrabajo());
        ordenTrabajoInformatica.setSolicitante(empleadoFacade.find(getLogin().getDni()));
        ordenTrabajoInformatica.setTecnica(getTecnica());
        getTecnica().getOrdenesTrabajo().add(ordenTrabajoInformatica);
        ordenTrabajoInformaticaFacade.create(ordenTrabajoInformatica);
        tecnicaFacade.edit(getTecnica());
        JsfUtil.addSuccessMessage("La Orden de Trabajo se ha creado correctamente");
        return "rellenar-orden-trabajo";
    }

    @Override
    public String comenzarEstudio() {
        if (ordenTrabajoInformatica != null) {
            if (ordenTrabajoInformatica.getEstado().equals(OrdenTrabajoLaboratorio.SOLICITADA)) {
//                HttpServletRequest request =
//			(HttpServletRequest)FacesContext
//				.getCurrentInstance()
//					.getExternalContext()
//						.getRequest();
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));

                ordenTrabajoInformatica.setFechainicioestudio(new Date());
                ordenTrabajoInformatica.setEstado(OrdenTrabajoLaboratorio.EN_ESTUDIO);
                ordenTrabajoInformaticaFacade.edit(ordenTrabajoInformatica);
                
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
        if (ordenTrabajoInformatica != null) {
            return "rellenar-orden-trabajo";
        } else {
            return null;
        }
    }

    @Override
    public String entregarMuestras() {
        if (ordenTrabajoInformatica != null) {
            if (ordenTrabajoInformatica.getEstado().equals(OrdenTrabajoLaboratorio.EN_ESTUDIO)) {
//                HttpServletRequest request =
//			(HttpServletRequest)FacesContext
//				.getCurrentInstance()
//					.getExternalContext()
//						.getRequest();
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));

                ordenTrabajoInformatica.setFechaentregaresultados(new Date());
                ordenTrabajoInformatica.setEstado(OrdenTrabajoLaboratorio.RESULTADOS_ENTREGADOS);
                enviarMensajeEntregaResultados();
                ordenTrabajoInformaticaFacade.edit(ordenTrabajoInformatica);
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
        if (ordenTrabajoInformatica != null) {
            if (ordenTrabajoInformatica.getEstado().equals(OrdenTrabajoLaboratorio.RESULTADOS_ENTREGADOS)) {
                try {
                    Date fecha = new Date();
                    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//                    ordenTrabajoLab.setRegistroFirmaEntrega("Firmado por " + login.getName() + " desde " + request.getRemoteAddr() + " a las " + df.format(new Date()));
                    ordenTrabajoInformatica.setRegistroFirmaEntrega("Firmado por " + getLogin().getName() + " a las " + df.format(fecha));
                    ordenTrabajoInformatica.setEstado(OrdenTrabajoLaboratorio.FINALIZADA);
                    ordenTrabajoInformatica.setFechafirmaentregadatos(fecha);
                    ordenTrabajoInformaticaFacade.edit(ordenTrabajoInformatica);
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
        if (ordenTrabajoInformatica != null) {
            if (ordenTrabajoInformatica.getEstado().equals(OrdenTrabajoLaboratorio.RELLENANDO)) {
                HttpServletRequest request =
                        (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date fecha = new Date();
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));

                ordenTrabajoInformatica.setRegistroFirmaPeticion("Firmado por " + getLogin().getName() + " a las " + df.format(fecha));

                ordenTrabajoInformatica.setFechasolicitud(fecha);
                ordenTrabajoInformatica.setEstado(OrdenTrabajoLaboratorio.SOLICITADA);

                ordenTrabajoInformatica.setNumordentrabajo(ordenTrabajoLaboratorioFacade.getSiguienteNumOrdenTrabajoInformatica(getTecnica()));

                ordenTrabajoInformaticaFacade.edit(ordenTrabajoInformatica);
                enviarMensajeSolicitudTrabajo();
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
        if (ordenTrabajoInformatica != null) {
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
        ordenTrabajoInformaticaFacade.edit(ordenTrabajoInformatica);
    }

    public List<OrdenTrabajoInformatica> getOrdenTrabajoLaboratorioInformaticaPendientesItems() {
        return ordenTrabajoInformaticaFacade.findOrdenTrabajoLaboratorioInformaticaPendientesEntities(getTecnica());
    }


    @Override
    public void enviarMensajeEntregaResultados(){
        if(ordenTrabajoInformatica.getSolicitante()==null)
            return;
        
        if(ordenTrabajoInformatica.getSolicitante().getContratoActual()==null)
            return;
        
        String correoSolicitante = ordenTrabajoInformatica.getSolicitante().getContratoActual().getEMail();
        
        if(correoSolicitante==null||correoSolicitante.isEmpty())
            return;
        
        String nombreOrden=getOrdenTrabajoLab().getNumordentrabajo();

        if(ordenTrabajoInformatica.getNombre()!=null&&!ordenTrabajoInformatica.getNombre().isEmpty()){
            nombreOrden+=" ("+ordenTrabajoInformatica.getNombre()+")";
        }

        String titulo = "["+getTecnica().getReferencia()+"] Entrega de Resultados para "+nombreOrden;
        String mensaje = "El Servicio de "+getTecnica().getNombre()+" ha terminado el Estudio para la Orden de Apoyo Técnico "+nombreOrden+"\r\n\r\n";
//        mensaje+="Podrá ver la ficha de la Orden de Apoyo Técnico y los Resultados subidos por el técnico en: "+"\r\n";
//        mensaje+="https://intranet.iact.csic.es:8081/iact-adm/services/"+getUrlName()+"/verordentrabajo.xhtml?id="+getOrdenTrabajoLab().getId()+"\r\n\r\n";

        mensaje+="Por favor, no olvide FIRMAR LA ENTREGA. Si no se firma, se procederá a hacerse de forma automática pasados 15 días después de haber recibido este mensaje."+"\r\n\r\n";
        mensaje+="Reciba un cordial Saludo"+"\r\n\r\n";

        String receptores[] = {correoSolicitante};
//        String receptores[] = {"edu@iact.ugr-csic.es"};
        try {
            postMail(receptores, titulo, mensaje);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }



    }
    
    @Override
    public void enviarMensajeSolicitudTrabajo(){
        if(ordenTrabajoInformatica.getSolicitante()==null)
            return;
        
        if(ordenTrabajoInformatica.getSolicitante().getContratoActual()==null)
            return;
        
        String correoTecnico = getTecnica().getResponsableTecnico().getContratoActual().getEMail();
        
        if(correoTecnico==null||correoTecnico.isEmpty())
            return;
        
        String nombreOrden=getOrdenTrabajoLab().getNumordentrabajo();
        
//        Empleado solicitante = getOrdenTrabajoLab().getSolicitante();
//        if(solicitante==null)
//            return;
        
        
        
        if(ordenTrabajoInformatica.getNombre()!=null&&!ordenTrabajoInformatica.getNombre().isEmpty()){
            nombreOrden+=" ("+ordenTrabajoInformatica.getNombre()+")";
        }

        String titulo = "["+getTecnica().getReferencia()+"] Solicitud de apoyo técnico "+nombreOrden;
        String mensaje = "Han solicitado una nueva solicitud de apoyo técnico para su servicio.\r\n\r\n";
//        mensaje+="Podrá ver la ficha de la Orden de Apoyo Técnico y los Resultados subidos por el técnico en: "+"\r\n";
//        mensaje+="https://intranet.iact.csic.es:8081/iact-adm/services/"+getUrlName()+"/verordentrabajo.xhtml?id="+getOrdenTrabajoLab().getId()+"\r\n\r\n";

//        mensaje+="Por favor, no olvide FIRMAR LA ENTREGA. Si no se firma, se procederá a hacerse de forma automática pasados 15 días después de haber recibido este mensaje."+"\r\n\r\n";
//        mensaje+="Reciba un cordial Saludo"+"\r\n\r\n";
        mensaje+="Reciba un cordial Saludo"+"\r\n\r\n";

        String receptores[] = {correoTecnico};
//        String receptores[] = {"edu@iact.ugr-csic.es"};
        try {
            postMail(receptores, titulo, mensaje);
            
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

    }
    
}
