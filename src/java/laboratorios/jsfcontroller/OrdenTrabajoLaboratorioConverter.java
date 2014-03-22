package laboratorios.jsfcontroller;

import javax.ejb.EJB;
import laboratorios.modelo.OrdenTrabajoLaboratorio;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import laboratorios.facade.OrdenTrabajoLaboratorioFacade;

@FacesConverter(forClass=OrdenTrabajoLaboratorio.class)
public class OrdenTrabajoLaboratorioConverter implements Converter {
    
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String string) {
        OrdenTrabajoLaboratorioFacade jpaFacade=null;
        try{
            InitialContext initialContext = new InitialContext();
            jpaFacade= (OrdenTrabajoLaboratorioFacade) initialContext.lookup("java:global/IACT-ADM_UI/OrdenTrabajoLaboratorioFacade");
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
        if (object instanceof OrdenTrabajoLaboratorio) {
            OrdenTrabajoLaboratorio o = (OrdenTrabajoLaboratorio) object;
            return o.getId() == null ? "" : o.getId().toString();
        } else {
            throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: laboratorios.modelo.OrdenTrabajoLaboratorio");
        }
    }

}
