package lectio

import lectio.service.LiturgicalYearService
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner

@RunWith(SpringRunner)
@SpringBootTest
class LectioApplicationTests {

  @Autowired
  LiturgicalYearService liturgicalYearService

	@Test
	void contextLoads() {
	}





}
