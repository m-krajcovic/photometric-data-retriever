package cz.muni.physics.sesame;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Michal Krajčovič
 * @version 1.0
 * @since 24/03/16
 */
public class Main {
    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        SesameClient client = context.getBean(SesameClient.class);

//        client.getAliases("RW Com").forEach(System.out::println);
    }
}
