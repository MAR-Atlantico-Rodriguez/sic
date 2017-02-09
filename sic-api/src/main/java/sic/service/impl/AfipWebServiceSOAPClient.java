package sic.service.impl;

import afip.wsaa.wsdl.LoginCms;
import afip.wsaa.wsdl.LoginCmsResponse;
import afip.wsfe.wsdl.FECAESolicitar;
import afip.wsfe.wsdl.FECAESolicitarResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertStore;
import java.security.cert.CertStoreException;
import java.security.cert.CertificateException;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessable;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.oxm.XmlMappingException;
import org.springframework.ws.client.WebServiceClientException;

public class AfipWebServiceSOAPClient extends WebServiceGatewaySupport {

    private final String WSAA_TESTING = "https://wsaahomo.afip.gov.ar/ws/services/LoginCms";
    private final String WSAA_PRODUCTION = "https://wsaa.afip.gov.ar/ws/services/LoginCms";
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
    private final String WSFE_TESTING = "https://wswhomo.afip.gov.ar/wsfev1/service.asmx";
    private final String WSFE_PRODUCTION = "";

    public String loginCMS(LoginCms loginCMS) {
        try {
            LoginCmsResponse response = (LoginCmsResponse) this.getWebServiceTemplate()
                    .marshalSendAndReceive(WSAA_TESTING, loginCMS);
            return response.getLoginCmsReturn();
        } catch (WebServiceClientException | XmlMappingException ex) {
            LOGGER.error(ex.getMessage());
            return ""; // lanzar la exception correspondiente
        }
    }

    public byte[] crearCMS(String p12file, String p12pass, String signer, String service, Long ticketTime) {
        PrivateKey pKey = null;
        X509Certificate pCertificate = null;
        byte[] asn1_cms = null;
        CertStore cstore = null;
        try {
            KeyStore ks = KeyStore.getInstance("pkcs12");
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(p12file);
            ks.load(is, p12pass.toCharArray());
            is.close();
            pKey = (PrivateKey) ks.getKey(signer, p12pass.toCharArray());
            pCertificate = (X509Certificate) ks.getCertificate(signer);
            ArrayList<X509Certificate> certList = new ArrayList<>();
            certList.add(pCertificate);
            if (Security.getProvider("BC") == null) {
                Security.addProvider(new BouncyCastleProvider());
            }
            cstore = CertStore.getInstance("Collection", new CollectionCertStoreParameters(certList), "BC");
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException |
                UnrecoverableKeyException | InvalidAlgorithmParameterException | NoSuchProviderException ex) {
            LOGGER.error(ex.getMessage());
        }
        String loginTicketRequest_xml = this.crearTicketRequerimientoAcceso(service, ticketTime);
        try {
            CMSSignedDataGenerator gen = new CMSSignedDataGenerator();
            gen.addSigner(pKey, pCertificate, CMSSignedDataGenerator.DIGEST_SHA1);
            gen.addCertificatesAndCRLs(cstore);
            CMSProcessable data = new CMSProcessableByteArray(loginTicketRequest_xml.getBytes());
            CMSSignedData signed = gen.generate(data, true, "BC");
            asn1_cms = signed.getEncoded();
        } catch (IllegalArgumentException | CertStoreException | CMSException | NoSuchAlgorithmException | NoSuchProviderException | IOException ex) {
            LOGGER.error(ex.getMessage());
        }
        return asn1_cms;
    }

    private String crearTicketRequerimientoAcceso(String service, Long ticketTime) {
        Date now = new Date();
        GregorianCalendar genenerationTime = new GregorianCalendar();
        GregorianCalendar expirationTime = new GregorianCalendar();
        DatatypeFactory datatypeFactory = null;
        String UniqueId = Long.toString(now.getTime() / 1000);
        expirationTime.setTime(new Date(now.getTime() + ticketTime));
        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException ex) {
            LOGGER.error(ex.getMessage());
        }
        XMLGregorianCalendar XMLGenTime = datatypeFactory.newXMLGregorianCalendar(genenerationTime);
        XMLGregorianCalendar XMLExpTime = datatypeFactory.newXMLGregorianCalendar(expirationTime);
        String LoginTicketRequest_xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<loginTicketRequest version=\"1.0\">"
                + "<header>"
                + "<uniqueId>" + UniqueId + "</uniqueId>"
                + "<generationTime>" + XMLGenTime + "</generationTime>"
                + "<expirationTime>" + XMLExpTime + "</expirationTime>"
                + "</header>"
                + "<service>" + service + "</service>"
                + "</loginTicketRequest>";
        return LoginTicketRequest_xml;
    }

    public void FECAESolicitar(FECAESolicitar solicitud) {
        FECAESolicitarResponse response = (FECAESolicitarResponse) this.getWebServiceTemplate()
                .marshalSendAndReceive(WSFE_TESTING, solicitud);
        
        response.getFECAESolicitarResult().getErrors().getErr().stream().forEach((err) -> {
            System.out.println(err.getCode() + " - " + err.getMsg());
        });
    }
}
