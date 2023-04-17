package com.otr.plugins.qualityGate;

import com.otr.plugins.qualityGate.service.TaskHandler;
import com.otr.plugins.qualityGate.service.gitlab.CompareApi;
import com.otr.plugins.qualityGate.service.gitlab.ProjectApi;
import com.otr.plugins.qualityGate.service.gitlab.TagApi;
import com.otr.plugins.qualityGate.service.jira.JiraTaskService;
import com.otr.plugins.qualityGate.service.mail.SendMailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.config.location = src/test/resources/test.yml"})
@SpringBootTest
class QualityGateApplicationTests {
	@Autowired
	CompareApi compareApi;
	@Autowired
	ProjectApi projectApi;
	@Autowired
	TagApi tagApi;
	@Autowired
	JiraTaskService jiraTaskService;
	@Autowired
	TaskHandler taskHandler;

	@Autowired
	SendMailService sendMailService;

	@Test
	void contextLoads() {
		assertThat(compareApi).isNotNull();
		assertThat(projectApi).isNotNull();
		assertThat(tagApi).isNotNull();
		assertThat(jiraTaskService).isNotNull();
		assertThat(taskHandler).isNotNull();
		assertThat(sendMailService).isNotNull();
	}
}
