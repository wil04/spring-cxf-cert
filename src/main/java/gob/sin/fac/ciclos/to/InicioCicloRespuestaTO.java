package gob.sin.fac.ciclos.to;

import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Sergio Criales
 */
@XmlRootElement
public class InicioCicloRespuestaTO {
    private String eticket;
    private List<String> mensajes;

    public String getEticket() {
        return eticket;
    }

    public void setEticket(String eticket) {
        this.eticket = eticket;
    }

    public List<String> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<String> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public String toString() {
        return "InicioCicloRespuestaTO{" + "eticket=" + eticket + ", mensajes=" + mensajes + '}';
    }
}
