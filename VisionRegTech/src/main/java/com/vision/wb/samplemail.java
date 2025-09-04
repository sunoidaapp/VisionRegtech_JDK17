//package com.vision.wb;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.vision.dao.EmailScheduleDao;
//import com.vision.vb.EmailScheduleVb;
//
//public class samplemail {
//	@Autowired
//	EmailScheduleDao emailScheduleDao;
//public static void main(String[] args) {
//	samplemail samplemail = new samplemail();
//	samplemail.mail();
//}
//public void mail() {
//List<EmailScheduleVb> collTemp= null;
//EmailScheduleVb dObj = new EmailScheduleVb();
//dObj.setReportingDate("26-06-2024");
//collTemp= emailScheduleDao.getDetails(dObj);
//String []toEmails= collTemp.stream().map(EmailScheduleVb::getEmailTo).distinct().toArray(String[]:: new);
//System.out.println(toEmails.toString());
//}
//}
