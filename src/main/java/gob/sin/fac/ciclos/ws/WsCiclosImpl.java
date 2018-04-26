package gob.sin.fac.ciclos.ws;

import gob.sin.fac.ciclos.bl.CiclosBean;
import gob.sin.fac.ciclos.bl.CiclosLocal;
import gob.sin.fac.ciclos.to.InicioCicloRespuestaTO;
import gob.sin.fac.ciclos.to.InicioCicloTO;
import gob.sin.fac.ciclos.util.FirmaUtils;
import gob.sin.fac.ciclos.util.JaxbUtils;
import java.util.Arrays;

/**
 *
 * @author Sergio Criales
 */
public class WsCiclosImpl implements WsCiclos {

    @Override
    public String iniciarCiclo(String mensaje) {
    	String respuesta = "";
    	
        try {
            // Verificar la firma digital
            String xml = FirmaUtils.verificarFirmaDigital(mensaje);
            // Transformar el XML en objeto
            InicioCicloTO inicioCicloTO = JaxbUtils.unmarshall(xml, InicioCicloTO.class);
            // Enviar el objeto al negocio
            CiclosLocal ciclosLocal = new CiclosBean();
            InicioCicloRespuestaTO inicioCicloRespuestaTO = ciclosLocal.iniciarCiclo(inicioCicloTO);
            // Transformar el objeto a XML
            respuesta = JaxbUtils.marshall(inicioCicloRespuestaTO);
        } catch (Exception ex) {
            InicioCicloRespuestaTO inicioCicloRespuestaTO = new InicioCicloRespuestaTO();
            inicioCicloRespuestaTO.setEticket("");
            inicioCicloRespuestaTO.setMensajes(Arrays.asList(ex.getMessage()));

            try {
                respuesta = JaxbUtils.marshall(inicioCicloRespuestaTO);
            } catch (Exception ex1) {
                // Ignorar la excepcion, no ocurrirá
            }
        }

        try {
			return FirmaUtils.firmarMensaje(respuesta, "agetic");
		} catch (Exception e) {
			return respuesta; // Este caso no debería ocurrir, se envía mensaje sin firma
		}
    }
}
