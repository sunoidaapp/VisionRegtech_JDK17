package com.vision.wb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

public class CRSXmlGenerator {

    /**
     * Generates the CRS XML using a ResultSet.
     */
	
	public static void main(String[] args) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");  

			//step2 create  the connection object  
			Connection con = DriverManager.getConnection("jdbc:oracle:thin:@10.16.1.106:1521:VISDB", "VISIONGDI","vision123");
			  

			//step3 create the statement object  
			System.out.println("1");
			Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
			System.out.println("2");
			String tableName = "CRS_DETAILS_CLOUD";
			String query = "SELECT ACCOUNT_NUMBER,ACCOUNT_STATUS,RES_COUNTRY_CODE,COUNTRY_CODE"
					+ ",CITY,ADDRESS,TO_CHAR(DATE_OF_BIRTH ,'RRRR-MM-DD')DATE_OF_BIRTH,ACCOUNT_BALANCE,"
					+ "FD_INT_PAID,ACCT_CURRENCY,DOC_TYPE_IN,DOC_REF_ID,ACCOUNT_TYPE,ORGANIZATION_IN,"
					+ "ORGANIZATION_NAME_TYPE,STREET,ORGANIZATION_NME,ACCOUNT_HOLDER_TYPE,SENDING_COMPANY_IN,"
					+ "TRANSMITTING_COUNTRY,RECEIVING_COUNTRY,MESSAGE_TYPE,WARNING,CONTACT,MESSAGE_REF_ID,MESSAGE_TYPE_INDIC,"
					+ "TO_CHAR(REPORTING_PERIOD ,'RRRR-MM-DD')REPORTING_PERIOD,FIRST_NAME,LAST_NAME,"
					+ "TO_CHAR(SYSDATE, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') AS Timestamp,COUNTRY_OF_TAX_REPORTING,TAX_ID_NUMBER "
					+ "FROM "+tableName;
			
