package dubbo.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * 
 * 功能描述：启动Dubbo服务用的MainClass
 *
 * @author  吴俊明 
 *
 * <p>修改历史：(修改人，修改时间，修改原因/内容)</p>
 */
public class DubboProviderTransaction {
	
	private static final Log log = LogFactory.getLog(DubboProviderTransaction.class);

	public static void main(String[] args) {
		try {
			ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/spring-context.xml");
			context.start();
		} catch (Exception e) {
			log.error("== DubboProvider context start error:",e);
		}
		synchronized (DubboProviderTransaction.class) {
			while (true) {
				try {
					DubboProviderTransaction.class.wait();
				} catch (InterruptedException e) {
					log.error("== synchronized error:",e);
				}
			}
		}
	}
    
}