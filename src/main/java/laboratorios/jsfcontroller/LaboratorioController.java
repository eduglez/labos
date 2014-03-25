package laboratorios.jsfcontroller;

import org.icefaces.component.fileentry.*;
import com.icesoft.faces.context.ByteArrayResource;
import com.icesoft.faces.context.Resource;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import jsf.util.JsfUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import jsf.controllers.LoginController;
import jsf.util.ResultadosResource;
//import jsf.util.ServicioCorreo;
import laboratorios.facade.DocumentoResultadosFacade;
import laboratorios.facade.OrdenTrabajoLaboratorioFacade;
import laboratorios.facade.PermisoEmpleadoFacade;
import laboratorios.facade.TecnicaFacade;
import laboratorios.facade.TrabajoFacade;
import laboratorios.facade.TrabajoOrdenFacade;
import laboratorios.modelo.DocumentoResultados;
import laboratorios.modelo.OrdenTrabajoLaboratorio;
import laboratorios.modelo.Tecnica;
import laboratorios.modelo.Trabajo;
import laboratorios.modelo.TrabajoOrden;
import ooxmlreports.Excelizeme;
import ooxmlreports.Helper;
import org.apache.velocity.VelocityContext;
import personal.facade.EmpleadoFacade;
import personal.modelo.Empleado;
import proyectos.facade.ProyectoFacade;
import proyectos.modelo.Proyecto;

public abstract class LaboratorioController {

    protected abstract OrdenTrabajoLaboratorioFacade getOrdenTrabajoLaboratorioFacade();

    protected abstract ProyectoFacade getProyectoFacade();

    protected abstract EmpleadoFacade getEmpleadoFacade();

    protected abstract TecnicaFacade getTecnicaFacade();

    protected abstract TrabajoFacade getTrabajoFacade();

    protected abstract TrabajoOrdenFacade getTrabajoOrdenFacade();

    protected abstract PermisoEmpleadoFacade getPermisoEmpleadoFacade();

    protected abstract DocumentoResultadosFacade getDocumentoResultadosFacade();

    protected abstract Session getMailSession();
    // <editor-fold defaultstate="collapsed" desc="Filtros">
    private boolean filtroEstadoRellenando;
    private boolean filtroEstadoSolictida;
    private boolean filtroEstadoEnEstudio;
    private boolean filtroEstadoEntregado;
    private boolean filtroFinalizada;

    public boolean isFiltroEstadoEnEstudio() {
        return filtroEstadoEnEstudio;
    }

    public void setFiltroEstadoEnEstudio(boolean filtroEstadoEnEstudio) {
        this.filtroEstadoEnEstudio = filtroEstadoEnEstudio;
    }

    public boolean isFiltroEstadoEntregado() {
        return filtroEstadoEntregado;
    }

    public void setFiltroEstadoEntregado(boolean filtroEstadoEntregado) {
        this.filtroEstadoEntregado = filtroEstadoEntregado;
    }

    public boolean isFiltroEstadoRellenando() {
        return filtroEstadoRellenando;
    }

    public void setFiltroEstadoRellenando(boolean filtroEstadoRellenando) {
        this.filtroEstadoRellenando = filtroEstadoRellenando;
    }

    public boolean isFiltroEstadoSolictida() {
        return filtroEstadoSolictida;
    }

    public void setFiltroEstadoSolictida(boolean filtroEstadoSolictida) {
        this.filtroEstadoSolictida = filtroEstadoSolictida;
    }

    public boolean isFiltroFinalizada() {
        return filtroFinalizada;
    }

    public void setFiltroFinalizada(boolean filtroFinalizada) {
        this.filtroFinalizada = filtroFinalizada;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="tecnica">
    private Tecnica tecnica;

    public Tecnica getTecnica() {
        return tecnica;
    }

    public void setTecnica(Tecnica tecnica) {
        this.tecnica = tecnica;
    }
    // </editor-fold>
    private OrdenTrabajoLaboratorioConverter ordenTrabajoLabConverter;
    // <editor-fold defaultstate="collapsed" desc="login">
    private LoginController login;

    public LoginController getLogin() {
        return login;
    }

    public void setLogin(LoginController login) {
        this.login = login;
    }
    // </editor-fold>

    public LaboratorioController() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        login = (LoginController) facesContext.getApplication().getELResolver().getValue(facesContext.getELContext(), null, "login");
        ascendente = false;
        columnaFiltro = "fechasolicitud";
        ordenTrabajoLabConverter = new OrdenTrabajoLaboratorioConverter();

        setFiltroEstadoEnEstudio(true);
        setFiltroEstadoEntregado(true);
        setFiltroEstadoRellenando(true);
        setFiltroEstadoSolictida(true);
        setFiltroFinalizada(true);

        //servicioLaboratorio=jpaController.findServicioLaboratorio("Fluorescencia");
    }
    private OrdenTrabajoLaboratorio ordenTrabajoLab = null;
    private TrabajoOrden trabajoSeleccionado;