			ResultSet rs = stmt.executeQuery(query);
			if (rs != null) {
			    while (rs.next()) {
			        String crsXml = generateCRSXml(rs);
			        System.out.println(crsXml);
			    }
			}

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    public static String generateCRSXml(ResultSet rs) throws SQLException {
        StringBuilder xmlBuilder = new StringBuilder();

        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int row = rs.getRow();

            if (row == 1) {
                // Start root element
                xmlBuilder.append("<crs:CRS_OECD xmlns:crs=\"urn:oecd:ties:crs:v2\" ")
                          .append("xmlns:cfc=\"urn:oecd:ties:commontypesfatcacrs:v2\" ")
                          .append("xmlns:ftc=\"urn:oecd:ties:fatca:v1\" ")
                          .append("xmlns:iso=\"urn:oecd:ties:isocrstypes:v1\" ")
                          .append("xmlns:stf=\"urn:oecd:ties:crsstf:v5\" ")
                          .append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ")
                          .append("version=\"2.0\" ")
                          .append("xsi:schemaLocation=\"urn:oecd:ties:crs:v1 CrsXML_v2.0.xsd\">");

                // MessageSpec
                xmlBuilder.append("<crs:MessageSpec>");
                tagElement(xmlBuilder, "crs:SendingCompanyIN", rs.getString("SENDING_COMPANY_IN"));
                tagElement(xmlBuilder, "crs:TransmittingCountry", rs.getString("TRANSMITTING_COUNTRY"));
                tagElement(xmlBuilder, "crs:ReceivingCountry", rs.getString("RECEIVING_COUNTRY"));
                tagElement(xmlBuilder, "crs:MessageType", rs.getString("MESSAGE_TYPE"));
                tagElement(xmlBuilder, "crs:Warning", rs.getString("WARNING"));
                tagElement(xmlBuilder, "crs:Contact", rs.getString("CONTACT"));
                tagElement(xmlBuilder, "crs:MessageRefId", rs.getString("MESSAGE_REF_ID"));
                tagElement(xmlBuilder, "crs:MessageTypeIndic", rs.getString("MESSAGE_TYPE_INDIC"));
                tagElement(xmlBuilder, "crs:ReportingPeriod", rs.getString("REPORTING_PERIOD"));
                tagElement(xmlBuilder, "crs:Timestamp", rs.getString("Timestamp"));
                xmlBuilder.append("</crs:MessageSpec>");

                // CRS Body
                xmlBuilder.append("<crs:CrsBody>");
                xmlBuilder.append("<crs:ReportingFI>");
                tagElement(xmlBuilder, "crs:ResCountryCode", "KE");
                tagElementWithAttribute(xmlBuilder, "crs:IN", "P051091117F", "issuedBy", "KE");
                tagElement(xmlBuilder, "crs:Name", "PRIME BANK");

                // Address
                xmlBuilder.append("<crs:Address legalAddressType=\"OECD303\">");
                tagElement(xmlBuilder, "cfc:CountryCode", "KE");
                xmlBuilder.append("<cfc:AddressFix>");
                tagElement(xmlBuilder, "cfc:Street", "RIVERSIDE DRIVE");
                tagElement(xmlBuilder, "cfc:BuildingIdentifier", "PRIMEBANK BUILDING");
                tagElement(xmlBuilder, "cfc:City", "NAIROBI");
                xmlBuilder.append("</cfc:AddressFix>");
                xmlBuilder.append("</crs:Address>");

                // DocSpec
                xmlBuilder.append("<crs:DocSpec>");
                tagElement(xmlBuilder, "stf:DocTypeIndic", "OECD1");
                tagElement(xmlBuilder, "stf:DocRefId", "KE2023P051091117F");
                xmlBuilder.append("</crs:DocSpec>");
                xmlBuilder.append("</crs:ReportingFI>");

                // AccountReport
                xmlBuilder.append("<crs:AccountReport>");
                xmlBuilder.append("<crs:DocSpec>");
                tagElement(xmlBuilder, "stf:DocTypeIndic", rs.getString("DOC_TYPE_IN"));
                tagElement(xmlBuilder, "stf:DocRefId", rs.getString("DOC_REF_ID"));
                xmlBuilder.append("</crs:DocSpec>");
                tagElementWithAttributes(xmlBuilder, "crs:AccountNumber", rs.getString("ACCOUNT_NUMBER"),
                        "AccNumberType", rs.getString("ACCOUNT_TYPE"),
                        "UndocumentedAccount", String.valueOf(rs.getString("TAX_ID_NUMBER") == null),
                        "ClosedAccount", String.valueOf("INACTIVE".equalsIgnoreCase(rs.getString("ACCOUNT_STATUS"))),
                        "DormantAccount", String.valueOf("DORMANT".equalsIgnoreCase(rs.getString("ACCOUNT_STATUS"))));

                // AccountHolder
                xmlBuilder.append("<crs:AccountHolder>");
                xmlBuilder.append("<crs:Individual>");
                tagElement(xmlBuilder, "crs:ResCountryCode", rs.getString("RES_COUNTRY_CODE"));
                tagElement(xmlBuilder, "crs:TIN", rs.getString("TAX_ID_NUMBER") != null ? rs.getString("TAX_ID_NUMBER") : "");
                xmlBuilder.append("<crs:Name>");
                tagElement(xmlBuilder, "crs:FirstName", rs.getString("FIRST_NAME"));
                tagElement(xmlBuilder, "crs:LastName", rs.getString("LAST_NAME"));
                xmlBuilder.append("</crs:Name>");
                xmlBuilder.append("</crs:Individual>");
                xmlBuilder.append("</crs:AccountHolder>");

                // Account Balance
                xmlBuilder.append("<crs:AccountBalance currCode=\"").append(rs.getString("ACCT_CURRENCY")).append("\">");
                xmlBuilder.append(rs.getString("ACCOUNT_BALANCE")).append("</crs:AccountBalance>");

                // Payment
                xmlBuilder.append("<crs:Payment>");
                tagElement(xmlBuilder, "crs:Type", "CRS502");
                xmlBuilder.append("<crs:PaymentAmnt currCode=\"").append(rs.getString("ACCT_CURRENCY")).append("\">");
                xmlBuilder.append(rs.getString("FD_INT_PAID") != null ? rs.getString("FD_INT_PAID") : "0.00");
                xmlBuilder.append("</crs:PaymentAmnt>");
                xmlBuilder.append("</crs:Payment>");

                xmlBuilder.append("</crs:AccountReport>");
                xmlBuilder.append("</crs:CrsBody>");
                xmlBuilder.append("</crs:CRS_OECD>");
            }
        } catch (Exception e) {
            throw new SQLException("Error generating CRS XML", e);
        }

        return xmlBuilder.toString();
    }

    /**
     * Utility method to write an XML element with content.
     */
    private static void tagElement(StringBuilder builder, String tag, String content) {
        if (content != null) {
            builder.append("<").append(tag).append(">")
                   .append(content)
                   .append("</").append(tag).append(">");
        }
    }

    /**
     * Utility method to write an XML element with a single attribute.
     */
    private static void tagElementWithAttribute(StringBuilder builder, String tag, String content, String attr, String attrValue) {
        if (content != null) {
            builder.append("<").append(tag).append(" ").append(attr).append("=\"").append(attrValue).append("\">")
                   .append(content)
                   .append("</").append(tag).append(">");
        }
    }

    /**
     * Utility method to write an XML element with multiple attributes.
     */
    private static void tagElementWithAttributes(StringBuilder builder, String tag, String content, String... attributes) {
        if (content != null) {
            builder.append("<").append(tag);
            for (int i = 0; i < attributes.length; i += 2) {
                builder.append(" ").append(attributes[i]).append("=\"").append(attributes[i + 1]).append("\"");
            }
            builder.append(">").append(content).append("</").append(tag).append(">");
        }
    }
}

