/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gob.sin.fac.ciclos.util;

import gob.sin.fac.ciclos.to.InicioCicloRespuestaTO;
import gob.sin.fac.ciclos.to.InicioCicloTO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigInteger;
import java.util.Arrays;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Sergio Criales
 */
public class JaxbUtils {

    private JaxbUtils() {
    }

    public static <T> String marshall(T object) throws Exception {
        if (null == object) {
            throw new Exception("Objeto a transformar es nulo");
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);

            Writer writer = new StringWriter();

            marshaller.marshal(object, writer);

            return writer.toString();
        } catch (JAXBException e) {
            throw new Exception("Error en la transformación de objeto a representación xml");
        }
    }

    public static <T> T unmarshall(String xmlDocument, Class<T> clazz)
            throws Exception {
        if (null == xmlDocument || null == clazz) {
            throw new Exception("Parámetros no deben ser nulos");
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            InputStream inputStream = new ByteArrayInputStream(xmlDocument.getBytes());

            T object = (T) unmarshaller.unmarshal(inputStream);

            return object;
        } catch (JAXBException e) {
            throw new Exception("Error en la transformación de mensaje xml a objetos");
        }
    }

    public static void main(String[] args) throws Exception {
        InicioCicloTO inicioCicloTO = new InicioCicloTO();
        inicioCicloTO.setNit(new BigInteger("102045781"));
        inicioCicloTO.setNitEmisor(new BigInteger("104058742"));
        inicioCicloTO.setCodigoActividadEconomica(new BigInteger("20"));
        inicioCicloTO.setCodigoCaracteristica("CARAC");
        inicioCicloTO.setParalela(1L);
        inicioCicloTO.setSucursal(2L);
        inicioCicloTO.setTipoAutorizacion("AUTO");
        inicioCicloTO.setUsuario("usuario");

        String xml = marshall(inicioCicloTO);
        System.out.println("XML: " + xml);
        
        InicioCicloTO objeto = unmarshall(xml, InicioCicloTO.class);
        System.out.println("OBJETO: " + objeto);
    }
}
