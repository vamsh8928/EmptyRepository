/**
 * 
 */
package gov.dcra.filenet.execute;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.filenet.api.constants.PropertyNames;
import com.filenet.api.core.Document;
import com.filenet.api.core.Domain;
import com.filenet.api.core.Factory;
import com.filenet.api.core.ObjectStore;
import com.filenet.api.property.FilterElement;
import com.filenet.api.property.PropertyFilter;
import com.filenet.api.util.Id;

import gov.dcra.filenet.connection.DCRAFileNetConnection;
import gov.dcra.filenet.constants.Constants;
import gov.dcra.filenet.util.DCRAFileNetUtil;



/**
 * @author Administrator
 *
 */
public class DocumentExportExecute {

	/**
	 * @param args
	 */

	private final static Logger log = Logger.getLogger(DocumentExportExecute.class);
	private static String inputFileLoc = null;

	private final String appPropsFile = Constants.APPLICATION_PROPS;
	private Properties appProps = new Properties();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		
		DocumentExportExecute exec = null;
		try {
//
//			StringBuilder sb = new StringBuilder();
//			sb.append("0. Exit").append(Constants.NEWLINE);
//			sb.append("1. Process Document Export").append(Constants.NEWLINE);
//
//			sb.append("Please input the number to proceed");
//
//			String sampleOperation = read(sb.toString());
//			int op = 0;
//			try {
//				op = Integer.parseInt(sampleOperation);
//				if (op == 0) {
//					System.out.println("Thanks, bye....!");
//					System.exit(0);
//				} else if (op == 1) {
//					inputFileLoc = read("Please input the input file path : ");
//
//					while (!isFilenameValid(inputFileLoc)) {
//						inputFileLoc = read("Please input a valid file path : ");
//					}
//				} else {
//					System.out.println("Unsupported " + op);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			exec = new DocumentExportExecute();
			exec.processExport();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	private void processExport() {

		DCRAFileNetConnection conn = null;
		DCRAFileNetUtil exportUtil = null;
		Domain domain = null;
		ObjectStore os = null;
		StringTokenizer docIds = null;
		Map<String, String> docPropsMap = null;
		BufferedWriter bufferedWriter = null;

		try {

			log.debug("Start Processing......");
			appProps.load(DocumentExportExecute.class.getResourceAsStream(appPropsFile));
			
			String filename = appProps.getProperty(Constants.MSG_EXPORT_DIR).concat(Constants.FILESEPARATOR).concat(appProps.getProperty(Constants.MSG_EXPORT_FILENAME));
			

			File file = new File(filename);
			if (!file.exists()) {
				file.createNewFile();
			}

			
			bufferedWriter = new BufferedWriter(new FileWriter(filename, true));

			conn = new DCRAFileNetConnection();
			exportUtil = new DCRAFileNetUtil();
			domain = conn.getDomain();
			os = exportUtil.getObjectStore(domain,appProps.getProperty(Constants.MSG_OBJECTSTORE_NAME));
			
			bufferedWriter.write("FileNumber" + "\t"  
			                     + "Address" +  "\t" 
					             + "PermitType" +"\t" 
			                     + "DocumentType" + "\t" 
					             + "Pages" + "\t" 
					             + "DocumentDate" + "\t"
			                     + "DateCreated" + "\t"  
					             + "DocumentTitle" + "\t"
					             + "NAME" + "\t"
			                     + "Creator" + "\t" 
					             + "MimeType" + "\t" 
			                     + "ContentSize" + "\t"  
					             + "Major_Version_Number" + "\t" 
			                     + "Minor_Version_Number" + "\t" 
					             + "Version_Status" + "\t" 
			                     + "ID" + "\t" 
					             + "FilePath" + "\t" + "\n");

		if(os!=null){
			
//			List<String> docIdList=exportUtil.readInputFile(inputFileLoc,appProps.getProperty(Constants.MSG_COLUMN_NAME));
			
			List<String> docIdList=exportUtil.getObjectIds(os);
			
			if(docIdList!=null && docIdList.size()>0){
				docIds = new StringTokenizer(docIdList.stream().collect(Collectors.joining(",")), ",");

				while (docIds.hasMoreElements()) {

					String docId = docIds.nextToken();
					docPropsMap = new LinkedHashMap<String, String>();

					log.debug("Processing ID : "+docId);
					
					PropertyFilter pf = new PropertyFilter();    
					pf.addIncludeProperty(new FilterElement(null, null, null, Constants.PROPERTY_PERMIT_TYPE, null) );
					pf.addIncludeProperty(new FilterElement(null, null, null, Constants.PROPERTY_FILE_NUMBER, null) );
					pf.addIncludeProperty(new FilterElement(null, null, null, Constants.PROPERTY_ADDRESS, null));
					pf.addIncludeProperty(new FilterElement(null, null, null, Constants.PROPERTY_DOCUMENT_TYPE, null));					
					pf.addIncludeProperty(new FilterElement(null, null, null, Constants.PROPERTY_PAGES, null));	
					pf.addIncludeProperty(new FilterElement(null, null, null, Constants.PROPERTY_DOCUMENTTITLE, null));	
					pf.addIncludeProperty(new FilterElement(null, null, null, Constants.PROPERTY_MIMETYPE, null));
					pf.addIncludeProperty(new FilterElement(null, null, null, Constants.PROPERTY_CREATOR, null));
					pf.addIncludeProperty(new FilterElement(null, null, null, Constants.PROPERTY_DOCUMENT_DATE, null));
					pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.NAME, null));
					pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.MAJOR_VERSION_NUMBER, null));	
					pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.DATE_CREATED, null));
					pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.DISPLAY_NAME, null));								
					pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.MINOR_VERSION_NUMBER, null));					
					pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.VERSION_STATUS, null));					
					pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.CONTAINMENT_NAME, null));					
					pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.ID, null));
					pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.CONTENT_ELEMENTS, null));
					pf.addIncludeProperty(new FilterElement(null, null, null, PropertyNames.CONTENT_SIZE, null));
					
					//Document_Date
					
			
					Document doc = Factory.Document.fetchInstance(os, new Id(docId),pf);

					docPropsMap = exportUtil.getDocProperties(os, doc);
					String filePath=exportUtil.getDocContent(doc,appProps.getProperty(Constants.MSG_EXPORT_DIR),docPropsMap.get(Constants.PROPERTY_FILE_NUMBER),docPropsMap.get(Constants.PROPERTY_DOCUMENTTITLE));
					/*docPropsMap.put("1", "a");
				docPropsMap.put("2", "b");
				docPropsMap.put("3", "c");
				docPropsMap.put("4", "d");*/
