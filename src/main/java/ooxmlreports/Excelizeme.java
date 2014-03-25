package ooxmlreports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import laboratorios.modelo.OrdenTrabajo;
import laboratorios.modelo.OrdenTrabajoLaboratorio;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;



public class Excelizeme {

    public static ByteArrayOutputStream getSuperExcel(List<OrdenTrabajoLaboratorio> ordenes) throws IOException{

            XSSFWorkbook w = new XSSFWorkbook(Excelizeme.class.getResource("/reports/Statics.xlsx").getPath());

            XSSFCellStyle cStyle=w.createCellStyle();

            XSSFDataFormat xdf=w.createDataFormat();

            cStyle.setDataFormat(xdf.getFormat("dd/mm/yyyy h:mm;@"));


            XSSFSheet s=w.getSheet("RAW");

            if(s==null)
                return null;

            for(OrdenTrabajoLaboratorio otl: ordenes){
                if(otl.getEstado().equals(OrdenTrabajo.RELLENANDO))
                    continue;

                XSSFRow r=s.createRow(s.getLastRowNum()+1);

                if(s==null)
                    continue;

                Cell c=r.createCell(0);

                c.setCellValue(otl.getNumordentrabajo());


                c=r.createCell(1);
                if(otl.getFechasolicitud()!=null){
                    c.setCellValue(otl.getFechasolicitud());
                    c.setCellStyle(cStyle);
                }else c.setCellValue("");



                c=r.createCell(2);
                if(otl.getFechaentregaresultados()!=null){
                    c.setCellValue(otl.getFechaentregaresultados());
                    c.setCellStyle(cStyle);
                }else c.setCellValue("");

                c=r.createCell(3);
                c.setCellValue(otl.getTecnica().getReferencia());


                c=r.createCell(4);
                if(otl.getSolicitante()!=null)
                   c.setCellValue(otl.getSolicitante().getDni());
                else c.setCellValue("");

                

                c=r.createCell(5);
                if(otl.getProyecto()!=null)
                   c.setCellValue(otl.getProyecto().getReferencia());
                else c.setCellValue("");

                c=r.createCell(6);
                c.setCellValue(otl.getEstado());

                c=r.createCell(7);
                c.setCellValue(otl.getNumTrabajos());
//                c.setCellValue(1);
                c=r.createCell(8);
                c.setCellValue((double)otl.getTotalTrabajos()/1000);
                

            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            w.write(baos);

            baos.close();

            return baos;


    }


}
