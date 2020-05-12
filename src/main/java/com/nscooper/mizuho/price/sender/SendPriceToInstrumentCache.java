package com.nscooper.mizuho.price.sender;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class SendPriceToInstrumentCache {

	public static void main(String[] args) {

		File file = new File("/config/mizuho/application.properties");
		Properties props = new Properties();
		try {
		FileInputStream fis = new FileInputStream(file);
			props.load(fis);
			fis.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		ConnectionFactory factory = new ActiveMQConnectionFactory(props.getProperty("activeMqHostnameAndPort"));

		Connection con = null;
		try {
			con = factory.createConnection();
			Session session = con.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Queue queue = session.createQueue(props.getProperty("inboundPricesQueueName"));

			MessageProducer producer = session.createProducer(queue);
			Message msg = session.createTextMessage(args[0].replaceAll(" ", "%20"));
			producer.send(msg);
			System.out.println("JMS msg sent to "+props.getProperty("inboundPricesQueueName")+" on: "+props.getProperty("activeMqHostnameAndPort"));

		} catch (JMSException e) {
			for (final StackTraceElement ste : e.getStackTrace()) {
				System.out.println(ste.toString());
			}
		} finally {
			try {
				con.close();
			} catch (JMSException e) {
				for (final StackTraceElement ste : e.getStackTrace()) {
					System.out.println(ste.toString());
				}
			}
		}

	}

}