    public TrabajoOrden getTrabajoSeleccionado() {
        return trabajoSeleccionado;
    }

    public void setTrabajoSeleccionado(TrabajoOrden trabajoSeleccionado) {
        this.trabajoSeleccionado = trabajoSeleccionado;
    }

    public Resource getOrdenPedidoWordProcessingML() {
        if (getOrdenTrabajoLab() == null) {
            return null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        VelocityContext context = new VelocityContext();

        context.put("fechaSolicitud", getOrdenTrabajoLab().getFechasolicitud() != null ? sdf.format(getOrdenTrabajoLab().getFechasolicitud()) : "");

        context.put("ip", getOrdenTrabajoLab().getProyecto().getReferencia() + " - " + getOrdenTrabajoLab().getInvestigadorPrincipal());

        context.put("email", getOrdenTrabajoLab().getCorreoInvestigadorPrincipal() != null ? getOrdenTrabajoLab().getCorreoInvestigadorPrincipal() : " ");

        context.put("dias", "-");

        context.put("numOrdenTrabajo", getOrdenTrabajoLab().getNumordentrabajo() != null ? getOrdenTrabajoLab().getNumordentrabajo() : "NO DEFINIDO");

        context.put("tipoTrabajo", getOrdenTrabajoLab().getDescripcion() != null ? getOrdenTrabajoLab().getDescripcion() : " ");

        context.put("firmaPeticion", getOrdenTrabajoLab().getRegistroFirmaPeticion() != null ? getOrdenTrabajoLab().getRegistroFirmaPeticion() : "NO FIRMADO");

        context.put("firmaEntrega", getOrdenTrabajoLab().getRegistroFirmaEntrega() != null ? getOrdenTrabajoLab().getRegistroFirmaEntrega() : "NO FIRMADO");

        context.put("trabajos", getOrdenTrabajoLab().getTrabajos().iterator());




        return new ByteArrayResource(Helper.openPattern(LaboratorioController.class.getResource("/reports/solicitudApoyoTecnico.docx").getPath(), context).toByteArray());
    }

    public Resource getResumenExcel() {
        try {
            return new ByteArrayResource(
                    Excelizeme.getSuperExcel(
                    getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioEntities()).toByteArray());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

//    public String getNombreFicheroOrdenPedidoWordProcessingML() {
//        return "OrdenPedido_" + getPedido().getNumPedido() + ".docx";
//    }
//
    public SelectItem[] getOrdenTrabajoLaboratorioItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(
                getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioEntities(), true);
    }

    public SelectItem[] getProyectoItemsAvailableSelectOne() {
        List<Proyecto> proyectos = Collections.EMPTY_LIST;
        if(login.isInvestigador()){
            proyectos = getProyectoFacade().findVigentes(login.getDni());
            proyectos.add(0, getProyectoFacade().getProyectoUGR());
            proyectos.addAll(getPermisoEmpleadoFacade().getProyectosAutorizadoServiciosCT(login.getEmpleado()));
        }else if(isAutorizado()){
            proyectos = getPermisoEmpleadoFacade().getProyectosAutorizadoServiciosCT(login.getEmpleado());
        }
        
        return JsfUtil.getSelectItems(proyectos, true);
    }

    public SelectItem[] getEstadoOrdenTrabajoItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(Arrays.asList(OrdenTrabajoLaboratorio.ESTADOS), true);
    }
    // <editor-fold defaultstate="collapsed" desc="ascendente">
    private boolean ascendente;

    public boolean isAscendente() {
        return ascendente;
    }

    public void setAscendente(boolean ascendente) {
        this.ascendente = ascendente;
    }
    // </editor-fold>
    // <editor-fold defaultstate="collapsed" desc="columnaFiltro">
    private String columnaFiltro;

    public String getColumnaFiltro() {
        return columnaFiltro;
    }

    public void setColumnaFiltro(String columnaFiltro) {
        this.columnaFiltro = columnaFiltro;
    }
    // </editor-fold>

    public OrdenTrabajoLaboratorio getOrdenTrabajoLab() {
        OrdenTrabajoLaboratorio solicitadoPorURL = (OrdenTrabajoLaboratorio) JsfUtil.getObjectFromRequestParameter("id", ordenTrabajoLabConverter, null);
        if (solicitadoPorURL != null) {

            if (login.isInvestigador()
                    && getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioEntities(tecnica, login.getDni()).contains(solicitadoPorURL)) {
                ordenTrabajoLab = solicitadoPorURL;
            }
        }
        return ordenTrabajoLab;
    }

    public void setOrdenTrabajoLab(OrdenTrabajoLaboratorio ordenTrabajoLab) {
        this.ordenTrabajoLab = ordenTrabajoLab;
    }

    public String verOrdenTrabajoLab() {
        if (ordenTrabajoLab != null && ordenTrabajoLab.getId() != null) {
            ordenTrabajoLab = getOrdenTrabajoLaboratorioFacade().find(ordenTrabajoLab.getId());
            return "ver-orden-trabajo";
        } else {
            return listSetup();
        }
    }

    public String salirSinGuardar() {
        return verOrdenTrabajoLab();
    }

    public String salirGuardando() {
        guardarCambios();
        return verOrdenTrabajoLab();
    }

    public String listSetup() {
        reset();
        return "listar-ordenes-trabajo";
    }

    public String rellenarOrdenTrabajoSetup() {

        reset();
        ordenTrabajoLab = new OrdenTrabajoLaboratorio();
        ordenTrabajoLab.setEstado(OrdenTrabajoLaboratorio.RELLENANDO);
        ordenTrabajoLab.setSolicitante(getEmpleadoFacade().find(getLogin().getDni()));
        ordenTrabajoLab.setTrabajos(new ArrayList<TrabajoOrden>());
        ordenTrabajoLab.setTecnica(tecnica);
        ordenTrabajoLab.setBorrado(true);
        tecnica.getOrdenesTrabajo().add(ordenTrabajoLab);
        try {
            getOrdenTrabajoLaboratorioFacade().create(getOrdenTrabajoLab());
            getTecnicaFacade().edit(tecnica);

        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "Error al almacenar.");
            return null;
        }
        return "rellenar-orden-trabajo";
    }

