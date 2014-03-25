package laboratorios.jsfcontroller;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.*;
import javax.mail.MessagingException;
import javax.mail.Session;
import jsf.util.ServicioCorreo;
import laboratorios.facade.DocumentoResultadosFacade;
import laboratorios.facade.OrdenTrabajoLaboratorioFacade;
import laboratorios.facade.PermisoEmpleadoFacade;
import laboratorios.facade.TecnicaFacade;
import laboratorios.facade.TrabajoFacade;
import laboratorios.facade.TrabajoOrdenFacade;
import personal.facade.EmpleadoFacade;
import proyectos.facade.ProyectoFacade;

@ManagedBean(name="prelam")
@SessionScoped
public class LAMINASController extends LaboratorioController {
    @Resource(name = "mail/iactavisos")
    private Session mailSession;
    
    @Override
    protected Session getMailSession() {
        return mailSession;
    }
    
    @EJB
    private OrdenTrabajoLaboratorioFacade ordenTrabajoLaboratorioFacade;

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
    private PermisoEmpleadoFacade permisoEmpleadoFacade;
    
    @Override
    protected PermisoEmpleadoFacade getPermisoEmpleadoFacade() {
        return permisoEmpleadoFacade;
    }
    
    @PostConstruct
    public void init(){
        setTecnica(tecnicaFacade.findByNombre("Láminas petrográficas y probetas"));
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

    public String ponerEnCola() {
        if (getOrdenTrabajoLab() != null) {
            getOrdenTrabajoLab().setFechasolicitud(new Date());

            enviarMensajeEncolamiento();
            ordenTrabajoLaboratorioFacade.edit(getOrdenTrabajoLab());
            return null;

        } else {
            return null;
        }
    }

    public void enviarMensajeEncolamiento() {
        if (getOrdenTrabajoLab().getSolicitante() == null) {
            return;
        }

        if (getOrdenTrabajoLab().getSolicitante().getContratoActual() == null) {
            return;
        }

        String correoSolicitante = getOrdenTrabajoLab().getSolicitante().getContratoActual().getEMail();

        if (correoSolicitante == null || correoSolicitante.isEmpty()) {
            return;
        }

        String nombreOrden = getOrdenTrabajoLab().getNumordentrabajo();

        if (getOrdenTrabajoLab().getNombre() != null && !getOrdenTrabajoLab().getNombre().isEmpty()) {
            nombreOrden += " (" + getOrdenTrabajoLab().getNombre() + ")";
        }

        String titulo = "[PRE-LAM] La orden " + nombreOrden + " ha sido puesto en cola por el técnico";
        String mensaje = "Su Orden de Apoyo Técnico " + nombreOrden + " ha sido puesto en cola. Póngase en contacto con el técnico responsable del servicio.\r\n\r\n";
//        mensaje += "Podrá ver la ficha de la Orden de Apoyo Técnico en: " + "\r\n";
//        mensaje+="https://150.214.192.118:8081/iact-adm/laboratorios/fluorescencia/verordentrabajo.xhtml?id="+getOrdenTrabajoLab().getId()+"\r\n\r\n";
//        mensaje += "https://150.214.192.76:8081/iact-adm/laboratorios/laminasdelgadas/verordentrabajo.xhtml?id=" + getOrdenTrabajoLab().getId() + "\r\n\r\n";
        mensaje += "Reciba un cordial Saludo" + "\r\n\r\n";

        String receptores[] = {correoSolicitante};
        try {
            postMail(receptores, titulo, mensaje);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

    }

  
}
