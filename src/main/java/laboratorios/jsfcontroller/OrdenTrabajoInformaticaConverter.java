package laboratorios.jsfcontroller;

import javax.ejb.EJB;
import laboratorios.modelo.OrdenTrabajoInformatica;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import laboratorios.facade.OrdenTrabajoInformaticaFacade;

@FacesConverter(forClass=OrdenTrabajoInformatica.class)
public class OrdenTrabajoInformaticaConverter implements Converter {
    
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String string) {
        OrdenTrabajoInformaticaFacade jpaFacade=null;
        try{
            InitialContext initialContext = new InitialContext();
            jpaFacade= (OrdenTrabajoInformaticaFacade) initialContext.lookup("java:global/IACT-ADM_UI/OrdenTrabajoInformaticaFacade");
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
        if (object instanceof OrdenTrabajoInformatica) {
            OrdenTrabajoInformatica o = (OrdenTrabajoInformatica) object;
            return o.getId() == null ? "" : o.getId().toString();
        } else {
            throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: laboratorios.modelo.OrdenTrabajoInformatica");
        }
    }

}
