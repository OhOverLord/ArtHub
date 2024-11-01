package org.arthub.backend.ContollerTest;

import org.arthub.backend.DemoApplicationTests;
import org.arthub.backend.Elasticsearch.PostElasticsearchRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = DemoApplicationTests.class)
public class UserControllerTest {
    @MockBean
    private PostElasticsearchRepository postElasticsearchRepository;
}
