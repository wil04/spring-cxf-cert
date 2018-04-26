package gob.sin.fac.ciclos.xml;

import gob.sin.fac.ciclos.seguridad.AlmacenLlaves;
import gob.sin.fac.ciclos.util.XmlUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.AlgorithmConstraints;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Date;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.signature.ObjectContainer;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xml.security.utils.Constants;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Sergio Criales
 */
public class FirmaXmlApache implements FirmaXml {

    private static final Log log = LogFactory.getLog(FirmaXmlApache.class);

    static {
        org.apache.xml.security.Init.init();
    }

    private FirmaXmlApache() {
    }

    public static FirmaXml getFirmaXmlApache() {
        log.info("Nuevo firmador creado");
        return new FirmaXmlApache();
    }

    @Override
    public XMLSignature extractDigitalSignature(Document doc) throws Exception {
        XMLSignature signature = null;

        try {
            Element nscontext = XmlUtils.createDSctx(doc, "ds", Constants.SignatureSpecNS);
            Element sigElement;
            sigElement = (Element) XPathAPI.selectSingleNode(doc, "//ds:Signature[1]", nscontext);
            signature = new XMLSignature(sigElement, null);
        } catch (TransformerException e) {
            e.printStackTrace();
            throw new Exception(e);
        } catch (XMLSignatureException e) {
            e.printStackTrace();
            throw new Exception(e);
        } catch (XMLSecurityException e) {
            e.printStackTrace();
            throw new Exception(e);
        }

        return signature;
    }

    @Override
    public PublicKey getPublicKey(String alias) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
    	// TODO: Cambiar a una fábrica q permite recuperar de configuración
    	AlmacenLlaves almacenLlaves = new AlmacenLlaves();
    	almacenLlaves.setTipo("JKS");
    	almacenLlaves.setArchivo("/home/sergio/Desarrollo/workspace-mars/spring-cxf-cert/src/main/resources/almacen.jks");
    	almacenLlaves.setPassAlmacen("12345678");

        KeyStore keyStore = KeyStore.getInstance(almacenLlaves.getTipo());
        FileInputStream fis = new FileInputStream(almacenLlaves.getArchivo());
        keyStore.load(fis, almacenLlaves.getPassAlmacen().toCharArray());
        
        Certificate certificate = keyStore.getCertificate(alias);
        
        if (null == certificate) {
        	throw new IOException("No existe certificado con el alias proporcionado: " + alias);
        }
        
        return certificate.getPublicKey();
    }

    @Override
    public Document signEnveloping(Document xmlDoc, String keyName) throws Exception {
        //Constants.setSignatureSpecNSprefix("ds");
    	ElementProxy.setDefaultPrefix(Constants.SignatureSpecNS, "ds");

        // Crear el nuevo documento contenedor de la firma
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.newDocument();
        document.setXmlStandalone(false);

        // Crear una objeto XMLSignature del documento, algoritmo RSA
        XMLSignature xmlSignature = new XMLSignature(document, null, XMLSignature.ALGO_ID_SIGNATURE_RSA);

        // Adicionar la firma al documento final
        document.appendChild(xmlSignature.getElement());

        // Obtener el nodo duplicado del documento a firmar
        NodeList list = xmlDoc.getChildNodes();
        Element elementMsg = (Element) list.item(0);
        Node nodoDuplicado = document.importNode(elementMsg, true);

        // Crear un ObjectContainer que contiene el documento original
        ObjectContainer objectContainer = new ObjectContainer(document);
        objectContainer.appendChild(nodoDuplicado);
        objectContainer.setId("SignedData");
        xmlSignature.appendObject(objectContainer);

        // Crear los transformadores para el documento final
        Transforms transforms = new Transforms(document);
        transforms.addTransform(Transforms.TRANSFORM_C14N_WITH_COMMENTS);

        // Adicionar los transformadores al documento final
        xmlSignature.addDocument("#" + "SignedData", transforms, Constants.ALGO_ID_DIGEST_SHA1);

        // Obtener la llave privada para firmar.
        PrivateKey privateKey = getPrivateKey();

        // Adicionar KeyName al KeyInfo
        xmlSignature.getKeyInfo().addKeyName(keyName);

        // Proceder con la firma digital
        log.info(new Date().toString() + " Firmando documento ...");
        xmlSignature.sign(privateKey);
        log.info(new Date().toString() + " Firma completada");

        return document;
    }

    @Override
    public PrivateKey getPrivateKey() throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException {
    	// TODO: Cambiar a una fábrica q permite recuperar de configuración
    	AlmacenLlaves almacenLlaves = new AlmacenLlaves();
    	almacenLlaves.setTipo("JKS");
    	almacenLlaves.setArchivo("/home/sergio/Desarrollo/workspace-mars/spring-cxf-cert/src/main/resources/almacen.jks");
    	almacenLlaves.setPassAlmacen("12345678");
    	almacenLlaves.setAliasLlavePrivada("almacen");
    	almacenLlaves.setPassLlavePrivada("123456");
    	
        KeyStore keyStore = KeyStore.getInstance(almacenLlaves.getTipo());
        FileInputStream fis = new FileInputStream(almacenLlaves.getArchivo());
        keyStore.load(fis, almacenLlaves.getPassAlmacen().toCharArray());

        Key key = keyStore.getKey(almacenLlaves.getAliasLlavePrivada(), almacenLlaves.getPassLlavePrivada().toCharArray());
        return (PrivateKey) key;
    }
}
