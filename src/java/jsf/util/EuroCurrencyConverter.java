package jsf.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.*;
import javax.faces.convert.ConverterException;

@FacesConverter(value="EuroCurrencyConverter")
public class EuroCurrencyConverter implements Converter {

    public Object getAsObject(FacesContext context, UIComponent component, String value){
        try{
            return EuroCurrency.getAsLong(value);
        }catch(NumberFormatException nfe){
            throw new ConverterException("Debe introducir una cantidad con formato 12345.67 o 12345,67 o 12345");
        }
    }

    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return EuroCurrency.getAsString(value);
    }
}
