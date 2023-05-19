package com.otr.plugins.qualityGate;

import com.otr.plugins.qualityGate.config.post.CustomAutowireCandidateResolver;
import com.otr.plugins.qualityGate.config.post.Type;
import com.otr.plugins.qualityGate.utils.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;


@SpringBootApplication
@Slf4j
public class QualityGateApplication {

    /**
     * Mods to launch the application, options available:<br/>
     * - not set: displays information about the program<br/>
     * - CHANGE_LOG: will generate a change log and send it to the mail<br/>
     * - LIST_OF: will generate a list of issues from jira and compare them with the commits given between
     * startTag/endTag<br/>
     * - BUILD: initiates project build and performs notification<br/>
     * <p>
     * It is possible to use a combination of mods, they will be launched in sequence, if the next mod gives an error,
     * the chain will end
     */
    public static void main(String[] args) {
        final List<Type> modes = Splitter.splitModeType(args);
        SpringApplication application = new SpringApplication(QualityGateApplication.class);
        application.addInitializers(context -> context.addBeanFactoryPostProcessor(beanFactory -> {
            final DefaultListableBeanFactory factory = (DefaultListableBeanFactory) beanFactory;
            factory.setAutowireCandidateResolver(new CustomAutowireCandidateResolver(modes, factory));
        }));

        application.run(args);
    }
}
