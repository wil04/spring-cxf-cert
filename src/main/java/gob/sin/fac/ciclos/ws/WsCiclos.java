package gob.sin.fac.ciclos.ws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 *
 * @author Sergio Criales
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT)
public interface WsCiclos {
    @WebMethod
    String iniciarCiclo(String mensaje);
}