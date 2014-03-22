package laboratorios.jsfcontroller;

import javax.ejb.EJB;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import proyectos.modelo.Proyecto;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import proyectos.facade.ProyectoFacade;

@FacesConverter(forClass=Proyecto.class)
public class ProyectoConverter implements Converter {
   
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String string) {
        ProyectoFacade jpaFacade=null;
        try{
            InitialContext initialContext = new InitialContext();
            jpaFacade= (ProyectoFacade) initialContext.lookup("java:global/IACT-ADM_UI/ProyectoFacade!proyectos.facade.ProyectoFacade");
        } catch (NamingException e) {
            e.printStackTrace();
            return null;
        }

        if (string == null || string.length() == 0) {
            return null;
        }
        String id=string;
        return jpaFacade.find(id);
    }

    @Override
    public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof Proyecto) {
            Proyecto o = (Proyecto) object;
            return o.getId() == null ? "" : o.getId();
        } else {
            throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: Proyecto");
        }
    }

}
