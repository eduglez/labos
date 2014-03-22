package laboratorios.jsfcontroller;

import personal.modelo.Empleado;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.*;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import personal.facade.EmpleadoFacade;

@FacesConverter(forClass=Empleado.class)
public class EmpleadoConverter implements Converter {
   
    @Override
    public Object getAsObject(FacesContext facesContext, UIComponent component, String string) {
        EmpleadoFacade jpaFacade=null;
        try{
            InitialContext initialContext = new InitialContext();
            jpaFacade= (EmpleadoFacade) initialContext.lookup("java:global/IACT-ADM_UI/EmpleadoFacade!personal.facade.EmpleadoFacade");
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
        if (object instanceof Empleado) {
            Empleado o = (Empleado) object;
            return o.getId().toString();
        } else {
            throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: Empleado");
        }
    }

}
