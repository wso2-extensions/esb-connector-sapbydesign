/*
*  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*  WSO2 Inc. licenses this file to you under the Apache License,
*  Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License.
*  You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package org.wso2.carbon.connector.integration.test.sapbydesign;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.commons.codec.binary.Base64;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.connector.integration.test.base.ConnectorIntegrationTestBase;
import org.wso2.connector.integration.test.base.RestResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Sample integration test
 */
public class SAPByDesignIntegrationTest extends ConnectorIntegrationTestBase {

    private Map<String, String> esbRequestHeadersMap = new HashMap<String, String>();
    private Map<String, String> apiRequestHeadersMap = new HashMap<String, String>();

    private final String SOAP_HEADER_XPATH_EXP = "/soap:Envelope/soap:Header/*";
    private final String SOAP_BODY_XPATH_EXP = "/soap:Envelope/soap:Body/*";
    Map<String, String> nameSpaceMap = new HashMap<String, String>();
    private String apiEndPoint;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        init("sapbydesign-connector-1.0.0");
        apiEndPoint = "https://" + connectorProperties.getProperty("sapHost") + "/sap/bc/srt/scs/sap";
        nameSpaceMap.put("env", "http://www.w3.org/2003/05/soap-envelope");
        nameSpaceMap.put("n0", "http://sap.com/xi/SAPGlobal20/Global");
        esbRequestHeadersMap.put("Accept-Charset", "UTF-8");
        esbRequestHeadersMap.put("Content-Type", "application/xml");
    }

    /**
     * Positive test case for checkCustomerAccountsMaintenance method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "SAPByDesign {checkCustomerAccountsMaintenance} integration test with mandatory parameters.")
    public void testCheckCustomerAccountsMaintenanceWithMandatoryParameters() throws Exception {
        connectorProperties.setProperty("familyName", connectorProperties.getProperty("familyName")
                + System.currentTimeMillis());
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "checkCustomerAccountsMaintenance.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String uuid = (String) xPathEvaluate(esbResponseElement, "string(//UUID/text())", nameSpaceMap);
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//InternalID/text())", nameSpaceMap);

        Assert.assertNotEquals(uuid, "");
        Assert.assertNotEquals(internalId, "");
    }

    /**
     * Positive test case for checkSalesOrdersMaintenance method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "SAPByDesign {checkSalesOrdersMaintenance} integration test with mandatory parameters.")
    public void testCheckSalesOrdersMaintenanceWithMandatoryParameters() throws Exception {
        connectorProperties.setProperty("salesOrderName", connectorProperties.getProperty("salesOrderName")
                + System.currentTimeMillis());
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "checkSalesOrdersMaintenance.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String uuid = (String) xPathEvaluate(esbResponseElement, "string(//UUID/text())", nameSpaceMap);
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//ID/text())", nameSpaceMap);

        Assert.assertNotEquals(uuid, "");
        Assert.assertNotEquals(internalId, "");
    }

    /**
     * Positive test case for determineAvailabilityOfProducts method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "SAPByDesign {determineAvailabilityOfProducts} integration test with mandatory parameters.")
    public void testDetermineAvailabilityOfProductsWithMandatoryParameters() throws Exception {
        String currentEpiEndPoint = apiEndPoint + "/productavailabilitydeterminati?sap-vhost=" + connectorProperties.getProperty("sapHost");
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("username") + ":"
                + connectorProperties.getProperty("password")).getBytes());
        apiRequestHeadersMap.put("Content-Type", "application/soap+xml;charset=UTF-8");
        apiRequestHeadersMap.put("Action", "http://sap.com/xi/A1S/Global/ProductAvailabilityDeterminationIn/DetermineRequest");
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "determineAvailabilityOfProducts.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//ProductInternalID/text())", nameSpaceMap);
        String supplyPlanningAreaID = (String) xPathEvaluate(esbResponseElement, "string(//SupplyPlanningAreaID/text())", nameSpaceMap);
        String productTypeName = (String) xPathEvaluate(esbResponseElement, "string(//ProductTypeName/text())", nameSpaceMap);
        String requirementQuantity = (String) xPathEvaluate(esbResponseElement, "string(//RequirementQuantity/text())", nameSpaceMap);

        final RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(currentEpiEndPoint, "POST", apiRequestHeadersMap, "apiDetermineAvailabilityOfProducts.xml");
        String apiInternalId = getValueByExpression("//ProductInternalID/text()", apiResponse.getBody());
        String apiSupplyPlanningAreaID = getValueByExpression("//SupplyPlanningAreaID/text()", apiResponse.getBody());
        String apiProductTypeName = getValueByExpression("//ProductTypeName/text()", apiResponse.getBody());
        String apiRequirementQuantity = getValueByExpression("//RequirementQuantity/text()", apiResponse.getBody());

        Assert.assertEquals(internalId, apiInternalId);
        Assert.assertEquals(supplyPlanningAreaID, apiSupplyPlanningAreaID);
        Assert.assertEquals(productTypeName, apiProductTypeName);
        Assert.assertEquals(requirementQuantity, apiRequirementQuantity);
    }

    /**
     * Positive test case for maintainCustomerAccounts method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "SAPByDesign {maintainCustomerAccounts} integration test with mandatory parameters.")
    public void testMaintainCustomerAccountsWithMandatoryParameters() throws Exception {
        String currentEpiEndPoint = apiEndPoint + "/querycustomerin1?sap-vhost=" + connectorProperties.getProperty("sapHost");
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("username") + ":"
                + connectorProperties.getProperty("password")).getBytes());
        apiRequestHeadersMap.put("Content-Type", "application/soap+xml;charset=UTF-8");
        apiRequestHeadersMap.put("Action", "http://sap.com/xi/A1S/Global/ManageCustomerIn/MaintainBundle_V1Request");
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));

        connectorProperties.setProperty("customerEmail", System.currentTimeMillis()
                + connectorProperties.getProperty("customerEmail"));
        connectorProperties.setProperty("familyName", connectorProperties.getProperty("familyName")
                + System.currentTimeMillis());
        connectorProperties.setProperty("organisationFirstLineName", connectorProperties.getProperty("organisationFirstLineName")
                + System.currentTimeMillis());
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "maintainCustomerAccounts.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//InternalID/text())", nameSpaceMap);
        String uuid = (String) xPathEvaluate(esbResponseElement, "string(//UUID/text())", nameSpaceMap);
        connectorProperties.setProperty("customerInternalId", internalId);
        connectorProperties.setProperty("customerUUID", uuid);
        Assert.assertNotEquals(internalId, "");
        Assert.assertNotEquals(uuid, "");

        final RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(currentEpiEndPoint, "POST", apiRequestHeadersMap, "apiQueryCustomerByElements.xml");
        String apiOrganisationFirstLineName = getValueByExpression("//Organisation/FirstLineName/text()", apiResponse.getBody());
        String apiFamilyName = getValueByExpression("//FamilyName/text()", apiResponse.getBody());

        Assert.assertEquals(connectorProperties.getProperty("familyName"), apiFamilyName);
        Assert.assertEquals(connectorProperties.getProperty("organisationFirstLineName"), apiOrganisationFirstLineName);
    }

    /**
     * Positive test case for maintainSalesOrders method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "SAPByDesign {maintainSalesOrders} integration test with mandatory parameters.")
    public void testMaintainSalesOrdersWithMandatoryParameters() throws Exception {
        String currentEpiEndPoint = apiEndPoint + "/querysalesorderin3?sap-vhost=" + connectorProperties.getProperty("sapHost");
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("username") + ":"
                + connectorProperties.getProperty("password")).getBytes());
        apiRequestHeadersMap.put("Content-Type", "application/soap+xml;charset=UTF-8");
        apiRequestHeadersMap.put("Action", "http://sap.com/xi/A1S/Global/ManageSalesOrderIn/MaintainBundleRequest");
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));
        connectorProperties.setProperty("salesOrderName", connectorProperties.getProperty("salesOrderName")
                + System.currentTimeMillis());
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "maintainSalesOrders.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//ID/text())", nameSpaceMap);
        connectorProperties.setProperty("salesOrderInternalId", internalId);
        Assert.assertNotEquals(internalId, "");
        final RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(currentEpiEndPoint, "POST", apiRequestHeadersMap, "apiQuerySalesOrders.xml");
        String apiSalesOrderName = getValueByExpression("//SalesOrder/Name/text()", apiResponse.getBody());

        Assert.assertEquals(connectorProperties.getProperty("salesOrderName"), apiSalesOrderName);
    }

    /**
     * Positive test case for queryAccountOpenAmounts method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "SAPByDesign {queryAccountOpenAmounts} integration test with mandatory parameters.")
    public void testQueryAccountOpenAmountsWithMandatoryParameters() throws Exception {
        String currentEpiEndPoint = apiEndPoint + "/queryaccountopenamountsin?sap-vhost=" + connectorProperties.getProperty("sapHost");
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("username") + ":"
                + connectorProperties.getProperty("password")).getBytes());
        apiRequestHeadersMap.put("Content-Type", "application/soap+xml;charset=UTF-8");
        apiRequestHeadersMap.put("Action", "http://sap.com/xi/A1S/Global/QueryAccountOpenAmountsIn/FindAccountOpenAmountsRequest");
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "queryAccountOpenAmounts.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String count = (String) xPathEvaluate(esbResponseElement, "string(count(//AccountOpenAmounts))", nameSpaceMap);
        String companyId = (String) xPathEvaluate(esbResponseElement, "string(//AccountOpenAmounts[1]/CompanyID/text())", nameSpaceMap);
        String receivablesBalanceAmount = (String) xPathEvaluate(esbResponseElement, "string(//AccountOpenAmounts[1]/ReceivablesBalanceAmount/text())", nameSpaceMap);

        final RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(currentEpiEndPoint, "POST", apiRequestHeadersMap, "apiQueryAccountOpenAmounts.xml");
        String apiCount = getValueByExpression("count(//AccountOpenAmounts)", apiResponse.getBody());
        String apiCompanyId = getValueByExpression("//AccountOpenAmounts[1]/CompanyID/text()", apiResponse.getBody());
        String apiReceivablesBalanceAmount = getValueByExpression("//AccountOpenAmounts[1]/ReceivablesBalanceAmount/text()", apiResponse.getBody());

        Assert.assertEquals(count, apiCount);
        Assert.assertEquals(companyId, apiCompanyId);
        Assert.assertEquals(receivablesBalanceAmount, apiReceivablesBalanceAmount);
    }

    /**
     * Positive test case for queryCustomerByCommunicationData method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testMaintainCustomerAccountsWithMandatoryParameters"},
            description = "SAPByDesign {queryCustomerByCommunicationData} integration test with mandatory parameters.")
    public void testQueryCustomerByCommunicationDataWithMandatoryParameters() throws Exception {
        String currentEpiEndPoint = apiEndPoint + "/querycustomerin1?sap-vhost=" + connectorProperties.getProperty("sapHost");
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("username") + ":"
                + connectorProperties.getProperty("password")).getBytes());
        apiRequestHeadersMap.put("Content-Type", "application/soap+xml;charset=UTF-8");
        apiRequestHeadersMap.put("Action", "http://sap.com/xi/A1S/Global/QueryCustomerIn/FindByCommunicationDataRequest");
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "queryCustomerByCommunicationData.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//InternalID/text())", nameSpaceMap);
        String uuid = (String) xPathEvaluate(esbResponseElement, "string(//UUID/text())", nameSpaceMap);
        Assert.assertEquals(internalId, connectorProperties.getProperty("customerInternalId"));

        final RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(currentEpiEndPoint, "POST", apiRequestHeadersMap, "apiQueryCustomerByCommunicationData.xml");
        String apiInternalId = getValueByExpression("//InternalID/text()", apiResponse.getBody());
        String apiUUID = getValueByExpression("//UUID/text()", apiResponse.getBody());

        Assert.assertEquals(internalId, apiInternalId);
        Assert.assertEquals(uuid, apiUUID);
    }

    /**
     * Positive test case for queryCustomerByElements method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testMaintainCustomerAccountsWithMandatoryParameters"},
            description = "SAPByDesign {queryCustomerByElements} integration test with mandatory parameters.")
    public void testQueryCustomerByElementsWithMandatoryParameters() throws Exception {
        String currentEpiEndPoint = apiEndPoint + "/querycustomerin1?sap-vhost=" + connectorProperties.getProperty("sapHost");
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("username") + ":"
                + connectorProperties.getProperty("password")).getBytes());
        apiRequestHeadersMap.put("Content-Type", "application/soap+xml;charset=UTF-8");
        apiRequestHeadersMap.put("Action", "http://sap.com/xi/A1S/Global/QueryCustomerIn/FindByElementsRequest");
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "queryCustomerByElements.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//InternalID/text())", nameSpaceMap);
        String uuid = (String) xPathEvaluate(esbResponseElement, "string(//UUID/text())", nameSpaceMap);
        Assert.assertEquals(internalId, connectorProperties.getProperty("customerInternalId"));

        final RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(currentEpiEndPoint, "POST", apiRequestHeadersMap, "apiQueryCustomerByElements.xml");
        String apiInternalId = getValueByExpression("//InternalID/text()", apiResponse.getBody());
        String apiUUID = getValueByExpression("//UUID/text()", apiResponse.getBody());

        Assert.assertEquals(internalId, apiInternalId);
        Assert.assertEquals(uuid, apiUUID);
    }

    /**
     * Positive test case for queryCustomerByIdentification method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testMaintainCustomerAccountsWithMandatoryParameters"},
            description = "SAPByDesign {queryCustomerByIdentification} integration test with mandatory parameters.")
    public void testQueryCustomerByIdentificationWithMandatoryParameters() throws Exception {
        String currentEpiEndPoint = apiEndPoint + "/querycustomerin1?sap-vhost=" + connectorProperties.getProperty("sapHost");
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("username") + ":"
                + connectorProperties.getProperty("password")).getBytes());
        apiRequestHeadersMap.put("Content-Type", "application/soap+xml;charset=UTF-8");
        apiRequestHeadersMap.put("Action", "http://sap.com/xi/A1S/Global/QueryCustomerIn/FindByIdentificationRequest");
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "queryCustomerByIdentification.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//InternalID/text())", nameSpaceMap);
        String uuid = (String) xPathEvaluate(esbResponseElement, "string(//UUID/text())", nameSpaceMap);
        Assert.assertEquals(internalId, connectorProperties.getProperty("customerInternalId"));

        final RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(currentEpiEndPoint, "POST", apiRequestHeadersMap, "apiQueryCustomerByIdentification.xml");
        String apiInternalId = getValueByExpression("//InternalID/text()", apiResponse.getBody());
        String apiUUID = getValueByExpression("//UUID/text()", apiResponse.getBody());

        Assert.assertEquals(internalId, apiInternalId);
        Assert.assertEquals(uuid, apiUUID);
    }

    /**
     * Positive test case for queryMaterials method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "SAPByDesign {queryMaterials} integration test with mandatory parameters.")
    public void testQueryMaterialsWithMandatoryParameters() throws Exception {
        String currentEpiEndPoint = apiEndPoint + "/querymaterialin?sap-vhost=" + connectorProperties.getProperty("sapHost");
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("username") + ":"
                + connectorProperties.getProperty("password")).getBytes());
        apiRequestHeadersMap.put("Content-Type", "application/soap+xml;charset=UTF-8");
        apiRequestHeadersMap.put("Action", "http://sap.com/xi/A1S/Global/QueryMaterialIn/FindByElementsRequest");
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "queryMaterials.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String count = (String) xPathEvaluate(esbResponseElement, "string(count(//Material))", nameSpaceMap);
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//Material[1]/InternalID/text())", nameSpaceMap);
        String uuid = (String) xPathEvaluate(esbResponseElement, "string(//Material[1]/UUID/text())", nameSpaceMap);
        connectorProperties.setProperty("ODUUID", uuid);

        final RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(currentEpiEndPoint, "POST", apiRequestHeadersMap, "apiQueryMaterials.xml");
        String apiCount = getValueByExpression("count(//Material)", apiResponse.getBody());
        String apiInternalId = getValueByExpression("//Material[1]/InternalID/text()", apiResponse.getBody());
        String apiUUID = getValueByExpression("//Material[1]/UUID/text()", apiResponse.getBody());

        Assert.assertEquals(count, apiCount);
        Assert.assertEquals(internalId, apiInternalId);
        Assert.assertEquals(uuid, apiUUID);
    }

    /**
     * Positive test case for queryOutboundDeliverables method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"},
            description = "SAPByDesign {queryOutboundDeliverables} integration test with mandatory parameters.")
    public void testQueryOutboundDeliverablesWithMandatoryParameters() throws Exception {
        String currentEpiEndPoint = apiEndPoint + "/queryoutbounddeliveryin?sap-vhost=" + connectorProperties.getProperty("sapHost");
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("username") + ":"
                + connectorProperties.getProperty("password")).getBytes());
        apiRequestHeadersMap.put("Content-Type", "application/soap+xml;charset=UTF-8");
        apiRequestHeadersMap.put("Action", "http://sap.com/xi/A1S/Global/QueryOutboundDeliveryIn/FindByElementsRequest");
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "queryOutboundDeliverables.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String count = (String) xPathEvaluate(esbResponseElement, "string(count(//OutboundDelivery))", nameSpaceMap);
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//OutboundDelivery[1]/OutboundDeliveryID/text())", nameSpaceMap);
        String uuid = (String) xPathEvaluate(esbResponseElement, "string(//OutboundDelivery[1]/OutboundDeliveryUUID/text())", nameSpaceMap);
        connectorProperties.setProperty("ODUUID", uuid);

        final RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(currentEpiEndPoint, "POST", apiRequestHeadersMap, "apiQueryOutboundDeliverables.xml");
        String apiCount = getValueByExpression("count(//OutboundDelivery)", apiResponse.getBody());
        String apiInternalId = getValueByExpression("//OutboundDelivery[1]/OutboundDeliveryID/text()", apiResponse.getBody());
        String apiUUID = getValueByExpression("//OutboundDelivery[1]/OutboundDeliveryUUID/text()", apiResponse.getBody());

        Assert.assertEquals(count, apiCount);
        Assert.assertEquals(internalId, apiInternalId);
        Assert.assertEquals(uuid, apiUUID);
    }

    /**
     * Positive test case for querySalesOrders method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testMaintainSalesOrdersWithMandatoryParameters"},
            description = "SAPByDesign {querySalesOrders} integration test with mandatory parameters.")
    public void testQuerySalesOrdersWithMandatoryParameters() throws Exception {
        String currentEpiEndPoint = apiEndPoint + "/querysalesorderin3?sap-vhost=" + connectorProperties.getProperty("sapHost");
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("username") + ":"
                + connectorProperties.getProperty("password")).getBytes());
        apiRequestHeadersMap.put("Content-Type", "application/soap+xml;charset=UTF-8");
        apiRequestHeadersMap.put("Action", "http://sap.com/xi/A1S/Global/QuerySalesOrderIn/FindByElementsRequest");
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "querySalesOrders.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//ID/text())", nameSpaceMap);
        String uuid = (String) xPathEvaluate(esbResponseElement, "string(//UUID/text())", nameSpaceMap);
        Assert.assertEquals(internalId, connectorProperties.getProperty("salesOrderInternalId"));

        final RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(currentEpiEndPoint, "POST", apiRequestHeadersMap, "apiQuerySalesOrders.xml");
        String apiInternalId = getValueByExpression("//ID/text()", apiResponse.getBody());
        String apiUUID = getValueByExpression("//UUID/text()", apiResponse.getBody());

        Assert.assertEquals(internalId, apiInternalId);
        Assert.assertEquals(uuid, apiUUID);
    }

    /**
     * Positive test case for readOutboundDeliverables method with mandatory parameters.
     */
    @Test(enabled = true, groups = {"wso2.esb"}, dependsOnMethods = {"testQueryOutboundDeliverablesWithMandatoryParameters"},
            description = "SAPByDesign {readOutboundDeliverables} integration test with mandatory parameters.")
    public void testReadOutboundDeliverablesWithMandatoryParameters() throws Exception {
        String currentEpiEndPoint = apiEndPoint + "/manageodin?sap-vhost=" + connectorProperties.getProperty("sapHost");
        byte[] encodedString = Base64.encodeBase64((connectorProperties.getProperty("username") + ":"
                + connectorProperties.getProperty("password")).getBytes());
        apiRequestHeadersMap.put("Content-Type", "application/soap+xml;charset=UTF-8");
        apiRequestHeadersMap.put("Action", "http://sap.com/xi/A1S/Global/ManageODIn/ReadRequest");
        apiRequestHeadersMap.put("Authorization", "Basic " + new String(encodedString));
        SOAPEnvelope esbSoapResponse =
                sendSOAPRequest(proxyUrl, "readOutboundDeliverables.xml", esbRequestHeadersMap, "mediate",
                        SOAP_HEADER_XPATH_EXP, SOAP_BODY_XPATH_EXP);
        OMElement esbResponseElement = AXIOMUtil.stringToOM(esbSoapResponse.getBody().toString());
        String internalId = (String) xPathEvaluate(esbResponseElement, "string(//OutboundDelivery/ID/text())", nameSpaceMap);
        String uuid = (String) xPathEvaluate(esbResponseElement, "string(//OutboundDelivery/UUID/text())", nameSpaceMap);
        Assert.assertEquals(uuid, connectorProperties.getProperty("ODUUID"));

        final RestResponse<OMElement> apiResponse =
                sendXmlRestRequest(currentEpiEndPoint, "POST", apiRequestHeadersMap, "apiReadOutboundDeliverables.xml");
        String apiInternalId = getValueByExpression("//OutboundDelivery/ID/text()", apiResponse.getBody());
        String apiUUID = getValueByExpression("//OutboundDelivery/UUID/text()", apiResponse.getBody());

        Assert.assertEquals(internalId, apiInternalId);
        Assert.assertEquals(uuid, apiUUID);
    }
}