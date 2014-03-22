package jsf.util;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import laboratorios.facade.ReservaFacade;
import laboratorios.modelo.Reserva;


public class Calendario {
    
    private int anioSeleccionado;
    private int mesSeleccionado;

    HashMap<Integer, Integer> diasSemana;
    private ReservaFacade reservaFacade;


    public Calendario(ReservaFacade reservaFacade){
        this.reservaFacade=reservaFacade;
        
        eventos= new DiaCalendario[49];
        diasSemana=new HashMap<Integer,Integer>();
        diasSemana.put(Calendar.MONDAY, 0);
        diasSemana.put(Calendar.TUESDAY, 1);
        diasSemana.put(Calendar.WEDNESDAY,2);
        diasSemana.put(Calendar.THURSDAY, 3);
        diasSemana.put(Calendar.FRIDAY, 4);
        diasSemana.put(Calendar.SATURDAY, 5);
        diasSemana.put(Calendar.SUNDAY, 6);

        
        GregorianCalendar gc = new GregorianCalendar();
        anioSeleccionado=gc.get(Calendar.YEAR);
        mesSeleccionado=gc.get(Calendar.MONTH);


//        actualizar();
    }


    private DiaCalendario[] eventos;

    public DiaCalendario[] getEventos() {
        return eventos;
    }

    public void setEventos(DiaCalendario[] eventos) {
        this.eventos = eventos;
    }

    
    public void actualizar(){
        GregorianCalendar calendarioMesActual=new GregorianCalendar(anioSeleccionado,mesSeleccionado,1,0,0);

        calendarioMesActual=new GregorianCalendar(anioSeleccionado,mesSeleccionado,1);
        int pivote=diasSemana.get(calendarioMesActual.get(Calendar.DAY_OF_WEEK));
        int mesActual=calendarioMesActual.get(Calendar.MONTH);

        for(int i=pivote-1;i>=0;i--){
            calendarioMesActual.add(Calendar.DAY_OF_YEAR,-1);

            Date dia=calendarioMesActual.getTime();

            List<Reserva> reservasDia=reservaFacade.findReservaEntitiesDia(dia);

            eventos[i]=new DiaCalendario(calendarioMesActual.getTime(), mesActual, reservasDia);

        }

        calendarioMesActual=new GregorianCalendar(anioSeleccionado,mesSeleccionado,1);

        for(int i=pivote;i<eventos.length;i++){
            Date dia=calendarioMesActual.getTime();

            List<Reserva> reservasDia=reservaFacade.findReservaEntitiesDia(dia);

            eventos[i]=new DiaCalendario(calendarioMesActual.getTime(), mesActual, reservasDia);
            
            calendarioMesActual.add(Calendar.DAY_OF_YEAR, 1);


        }

    }

    public String getMesActual(){
        DateFormatSymbols dfs = new DateFormatSymbols();

        return dfs.getMonths()[mesSeleccionado]+" de "+anioSeleccionado;
    }

    public void irMesSiguiente(){
        mesSeleccionado++;
        if(mesSeleccionado==12){
            mesSeleccionado=0;
            anioSeleccionado++;
        }


        actualizar();
    }

    public void irMesAnterior(){
        mesSeleccionado--;
        if(mesSeleccionado==-1){
            mesSeleccionado=11;
            anioSeleccionado--;
        }

        actualizar();
    }

}
