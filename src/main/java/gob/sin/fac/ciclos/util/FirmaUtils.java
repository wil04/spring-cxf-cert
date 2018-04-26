package gob.sin.fac.ciclos.util;

import gob.sin.fac.ciclos.xml.FirmaXml;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.content.KeyName;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Sergio Criales
 */
public class FirmaUtils {

    private FirmaUtils() {
    }

    public static String verificarFirmaDigital(String mensajeXml) throws Exception {
        Document document = null;

        try {
            document = XmlUtils.getDomFromString(mensajeXml);
        } catch (ParserConfigurationException | SAXException | IOException e) {
        	e.printStackTrace();
            // En caso de que no se pueda transformar el mensaje xml a document
            String codError = "9001";
            String descError = "Servicio no pudo procesar mensaje, revise la estructura xml";
            throw new Exception(codError + " " + descError);
        }
        // En caso de que no se pueda transformar el mensaje xml a document

        FirmaXml firmaXmlApache = FirmaXml.Factory.getFirmaXmlApache();
        XMLSignature digitalSignature = null;

        try {
            digitalSignature = firmaXmlApache.extractDigitalSignature(document);
        } catch (Exception e) {
            // En caso de que no se pueda extraer la firma digital del document
            String codError = "9002";
            String descError = "Servicio no pudo procesar mensaje, no se pudo obtener firma digital del mensaje";
            throw new Exception(codError + " " + descError);
        }

        KeyInfo keyInfo = digitalSignature.getKeyInfo();

        KeyName itemKeyName = null;

        try {
            itemKeyName = keyInfo.itemKeyName(0);
        } catch (XMLSecurityException e) {
            // Este error se presenta si no se puede obtener el item que contiene el
            // keyName para hacer la verificación de firma digital
            String codError = "9003";
            String descError = "Servicio no pudo procesar mensaje, no se pudo obtener elemento keyName";
            throw new Exception(codError + " " + descError);
        }

        String keyName = itemKeyName.getKeyName();

        // Verificar el mensaje con la llave pública del solicitante
        boolean checkSignatureValue = false;

        try {
            checkSignatureValue = digitalSignature.checkSignatureValue(firmaXmlApache.getPublicKey(keyName));
        } catch (XMLSignatureException e) {
            String codError = "9005";
            String descError = "No se pudo realizar la verificación de firma digital";
            throw new Exception(codError + " " + descError);
        }

        if (!checkSignatureValue) {
            String codError = "9006";
            String descError = "Firma Digital no válida";
            throw new Exception(codError + " " + descError);
        }

        // Obtener el contenido firmado luego de la verificación
        ObjectContainer objectItem = digitalSignature.getObjectItem(0);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = null;

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // Este error no debería ocurrir
            String codError = "9007";
            String descError = "Servicio no pudo procesar mensaje";
            throw new Exception(codError + " " + descError);
        }

        Document objectDocument = db.newDocument();
        objectDocument.setXmlStandalone(false);
        Node node = objectDocument.importNode(objectItem.getElement().getFirstChild(), true);
        objectDocument.appendChild(node);

        String objetoFirmado = null;

        try {
            objetoFirmado = XmlUtils.getStringFromDom(objectDocument);
        } catch (TransformerException e) {
            // Este error en caso de que el object del mensaje no pueda convertirse a
            // cadena, no debería ocurrir
            String codError = "9007";
            String descError = "Servicio no pudo procesar mensaje";
            throw new Exception(codError + " " + descError);
        }

