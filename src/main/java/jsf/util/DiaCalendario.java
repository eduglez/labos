package jsf.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import laboratorios.modelo.Reserva;


public class DiaCalendario {
    private Date fecha;

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public DiaCalendario(Date fecha, int mesActual, List<Reserva> reservas){
        this.fecha=fecha;
        this.mesActual=mesActual;
        this.reservas=reservas;
    }

    public DiaCalendario(){
        
    }

    private int mesActual;
    
    public boolean isMesActual(){
        GregorianCalendar gc = new GregorianCalendar();
        gc.setTime(fecha);

        return gc.get(Calendar.MONTH)==mesActual;
    }

    private List<Reserva> reservas;

    public List<Reserva> getReservas() {
        return reservas;
    }

    public void setReservas(List<Reserva> reservas) {
        this.reservas = reservas;
    }


    public boolean isHoy(){
        GregorianCalendar gcHoy = new GregorianCalendar();
        GregorianCalendar gcDia = new GregorianCalendar();
        gcDia.setTime(fecha);

        return gcHoy.get(Calendar.YEAR)==gcDia.get(Calendar.YEAR)
                &&gcHoy.get(Calendar.MONTH)==gcDia.get(Calendar.MONTH)
                &&gcHoy.get(Calendar.DAY_OF_MONTH)==gcDia.get(Calendar.DAY_OF_MONTH);
    }
}
