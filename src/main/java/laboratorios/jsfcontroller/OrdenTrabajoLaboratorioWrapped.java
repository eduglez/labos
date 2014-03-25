package laboratorios.jsfcontroller;

import jsf.util.ResultadosResource;
import laboratorios.modelo.DocumentoResultados;
import laboratorios.modelo.OrdenTrabajoLaboratorio;


public class OrdenTrabajoLaboratorioWrapped {

    private OrdenTrabajoLaboratorio orden;

    public OrdenTrabajoLaboratorioWrapped(OrdenTrabajoLaboratorio orden){
        this.orden=orden;
    }

    public OrdenTrabajoLaboratorio getOrdenTrabajo(){
        return orden;
    }

    public ResultadosResource getPrimerResultado(){
        DocumentoResultados dr = orden.getPrimerDocumentoResultados();
        if(dr!=null)
            return new ResultadosResource(dr);
        else
            return null;


    }
}