        return objetoFirmado;
    }

    public static String firmarMensaje(String string, String keyName)
            throws Exception {
    	Document document = documentFromString(string);
        Document signEnveloping = firmarDocumento(document, keyName);
        String mensajeFirmado = stringFromDocument(signEnveloping);

        return mensajeFirmado;
    }

    public static Document firmarDocumento(Document document, String keyName) throws Exception {
        FirmaXml firmaXml = FirmaXml.Factory.getFirmaXmlApache();

        try {
            Document signEnveloping = firmaXml.signEnveloping(document, keyName);

            return signEnveloping;
        } catch (Exception e) {
        	e.printStackTrace();
            // En caso de que no se pueda firmar el mensaje xml
            String codError = "9008";
            String descError = "No se pudo realizar la firma digital";
            throw new Exception(codError + " " + descError);
        }
    }

    public static String stringFromDocument(Document document) throws Exception {
        try {
            String stringFromDom = XmlUtils.getStringFromDom(document);

            return stringFromDom;
        } catch (TransformerException e) {
            // Este error en caso de que el document no pueda convertirse a cadena, no
            // debería ocurrir
            System.out.println("Error de parseo de elemento object a cadena" + e);

            throw new Exception();
        }
    }
    
    public static Document documentFromString(String string) throws Exception {
        try {
            Document domFromString = XmlUtils.getDomFromString(string);

            return domFromString;
        } catch (ParserConfigurationException e) {
            // Este error en caso de que la cadena no pueda convertirse a documento, no
            // debería ocurrir
            System.out.println("Error de parseo de cadena a elemento object" + e);

            throw new Exception();
        }
    }
    
    public static void main(String[] args) {
        try {
            String string = "<inicioCicloTO>\n" +
            		"    <codigoActividadEconomica>20</codigoActividadEconomica>\n" +
            		"    <codigoCaracteristica>CARAC</codigoCaracteristica>\n" +
            		"    <nit>102045781</nit>\n" +
            		"    <nitEmisor>104058742</nitEmisor>\n" +
            		"    <paralela>1</paralela>\n" +
            		"    <sucursal>2</sucursal>\n" +
            		"    <tipoAutorizacion>AUTO</tipoAutorizacion>\n" +
            		"    <usuario>usuario</usuario>\n" +
            		"</inicioCicloTO>";
            String firmado = firmarMensaje(string, "almacen");
            System.out.println("Documento firmado: " + firmado);
            
            String verificado = verificarFirmaDigital("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><ds:Signature xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
            		"<ds:SignedInfo>\n" +
            		"<ds:CanonicalizationMethod Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315\"/>\n" +
            		"<ds:SignatureMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#rsa-sha1\"/>\n" +
            		"<ds:Reference URI=\"#SignedData\">\n" +
            		"<ds:Transforms>\n" +
            		"<ds:Transform Algorithm=\"http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments\"/>\n" +
            		"</ds:Transforms>\n" +
            		"<ds:DigestMethod Algorithm=\"http://www.w3.org/2000/09/xmldsig#sha1\"/>\n" +
            		"<ds:DigestValue>3Y9o/YenqqnPWmh8RKsOu9m0qMU=</ds:DigestValue>\n" +
            		"</ds:Reference>\n" +
            		"</ds:SignedInfo>\n" +
            		"<ds:SignatureValue>\n" +
            		"L1shdN7+Sl4ZMKyicnhe6tfXyxOFeMF0PihqQGWCYUlC6zu86aPA3Es8BVhD2mjdzwOHblkgEOBr\n" +
            		"j/1gbuNqZQUxQNqtZ26f8sAovsp/oCYl8j3tSmb3Wk1xuwkdG1bPWz+QixVL+rTjgMGk3OVmz3ro\n" +
            		"NhTJRBLPIei6fj8qpDHHRqKvNZ1/HFT/AAcyB7s42Ki/FB9YH934v49KK5I2CDMN/LRZD88/lr/l\n" +
            		"Qg9Nlldufuvdd3GrEYFoyH8z4/RYCbx7MS/AAYLog+RPjTadOTEPfL00tfxHtjgrUReFBFXGA+ji\n" +
            		"yh42UYEHUgIhGe5bltGbeHQzXRVMgj03+o4sJA==\n" +
            		"</ds:SignatureValue>\n" +
            		"<ds:KeyInfo xmlns:ds=\"http://www.w3.org/2000/09/xmldsig#\">\n" +
            		"<ds:KeyName>AGETIC</ds:KeyName>\n" +
            		"</ds:KeyInfo>\n" +
            		"<ds:Object Id=\"SignedData\"><inicioCicloTO>\n" +
            		"    <codigoActividadEconomica>20</codigoActividadEconomica>\n" +
            		"    <codigoCaracteristica>CARAC</codigoCaracteristica>\n" +
            		"    <nit>102045781</nit>\n" +
            		"    <nitEmisor>104058742</nitEmisor>\n" +
            		"    <paralela>1</paralela>\n" +
            		"    <sucursal>2</sucursal>\n" +
            		"    <tipoAutorizacion>AUTO</tipoAutorizacion>\n" +
            		"    <usuario>usuario</usuario>\n" +
            		"</inicioCicloTO></ds:Object>\n" +
            		"</ds:Signature>");
            System.out.println("Verificado: " + verificado);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FirmaUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(FirmaUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FirmaUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FirmaUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