    public String editarSetup() {
        if (ordenTrabajoLab != null) {
            return "rellenar-orden-trabajo";
        } else {
            return null;
        }
    }

    public String rellenarOrdenTrabajo() {
        try {
            double numTrabajos;
            numTrabajos = renumerarTrabajos(getOrdenTrabajoLab().getTrabajos().iterator());
            if (numTrabajos > 1.0) {
                JsfUtil.addErrorMessage("El número de tareas ha superado el máximo del servicio");
                return null;
            }

            guardarCambios();
            JsfUtil.addSuccessMessage("La orden de trabajo se ha almacenado correctamente.");
        } catch (Exception e) {
            JsfUtil.ensureAddErrorMessage(e, "Error al almacenar.");
            return null;
        }
        return "ver-orden-trabajo";
    }

    public String revisarOrdenTrabajoSetup() {
        if (ordenTrabajoLab != null) {
            return "revisar-orden-trabajo";
        } else {
            return null;
        }
    }

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

    public void guardarCambios() {
        getOrdenTrabajoLab().setBorrado(false);

        for (TrabajoOrden t : getOrdenTrabajoLab().getTrabajos()) {
            if (t.isMarcaBorrado()) {
                t.setOrdenTrabajoLaboratorio(null);
            }
        }

        getOrdenTrabajoLaboratorioFacade().edit(getOrdenTrabajoLab());

        Iterator<TrabajoOrden> itTrabajosPedido = getOrdenTrabajoLab().getTrabajos().iterator();
        while (itTrabajosPedido.hasNext()) {
            if (itTrabajosPedido.next().isMarcaBorrado()) {
                itTrabajosPedido.remove();
            }
        }


        getOrdenTrabajoLaboratorioFacade().edit(getOrdenTrabajoLab());
        JsfUtil.addSuccessMessage("La Orden de Trabajo se ha guardado correctamente");
    }

    public String destroy() {

        getOrdenTrabajoLab().setBorrado(true);
        getOrdenTrabajoLaboratorioFacade().edit(getOrdenTrabajoLab());
        JsfUtil.addSuccessMessage("La orden de trabajo se ha borrado satisfactoriamente.");
        return listSetup();
    }

    public List<OrdenTrabajoLaboratorio> getOrdenTrabajoLaboratorioItems() {
        List<OrdenTrabajoLaboratorio> ordenTrabajoLabEncontrados= Collections.EMPTY_LIST;
        if (columnaFiltro == null) {
            if (login.isInvestigador()) {
                ordenTrabajoLabEncontrados = getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioEntities(tecnica, login.getEmpleado());
//              ordenTrabajoLabEncontrados= getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioEntities(tecnica, login.getDni());
            } else if(isTecnico()){
                ordenTrabajoLabEncontrados = getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioTecnicoEntities(tecnica);
            }else if(isAutorizado()){
                ordenTrabajoLabEncontrados = getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioEntitiesAutorizado(tecnica, login.getEmpleado());
            }

        } else {
            if (login.isInvestigador()) {
                ordenTrabajoLabEncontrados = getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioEntities(tecnica, login.getEmpleado(), columnaFiltro, ascendente);
//                ordenTrabajoLabEncontrados=getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioEntities(tecnica,login.getDni(), columnaFiltro, ascendente);
            } else if(isTecnico()) {
                ordenTrabajoLabEncontrados = getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioTecnicoEntities(tecnica, columnaFiltro, ascendente);
            }else if(isAutorizado()){
                ordenTrabajoLabEncontrados = getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioEntitiesAutorizado(tecnica, login.getEmpleado(), columnaFiltro, ascendente);
            }
        }

        return ordenTrabajoLabEncontrados;

    }

