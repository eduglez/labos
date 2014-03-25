package jsf.util;

import com.icesoft.faces.context.FileResource;
import com.icesoft.faces.context.Resource.Options;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import laboratorios.modelo.DocumentoResultados;


public class ResultadosResource extends FileResource{

    private String nombreFichero;

    public String getNombreFichero() {
        return nombreFichero;
    }

    public void setNombreFichero(String nombreFichero) {
        this.nombreFichero = nombreFichero;
    }

    
    public ResultadosResource(File fichero, String nombreFichero){
        super(fichero);
        this.nombreFichero=nombreFichero;
    }

    public ResultadosResource(DocumentoResultados doc){
        super(doc.getFile());
        this.nombreFichero = doc.getNombreDocumento();
        nombreFichero = doc.getOrdenTrabajoLaboratorio().getNumordentrabajo().replace('/', '-');
    }

    public String getCreadoHace() {
        File fichero = getFile();
        Date ahora = new Date();
        long diferencia = ahora.getTime() - fichero.lastModified();

        int cantidad;
        if ((diferencia - 86400000) >= 0) {
            cantidad = (int) ((float) diferencia / 86400000);
            if (cantidad == 1) {
                return "1 día";
            } else {
                return cantidad + " días";
            }

        } else if ((diferencia - 3600000) >= 0) {
            cantidad = (int) ((float) diferencia / 3600000);
            if (cantidad == 1) {
                return "1 hora";
            } else {
                return cantidad + " horas";
            }
        } else if ((diferencia - 60000) >= 0) {
            cantidad = (int) ((float) diferencia / 60000);
            if (cantidad == 1) {
                return "1 minuto";
            } else {
                return cantidad + " minutos";
            }
        } else {
            cantidad = (int) ((float) diferencia / 1000);
            if (cantidad == 1) {
                return "1 segundo";
            } else {
                return cantidad + " segundos";
            }
        }

    }

    public Date getFechaFichero() {
        File f = getFile();
        return f != null ? new Date(f.lastModified()) : null;
    }
    
    @Override
    public void withOptions(Options options) throws IOException {
        super.withOptions(options);

        String ext=getFile().getName().substring(getFile().getName().lastIndexOf(".")+1);
        options.setFileName(nombreFichero+"."+ext);

        if(ext.equalsIgnoreCase("docx"))
            options.setMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        else if(ext.equalsIgnoreCase("xlsx"))
            options.setMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    }



}
