package jsf.util;

import java.text.Collator;
import java.util.Comparator;
import personal.modelo.Empleado;

public class EmpleadoComparator implements Comparator<Empleado> {

    private boolean ascendente;
    private int campoOrden;
    public final static int POR_APELLIDO = 0;
    public final static int POR_NOMBRE = 1;

    public boolean isAscendente() {
        return ascendente;
    }

    public void setAscendente(boolean ascendente) {
        this.ascendente = ascendente;
    }

    public int getCampoOrden() {
        return campoOrden;
    }

    public void setCampoOrden(int campoOrden) {
        this.campoOrden = campoOrden;
    }

    private int compareApellidosEmpleado(Empleado o1, Empleado o2) {
        Collator c = Collator.getInstance();
        if (isAscendente()) {
            if (o1.getApellidos() == null) {
                return Integer.MIN_VALUE;
            } else if (o2.getApellidos() == null) {
                return Integer.MAX_VALUE;
            } else {
                return c.compare(o1.getApellidos(), o2.getApellidos());
            }
        } else {
            if (o1.getApellidos() == null) {
                return Integer.MAX_VALUE;
            } else if (o2.getApellidos() == null) {
                return Integer.MIN_VALUE;
            } else {
                return c.compare(o2.getApellidos(), o1.getApellidos());
            }
        }
    }

    private int compareNombresEmpleado(Empleado o1, Empleado o2) {
        Collator c = Collator.getInstance();
        if (isAscendente()) {
            if (o1.getNombre() == null) {
                return Integer.MIN_VALUE;
            } else if (o2.getNombre() == null) {
                return Integer.MAX_VALUE;
            } else {
                return c.compare(o1.getNombre(), o2.getNombre());
            }
        } else {
            if (o1.getNombre() == null) {
                return Integer.MAX_VALUE;
            } else if (o2.getNombre() == null) {
                return Integer.MIN_VALUE;
            } else {
                return c.compare(o2.getNombre(), o1.getNombre());
            }
        }
    }

    public int compare(Empleado o1, Empleado o2) {

        switch (getCampoOrden()) {
            case POR_APELLIDO:
                return compareApellidosEmpleado(o1, o2);

            case POR_NOMBRE:
                return compareNombresEmpleado(o1, o2);

            default:
                return compareApellidosEmpleado(o1, o2);

        }

    }
}
