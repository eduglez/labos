package laboratorios.jsfcontroller;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.faces.bean.*;
import javax.mail.Session;
import laboratorios.facade.DocumentoResultadosFacade;
import laboratorios.facade.OrdenTrabajoLaboratorioFacade;
import laboratorios.facade.PermisoEmpleadoFacade;
import laboratorios.facade.TecnicaFacade;
import laboratorios.facade.TrabajoFacade;
import laboratorios.facade.TrabajoOrdenFacade;
import personal.facade.EmpleadoFacade;
import proyectos.facade.ProyectoFacade;

@ManagedBean(name="pregeo")
@SessionScoped
public class PREGEOController extends LaboratorioController{
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
        setTecnica(tecnicaFacade.findByNombre("Preparación de muestras para análisis geoquímico"));
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
  
}
