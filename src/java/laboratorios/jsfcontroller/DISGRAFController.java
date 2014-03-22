package laboratorios.jsfcontroller;
import com.icesoft.faces.context.FileResource;
import com.icesoft.faces.context.Resource;
import org.icefaces.component.fileentry.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.*;
import javax.faces.context.FacesContext;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.servlet.http.HttpServletRequest;
import jsf.util.JsfUtil;
import jsf.util.ResultadosResource;
import jsf.util.ServicioCorreo;
import laboratorios.facade.DibujoFacade;
import laboratorios.facade.DocumentoResultadosFacade;
import laboratorios.facade.FicheroTrabajoFacade;
import laboratorios.facade.OrdenTrabajoDibujoFacade;
import laboratorios.facade.OrdenTrabajoLaboratorioFacade;
import laboratorios.facade.PermisoEmpleadoFacade;
import laboratorios.facade.TecnicaFacade;
import laboratorios.facade.TrabajoFacade;
import laboratorios.facade.TrabajoOrdenFacade;
import laboratorios.modelo.Dibujo;
import laboratorios.modelo.FicheroTrabajo;
import laboratorios.modelo.OrdenTrabajoDibujo;
import laboratorios.modelo.OrdenTrabajoLaboratorio;
import personal.facade.EmpleadoFacade;
import proyectos.facade.ProyectoFacade;


@ManagedBean(name="comgd")
@SessionScoped
public class DISGRAFController extends LaboratorioController{
    @javax.annotation.Resource(name = "mail/iactavisos")
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
    private OrdenTrabajoDibujoFacade ordenTrabajoDibujoFacade;

    @EJB
    private DibujoFacade dibujoFacade;

    @EJB
    private FicheroTrabajoFacade ficheroTrabajoFacade;

    @EJB
    private PermisoEmpleadoFacade permisoEmpleadoFacade;
    
    @Override
    protected PermisoEmpleadoFacade getPermisoEmpleadoFacade() {
        return permisoEmpleadoFacade;
    }
    
    @PostConstruct
    public void init(){
        setTecnica(tecnicaFacade.findByNombre("Ilustración científica"));
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

    private OrdenTrabajoDibujo ordenTrabajoDibujo;

    public OrdenTrabajoDibujo getOrdenTrabajoDibujo() {
        return ordenTrabajoDibujo;
    }

    public void setOrdenTrabajoDibujo(OrdenTrabajoDibujo ordenTrabajoDibujo) {
        this.ordenTrabajoDibujo = ordenTrabajoDibujo;
    }


    public List<OrdenTrabajoDibujo> getOrdenTrabajoLaboratorioDibujoItems() {
        List<OrdenTrabajoDibujo> ordenTrabajoLabEncontrados;
        if (getColumnaFiltro() == null) {
            if (getLogin().isInvestigador()) {
                ordenTrabajoLabEncontrados = ordenTrabajoDibujoFacade.findOrdenTrabajoLaboratorioDibujoEntities(getTecnica(), getLogin().getDni(), getColumnaFiltro(), isAscendente());
            } else {
                ordenTrabajoLabEncontrados = ordenTrabajoDibujoFacade.findByTecnica(getTecnica());
            }

        } else {
            if (getLogin().isInvestigador()) {
                ordenTrabajoLabEncontrados = ordenTrabajoDibujoFacade.findOrdenTrabajoLaboratorioDibujoEntities(getTecnica(), getLogin().getDni(), getColumnaFiltro(), isAscendente());
            } else {
                ordenTrabajoLabEncontrados = ordenTrabajoDibujoFacade.findOrdenTrabajoLaboratorioDibujoTecnicoEntities(getTecnica(), getColumnaFiltro(), isAscendente());
            }
        }

        return ordenTrabajoLabEncontrados;
    }

    @Override
    public String verOrdenTrabajoLab() {
        if (ordenTrabajoDibujo != null && ordenTrabajoDibujo.getId() != null) {
            ordenTrabajoDibujo = ordenTrabajoDibujoFacade.find(ordenTrabajoDibujo.getId());
            return "ver-orden-trabajo";
        } else {
            return listSetup();
        }
    }

    @Override
    public String rellenarOrdenTrabajoSetup() {
        ordenTrabajoDibujo = new OrdenTrabajoDibujo();
        ordenTrabajoDibujo.setEstado(OrdenTrabajoLaboratorio.RELLENANDO);
        ordenTrabajoDibujo.setSolicitante(empleadoFacade.find(getLogin().getDni()));
        ordenTrabajoDibujo.setTecnica(getTecnica());
        getTecnica().getOrdenesTrabajo().add(ordenTrabajoDibujo);
        try {
            ordenTrabajoDibujoFacade.create(ordenTrabajoDibujo);
            tecnicaFacade.edit(getTecnica());
            JsfUtil.addSuccessMessage("La Orden de Trabajo se ha creado correctamente");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "Error al almacenar.");
            e.printStackTrace();
            return null;
        }
        return "rellenar-orden-trabajo";
    }