    public List<OrdenTrabajoLaboratorioWrapped> getOrdenTrabajoLaboratorioItemsWrapped() {
        ArrayList<OrdenTrabajoLaboratorioWrapped> ordenesWrapped = new ArrayList<OrdenTrabajoLaboratorioWrapped>();

        for (OrdenTrabajoLaboratorio otl : getOrdenTrabajoLaboratorioItems()) {
            ordenesWrapped.add(new OrdenTrabajoLaboratorioWrapped(otl));
        }

        return ordenesWrapped;
    }

    public List<OrdenTrabajoLaboratorio> getOrdenTrabajoLaboratorioPendientesItems() {
        return getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioPendientesEntities(tecnica);
    }

    private void reset() {
        ordenTrabajoLab = null;
    }

    public void insertarTrabajo() {
        TrabajoOrden trabajo = new TrabajoOrden();
        trabajo.setOrdenTrabajoLaboratorio(ordenTrabajoLab);
        trabajo.setTrabajo(getTrabajoFacade().findTrabajoNoRegistrado());
        ordenTrabajoLab.getTrabajos().add(trabajo);

        //Renumeramos
        renumerarTrabajos(ordenTrabajoLab.getTrabajos().iterator());

        getTrabajoOrdenFacade().create(trabajo);

        getOrdenTrabajoLaboratorioFacade().edit(ordenTrabajoLab);

    }

    private double renumerarTrabajos(Iterator<TrabajoOrden> trabajosIt) {
        int i = 1;
        double tiempoTrabajo = 0.0;

        while (trabajosIt.hasNext()) {
            TrabajoOrden t = trabajosIt.next();
            if (!t.isMarcaBorrado()) {
                t.setNum((short) i);
                i++;
                tiempoTrabajo += 1.0 / t.getTrabajo().getTiempoRelativo();
            } else {
                t.setNum((short) 0);
            }
        }

        return tiempoTrabajo;
    }

    public void toggleMarcaBorrarTrabajo() {
        getTrabajoSeleccionado().setMarcaBorrado(!getTrabajoSeleccionado().isMarcaBorrado());
    }
    private List proyectoMatchPossibilities;

    public List getProyectoMatchPossibilities() {
        return proyectoMatchPossibilities;
    }
    private String proyectoSeleccionadoValue;

    public String getProyectoSeleccionadoValue() {
        return proyectoSeleccionadoValue;
    }

    /**
     * Investigador - Firma solicitud de trabajo
     * Estado (Rellenando) -> Estado (Solicitada)
     * @return 
     */
    public String firmarPeticion() {
        
        /*
        if (!isPosibleNuevaPeticion()) {
            JsfUtil.addErrorMessage("Tiene solicitudes de trabajo sin FINALIZAR. No puede realizar otra.");
            return null;
        }

        if (!getTecnica().isActivo()) {
            JsfUtil.addErrorMessage("El Servicio está ináctivo. No puede realizar una solicitud de trabajo en estos momentos.");
            return null;
        }
        */
        if (ordenTrabajoLab != null) {
            if (ordenTrabajoLab.getEstado().equals(OrdenTrabajoLaboratorio.RELLENANDO)) {
                HttpServletRequest request =
                        (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date fecha = new Date();
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));

                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por " + login.getName() + " a las " + df.format(fecha));

                ordenTrabajoLab.setFechasolicitud(fecha);
                ordenTrabajoLab.setEstado(OrdenTrabajoLaboratorio.SOLICITADA);

                ordenTrabajoLab.setNumordentrabajo(getOrdenTrabajoLaboratorioFacade().getSiguienteNumOrdenTrabajo(tecnica));

                getOrdenTrabajoLaboratorioFacade().edit(ordenTrabajoLab);

                enviarMensajeSolicitudTrabajo();
            }
        }

