package laboratorios.jsfcontroller;

import javax.ejb.EJB;
import laboratorios.modelo.PrioridadServicioInformatica;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import laboratorios.facade.PrioridadServicioInformaticaFacade;

@FacesConverter(forClass=PrioridadServicioInformatica.class)
public class PrioridadServicioInformaticaConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String string) {
        PrioridadServicioInformaticaFacade jpaFacade=null;
        try{
            InitialContext initialContext = new InitialContext();
            jpaFacade= (PrioridadServicioInformaticaFacade) initialContext.lookup("java:global/IACT-ADM_UI/PrioridadServicioInformaticaFacade");
        } catch (NamingException e) {
            e.printStackTrace();
            return null;
        }

        if (string == null || string.length() == 0) {
            return null;
        }
        Long id=Long.parseLong(string);
        return jpaFacade.find(id);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof PrioridadServicioInformatica) {
            PrioridadServicioInformatica o = (PrioridadServicioInformatica) object;
            return o.getId() == null ? "" : o.getId().toString();
        } else {
            throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: laboratorios.modelo.PrioridadServicioInformatica");
        }
    }

}
