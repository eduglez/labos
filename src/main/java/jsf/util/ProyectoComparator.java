package jsf.util;

import proyectos.modelo.*;
import java.util.Comparator;
import jsf.util.EmpleadoComparator;


public class ProyectoComparator implements Comparator<Proyecto>{

    private boolean ascendente;

    private String columna;

    public ProyectoComparator(boolean ascendente, String columna){
        this.ascendente=ascendente;
        this.columna=columna;
    }


    public int compare(Proyecto o1, Proyecto o2) {
        if(columna==null)
            return 0;
        if(columna.equals("id")){

            if(ascendente){
                return Integer.parseInt(o1.getId())-Integer.parseInt(o2.getId());
            }else{
                return Integer.parseInt(o2.getId())-Integer.parseInt(o1.getId());
            }
        }else if(columna.equals("responsable")){
            if(o1.getResponsable()==null)
                return Integer.MAX_VALUE;

            if(o2.getResponsable()==null)
                return Integer.MIN_VALUE;
            
            EmpleadoComparator ec=new EmpleadoComparator();
            ec.setAscendente(ascendente);
            ec.setCampoOrden(EmpleadoComparator.POR_APELLIDO);

            return ec.compare(o1.getResponsable().getEmpleadoIACT(), o2.getResponsable().getEmpleadoIACT());

        }else if(columna.equals("referencia")){
            if(o1.getReferencia()==null)
                return Integer.MAX_VALUE;

            if(o2.getReferencia()==null)
                return Integer.MIN_VALUE;

            if(ascendente){
                return o1.getReferencia().compareTo(o2.getReferencia());
            }else{
                return o2.getReferencia().compareTo(o1.getReferencia());
            }
        }else if(columna.equals("tipoproyecto")){
            if(o1.getTipoProyecto()==null)
                return Integer.MAX_VALUE;

            if(o2.getTipoProyecto()==null)
                return Integer.MIN_VALUE;


            if(ascendente){
                return o1.getTipoProyecto().getNombre().compareTo(o2.getTipoProyecto().getNombre());
            }else{
                return o2.getTipoProyecto().getNombre().compareTo(o1.getTipoProyecto().getNombre());
            }
        }else if(columna.equals("estadoproyecto")){
            if(o1.getEstadoProyecto()==null)
                return Integer.MAX_VALUE;

            if(o2.getEstadoProyecto()==null)
                return Integer.MIN_VALUE;

            if(ascendente){
                return o1.getEstadoProyecto().getNombre().compareTo(o2.getEstadoProyecto().getNombre());
            }else{
                return o2.getEstadoProyecto().getNombre().compareTo(o1.getEstadoProyecto().getNombre());
            }
        }else if(columna.equals("fechaAceptacion")){
            if(o1.getFechaAceptacion()==null)
                return Integer.MAX_VALUE;

            if(o2.getFechaAceptacion()==null)
                return Integer.MIN_VALUE;

            if(ascendente){
                return o1.getFechaAceptacion().compareTo(o2.getFechaAceptacion());
            }else{
                return o2.getFechaAceptacion().compareTo(o1.getFechaAceptacion());
            }
        }else if(columna.equals("fechaSolicitud")){
            if(o1.getFechaSolicitud()==null)
                return Integer.MAX_VALUE;

            if(o2.getFechaSolicitud()==null)
                return Integer.MIN_VALUE;

            if(ascendente){
                return o1.getFechaSolicitud().compareTo(o2.getFechaSolicitud());
            }else{
                return o2.getFechaSolicitud().compareTo(o1.getFechaSolicitud());
            }
        }else if(columna.equals("fechaInicio")){
            if(o1.getFechaInicio()==null)
                return Integer.MAX_VALUE;

            if(o2.getFechaInicio()==null)
                return Integer.MIN_VALUE;

            if(ascendente){
                return o1.getFechaInicio().compareTo(o2.getFechaInicio());
            }else{
                return o2.getFechaInicio().compareTo(o1.getFechaInicio());
            }
        }else
            return 0;
    }

}