        return null;
    }

    /**
     * Tecnico - Solicita las muestras
     * Estado (Solicitada) -> Estado (EntregandoMuestras)
     * @return 
     */
    public String solicitarMuestras() {
        if (ordenTrabajoLab != null) {
            if (ordenTrabajoLab.getEstado().equals(OrdenTrabajoLaboratorio.SOLICITADA)) {
                ordenTrabajoLab.setEstado(OrdenTrabajoLaboratorio.ENTREGANDO_MUESTRAS);
                getOrdenTrabajoLaboratorioFacade().edit(ordenTrabajoLab);
                enviarMensajeSolicitudMuestras();
                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Tecnico - Comienza el estudio
     * Estado (EntregandoMuestras) -> Estado (EnEstudio)
     * @return 
     */
    public String comenzarEstudio() {
        if (ordenTrabajoLab != null) {
            if (ordenTrabajoLab.getEstado().equals(OrdenTrabajoLaboratorio.ENTREGANDO_MUESTRAS)) {
                ordenTrabajoLab.setFechainicioestudio(new Date());
                ordenTrabajoLab.setEstado(OrdenTrabajoLaboratorio.EN_ESTUDIO);
                getOrdenTrabajoLaboratorioFacade().edit(ordenTrabajoLab);
                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Tecnico - Finaliza el estudio y entrega los resultados
     * Estado (EnEstudio) -> Estado (Datos Entregados)
     * @return 
     */
    public String entregarMuestras() {
        if (ordenTrabajoLab != null) {
            if (ordenTrabajoLab.getEstado().equals(OrdenTrabajoLaboratorio.EN_ESTUDIO)) {
                ordenTrabajoLab.setFechaentregaresultados(new Date());
                ordenTrabajoLab.setEstado(OrdenTrabajoLaboratorio.RESULTADOS_ENTREGADOS);
                enviarMensajeEntregaResultados();

                getOrdenTrabajoLaboratorioFacade().edit(ordenTrabajoLab);
                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Investigador - Firma la entrega de resultados y finaliza la solicitud
     * Estado (DatosEntregados) -> Estado (Finalizada)
     * @return 
     */
    public String firmarEntrega() {
        if (ordenTrabajoLab != null) {
            if (ordenTrabajoLab.getEstado().equals(OrdenTrabajoLaboratorio.RESULTADOS_ENTREGADOS)) {

                Date fecha = new Date();
                HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//                    ordenTrabajoLab.setRegistroFirmaEntrega("Firmado por " + login.getName() + " desde " + request.getRemoteAddr() + " a las " + df.format(new Date()));
                ordenTrabajoLab.setRegistroFirmaEntrega("Firmado por " + login.getName() + " a las " + df.format(fecha));
                ordenTrabajoLab.setEstado(OrdenTrabajoLaboratorio.FINALIZADA);
                ordenTrabajoLab.setFechafirmaentregadatos(fecha);
                getOrdenTrabajoLaboratorioFacade().edit(ordenTrabajoLab);
                return null;
            }
        }
        return null;

    }

    public String reiniciarEstudio() {
        if (ordenTrabajoLab != null) {
            if (ordenTrabajoLab.getEstado().equals(OrdenTrabajoLaboratorio.RESULTADOS_ENTREGADOS)) {


//                HttpServletRequest request =
//			(HttpServletRequest)FacesContext
//				.getCurrentInstance()
//					.getExternalContext()
//						.getRequest();
//                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
//
//                ordenTrabajoLab.setRegistroFirmaPeticion("Firmado por "+login.getName()+" desde "+request.getRemoteAddr()+" a las "+df.format(new Date()));


                ordenTrabajoLab.setFechainicioestudio(new Date());
                ordenTrabajoLab.setFechaentregaresultados(null);
                ordenTrabajoLab.setEstado(OrdenTrabajoLaboratorio.SOLICITADA);

                getOrdenTrabajoLaboratorioFacade().edit(ordenTrabajoLab);

                return null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public void setProyectoSeleccionadoValue(String proyectoSeleccionadoValue) {
        this.proyectoSeleccionadoValue = proyectoSeleccionadoValue;
    }
    private String trabajosParsingText;

    public String getTrabajosParsingText() {
        return trabajosParsingText;
    }

    public void setTrabajosParsingText(String trabajosParsingText) {
        this.trabajosParsingText = trabajosParsingText;
    }
    
    
    public void parseTrabajos(){
        StringTokenizer st = new StringTokenizer(trabajosParsingText, System.getProperty("line.separator"));
        while (st.hasMoreElements()) {
            String linea = st.nextToken();
            String[] s = linea.split("\\s+");
            Trabajo trabajo;
            String muestra;
            System.out.println("Buscando codigo "+s[0].toUpperCase()+" con muestra "+s[1]+" tam "+s.length);
            if(s.length>=2){
                try{
                TrabajoOrden t = new TrabajoOrden();
                if(!s[s.length-1].isEmpty()){
                    trabajo=getTrabajoFacade().findTrabajoByCodigo(s[s.length-1].toUpperCase());
                    
                    if(trabajo==null){
                        trabajo=getTrabajoFacade().findTrabajoNoRegistrado();
                        t.setTrabajo(trabajo);
                        t.setConcepto(s[s.length-1]);
                    }else{
                        t.setTrabajo(trabajo);
                    }
                    
                    muestra="";
                    for(int i=0;i<(s.length-2);i++)
                        muestra+=s[i]+" ";
                    
                    muestra+=s[s.length-2];
                    
                    if(!muestra.isEmpty()){
                        t.setMuestra(muestra);
                        t.setOrdenTrabajoLaboratorio(ordenTrabajoLab);
                        ordenTrabajoLab.getTrabajos().add(t);
                        getTrabajoOrdenFacade().create(t); 
                    }
                }
                
                }catch(Exception e){
                    
                }
            }
            
        }
        
        renumerarTrabajos(ordenTrabajoLab.getTrabajos().iterator());

        getOrdenTrabajoLaboratorioFacade().edit(ordenTrabajoLab);

        setTrabajosParsingText("");
        
        
    }
    

    private Trabajo findTrabajo(List<Trabajo> trabajos, String codTrabajo) {
        for (Trabajo t : trabajos) {
            try {
                if (getCodLaboratorio(t.getCodigo()).equalsIgnoreCase(getCodLaboratorio(codTrabajo))) {
                    if (Integer.parseInt(getCodNumericoTrabajo(t.getCodigo())) == Integer.parseInt(getCodNumericoTrabajo(codTrabajo))) {
                        return t;
                    }
                }
            } catch (Exception e) {
            }
        }
        return null;
    }

    private String getCodLaboratorio(String codTrabajo) {
        String parsedCodLab = codTrabajo.trim();
        String iCod1 = "";
        for (int i = 0; i < parsedCodLab.length(); i++) {
            if (Character.isLetter(parsedCodLab.charAt(i))) {
                iCod1 = iCod1 + parsedCodLab.charAt(i);
            }
        }

        System.out.println("[codLaboratorio] De " + codTrabajo + " he sacao " + iCod1);

        return iCod1;
    }

    private String getCodNumericoTrabajo(String codTrabajo) {
        String parsedCodLab = codTrabajo.trim();

        String fCod1 = "";
        for (int i = parsedCodLab.length() - 1; i >= 0; i--) {
            if (Character.isDigit(parsedCodLab.charAt(i))) {
                fCod1 = parsedCodLab.charAt(i) + fCod1;
            }
        }

        System.out.println("[codNumericoTrabajo] De " + codTrabajo + " he sacao " + fCod1);

        return fCod1;
    }

    
    public void parseTrabajos2() {
        List<Trabajo> trabajos = getTrabajoFacade().findAll();

        StringTokenizer st = new StringTokenizer(trabajosParsingText, System.getProperty("line.separator"));
        while (st.hasMoreElements()) {
            String linea = st.nextToken();
            StringTokenizer stTrabajo = new StringTokenizer(linea, "\t");
            String muestra;
            String trabajo;
            if (stTrabajo.hasMoreTokens()) {
                muestra = stTrabajo.nextToken();
            } else {
                continue;
            }

            if (stTrabajo.hasMoreTokens()) {
                trabajo = stTrabajo.nextToken();
            } else {
                continue;
            }

            if (muestra == null || muestra.isEmpty()) {
                continue;
            }

            if (trabajo == null || trabajo.isEmpty()) {
                continue;
            }

            TrabajoOrden t = new TrabajoOrden();
            t.setMuestra(muestra);
            Trabajo trabajoEncontrado = findTrabajo(trabajos, trabajo);
            if (trabajoEncontrado != null) {
                t.setTrabajo(trabajoEncontrado);
            } else {
                t.setTrabajo(getTrabajoFacade().findTrabajoNoRegistrado());
                t.setConcepto(trabajo);
            }

            t.setOrdenTrabajoLaboratorio(ordenTrabajoLab);
            ordenTrabajoLab.getTrabajos().add(t);
            getTrabajoOrdenFacade().create(t);
        }

        renumerarTrabajos(ordenTrabajoLab.getTrabajos().iterator());

        getOrdenTrabajoLaboratorioFacade().edit(ordenTrabajoLab);

        setTrabajosParsingText("");


    }

    //1 PETICION POR EMPLEADO
    /*
    protected boolean isPosibleNuevaPeticion(){
    List<OrdenTrabajoLaboratorio> ordenesTrabajo=getJpaController().findOrdenTrabajoLaboratorioEntities(tecnica, login.getDni());
    for(OrdenTrabajoLaboratorio orden : ordenesTrabajo){
    if(orden.getEstado().equals(OrdenTrabajoLaboratorio.SOLICITADA)||
    orden.getEstado().equals(OrdenTrabajoLaboratorio.EN_ESTUDIO)||
    orden.getEstado().equals(OrdenTrabajoLaboratorio.RESULTADOS_ENTREGADOS))
    return false;
    }
    
    return true;
    
    }
     */
    //1 PETICION POR PROYECTO
    protected boolean isPosibleNuevaPeticion() {
        if (getOrdenTrabajoLab().getProyecto() == null) {
            return false;
        }

        List<OrdenTrabajoLaboratorio> ordenesTrabajo = getOrdenTrabajoLaboratorioFacade().findOrdenTrabajoLaboratorioEntities(getTecnica(), getOrdenTrabajoLab().getProyecto());
        for (OrdenTrabajoLaboratorio orden : ordenesTrabajo) {
            if (orden.getEstado().equals(OrdenTrabajoLaboratorio.SOLICITADA)
                    || orden.getEstado().equals(OrdenTrabajoLaboratorio.EN_ESTUDIO)
                    || orden.getEstado().equals(OrdenTrabajoLaboratorio.RESULTADOS_ENTREGADOS)) {
                return false;
            }
        }

        return true;

    }

    public SelectItem[] getTrabajosSelectOne() {
        ArrayList<Trabajo> trabajos = new ArrayList<Trabajo>();
        trabajos.add(getTrabajoFacade().findTrabajoNoRegistrado());
        trabajos.addAll(getTecnica().getTrabajos());

        return JsfUtil.getSelectItems(trabajos, true);
    }

    public String getDirectorioResultados() {
        File f = new File("/home/intranet/DOCUMENTOS/" + getTecnica().getReferencia() + "/" + getOrdenTrabajoLab().getId());
        if (!f.exists()) {
            f.mkdirs();
        }
        return f.getAbsolutePath();
    }

    public void uploaded(FileEntryEvent event) {
        FileEntry fileEntry = (FileEntry) event.getSource();
        FileEntryResults results = fileEntry.getResults();
        for (FileEntryResults.FileInfo fileInfo : results.getFiles()) {
            if (fileInfo.isSaved()) {

                DocumentoResultados dr = new DocumentoResultados();
                dr.setNombreDocumento(fileInfo.getFileName());
                dr.setFile(fileInfo.getFile());
                dr.setOrdenTrabajoLaboratorio(getOrdenTrabajoLab());
                getDocumentoResultadosFacade().create(dr);
                getOrdenTrabajoLab().getResultados().add(dr);
                getOrdenTrabajoLaboratorioFacade().edit(getOrdenTrabajoLab());
            } else {
                System.out.println("uploaded(): Invalid file???");
            }
        }
    }

    private String getUrlName() {
        return tecnica.getServicio().getUrl() + "/" + tecnica.getUrl();
    }

    public boolean isTecnico() {
        return login.isInRole(tecnica.getRoleTecnico());
    }
    
    public boolean isAutorizado(){
        return login.isAutorizadoServiciosCT();
    }

    public List<Resource> getResultadoResources() {
        ArrayList<Resource> resources = new ArrayList<Resource>();
        for (DocumentoResultados dr : getOrdenTrabajoLab().getResultados()) {
            resources.add(new ResultadosResource(dr));
        }
        return resources;
    }

    public boolean isProyectoNoIACT() {
        if (getOrdenTrabajoLab() == null) {
            return false;
        }

        if (getOrdenTrabajoLab().getProyecto() == null) {
            return false;
        }

        if (getOrdenTrabajoLab().getProyecto().getId() == null) {
            return false;
        }

        if (!getOrdenTrabajoLab().getProyecto().getId().equals("0")) {
            return false;
        } else {
            return true;
        }
    }

    public void activarServicio() {
        getTecnica().activarServicio();
        getTecnicaFacade().edit(getTecnica());
    }

    public void desactivarServicio() {
        getTecnica().desactivarServicio();
        getTecnicaFacade().edit(getTecnica());
    }

    
    
    public void enviarMensajeSolicitudTrabajo() {

        Empleado solicitante = getOrdenTrabajoLab().getSolicitante();
        if (solicitante == null
                || solicitante.getContratoActual() == null) {


            return;

        }


        String correoTecnico = getTecnica().getResponsableTecnico().getContratoActual().getEMail();

        if (correoTecnico == null || correoTecnico.isEmpty()) {
            return;
        }

        String nombreOrden = getOrdenTrabajoLab().getNumordentrabajo();

        if (getOrdenTrabajoLab().getNombre() != null && !getOrdenTrabajoLab().getNombre().isEmpty()) {
            nombreOrden += " (" + getOrdenTrabajoLab().getNombre() + ")";
        }

        String titulo = "[" + getTecnica().getReferencia() + "] Solicitud de apoyo técnico " + nombreOrden;
        String mensaje = "Han solicitado una nueva solicitud de apoyo técnico para su servicio.\r\n\r\n";
//        mensaje+="Podrá ver la ficha de la Orden de Apoyo Técnico y los Resultados subidos por el técnico en: "+"\r\n";
//        mensaje+="https://intranet.iact.csic.es:8081/iact-adm/services/"+getUrlName()+"/verordentrabajo.xhtml?id="+getOrdenTrabajoLab().getId()+"\r\n\r\n";

//        mensaje+="Por favor, no olvide FIRMAR LA ENTREGA. Si no se firma, se procederá a hacerse de forma automática pasados 15 días después de haber recibido este mensaje."+"\r\n\r\n";
//        mensaje+="Reciba un cordial Saludo"+"\r\n\r\n";
        mensaje += "Reciba un cordial Saludo" + "\r\n\r\n";

        String receptores[] = {correoTecnico};
//        String receptores[] = {"edu@iact.ugr-csic.es"};
        try {

            postMail(receptores, titulo, mensaje);

        } catch (MessagingException ex) {
            ex.printStackTrace();
        }

    }

    public void enviarMensajeEntregaResultados() {
        if (ordenTrabajoLab.getSolicitante() == null) {
            return;
        }

        if (ordenTrabajoLab.getSolicitante().getContratoActual() == null) {
            return;
        }

        String correoSolicitante = ordenTrabajoLab.getSolicitante().getContratoActual().getEMail();

        if (correoSolicitante == null || correoSolicitante.isEmpty()) {
            return;
        }

        String nombreOrden = getOrdenTrabajoLab().getNumordentrabajo();

        if (getOrdenTrabajoLab().getNombre() != null && !getOrdenTrabajoLab().getNombre().isEmpty()) {
            nombreOrden += " (" + getOrdenTrabajoLab().getNombre() + ")";
        }

        String titulo = "[" + getTecnica().getReferencia() + "] Entrega de Resultados para " + nombreOrden;
        String mensaje = "El Servicio de " + getTecnica().getNombre() + " ha terminado el Estudio para la Orden de Apoyo Técnico " + nombreOrden + "\r\n\r\n";
//        mensaje+="Podrá ver la ficha de la Orden de Apoyo Técnico y los Resultados subidos por el técnico en: "+"\r\n";
//        mensaje+="https://intranet.iact.csic.es:8081/iact-adm/services/"+getUrlName()+"/verordentrabajo.xhtml?id="+getOrdenTrabajoLab().getId()+"\r\n\r\n";

        mensaje += "Por favor, no olvide FIRMAR LA ENTREGA. Si no se firma, se procederá a hacerse de forma automática pasados 15 días después de haber recibido este mensaje." + "\r\n\r\n";
        mensaje += "Reciba un cordial Saludo" + "\r\n\r\n";

        String receptores[] = {correoSolicitante};
//        String receptores[] = {"edu@iact.ugr-csic.es"};
        try {
            postMail(receptores, titulo, mensaje);
        } catch (MessagingException ex) {
            Logger.getLogger(LaboratorioController.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    public void enviarMensajeSolicitudMuestras() {
        if (ordenTrabajoLab.getSolicitante() == null) {
            return;
        }

        if (ordenTrabajoLab.getSolicitante().getContratoActual() == null) {
            return;
        }

        String correoSolicitante = ordenTrabajoLab.getSolicitante().getContratoActual().getEMail();

        if (correoSolicitante == null || correoSolicitante.isEmpty()) {
            return;
        }

        String nombreOrden = getOrdenTrabajoLab().getNumordentrabajo();

        if (getOrdenTrabajoLab().getNombre() != null && !getOrdenTrabajoLab().getNombre().isEmpty()) {
            nombreOrden += " (" + getOrdenTrabajoLab().getNombre() + ")";
        }

        String titulo = "[" + getTecnica().getReferencia() + "] Solicitud de muestras para " + nombreOrden;
        String mensaje = "El Servicio de " + getTecnica().getNombre() + " solicita las muestras para " + nombreOrden + "\r\n\r\n";
//        mensaje+="Podrá ver la ficha de la Orden de Apoyo Técnico y los Resultados subidos por el técnico en: "+"\r\n";
//        mensaje+="https://intranet.iact.csic.es:8081/iact-adm/services/"+getUrlName()+"/verordentrabajo.xhtml?id="+getOrdenTrabajoLab().getId()+"\r\n\r\n";

//        mensaje+="Por favor, no olvide FIRMAR LA ENTREGA. Si no se firma, se procederá a hacerse de forma automática pasados 15 días después de haber recibido este mensaje."+"\r\n\r\n";
//        mensaje+="Reciba un cordial Saludo"+"\r\n\r\n";
        mensaje += "Reciba un cordial Saludo" + "\r\n\r\n";

        String receptores[] = {correoSolicitante};
//        String receptores[] = {"edu@iact.ugr-csic.es"};
        try {
            postMail(receptores, titulo, mensaje);
        } catch (MessagingException ex) {
            Logger.getLogger(LaboratorioController.class.getName()).log(Level.SEVERE, null, ex);
        }



    }

    protected void postMail(String recipients[], String subject, String message) throws MessagingException {
//        boolean debug = false;

        //Set the host smtp address
//        Properties props = new Properties();
//        props.put("mail.smtp.host", "smtpin.csic.es");
//        props.put("mail.smtp.auth", "true");

//        Authenticator auth = new SMTPAuthenticator("iact_avisos", "acd136Mb9");
//        Session session = Session.getInstance(props, auth);
//        session.setDebug(debug);

        // create a message
        Message msg = new MimeMessage(getMailSession());

        // set the from and to address
        InternetAddress addressFrom = new InternetAddress("iact_avisos@iact.ugr-csic.es");
        msg.setFrom(addressFrom);

        InternetAddress[] addressTo = new InternetAddress[recipients.length];
        for (int i = 0; i < recipients.length; i++) {
            addressTo[i] = new InternetAddress(recipients[i]);
        }
        
//        addressTo[0] = new InternetAddress("edu@iact.ugr-csic.es");
        msg.setRecipients(Message.RecipientType.TO, addressTo);

        // Optional : You can also set your custom headers in the Email if you Want
//        msg.addHeader("charset", "UTF-8");

        // Setting the Subject and Content Type
        msg.setSubject(subject);
        msg.setText(message);
        Transport.send(msg);
    }
}