    @Override
    public String comenzarEstudio() {
        if (ordenTrabajoDibujo != null) {
            if (ordenTrabajoDibujo.getEstado().equals(OrdenTrabajoLaboratorio.SOLICITADA)) {
//                HttpServletRequest request =
//			(HttpServletRequest)FacesContext
//				.getCurrentInstance()
//					.getExternalContext()
//						.getRequest();
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));

                ordenTrabajoDibujo.setFechainicioestudio(new Date());
                ordenTrabajoDibujo.setEstado(OrdenTrabajoLaboratorio.EN_ESTUDIO);

                ordenTrabajoDibujoFacade.edit(ordenTrabajoDibujo);

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
        if (ordenTrabajoDibujo != null) {
            return "rellenar-orden-trabajo";
        } else {
            return null;
        }
    }

    @Override
    public String entregarMuestras() {
        if (ordenTrabajoDibujo != null) {
            if (ordenTrabajoDibujo.getEstado().equals(OrdenTrabajoLaboratorio.EN_ESTUDIO)) {
//                HttpServletRequest request =
//			(HttpServletRequest)FacesContext
//				.getCurrentInstance()
//					.getExternalContext()
//						.getRequest();
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));

                ordenTrabajoDibujo.setFechaentregaresultados(new Date());
                ordenTrabajoDibujo.setEstado(OrdenTrabajoLaboratorio.RESULTADOS_ENTREGADOS);
                enviarMensajeEntregaResultados();
                
                ordenTrabajoDibujoFacade.edit(ordenTrabajoDibujo);

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
        if (ordenTrabajoDibujo != null) {
            if (ordenTrabajoDibujo.getEstado().equals(OrdenTrabajoLaboratorio.RESULTADOS_ENTREGADOS)) {
                try {
                    Date fecha = new Date();
                    HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//                    ordenTrabajoLab.setRegistroFirmaEntrega("Firmado por " + login.getName() + " desde " + request.getRemoteAddr() + " a las " + df.format(new Date()));
                    ordenTrabajoDibujo.setRegistroFirmaEntrega("Firmado por " + getLogin().getName() + " a las " + df.format(fecha));
                    ordenTrabajoDibujo.setEstado(OrdenTrabajoLaboratorio.FINALIZADA);
                    ordenTrabajoDibujo.setFechafirmaentregadatos(fecha);
                    ordenTrabajoDibujoFacade.edit(ordenTrabajoDibujo);
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
        if (ordenTrabajoDibujo != null) {
            if (ordenTrabajoDibujo.getEstado().equals(OrdenTrabajoLaboratorio.RELLENANDO)) {
                HttpServletRequest request =
                        (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date fecha = new Date();
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));

                ordenTrabajoDibujo.setRegistroFirmaPeticion("Firmado por " + getLogin().getName() + " a las " + df.format(fecha));

                ordenTrabajoDibujo.setFechasolicitud(fecha);
                ordenTrabajoDibujo.setEstado(OrdenTrabajoLaboratorio.SOLICITADA);

                ordenTrabajoDibujo.setNumordentrabajo(ordenTrabajoLaboratorioFacade.getSiguienteNumOrdenTrabajoDibujo(getTecnica()));

                ordenTrabajoDibujoFacade.edit(ordenTrabajoDibujo);

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
        if (ordenTrabajoDibujo != null) {
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
        ordenTrabajoDibujoFacade.edit(ordenTrabajoDibujo);
    }

    public List<OrdenTrabajoDibujo> getOrdenTrabajoLaboratorioInformaticaPendientesItems() {
        return ordenTrabajoDibujoFacade.findByTecnicaPendientes(getTecnica());
    }

    public String getDirectorioFicherosTrabajo(){
        File f = new File("/home/intranet/DOCUMENTOS/dibujo/"+ordenTrabajoDibujo.getId()+"/ficherosTrabajo");

        if(!f.exists())
            f.mkdirs();

        return f.getAbsolutePath();

    }

    public String getDirectorioDibujos(){
        File f = new File("/home/intranet/DOCUMENTOS/dibujo/"+ordenTrabajoDibujo.getId()+"/dibujos");

        if(!f.exists())
            f.mkdirs();

        return f.getAbsolutePath();

    }

    @Override
    public void uploaded(FileEntryEvent event){
        FileEntry fileEntry = (FileEntry) event.getSource();
        FileEntryResults results = fileEntry.getResults();
        for (FileEntryResults.FileInfo fileInfo : results.getFiles()) {
            if (fileInfo.isSaved()) {
                    String directorioPadre=fileInfo.getFile().getParent();
                    if(directorioPadre.endsWith("ficherosTrabajo")){
                        FicheroTrabajo ft= new FicheroTrabajo();
                        ft.setFile(fileInfo.getFile());
                        ft.setOrdenTrabajoDibujo(ordenTrabajoDibujo);
                        ficheroTrabajoFacade.create(ft);
                        ordenTrabajoDibujo.getFicherosTrabajo().add(ft);
                        ordenTrabajoDibujoFacade.edit(ordenTrabajoDibujo);
                    }else if(directorioPadre.endsWith("dibujos")){
                        Dibujo d= new Dibujo();
                        d.setFile(fileInfo.getFile());
                        d.setOrdenTrabajoDibujo(ordenTrabajoDibujo);
                        dibujoFacade.create(d);
                        ordenTrabajoDibujo.getDibujos().add(d);
                        ordenTrabajoDibujoFacade.edit(ordenTrabajoDibujo);

                    }

            }else{
                System.out.println("uploaded(): Invalid file???");
            }
        }

 }

    public String enviarCorreccion(){
        enviarMensajeEntregaResultados();
        return null;
    }

    @Override
    public void enviarMensajeEntregaResultados(){
        if(ordenTrabajoDibujo.getSolicitante()==null)
            return;
        
        if(ordenTrabajoDibujo.getSolicitante().getContratoActual()==null)
            return;
        
        String correoSolicitante = ordenTrabajoDibujo.getSolicitante().getContratoActual().getEMail();
        
        if(correoSolicitante==null||correoSolicitante.isEmpty())
            return;

        
        String nombreOrden=ordenTrabajoDibujo.getNumordentrabajo();

        if(ordenTrabajoDibujo.getNombre()!=null&&!ordenTrabajoDibujo.getNombre().isEmpty()){
            nombreOrden+=" ("+ordenTrabajoDibujo.getNombre()+")";
        }

        String titulo = "[DIB] Entrega de Dibujo para "+nombreOrden;
        String mensaje = "El Servicio de Diseño gráfico ha subido un nuevo Dibujo correspondiente a la Orden de Apoyo Técnico "+nombreOrden+"\r\n\r\n";
//        mensaje+="Podrá ver la ficha de la Orden de Apoyo Técnico y los Dibujos subidos por el técnico en: "+"\r\n";
//        mensaje+="https://intranet.iact.csic.es:8081/iact-adm/services/com/gd/verordentrabajo.xhtml?id="+ordenTrabajoDibujo.getId()+"\r\n\r\n";

        mensaje+="Por favor, no olvide FIRMAR LA ENTREGA cuando finalicen todas las correcciones. Si no se firma, se procederá a hacerse de forma automática pasados 15 días después de haber recibido este mensaje."+"\r\n\r\n";
        mensaje+="Reciba un cordial Saludo"+"\r\n\r\n";

        String receptores[] = {correoSolicitante};
        try {
            postMail(receptores, titulo, mensaje);
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }



    }

    public List<OrdenTrabajoDibujo> getOrdenTrabajoLaboratorioDibujoPendientesItems() {
        return ordenTrabajoDibujoFacade.findByTecnicaPendientes(getTecnica());
    }


    public List<ResultadosResource> getDibujoResources(){
        ArrayList<ResultadosResource> resources=new ArrayList<ResultadosResource>();
        for(Dibujo dr : getOrdenTrabajoDibujo().getDibujos()){
            resources.add(new ResultadosResource(dr.getFile(),dr.getFileName()));
        }
        return resources;
    }

    public List<ResultadosResource> getFicheroTrabajoResources(){
        ArrayList<ResultadosResource> resources=new ArrayList<ResultadosResource>();
        for(FicheroTrabajo f : getOrdenTrabajoDibujo().getFicherosTrabajo()){
            resources.add(new ResultadosResource(f.getFile(),f.getFileName()));
        }
        return resources;
    }
    
}