// docPropsMap.get(PropertyNames.CONTAINMENT_NAME) + "\t" 
					
                      
					bufferedWriter.write( docPropsMap.get(Constants.PROPERTY_FILE_NUMBER) + "\t"
							+ docPropsMap.get(Constants.PROPERTY_ADDRESS) + "\t"
							+ docPropsMap.get(Constants.PROPERTY_PERMIT_TYPE) + "\t"						
							+ docPropsMap.get(Constants.PROPERTY_DOCUMENT_TYPE) + "\t"
							+ docPropsMap.get(Constants.PROPERTY_PAGES) + "\t"
						    + docPropsMap.get(Constants.PROPERTY_DOCUMENT_DATE) + "\t"
						    + docPropsMap.get(PropertyNames.DATE_CREATED) + "\t"
							+ docPropsMap.get(Constants.PROPERTY_DOCUMENTTITLE) + "\t"
						    + docPropsMap.get(PropertyNames.NAME) + "\t"
							+ docPropsMap.get(Constants.PROPERTY_CREATOR) + "\t"
							+ docPropsMap.get(Constants.PROPERTY_MIMETYPE) + "\t"
							+ docPropsMap.get(PropertyNames.CONTENT_SIZE.toString()) + "\t"					   
							+ docPropsMap.get(PropertyNames.MAJOR_VERSION_NUMBER) + "\t"
							+ docPropsMap.get(PropertyNames.MINOR_VERSION_NUMBER) + "\t"
							+ docPropsMap.get(PropertyNames.VERSION_STATUS) + "\t"
							+ docPropsMap.get(PropertyNames.ID) + "\t"
							+ filePath+"\t"
							+ "\n");
				}

			}else{
				
				log.error("Cannot Process,error extracting docIds from file : "+inputFileLoc);
			}
		}else{
			
			log.error("Cannot Process,error fetching objectStore : "+appProps.getProperty(Constants.MSG_OBJECTSTORE_NAME));
		}

			log.debug("Please find the Report at : "+file.getPath());
			log.debug("End Processing......");
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {

				ex.printStackTrace();

			}
			catch (Exception ex) {

				ex.printStackTrace();

			}
		}
	}

	public static boolean isFilenameValid(String fileName) {

		File f = new File(fileName);
		return f.isFile();
	}

	public static String read(String prompt) {
		String value = null;
		byte[] bytes = new byte[100];

		try {
			System.out.println(prompt);
			int readed;
			readed = System.in.read(bytes);
			while (readed <= 0) {
				System.out.println(prompt);
				readed = System.in.read(bytes);
			}
			value = new String(bytes, 0, readed).trim();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return value;
	}

}
