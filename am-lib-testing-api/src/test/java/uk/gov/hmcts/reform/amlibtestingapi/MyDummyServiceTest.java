package uk.gov.hmcts.reform.amlibtestingapi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class MyDummyServiceTest {

    @Test
    public void dummyTest() {
        assertThat(new MyDummyService().getHello()).isEqualTo("Hello Dummy Service");
    }
}
